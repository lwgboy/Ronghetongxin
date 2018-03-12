package com.zhbd.beidoucommunication.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.event.AddFriendFeekBackEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.event.WaitTimeEvent;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.IdcardUtils;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
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

public class AddFriendsActivity extends TitlebarActivity {

    private final String TAG = "AddFriendsActivity";

    @Bind(R.id.et_id_or_number)
    EditText mEtNumber;

    @Bind(R.id.et_addfriend_name)
    EditText mEtName;

    @Bind(R.id.btn_add_friend_confirm)
    Button mBtnConfirm;

    private DatabaseDao dao;
    /**
     * 等待提示框
     */
    private WaitDialog waitDialog;

    // 记录添加好友的类型
    private int addType;

    private int recLen = Constants.WAITDIALOG_WAIT_MAX_SECONDS;
    Timer timer = new Timer();
    private boolean isPause = true;


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
                            // 封装数据
                            EventBus.getDefault().post(new AddFriendFeekBackEvent(0, -1));
                            isPause = true;

                        }
                    }
                }
            });
        }
    };


    // 注册接收者接收广播
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revWaitTime(WaitTimeEvent event) {
        // 收到时间估计通报
//                Log.e(TAG, "收到更新广播");
        long waitTime = event.getWaitTime();
        if (waitTime > 0) {
            mBtnConfirm.setEnabled(false);
            mBtnConfirm.setText("请等待" + waitTime + "'s");
            mBtnConfirm.setBackgroundResource(R.drawable.login_button_bg_down);
        } else {
            mBtnConfirm.setEnabled(true);
            mBtnConfirm.setText(getResources().getString(R.string.confirm));
            mBtnConfirm.setBackgroundResource(R.drawable.friend_send_message_button_bg_selector);
        }

    }

    private String number;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_add_friends);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        // 设置标题栏
        setTitleText(R.string.add_friends);
        setLeftText(R.string.home_pager, true);
        setLeftIcon(R.drawable.back_arrow, true);
        setRightIcon(0, false);
        setRightText(0, false);

        // 初始化等待对话框
        waitDialog = new WaitDialog(this, R.string.being_added);
    }

    private void initData() {
        EventBus.getDefault().register(this);

        // 初始化数据库操作类
        int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(this, userId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revAddFriendFeekBack(AddFriendFeekBackEvent event) {
        int registState = event.getStatus();
        int userId = event.getFriendUserId();
        switch (registState) {
            // 添加成功
            case DataProcessingUtil.ADD_FRIENDS_FEEDBACK_SUCCESS:
                // 对应信息存入数据库
                Friend friend = new Friend();
                friend.setUserId(userId);
                friend.setAddType(addType);
                friend.setName(name);
                switch (addType) {
                    case DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD:
                        friend.setIdCard(number);
                        break;
                    case DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER:
                        friend.setPhoneNumber(number);
                        break;
                }
                dao.addDataToFriend(friend);
                waitDialog.dismiss();
                ToastUtils.showToast(AddFriendsActivity.this, R.string.add_friends_success);
                finish();
                break;
            // 索引非法
            case DataProcessingUtil.ADD_FRIENDS_FEEDBACK_INDEX_ILLEGAL:
                waitDialog.dismiss();
                // 提示用户格式错误
                ToastUtils.showToast(AddFriendsActivity.this, R.string.format_error);
                break;
            // 没有该用户
            case DataProcessingUtil.ADD_FRIENDS_FEEDBACK_INDEX_NOT_EXIST:
                waitDialog.dismiss();
                // 提示用户没有该用户
                ToastUtils.showToast(AddFriendsActivity.this, R.string.user_not_exist);
                break;
            default:
                waitDialog.dismiss();
                // 提示用户没有该用户
                ToastUtils.showToast(AddFriendsActivity.this, R.string.add_friends_failure);

        }
    }

    @OnClick(R.id.btn_add_friend_confirm)
    public void addConfirm() {
        Editable text = mEtNumber.getText();
        if (text != null) {
            number = text.toString();
            checkAndSend(number);
        }
    }

    private void checkAndSend(String number) {
        // 手机号添加
        name = mEtName.getText().toString();
        byte[] bytes = new byte[0];
        try {
            bytes = name.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 判断昵称是否符合规范
        if (CommUtil.isEmpty(name)) {
            ToastUtils.showToast(this, R.string.nick_name_not_none);
            return;
        } else if (!CommUtil.isString(name)) {
            ToastUtils.showToast(this, R.string.nick_name_format_error);
            return;
        } else if (bytes.length > 20) {
            ToastUtils.showToast(this, R.string.nick_name_length);
            return;
        } else if (CommUtil.isEmpty(number)) {
            ToastUtils.showToast(this, R.string.search_condition_not_none);
            return;
        }
        if (CommUtil.isPhone(number)) {
            addType = DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER;
            byte[] result = DataProcessingUtil.addFriendsDataPackage(number,
                    DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER);
            // 请求网络发送数据
            EventBus.getDefault().post(new SendMessage(result));
            waitDialog.show();

//        } else if (CommUtil.isIdCard(number)) {
        } else if (IdcardUtils.isValidatedAllIdcard(number)) {
            addType = DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD;
            // 身份证号添加
            byte[] result = DataProcessingUtil.addFriendsDataPackage(number,
                    DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD);
            // 请求网络发送数据
            EventBus.getDefault().post(new SendMessage(result));
            waitDialog.show();
        } else if (CommUtil.isNumber(number)) {
            addType = DataProcessingUtil.ADD_FRIENDS_FOR_USER_ID;
            // 用户id添加
            byte[] result = DataProcessingUtil.addFriendsDataPackage(number,
                    DataProcessingUtil.ADD_FRIENDS_FOR_USER_ID);
            // 请求网络发送数据
            EventBus.getDefault().post(new SendMessage(result));
            waitDialog.show();
        } else {
            // 格式有误
            ToastUtils.showToast(this, getResources().getString(R.string.format_error));
        }
        if (waitDialog.isShowing()) {
            // 开始计时,如果30秒内没有反馈回任何数,表示登录失败
            isPause = false;
            try {
                timer.schedule(task, 1000, 1000);
            } catch (Exception e) {
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dao != null) {
            dao.close();
        }
        if (timer != null) {
            timer.cancel();
        }
        EventBus.getDefault().unregister(this);
    }
}
