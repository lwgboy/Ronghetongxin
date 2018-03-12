package com.zhbd.beidoucommunication.domain;

/**
 * Created by zhangyaru on 2017/10/23.
 */

public class Staff {
    private String realName;
    private String job;
    private int picRes;

    public int getPicRes() {
        return picRes;
    }

    public void setPicRes(int picRes) {
        this.picRes = picRes;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
