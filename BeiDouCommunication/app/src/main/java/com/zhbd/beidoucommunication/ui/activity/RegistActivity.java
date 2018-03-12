package com.zhbd.beidoucommunication.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.User;
import com.zhbd.beidoucommunication.event.RegFeekBackEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.event.WaitTimeEvent;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.widget.WaitDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistActivity extends TitlebarActivity {
    // 手机号
    @Bind(R.id.et_phone_number)
    EditText mEtPhoneNumber;
    // 身份证号
    @Bind(R.id.et_idcard_number)
    EditText mEtIdCard;
    // 昵称
    @Bind(R.id.et_nick_name)
    EditText mEtNickName;
    // 密码
    @Bind(R.id.et_password)
    EditText mEtPassword;
    // 确认密码
    @Bind(R.id.et_confirmpassword)
    EditText mEtConfirmPwd;
    // 确定按钮
    @Bind(R.id.btn_register_ok)
    Button mBtnOk;

    private String phoneNumber;
    private String idCard;
    private String nickName;
    private String password;
    private String cofirmPwd;

    private DatabaseDao dao;

    private int recLen = 30;
    Timer timer = new Timer();
    private boolean isPause;
    /**
     * 等待提示框
     */
    private WaitDialog waitDialog;


    // 注册接收者接收广播
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revWaitTime(WaitTimeEvent event) {
        // 更新界面,向用户显示需要等待的秒数
        long waitTime = event.getWaitTime();
        if (waitTime > 0) {
            mBtnOk.setEnabled(false);
            mBtnOk.setText("请等待" + waitTime + "'s");
            mBtnOk.setBackgroundResource(R.drawable.login_button_bg_down);
        } else {
            mBtnOk.setEnabled(true);
            mBtnOk.setText(getResources().getString(R.string.confirm));
            mBtnOk.setBackgroundResource(R.drawable.login_button_bg);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_regist);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        // 设置标题属性
        setTitleText(R.string.register);
        setLeftText(R.string.login, true);
        setLeftIcon(R.drawable.back_arrow, true);
        // 检测手机号格式
        mEtPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 焦点不在文本框时,检测数据格式,验证并提示
                if (!mEtPhoneNumber.hasFocus()) {
                    phoneNumber = mEtPhoneNumber.getText().toString();
                    if (CommUtil.isEmpty(phoneNumber)) {
                        ToastUtils.showToast(RegistActivity.this, R.string.phone_number_not_none);
                    } else if (!CommUtil.isPhone(phoneNumber)) {
                        ToastUtils.showToast(RegistActivity.this, R.string.phone_number_iswrong);
                    }
                }
            }
        });

        // 初始化等待对话框
        waitDialog = new WaitDialog(this, R.string.registerloading);
        // 设置dialog点击屏幕不消失
        waitDialog.setCanceledOnTouchOutside(false);

        // 注册广播接收者,接收注册反馈
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revRegFeekBack(RegFeekBackEvent event) {
        int registState = event.getStatus();
        int userId = event.getUserId();
        // 只有在对话框显示的情况下,才继续接受数据
        if (waitDialog.isShowing()) {
            switch (registState) {
                // 注册成功
                case DataProcessingUtil.REGISTER_FEEDBACK_SUCCESS:
                    ToastUtils.showToast(RegistActivity.this, R.string.regist_success);
                    // 对应的用户id保存起来,整条数据存到数据库
                    //SharedPrefUtil.putInt(RegistActivity.this, Constants.USER_ID, userId);
                    User user = new User();
                    user.setUserId(userId);
                    user.setPhoneNumber(phoneNumber);
                    user.setIdCardNumber(idCard);
                    user.setNickName(nickName);
                    user.setPassWord(password);

                    //存入数据库
                    dao = DatabaseDao.getInstance(RegistActivity.this, 0);
                    dao.addDateToUser(user);
                    //设置返回数据
                    Intent intent = new Intent();
                    intent.putExtra("userId", userId);
                    RegistActivity.this.setResult(RESULT_OK, intent);
                    waitDialog.dismiss();
                    finish();
                    break;
                // 身份证被占用
                case DataProcessingUtil.REGISTER_FEEDBACK_IDCARD_OCCUPY:
                    waitDialog.dismiss();
                    // 提示用户身份证被占用
                    ToastUtils.showToast(RegistActivity.this, R.string.idCard_register);
                    break;
                // 手机号被占用
                case DataProcessingUtil.REGISTER_FEEDBACK_PHONE_OCCUPY:
                    waitDialog.dismiss();
                    // 提示用户手机号被占用
                    ToastUtils.showToast(RegistActivity.this, R.string.phone_register);
                    break;
                // 身份证被或手机号格式非法
                case DataProcessingUtil.REGISTER_FEEDBACK_FORMAT_ILLEGAL:
                    waitDialog.dismiss();
                    // 提示用户身份证被或手机格式非法
                    ToastUtils.showToast(RegistActivity.this, R.string.format_illegal);
                    break;
                default:
                    waitDialog.dismiss();
                    // 提示用户注册失败
                    ToastUtils.showToast(RegistActivity.this, R.string.regist_failure);
            }
        }
    }

    @OnClick({R.id.btn_register_ok})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_register_ok:
                // 确定注册按钮
                // 检测数据格式
                boolean flag = checkTheFormat();
                // 表示符合要求
                if (flag) {
                    waitDialog.show();
                    // 注册
                    User user = new User();
                    user.setPhoneNumber(phoneNumber);
                    user.setIdCardNumber(idCard);
                    user.setNickName(nickName);
                    user.setPassWord(password);
                    // 封装注册数据
                    byte[] data = DataProcessingUtil.registDataPackage(user);
                    // 发送注册请求
                    EventBus.getDefault().post(new SendMessage(data));
                    // 开始计时,如果30秒内没有反馈回任何数,表示注册失败
                    isPause = false;
                    try {
                        timer.schedule(task, 1000, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                    //ToastUtils.showToast(this, R.string.server_connection_failure);
                }
                break;
        }
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
                            EventBus.getDefault().post(new RegFeekBackEvent(0, -1));
                            isPause = true;
                        }
                    }
                }
            });
        }
    };

    /**
     * 检测页面数据是否符合要求
     *
     * @return true符合  false 不符合
     */
    private boolean checkTheFormat() {
        phoneNumber = mEtPhoneNumber.getText().toString();
        // 判断手机号为空
        if (CommUtil.isEmpty(phoneNumber)) {
            ToastUtils.showToast(this, R.string.phone_number_not_none);
            return false;
        }
        // 判断手机号格式错误
        if (!CommUtil.isPhone(phoneNumber)) {
            ToastUtils.showToast(this, R.string.phone_number_iswrong);
            return false;
        }
        idCard = mEtIdCard.getText().toString();
        // 判断身份证号为空
        if (CommUtil.isEmpty(idCard)) {
            ToastUtils.showToast(this, R.string.id_card_not_none);
            return false;
        }
        // 判断身份证号格式错误
        if (!CommUtil.isIdCard(idCard)) {
//        if (!IdcardUtils.isValidatedAllIdcard(idCard)) {
//        if (!Identity.checkIDCard(idCard)) {
            ToastUtils.showToast(this, R.string.id_card_iswrong);
            return false;
        }

        nickName = mEtNickName.getText().toString();
        // 判断昵称为空
        if (CommUtil.isEmpty(nickName)) {
            ToastUtils.showToast(this, R.string.nick_name_not_none);
            return false;
        }
        // 判断昵称格式
        if (!CommUtil.isString(nickName)) {
            ToastUtils.showToast(this, R.string.nick_name_format_error);
            return false;
        }
        // 判断昵称长度
        byte[] bytes = new byte[0];
        try {
            bytes = nickName.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (bytes.length > 20) {
            ToastUtils.showToast(this, R.string.nick_name_length);
            return false;
        }
        password = mEtPassword.getText().toString();
        cofirmPwd = mEtConfirmPwd.getText().toString();
        //判断密码或确认密码为空
        if (CommUtil.isEmpty(password) || CommUtil.isEmpty(cofirmPwd)) {
            ToastUtils.showToast(this, R.string.password_is_empty);
            return false;
        }
        // 判断密码或确认密码的长度
        if (password.length() != 6 || cofirmPwd.length() != 6) {
            ToastUtils.showToast(this, R.string.pwd_length);
            return false;
        }
        // 判断密码格式,只能是数字
        if (!CommUtil.isNumber(password)) {
            ToastUtils.showToast(this, R.string.pwd_number);
            return false;
        }
        // 判断密码和确认密码是否一致
        if (!(password.equals(cofirmPwd))) {
            ToastUtils.showToast(this, R.string.pwd_not_same);
            return false;
        }
        // 判断网络是否可用
//        if (!CommUtil.isNetWorkAvailable(this)) {
//            ToastUtils.showToast(this, R.string.network_unavailable);
//            return false;
//        }
        return true;
    }

    /**
     * 判断点击的是否是EditText区域
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            } else {
                //mListView.setSelection(mList.size()-1);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dao != null) {
            dao.close();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
