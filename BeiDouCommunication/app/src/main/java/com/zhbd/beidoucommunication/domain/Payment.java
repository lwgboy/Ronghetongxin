package com.zhbd.beidoucommunication.domain;

/**
 * Created by zhangyaru on 2017/10/14.
 */

public class Payment {
    // 是否选中
    private boolean isSelect;

    // 支付方式名称
    private String designation;

    // 图标资源
    private int imgRes;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }
}
