package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class CanSendMsgEvent {
    private boolean canSent;

    public CanSendMsgEvent(boolean canSent) {
        this.canSent = canSent;
    }

    public boolean isCanSent() {
        return canSent;
    }

    public void setCanSent(boolean canSent) {
        this.canSent = canSent;
    }
}
