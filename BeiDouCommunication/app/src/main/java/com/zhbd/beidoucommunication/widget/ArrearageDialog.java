package com.zhbd.beidoucommunication.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.WindowManager;


/**
 * 等待提示框
 */
public class ArrearageDialog extends Dialog {

    private Activity mActivity;
    private AlertDialog dialog;

    private OnArrearageClickListener mListener;

    /**
     * @param activity
     */
    public ArrearageDialog(Activity activity) {
        super(activity);
        mActivity = activity;
        initView();
    }

    private void initView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("您的账号已欠费,请充值!")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("去充值", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“充值”后的操作
                        mListener.onArrearageClick();
                    }
                })
                .setNegativeButton("下次再说", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                });
        //在dialog show前添加此代码，表示该dialog属于系统dialog。
        dialog = builder.create();
        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));

    }

    /**
     * 显示对话框
     */
    public void showDialog() {
        dialog.show();
    }

    public void setOnArrearageClickListener(OnArrearageClickListener listener) {
        if (listener != null) {
            mListener = listener;
        }
    }

    public interface OnArrearageClickListener {
        void onArrearageClick();
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
