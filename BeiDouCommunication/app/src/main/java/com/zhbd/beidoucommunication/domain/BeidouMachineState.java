package com.zhbd.beidoucommunication.domain;


import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhangyaru on 2017/9/18.
 */

public class BeidouMachineState {
    // 硬件状态   0 正常,1 异常
    private byte hardwareState;
    // 10个波束信号强度
    // 00: <-158dBW
    // 01: -156~-157dBW
    // 02: -154~-155dBW
    // 03: -152~-153dBW
    // 04: >-152dBW
    private byte[] signalStrength;
    // ic卡数量
    private byte icCardCount;
    // ic卡信息
    private ArrayList<IcCardInfo> icInfos;

    public byte getHardwareState() {
        return hardwareState;
    }

    public void setHardwareState(byte hardwareState) {
        this.hardwareState = hardwareState;
    }

    public byte[] getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(byte[] signalStrength) {
        this.signalStrength = signalStrength;
    }

    public byte getIcCardCount() {
        return icCardCount;
    }

    public void setIcCardCount(byte icCardCount) {
        this.icCardCount = icCardCount;
    }

    public ArrayList<IcCardInfo> getIcInfos() {
        return icInfos;
    }

    public void setIcInfos(ArrayList<IcCardInfo> icInfos) {
        this.icInfos = icInfos;
    }

    @Override
    public String toString() {
        return "BeidouMachineState{" +
                "hardwareState=" + hardwareState +
                ", signalStrength=" + Arrays.toString(signalStrength) +
                ", icCardCount=" + icCardCount +
                ", icInfos=";
    }
}
