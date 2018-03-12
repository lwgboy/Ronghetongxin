package com.zhbd.beidoucommunication.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendDetailsActivity extends TitlebarActivity {

    private static final int SET_RAMARK_REQUEST_CODE = 100;
    public static final int RESULT_CHANGE = 101;
    @Bind(R.id.friend_name)
    TextView mTvName;

    @Bind(R.id.friend_user_id)
    TextView mTvUserId;

    @Bind(R.id.friend_phone_number)
    TextView mTvPhoneNumber;

    @Bind(R.id.friend_id_card)
    TextView mTvIdCard;

    @Bind(R.id.btn_friend_del)
    Button mBtnDel;

    private Friend friend;
    private DatabaseDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_friend_details);
        // 获取传来的好友信息
        friend = (Friend) getIntent().getSerializableExtra("friend");
        // 初始化数据库操作类
        int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(this, userId);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        // 设置标题栏
        setTitleText(R.string.friend_details);
        setLeftText(R.string.home_pager, true);
        setLeftIcon(R.drawable.back_arrow, true);
        setRightIcon(0, false);
        setRightText(0, false);

        mTvName.setText(friend.getName());
        mTvUserId.setText(friend.getUserId() + "");
        mTvIdCard.setText(CommUtil.isEmpty(friend.getIdCard()) ? "未知" : friend.getIdCard());
        mTvPhoneNumber.setText(CommUtil.isEmpty(friend.getPhoneNumber()) ? "未知" : friend.getPhoneNumber());

        //  判断如果当前用户是系统小秘书,不能删除
        if (100000 == friend.getUserId()) {
            mBtnDel.setVisibility(View.GONE);
        } else {
            mBtnDel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击发消息按钮
     */
    @OnClick(R.id.btn_friend_send_message)
    public void sendMessage() {
        Intent intent = new Intent(this, ChatMessageActivity.class);
        intent.putExtra("instance", friend);
        startActivity(intent);
        finish();
    }

    /**
     * 点击删除按钮
     */
    @OnClick(R.id.btn_friend_del)
    public void delFriend() {
        boolean isDel = dao.delDataforFriendBy_id(friend.get_id());
        if (isDel) {
            ToastUtils.showToast(this, R.string.delete_success);
            finish();
        } else {
            ToastUtils.showToast(this, R.string.delete_failed);
        }
    }

    /**
     * 点击设置备注
     */
    @OnClick(R.id.tv_set_ramark)
    public void setRamark() {
        Intent intent = new Intent(this, SetRamarkActivity.class);
        intent.putExtra("name", friend.getName());
        startActivityForResult(intent, SET_RAMARK_REQUEST_CODE);
    }

    /**
     * 设置备注页面的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_RAMARK_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_CHANGE:
                    String newName = data.getStringExtra("newName");
                    mTvName.setText(newName);
                    // 用户数据存入数据库
                    dao.updateRamarkBy_Id(friend.get_id(), newName);
                    break;

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dao != null) {
            dao.close();
        }
    }
}
