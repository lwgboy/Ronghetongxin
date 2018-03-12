package com.zhbd.beidoucommunication.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TitlebarActivity extends Activity implements View.OnClickListener {
    // 返回按键的大布局
    protected LinearLayout mLlGoback;
    // 返回箭头
    protected ImageView mIvBack;
    // 返回文本
    protected TextView mTvBackText;
    // 标题文本
    protected TextView mTvTitleBarName;
    // 右侧按键的大布局
    protected RelativeLayout mRlOperation;
    // 右侧图标
    protected ImageView mIvRightIcon;
    // 右侧文本
    protected TextView mTvRightText;

    // 大布局填充
    protected FrameLayout mFlContent;

    protected static ViewTreeObserver viewTreeObserver;
    protected InputMethodManager imm;
    protected static ViewTreeObserver.OnGlobalLayoutListener layoutListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titlebar);
        findView();

    }

    // 键盘显示和隐藏的监听
    public interface OnSoftKeyWordShowListener {
        void hasShow(boolean isShow);
    }

    /**
     * 判断软键盘是否弹出
     * * @param rootView
     *
     * @param listener 备注：在不用的时候记得移除OnGlobalLayoutListener
     */
    public static ViewTreeObserver.OnGlobalLayoutListener doMonitorSoftKeyWord(final View rootView, final OnSoftKeyWordShowListener listener) {
        layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);
                final int screenHeight = rootView.getRootView().getHeight();
                //Log.e("TAG", rect.bottom + "#" + screenHeight);
                final int heightDifference = screenHeight - rect.bottom;
                boolean visible = heightDifference > screenHeight / 3;
                if (listener != null)
                    listener.hasShow(visible);
            }
        };
        viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener);
        return layoutListener;
    }

    protected void setKeyboardLoc(View rootView) {
        // 隐藏键盘
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0); //强制隐藏键盘
    }


    private void findView() {
        mLlGoback = (LinearLayout) findViewById(R.id.ll_goback);
        mTvBackText = (TextView) findViewById(R.id.tv_back_text);
        mIvBack = (ImageView) findViewById(R.id.iv_back_arrow);
        mTvTitleBarName = (TextView) findViewById(R.id.tv_title_bar_name);
        mRlOperation = (RelativeLayout) findViewById(R.id.rl_operation);
        mTvRightText = (TextView) findViewById(R.id.tv_right_text);
        mIvRightIcon = (ImageView) findViewById(R.id.iv_right_icon);
        mFlContent = (FrameLayout) findViewById(R.id.layout_content);
        mLlGoback.setOnClickListener(this);
        mTvBackText.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTvTitleBarName.setOnClickListener(this);
        mRlOperation.setOnClickListener(this);
        mTvRightText.setOnClickListener(this);
        mIvRightIcon.setOnClickListener(this);
    }

    /**
     * 填充布局
     *
     * @param layoutRes 布局资源id
     */
    protected void setLayoutRes(int layoutRes) {
        View view = View.inflate(this, layoutRes, null);
        mFlContent.addView(view);
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
    }

    /**
     * 设置左侧文本
     *
     * @param res
     * @param isShow
     */
    protected void setLeftText(int res, boolean isShow) {
        if (isShow) {
            mTvBackText.setText(getResources().getString(res));
            mTvBackText.setVisibility(View.VISIBLE);
        } else {
            mTvBackText.setVisibility(View.INVISIBLE);
        }
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
     * 设置标题名称
     *
     * @param title
     */
    protected void setTitleText(String title) {
        mTvTitleBarName.setText(title);
    }

    /**
     * 设置右侧图标
     *
     * @param res
     * @param isShow
     */
    protected void setRightIcon(int res, boolean isShow) {
        if (isShow) {
            mIvRightIcon.setImageResource(res);
            mIvRightIcon.setVisibility(View.VISIBLE);
        } else {
            mIvRightIcon.setVisibility(View.GONE);
        }
    }

    /**
     * 设置右侧文本
     *
     * @param res
     * @param isShow
     */
    protected void setRightText(int res, boolean isShow) {
        if (isShow) {
            mTvRightText.setText(getResources().getString(res));
            mTvRightText.setVisibility(View.VISIBLE);
        } else {
            mTvRightText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 点击左侧按钮的回调
     */
    protected void clickLeft(Activity activity) {
        activity.finish();
    }

    /**
     * 点击右侧按钮的回调
     */
    protected void clickRight(Activity activity) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back_text:
            case R.id.iv_back_arrow:
            case R.id.ll_goback:
                //ToastUtils.showToast(this,"点了左边");
                clickLeft(this);
                break;
            case R.id.tv_right_text:
            case R.id.iv_right_icon:
            case R.id.rl_operation:
                //ToastUtils.showToast(this,"点了右边");
                clickRight(this);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnGlobalLayoutListener(layoutListener);
        }
    }
}
