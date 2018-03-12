package com.zhbd.beidoucommunication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPersonalInfo extends TitlebarActivity {
    // 头像
    @Bind(R.id.iv_header)
    ImageView mIvHeader;
    // 用户ID
    @Bind(R.id.tv_personal_info_userid)
    TextView mtvUserId;
    // 手机号
    @Bind(R.id.ll_info_phone)
    LinearLayout mLlPhone;
    @Bind(R.id.tv_info_phone_number)
    TextView mTvPhone;
    // 身份证号
    @Bind(R.id.ll_info_idcard)
    LinearLayout mLlIdCard;
    @Bind(R.id.tv_info_idcard_number)
    TextView mTvIdCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_edit_personal_info);
        initView();

    }

    private void initView() {
        ButterKnife.bind(this);
        setTitleText(R.string.personal_info);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(R.string.main_button_mine, true);
        setRightIcon(0, false);
        setRightText(0, false);

        // sp中获取本人用户信息并显示
        int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        mtvUserId.setText(userId == 0 ? "" : String.valueOf(userId));
        String phoneNumber = SharedPrefUtil.getString(this, Constants.PHONE_NUMBER, "");
        mTvPhone.setText(phoneNumber);
        String idCardNumber = SharedPrefUtil.getString(this, Constants.ID_CARD_NUMBER, "");
        mTvIdCard.setText(idCardNumber);
    }

    /**
     * 点击修改密码
     */
    @OnClick(R.id.tv_info_update_pwd)
    public void updatePwd() {
        // 跳转到修改密码界面
        Intent intent = new Intent(this, ModifyPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void clickLeft(Activity activity) {
        super.clickLeft(this);
    }
}
