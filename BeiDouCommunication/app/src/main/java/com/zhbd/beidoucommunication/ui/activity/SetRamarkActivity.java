package com.zhbd.beidoucommunication.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetRamarkActivity extends TitlebarActivity {

    @Bind(R.id.et_ramark)
    EditText mEtRamark;

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_set_ramark);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        // 设置标题栏
        setTitleText(R.string.ramark_info);
        setLeftText(R.string.return_text, true);
        setLeftIcon(R.drawable.back_arrow, true);
        setRightIcon(0, false);
        setRightText(R.string.confirm, true);

        // 获取原来的备注名称
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        mEtRamark.setText(name);
    }

    @Override
    protected void clickRight(Activity activity) {
        super.clickRight(activity);
        String newName = mEtRamark.getText().toString();
        byte[] bytes = newName.getBytes();
        if (CommUtil.isEmpty(newName)) {
            ToastUtils.showToast(this, R.string.nick_name_not_none);
            return;
        } else if (bytes.length > 20) {
            ToastUtils.showToast(this, R.string.nick_name_length);
            return;
        } else if (!CommUtil.isString(name)) {
            ToastUtils.showToast(this, R.string.nick_name_format_error);
            return;
        }  else {
            setResult(FriendDetailsActivity.RESULT_CHANGE, new Intent().putExtra("newName", newName));
            finish();
        }
    }
}
