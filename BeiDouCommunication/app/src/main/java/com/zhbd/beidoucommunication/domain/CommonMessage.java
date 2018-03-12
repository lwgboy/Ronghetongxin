package com.zhbd.beidoucommunication.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhbd.beidoucommunication.base.BaseFragment;

import java.io.Serializable;

/**
 * Created by zhangyaru on 2017/9/4.
 */

public class CommonMessage extends BaseMessage {
    /**
     * 发送者ic号码
     */
    private int senderNumber;
    /**
     * 发送者备注
     */
    private String senderName;
    /**
     * 语音消息的秒数
     */
    private int second;


    public int getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(int senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public String toString() {
        String s = "BaseMessage{" +
                "_id=" + _id +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", from=" + from +
                ", status=" + status +
                ", type=" + type +
                ", isRead=" + isRead +
                '}' +
                "CommonMessage{" +
                "senderNumber=" + senderNumber +
                ", senderName='" + senderName + '\'' +
                ", second=" + second +
                '}';
        return s;
    }
}
