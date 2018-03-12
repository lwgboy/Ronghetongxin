package com.zhbd.beidoucommunication.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.base.BaseFragment;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.event.BeiDouStatusEvent;
import com.zhbd.beidoucommunication.ui.activity.AboutUsActivity;
import com.zhbd.beidoucommunication.ui.activity.EditPersonalInfo;
import com.zhbd.beidoucommunication.ui.activity.LoginActivity;
import com.zhbd.beidoucommunication.ui.activity.ShowIcInfoActivity;
import com.zhbd.beidoucommunication.ui.activity.ShowSignalStrengthActivity;
import com.zhbd.beidoucommunication.ui.activity.VoucherCenterAcitvity;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.widget.ExitLoginPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangyaru on 2017/8/30.
 */

public class MineFragment extends BaseFragment {

    @Bind(R.id.tv_my_user_id)
    TextView mTvUserId;

    @Bind(R.id.mine_fragment_rootview)
    LinearLayout mRootView;

    @Bind(R.id.view_hardware_state)
    LinearLayout mViewHardwareState;

    @Bind(R.id.view_signal_strength)
    LinearLayout mViewHSignalStrength;

    @Bind(R.id.view_ic_info)
    LinearLayout mViewIcInfo;

    @Bind(R.id.view_voucher)
    LinearLayout mViewVoucher;

    @Bind(R.id.view_about_us)
    LinearLayout mViewAboutUs;

    @Bind(R.id.view_setting)
    LinearLayout mViewSetting;

    @Bind(R.id.tv_exit_login)
    TextView mTvExitLogin;

    // 显示硬件状态的textview
    private TextView hardwareStateRight;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revBeidouStatus(BeiDouStatusEvent event) {
        // 设置硬件状态
        setHardwareState();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注册广播接收者
        EventBus.getDefault().register(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View initView() {
        View view = View.inflate(getContext(), R.layout.fragment_mine, null);
        ButterKnife.bind(this, view);
        // 设置标题栏属性
        setTitleText(R.string.main_button_mine);
        setRightText(0, false);
        setRightIcon(R.drawable.set, true);
        setLeftIcon(R.drawable.edit, true);

        int userId = SharedPrefUtil.getInt(getActivity(), Constants.USER_ID, 0);
        mTvUserId.setText(String.valueOf(userId));

        // ic 信息条目
        TextView icInfo = (TextView) mViewIcInfo.findViewById(R.id.tv_setting_text);
        icInfo.setText(R.string.ic_info);
        // 硬件状态条目
        TextView hardwareStateLeft = (TextView) mViewHardwareState.findViewById(R.id.tv_setting_text);
        hardwareStateRight = (TextView) mViewHardwareState.findViewById(R.id.tv_setting_text_right);
        hardwareStateLeft.setText(R.string.hardware_state);
        setHardwareState();
        // 信号强度条目
        TextView signalStrength = (TextView) mViewHSignalStrength.findViewById(R.id.tv_setting_text);
        signalStrength.setText(R.string.signal_strength);
        // 充值条目
        TextView voucher = (TextView) mViewVoucher.findViewById(R.id.tv_setting_text);
        voucher.setText(R.string.balance_voucher);
        // 关于我们条目
        TextView aboutUs = (TextView) mViewAboutUs.findViewById(R.id.tv_setting_text);
        aboutUs.setText(R.string.about_us);
        // 设置条目
        TextView setting = (TextView) mViewSetting.findViewById(R.id.tv_setting_text);
        setting.setText(R.string.setting);
        return view;
    }

    /**
     * 点击个人信息
     */
    @OnClick(R.id.rl_personal_details)
    public void editPersonalDetails() {
        Intent intent = new Intent(getActivity(), EditPersonalInfo.class);
        getActivity().startActivity(intent);
    }

    /**
     * 点击左侧编辑个人信息
     *
     * @param fragment
     */
    @Override
    protected void clickLeft(BaseFragment fragment) {
        // 跳转到编辑个人信息界面
        Intent intent = new Intent(getActivity(), EditPersonalInfo.class);
        getActivity().startActivity(intent);
    }

    @Override
    protected void clickRight(BaseFragment fragment) {
        super.clickRight(fragment);
        setting();
    }

    /**
     * 点击设置做的操作
     */
    private void setting() {
        ToastUtils.showToast(getActivity(), "努力开发中,敬请期待...");
    }

    /**
     * 设置硬件状态
     */
    public void setHardwareState() {
        // 本地获取硬件状态,设置界面
        int i = SharedPrefUtil.getInt(getActivity(), Constants.HARDWARE_STATE, -1);
        switch (i) {
            case 0:
                hardwareStateRight.setText("正常");
                break;
            case 1:
                hardwareStateRight.setText("异常");
                break;
            default:
                hardwareStateRight.setText("未知");
                break;
        }
    }

    /**
     * 点击ic信息条目操作
     */
    @OnClick(R.id.view_ic_info)
    public void clickIcInfo() {
        Intent intent = new Intent(getActivity(), ShowIcInfoActivity.class);
        startActivity(intent);
    }

    /**
     * 点击充值
     */
    @OnClick(R.id.view_voucher)
    public void clickVoucher() {
        Intent intent = new Intent(getActivity(), VoucherCenterAcitvity.class);
        startActivity(intent);
    }

    /**
     * 点击关于我们执行的操作
     */
    @OnClick(R.id.view_about_us)
    public void clickAboutUs() {
        Intent intent = new Intent(getActivity(), AboutUsActivity.class);
        startActivity(intent);
    }

    /**
     * 点击信号强度条目操作
     */
    @OnClick(R.id.view_signal_strength)
    public void clickSignalStrength() {
        Intent intent = new Intent(getActivity(), ShowSignalStrengthActivity.class);
        startActivity(intent);
    }

    /**
     * 点击设置的操作
     */
    @OnClick(R.id.view_setting)
    public void clickSetting() {
        setting();
    }

    /**
     * 点击退出登录的操作
     */
    @OnClick(R.id.tv_exit_login)
    public void clickExitLogin() {
        // 弹出退出登录的popupWindow
        ExitLoginPopupWindow popupWindow = new ExitLoginPopupWindow(getActivity(), onItemClickListener);
        // 设置弹出的位置
        popupWindow.showAtLocation(mRootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 点击退出登录的回调
     */
    View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 密码重置
            SharedPrefUtil.removeString(getActivity(), Constants.USER_PASSWORD);
            // 数据库操作类重置
            DatabaseDao dao = DatabaseDao.getInstance(getActivity(), 0);
            dao.removeDao();
            // 跳转到login页面
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
