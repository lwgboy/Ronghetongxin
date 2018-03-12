package com.zhbd.beidoucommunication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.VoucherGrideviewAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.domain.Money;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VoucherCenterAcitvity extends TitlebarActivity {
    @Bind(R.id.tv_voucher_userid)
    TextView mTvUserId;

    @Bind(R.id.gridview)
    GridView gridView;

    private int[] money = {10, 20, 30, 50, 100, 200, 300, 500};
    private double[] price = {10, 20, 30, 49.95, 99.90, 199.80, 299.70, 499.50};
    private ArrayList<Money> mList;
    private VoucherGrideviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_voucher_center_acitvity);
        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        setTitleText(R.string.voucher_center);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(R.string.return_text, true);
        setRightIcon(0, false);
        setRightText(0, false);
    }

    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < money.length; i++) {
            Money ins = new Money();
            ins.setMoney(money[i]);
            ins.setPrice(price[i]);
            mList.add(ins);
        }
        mAdapter = new VoucherGrideviewAdapter(this, mList);

        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.addToQueue(VoucherCenterAcitvity.this);
                Intent intent = new Intent(VoucherCenterAcitvity.this, PatternOfPaymentActivity.class);
                intent.putExtra("money", money[position]);
                intent.putExtra("price", price[position]);
                startActivity(intent);
            }
        });

        int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        mTvUserId.setText(String.valueOf(userId));
    }
}
