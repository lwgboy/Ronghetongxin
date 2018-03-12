package com.zhbd.beidoucommunication.domain;

/**
 * Created by zhangyaru on 2017/9/4.
 */

public class SmsMessage extends BaseMessage {
    /**
     * 发送者手机号码
     */
    private String phoneNumber;
    /**
     * 发送者姓名
     */
    private String senderName;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "_id=" + _id +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", from=" + from +
                ", status=" + status +
                ", type=" + type +
                ", isRead=" + isRead +
                "SmsMessage{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", senderName='" + senderName + '\'' +
                '}';
    }
}
