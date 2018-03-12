package com.zhbd.beidoucommunication.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.CommonMessageAdapter;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.domain.ReceiverMessage;
import com.zhbd.beidoucommunication.event.ReceiveNewTextMsgEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.ui.activity.ChatMessageActivity;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.view.RefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 普通消息
 * Created by zhangyaru on 2017/9/4.
 */

public class CommonMSGFragment extends Fragment implements
        AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = "CommonMSGFragment";

    // 下拉刷新控件
    @Bind(R.id.refresh_layout)
    RefreshLayout mRefreshLayout;

    @Bind(R.id.side_pull_listview)
    ListView mSidepullListView;

    CommonMessageAdapter mAdapter;
    private ArrayList<CommonMessage> mList;
    /**
     * 数据库操作类
     */
    DatabaseDao dao;

    /**
     * 注册广播接受者,接收消息并显示到界面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revTextMsg(ReceiveNewTextMsgEvent event) {
        Log.e(TAG, "CommonMSGFragment收到文字消息");
        ReceiverMessage msg = event.getMsg();
        //if (msg.getSenderUserId() == friend.getMsgNumber()) {
        CommonMessage commonMessage = new CommonMessage();
        commonMessage.setSenderNumber(msg.getSenderUserId());
        commonMessage.setContent(msg.getContent());
        String name = dao.queryFriendNameByUserId(msg.getSenderUserId());
        commonMessage.setSenderName(name);
        commonMessage.setType(Constants.MESSAGE_TYPE_TEXT);
        commonMessage.setIsRead(Constants.MESSAGE_NO_READ);
        commonMessage.setTime(CommUtil.getDate());
        commonMessage.setStatus(Constants.MESSAGE_STATE_RECEIVER);
        commonMessage.setFrom(msg.getFrom());
        // 添加消息到数据库
        dao.addDataToMessage(commonMessage);

        // 向后台发送接收成功反馈
        byte[] result = DataProcessingUtil.receiverTextMsgFeedback(msg.getTextMsgNumber());
        EventBus.getDefault().post(new SendMessage(result));
        // 更新界面
        refresh();
        //}
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_msg, null);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    // Fragment 可见时更新数据
    @Override
    public void onResume() {
        super.onResume();
        refresh();
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
        mList = dao.queryMessageDistinct();

        // 设置适配器
        mAdapter = new CommonMessageAdapter(mList, getActivity());
        mSidepullListView.setAdapter(mAdapter);
        // 设置条目点击监听
        mSidepullListView.setOnItemClickListener(this);

        // 删除按钮回调
//        mSidepullListView.setDelButtonClickListener(new SidePullDelListView.DelButtonClickListener() {
//
//            @Override
//            public void clickHappend(int position) {
//                // 删除数据库中与该用户的所有消息
//                boolean isDel = dao.delDataforMsgInfoByIcNumber(mList.get(position).getSenderNumber());
//                if (isDel) {
//                    // 删除成功,更新界面
//                    mList.remove(position);
//                    refresh();
//                } else {
//                    ToastUtils.showToast(getActivity(), getResources().getString(R.string.delete_failed));
//                }
//            }
//        });
        //设置item长按事件
        mSidepullListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (mSidepullListView.isAllowItemClick()) {
                Log.i(TAG, mList.get(position).getSenderName() + "被长按了");
                ToastUtils.showToast(getActivity(), mList.get(position).getSenderName() + "被长按了");
                return true;//返回true表示本次事件被消耗了，若返回
//                }
//                return false;
            }
        });

        // 设置刷新监听,这里不需要加载更多
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ChatMessageActivity.class);
        Friend friend = new Friend();
        CommonMessage msg = mList.get(position);
        friend.setName(msg.getSenderName());
        friend.setUserId(msg.getSenderNumber());
        intent.putExtra("instance", friend);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                refresh();
                ToastUtils.showToast(getActivity(), getResources().getString(R.string.update_success));
                // 更新完后调用该方法结束刷新
                mRefreshLayout.setRefreshing(false);
            }
        }, 1000);

    }

    private void refresh() {
        // 更新数据
        mList.clear();
        ArrayList<CommonMessage> commonMessages = dao.queryMessageDistinct();
        mList.addAll(commonMessages);
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dao != null) {
            dao.close();
        }
    }
}
