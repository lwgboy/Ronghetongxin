package com.zhbd.beidoucommunication.utils;

import com.zhbd.beidoucommunication.domain.Friend;

import java.util.Comparator;

/**
 * Created by zhangyaru on 2017/8/25.
 */

public class PinyinComparator implements Comparator<Friend> {

    public int compare(Friend o1, Friend o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        if (o2.getLetter().equals("#")) {
            return -1;
        } else if (o1.getLetter().equals("#")) {
            return 1;
        } else {
            return o1.getLetter().compareTo(o2.getLetter());
        }
    }
}
