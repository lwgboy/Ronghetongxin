package com.zhbd.beidoucommunication.domain;

import java.io.Serializable;

/**
 * Created by zhangyaru on 2017/9/5.
 */

public class Friend implements Serializable {
    /**
     * id
     */
    private int _id;
    /**
     * 用户id
     */
    private int userId;
    /**
     * 添加的类型
     */
    private int addType;

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }

    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * SIM卡号
     */
    private String simNumber;
    /**
     * 名字首字符
     * */
    private String letter;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(String simNumber) {
        this.simNumber = simNumber;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "_id=" + _id +
                ", userId=" + userId +
                ", addType=" + addType +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idCard='" + idCard + '\'' +
                ", simNumber='" + simNumber + '\'' +
                ", letter='" + letter + '\'' +
                '}';
    }
}
