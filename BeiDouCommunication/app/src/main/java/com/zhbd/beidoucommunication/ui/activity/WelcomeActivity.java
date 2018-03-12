package com.zhbd.beidoucommunication.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.event.LoginFeekBackEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

public class WelcomeActivity extends Activity {
    private String userName;
    private String userPwd;
    private boolean isPhone;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            // 判断是哪里发的消息
            switch (msg.what) {
                // 自动登录
                case 0:
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                // 第一次登录
                case 1:
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
            finish();
        }
    };

    // 时间订阅者
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revLogFeekBack(LoginFeekBackEvent event) {
        byte def = 0;
        int loginState = event.getStatus();
        switch (loginState) {
            // 登录成功
            case DataProcessingUtil.LOGIN_FEEDBACK_SUCCESS:

                ToastUtils.showToast(WelcomeActivity.this, R.string.login_success);

                SharedPrefUtil.putBoolean(WelcomeActivity.this, SharedPrefUtil.ISPHONELOGIN, isPhone);
                // 判断登录方式
                if (isPhone) {
                    SharedPrefUtil.putString(WelcomeActivity.this, SharedPrefUtil.LOGINPHONENUMBER, userName);
                } else {
                    int userId = Integer.parseInt(userName);
                    // 把用户id和密码记录起来
                    SharedPrefUtil.putInt(WelcomeActivity.this, SharedPrefUtil.LOGINUSERID, userId);
                }
                SharedPrefUtil.putString(WelcomeActivity.this, Constants.USER_PASSWORD, userPwd);
                break;
            // 密码错误
            case DataProcessingUtil.LOGIN_FEEDBACK_PWD_ERROR:
                // 提示用户身份证被占用
                ToastUtils.showToast(WelcomeActivity.this, R.string.idCard_register);
                break;
            // 用户id不存在
            case DataProcessingUtil.LOGIN_FEEDBACK_ID_INEXISTENCE:
                // 身份证被或手机号格式非法
            case DataProcessingUtil.REGISTER_FEEDBACK_FORMAT_ILLEGAL:
                // 提示是一样的
                ToastUtils.showToast(WelcomeActivity.this, R.string.pwd_or_userid_error);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        MyApplication.getInstance().startGlobalSecrvice();
//        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void init() {
        // 判断服务是否在运行
//        if (!CommUtil.isServiceWork("com.zhbd.beidoucommunication.service.GlobalService")) {
//            // Log.e("error", "开启GlobalService服务了");
//            // 开启服务,全局接收消息
//            Intent serviceIntent = MyApplication.getInstance().getGlobalServiceIntent();
//            startService(serviceIntent);
//        }
        // 注册事件订阅者
        EventBus.getDefault().register(this);

        // 从sp中获取保存的用户名和密码,自动登录
        // 判断是何种方式登录
        isPhone = SharedPrefUtil.getBoolean(this, SharedPrefUtil.ISPHONELOGIN, false);
        if (isPhone) {
            //userName = SharedPrefUtil.getString(this, Constants.PHONE_NUMBER, "");
        } else {
            userName = String.valueOf(SharedPrefUtil.getInt(this, Constants.USER_ID, 0));
        }
        userPwd = SharedPrefUtil.getString(this, Constants.USER_PASSWORD, "");
        byte[] data = null;
        if (!CommUtil.isEmpty(userName) && !CommUtil.isEmpty(userPwd) && !"0".equals(userName)) {
            if (isPhone) {
                data = DataProcessingUtil.loginDataPackage(userName, userPwd);
            } else {
                data = DataProcessingUtil.loginDataPackage(
                        Integer.parseInt(userName), userPwd);
            }
            // 发送登录信息
            final byte[] finalData = data;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.e("啦啦啦", "发送登录消息了:" + Arrays.toString(finalData));
                    EventBus.getDefault().post(new SendMessage(finalData));
                }
            }.start();
            // 以前登录过,自动登录
            handler.sendEmptyMessageDelayed(0, 4000);
        } else {
            // 本地没有拿到用户名和密码,表示是第一次登录,到登录界面
            handler.sendEmptyMessageDelayed(1, 5000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
