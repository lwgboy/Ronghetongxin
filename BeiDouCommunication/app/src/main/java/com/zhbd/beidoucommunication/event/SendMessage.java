package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

/**
 * 通知服务发送消息的事件
 */
public class SendMessage {
    private byte[] content;

    public SendMessage(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
