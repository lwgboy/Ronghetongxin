package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class ReceiveEmailEvent {
    private String address;
    private String content;

    public ReceiveEmailEvent(String address, String content) {
        this.address = address;
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
