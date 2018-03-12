package com.zhbd.beidoucommunication.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.ListView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.IcInfoAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.IcCardInfo;
import com.zhbd.beidoucommunication.event.BeiDouStatusEvent;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * TODO 现在只在消息界面加入了,周一去测试再调整
 */
public class ShowIcInfoActivity extends TitlebarActivity {

    @Bind(R.id.listview_ic_info)
    ListView mListView;

    ArrayList<IcCardInfo> mList = new ArrayList<>();
    private IcInfoAdapter mAdapter;
    private DatabaseDao dao;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revBeidouStatus(BeiDouStatusEvent event) {
        // 设置ic信息
        setIcInfo();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_show_ic_info);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);

        setTitleText(R.string.ic_info);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(R.string.main_button_mine, true);
        setRightIcon(0, false);
        setRightText(0, false);
    }

    private void initData() {
        int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(this, userId);

        mAdapter = new IcInfoAdapter(this, mList);
        mListView.setAdapter(mAdapter);

        // 从数据库中查找数据, 添加到集合
        setIcInfo();

        // 注册广播接收者
        EventBus.getDefault().register(this);
    }

    public void setIcInfo() {
        mList.clear();
        ArrayList<IcCardInfo> icCardInfos = dao.queryIcInfo();
        mList.addAll(icCardInfos);

        mAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
