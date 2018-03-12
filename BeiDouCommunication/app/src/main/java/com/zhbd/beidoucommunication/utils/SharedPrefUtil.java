package com.zhbd.beidoucommunication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
    /**HOST*/
    public static final String HOST = "host";
    /**PORT*/
    public static final String PORT = "port";
    /**登录方式*/
    public static final String ISPHONELOGIN = "isPhone";
    /**登录状态*/
    public static final String LOGINSTATE = "loginState";
    /**用户ID*/
    public static final String LOGINUSERID = "login_userid";
    /**手机号*/
    public static final String LOGINPHONENUMBER = "login_phone_number";
    /**密码*/
    public static final String LOGINUSERPWD = "login_userpwd";

    private static SharedPreferences mSp;

    private static SharedPreferences getSharedPref(Context context) {
        if (mSp == null) {
            mSp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mSp;
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getSharedPref(context).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        
        return getSharedPref(context).getBoolean(key, defValue);
    }
    
    public static void putString(Context context, String key, String value) {
        getSharedPref(context).edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue) {
        return getSharedPref(context).getString(key, defValue);
    }
    
    public static void removeString(Context context, String key) {
        getSharedPref(context).edit().remove(key).commit();
    }
    
    public static void putInt(Context context, String key, int value) {
        getSharedPref(context).edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        return getSharedPref(context).getInt(key, defValue);
    }
    public static void putLong(Context context, String key, long value) {
        getSharedPref(context).edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, long defValue) {
        return getSharedPref(context).getLong(key, defValue);
    }

}
