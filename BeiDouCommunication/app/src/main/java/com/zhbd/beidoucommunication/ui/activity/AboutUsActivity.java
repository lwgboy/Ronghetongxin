package com.zhbd.beidoucommunication.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.AboutUsRecyclerviewAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.domain.Staff;
import com.zhbd.beidoucommunication.widget.ItemRemovedListener;
import com.zhbd.beidoucommunication.widget.SwipeCardLayoutManager;
import com.zhbd.beidoucommunication.widget.SwipeCardRecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUsActivity extends TitlebarActivity {

    public static final String TAG = "AboutUsActivity";

    @Bind(R.id.about_us_recyclerView)
    SwipeCardRecyclerView mRecyclerView;
    private AboutUsRecyclerviewAdapter mAdapter;
    private ArrayList<Staff> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitleText(R.string.about_us);
        setLeftIcon(R.drawable.back_arrow, true);
        setLeftText(R.string.return_text, true);
        setRightIcon(0, false);
        setRightText(0, false);

        mRecyclerView.setLayoutManager(new SwipeCardLayoutManager());
        mList = new ArrayList<>();
        // 设置数据的方法
        setData();

        mAdapter = new AboutUsRecyclerviewAdapter(this, mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRemovedListener(new ItemRemovedListener() {
            @Override
            public void onRightRemoved() {
                Log.e(TAG, "→→→→" + (mList.size() -1));
                Staff staff = mList.get(mList.size() - 1);
                mList.add(0, staff);
                mList.remove(mList.size()-1);
                mAdapter.notifyDataSetChanged();
                //Toast.makeText(AboutUsActivity.this, mList.get(mList.size() - 1) + " was right removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLeftRemoved() {
                Log.e(TAG, "←←←←←←" + (mList.size() -1));
                Staff staff = mList.get(mList.size() - 1);
                mList.add(0, staff);
                mList.remove(mList.size()-1);
                mAdapter.notifyDataSetChanged();
                //Toast.makeText(AboutUsActivity.this, mList.get(mList.size() - 1) + " was left removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        Staff staff = new Staff();
        staff.setRealName("张雅茹");
        staff.setJob("安卓开发工程师");
        staff.setPicRes(R.drawable.nv1);
        mList.add(staff);

        staff = new Staff();
        staff.setRealName("李瑞丰");
        staff.setJob("什么都会的软件开发工程师");
        staff.setPicRes(R.drawable.nan1);
        mList.add(staff);

        staff = new Staff();
        staff.setRealName("赵娜");
        staff.setJob("C语言开发工程师");
        staff.setPicRes(R.drawable.nv2);
        mList.add(staff);
    }
}
