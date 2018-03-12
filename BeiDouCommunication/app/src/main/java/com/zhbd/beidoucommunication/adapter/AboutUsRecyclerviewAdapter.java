package com.zhbd.beidoucommunication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.domain.Staff;
import com.zhbd.beidoucommunication.widget.SwipeCardAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xingzhu on 2016/11/15.
 */

public class AboutUsRecyclerviewAdapter extends SwipeCardAdapter<AboutUsRecyclerviewAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<Staff> mList;

    public AboutUsRecyclerviewAdapter(Context context, ArrayList<Staff> list) {
        super(list);
        mContext = context;
        mList = list;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate
                (R.layout.layout_about_us_recycelview_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Staff staff = mList.get(position);
        holder.setName(staff.getRealName());
        holder.setJob(staff.getJob());
        holder.setPicture(staff.getPicRes());
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView mTvRealName;
        private TextView mTvJob;
        private ImageView mIvPicture;

        public MyHolder(View itemView) {
            super(itemView);
            mTvRealName = (TextView) itemView.findViewById(R.id.real_name);
            mTvJob = (TextView) itemView.findViewById(R.id.job_post);
            mIvPicture = (ImageView) itemView.findViewById(R.id.iv_personal_picture);
        }

        public void setName(String name) {
            mTvRealName.setText(name);
        }

        public void setJob(String job) {
            mTvJob.setText(job);
        }

        public void setPicture(int imgRes) {
            mIvPicture.setImageResource(imgRes);
        }
    }
}
