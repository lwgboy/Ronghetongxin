package com.zhbd.beidoucommunication.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.widget.PopWindowMenu;

/**
 * Created by zhangyaru on 2017/8/30.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    // 标题文本
    protected TextView mTvTitleBarName;

    // 返回按键的大布局
    protected LinearLayout mLlGoback;
    // 返回箭头
    protected ImageView mIvBack;
    // 右侧按键的大布局
    protected RelativeLayout mRlOperation;
    // 右侧图标
    protected ImageView mIvRightIcon;
    // 右侧文本
    protected TextView mTvRightText;

    // 大布局填充
    protected FrameLayout mFlContent;

    // Fragment对应的布局view
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_titlebar, null);
        findView(view);
        mRootView = initView();
        mFlContent.addView(mRootView);
        return view;
    }

    protected abstract View initView();

    private void findView(View view) {
        mTvTitleBarName = (TextView) view.findViewById(R.id.tv_title_bar_name);
        mRlOperation = (RelativeLayout) view.findViewById(R.id.rl_operation);
        mLlGoback = (LinearLayout) view.findViewById(R.id.ll_goback);
        mIvBack = (ImageView) view.findViewById(R.id.iv_back_arrow);
        mTvRightText = (TextView) view.findViewById(R.id.tv_right_text);
        mIvRightIcon = (ImageView) view.findViewById(R.id.iv_right_icon);
        mFlContent = (FrameLayout) view.findViewById(R.id.layout_content);
        mIvBack.setOnClickListener(this);
        mLlGoback.setOnClickListener(this);
        mRlOperation.setOnClickListener(this);
        mTvRightText.setOnClickListener(this);
        mIvRightIcon.setOnClickListener(this);
    }

    /**
     * 设置标题名称
     *
     * @param res
     */
    protected void setTitleText(int res) {
        mTvTitleBarName.setText(res);
    }

    /**
     * 设置右侧图标
     *
     * @param res
     * @param isShow
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void setRightIcon(int res, boolean isShow) {
        if (isShow) {
            mIvRightIcon.setVisibility(View.VISIBLE);
            mIvRightIcon.setBackground(getResources().getDrawable(res));
        } else {
            mIvRightIcon.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * 设置左侧图标
     *
     * @param res
     * @param isShow
     */
    protected void setLeftIcon(int res, boolean isShow) {
        if (isShow) {
            mIvBack.setImageResource(res);
            mIvBack.setVisibility(View.VISIBLE);
        } else {
            mIvBack.setVisibility(View.INVISIBLE);
        }
        mIvBack.setBackgroundResource(R.color.trans);
    }

    /**
     * 设置右侧文本
     *
     * @param res
     * @param isShow
     */
    protected void setRightText(int res, boolean isShow) {
        if (isShow) {
            mTvRightText.setVisibility(View.VISIBLE);
            mTvRightText.setText(getResources().getString(res));
        } else {
            mTvRightText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 点击左侧按钮的回调
     */
    protected void clickLeft(BaseFragment fragment) {

    }

    /**
     * 点击右侧按钮的回调
     */
    protected void clickRight(BaseFragment fragment) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back_text:
            case R.id.iv_back_arrow:
            case R.id.ll_goback:
//                ToastUtils.showToast(getActivity(),"点了左边");
                clickLeft(this);
                break;
            case R.id.tv_right_text:
            case R.id.iv_right_icon:
            case R.id.rl_operation:
//                ToastUtils.showToast(getActivity(),"点了右边");
                clickRight(this);
                break;
        }
    }
}
