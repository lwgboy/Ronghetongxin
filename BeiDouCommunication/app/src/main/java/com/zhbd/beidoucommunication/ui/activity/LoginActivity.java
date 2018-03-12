package com.zhbd.beidoucommunication.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.User;
import com.zhbd.beidoucommunication.event.LoginFeekBackEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.widget.WaitDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {
    // 用户名
    @Bind(R.id.et_user_name)
    EditText mEtUserName;
    // 密码
    @Bind(R.id.et_user_pwd)
    EditText mEtUserPwd;
    // 登录
    @Bind(R.id.btn_login)
    Button mBtnLogin;
    // 忘记密码
    @Bind(R.id.tv_forget_pwd)
    TextView mTvForgetPwd;
    // 注册
    @Bind(R.id.btn_register)
    Button mBtnRegister;

    // 用户名
    private String userName;
    // 密码
    private String userPwd;

    private int userId;

    /**
     * 等待提示框
     */
    private WaitDialog waitDialog;
    private int recLen = Constants.WAITDIALOG_WAIT_MAX_SECONDS;
    Timer timer = new Timer();
    private boolean isPause = true;

    // 注册时间订阅者
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revLogFeekBack(LoginFeekBackEvent event) {
        //Log.e("error", "收到广播");
        if (waitDialog.isShowing()) {
            //Log.e("error", "进了if");
            byte def = -1;
            int loginState = event.getStatus();
            int userId = event.getUserId();
            //boolean isPhone = intent.getBooleanExtra(SharedPrefUtil.ISPHONELOGIN, false);
            switch (loginState) {
                // 登录成功
                case DataProcessingUtil.LOGIN_FEEDBACK_SUCCESS:
                    // 启动消息通知的服务
                    // 判断服务是否在运行
//                    if (!CommUtil.isServiceWork("com.zhbd.beidoucommunication.service.NotificationService")) {
//                        Intent notificationService = new Intent();
//                        notificationService.setAction("com.zhbd.beidoucommunication.service.NotificationService");
//                        notificationService.setPackage(getPackageName());
//                        startService(notificationService);
//                    }
                    MyApplication.getInstance().startnotifySecrvice();
                    SharedPrefUtil.putInt(LoginActivity.this, Constants.USER_ID, userId);
                    SharedPrefUtil.putString(LoginActivity.this, Constants.USER_PASSWORD, userPwd);
                    // 等待对话框消失
                    waitDialog.dismiss();
                    ToastUtils.showToast(LoginActivity.this, R.string.login_success);
                    // 跳转到主页
                    Intent goMain = new Intent(LoginActivity.this, MainActivity.class);
                    User user = null;
                    // 判断登录方式
                    //if (isPhone) {
                    // TODO 手机号登录怎么获取用户id
                    //String phone = intent.getStringExtra(SharedPrefUtil.LOGINPHONENUMBER);

                    //} else {
                    // 从数据库中找该id对应的信息
                    DatabaseDao dao = DatabaseDao.getInstance(LoginActivity.this, 0);
                    user = dao.queryUserInfoByUserId(userId);
                    //}

                    if (user != null) {
                        // 登录者信息存到本地
                        SharedPrefUtil.putString(LoginActivity.this, Constants.PHONE_NUMBER, user.getPhoneNumber());
                        SharedPrefUtil.putString(LoginActivity.this, Constants.ID_CARD_NUMBER, user.getIdCardNumber());
                        SharedPrefUtil.putString(LoginActivity.this, Constants.NICKNAME, user.getNickName());
                    }
                    startActivity(goMain);
                    // 为了找到用户ID对应的数据,清空一下dao,主页会重新获取
                    dao.removeDao();
                    finish();
                    break;
                // 密码错误
                case DataProcessingUtil.LOGIN_FEEDBACK_PWD_ERROR:
                    // 等待对话框消失
                    waitDialog.dismiss();
                    ToastUtils.showToast(LoginActivity.this, R.string.pwd_or_userid_error);
                    break;
                // 用户id不存在
                case DataProcessingUtil.LOGIN_FEEDBACK_ID_INEXISTENCE:
                    // 等待对话框消失
                    waitDialog.dismiss();
                    ToastUtils.showToast(LoginActivity.this, R.string.user_not_exist);
                    break;
                default:
                    waitDialog.dismiss();
                    // 提示用户登录失败
                    ToastUtils.showToast(LoginActivity.this, R.string.login_failure);
                    //Log.e("error", isPause + "--收到广播");

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }


    private void initView() {
        ButterKnife.bind(this);
        // 设置用户名为上次登录的id
        int defaultId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        mEtUserName.setText(defaultId == 0 ? "" : String.valueOf(defaultId));

        mEtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 焦点不在文本框时,检测数据格式,验证并提示
                if (!mEtUserName.hasFocus()) {
                    userName = mEtUserName.getText().toString();
                    if (CommUtil.isEmpty(userName)) {
                        ToastUtils.showToast(LoginActivity.this, R.string.account_not_none);
                        // 判断用户名格式正确
//                    } else if ((!CommUtil.isPhone(userName)) && userName.length() != 6) {
                    } else if ((userName.length() != 6)) {
                        ToastUtils.showToast(LoginActivity.this, R.string.account_iswrong);
                    }
                }
            }
        });
        // 初始化等待对话框
        waitDialog = new WaitDialog(this, R.string.downloading);
        // 设置dialog点击屏幕不消失
        waitDialog.setCanceledOnTouchOutside(false);

        // 注册事件订阅者
        EventBus.getDefault().register(this);

    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isPause) {
                        recLen--;
                        if (!waitDialog.isShowing()) {
                            recLen = Constants.WAITDIALOG_WAIT_MAX_SECONDS;
                            isPause = true;
                        }
                        // 判断到达30秒未收到反馈
                        if (recLen <= 0) {
                            recLen = Constants.WAITDIALOG_WAIT_MAX_SECONDS;
                            // 自己发广播告诉用户失败了
                            EventBus.getDefault().post(new LoginFeekBackEvent(userId, -1));
                            isPause = true;

                        }
                    }
                }
            });
        }
    };

    /**
     * 登录按钮点击事件
     */
    @OnClick(R.id.btn_login)
    public void login() {
        // 登录
        boolean flag = checkTheFormat();
        //boolean isPhone = CommUtil.isPhone(userName);
        if (flag) {
            // MD5加密
            //userPwd = MD5Util.getMD5(userPwd);
            // 发送请求
            waitDialog.show();
            try {
                byte[] data = null;
                // 区别是用用户id登录还是手机号登录
                //if (isPhone) {
                // 封装手机号登录数据
                //    data = DataProcessingUtil.loginDataPackage(userName, userPwd);
                //} else {
                userId = Integer.parseInt(userName);
                // 封装用户ID登录数据
                data = DataProcessingUtil.loginDataPackage(
                        userId, userPwd);
                //}
                // 发送登录请求
                EventBus.getDefault().post(new SendMessage(data));
                // 开始计时,如果30秒内没有反馈回任何数,表示登录失败
                isPause = false;
                timer.schedule(task, 1000, 1000);
            } catch (Exception e) {
                // e.printStackTrace();
                // ToastUtils.showToast(this, R.string.net_excaption);
            }
        }
    }

    /**
     * 注册按钮点击事件
     */
    @OnClick(R.id.btn_register)
    public void register() {
        Intent intent = new Intent();
        intent.setClass(this, RegistActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, 1);
    }

    /**
     * 忘记密码点击事件
     */
    @OnClick(R.id.tv_forget_pwd)
    public void forgetPwd() {
        ToastUtils.showToast(this, "正在开发中,敬请期待...");
//        Intent intent = new Intent();
//        intent.setClass(this, ForgetPassActivity.class);
//        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        waitDialog.dismiss();
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检测页面数据是否符合要求
     *
     * @return true符合  false 不符合
     */
    private boolean checkTheFormat() {
        userName = mEtUserName.getText().toString();
        userPwd = mEtUserPwd.getText().toString();
        // 判断用户名为空
        if (CommUtil.isEmpty(userName)) {
            ToastUtils.showToast(this, R.string.account_not_none);
            return false;
        }
        // 判断密码为空
        if (CommUtil.isEmpty(userPwd)) {
            ToastUtils.showToast(this, R.string.password_is_empty);
            return false;
        }
        // 判断用户名格式正确
        if (userName.length() != 6 || !CommUtil.isNumber(userName)) {
//        if (!CommUtil.isPhone(userName) && (userName.length() != 6 || !CommUtil.isNumber(userName))) {
//        if (userName.length() != 6) {
            ToastUtils.showToast(this, R.string.account_iswrong);
            return false;
        }
        // 判断密码的长度
        if (userPwd.length() != 6) {
            ToastUtils.showToast(this, R.string.pwd_length);
            return false;
        }
        // 判断密码必须是数字
        if (!CommUtil.isNumber(userPwd)) {
            ToastUtils.showToast(this, R.string.pwd_number);
            return false;
        }
        //判断当前网络是否可用
//        if (!CommUtil.netWorkIsAvailable(this)) {
//            ToastUtils.showToast(this, R.string.network_unavailable);
//            return false;
//        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 1) {
            int userId = data.getIntExtra("userId", 0);
            mEtUserName.setText(String.valueOf(userId));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (timer != null) {
            timer.cancel();
        }
    }
}
