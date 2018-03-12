package com.zhbd.beidoucommunication.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.ui.activity.FriendDetailsActivity;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import java.util.List;

/**
 * Created by zhangyaru on 2017/8/25.
 */
public class BuildGroupAdapter extends BaseAdapter implements SectionIndexer {
    private List<Friend> mList = null;
    private Activity mActivity;
    private DatabaseDao dao;
    private boolean[] state;

    public BuildGroupAdapter(Activity activity, List<Friend> list) {
        mList = list;
        mActivity = activity;
        state = new boolean[mList.size()];
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_build_group_select_contacts_list, null);
            viewHolder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_contacts_item);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_contacts_name);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.tv_index);
            viewHolder.isSelected = (CheckBox) convertView.findViewById(R.id.checkbox_is_selected);
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
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state[position] = !state[position];
                notifyDataSetChanged();
            }
        };
        viewHolder.llItem.setOnClickListener(listener);

        viewHolder.isSelected.setChecked(state[position]);

        return convertView;
    }

    final static class ViewHolder {
        LinearLayout llItem;
        CheckBox isSelected;
        TextView tvLetter;
        TextView tvName;
    }

    public boolean[] getState() {
        return state;
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
}