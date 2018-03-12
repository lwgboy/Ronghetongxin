package com.zhbd.beidoucommunication;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.zhbd.beidoucommunication.utils.LogcatHelper;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by zhangyaru on 2017/8/24.
 */

public class MyApplication extends Application {
    private static Context context;
    private static MyApplication mIns;
    private Activity topAcitvity;

    public int netCount = 0;

    private Socket receiverSocket = null;

    public Socket getReceiverSocket() {
        return receiverSocket;
    }

    public void setReceiverSocket(Socket socket) {
        receiverSocket = socket;
    }

    // 存放Activity对象,方便一次性全部关掉
    private static ArrayList<Activity> queue = new ArrayList<>();

    private Service globalService;
    private Intent globalServiceIntent;

    public void startGlobalSecrvice() {
        if (globalServiceIntent != null) {
            stopGlobalService();
        }
        globalServiceIntent = new Intent();
        globalServiceIntent.setAction("com.zhbd.beidoucommunication.service.GlobalService");
        globalServiceIntent.setPackage(getPackageName());
        startService(globalServiceIntent);
        //globalService.startService(globalServiceIntent);
    }

    public void stopGlobalService() {
        //globalService.stopService(globalServiceIntent);
        stopService(globalServiceIntent);
    }

    private Service notifyService;
    private Intent notifyServiceIntent;

    public void startnotifySecrvice() {
        if (notifyServiceIntent != null) {
            stopnotifyService();
        }
        notifyServiceIntent = new Intent();
        notifyServiceIntent.setAction("com.zhbd.beidoucommunication.service.GlobalService");
        notifyServiceIntent.setPackage(getPackageName());
        startService(notifyServiceIntent);
        //globalService.startService(globalServiceIntent);
    }

    public void stopnotifyService() {
        //globalService.stopService(globalServiceIntent);
        stopService(notifyServiceIntent);
    }

    private ArrayList<Thread> threads = new ArrayList<>();

    public void addThread(Thread thread) {
        threads.add(thread);
    }

    boolean threadIsStart = false;

    public void startThread() {
//        boolean threadIsStart = SharedPrefUtil.getBoolean(this, "thread_is_start", false);
        for (int i = 0; i < threads.size(); i++) {
            Thread thread = threads.get(i);
            if (!threadIsStart) {
                thread.start();
                //SharedPrefUtil.putBoolean(this, "thread_is_start", true);
            }
        }
        threadIsStart = true;
    }

    public void stopThread() {
        for (int i = 0; i < threads.size(); i++) {
            if (threads.get(i).isAlive()) {
                threads.get(i).start();
            }
        }
    }


    public static void finishQueue() {
        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).finish();
        }
    }

    public static void addToQueue(Activity activity) {
        if (activity != null) {
            queue.add(activity);
        }
    }

    @Override
    public void onCreate() {
        //获取Context
        context = getApplicationContext();
        mIns = this;
        initGlobleActivity();

        LogcatHelper.getInstance(this).start();


        // 异常处理，不需要处理时注释掉这两句即可！
        //CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        //crashHandler.init(context);
    }

    //返回
    public static Context getContextObject() {
        return context;
    }


    private void initGlobleActivity() {
        registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacks() {

                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                    }

                    /** Unused implementation **/
                    @Override
                    public void onActivityStarted(Activity activity) {
                        topAcitvity = activity;
                        Log.e("onActivityStarted===", topAcitvity + "");
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        topAcitvity = activity;
                        Log.e("onActivityResumed===", topAcitvity + "");
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {

                    }
                });
    }

    public static MyApplication getInstance() {
        return mIns;
    }

    /**
     * 公开方法，外部可通过 MyApplication.getInstance().getCurrentActivity() 获取到当前最上层的activity
     */
    public Activity getCurrentActivity() {
        Log.e("MyApplication", topAcitvity.toString());
        return topAcitvity;
    }

//    @Override
//    public void onTrimMemory(int level) {
//        // 程序在内存清理的时候执行
//        Log.e("application", "程序在内存清理的时候执行");
//        super.onTrimMemory(level);
//    }
}
