package com.zhbd.beidoucommunication.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.EmailMessage;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.event.WaitTimeEvent;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SendEmailActivity extends TitlebarActivity implements View.OnFocusChangeListener {

    @Bind(R.id.et_address)
    EditText mEtAddress;

    @Bind(R.id.tv_address)
    TextView mTvAddress;

    @Bind(R.id.et_email_content)
    EditText mEtContent;

    @Bind(R.id.tv_email_content)
    TextView mTvContent;

    private String address;
    private String content;

    /**
     * 注册广播接受者,接收消息并显示到界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revWaitTime(WaitTimeEvent event) {
        // 更新界面,向用户显示需要等待的秒数
        long waitTime = event.getWaitTime();
        if (waitTime > 0) {
            mEtContent.setVisibility(View.GONE);
            mEtContent.setText("");
            mTvContent.setText("请等待" + waitTime + "'s");
            mTvContent.setVisibility(View.VISIBLE);
            mIvRightIcon.setEnabled(false);
            mTvRightText.setEnabled(false);
            mRlOperation.setEnabled(false);
        } else {
            mEtContent.setVisibility(View.VISIBLE);
            mEtContent.setText("");
            mTvContent.setVisibility(View.GONE);
            mTvContent.setText("");
            mIvRightIcon.setEnabled(true);
            mTvRightText.setEnabled(true);
            mRlOperation.setEnabled(true);

        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    SendEmailActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_send_email);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setTitleText(R.string.new_email);
        setRightIcon(R.drawable.next_arrow_white, false);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(R.string.cancel_text, true);
        setRightText(R.string.send, true);

        mEtAddress.setOnFocusChangeListener(this);
        mTvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvAddress.setVisibility(View.GONE);
                mEtAddress.setVisibility(View.VISIBLE);
                mEtAddress.setFocusable(true);
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    protected void clickRight(Activity activity) {
        boolean checkFormat = checkFormat();
        // 地址和内容符合要求
        if (checkFormat) {
            // 发送数据
            byte[] result = DataProcessingUtil.userDefinedDataPackage(
                    DataProcessingUtil.DATA_TYPE_USER_DEFINED,
                    address, content);
            EventBus.getDefault().post(new SendMessage(result));
            ToastUtils.showToast(this, getResources().getString(R.string.send_success));
            // 数据加入数据库
            int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
            DatabaseDao dao = DatabaseDao.getInstance(this, userId);

            EmailMessage email = new EmailMessage();
            email.setAddress(address);
            email.setContent(content);
            email.setSendTime(CommUtil.getDate());
            dao.addDataToEmail(email);
            // 0.5秒后关闭页面
            handler.sendEmptyMessageDelayed(1, 500);
        }
    }

    private boolean checkFormat() {
        address = mEtAddress.getText().toString();
        content = mEtContent.getText().toString();
        byte[] contentBys = new byte[0];
        try {
            contentBys = content.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 判断地址是否是邮箱地址
        if (!CommUtil.isEmailAddress(address)) {
            ToastUtils.showToast(this, getResources().getString(R.string.email_address_format_error));
            return false;
            // 判断内容是否为空
        } else if (CommUtil.isEmpty(content)) {
            ToastUtils.showToast(this, getResources().getString(R.string.content_not_empty));
            return false;
            // 判断内容是否符合长度
        } else if (contentBys.length > 50) {
            ToastUtils.showToast(this, getResources().getString(R.string.content_overlength));
            return false;
        }
        return true;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_address:
                if (!hasFocus) {
                    mEtAddress.setVisibility(View.GONE);
                    mTvAddress.setText(mEtAddress.getText());
                    mTvAddress.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
