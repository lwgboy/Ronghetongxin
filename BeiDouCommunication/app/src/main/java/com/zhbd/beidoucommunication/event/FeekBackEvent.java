package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class FeekBackEvent {
    private int userId;
    private int status;

    public FeekBackEvent(int userId, int status) {
        this.userId = userId;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
