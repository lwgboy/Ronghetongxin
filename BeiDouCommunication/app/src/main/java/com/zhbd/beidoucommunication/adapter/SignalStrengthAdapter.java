package com.zhbd.beidoucommunication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;

/**
 * Created by zhangyaru on 2017/8/25.
 */
public class SignalStrengthAdapter extends BaseAdapter {
    private int[] mArr = null;
    private Activity mActivity;

    public SignalStrengthAdapter(Activity activity, int[] list) {
        mArr = list;
        mActivity = activity;
    }

    public int getCount() {
        return mArr.length;
    }

    public Integer getItem(int position) {
        return mArr[position];
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int signal = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_signal_strength, null);
            viewHolder.tvIcNumber = (TextView) convertView.findViewById(R.id.tv_signal_lable);
            viewHolder.pbSignal = (ProgressBar) convertView.findViewById(R.id.prsbar_signal);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 填充界面
        viewHolder.tvIcNumber.setText((position + 1) + "#");
        viewHolder.pbSignal.setProgress(mArr[position]);

        return convertView;
    }

    final static class ViewHolder {
        TextView tvIcNumber;
        ProgressBar pbSignal;
    }
}