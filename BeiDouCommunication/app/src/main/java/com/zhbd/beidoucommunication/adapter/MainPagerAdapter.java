package com.zhbd.beidoucommunication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zhbd.beidoucommunication.base.BaseFragment;

import java.util.List;

/**
 * Created by zhangyaru on 2017/8/30.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mList;

    public MainPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
