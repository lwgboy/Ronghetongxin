package com.zhbd.beidoucommunication.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.BuildGroupAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CharacterParser;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.PinyinComparator;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.view.SideBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BuildGroupActivity extends TitlebarActivity {

    @Bind(R.id.et_group_name)
    EditText mEtGroupName;

    @Bind(R.id.sidrbar)
    SideBar sidrbar;

    @Bind(R.id.qucik_index_listview)
    ListView mListview;

    @Bind(R.id.tv_pop)
    TextView tv_pop;

    private ArrayList<Friend> mList = new ArrayList<>();
    private DatabaseDao dao;
    private BuildGroupAdapter mAdapter;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private View view;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 判断是adapter发送来的跟新数据的广播
            if (Constants.ACTION_GROUP_OPERATE_FEEKBACK.equals(intent.getAction())) {
                // TODO
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_build_group);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        setTitleText(R.string.build_group);
        setRightIcon(R.drawable.next_arrow_white, true);
        setLeftIcon(R.drawable.back_arrow, true);
        setRightText(R.string.confirm, true);
        setLeftText(R.string.cancel_text, true);
    }

    private void initData() {
        // 注册广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_GROUP_OPERATE_FEEKBACK);
        registerReceiver(mReceiver, filter);

        // 初始化数据库操作类
        int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(this, userId);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sidrbar.setTextView(tv_pop);

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

        mAdapter = new BuildGroupAdapter(this, mList);
        mListview.setAdapter(mAdapter);

    }
    /**
     * 为ListView填充数据
     */
    private ArrayList<Friend> filledData() {
        mList.clear();
        //从数据库中查找联系人信息
        mList = dao.queryFriensInfo();
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

    @Override
    protected void clickRight(Activity activity) {
        super.clickRight(activity);
        String name = mEtGroupName.getText().toString();
        // 判断群名称不能为空
        if (CommUtil.isEmpty(name)) {
            ToastUtils.showToast(this, R.string.group_name_not_none);
            return;
        }
        int count = 0;
        // 获取哪些条目是选中的
        boolean[] state = mAdapter.getState();
        for (int i = 0; i < state.length; i++) {
            if (state[i]) {
                count++;
            }
        }
        // 判断群组成员人数,单次最多20个人
        if (count > Constants.GROUP_MAX_COUNT) {
            ToastUtils.showToast(this, R.string.so_many_people);
            return;
        }
        // 判断群组成员人数,如果没有选中任何
        if (count == 0) {
            ToastUtils.showToast(this, R.string.as_least_selected_two);
            return;
        }
        int[] userIds = new int[count];
        int index = 0;
        // 把选中的用户号码添加到一个数组中
        for (int i = 0; i < state.length; i++) {
            if (state[i]) {
                userIds[index++] = mList.get(i).getUserId();
            }
        }
        // 封装数据,请求网络
        byte[] result = DataProcessingUtil.groupRequestDataPackage(
                DataProcessingUtil.DATA_TYPE_GROUP_OPERATE, 0, userIds);
        EventBus.getDefault().post(new SendMessage(result));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dao != null) {
            dao.close();
        }
        unregisterReceiver(mReceiver);
    }
}
