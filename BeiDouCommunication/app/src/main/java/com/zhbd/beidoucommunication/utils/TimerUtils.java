package com.zhbd.beidoucommunication.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.event.CanSendMsgEvent;
import com.zhbd.beidoucommunication.event.ReceiveTimeNotifyEvent;
import com.zhbd.beidoucommunication.event.WaitTimeEvent;
import com.zhbd.beidoucommunication.ui.activity.AddFriendsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangyaru on 2017/10/12.
 */

public class TimerUtils {

    private final String TAG = "TimeUtils";

    private static TimerUtils timerUtils = new TimerUtils();

    public static TimerUtils getInstance() {
        return timerUtils;
    }

    private TimerUtils() {
        EventBus.getDefault().register(this);

        // 开启计时器
        timer.schedule(task, 1000, 1000);
        waitTime = SharedPrefUtil.getLong(MyApplication.getContextObject(), Constants.WAIT_TIME, 0);
    }

    private long waitTime = 0;
    Timer timer = new Timer();

    // 注册接收者接收广播
    @Subscribe
    public void revTimeNotify(ReceiveTimeNotifyEvent event) {
        // 收到时间估计通报
        waitTime = SharedPrefUtil.getLong(MyApplication.getContextObject(), Constants.WAIT_TIME, 0);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            waitTime--;

            // 封装数据
            Intent intent = new Intent();
            if (waitTime >= 0) {
                EventBus.getDefault().post(new WaitTimeEvent(waitTime));
            }
            // 等于0了,推送通知给用户
            if (waitTime == 0) {
                EventBus.getDefault().post(new CanSendMsgEvent(true));
            }
        }
    };

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
