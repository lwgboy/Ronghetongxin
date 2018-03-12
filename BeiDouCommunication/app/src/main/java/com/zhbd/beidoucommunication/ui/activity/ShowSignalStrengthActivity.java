package com.zhbd.beidoucommunication.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.SignalStrengthAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.event.BeiDouStatusEvent;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowSignalStrengthActivity extends TitlebarActivity {


    @Bind(R.id.lv_signal_strength)
    ListView mListView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revBeidouStatus(BeiDouStatusEvent event) {
        // 设置信号强度
        setSignalStrength();
    }

    private SignalStrengthAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_show_signal_strength);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);

        setTitleText(R.string.signal_strength);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(R.string.main_button_mine, true);
        setRightIcon(0, false);
        setRightText(0, false);
    }

    private void initData() {
        setSignalStrength();
        // 注册广播接收者
        EventBus.getDefault().register(this);
    }

    /**
     * 设置信号强度的数据
     */
    public void setSignalStrength() {
        String signal = SharedPrefUtil.getString(this, Constants.SIGNAL_STRENGTH, "");
        int[] signals = new int[10];
        if (!CommUtil.isEmpty(signal)) {
            // 把取出的字符串解析成int数组
            for (int i = 0; i < signal.length(); i++) {
                int parseInt = 0;
                try {
                    parseInt = Integer.parseInt(String.valueOf(signal.charAt(i)));
                } catch (NumberFormatException e) {

                }
                signals[i] = parseInt;
            }
        }
        if (mAdapter == null) {
            mAdapter = new SignalStrengthAdapter(this, signals);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
