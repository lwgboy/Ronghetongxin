package com.zhbd.beidoucommunication.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;


/**
 * 等待提示框
 */
public class WaitDialog extends Dialog {

    private TextView warn_title;
    private ProgressBar progress;
    private ImageView complete_image;

    /**
     * @param context
     * @param textId  提示文字
     */
    public WaitDialog(Context context, int textId) {
        super(context);
        initView(textId);
    }

    private void initView(int textId) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wait);
        View mRootView = findViewById(R.id.rootView);
//		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(EcgRunApplication.getScreenWidth() - 100, LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mRootView.setLayoutParams(params);
        mRootView.setBackgroundDrawable(new ColorDrawable(0x0000ff00));

        warn_title = (TextView) findViewById(R.id.warn_title);
        warn_title.setText(textId);
        progress = (ProgressBar) findViewById(R.id.progress);
        complete_image = (ImageView) findViewById(R.id.complete_image);
        Window dialogWindow = getWindow();
        ColorDrawable dw = new ColorDrawable(0x0000ff00);
        dialogWindow.setBackgroundDrawable(dw);
        setCanceledOnTouchOutside(false);

    }
//        setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    WaitDialog.this.dismiss();
//                }
//                return false;
//            }
//        });

    /**
     * @param warnText     提示信息
     * @param isVisibility 切换图片显示 true为显示转圈 false为对钩
     * @param isShow       此窗口是否需要显示
     * @param handler      如果为null则说明不需要dismiss的，如果不为null，则此窗口在1000ms后dismiss
     */
    public void setValue(int warnText, boolean isVisibility, boolean isShow, Handler handler) {
        warn_title.setText(warnText);
        if (isVisibility) {
            progress.setVisibility(View.VISIBLE);
            complete_image.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            complete_image.setVisibility(View.VISIBLE);
        }
        if (isShow) {
            this.show();
        }
        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WaitDialog.this.dismiss();
                }
            }, 1000);
        }
    }

    /**
     * @param warnText     提示信息
     * @param isVisibility 切换图片显示 true为显示转圈 false为对钩
     * @param isShow       此窗口是否需要显示
     * @param handler      如果为null则说明不需要dismiss的，如果不为null，则此窗口在1000ms后dismiss
     */
    public void setValue(String warnText, boolean isVisibility, boolean isShow, Handler handler) {
        warn_title.setText(warnText);
        if (isVisibility) {
            progress.setVisibility(View.VISIBLE);
            complete_image.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            complete_image.setVisibility(View.VISIBLE);
        }
        if (isShow) {
            this.show();
        }
        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WaitDialog.this.dismiss();
                }
            }, 1000);
        }
    }

//    public boolean dispatchKeyEvent(KeyEvent event) {
//        switch (event.getKeyCode()) {
//            case KeyEvent.KEYCODE_BACK: {
//                return false;
//            }
//        }
//        return true;
//    }
}
