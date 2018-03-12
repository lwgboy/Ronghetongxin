package com.zhbd.beidoucommunication.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.domain.IcCardInfo;
import com.zhbd.beidoucommunication.ui.activity.FriendDetailsActivity;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import java.util.List;

/**
 * Created by zhangyaru on 2017/8/25.
 */
public class IcInfoAdapter extends BaseAdapter {
    private List<IcCardInfo> mList = null;
    private Activity mActivity;

    public IcInfoAdapter(Activity activity, List<IcCardInfo> list) {
        mList = list;
        mActivity = activity;
    }

    public int getCount() {
        return mList.size();
    }

    public IcCardInfo getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final IcCardInfo icInfo = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_ic_info_list, null);
            viewHolder.tvIcNumber = (TextView) convertView.findViewById(R.id.tv_ic_number);
            viewHolder.tvIsControl = (TextView) convertView.findViewById(R.id.tv_is_control);
            viewHolder.tvIsQuiesce = (TextView) convertView.findViewById(R.id.tv_is_quiesce);
            viewHolder.tvServiceFrequency = (TextView) convertView.findViewById(R.id.tv_service_frequency);
            viewHolder.tvGrade = (TextView) convertView.findViewById(R.id.tv_grade);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 填充界面
        viewHolder.tvIcNumber.setText(String.valueOf(icInfo.getIcNumber()));
        viewHolder.tvIsControl.setText(icInfo.isControl() ? "抑制" : "非抑制");
        viewHolder.tvIsQuiesce.setText(icInfo.isQuiesce() ? "静默" : "非静默");
        viewHolder.tvServiceFrequency.setText(String.valueOf(icInfo.getServiceFrequency()));
        viewHolder.tvGrade.setText(String.valueOf(icInfo.getGrade()));

        return convertView;
    }

    final static class ViewHolder {
        TextView tvIcNumber;
        TextView tvIsControl;
        TextView tvIsQuiesce;
        TextView tvServiceFrequency;
        TextView tvGrade;
    }
}