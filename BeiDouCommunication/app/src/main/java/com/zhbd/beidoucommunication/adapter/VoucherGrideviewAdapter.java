package com.zhbd.beidoucommunication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.domain.Money;

import java.util.ArrayList;

/**
 * Created by zhangyaru on 2017/10/14.
 */

public class VoucherGrideviewAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<Money> mList;

    public VoucherGrideviewAdapter(Activity activity, ArrayList<Money> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Money getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new VoucherGrideviewAdapter.ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_voucher_grideview, null);
            holder.tvMoney = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (VoucherGrideviewAdapter.ViewHolder) convertView.getTag();
        }
        Money money = mList.get(position);
        holder.tvMoney.setText(money.getMoney() + "元");
        holder.tvPrice.setText("售价: " + money.getPrice());
        return convertView;
    }


    final static class ViewHolder {
        TextView tvMoney;
        TextView tvPrice;
    }
}
