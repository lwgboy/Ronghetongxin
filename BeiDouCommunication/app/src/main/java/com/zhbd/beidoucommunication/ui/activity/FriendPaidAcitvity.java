package com.zhbd.beidoucommunication.ui.activity;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.os.Bundle;

import android.widget.ListView;
import android.widget.TextView;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.FriendsPaidAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.utils.CharacterParser;
import com.zhbd.beidoucommunication.utils.PinyinComparator;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendPaidAcitvity extends TitlebarActivity {
    @Bind(R.id.sidrbar)
    SideBar sidrbar;

    @Bind(R.id.qucik_index_listview)
    ListView mListview;

    private ArrayList<Friend> mList = new ArrayList<>();
    private DatabaseDao dao;
    private FriendsPaidAdapter mAdapter;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private double price;
    private int money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_friend_paid);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        setTitleText(R.string.choice_friend);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(0, false);
        setRightIcon(0, false);
        setRightText(0, false);
    }

    private void initData() {
        price = getIntent().getDoubleExtra("price", 0);
        money = getIntent().getIntExtra("money", 0);

        int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(this, userId);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        //设置右侧触摸监听
        sidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListview.setSelection(position);
                }

            }
        });

        // 填充数据集合
        mList = filledData();

        mAdapter = new FriendsPaidAdapter(this, mList, money);
        mListview.setAdapter(mAdapter);

        MyApplication.addToQueue(this);
    }

    /**
     * 为ListView填充数据
     */
    private ArrayList<Friend> filledData() {
        mList.clear();
        //从数据库中查找联系人信息
        mList.addAll(dao.queryFriensInfo());
        //setData();
        for (int i = 0; i < mList.size(); i++) {
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(mList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                mList.get(i).setLetter(sortString.toUpperCase());
            } else {
                mList.get(i).setLetter("#");
            }
        }
        // 根据a-z进行排序源数据
        Collections.sort(mList, pinyinComparator);
        return mList;
    }
}
