package com.zhbd.beidoucommunication.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.event.SendFailUpdateEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.TimerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by zhangyaru on 2017/8/21.
 */

public class GlobalService extends Service {

    private final String TAG = "GlobalService";


    /**
     * 消息队列
     */
    private ArrayList<byte[]> messageQueues = new ArrayList<>();
    private Thread parseDataThread;
    private Thread waitTimeThread;
    private Thread sentMsgThread;
    private Thread receiverSocketAgainCon;
    private Thread receiveThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendMsg(SendMessage event) {
        Log.e("啦啦啦","收到消息,发了");
        byte[] content = event.getContent();
        // 连接网络发送数据
        NetWorkUtil.connectServerWithTCPSocket(content);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        MyApplication application = MyApplication.getInstance();

        // 检测是否有网络
//        boolean netConnect = NetWorkUtil.isNetConnect(NetWorkUtil.getNetWorkState(this));
//        if (!netConnect) {
//            ToastUtils.showToast(this, R.string.net_excaption);
//        }

        Log.e(TAG, "onStart执行了");

        /**
         * 解析收到内容的线程
         */
        class ParseDataThread extends Thread {
            private Handler handler1;

            public Handler getHandler() {//注意哦，在run执行之前，返回的是null
                return handler1;
            }

            @Override
            public void run() {
                super.run();
//                Looper.prepare();
//                handler1 = new Handler() {
//                    public void handleMessage(android.os.Message msg) {
                //这里处理消息
                //Log.i("MThread", "收到消息了：" + Thread.currentThread().getName() + "----" + msg.obj);

                while (true) {
//                    Log.e(TAG,"true");
                    //synchronized (this) {
                    while (messageQueues.size() > 0) {
                        Log.e(TAG, "size > 0");
                        //for (int i = 0; i < messageQueues.size(); i++) {
                        Log.e(TAG, "z之前size=" + messageQueues.size());
                        // 解析数据
                        DataProcessingUtil.receiveDataPackage(messageQueues.get(0));
                        Log.e(TAG, "之后size=" + messageQueues.size());
                        if (messageQueues.size() > 0) {
                            Log.e(TAG, "remove(0)");
                            messageQueues.remove(0);
                        }
                        //}
                    }
                    //}
                }
//                    }
//                };
//                Looper.loop();
            }
        }

        parseDataThread = new ParseDataThread();
        application.addThread(parseDataThread);



        TimerUtils instance;
        // 等待时长工具类
        waitTimeThread = new Thread() {

            @Override
            public void run() {
                super.run();
                TimerUtils instance = TimerUtils.getInstance();

            }
        };
        application.addThread(waitTimeThread);

        // 维护接收消息的socket的线程
        receiverSocketAgainCon = new Thread() {
            @Override
            public void run() {
                super.run();
                NetWorkUtil.againConReceiveSocket();
            }
        };
        application.addThread(receiverSocketAgainCon);


        // 接受消息线程
        receiveThread = new Thread() {
            @Override
            public void run() {
                // 先循环接收消息,再开始发消息,保证所有都能接收到
                while (true) {
                    // 接收收到的数据
                    byte[] receiver = NetWorkUtil.alwaysReceiver();

                    // 加入消息队列,用另一条线程解析数据
                    if (receiver != null) {
                        Log.e(TAG, "receiver != null");
                        messageQueues.add(receiver);
                    }
                }
            }
        };
        application.addThread(receiveThread);

        // 用于约束未发送消息
        Thread sentStatusThread = new Thread() {
            @Override
            public void run() {
                // 每20秒检查一次
                while (true) {
                    // 从数据库中查找有没有未发送成功的消息
                    int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, 0);
                    DatabaseDao dao = DatabaseDao.getInstance(MyApplication.getContextObject(), userId);
                    ArrayList<CommonMessage> list = dao.queryMsgIfSending();
                    // 如果有未读的
                    if (list.size() > 0) {
                        // 获取未读消息的发送时间
                        for (int i=0;i<list.size();i++) {
                            CommonMessage commonMessage = list.get(i);
                            String time = commonMessage.getTime();
                            long sendMsec = CommUtil.parseDate(time);
                            long now = System.currentTimeMillis();
                            // 如果发送超过一分钟,没有收到反馈,
                            if ((now - sendMsec) >= 60 * 1000) {
                                // 吧状态改为发送失败
                                dao.updateStateBy_Id(commonMessage.get_id(),
                                        (byte)Constants.MESSAGE_STATE_SEND_FAILURE);
                                EventBus.getDefault().post(new SendFailUpdateEvent());
                            }
                        }
                    }
                    try {
                        sleep(20 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        application.addThread(sentStatusThread);

        // 开启所有线程
        application.startThread();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.e(TAG,"stopService");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.e(TAG,"Service Destroy");
    }
}
