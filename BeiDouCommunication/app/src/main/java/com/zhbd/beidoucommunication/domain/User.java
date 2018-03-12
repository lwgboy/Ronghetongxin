package com.zhbd.beidoucommunication.domain;

/**
 * Created by zhangyaru on 2017/8/23.
 */

public class User {
    /**
     * 用户唯一标示，id
     */
    private int userId;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户登录密码
     */
    private String passWord;
    /**
     * 用户手机号
     */
    private String phoneNumber;
    /**
     * 用户身份证号码
     */
    private String idCardNumber = "";

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                '}';
    }
}
