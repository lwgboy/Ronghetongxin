package com.zhbd.beidoucommunication.manager;


import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.utils.AudioFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @param
 * @author ldm
 * @description 播放声音工具类
 * @time 2016/6/25 11:29
 */
public class MediaPlayerManager {
    //播放音频API类：MediaPlayer
    private static MediaPlayer mMediaPlayer;
    //是否暂停
    private static boolean isPause;

    /**
     * @param filePath：文件路径 onCompletionListener：播放完成监听
     * @description 播放声音
     * @author ldm
     * @time 2016/6/25 11:30
     */
    public static boolean playSound(boolean isWav, String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }

        //mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
        String wavName = null;
        if (isWav) {
            wavName = filePath;
        } else {
            wavName = filePath.substring(0, filePath.lastIndexOf('.')) + ".wav";
            AudioFileUtils.raw2Wav(filePath, wavName, 160);
        }
        try {
            File file = new File(wavName);
            FileInputStream fis = new FileInputStream(file);

            mMediaPlayer.setDataSource(fis.getFD());
//            mMediaPlayer.setDataSource(filePath.substring(0, filePath.lastIndexOf('.')) + ".wav");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param
     * @description 暂停播放
     * @author ldm
     * @time 2016/6/25 11:31
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * @param
     * @description 重新播放
     * @author ldm
     * @time 2016/6/25 11:31
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * @param
     * @description 释放操作
     * @author ldm
     * @time 2016/6/25 11:32
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
