package com.zhbd.beidoucommunication.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.ui.activity.AddFriendsActivity;
import com.zhbd.beidoucommunication.ui.activity.BuildGroupActivity;
import com.zhbd.beidoucommunication.ui.activity.SendEmailActivity;
import com.zhbd.beidoucommunication.ui.activity.SendSmsActivity;

/**
 * Created by zhangyaru on 2017/9/9.
 */

public class PopWindowMenu extends PopupWindow implements View.OnClickListener {
    private final TextView operate_0;
    private final TextView operate_1;
    private View conentView;
    private Context mActivity;
    private final int w;
    private String mFlag;

    public PopWindowMenu(final Activity activity) {
        mActivity = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_window_layout, null);
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        w = activity.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 4);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);

        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        operate_0 = (TextView) conentView.findViewById(R.id.menu_operate_0);
        operate_1 = (TextView) conentView.findViewById(R.id.menu_operate_1);

        operate_0.setOnClickListener(this);
        operate_1.setOnClickListener(this);
    }

    public void setClickOperate(String flag, int name_0_res, int name_1_res) {
        this.mFlag = flag;
        operate_0.setText(name_0_res);
        operate_1.setText(name_1_res);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.menu_operate_0:
                // 消息界面
                if ("message".equals(mFlag)) {
                    //跳转发送短信界面
                    intent.setClass(mActivity, SendSmsActivity.class);
                    intent.putExtra("entry", "PopWindowMenu");
                    // 通讯录界面
                } else {
                    //跳转到添加好友界面
                    intent.setClass(mActivity, AddFriendsActivity.class);
                }
                break;
            case R.id.menu_operate_1:
                // 消息界面
                if ("message".equals(mFlag)) {
                    //跳转发送邮件界面
                    intent.setClass(mActivity, SendEmailActivity.class);
                    // 通讯录界面
                } else {
                    // 跳转到创建群组界面
                    intent.setClass(mActivity, BuildGroupActivity.class);
                }
                break;
        }
        mActivity.startActivity(intent);
        PopWindowMenu.this.dismiss();
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, -(w / 4 - 120), 10);
        } else {
            this.dismiss();
        }
    }
}
