package com.zhbd.beidoucommunication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.domain.Money;
import com.zhbd.beidoucommunication.domain.Payment;

import java.util.ArrayList;

/**
 * Created by zhangyaru on 2017/10/14.
 */

public class PatternOfPaymentAdapter extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<Payment> mList;

    public PatternOfPaymentAdapter(Activity activity, ArrayList<Payment> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Payment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new PatternOfPaymentAdapter.ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_pattern_of_payment, null);
            holder.rbtnSelect = (RadioButton) convertView.findViewById(R.id.rbtn_select);
            holder.ivPayment = (ImageView) convertView.findViewById(R.id.iv_payment);
            holder.tvDesignation = (TextView) convertView.findViewById(R.id.tv_pay_designation);
            convertView.setTag(holder);
        } else {
            holder = (PatternOfPaymentAdapter.ViewHolder) convertView.getTag();
        }
        final ViewHolder finalHolder = holder;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setSelect(false);
                }
                finalHolder.rbtnSelect.setChecked(true);
                mList.get(position).setSelect(true);
                notifyDataSetChanged();
            }
        });
        final Payment payment = mList.get(position);
        holder.rbtnSelect.setChecked(payment.isSelect());
        holder.ivPayment.setBackgroundResource(payment.getImgRes());
        holder.tvDesignation.setText(payment.getDesignation());
        return convertView;
    }


    final static class ViewHolder {
        RadioButton rbtnSelect;
        ImageView ivPayment;
        TextView tvDesignation;
    }

    // 返回当前选中的支付方式
    public int getSelectPosition() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isSelect()) {
                return i;
            }
        }
        return -1;
    }
}
