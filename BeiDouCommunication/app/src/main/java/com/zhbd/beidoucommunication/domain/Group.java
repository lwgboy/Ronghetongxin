package com.zhbd.beidoucommunication.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhangyaru on 2017/9/7.
 */

public class Group implements Serializable {
    /**
     * id
     */
    private int _id;
    /**
     * 群组号码
     */
    private int groupId;
    /**
     * 群组名称
     */
    private String name;
    /**
     * 群成员信息(待定)
     */
    private ArrayList<Friend> memberInfo;
    /**
     * 是否群主,数据库中存储为int类型,0表示是群主,1表示不是群主
     */
    private boolean isOwner;
    /**
     * 创建时间
     */
    private String buildTime;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Friend> getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(ArrayList<Friend> memberInfo) {
        this.memberInfo = memberInfo;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }
}
