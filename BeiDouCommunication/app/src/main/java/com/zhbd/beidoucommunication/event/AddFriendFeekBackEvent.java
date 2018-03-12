package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class AddFriendFeekBackEvent {
    private int friendUserId;
    private int status;

    public AddFriendFeekBackEvent(int userId, int status) {
        this.friendUserId = userId;
        this.status = status;
    }

    public int getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(int friendUserId) {
        this.friendUserId = friendUserId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
