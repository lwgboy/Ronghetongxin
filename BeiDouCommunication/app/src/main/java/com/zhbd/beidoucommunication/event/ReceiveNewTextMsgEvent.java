package com.zhbd.beidoucommunication.event;

import com.zhbd.beidoucommunication.domain.ReceiverMessage;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class ReceiveNewTextMsgEvent {
    private ReceiverMessage msg;

    public ReceiveNewTextMsgEvent(ReceiverMessage msg) {
        this.msg = msg;
    }

    public ReceiverMessage getMsg() {
        return msg;
    }

    public void setMsg(ReceiverMessage msg) {
        this.msg = msg;
    }
}
