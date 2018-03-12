package com.zhbd.beidoucommunication.adapter;

import java.util.List;

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
import com.zhbd.beidoucommunication.ui.activity.FriendDetailsActivity;
import com.zhbd.beidoucommunication.ui.activity.LoginActivity;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

/**
 * Created by zhangyaru on 2017/8/25.
 */
public class FriendsAdapter extends BaseAdapter implements SectionIndexer {
    private List<Friend> mList = null;
    private Activity mActivity;
    private AlertDialog dialog;
    private DatabaseDao dao;

    public FriendsAdapter(Activity activity, List<Friend> list) {
        mList = list;
        mActivity = activity;
        int userId = SharedPrefUtil.getInt(mActivity, Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(mActivity, userId);
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<Friend> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mList.size();
    }

    public Friend getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Friend mContent = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_contacts_list, null);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_contacts_name);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.tv_index);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getLetter());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvName.setText(mList.get(position).getName());
        // 设置联系人的长按删除功能
//        viewHolder.tvName.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showDialog(position);
//                return true;
//            }
//        });
        viewHolder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, FriendDetailsActivity.class);
                intent.putExtra("friend", mList.get(position));
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvName;
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).getLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }


    /**
     * 初始化并显示删除按钮的弹窗
     *
     * @param position 长按的条目index
     */
    private void showDialog(final int position) {

        dialog = new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("要删除此联系人吗?")
                //相当于点击确认按钮
                .setPositiveButton("看着烦,果断删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 先判断是普通消息还是群组消息,然后删除对应数据
                        Friend friends = mList.get(position);
                        // 删除数据
                        //dao.delDataforFriendsByIcNumber(friends.ic_number);

                        // 确认标志后,从数据库删除该条数据
                        //ToastUtils.showToast(mActivity, "删除成功了~亲");

                        // 发送广播告诉listview更新数据
                        Intent intent = new Intent();
                        intent.setAction("com.contacts.listview.updata");
                        mActivity.sendBroadcast(intent);
                    }
                })
                //相当于点击取消按钮
                .setNegativeButton("手下留情,留着", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialog.show();
    }
}