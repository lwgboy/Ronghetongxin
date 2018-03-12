package com.zhbd.beidoucommunication.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhbd.beidoucommunication.R;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.domain.BaseMessage;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.domain.GroupMessage;
import com.zhbd.beidoucommunication.manager.MediaPlayerManager;
import com.zhbd.beidoucommunication.utils.AudioFileUtils;

import java.util.List;

public class ChatMsgAdapter extends BaseAdapter {

    private ViewHolderText viewHolder_text;

    public static interface IMsgViewType {
        int RECEIVER_TEXT_MSG = 0;// 收到对方的文字消息
        int SEND_TEXT_MSG = 1;// 自己发送出去的文字消息
        int RECEIVER_VOICE_MSG = 2;// 收到对方语音消息
        int SEND_VOICE_MSG = 3;// 发送给对方的语音消息
    }

    private static final int ITEMCOUNT = 4;// 消息类型的总数
    private List<BaseMessage> mList;// 消息对象数组
    private LayoutInflater mInflater;

    // 语音条的最大长度和最小长度
    private int mMaxItemWidth;
    private int mMinItenWidth;

    // 标记是否正在播放
    private boolean isPlay;

    public ChatMsgAdapter(Context context, List<BaseMessage> mList) {
        this.mList = mList;
        for (int i = 0; i < mList.size(); i++) {
            //Log.e("abc", i + ":" + mList.get(i).toString());
        }
        mInflater = LayoutInflater.from(context);
        // 动态测量窗口宽度,用于计算语音条的长度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItenWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    public int getCount() {
        return mList.size();
    }

    public BaseMessage getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * 得到Item的类型，是对方发过来的消息，还是自己发送出去的
     */
    public int getItemViewType(int position) {
        BaseMessage message = mList.get(position);
        // 判断是群组消息还是普通消息
        if (message instanceof CommonMessage) {
            CommonMessage msg = (CommonMessage) message;
            //收到的消息
            if (msg.getStatus() == Constants.MESSAGE_STATE_RECEIVER) {
                // 收到文字消息
                if (msg.getType() == Constants.MESSAGE_TYPE_TEXT) {
                    return IMsgViewType.RECEIVER_TEXT_MSG;
                    // 收到语音消息
                } else {
                    return IMsgViewType.RECEIVER_VOICE_MSG;
                }

            } else {//自己发送的消息
                //发送文字消息
                if (msg.getType() == Constants.MESSAGE_TYPE_TEXT) {
                    return IMsgViewType.SEND_TEXT_MSG;
                } else {
                    //发送语音消息
                    return IMsgViewType.SEND_VOICE_MSG;
                }
            }
            // 群组消息
        } else {
            GroupMessage msg = (GroupMessage) message;
            //收到的消息
            if (msg.getStatus() == Constants.MESSAGE_STATE_RECEIVER) {
                // 收到文字消息
                if (msg.getType() == Constants.MESSAGE_TYPE_TEXT) {
                    return IMsgViewType.RECEIVER_TEXT_MSG;
                    // 收到语音消息
                } else {
                    return IMsgViewType.RECEIVER_VOICE_MSG;
                }

            } else {//自己发送的消息
                //发送文字消息
                if (msg.getType() == Constants.MESSAGE_TYPE_TEXT) {
                    return IMsgViewType.SEND_TEXT_MSG;
                } else {
                    //发送语音消息
                    return IMsgViewType.SEND_VOICE_MSG;
                }
            }
        }
    }

    /**
     * Item类型的总数
     */
    public int getViewTypeCount() {
        return ITEMCOUNT;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        //Log.e("error",mList.size()+"");
        final BaseMessage message = getItem(position);
        if (message instanceof CommonMessage) {
            final CommonMessage commonMessage = (CommonMessage) message;

            final int msg_state = commonMessage.getStatus();
            int msg_type = commonMessage.getType();

            final boolean isReceive = (msg_state == Constants.MESSAGE_STATE_RECEIVER);
            boolean isText = (msg_type == Constants.MESSAGE_TYPE_TEXT);

            ViewHolderVoice viewHolder_voice = null;
            // 复用convertVIew
            if (convertView == null) {
                // 判断状态是发送还是接收,返回条目类型
                if (isReceive) {
                    // 判断类型是文本还是语音, 返回条目类型
                    if (isText) {
                        convertView = mInflater.inflate(
                                R.layout.row_received_message, null);
                    } else {
                        convertView = mInflater.inflate(
                                R.layout.row_received_voice, null);
                    }

                } else {
                    if (isText) {
                        convertView = mInflater.inflate(
                                R.layout.row_sent_message, null);
                    } else {
                        convertView = mInflater.inflate(
                                R.layout.row_sent_voice, null);
                    }
                }
                // 找到对应布局的对应控件
                if (isText) {
                    viewHolder_text = new ViewHolderText();
                    viewHolder_text.tvSendTime = (TextView) convertView
                            .findViewById(R.id.tv_date_time);
                    viewHolder_text.tvContent = (TextView) convertView
                            .findViewById(R.id.tv_chatcontent);
                    viewHolder_text.tvMsgFrom = (TextView) convertView
                            .findViewById(R.id.tv_msg_from);
                    viewHolder_text.ivStatus = (ImageView) convertView
                            .findViewById(R.id.iv_msg_status);
                    viewHolder_text.pbStatus = (ProgressBar) convertView
                            .findViewById(R.id.pb_sending);
                    convertView.setTag(viewHolder_text);

                }
                // 语音消息
                else {
                    viewHolder_voice = new ViewHolderVoice();
                    viewHolder_voice.rlVoice = (RelativeLayout) convertView
                            .findViewById(R.id.rl_voice);
                    viewHolder_voice.tvSendTime = (TextView) convertView
                            .findViewById(R.id.tv_date_time);
                    viewHolder_voice.ivUnRead = (ImageView) convertView
                            .findViewById(R.id.iv_unread_voice);
                    viewHolder_voice.tvLength = (TextView) convertView
                            .findViewById(R.id.tv_length);
                    viewHolder_voice.ivVoice = (ImageView) convertView
                            .findViewById(R.id.iv_voice);
                    convertView.setTag(viewHolder_voice);
                }
                // 设置tag
            } else {
                if (isText) {
                    viewHolder_text = (ViewHolderText) convertView.getTag();
                } else {
                    viewHolder_voice = (ViewHolderVoice) convertView.getTag();
                }
            }

            if (isText) {
                viewHolder_text.tvSendTime.setText(commonMessage.getTime());
                viewHolder_text.tvContent.setText(commonMessage.getContent());
                // TODO 语音其实也是需要的,避免出错,就先写到这里
//                int status = commonMessage.getStatus();
                switch (msg_state) {
                    case Constants.MESSAGE_STATE_SENDING:
                        sending();
                        break;
                    case Constants.MESSAGE_STATE_SEND_SUCCESS:
                        sendSuccess();
                        break;
                    case Constants.MESSAGE_STATE_SEND_FAILURE:
                        sendFail();
                        break;
                }
                // 发送失败点击重发


            } else {
                //发送时间
                viewHolder_voice.tvSendTime.setText(commonMessage.getTime());

                ViewGroup.LayoutParams lp = viewHolder_voice.rlVoice.getLayoutParams();
                // 长度,动画和时间设置
                lp.width = (int) (mMinItenWidth + (mMaxItemWidth / 60f * commonMessage.getSecond()));
                // 设置语音框宽度
                viewHolder_voice.rlVoice.setLayoutParams(lp);
                final ViewHolderVoice finalViewHolder_voice = viewHolder_voice;
                viewHolder_voice.rlVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 播放动画
                        final ImageView animView = finalViewHolder_voice.ivVoice;
                        if (isReceive) {
                            animView.setImageResource(R.drawable.anim_receiver_play_voice);
                        } else {
                            animView.setImageResource(R.drawable.anim_play_voice);
                        }
                        final AnimationDrawable anim = (AnimationDrawable) animView.getDrawable();
                        // 判断如果正在播放,则暂停播放,暂停动画
                        if (isPlay) {
                            MediaPlayerManager.pause();
                            MediaPlayerManager.release();
                            anim.stop();
                            if (msg_state == Constants.MESSAGE_STATE_RECEIVER) {
                                animView.setImageResource(R.drawable.chatfrom_voice_playing);
                            } else {
                                animView.setImageResource(R.drawable.chatto_voice_playing);
                            }
                        } else {
                            anim.start();
                            // 播放音频
                            // 参数1是是否是WAV文件,参数2是文件地址,参数3是播放完成的回调
                            MediaPlayer.OnCompletionListener listener = new
                                    MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            anim.stop();
                                            if (msg_state == Constants.MESSAGE_STATE_RECEIVER) {
                                                animView.setImageResource(R.drawable.chatfrom_voice_playing);
                                            } else {
                                                animView.setImageResource(R.drawable.chatto_voice_playing);
                                            }
                                            // 释放MediaPlayer资源
                                            MediaPlayerManager.release();
                                            isPlay = false;
                                        }
                                    };
                            if (isReceive) {
                                MediaPlayerManager.playSound(true, message.getContent(), listener);
                            } else {
                                MediaPlayerManager.playSound(false, message.getContent(), listener);
                            }
                        }
                        // 改变播放标记
                        isPlay = !isPlay;
                    }
                });
                viewHolder_voice.tvLength.setText(commonMessage.getSecond() + "\"");
            }
//              --------------------------------------------
            // 是群组的情况
        } else {
//            Log.e("error","走了群组");
//            final GroupMessage groupMsg = (GroupMessage) getItem(position);
//            int msg_state = groupMsg.getStatus();
//            int msg_type = groupMsg.getType();
//
//            boolean isReceive = (msg_state == Constants.MESSAGE_STATE_RECEIVER);
//            boolean isText = (msg_type == Constants.MESSAGE_TYPE_TEXT);
//
//            ViewHolderText viewHolder_text = null;
//            ViewHolderVoice viewHolder_voice = null;
//            // 复用convertVIew
//            if (convertView == null) {
//                // 判断状态是发送还是接收,返回条目类型
//                if (isReceive) {
//
//                    // 判断类型是文本还是语音, 返回条目类型
//                    if (isText) {
//                        convertView = mInflater.inflate(
//                                R.layout.row_received_message, null);
//                    } else {
//                        convertView = mInflater.inflate(
//                                R.layout.row_received_message, null);
//                    }
//
//                } else {
//
//                    if (isText) {
//                        convertView = mInflater.inflate(
//                                R.layout.row_sent_message, null);
//                    } else {
//                        convertView = mInflater.inflate(
//                                R.layout.row_sent_message, null);
//                    }
//                }
//                // 找到对应布局的对应控件
//                if (isText) {
//                    viewHolder_text = new ViewHolderText();
//                    viewHolder_text.tvSendTime = (TextView) convertView
//                            .findViewById(R.id.tv_date_time);
//                    viewHolder_text.tvUserName = (TextView) convertView
//                            .findViewById(R.id.tv_user_name);
//                    viewHolder_text.tvContent = (TextView) convertView
//                            .findViewById(R.id.tv_chatcontent);
//                    convertView.setTag(viewHolder_text);
//                }
////                else {
////                    viewHolder_voice = new ViewHolderVoice();
////                    viewHolder_voice.tvSendTime = (TextView) convertView
////                            .findViewById(R.id.tv_sendtime);
////                    viewHolder_voice.tvUserName = (TextView) convertView
////                            .findViewById(R.id.tv_username);
////                    viewHolder_voice.flRecorderLength = (FrameLayout) convertView
////                            .findViewById(R.id.fl_recorder_length);
////                    viewHolder_voice.viewRecorderAnim = convertView
////                            .findViewById(R.id.view_recorder_anim);
////                    viewHolder_voice.tvRecorderTime = (TextView) convertView
////                            .findViewById(R.id.tv_recorder_time);
////                    convertView.setTag(viewHolder_voice);
////                }
//                // 设置tag
//            } else {
//                if (isText) {
//                    viewHolder_text = (ViewHolderText) convertView.getTag();
//                } else {
//                    viewHolder_voice = (ViewHolderVoice) convertView.getTag();
//                }
//            }
//            if (isText) {
//                viewHolder_text.tvSendTime.setText(groupMsg.getTime());
//                if (msg_state == Constants.MESSAGE_STATE_RECEIVER) {
//                    viewHolder_text.tvUserName.setText(groupMsg.getSenderName());
//                } else {
//                    viewHolder_text.tvUserName.setText("我");
//                }
//                viewHolder_text.tvContent.setText(groupMsg.getContent());
//            } else {
//                //发送时间
//                viewHolder_voice.tvSendTime.setText(groupMsg.getTime());
//                // 用户名称
//                if (msg_state == Constants.MESSAGE_STATE_RECEIVER) {
//                    viewHolder_voice.tvUserName.setText(groupMsg.getSenderName());
//                } else {
//                    viewHolder_voice.tvUserName.setText("我");
//                }
//                ViewGroup.LayoutParams lp = viewHolder_voice.flRecorderLength.getLayoutParams();
//                // 长度,动画和时间设置
//                lp.width = (int) (mMinItenWidth + (mMaxItemWidth / 60f * groupMsg.getSecond()));
//                // 设置语音框宽度
//                viewHolder_voice.flRecorderLength.setLayoutParams(lp);
//                final ViewHolderVoice finalViewHolder_voice = viewHolder_voice;
////                viewHolder_voice.flRecorderLength.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        // 播放动画
////                        final View animView = finalViewHolder_voice.viewRecorderAnim;
////                        animView.setBackgroundResource(R.drawable.anim_play_voice);
////                        AnimationDrawable anim = (AnimationDrawable) animView.getBackground();
////                        anim.start();
////                        // 播放音频
////                        // 参数1是文件地址,参数2是播放完成的回调
////                        MediaPlayerManager.playSound(groupMsg.g_msg_content,
////                                new MediaPlayer.OnCompletionListener() {
////                                    @Override
////                                    public void onCompletion(MediaPlayer mp) {
////                                        animView.setBackgroundResource(R.drawable.send_voice_pic);
////                                    }
////                                });
////                    }
////                });
//                viewHolder_voice.tvRecorderTime.setText(groupMsg.getSecond() + "\"");
//            }
        }
        return convertView;
    }

    public class ViewHolderText {
        public TextView tvSendTime;
        public TextView tvContent;
        public TextView tvMsgFrom;
        public ImageView ivStatus;
        public ProgressBar pbStatus;
    }

    public class ViewHolderVoice {
        public RelativeLayout rlVoice;
        public ImageView ivVoice;
        public TextView tvLength;
        public TextView tvSendTime;
        public TextView tvUserName;
        public ImageView ivUnRead;
    }

    public void sending() {
        viewHolder_text.ivStatus.setVisibility(View.GONE);
        viewHolder_text.pbStatus.setVisibility(View.VISIBLE);
    }

    public void sendSuccess() {
        viewHolder_text.ivStatus.setVisibility(View.GONE);
        viewHolder_text.pbStatus.setVisibility(View.GONE);
    }

    public void sendFail() {
        viewHolder_text.ivStatus.setVisibility(View.VISIBLE);
        viewHolder_text.pbStatus.setVisibility(View.GONE);
    }
}