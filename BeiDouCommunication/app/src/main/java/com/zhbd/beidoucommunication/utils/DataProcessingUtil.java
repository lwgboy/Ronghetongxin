package com.zhbd.beidoucommunication.utils;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.BeidouMachineState;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.domain.IcCardInfo;
import com.zhbd.beidoucommunication.domain.ReceiverMessage;
import com.zhbd.beidoucommunication.domain.User;
import com.zhbd.beidoucommunication.event.AddFriendFeekBackEvent;
import com.zhbd.beidoucommunication.event.BeiDouStatusEvent;
import com.zhbd.beidoucommunication.event.FriendPaidEvent;
import com.zhbd.beidoucommunication.event.LoginFeekBackEvent;
import com.zhbd.beidoucommunication.event.ReceiveNewTextMsgEvent;
import com.zhbd.beidoucommunication.event.ReceiveEmailEvent;
import com.zhbd.beidoucommunication.event.ReceiveSMSEvent;
import com.zhbd.beidoucommunication.event.ReceiveTimeNotifyEvent;
import com.zhbd.beidoucommunication.event.ReceiverArrearageEvent;
import com.zhbd.beidoucommunication.event.RegFeekBackEvent;
import com.zhbd.beidoucommunication.event.TextMsgFeekBackEvent;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.manager.IcCardManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhangyaru on 2017/8/23.
 */

public class DataProcessingUtil {

    public static final String TAG = "DataProcessingUtil";

    // 数据包固定长度
    public static final int DATAPACKAGE_FIXED_LENGTH = 15;
    // 注册内容包固定长度
    public static final int REGISTER_FIXED_LENGTH = 35;
    // 用户ID登录内容包固定长度
    public static final int LOGIN_USERID_FIXED_LENGTH = 9;
    // 手机号登录内容包固定长度
    public static final int LOGIN_PHONE_FIXED_LENGTH = 17;
    // 修改密码内容包固定长度
    public static final int ALTER_PWD_FIXED_LENGTH = 15;
    // 群组操作内容包固定长度
    public static final int GROUP_OPERATE_FIXED_LENGTH = 5;
    // 自定义消息内容包固定长度
    public static final int USER_DEFINED_FIXED_LENGTH = 3;
    // 文字消息包固定长度
    public static final int TEXT_MESSAGE_LENGTH = 13;
    // 好友代付数据包固定长度
    public static final int FRIEND_PAID_LENGTH = 8;
    // 文字包固定长度
    //public static final int LOGIN_FIXED_LENGTH = 38;


    // 注册-数据类型
    public static final byte DATA_TYPE_REGISTER = 0;
    // 登录-数据类型
    public static final byte DATA_TYPE_LOGIN = 1;
    // 修改密码-数据类型
    public static final byte DATA_TYPE_ALTER_PWD = 2;
    // 添加好友-数据类型
    public static final byte DATA_TYPE_ADD_FRIENDS = 3;
    // 群操作请求-数据类型
    public static final byte DATA_TYPE_GROUP_OPERATE = 4;
    // 文字消息-数据类型
    public static final byte DATA_TYPE_TEXT_MSG = 5;
    // 语音消息-数据类型
    public static final byte DATA_TYPE_VOICE_MSG = 6;
    // 文件消息-数据类型
    public static final byte DATA_TYPE_FILE_MSG = 7;
    // 时间估计通报-数据类型
    public static final byte DATA_TYPE_TIME_NOTIFICATION = 8;

    // 北斗机状态接收-数据类型
    public static final byte DATA_TYPE_BEIDOUJI_STATE = 10;
    // 接收的文字消息-数据类型
    public static final byte DATA_TYPE_RECEIVER_TEXT_MSG = 15;
    // 接收的语音消息-数据类型
    public static final byte DATA_TYPE_RECEIVER_VOICE_MSG = 16;

    // 朋友代付 - 数据类型
    public static final byte DATA_TYPE_FRIEND_PAID = 29;

    // 自定义信息发送-数据类型
    public static final byte DATA_TYPE_USER_DEFINED = 30;
    // 自定义信息接收-数据类型
    public static final byte DATA_TYPE_RECEIVER_USER_DEFINED = 30;
    // 接收欠费通报  - 数据类型
    public static final byte DATA_TYPE_RECEIVER_ARREARAGE = 31;


    // 添加好友-身份证号-索引类型
    public static final byte ADD_FRIENDS_FOR_IDCARD = 0;
    // 添加好友-手机号-索引类型
    public static final byte ADD_FRIENDS_FOR_PHONE_NUMBER = 1;
    // 添加好友-用户ID-索引类型
    public static final byte ADD_FRIENDS_FOR_USER_ID = 2;

    // 群操作请求-建群-操作类型
    public static final byte GROUP_OPERATE_BUILD_GROUP = 0;
    // 群操作请求-加友请求-操作类型
    public static final byte GROUP_OPERATE_ADD_MEMBER = 1;
    // 群操作请求-撤群请求-操作类型
    public static final byte GROUP_OPERATE_EXIT_GROUP = 2;

    // 自定义信息-手机短信-信息类型
    public static final byte USER_DEFINED_TYPE_SMS = 0;
    // 自定义信息-电子邮件-信息类型
    public static final byte USER_DEFINED_TYPE_EMAIL = 1;

    // 注册成功-注册反馈
    public static final byte REGISTER_FEEDBACK_SUCCESS = 0;
    // 身份证号被占用-注册反馈
    public static final byte REGISTER_FEEDBACK_IDCARD_OCCUPY = 1;
    // 手机号被占用-注册反馈
    public static final byte REGISTER_FEEDBACK_PHONE_OCCUPY = 2;
    // 手机号或身份证号非法-注册反馈
    public static final byte REGISTER_FEEDBACK_FORMAT_ILLEGAL = 3;

    // 登录成功-登录反馈
    public static final byte LOGIN_FEEDBACK_SUCCESS = 0;
    // 密码错误 - 登录反馈
    public static final byte LOGIN_FEEDBACK_PWD_ERROR = 1;
    // 用户id不存在-登录反馈
    public static final byte LOGIN_FEEDBACK_ID_INEXISTENCE = 2;

    // 添加好友成功-添加好友反馈
    public static final byte ADD_FRIENDS_FEEDBACK_SUCCESS = 0;
    // 索引非法-添加好友反馈
    public static final byte ADD_FRIENDS_FEEDBACK_INDEX_ILLEGAL = 1;
    // 索引不存在-添加好友反馈
    public static final byte ADD_FRIENDS_FEEDBACK_INDEX_NOT_EXIST = 2;

    // 文字消息发送成功-文字消息反馈
    public static final byte TEXT_MSG_SEND_FEEDBACK_SUCCESS = 0;
    // 文字消息发送失败-文字消息反馈
    public static final byte TEXT_MSG_SEND_FEEDBACK_FAILURE = 1;

    /**
     * 来自北斗
     */
    public static final int FROM_TYPE_BEIDOU = 100;
    /**
     * 来自互联网
     */
    public static final int FROM_TYPE_INTENT = 101;
    /**
     * 来自舒拉亚
     */
    public static final int FROM_TYPE_THURAYA = 102;

    /**
     * 封装发送的数据包
     *
     * @param dataType 数据类型
     * @param content  数据内容
     */
    private static byte[] sendDataPackage(byte dataType, byte[] content) {
        // 包头6个$  长度2字节  数据类型  1字节  内容   包尾6个!
        int flag = 0;
        // 创建数组,长度为数据包定长加内容长度
        int len = DATAPACKAGE_FIXED_LENGTH + content.length;
        byte[] result = new byte[len];
        for (int i = 0; i < 6; i++) {
            result[i] = '$';
            result[result.length - 1 - i] = '!';
        }
        flag += 6;
        // 长度
        flag = parseLen(len, result, flag);
        // 数据类型
        result[flag++] = dataType;
        // 内容
        addSmallArrToBigArr(result, content, flag);
        //Log.e(TAG, Arrays.toString(result));
        return result;
    }

    /**
     * 解析接收到的数据包,确定数据类型
     *
     * @param bys 接收到的内容
     * @return 数据类型
     */
    public static byte receiveDataPackage(byte[] bys) {
        //Log.e("error","进来了解析接收数据");
        int flag = 0;
        byte dataType = -1;
        if (bys != null) {
//            Log.e("error","bys!=null");
            if ("$TXSQ".equals(new String(bys, 0, 5))) {
                Log.e("error", bys.length + "---");

                parseVoiceMsgDataPackage(bys);
            }
            // 判断数据包头和包尾是否与格式一致
            if ("$$$$$$".equals(new String(bys, 0, 6)) &&
                    "!!!!!!".equals(new String(bys, bys.length - 6, 6))) {
                flag += 6;
                //Log.e("error", "嗯~~是我要的");
                // 解析长度
                int len = parseLen(bys, flag);
                flag += 2;
                // 判断数据类型
                dataType = bys[flag++];
                // 截取内容数组
                byte[] content = new byte[len - DATAPACKAGE_FIXED_LENGTH];
                addBigArrToSmallArr(bys, content, flag);
                // 判断文字消息
                int iThuraya = dataType << 27 >> 27;
                int iInternet = dataType << 26 >> 26;
                // 文字消息来自北斗
                if (dataType == DATA_TYPE_RECEIVER_TEXT_MSG) {
                    receiverTextMsg(FROM_TYPE_BEIDOU, content);
                    // 文字消息来自舒拉亚
                } else if (iThuraya == DATA_TYPE_RECEIVER_TEXT_MSG) {
                    receiverTextMsg(FROM_TYPE_THURAYA, content);
                    // 文字消息来自网络
                } else if (iInternet == DATA_TYPE_RECEIVER_TEXT_MSG) {
                    receiverTextMsg(FROM_TYPE_INTENT, content);
                }

                // 文字消息反馈来自北斗
                if (dataType == DATA_TYPE_TEXT_MSG
                        // 文字消息反馈来自舒拉亚
                        || iThuraya == DATA_TYPE_TEXT_MSG
                        // 文字消息反馈来自网络
                        || iInternet == DATA_TYPE_TEXT_MSG) {
                    textMsgFeedback(content);
                }

                switch (dataType) {
                    // 注册反馈
                    case DATA_TYPE_REGISTER:
                        registFeedback(content);
                        break;
                    // 登录反馈
                    case DATA_TYPE_LOGIN:
                        loginFeedback(content);
                        break;
                    // 修改密码反馈
                    case DATA_TYPE_ALTER_PWD:
                        alterPwdFeedback(content);
                        break;
                    // 添加好友反馈
                    case DATA_TYPE_ADD_FRIENDS:
                        addFriendsFeedback(content);
                        break;
                    // 群操作反馈
                    case DATA_TYPE_GROUP_OPERATE:
                        groupRequestFeedback(content);
                        break;
                    // 文字消息反馈
//                    case DATA_TYPE_TEXT_MSG:
//                        textMsgFeedback(content);
//                        break;
                    // 接受文字消息
                    // 消息来自北斗
//                    case DATA_TYPE_RECEIVER_TEXT_MSG:
//                            // 消息来自舒拉亚
//                            | (DATA_TYPE_RECEIVER_TEXT_MSG << 27 >> 27)
//                            // 消息来自以太网
//                            |(DATA_TYPE_RECEIVER_TEXT_MSG << 26 >> 26):
//                        receiverTextMsg(content);
//                        break;
                    // 语音消息反馈
                    case DATA_TYPE_VOICE_MSG:
//                        voiceMsgFeedback(content);
                        break;
                    // 时间估计通报
                    case DATA_TYPE_TIME_NOTIFICATION:
                        timeNotification(content);
                        break;
                    // 接收到北斗机状态
                    case DATA_TYPE_BEIDOUJI_STATE:
                        beidouMachineState(content);
                        break;
                    // 接收到的语音消息
                    case DATA_TYPE_RECEIVER_VOICE_MSG:
//                        receiverVoiceMsg(content);
                        break;
                    // 接收到的自定义消息
                    case DATA_TYPE_RECEIVER_USER_DEFINED:
                        receiverUserDefined(content);
                        break;
                    // 接收到的欠费通吧
                    case DATA_TYPE_RECEIVER_ARREARAGE:
                        receiverArrearage(content);
                        break;
                    // 接收到的朋友代付充值通报
                    case DATA_TYPE_FRIEND_PAID:
                        receiverFriendPaid(content);
                        break;
                }
            }
        }
        return dataType;
    }

    /**
     * 封装注册数据
     *
     * @param user 用户信息
     * @return 组合好的数据
     */
    public static byte[] registDataPackage(User user) {
        // 注册固定长度
        byte[] bys = new byte[REGISTER_FIXED_LENGTH];
        int flag = 0;
        try {
            // 身份证号
            byte[] idcardBys = user.getIdCardNumber().getBytes("GBK");
            addSmallArrToBigArr(bys, idcardBys, flag);
            flag += idcardBys.length;
            // 手机号
            byte[] phoneBys = user.getPhoneNumber().getBytes("GBK");
            addSmallArrToBigArr(bys, phoneBys, flag);
            flag += phoneBys.length;
            // 密码
            byte[] pwdBys = user.getPassWord().getBytes("GBK");
            addSmallArrToBigArr(bys, pwdBys, flag);
            flag += pwdBys.length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 把数据存到本地
        SharedPrefUtil.putString(MyApplication.getContextObject(), Constants.PHONE_NUMBER, user.getPhoneNumber());
        //SharedPrefUtil.putString(MyApplication.getContextObject(), Constants.ID_CARD_NUMBER, user.getIdCardNumber());
        SharedPrefUtil.putString(MyApplication.getContextObject(), Constants.USER_PASSWORD, user.getPassWord());
        SharedPrefUtil.putString(MyApplication.getContextObject(), Constants.NICKNAME, user.getNickName());

        return sendDataPackage(DATA_TYPE_REGISTER, bys);
    }


    /**
     * 解析注册反馈数据
     *
     * @param bys 注册反馈信息数据包
     */
    public static void registFeedback(byte[] bys) {
        int flag = 0;
        // 判断注册状态
        Intent intent = new Intent();
        byte registState = bys[flag++];
        int userId = 0;
        // 注册成功
        if (registState == REGISTER_FEEDBACK_SUCCESS) {
            userId = packUserAddress(bys, flag);
        }
        EventBus.getDefault().post(new RegFeekBackEvent(userId, registState));
    }

    /**
     * 封装用户ID登录数据
     *
     * @param userId 用户ID
     * @return 组合好的数据
     * @Param password 密码
     */
    public static byte[] loginDataPackage(int userId, String password) {
        // 登录固定长度
        byte[] bys = new byte[LOGIN_USERID_FIXED_LENGTH];
        int flag = 0;
        try {
            // 用户ID
            flag = packUserAddress(userId, bys, flag);
            // 密码
            addSmallArrToBigArr(bys, password.getBytes("GBK"), flag);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendDataPackage(DATA_TYPE_LOGIN, bys);
    }

    /**
     * 封装手机号登录数据
     *
     * @param phone 手机号
     * @return 组合好的数据
     * @Param password 密码
     */
    public static byte[] loginDataPackage(String phone, String password) {
        // 注册固定长度
        byte[] bys = new byte[LOGIN_PHONE_FIXED_LENGTH];
        int flag = 0;
        try {
            // 手机号码
            byte[] phoneBytes = phone.getBytes("GBK");
            addSmallArrToBigArr(bys, phoneBytes, flag);
            flag += phoneBytes.length;
            // 密码
            addSmallArrToBigArr(bys, password.getBytes("GBK"), flag);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendDataPackage(DATA_TYPE_LOGIN, bys);
    }

    /**
     * 解析登录反馈数据
     *
     * @param bys 登录反馈信息数据包
     */
    public static void loginFeedback(byte[] bys) {
        Log.e(TAG,Arrays.toString(bys));
        int flag = 0;
        // 用于标志是手机号登录还是iD登录
        boolean isPhone = false;
        // 得到反馈数据,判断是手机号还是用户id
        // 用户ID登录反馈
        int userId = 0;
        if (bys.length == 4) {
            // 用户id
            userId = packUserAddress(bys, flag);
            flag += 3;
        } else {
            // 手机号登录反馈
            String phone = new String(bys, 0, 11);
            isPhone = true;
            flag += 11;
        }
        // 登录状态
        int loginState = bys[flag++];
        EventBus.getDefault().post(new LoginFeekBackEvent(userId, loginState));
    }

    /**
     * 封装修改密码数据包
     *
     * @param formerPwd 原来的密码
     * @param newPwd    新密码
     * @return 组合好的数据
     */
    public static byte[] alterPwdDataPackage(String formerPwd, String newPwd) {
        // 修改密码固定长度
        byte[] bys = new byte[ALTER_PWD_FIXED_LENGTH];
        int flag = 0;
        int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, 0);
        try {
            // 用户ID
            flag = packUserAddress(userId, bys, flag);
            // 原密码
            addSmallArrToBigArr(bys, formerPwd.getBytes("GBK"), flag);
            flag += formerPwd.length();
            // 新密码
            addSmallArrToBigArr(bys, newPwd.getBytes("GBK"), flag);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sendDataPackage(DATA_TYPE_ALTER_PWD, bys);
    }


    /**
     * 解析修改密码反馈数据
     *
     * @param bys 修改密码反馈信息数据包
     */
    public static void alterPwdFeedback(byte[] bys) {
        int flag = 0;
        Intent intent = new Intent();
        // 解析用户ID
        int userId = packUserAddress(bys, flag);
        flag += 3;
        // 解析修改密码状态
        byte state = bys[flag++];
        // 封装数据
        //EventBus.getDefault().post(new FeekBackEvent(userId,state));
    }


    /**
     * 封装添加好友数据包
     *
     * @param number 好友号码  userId/手机号/身份证号
     * @param index  号码索引类型
     * @return 封装好的数据包
     */
    public static byte[] addFriendsDataPackage(String number, byte index) {
        int flag = 0;
        // 根据索引类型确定字节数组的长度
        int length = 4;
        byte[] bys = null;
        // 本地获取id
        int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(),
                Constants.USER_ID, 0);
        byte[] numberBys = new byte[0];
        switch (index) {
            case ADD_FRIENDS_FOR_IDCARD:
                length += Constants.ID_CARD_LENGTH;
                bys = new byte[length];
                // 用户ID
                flag = packUserAddress(userId, bys, flag);
                // 索引类型
                bys[flag++] = ADD_FRIENDS_FOR_IDCARD;
                // 号码
                try {
                    numberBys = number.getBytes("GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                addSmallArrToBigArr(bys, numberBys, flag);
                flag += length;
                break;
            case ADD_FRIENDS_FOR_PHONE_NUMBER:
                length += Constants.PHONE_NUMBER_LENGTH;
                bys = new byte[length];
                // 手机号
                flag = packUserAddress(userId, bys, flag);
                // 索引类型
                bys[flag++] = ADD_FRIENDS_FOR_PHONE_NUMBER;
                // 号码
                try {
                    numberBys = number.getBytes("GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                addSmallArrToBigArr(bys, numberBys, flag);
                flag += length;
                break;
            case ADD_FRIENDS_FOR_USER_ID:
                length += Constants.USER_ID_LENGTH;
                bys = new byte[length];
                // 用户ID号
                flag = packUserAddress(userId, bys, flag);
                // 索引类型
                bys[flag++] = ADD_FRIENDS_FOR_USER_ID;
                // 号码
                int friendUserId = 0;
                try {
                    friendUserId = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                packUserAddress(friendUserId, bys, flag);
                break;
        }
        return sendDataPackage(DATA_TYPE_ADD_FRIENDS, bys);
    }

    /**
     * 解析添加好友反馈数据
     *
     * @param bys 添加好友反馈信息数据包
     */
    public static void addFriendsFeedback(byte[] bys) {
        int flag = 0;
        Intent intent = new Intent();
        // 自己的用户ID
        //int userId = packUserAddress(bys, flag);
        flag += 3;
        // 好友用户ID
        int userId = packUserAddress(bys, flag);
        flag += 3;
        // 判断添加好友状态
        byte operateState = bys[flag++];

        EventBus.getDefault().post(new AddFriendFeekBackEvent(userId, operateState));
    }

    /**
     * 封装朋友代付请求数据包
     *
     * @param friendId 代付的好友ID
     * @param money    代付金额
     * @return
     */
    public static byte[] friendPaidDataPackage(int friendId, int money) {
        byte[] bys = new byte[FRIEND_PAID_LENGTH];
        int flag = 0;
        // 自己的ID,本地直接获取
        int myUserId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, -1);
        flag = packUserAddress(myUserId, bys, flag);
        // 好友ID
        flag = packUserAddress(friendId, bys, flag);
        // 代付金额
        parseLen(money, bys, flag);
        return sendDataPackage(DATA_TYPE_FRIEND_PAID, bys);
    }

    /**
     * 解析时间估计通报
     *
     * @param bys
     */
    private static void timeNotification(byte[] bys) {
        /**
         * TODO 算出来的结果比实际结果小了1是为啥不知道
         */
        int flag = 0;
        long temp = 0;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        long second = 0;
        second += temp << 24;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        second += temp << 16;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        second += temp << 8;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        second += temp;
        SharedPrefUtil.putLong(MyApplication.getContextObject(), Constants.WAIT_TIME, second);
        EventBus.getDefault().post(new ReceiveTimeNotifyEvent());
    }

    /**
     * 解析欠费通报
     *
     * @param bys
     */
    private static void receiverArrearage(byte[] bys) {
        Log.e(TAG, "接收到欠费通报");
        byte by = bys[0];
        // 待定字节
        // 发广播,及时更新界面
        EventBus.getDefault().post(new ReceiverArrearageEvent());
    }

    /**
     * 解析朋友代付通报
     *
     * @param bys
     */
    private static void receiverFriendPaid(byte[] bys) {
        Log.e(TAG, "接收朋友代付充值通报");
        int flag = 0;
        // 自己的用户ID
        int myUserId = packUserAddress(bys, flag);
        flag += 3;
        int navUserid = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, -1);
        if (myUserId != navUserid) {
            return;
        }
        // 好友用户ID
        int friendUserId = packUserAddress(bys, flag);
        flag += 3;
        // 通过Id得到好友名称
        DatabaseDao dao = DatabaseDao.getInstance(MyApplication.getContextObject(), navUserid);
        String friendName = dao.queryFriendNameByUserId(friendUserId);

        // 金额
        int money = parseLen(bys, flag);

        EventBus.getDefault().post(new FriendPaidEvent(friendUserId,friendName,money));
    }


    /**
     * 解析北斗机状态
     *
     * @param bys 状态数据包
     * @return 北斗机状态bean对象
     */
    public static void beidouMachineState(byte[] bys) {
        Log.e("error", Arrays.toString(bys));
        BeidouMachineState instance = new BeidouMachineState();
        int flag = 0;
        // 硬件状态
        instance.setHardwareState(bys[flag++]);
        // 10个波束信号强度
        byte[] signalStrength = new byte[10];
        addBigArrToSmallArr(bys, signalStrength, flag);
        instance.setSignalStrength(signalStrength);
        flag += signalStrength.length;
        // ic卡数量
        byte icCount = bys[flag++];
        instance.setIcCardCount(icCount);
        // ic卡信息
        ArrayList<IcCardInfo> icInfos = new ArrayList<>();
        for (int i = 0; i < icCount; i++) {
            IcCardInfo icInfo = new IcCardInfo();
            // ic卡号
            icInfo.setIcNumber(packUserAddress(bys, flag));
            flag += 3;
            // 入站状态
            byte enterState = bys[flag++];
            String state = Integer.toBinaryString(enterState);
            StringBuffer sb = new StringBuffer(state);
            if (state.length() < 8) {
                for (int j = 0; j < 8 - state.length(); j++) {
                    sb.insert(0, '0');
                }
            }
            // 是否抑制
            icInfo.setControl(sb.charAt(0) == '0' ? false : true);
            // 是否静默
            icInfo.setQuiesce(sb.charAt(1) == '0' ? true : false);
            // 服务频度
            icInfo.setServiceFrequency(bys[flag++]);
            // 通信等级
            icInfo.setGrade(bys[flag++]);
            icInfos.add(icInfo);
        }
        instance.setIcInfos(icInfos);
//        Log.e("error", instance.toString());
//        for (int i = 0; i < icInfos.size(); i++) {
//            Log.e("error", icInfos.get(i).toString());
//        }
        // 更新Ic卡信息
        IcCardManager manager = new IcCardManager();
        manager.updateDatabase(icInfos);
        // 把硬件状态和信号信息记录到本地
        SharedPrefUtil.putInt(MyApplication.getContextObject(),
                Constants.HARDWARE_STATE, instance.getHardwareState());
        StringBuilder sb = new StringBuilder();
        byte[] strength = instance.getSignalStrength();
        for (int i = 0; i < strength.length; i++) {
            sb.append(strength[i]);
        }
        SharedPrefUtil.putString(MyApplication.getContextObject(),
                Constants.SIGNAL_STRENGTH, sb.toString());
        // 发广播,及时更新界面
        EventBus.getDefault().post(new BeiDouStatusEvent());
    }

    /**
     * 封装群操作请求数据包
     *
     * @param operateType 操作类型
     * @param groupNumber 群组号码
     * @param userIds     用户ID数组
     * @return
     */
    public static byte[] groupRequestDataPackage(byte operateType, int groupNumber, int[] userIds) {
        int flag = 0;
        // 群操作数据包的固定长度
        int bysLen = GROUP_OPERATE_FIXED_LENGTH;
        byte[] bys = null;
        // TODO 获取自己的userId
        int myUserId = 0;
        // 先判断操作类型
        switch (operateType) {
            // 建群操作
            case GROUP_OPERATE_BUILD_GROUP:
                // 固定长度+集合中的用户的userid*3
                bysLen += userIds.length * 3;
                bys = new byte[bysLen];
                // 先存入自己的userid
                flag = packUserAddress(myUserId, bys, flag);
                // 操作类型
                bys[flag++] = operateType;
                // 新增用户数量,恒填1
                bys[flag++] = (byte) 1;
                // 好友的用户ID
                for (int i = 0; i < userIds.length; i++) {
                    flag = packUserAddress(userIds[i], bys, flag);
                }
                break;
            // 加友操作
            case GROUP_OPERATE_ADD_MEMBER:
                // TODO 加友操作,群号放哪里?
                // 固定长度+集合中的用户的userid*3
                bysLen += userIds.length * 3;
                bys = new byte[bysLen];
                // 先存入自己的userid
                flag = packUserAddress(myUserId, bys, flag);
                // 操作类型
                bys[flag++] = operateType;
                // 新增用户数量,恒填1
                bys[flag++] = (byte) 1;
                // 好友的用户ID
                for (int i = 0; i < userIds.length; i++) {
                    flag = packUserAddress(userIds[i], bys, flag);
                }
                break;
            // 撤群操作
            case GROUP_OPERATE_EXIT_GROUP:
                // 固定长度+集合中的用户的userid*3
                bysLen += userIds.length * 3;
                bys = new byte[bysLen];
                // 先存入自己的userid
                flag = packUserAddress(myUserId, bys, flag);
                // 操作类型
                bys[flag++] = operateType;
                // 新增用户数量,恒填1
                bys[flag++] = (byte) 1;
                // 要撤的群组号码
                packUserAddress(userIds[0], bys, flag);
                break;
        }
        return sendDataPackage(DATA_TYPE_GROUP_OPERATE, bys);
    }

    /**
     * 解析群操作反馈数据
     *
     * @param bys 群操作反馈数据包
     */
    public static void groupRequestFeedback(byte[] bys) {
        int flag = 0;
        Intent intent = new Intent();
        // 自己的用户ID
        int userId = packUserAddress(bys, flag);
        flag += 3;
        // 判断群操作类型
        byte operateType = bys[flag++];
        // 获取新分配的群号
        int groupNumber = packUserAddress(bys, flag);
        flag += 3;
        // 操作状态
        byte operateState = bys[flag];
        // 封装数据
        intent.putExtra("operateType", operateType);
        intent.putExtra("operateState", operateState);
        intent.putExtra("userId", userId);
        intent.putExtra("groupNumber", groupNumber);
        intent.setAction(Constants.ACTION_GROUP_OPERATE_FEEKBACK);
        // 发送广播
        MyApplication.getContextObject().sendBroadcast(intent);
    }

    /**
     * 封装自定义信息数据包
     *
     * @param type        信息类型:短信/邮件
     * @param destination 目的地地址
     * @param content     信息内容
     * @return 封装好的数据包
     */
    public static byte[] userDefinedDataPackage(byte type, String destination, String content) {
        int flag = 0;
        byte[] destinationBys = null;
        byte[] contentBys = null;
        try {
            destinationBys = destination.getBytes("GBK");
            contentBys = content.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 长度为目的地长度 + 内容长度 + 固定长度
        int len = destinationBys.length + contentBys.length + USER_DEFINED_FIXED_LENGTH;
        byte[] bys = new byte[len];
        // 信息类型
        bys[flag++] = type;
        // 目的地地址字节长度
        bys[flag++] = (byte) destinationBys.length;
        // 目的地地址字符串
        addSmallArrToBigArr(bys, destinationBys, flag);
        flag += destinationBys.length;
        // 信息内容长度
        bys[flag++] = (byte) contentBys.length;
        // 信息内容
        addSmallArrToBigArr(bys, contentBys, flag);
        return sendDataPackage(DATA_TYPE_USER_DEFINED, bys);
    }

    /**
     * 解析收到的手机短信
     *
     * @param bys 文字消息反馈信息数据包
     */
    public static void receiverUserDefined(byte[] bys) {
        Log.e(TAG, "解析短信消息");
        int flag = 0;
        byte type = bys[flag++];
        // 判断是短信消息
        if (type == USER_DEFINED_TYPE_SMS) {
            // 目的地地址长度
            byte addressLen = bys[flag++];
            // 目的地地址内容
            byte[] address = new byte[addressLen];
            addBigArrToSmallArr(bys, address, flag);
            flag += addressLen;
            // 信息长度
            byte contentLen = bys[flag++];
            // 信息内容
            byte[] content = new byte[contentLen];
            addBigArrToSmallArr(bys, content, flag);
            flag += contentLen;
            // 封装数据
            try {
                EventBus.getDefault().post(
                        new ReceiveSMSEvent(
                                new String(address, "GBK"),new String(content, "GBK")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 封装文字消息数据
     *
     * @param msg        消息信息
     * @param isEncrypt  是否加密
     * @param isGroupMsg 单播还是组播
     * @return 组合好的数据
     */
    public static byte[] textMsgDataPackage(CommonMessage msg, boolean isEncrypt, boolean isGroupMsg) {
        Log.e(TAG, "封装文字消息");
        byte[] contents = new byte[0];
        try {
            contents = msg.getContent().getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 数据包固定长度 + 文字消息包固定长度 + 内容长度
        byte[] bys = new byte[TEXT_MESSAGE_LENGTH + contents.length];
        int flag = 0;
        // 文字消息序号 用于与反馈关联起来
        flag = packUserAddress(msg.get_id(), bys, flag);
        // 用户源ID
        int myUserId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, 0);
//        int myUserId = 230626;
        flag = packUserAddress(myUserId, bys, flag);
        // 信息bit长度
        flag = parseLen(contents.length * 8, bys, flag);
        // 加密标识 0 明文   1密文
        byte one = 1;
        byte zero = 0;
        bys[flag++] = isEncrypt ? one : zero;
        // 目的地用户id
        flag = packUserAddress(msg.getSenderNumber(), bys, flag);
        // 发送方式  单播/组播
        bys[flag++] = isGroupMsg ? one : zero;
        // 消息内容
        addSmallArrToBigArr(bys, contents, flag);
        MsgNumberFileUtils.writeToTxt(CommUtil.getDate() + "\t" + "发送" + "\t" + msg.get_id() + "\r");
        // 类别做一下修改
        // 判断当前有没有网络,修改类别的前两个bit
        boolean netConnect = NetWorkUtil.isNetConnect(NetWorkUtil.getNetWorkState(MyApplication.getContextObject()));
        byte dataType = 0;
        if (netConnect) {
            dataType = (byte) ((DATA_TYPE_TEXT_MSG >> 2 << 7) + DATA_TYPE_TEXT_MSG);
        } else {
            dataType = DATA_TYPE_TEXT_MSG;
        }

        return sendDataPackage(dataType, bys);
    }

    /**
     * 解析文字消息反馈数据
     *
     * @param bys 文字消息反馈信息数据包
     */
    public static void textMsgFeedback(byte[] bys) {
        int flag = 0;
        // 消息序号
        int messageNumber = packUserAddress(bys, flag);
        flag += 3;
        // 发送状态
        byte sendState = bys[flag++];
        // 发送事件
        EventBus.getDefault().post(new TextMsgFeekBackEvent(messageNumber, sendState));
    }

    /**
     * 解析接收到的文字消息
     *
     * @param bys 文字消息信息数据包
     */
    public static void receiverTextMsg(int from, byte[] bys) {
        Log.e(TAG, "receiverTextmsg");
        ReceiverMessage msg = new ReceiverMessage();
        // 消息来自哪里
        msg.setFrom(from);
        // 实际内容的数组   长度为总长度-文字消息数据包固定长度
        byte[] content = new byte[bys.length - TEXT_MESSAGE_LENGTH];
        int flag = 0;
        // 文字消息序号
        msg.setTextMsgNumber(packUserAddress(bys, flag));
        flag += 3;
        // 源用户ID
        msg.setSenderUserId(packUserAddress(bys, flag));
        flag += 3;
        // 信息bit长度
        msg.setContentBitLen(parseLen(bys, flag));
        flag += 2;
        // 加密标识
        msg.setEncrypt(bys[flag++] == 0 ? false : true);
        // 目的地用户ID
        int myUserId = packUserAddress(bys, flag);
        int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, -1);
        if (myUserId != userId) {
            return;
        }
        flag += 3;
        // 发送方式
        msg.setGroup(bys[flag++] == 0 ? false : true);
        // 实际内容
        addBigArrToSmallArr(bys, content, flag);
        try {
            msg.setContent(new String(content, "GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 封装,发送数据
        //MsgNumberFileUtils.writeToTxt(CommUtil.getDate() + "\t" + "接收" + "\t" + msg.getTextMsgNumber() + "\r");
        EventBus.getDefault().post(new ReceiveNewTextMsgEvent(msg));

    }


    /**
     * 封装文字消息接收成功反馈
     *
     * @param msgNumber 文字消息序号
     */
    public static byte[] receiverTextMsgFeedback(int msgNumber) {
        byte[] bys = new byte[4];
        int flag = 0;
        // 文字消息序号
        flag = packUserAddress(msgNumber, bys, flag);
        // 接收状态(只要发送反馈都是接收成功的)
        bys[3] = TEXT_MSG_SEND_FEEDBACK_SUCCESS;

        return sendDataPackage(DATA_TYPE_RECEIVER_TEXT_MSG, bys);
    }

    /**
     * 封装语音消息数据
     *
     * @param msg        消息类数据
     * @param isEncrypt  是否加密
     * @param isGroupMsg 单播还是组播
     * @return 组合好的数据
     */
    public static byte[] voiceMsgDataPackage(CommonMessage msg, boolean isEncrypt, boolean isGroupMsg) {
        if (msg == null) {
            return new byte[0];
        }
        byte def = 1;
        // 得到raw文件名称
        String rawName = msg.getContent();
        String spxPath = CommUtil.getSpxPath(rawName);
//        Log.e("error", spxPath + "--=-=-=-=-=");
        File file = new File(spxPath);
        // 把文件写到内存流中,得到长度
        FileInputStream fis = null;
        FileOutputStream fos = null;
        int len = 0;
        byte[] buffer = new byte[2048];
        int read = 0;
        ArrayList<byte[]> list = new ArrayList<>();
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(spxPath.substring(0, spxPath.lastIndexOf('.')) + ".abc");
            while ((read = fis.read(buffer)) != -1) {
//                Log.e("error", Arrays.toString(buffer));
//                Log.e("error", "----------------------");
                fos.write(buffer, 0, read);
                byte[] temp = new byte[read];
                for (int i = 0; i < read; i++) {
                    temp[i] = buffer[i];
                }
                list.add(temp);
                len += temp.length;
                fos.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("error", "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("error", "IOException");
        }
        byte[] content = new byte[len];
        int flag = 0;
        byte[] bys = null;
        try {
            //这里使用内容长度加固定长度
            bys = new byte[content.length + 22];
            // 指令
            byte[] cmd = "$TXSQ".getBytes("GBK");
            for (int i = 0; i < cmd.length; i++) {
                bys[i] = cmd[i];
            }
            flag += 5;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 长度
        flag = parseLen(bys.length, bys, flag);

        // 用户ID
        int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, 0);
        flag = packUserAddress(userId, bys, flag);

        // 信息类别
        bys[flag++] = 0x46;

        // TODO 中心号码为0
        flag = packUserAddress(0, bys, flag);

        // 电文长度  内容长度
        flag = parseLen(content.length, bys, flag);

        // 是否应答
        bys[flag++] = def;

        // 业务标识(用来存秒数)
        bys[flag++] = (byte) msg.getSecond();

        // 目的地ic卡号,由外界传入
        flag = packUserAddress(msg.getSenderNumber(), bys, flag);

        // 电文内容
        for (int i = 0; i < list.size(); i++) {
            addSmallArrToBigArr(bys, list.get(i), flag);
            flag += list.get(i).length;
        }

        // 校验和,前面所有数据按位异或
        bys[bys.length - 1] = def;
//        Log.e("error", Arrays.toString(bys));
        return bys;
    }

    /**
     * 解析接收到的语音消息数据
     *
     * @param bys 接收到的数据包
     * @return 组合好的数据
     */
    public static void parseVoiceMsgDataPackage(byte[] bys) {

        Log.e("error", Arrays.toString(bys));
        CommonMessage msg = new CommonMessage();
        int flag = 0;
        byte def = 1;
        // 得到raw文件名称
//        flag = 5;
//        int len = parseLen(bys, flag);
        // 解析业务标识,用来存语音秒数的
        flag = 17;
        msg.setSecond(bys[flag++]);
        // 开始解析对方地址
        int ads = packUserAddress(bys, flag);
        msg.setSenderNumber(ads);
//        msg.setFrom(NetWorkUtil.getFromType());
        msg.setTime(CommUtil.getDate());
        msg.setType(Constants.MESSAGE_TYPE_VOICE);
        msg.setStatus(Constants.MESSAGE_STATE_RECEIVER);
        msg.setIsRead(Constants.MESSAGE_NO_READ);
        // 从数据库中查找号码对应的名字
        int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, -1);
        DatabaseDao dao = DatabaseDao.getInstance(MyApplication.getContextObject(), userId);
        String name = dao.queryFriendNameByUserId(msg.getSenderNumber());
        msg.setSenderName(name);

        flag = 21;
        byte[] content = new byte[bys.length - 22];
        addBigArrToSmallArr(bys, content, flag);

//        Log.e("error", "---" + Arrays.toString(content));
//        Log.e("error", bys.length + "---" + content.length);
        // 写到本地文件
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BeiDouCommunication/voiceFiles/";
        String fileName = System.currentTimeMillis() + "";
        File file = new File(dir, fileName + ".spx");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("error", "解压缩" + msg.getContent());
        File wavFile = new File(dir, fileName + ".wav");
        //解压缩
        AudioFileUtils.spx2Wav(file.getAbsolutePath(), wavFile.getAbsolutePath(), 160);

        msg.setContent(wavFile.getAbsolutePath());

        // 发送广播通知界面
        Intent intent = new Intent(Constants.RECEIVE_VOICE_MSG_ACTION);
        Log.e("error", msg.toString());
        Log.e("error", "---------------------------------");
        intent.putExtra("voicemsg", msg);
        MyApplication.getContextObject().sendBroadcast(intent);
        //EventBus.getDefault().post();
    }

    /**
     * 封装用户地址,地址to字节数组
     *
     * @param ads
     * @param bys
     * @param flag
     * @return
     */

    public static int packUserAddress(int ads, byte[] bys, int flag) {
        bys[flag++] = (byte) (ads >> 16);
        bys[flag++] = (byte) (ads << 16 >> 24);
        bys[flag++] = (byte) (ads << 24 >> 24);
        return flag;
    }

    /**
     * 解析用户地址(3字节)
     *
     * @param bys
     * @param flag
     * @return ads  返回地址
     */
    public static int packUserAddress(byte[] bys, int flag) {
        int temp = 0;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        int ads = 0;
        ads += temp << 16;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        ads += temp << 8;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        ads += temp;
        return ads;
    }

    /**
     * 封装长度,长度to字节数组
     *
     * @param bys  字节数组
     * @param flag 索引标记
     * @return flag的值
     */
    public static int parseLen(int len, byte[] bys, int flag) {
        bys[flag++] = (byte) (len >> 8);
        bys[flag++] = (byte) (len << 8 >> 8);
        return flag;
    }

    /**
     * 解析长度(2字节),字节ToInt
     *
     * @param bys
     * @param flag
     * @return
     */
    public static int parseLen(byte[] bys, int flag) {
        int temp = 0;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        int len = 0;
        len += temp;
        if (bys[flag] < 0) {
            temp = bys[flag++] + 256;
        } else {
            temp = bys[flag++];
        }
        len += temp;
        return len;
    }

    /**
     * 把小字节数组从指定位置开始放到大字节数组中
     *
     * @param big   大字节数组
     * @param small 小字节数组
     * @param index 索引
     */
    public static void addSmallArrToBigArr(byte[] big, byte[] small, int index) {
        for (int i = 0; i < small.length; i++) {
            if (index + i < big.length) {
                big[index + i] = small[i];
            }
        }
    }

    /**
     * 把大字节数组从指定位置开始放到小字节数组中
     *
     * @param big   大字节数组
     * @param small 小字节数组
     * @param index 索引
     */
    public static void addBigArrToSmallArr(byte[] big, byte[] small, int index) {
        for (int i = 0; i < small.length; i++) {
            //Log.e("error", "small.length:" + small.length + ",big.length:" + big.length + ",index:" + index + ",i:" + i);
            //防止索引越界，判断一下
            if (index + i <big.length) {
                small[i] = big[index + i];
            }
        }
    }

}
