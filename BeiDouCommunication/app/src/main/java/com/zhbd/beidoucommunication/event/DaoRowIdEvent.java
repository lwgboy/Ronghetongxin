package com.zhbd.beidoucommunication.event;

/**
 * Created by zhangyaru on 2017/10/27.
 */

public class DaoRowIdEvent {
    private int rowid;

    public DaoRowIdEvent(int rowid) {
        this.rowid = rowid;
    }

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }
}
