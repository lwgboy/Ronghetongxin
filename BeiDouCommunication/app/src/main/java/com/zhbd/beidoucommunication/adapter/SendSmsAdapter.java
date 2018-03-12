package com.zhbd.beidoucommunication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.domain.SmsMessage;

import java.util.List;

public class SendSmsAdapter extends BaseAdapter {

    public interface IMsgViewType {
        int RECEIVER_SMS = 0;// 收到对方的短信
        int SEND_SMS = 1;// 自己发送出去的短信
    }

    private static final int ITEMCOUNT = 2;// 消息类型的总数
    private List<SmsMessage> mList;// 消息对象数组
    private LayoutInflater mInflater;

    public SendSmsAdapter(Context context, List<SmsMessage> mList) {
        this.mList = mList;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mList.size();
    }

    public SmsMessage getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 得到Item的类型，是对方发过来的消息，还是自己发送出去的
     */
    public int getItemViewType(int position) {
        SmsMessage msg = mList.get(position);
        //收到的消息
        if (msg.getStatus() == Constants.MESSAGE_STATE_RECEIVER) {
            // 收到文字消息
            return IMsgViewType.RECEIVER_SMS;
        } else {//自己发送的消息
            //发送文字消息
            return IMsgViewType.SEND_SMS;
        }
    }

    /**
     * Item类型的总数
     */
    public int getViewTypeCount() {
        return ITEMCOUNT;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        SmsMessage message = getItem(position);
        boolean isReceive = (message.getStatus() == Constants.MESSAGE_STATE_RECEIVER);

        ViewHolder viewHolder = null;
        // 复用convertVIew
        if (convertView == null) {
            // 判断状态是发送还是接收,返回条目类型
            if (isReceive) {
                convertView = mInflater.inflate(
                        R.layout.row_received_sms, null);
            } else {
                convertView = mInflater.inflate(
                        R.layout.row_sent_sms, null);
            }
            // 找到对应布局的对应控件
            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView
                    .findViewById(R.id.tv_date_time);
            viewHolder.tvContent = (TextView) convertView
                    .findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
            // 设置tag
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSendTime.setText(message.getTime());
        viewHolder.tvContent.setText(message.getContent());
        return convertView;
    }

    public class ViewHolder {
        public TextView tvSendTime;
        public TextView tvContent;
    }

}