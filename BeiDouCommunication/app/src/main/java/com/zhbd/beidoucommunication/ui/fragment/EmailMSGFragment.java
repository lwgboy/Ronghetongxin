package com.zhbd.beidoucommunication.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.EmailMessageAdapter;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.EmailMessage;
import com.zhbd.beidoucommunication.ui.activity.ShowEmailActivity;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.view.RefreshLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 普通消息
 * Created by zhangyaru on 2017/9/4.
 */

public class EmailMSGFragment extends Fragment implements
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    // 下拉刷新控件
    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;

    @Bind(R.id.side_pull_listview)
    ListView mSidepullListView;

    EmailMessageAdapter mAdapter;
    private ArrayList<EmailMessage> mList;


    /**
     * 数据库操作类
     */
    DatabaseDao dao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_msg, null);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        // 设置下拉刷新的进度条颜色
        mRefreshLayout.setColorScheme(
                new int[]{android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_red_light});
        mList = new ArrayList<>();
        int userId = SharedPrefUtil.getInt(getContext(), Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(getContext(), userId);
        // 去重复查找,找到最新的消息
        mList = dao.queryEmail();
        // 设置适配器
        mAdapter = new EmailMessageAdapter(mList, getActivity());
        mSidepullListView.setAdapter(mAdapter);

        // 设置条目点击监听
        mSidepullListView.setOnItemClickListener(this);

        // 设置刷新监听,这里不需要加载更多
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ShowEmailActivity.class);
        EmailMessage email = mList.get(position);
        intent.putExtra("address", email.getAddress());
        intent.putExtra("content", email.getContent());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据
                mList.clear();
                ArrayList<EmailMessage> messages = dao.queryEmail();
                mList.addAll(messages);
                //setData();
                refresh();

                ToastUtils.showToast(getActivity(), getResources().getString(R.string.update_success));
                // 更新完后调用该方法结束刷新
                mRefreshLayout.setRefreshing(false);
            }
        }, 1000);

    }

    private void refresh() {
        mList.clear();
        mList.addAll(dao.queryEmail());
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }
}
