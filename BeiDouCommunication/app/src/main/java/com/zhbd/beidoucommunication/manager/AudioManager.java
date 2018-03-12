package com.zhbd.beidoucommunication.manager;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.utils.AudioFileUtils;
import com.zhbd.beidoucommunication.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @param
 * @author ldm
 * @description 录音管理工具类
 * @time 2016/6/25 9:39
 */
public class AudioManager {
    private final int maxSecond = 1000 * 8;

    //AudioRecord: 主要是实现边录边播（AudioRecord+AudioTrack）以及对音频的实时处理。
    private AudioRecord mAudioRecord;
    //录音文件
    private String mDir;
    //当前录音文件目录
    private String mCurrentFilePath;
    //单例模式
    private static AudioManager mInstance;
    //是否准备好
    private boolean isPrepare;
    private FileOutputStream mFos;
    // 录音状态,保证多线程内存同步,避免出问题
    public volatile boolean mIsRecording;

    // 播放状态,保证多线程内存同步,避免出问题
    public volatile boolean mIsPlaying;

    // 线程池
    ExecutorService mExecutorService;
    // 线程间通信
    Handler mMainThreadHandler;

    // 缓冲区读取文件,不能太大,避免OOM
    private byte[] mBuffer;

    // 记录开始和结束时间
    private long mStartRecordTime, mStopRecordTime;


    // 缓冲区大小
    private static final int BUFFER_SIZE = 500;
    private File mAudioFile;
    private boolean first = true;
    private int read;

    private boolean isPause = true;

    //私有构造方法
    private AudioManager(String dir) {
        mDir = dir;
    }

    //对外公布获取实例的方法
    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * @param
     * @author ldm
     * @description 录音准备工作完成回调接口
     * @time 2016/6/25 11:14
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mAudioStateListener;

    /**
     * @param
     * @description 供外部类调用的设置回调方法
     * @author ldm
     * @time 2016/6/25 11:14
     */
    public void setOnAudioStateListener(AudioStateListener listener) {
        mAudioStateListener = listener;
    }

    /**
     * @param
     * @description 录音准备工作
     * @author ldm
     * @time 2016/6/25 11:15
     */
    public void prepareAudio() {

        // 单线程
        mExecutorService = Executors.newSingleThreadExecutor();
        mMainThreadHandler = new Handler(Looper.getMainLooper());

        mBuffer = new byte[BUFFER_SIZE];

        isPrepare = false;
        File dir = new File(mDir);
        if (!dir.exists()) {
            dir.mkdirs();//文件不存在，则创建文件
        }
        String fileName = generateFileName();
        mAudioFile = new File(dir, fileName);
        mCurrentFilePath = mAudioFile.getAbsolutePath();
        //创建文件输出流
//        Log.e("error", mAudioFile.getAbsolutePath());
        try {
            mFos = new FileOutputStream(mAudioFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 配置AudioRecorder
        // 从麦克风采集
        int audioSource = MediaRecorder.AudioSource.MIC;
        // 所有安卓系统都支持的频率
        int sampleRate = 8000;
        // 单声道输入
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        // PCM 16 是所有安卓系统都支持
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
//            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        // 计算AudioRecord内部buffer最小的大小啊
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        // buffer 不能小于最低要求,也不能小于每次读取的大小
//            mAudioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, BUFFER_SIZE);
        mAudioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, Math.max(minBufferSize, BUFFER_SIZE));

    }

    /**
     * @deprecated 开始录音
     */
    public void startTheAudio() {
        if (mAudioRecord == null) {
            isPrepare = false;
            return;
        } else {
            isPrepare = true;
            isPause = false;
        }

        //开始录制音频
        // 改变状态
        mIsRecording = true;
        // 提交后台任务,执行开始录音逻辑
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                // 执行开始录音逻辑,失败提醒用户
                if (!startRecord()) {
                    recordFail();
                }
            }
        });
        // 准备完成
//        isPrepare = true;
//        if (mAudioStateListener != null) {
//            mAudioStateListener.wellPrepared();
//        }
//            SharedPrefUtil.putBoolean(MyApplication.getInstance(), "isFirstRun", false);
//        }
    }

    /**
     * 开始录音
     *
     * @return
     */
    private boolean startRecord() {

        try {
            // 开始录音
            mAudioRecord.startRecording();

            // 记录开始录音时间,记录时长
            mStartRecordTime = System.currentTimeMillis();
            // 循环读取数据,写到输出流中
            while (mIsRecording) {
//                Log.e("error", "eeeeeeeeeeeeee");  as
                // 只要还在录音状态,就一直读取数据
                read = mAudioRecord.read(mBuffer, 0, BUFFER_SIZE);
//                Log.e("error", "1");
                if (read > 0) {
                    if (isPrepare) {
//                    Log.e("error", "fffffffffffffff");
                        Timer timer = new Timer();
                        if (first)//first 这里是为了避免在while(true)中开启多个任务，获取音量的任务只需要一个就够了
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!isPause) {
//                                    Log.e("error", "gggggggggggg");
                                        long v = 0;
                                        // 将 buffer 内容取出，进行平方和运算
                                        for (int i = 0; i < mBuffer.length; i++) {
                                            v += mBuffer[i] * mBuffer[i];
                                        }
                                        // 平方和除以数据总长度，得到音量大小。
                                        double mean = v / (double) read;
                                        double volume = 10 * Math.log10(mean);
//                                        Log.e("error", (int) volume + "--123");
                                    }
//                        ChatMessageActivity..runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                MainActivity.instance.voice.setText(volume + "");
//                            }
//                        });
                                }
                            }, 0, 200);
                        first = false;
                    }
//                    Log.e("error", "2");
                    long l = System.currentTimeMillis();
                    long l1 = l - mStartRecordTime;
                    //Log.e("err", l1 + "===========");
                    if (l1 > maxSecond) {
//                        Log.e("error", "3");
                        mIsRecording = false;
                        // 时间限制,回调方法
//                        mMainThreadHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mStopRecordTime - mStartRecordTime >= maxSecond) {
//                                    ToastUtils.showToast(MyApplication.getContextObject(), "最多录制8秒钟");
//                                    //Log.e("error", "录音成功");
//                                }
//                            }
//
//                        });
                    }
                    // 读取成功,写入文件
//                    Log.e("error", "10");
                    mFos.write(mBuffer, 0, read);
//                    Log.e("error", "11");
                    //Log.e("error", mBuffer.length + "----" + read);
                } else {
//                    Log.e("error", "4");
                    // 读取失败,返回false提示用户
                    break;
//                    return false;
                }
            }
            // 退出循环,停止录音,释放录音资源
            return stopRecord();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            // 捕获异常,避免闪退,返回false提示用户
            return false;
        } finally {
            // 释放资源
            if (mAudioRecord != null) {
                mAudioRecord.release();
            }
        }
    }

    /**
     * 结束录音逻辑
     *
     * @return
     */
    private boolean stopRecord() {
//        Log.e("error", "stopRecord");
        isPause = true;
        try {
            // 停止录音,关闭文件输出流
//            mAudioRecord.stop();
//            mAudioRecord.release();
            mAudioRecord = null;
            mFos.close();
        } catch (IOException e) {
            // 捕获异常,避免闪退
//            Log.e("error", "5");
            e.printStackTrace();
            return false;
        }

        // 记录结束时间,统计录音时长
        mStopRecordTime = System.currentTimeMillis();
//        Log.e("error", "6");
        // 大于1秒算成功,主线程更新UI显示
        double second = (mStopRecordTime - mStartRecordTime) / 1000.0;
//        Log.e("err", second + "-=-=-=-=-=-=-=");
        if (second > 0.8) {
//            Log.e("error", "7");
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mStopRecordTime - mStartRecordTime >= maxSecond) {
                        ToastUtils.showToast(MyApplication.getContextObject(), "最多录制8秒钟");
                        Log.e("error", "录音成功");
                    }
                }

            });
//            Log.e("error", "8");
            // 压缩录音文件
            compressFile();

//            Log.e("error", "9");
        } else {
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(MyApplication.getContextObject(), "录音时间太短");
                    // 删除产生的文件
                    File file = new File(mCurrentFilePath);
                    if (file != null) {
                        file.delete();
                    }
                }
            });
        }
        return true;
    }

    /**
     * 压缩录音文件
     */
    private void compressFile() {
        try {
            String fileName = mAudioFile.getName()
                    .substring(0, mAudioFile.getName().lastIndexOf('.'));
            File spxFile = new File(mDir + "/" + fileName + ".spx");
            spxFile.createNewFile();

//            Log.e("error", "b:" + spxFile.getAbsolutePath());
            AudioFileUtils.raw2spx(mAudioFile, spxFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 为避免没有压缩完成就回调onFinish方法,这里完成后发广播告诉Activity
        Intent intent = new Intent(Constants.ACTION_SPX_OK);
        MyApplication.getContextObject().sendBroadcast(intent);
    }

    /**
     * 录音发生错误的处理
     */
    private void recordFail() {
//        Log.e("error", "recordFail");
        // 主线程改变ui
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                //ToastUtils.showToast(MyApplication.getContextObject(), "录音失败");
                // 重置录音状态,以及UI状态
                mIsRecording = false;
                isPause = true;
                // 删除产生的文件
                if (mCurrentFilePath != null) {
                    File file = new File(mCurrentFilePath);
                    if (file != null) {
                        file.delete();
                    }
                }
            }
        });
    }


    /**
     * @param
     * @description 随机生成录音文件名称
     * @author ldm
     * @time 2016/6/25 、
     */

    private String generateFileName() {
        //随机生成不同的UUID
//        return UUID.randomUUID().toString() + ".raw";
        return System.currentTimeMillis() + ".raw";
    }

    /**
     * @param
     * @description 获取音量值
     * @author ldm
     * @time 2016/6/25 9:49
     */
    public int getVoiceLevel() {

        return 1;
    }

    /**
     * @param
     * @description 释放资源
     * @author ldm
     * @time 2016/6/25 9:50
     */
    public void release() {
        if (mAudioRecord != null) {
            //mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    /**
     * @param
     * @description 录音取消
     * @author ldm
     * @time 2016/6/25 9:51
     */
    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            //取消录音后删除对应文件
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }

    }

    /**
     * @param
     * @description 获取当前文件路径
     * @author ldm
     * @time 2016/6/25 9:51
     */
    public String getCurrentFilePath() {

        return mCurrentFilePath;
    }
}
