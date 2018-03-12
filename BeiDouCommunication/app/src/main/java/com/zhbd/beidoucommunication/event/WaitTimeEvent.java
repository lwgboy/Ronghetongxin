package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class WaitTimeEvent {
    private long waitTime;

    public WaitTimeEvent(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }
}
