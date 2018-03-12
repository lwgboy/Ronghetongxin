package com.zhbd.beidoucommunication.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPasswordActivity extends TitlebarActivity {

    @Bind(R.id.edit_old_password)
    EditText mEtOld;

    @Bind(R.id.edit_new_password)
    EditText mEtNew;

    @Bind(R.id.edit_again_password)
    EditText mEtAgain;
    private String old;
    private String newPwd;
    private String again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_modify_password);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        setTitleText(R.string.update_password);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(0, false);
        setRightIcon(0, false);
        setRightText(0, false);

    }

    @OnClick(R.id.btn_modify_pwd_confirm)
    public void confirm() {
        ToastUtils.showToast(this, "努力开发中,敬请期待...");
//        old = mEtOld.getText().toString();
//        newPwd = mEtNew.getText().toString();
//        again = mEtAgain.getText().toString();
//        boolean isOk = checkFormat();
//        if (isOk) {
//            // 分装数据发送请求
//        }
    }

    private boolean checkFormat() {
        // 获取原密码
        String primaryPwd = SharedPrefUtil.getString(this, Constants.USER_PASSWORD, "");
        // 判断为空
        if (CommUtil.isEmpty(old)) {
            ToastUtils.showToast(this, R.string.old_empty_hint);
            return false;
        } else if (CommUtil.isEmpty(newPwd)) {
            ToastUtils.showToast(this, R.string.new_empty_hint);
            return false;
        } else if (CommUtil.isEmpty(again)) {
            ToastUtils.showToast(this, R.string.again_empty_hint);
            return false;
            //判断格式
        } else if (CommUtil.isNumber(newPwd)) {
            ToastUtils.showToast(this, R.string.new_pwd_formort_error);
            return false;
            //判断两次密码是否一致
        } else if (!newPwd.equals(again)) {
            ToastUtils.showToast(this, R.string.new_and_again_not_fit);
            return false;
            //判断旧密码是否正确
        } else if (CommUtil.isEmpty(newPwd)) {
            ToastUtils.showToast(this, R.string.new_empty_hint);
            return false;
        } else if (!old.equals(primaryPwd)) {
            ToastUtils.showToast(this, R.string.old_pwd_error);
            return false;
        }
        return true;
    }
}
