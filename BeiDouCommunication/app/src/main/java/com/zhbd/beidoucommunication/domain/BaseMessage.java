package com.zhbd.beidoucommunication.domain;

import java.io.Serializable;

/**
 * Created by zhangyaru on 2017/9/4.
 */

public class BaseMessage implements Serializable {
    /**
     * 消息id
     */
    protected int _id;
    /**
     * 时间
     */
    protected String time;
    /**
     * 消息内容
     */
    protected String content;
    /**
     * 消息来源
     */
    protected int from;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * 收发标志  0表发送成功,1表发送失败,2表接收
     */
    protected int status;
    /**
     * 消息类型 0表文本,1表语音
     */
    protected int type;
    /**
     * 是否读取标志 0表未读,1表已读
     */
    protected int isRead;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
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
                '}';
    }
}
