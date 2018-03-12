package com.zhbd.beidoucommunication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.PatternOfPaymentAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.domain.Payment;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatternOfPaymentActivity extends TitlebarActivity {
    @Bind(R.id.tv_pay_money)
    TextView mTvPayMoney;

    @Bind(R.id.listview_pay)
    ListView mListView;

    private String[] payDesignation = {"支付宝", "微信支付", "银行卡快捷支付", "朋友代付"};

    private int[] imgRes = {
            R.drawable.aipay_icon,
            R.drawable.weichat_icon,
            R.drawable.yinlian_icon,
            R.drawable.pengyoudaifu_icon};
    private ArrayList<Payment> mList;
    private PatternOfPaymentAdapter mAdapter;
    private double price;
    private int money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_pattern_of_payment);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        setTitleText(R.string.cashier_desk);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(0, false);
        setRightIcon(0, false);
        setRightText(0, false);
    }

    private void initData() {
        price = getIntent().getDoubleExtra("price", 0);
        money = getIntent().getIntExtra("money", 0);
        mTvPayMoney.setText(String.valueOf(price) + "元");
        mList = new ArrayList<>();
        for (int i = 0; i < imgRes.length; i++) {
            Payment payment = new Payment();
            payment.setDesignation(payDesignation[i]);
            payment.setImgRes(imgRes[i]);
            if (i == 0) {
                payment.setSelect(true);
            } else {
                payment.setSelect(false);
            }
            mList.add(payment);
        }

        mAdapter = new PatternOfPaymentAdapter(this, mList);

        mListView.setAdapter(mAdapter);
    }

    @OnClick(R.id.tv_pay)
    public void clickPay() {
        // 如果选择朋友代付,可以下一步操作
        if (mAdapter.getSelectPosition() == 3) {
            MyApplication.addToQueue(this);
            Intent intent = new Intent(this, FriendPaidAcitvity.class);
            intent.putExtra("price", price);
            intent.putExtra("money", money);
            startActivity(intent);
        } else {
            ToastUtils.showToast(this, "暂未开通,敬请期待..");
        }
    }
}
