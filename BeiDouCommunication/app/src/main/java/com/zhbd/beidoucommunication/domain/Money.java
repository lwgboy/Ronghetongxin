package com.zhbd.beidoucommunication.domain;

import java.io.Serializable;

/**
 * Created by zhangyaru on 2017/10/14.
 */

public class Money implements Serializable {
    private int money;
    private double price;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
