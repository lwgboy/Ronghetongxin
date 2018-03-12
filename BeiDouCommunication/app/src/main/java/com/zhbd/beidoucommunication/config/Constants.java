package com.zhbd.beidoucommunication.config;

/**
 * Created by zhangyaru on 2017/8/24.
 */

public class Constants {
    // 群组成员最多个数
    public static final int GROUP_MAX_COUNT = 20;
    // 等待对话框等待的最长秒数
    public static final int WAITDIALOG_WAIT_MAX_SECONDS = 20;


    // 用户id
    public static final String USER_ID = "user_id";
    // 手机号
    public static final String PHONE_NUMBER = "phone_number";
    // 身份证号
    public static final String ID_CARD_NUMBER = "id_card_number";
    // 密码
    public static final String USER_PASSWORD = "password";
    // 昵称
    public static final String NICKNAME = "nickname";


    // 网络状态
    public static final String NETWORK_STATE = "network_state";

    // 上次停止连接网络的时间
    public static final String NETWORK_STOP_TIME = "network_stop_time";


    // 硬件状态
    public static final String HARDWARE_STATE = "hardware_state";
    // 信号强度
    public static final String SIGNAL_STRENGTH = "signal_strength";
    // 等待时长
    public static final String WAIT_TIME = "wait_time";

    // 推送的消息Id
    public static final String MESSAGE_NOTIFICATION_ID = "messagenotificationids";


    // 消息收发状态---发送成功
    public static final int MESSAGE_STATE_SEND_SUCCESS = 0;
    // 消息收发状态---发送失败
    public static final int MESSAGE_STATE_SEND_FAILURE = 1;
    // 消息收发状态---接收
    public static final int MESSAGE_STATE_RECEIVER = 2;
    // 消息收发状态---发送中
    public static final int MESSAGE_STATE_SENDING = 3;


    // 文本消息
    public static final int MESSAGE_TYPE_TEXT = 0;
    // 语音消息
    public static final int MESSAGE_TYPE_VOICE = 1;
    // 消息未读
    public static final int MESSAGE_NO_READ = 0;
    // 消息已读
    public static final int MESSAGE_HAVE_READ = 1;

    // 身份证号长度
    public static final int ID_CARD_LENGTH = 18;
    // 手机号长度
    public static final int PHONE_NUMBER_LENGTH = 11;
    // 用户id长度
    public static final int USER_ID_LENGTH = 3;

    // 接收到语音消息广播
    public static final String RECEIVE_VOICE_MSG_ACTION = "com.receive.voice.msg";

    // 语音压缩完成广播action
    public static final String ACTION_SPX_OK = "com.spx.ok.action";

    // 接收群组号码新消息广播
    public static final String RECEIVE_GROUPNUMBER_UPDATEUI_ACTION = "com.beidou.receive.groupnumber";

    //群操作请求反馈广播action
    public static final String ACTION_GROUP_OPERATE_FEEKBACK = "com.group.operate.feekback";

}
