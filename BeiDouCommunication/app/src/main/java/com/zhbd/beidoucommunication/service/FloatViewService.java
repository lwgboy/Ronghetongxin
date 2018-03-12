package com.zhbd.beidoucommunication.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.event.ReceiveTimeNotifyEvent;
import com.zhbd.beidoucommunication.manager.MyWindowManager;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public class FloatViewService extends Service {
    private static final String TAG = "FloatViewService";
    //定义浮动窗口布局
    private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;

    private long reclen = 0;

    private long second;

    private boolean isOk;

    private boolean isPause;

    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 1:
                    MyWindowManager.updateUsedPercent("请等待:" + reclen + "'s");

                    Log.e("error", "请等待:" + reclen + "'s");

                    break;
                case 2:
                    if (MyWindowManager.isWindowShowing()) {
                        MyWindowManager.removeSmallWindow(FloatViewService.this);
                    }
                    Log.e("error", "隐藏了");

            }


        }
    };

    // 注册广播接收者接收时间通报广播
    @Subscribe
    public void revTimeNotify(ReceiveTimeNotifyEvent event) {
        setTime();
    }


    Timer timer = new Timer();

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Log.e("error", "run().reclen" + reclen);
            if (!isPause) {
                reclen--;
                if (reclen > 0) {
                    handler.sendEmptyMessage(1);
                    isOk = false;
                    isPause = false;
                } else {
                    handler.sendEmptyMessage(2);
                    isOk = true;
                    isPause = true;
                }
                //Intent intent = new Intent(Constants.ACTION_UPDATE_WAIT_TIME);
                //intent.putExtra("isOk", isOk);
                //sendBroadcast(intent);
            }
        }
    };

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        createFloatView();
//        showView();
        Log.e("error", "startService");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        createFloatView();
////        showView();
//        IntentFilter filter = new IntentFilter(Constants.ACTION_RECEIVER_WAIT_TIME);
//        registerReceiver(mReceiver, filter);
    }

    private void showView() {
        WindowManager.LayoutParams param = new WindowManager.LayoutParams();
        //获取WindowManager
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //设置LayoutParams(全局变量）相关参数

        param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;     // 系统提示类型,重要
        param.format = 1;
        param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

        param.alpha = 1.0f;

        param.gravity = Gravity.LEFT | Gravity.TOP;   //调整悬浮窗口至左上角
        //以屏幕左上角为原点，设置x、y初始值
        param.x = 0;
        param.y = 0;

        //设置悬浮窗口长宽数据
        param.width = 140;
        param.height = 140;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.alert_window_menu, null);

        //显示myFloatView图像
        mWindowManager.addView(mFloatLayout, param);

        //浮动窗口文本
        //mTimeView = (TextView) mFloatLayout.findViewById(R.id.tv_wait_time);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

    }


    private void createFloatView() {
        // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
        if (!MyWindowManager.isWindowShowing()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyWindowManager.createSmallWindow(getApplicationContext());
                }
            });
        }
    }

    /**
     * 给textview设置文本
     */
    public void setTime() {
        second = SharedPrefUtil.getLong(this, Constants.WAIT_TIME, 0);
//        Log.e("error", "second:" + second);
        if (second == 0) {
            isOk = true;
            if (MyWindowManager.isWindowShowing()) {
                MyWindowManager.removeSmallWindow(FloatViewService.this);
            }
        } else if (second > 0) {
            isOk = false;
            if (!MyWindowManager.isWindowShowing()) {
                MyWindowManager.createSmallWindow(FloatViewService.this);
            }
            reclen = second;
            // 防止重复启动计时器的异常
            try {
                isPause = false;
                timer.schedule(task, 1000, 1000);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
        EventBus.getDefault().unregister(this);
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
