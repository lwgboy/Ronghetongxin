package com.zhbd.beidoucommunication.domain;

/**
 * Created by zhangyaru on 2017/9/21.
 */

public class IcCardInfo {
    /**
     * id
     */
    private int _id;
    /**
     * 卡号
     */
    private int icNumber;
    /**
     * 是否抑制
     */
    private boolean isControl;
    /**
     * 是否静默
     */
    private boolean isQuiesce;
    /**
     * 服务频度
     */
    private byte serviceFrequency;
    /**
     * 通信等级
     */
    private byte grade;

    /**
     * 上次发送信息的时间
     */
    private String lastSendTime;

    public String getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(String lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(int icNumber) {
        this.icNumber = icNumber;
    }

    public boolean isControl() {
        return isControl;
    }

    public void setControl(boolean control) {
        isControl = control;
    }

    public boolean isQuiesce() {
        return isQuiesce;
    }

    public void setQuiesce(boolean quiesce) {
        isQuiesce = quiesce;
    }

    public byte getServiceFrequency() {
        return serviceFrequency;
    }

    public void setServiceFrequency(byte serviceFrequency) {
        this.serviceFrequency = serviceFrequency;
    }

    public byte getGrade() {
        return grade;
    }

    public void setGrade(byte grade) {
        this.grade = grade;
    }
}
