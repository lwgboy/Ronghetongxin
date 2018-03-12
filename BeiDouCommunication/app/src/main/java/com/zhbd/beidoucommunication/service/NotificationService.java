package com.zhbd.beidoucommunication.service;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.domain.ReceiverMessage;
import com.zhbd.beidoucommunication.domain.SmsMessage;
import com.zhbd.beidoucommunication.event.CanSendMsgEvent;
import com.zhbd.beidoucommunication.event.FriendPaidEvent;
import com.zhbd.beidoucommunication.event.ReceiveNewTextMsgEvent;
import com.zhbd.beidoucommunication.event.ReceiveSMSEvent;
import com.zhbd.beidoucommunication.event.ReceiverArrearageEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.event.WaitTimeEvent;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.ui.activity.ChatMessageActivity;
import com.zhbd.beidoucommunication.ui.activity.MainActivity;
import com.zhbd.beidoucommunication.ui.activity.PatternOfPaymentActivity;
import com.zhbd.beidoucommunication.ui.activity.SendSmsActivity;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.widget.ArrearageDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class NotificationService extends Service {
    public final String TAG = "NotificationService";

    // 文字消息的通知类型
    private static final int NOTIFICATION_TYPE_TEXT_MSG = 1001;
    // 短信消息的通知类型
    private static final int NOTIFICATION_TYPE_SMS_MSG = 1002;
    // 可以发消息的通知类型
    private static final int NOTIFICATION_CAN_SEND_MSG_MSG = 1003;

    // 获取消息线程
    private MessageThread thread;

    // 通知栏消息
    private int messageNotificationID;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;

    private boolean isRun = true;

    private String mPackageName;
    private ActivityManager mActivityManager;

    private DatabaseDao dao;

    /**
     * 从服务器端获取消息
     */
    public class MessageThread extends Thread {

        public Handler mHandler = null;

        @Override
        public void run() {
            while (true) {
//                Log.e("error", "run");
                Looper.prepare();
                mHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        Bundle data = msg.getData();
                        switch (msg.what) {
                            case 2:
                                notification(NOTIFICATION_TYPE_TEXT_MSG, data);
//                                Log.e("error", "NOTIFICATION_TYPE_TEXT_MSG");
                                break;
                            case 1:
//                                Log.e("error", "NOTIFICATION_TYPE_SMS_MSG");
                                notification(NOTIFICATION_TYPE_SMS_MSG, data);
                                break;
                            case 3:
                                notification(NOTIFICATION_CAN_SEND_MSG_MSG, null);
                                break;
                        }
//                        Log.e("error", messageNotificationID + "");
                        // 每次通知完，通知ID递增一下，避免消息覆盖掉
                        messageNotificationID++;
                        SharedPrefUtil.putInt(NotificationService.this,
                                Constants.MESSAGE_NOTIFICATION_ID, messageNotificationID);
                    }
                };
                Looper.loop();
            }
        }

    }


    public NotificationService() {
        thread = new MessageThread();
    }

    @Subscribe
    public void revFriendPaid(FriendPaidEvent event) {
        // 接收到欠费广播
        String friendName = event.getFriendName();
        int friendUserId = event.getFriendUserId();
        int money = event.getMoney();
        if (isAppOnForeground()) {
            Dialog dialog = null;
            Log.e(TAG, "接收到朋友代付充值解析广播");
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MyApplication.getInstance().getCurrentActivity());
            builder.setTitle("天降喜事," + (CommUtil.isEmpty(friendName) ?
                    String.valueOf(friendUserId) : "您的好友" + friendName)
                    + "为您充值" + money + "元!!")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("朕知道了", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            //在dialog show前添加此代码，表示该dialog属于系统dialog。
            dialog = builder.create();
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            dialog.show();
        }
    }

    @Subscribe
    public void revArrearage(ReceiverArrearageEvent event) {
        if (isAppOnForeground()) {
            Log.e(TAG, "接收到欠费解析广播");
            ArrearageDialog dialog = new ArrearageDialog(
                    MyApplication.getInstance().getCurrentActivity()
            );
            dialog.setOnArrearageClickListener(new ArrearageDialog.OnArrearageClickListener() {
                @Override
                public void onArrearageClick() {
                    Intent goArrearage = new Intent(MyApplication.getContextObject(),
                            PatternOfPaymentActivity.class);
                    startActivity(goArrearage);
                }
            });
            dialog.showDialog();
        }
    }

    @Subscribe
    public void revWaitTime(CanSendMsgEvent event) {
        Message msg = new Message();
        Log.e(TAG, "接收到可以发消息的广播");
        msg.what = 3;
        thread.mHandler.sendMessage(msg);
    }

    @Subscribe
    public void revTextMsg(ReceiveNewTextMsgEvent event) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        ReceiverMessage receive = event.getMsg();
        msg.what = 2;
        bundle.putSerializable("receiverMsg", receive);
        Log.e(TAG, "mReceive:" + receive.toString());
        msg.setData(bundle);
        thread.mHandler.sendMessage(msg);

        // 如果不在聊天界面或聊天列表界面,添加数据到数据库
        if (!CommUtil.isTopActivy("ChatMessageActivity") &&
                !CommUtil.isTopActivy("MainActivity")) {
            Log.e(TAG, "NotificationService收到文字消息并加入数据库");
            CommonMessage commonMessage = new CommonMessage();
            commonMessage.setSenderNumber(receive.getSenderUserId());
            commonMessage.setContent(receive.getContent());
            String name = dao.queryFriendNameByUserId(receive.getSenderUserId());
            commonMessage.setSenderName(name);
            commonMessage.setType(Constants.MESSAGE_TYPE_TEXT);
            commonMessage.setIsRead(Constants.MESSAGE_NO_READ);
            commonMessage.setTime(CommUtil.getDate());
            commonMessage.setStatus(Constants.MESSAGE_STATE_RECEIVER);
            commonMessage.setFrom(receive.getFrom());

            dao.addDataToMessage(commonMessage);
            // 向后台发送接收成功反馈
            byte[] result = DataProcessingUtil.receiverTextMsgFeedback(receive.getTextMsgNumber());
            EventBus.getDefault().post(new SendMessage(result));
//                    NetWorkUtil.connectServerWithTCPSocket(result);
        }
    }

    @Subscribe
    public void revSmsMsg(ReceiveSMSEvent event) {
        //Log.e(TAG, "接受到手机短信");
        Message msg = new Message();
        Bundle bundle = new Bundle();
        msg.what = 1;
        String address = event.getAddress();
        String content = event.getContent();
        // 存入手机号
        bundle.putString("sms_address", address);
        // 存入内容
        bundle.putString("sms_content", content);
        msg.setData(bundle);
        thread.mHandler.sendMessage(msg);

        // 如果不在短信界面或聊天列表界面,添加数据到数据库
        if (!CommUtil.isTopActivy("MainActivity") &&
                !CommUtil.isTopActivy("SendSmsActivity")) {
            SmsMessage sms = new SmsMessage();
            sms.setPhoneNumber(address);
            sms.setContent(content);
//                    String name = SendSmsActivity
//                            .getContactNameByPhoneNumber(NotificationService.this, address);
            //sms.setSenderName(name);
            sms.setType(Constants.MESSAGE_TYPE_TEXT);
            sms.setIsRead(Constants.MESSAGE_NO_READ);
            sms.setTime(CommUtil.getDate());
            sms.setStatus(Constants.MESSAGE_STATE_RECEIVER);
            //Log.e(TAG, "receiver:SmsMessage=" + sms.toString());
            // 添加消息到数据库
            dao.addDataToSms(sms);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        int userId = SharedPrefUtil.getInt(NotificationService.this, Constants.USER_ID, -1);
        dao = DatabaseDao.getInstance(NotificationService.this, userId);

        EventBus.getDefault().register(this);

//        Log.e("error", "onStartCommand");
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    /**
     * 推送通知
     *
     * @param type 通知类型
     * @param data bundle数据
     */
    private void notification(int type, Bundle data) {
        // 判断是否在App界面
        if (!isAppOnForeground()) {
            Intent resultIntent = null;
            // 本地获取消息id,保证每次不一样
            messageNotificationID = SharedPrefUtil.getInt(
                    NotificationService.this, Constants.MESSAGE_NOTIFICATION_ID, 0);

            String title = "";
            String text = "";
            String name = "";
            // 判断是文字消息还是短信消息
            switch (type) {
                case NOTIFICATION_TYPE_TEXT_MSG:
//                    Log.e(TAG, "notifyNOTIFICATION_TYPE_TEXT_MSG");

                    ReceiverMessage receiverMsg = (ReceiverMessage)
                            data.getSerializable("receiverMsg");
                    Log.e(TAG, "mReceive:" + receiverMsg.toString());

                    int userId = SharedPrefUtil.getInt(NotificationService.this, Constants.USER_ID, -1);
                    DatabaseDao dao = DatabaseDao.getInstance(NotificationService.this, userId);
                    name = dao.queryFriendNameByUserId(receiverMsg.getSenderUserId());
                    if (CommUtil.isEmpty(name)) {
                        title = String.valueOf(receiverMsg.getSenderUserId());
                    } else {
                        title = name;
                    }
                    text = receiverMsg.getContent();
                    resultIntent = new Intent(this, ChatMessageActivity.class);
                    // 需要一个friend对象
                    Friend friend = new Friend();
                    friend.setUserId(receiverMsg.getSenderUserId());
                    friend.setName(name);
                    resultIntent.putExtra("instance", friend);
                    break;

                case NOTIFICATION_TYPE_SMS_MSG:
//                    Log.e(TAG, "notifyNOTIFICATION_TYPE_SMS_MSG");

                    String address = data.getString("sms_address");
                    String content = data.getString("sms_content");
                    // 从联系人中查找是否存有该联系人
//                    name = SendSmsActivity.getContactNameByPhoneNumber(
//                            NotificationService.this, address);
                    // 消息中存入号码
                    if (CommUtil.isEmpty(name)) {
                        title = String.valueOf(address);
                    } else {
                        title = name;
                    }
                    text = content;
                    resultIntent = new Intent(this, SendSmsActivity.class);
                    resultIntent.putExtra("entry", "NotificationService");
                    resultIntent.putExtra("phone", address);
                    resultIntent.putExtra("name", name);
                    break;
                case NOTIFICATION_CAN_SEND_MSG_MSG:
                    title = getResources().getString(R.string.app_name);
                    text = getResources().getString(R.string.can_send);
                    resultIntent = new Intent(this, MainActivity.class);
                    break;
            }

            Bitmap btm = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
            mBuilder = new NotificationCompat.Builder(
                    this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setTicker(title)//第一次提示消息的时候显示在通知栏上
//        mBuilder.setNumber(12);
                    .setLargeIcon(btm)
                    .setAutoCancel(true)//自己维护通知的消失
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)// 设置优先级,第二高的优先级
                    .setDefaults(Notification.DEFAULT_SOUND)//获取默认铃声
//                .setVibrate(new long[]{0, 300, 500, 700});// 设置震动方式,延迟0ms，然后振动300ms，在延迟500ms，接着在振动700ms。
                    .setVibrate(new long[]{0, 300, 500});// 设置震动方式,延迟0ms，然后振动300ms，在延迟500ms，接着在振动700ms。
            //封装一个Intent
            if (resultIntent == null) {
                return;
            }
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this, 0, resultIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            // 设置通知主题的意图
            mBuilder.setContentIntent(resultPendingIntent);
            //获取通知管理器对象
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(messageNotificationID, mBuilder.build());
        }
    }

    /**
     * 判断栈顶的是否是自己的界面
     *
     * @return
     */
    public boolean isAppOnForeground() {
        mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        mPackageName = getPackageName();
        List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (mPackageName.equals(tasksInfo.get(0).topActivity
                    .getPackageName())) {
                return true;
            }
        }
        return false;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
//        Log.e("error", "onDestroy");
        // System.exit(0);
        super.onDestroy();
        isRun = false;
        EventBus.getDefault().unregister(this);
    }
}
