package com.zhbd.beidoucommunication.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by and on 2016/8/3.
 */
public class ToastUtils {
    private static Toast mToast;
    private static long twoTime;
    private static long oneTime;
    private static String odlMsg;

    /**
     * @param context
     * @param s
     */
    public static void showToast(Context context, String s) {
        if (mToast == null) {
            mToast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            mToast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(odlMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    mToast.show();
                }
            } else {
                odlMsg = s;
                mToast.setText(s);
                mToast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }
}
