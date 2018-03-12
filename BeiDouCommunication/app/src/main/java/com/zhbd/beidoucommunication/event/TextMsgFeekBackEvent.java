package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class TextMsgFeekBackEvent {
    private int msgNumber;
    private int status;

    public TextMsgFeekBackEvent(int userId, int status) {
        this.msgNumber = userId;
        this.status = status;
    }

    public int getMsgNumber() {
        return msgNumber;
    }

    public void setMsgNumber(int msgNumber) {
        this.msgNumber = msgNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
