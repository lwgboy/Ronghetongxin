package com.zhbd.beidoucommunication.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.domain.SmsMessage;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyaru on 2017/9/4.
 */

public class SmsMessageAdapter extends BaseAdapter {
    private List<SmsMessage> mList;
    private Activity mActivity;
    private final DatabaseDao dao;

    public SmsMessageAdapter(List<SmsMessage> mList, Activity mActivity) {
        int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, -1);
        dao = DatabaseDao.getInstance(MyApplication.getContextObject(), userId);
        this.mList = mList;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SmsMessage getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.item_sms_list, null);
            // 初始化控件
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_contacts_name);
            holder.tvLastTime = (TextView) convertView.findViewById(R.id.tv_last_sms_time);
            holder.tvLastMsg = (TextView) convertView.findViewById(R.id.tv_last_sms);
            //holder.tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);
            holder.tvUnReadFlag = (TextView) convertView.findViewById(R.id.tv_sms_unread_flag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SmsMessage message = getItem(position);

        // 填充数据
        // 名字/备注
        if (CommUtil.isEmpty(message.getSenderName())) {
            holder.tvName.setText(String.valueOf(message.getPhoneNumber()));
        } else {
            holder.tvName.setText(message.getSenderName());
        }
        // 最后一次时间, 时间长, 做截取
        String time = message.getTime();
        boolean isToday = false;
        try {
            isToday = CommUtil.isToday(time);
        } catch (ParseException e) {
            e.printStackTrace();
            ToastUtils.showToast(mActivity, mActivity.getResources().getString(R.string.small_problem));
        }
        // 是今天显示时间,不是今天显示日期
        if (isToday) {
            time = time.substring(11, time.length());
        } else {
            time = time.substring(0, 10);
        }
        holder.tvLastTime.setText(time);
        // 最后一条消息
        holder.tvLastMsg.setText(message.getContent());

        // 设置读取状态显示
        // 查找数据,看有几条未读,数字就显示几
        int count = dao.querySMSNoReadCountByPhone(message.getPhoneNumber());
        if (count > 0) {
            holder.tvUnReadFlag.setText(String.valueOf(count));
            holder.tvUnReadFlag.setVisibility(View.VISIBLE);
        } else {
            holder.tvUnReadFlag.setVisibility(View.GONE);
        }


        // 删除按钮
//        final int pos = position;
//        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastUtils.showToast(mActivity, mList.get(pos).getSenderName() + "被删除了");
//                mList.remove(pos);
//                notifyDataSetChanged();
//                ((SidePullDelListView) parent).turnNormal();
//            }
//        });

        return convertView;
    }


    class ViewHolder {
        TextView tvName;
        TextView tvLastMsg;
        TextView tvLastTime;
        TextView tvDelete;
        TextView tvUnReadFlag;
    }

    /**
     * 更新Adapter数据
     *
     * @param list
     */
    public void setList(ArrayList<SmsMessage> list) {
        mList = list;
        notifyDataSetChanged();
    }
}
