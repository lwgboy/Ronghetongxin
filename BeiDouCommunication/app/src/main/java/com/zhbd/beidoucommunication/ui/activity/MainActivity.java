package com.zhbd.beidoucommunication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.MainPagerAdapter;
import com.zhbd.beidoucommunication.base.BaseFragment;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.ui.fragment.ContactsFragment;
import com.zhbd.beidoucommunication.ui.fragment.MessageFragment;
import com.zhbd.beidoucommunication.ui.fragment.MineFragment;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.view.NoSlideViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.no_slide_viewpager)
    NoSlideViewPager viewPager;

    @Bind(R.id.rg_main_bottom)
    RadioGroup mRgBottom;

    @Bind(R.id.rbtn_message)
    RadioButton mRbtnMessage;

    @Bind(R.id.rbtn_contacts)
    RadioButton mRbtnContacts;

    @Bind(R.id.rbtn_mine)
    RadioButton mRbtnMine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (!CommUtil.isServiceWork("com.zhbd.beidoucommunication.service.NotificationService")) {
//            //启动消息推送服务
//            Intent notificationService = new Intent();
//            notificationService.setAction("com.zhbd.beidoucommunication.service.NotificationService");
//            notificationService.setPackage(getPackageName());
//            startService(notificationService);
//        }
        MyApplication.getInstance().startnotifySecrvice();
        // 填充布局
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 绑定ButterKnife
        init();

        setData();
    }

    /**
     * 三个好友测试数据
     */
    private void setData() {
        // 测试数据北斗机状态
        byte[] bys = new byte[]{0, 1, 2, 3, 4, 1, 2, 3, 4, 0, 0, 2, 3, -124, -30, 10, 11, 12, 3, -124, -29, 10, 11, 12};
        DataProcessingUtil.beidouMachineState(bys);


        boolean setData = SharedPrefUtil.getBoolean(this, "setData", true);
        if (setData) {
            // 对应信息存入数据库
            int addType = DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD;
            Friend friend = new Friend();
            friend.setUserId(110011);
            friend.setAddType(addType);
            friend.setName("张三");
            switch (addType) {
                case DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD:
                    friend.setIdCard("123456789012345678");
                    break;
                case DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER:
                    friend.setPhoneNumber("13345678908");
                    break;
            }
            int userId = SharedPrefUtil.getInt(this, Constants.USER_ID, -1);
            DatabaseDao dao = DatabaseDao.getInstance(this, userId);
            dao.addDataToFriend(friend);

            //----------------------------------------------
            addType = DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER;
            friend = new Friend();
            friend.setUserId(100000);
            friend.setAddType(addType);
            friend.setName("融合小秘书");
            switch (addType) {
                case DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD:
                    friend.setIdCard("123456789012345678");
                    break;
                case DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER:
                    friend.setPhoneNumber("13345678908");
                    break;
            }
            dao.addDataToFriend(friend);
            //----------------------------------------------

            addType = DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER;
            friend = new Friend();
            friend.setUserId(110012);
            friend.setAddType(addType);
            friend.setName("李四");
            switch (addType) {
                case DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD:
                    friend.setIdCard("123456789012345678");
                    break;
                case DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER:
                    friend.setPhoneNumber("13345678908");
                    break;
            }
            dao.addDataToFriend(friend);

            addType = DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD;
            friend = new Friend();
            friend.setUserId(110013);
            friend.setAddType(addType);
            friend.setName("王五");
            switch (addType) {
                case DataProcessingUtil.ADD_FRIENDS_FOR_IDCARD:
                    friend.setIdCard("123456789012345678");
                    break;
                case DataProcessingUtil.ADD_FRIENDS_FOR_PHONE_NUMBER:
                    friend.setPhoneNumber("13345678908");
                    break;
            }
            dao.addDataToFriend(friend);
            SharedPrefUtil.putBoolean(this, "setData", false);
        }
    }

    private void init() {

        // 启动等待时长悬浮窗的服务
//        Intent waitTimeIntent = new Intent(this, FloatViewService.class);
//        startService(waitTimeIntent);
        // 测试数据开始
//        Intent serviceIntent = new Intent();
//        serviceIntent.setAction("com.zhbd.beidoucommunication.service.GlobalService");
//        serviceIntent.setPackage(getPackageName());
//        startService(serviceIntent);
//        // 测试数据结束
        // RadioButton 联动 fragment
        mRgBottom.setOnCheckedChangeListener(this);
        // 创建Fragment对象放入集合
        ArrayList<BaseFragment> list = new ArrayList<>();
        MessageFragment messageFragment = new MessageFragment();
        ContactsFragment contactsFragment = new ContactsFragment();
        MineFragment mineFragment = new MineFragment();
        list.add(messageFragment);
        list.add(contactsFragment);
        list.add(mineFragment);
        // viewPager设置适配器
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), list);

        // 设置缓存两个页面
        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(pagerAdapter);
        mRbtnMessage.setChecked(true);

        // 检测网络,提示用户
//        NetWorkUtil.inspectNet()
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rbtn_message:
                viewPager.setCurrentItem(0);
                break;
            case R.id.rbtn_contacts:
                viewPager.setCurrentItem(1);
                break;
            case R.id.rbtn_mine:
                viewPager.setCurrentItem(2);
                break;
        }
    }

    //退出时的时间
    private long mExitTime;

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showToast(MainActivity.this, getResources().getString(R.string.again_exit));
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            //System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束掉当前程序,包括服务
//        Process.killProcess(Process.myPid());
    }
}
