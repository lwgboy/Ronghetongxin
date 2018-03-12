package com.zhbd.beidoucommunication.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowEmailActivity extends TitlebarActivity {

    @Bind(R.id.tv_show_address)
    TextView mTvAddress;

    @Bind(R.id.tv_show_content)
    TextView mTvContent;

    private String address;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_show_email);
        initView();
    }

    private void initView() {
        setTitleText("");
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(R.string.email_sender, true);
        setRightText(0, false);
        setRightIcon(0, false);

        ButterKnife.bind(this);
        address = getIntent().getStringExtra("address");
        content = getIntent().getStringExtra("content");

        mTvAddress.setText(address);
        mTvContent.setText(content);
    }
}
