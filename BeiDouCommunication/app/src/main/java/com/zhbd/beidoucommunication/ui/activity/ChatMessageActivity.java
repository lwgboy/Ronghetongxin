package com.zhbd.beidoucommunication.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.adapter.ChatMsgAdapter;
import com.zhbd.beidoucommunication.base.TitlebarActivity;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.BaseMessage;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.domain.Group;
import com.zhbd.beidoucommunication.domain.ReceiverMessage;
import com.zhbd.beidoucommunication.event.DaoRowIdEvent;
import com.zhbd.beidoucommunication.event.ReceiveNewTextMsgEvent;
import com.zhbd.beidoucommunication.event.SendFailUpdateEvent;
import com.zhbd.beidoucommunication.event.SendMessage;
import com.zhbd.beidoucommunication.event.TextMsgFeekBackEvent;
import com.zhbd.beidoucommunication.event.WaitTimeEvent;
import com.zhbd.beidoucommunication.http.NetWorkUtil;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;
import com.zhbd.beidoucommunication.utils.ToastUtils;
import com.zhbd.beidoucommunication.view.AudioRecorderButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatMessageActivity extends TitlebarActivity {
    private final String TAG = "ChatMessageActivity";

    private static final int REQUEST_CODE_CONTENT = 10002;
    /**
     * 请求获取联系人的权限码
     */
    private static final int REQUEST_RECORDER_NONE = 200;

    // 要申请的权限
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    @Bind(R.id.chat_listview)
    ListView mListView;
    // 消息输入框
    @Bind(R.id.et_sendmessage)
    EditText mEtContent;
    // 显示等待时长
    @Bind(R.id.tv_sendmessage)
    TextView mTvContent;
    // 跟布局
    @Bind(R.id.send_msg_rootview)
    RelativeLayout mRootView;
    // 发送按钮
    @Bind(R.id.btn_send)
    Button mBtnSend;
    // 更多按钮
    @Bind(R.id.btn_more)
    Button mBtnMore;
    // 按住说话
    @Bind(R.id.btn_audio_recorder)
    AudioRecorderButton mAudioRecorder;
    // 语音切换按钮
    @Bind(R.id.btn_set_mode_voice)
    Button mBtnVoice;
    // 键盘切换按钮
    @Bind(R.id.btn_set_mode_keyboard)
    Button mBtnKeyBoard;

    private Friend friend;
    private Group group;
    private boolean isGroupMsg;
    // 数据库操作类
    private DatabaseDao dao;
    // 行id
    private int rowid = -1;

    // 是否压缩完成
    private boolean isSpxOk;

    private Intent waitTimeIntent;
    private CommonMessage msg = new CommonMessage();
    private AlertDialog dialog;
    private boolean isSuccess;

    ArrayList<BaseMessage> mList = new ArrayList<>();
    private ChatMsgAdapter mAdapter;
    // 记录是否正在录音
    private boolean isVoice;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            // 判断是否添加成功
            if (isSuccess) {
                Log.e(TAG, "isSuccess:rowid=" + rowid);
                msg.set_id(rowid);
            } else {
                ToastUtils.showToast(ChatMessageActivity.this, getResources().getString(R.string.server_error));
            }
            if (isGroupMsg) {

            } else {
                // 组合数据
                byte[] result = DataProcessingUtil.textMsgDataPackage(msg, false, false);
                // 连接网络并发送数据
                EventBus.getDefault().post(new SendMessage(result));
            }
        }
    };

    /**
     * 注册广播接受者,接收消息并显示到界面
     */
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 语音文件压缩完成的广播
            if (Constants.ACTION_SPX_OK.equals(action)) {
                // 发送消息
                // 封装发送数据,参数依次为,语音秒数, 文件路径, 是否加密, 是否群组消息
                byte[] data = DataProcessingUtil.voiceMsgDataPackage(msg, false, false);
                // 发送数据
                EventBus.getDefault().post(new SendMessage(data));
            }
            // 接收到新的语音消息广播
//            if (Constants.RECEIVE_VOICE_MSG_ACTION.equals(action)) {
//                CommonMessage receive = (CommonMessage) intent.getSerializableExtra("voicemsg");
//                // 存入数据库
//                boolean isOk = dao.addDataToMessage(receive);
//                // 数据库存入成功,更新界面
//                if (isOk) {
//                    mList.add(receive);
//                    mAdapter.notifyDataSetChanged();
//                }
//            }
//            } else {
//                // 判断页面时群组消息的页面还是普通消息页面
//                Serializable receive = intent.getSerializableExtra("receive");
//                if (isGroupMsg) {
//                    // 避免强转出异常
//                    if (receive instanceof DBGroupMessage) {
//                        DBGroupMessage groupMessage = (DBGroupMessage) receive;
//                        // 判断如果收到的消息中的号码是当前页面的号码,就显示
//                        if (groupMessage.group_number == number) {
//                            mDataArrays.add(groupMessage);
//                        }
//                    }
//                } else {
//                    if (receive instanceof DBMessage) {
//                        DBMessage dbMessage = (DBMessage) receive;
//                        if (dbMessage.com_user_number == number) {
//                            mDataArrays.add(dbMessage);
//                        }
//                    }
//                }
//            }
//            // 通知ListView，数据已发生改变
//            mAdapter.notifyDataSetChanged();
//            mListView.setSelection(mDataArrays.size() - 1);
            //refreshListView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRes(R.layout.activity_chat_message);

        initView();
        initData();
    }

    private void initView() {
        ButterKnife.bind(this);
        // 获取传来的好友信息
        Serializable instance = getIntent().getSerializableExtra("instance");
        // 群组信息
        if (instance instanceof Group) {
            isGroupMsg = true;
            this.group = (Group) instance;
            // 好友信息
        } else {
            isGroupMsg = false;
            this.friend = (Friend) instance;
            Log.e(TAG, this.friend.toString());
        }

        // 设置标题栏
        if (isGroupMsg) {
            setTitleText(group.getName());
        } else {
            setTitleText(CommUtil.isEmpty(friend.getName()) ?
                    String.valueOf(friend.getUserId()) : friend.getName());
        }
        setLeftText(R.string.main_button_message, true);
        setLeftIcon(R.drawable.back_arrow, true);
        setRightIcon(0, false);
        setRightText(0, false);

        // 用于键盘的隐藏和显示
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //监听输入框的文本改变事件,友好的向用户提示文字超长
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 内容不为空, 显示发送按钮,为空,隐藏发送按钮
                if (CommUtil.isEmpty(s.toString())) {
                    mBtnMore.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                    return;
                } else {
                    mBtnMore.setVisibility(View.GONE);
                    mBtnSend.setVisibility(View.VISIBLE);

                }

                // 判断文字达到长度不能再输入
                if (s.length() >= 30) {
                    ToastUtils.showToast(ChatMessageActivity.this,
                            getResources().getString(R.string.content_overlength));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * 语音完成的回调
         */
        mAudioRecorder.setAudioFinishRecorderListener(
                new AudioRecorderButton.AudioFinishRecorderListener() {
                    @Override
                    public void onFinish(float seconds, String filePath) {
                        // 把数据填充到对象
                        msg = new CommonMessage();
                        msg.setSenderNumber(friend.getUserId());
                        msg.setSenderName(friend.getName());
                        msg.setContent(filePath);
                        msg.setTime(CommUtil.getDate());
                        // 秒数四舍五入
                        msg.setSecond(Math.round(seconds));
                        // 状态默认为失败, 得到反馈后再更新
                        msg.setStatus(Constants.MESSAGE_STATE_SENDING);
                        msg.setType(Constants.MESSAGE_TYPE_VOICE);
                        msg.setIsRead(Constants.MESSAGE_HAVE_READ);
//                        msg.setFrom(NetWorkUtil.getFromType());

                        // 加入集合
                        mList.add(msg);
                        // 存储到数据库
                        //dao.addDataToMessage(msg);

                        // 更新界面
                        mAdapter.notifyDataSetChanged();
                        mListView.setSelection(mList.size() - 1);

                        // 未避免语音文件没有压缩完成就执行此操作,判断一下先

                    }
                });
    }

    private void initData() {
        int myUserId = SharedPrefUtil.getInt(this, Constants.USER_ID, 0);
        // 数据库中查找消息,初始化界面
        dao = DatabaseDao.getInstance(this, myUserId);

        if (isGroupMsg) {
            mList.addAll(dao.queryGroupMessageByGroupId(group.getGroupId()));
        } else {
            mList.addAll(dao.queryCommonMessageByUserId(friend.getUserId()));
            // 吧此人的消息全都设置为已读
            dao.updateIsReadToHaveReadOfMsg(friend.getUserId());
        }

        mAdapter = new ChatMsgAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        if (mList.size() >= 1) {
            mListView.setSelection(mList.size() - 1);
        }
        // 提前向用户获取录音权限
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendFailUpdate(SendFailUpdateEvent event) {
        refreshListView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revWaitTime(WaitTimeEvent event) {
        // 更新界面,向用户显示需要等待的秒数
        long waitTime = event.getWaitTime();
        if (waitTime > 0) {
            // Log.e(TAG, "if:" + waitTime);
            mEtContent.setVisibility(View.GONE);
            mEtContent.setText("");
            mTvContent.setText("请等待" + waitTime + "'s");
            mTvContent.setVisibility(View.VISIBLE);
        } else {
            // Log.e(TAG, "else:" + waitTime);
            mEtContent.setVisibility(View.VISIBLE);
            mEtContent.setText("");
            mTvContent.setVisibility(View.GONE);
            mTvContent.setText("");
        }
    }

    @Subscribe
    public void revRowId(DaoRowIdEvent event) {
        rowid = event.getRowid();
        // 发消息表示可以给服务器发送数据了,rowid作为文字消息序号
        handler.sendEmptyMessage(100);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revtextMsgFeekBack(TextMsgFeekBackEvent event) {
        int msgNumber = event.getMsgNumber();
        int status = event.getStatus();

        int lines = dao.updateStateBy_Id(msgNumber, (byte)status);
        if (lines > 0) {
            // 消息状态可能发生改变,更新界面
            refreshListView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revTextMsg(ReceiveNewTextMsgEvent event){
        Log.e(TAG, "ChatMessageActivity收到文字消息并存入数据库");
        ReceiverMessage msg = event.getMsg();
//                if (msg.getSenderUserId() == friend.getMsgNumber()) {
        CommonMessage commonMessage = new CommonMessage();
        commonMessage.setSenderNumber(msg.getSenderUserId());
        commonMessage.setContent(msg.getContent());
        commonMessage.setSenderName(dao.queryFriendNameByUserId(msg.getSenderUserId()));
        commonMessage.setType(Constants.MESSAGE_TYPE_TEXT);
        commonMessage.setIsRead(Constants.MESSAGE_HAVE_READ);
        commonMessage.setTime(CommUtil.getDate());
        commonMessage.setStatus(Constants.MESSAGE_STATE_RECEIVER);
        commonMessage.setFrom(msg.getFrom());
        // 添加消息到数据库
        dao.addDataToMessage(commonMessage);

        // 向后台发送接收成功反馈
        byte[] result = DataProcessingUtil.receiverTextMsgFeedback(msg.getTextMsgNumber());

        EventBus.getDefault().post(new SendMessage(result));

        // 如果收到与本页面号码一致的消息才更新界面
        if (friend.getUserId() == msg.getSenderUserId()) {
            // 更新界面
            refreshListView();
        }
//                }
    }

    @Override
    protected void clickLeft(Activity activity) {
        super.clickLeft(activity);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 点击语音切换按钮的回调
     */
    @OnClick(R.id.btn_set_mode_voice)
    public void setModeVoice() {
        ToastUtils.showToast(this, "努力开发中,敬请期待..");
//        // 语音按钮隐藏
//        mBtnVoice.setVisibility(View.GONE);
//        // 键盘按钮显示
//        mBtnKeyBoard.setVisibility(View.VISIBLE);
//        // 隐藏软键盘
//        imm.hideSoftInputFromWindow(mAudioRecorder.getWindowToken(), 0); //强制隐藏键盘
//        // 按住说话按钮显示
//        mAudioRecorder.setVisibility(View.VISIBLE);
//        // 输入内容文本框隐藏
//        mEtContent.setVisibility(View.GONE);
//        // 检测是否有录音权限
//        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
//        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//            // 如果没有授予该权限，就去提示用户请求
//            showDialogTipUserRequestPermission();
//        }
    }

    /**
     * 点击加号按钮的回调
     */
    @OnClick(R.id.btn_more)
    public void setModeMore() {
        ToastUtils.showToast(this, "努力开发中,敬请期待..");
    }

    /**
     * 注册权限申请回调
     *
     * @param requestCode  申请码
     * @param permissions  申请的权限
     * @param grantResults 结果
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORDER_NONE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    ToastUtils.showToast(this, getResources().getString(R.string.permission_success));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.permission_not_available))
                .setMessage(getResources().getString(R.string.open_permission_for_setting))
                .setPositiveButton(getResources().getString(R.string.immediately_open), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.recorder_permission_not_available))
                .setMessage(getResources().getString(R.string.recorder_permission_not_available_msg))
                .setPositiveButton(getResources().getString(R.string.immediately_open), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORDER_NONE);
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    /**
     * 点击键盘切换按钮的回调
     */
    @OnClick(R.id.btn_set_mode_keyboard)
    public void setModeKeyboard() {
        // 显示键盘按钮
        mBtnKeyBoard.setVisibility(View.GONE);
        // 隐藏语音按钮
        mBtnVoice.setVisibility(View.VISIBLE);
        // 显示软键盘
        imm.showSoftInput(mEtContent, InputMethodManager.SHOW_FORCED);  //强制显示键盘
        // 按住说话按钮隐藏
        mAudioRecorder.setVisibility(View.GONE);
        // 输入内容文本框显示
        mEtContent.setVisibility(View.VISIBLE);
    }


    /**
     * 点击文字发送按钮
     */
    @OnClick(R.id.btn_send)
    public void send() {
        // 获取消息内容
        String contString = mEtContent.getText().toString();
        // 判断单播或组播
        int length = contString.length();
        if (length > 0 && length <= 70) {
            mEtContent.setText("");
            //封装要发送的数据
            // 判断是群组消息
            if (isGroupMsg) {
                // 封装发送数据


                // 普通消息
            } else {
                // 封装发送数据
                msg.setSenderNumber(friend.getUserId());
                msg.setSenderName(friend.getName());
                msg.setTime(CommUtil.getDate());
                msg.setContent(contString);
                msg.setType(Constants.MESSAGE_TYPE_TEXT);
                msg.setIsRead(Constants.MESSAGE_HAVE_READ);
                // 判断有没有网,设置消息来源
                boolean netConnect = NetWorkUtil.isNetConnect(
                        NetWorkUtil.getNetWorkState(MyApplication.getContextObject()));
                if (netConnect) {
                    msg.setFrom(DataProcessingUtil.FROM_TYPE_INTENT);
                } else {
                    msg.setFrom(DataProcessingUtil.FROM_TYPE_BEIDOU);
                }
                // 设置状态
                msg.setStatus(Constants.MESSAGE_STATE_SENDING);
                // id放到数据库后才明确,状态要在得到反馈后明确
                // 信息存入数据库
                isSuccess = dao.addDataToMessage(msg);
            }
            // 更新界面
            refreshListView();
        }
    }

    /**
     * 重新填充集合,更新界面
     */

    private void refreshListView() {
        mList.clear();
        mList.addAll(dao.queryCommonMessageByUserId(friend.getUserId()));
        mAdapter.notifyDataSetChanged();
        if (mList.size() >= 1) {
            mListView.setSelection(mList.size() - 1);
        }
    }

    /**
     * 判断点击的是否是EditText区域
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            } else {
                //mListView.setSelection(mList.size()-1);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "start");

        // 注册广播接收者
        IntentFilter filter = new IntentFilter();
        // 接收群号action
        filter.addAction(Constants.RECEIVE_GROUPNUMBER_UPDATEUI_ACTION);
        // 接收语音压缩完成  action
        filter.addAction(Constants.ACTION_SPX_OK);
        registerReceiver(mReceiver, filter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTvContent.getVisibility() == View.VISIBLE) {
            setKeyboardLoc(mRootView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "stop");
        // 取消广播接收者
        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭数据库
        if (dao != null) {
            dao.close();
        }
    }
}
