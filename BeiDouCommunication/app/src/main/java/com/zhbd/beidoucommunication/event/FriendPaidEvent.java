package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class FriendPaidEvent {
    private int friendUserId;
    private String friendName;
    private int money;

    public FriendPaidEvent(int friendUserId, String friendName, int money) {
        this.friendUserId = friendUserId;
        this.friendName = friendName;
        this.money = money;
    }

    public int getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(int friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
