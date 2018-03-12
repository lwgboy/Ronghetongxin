package com.zhbd.beidoucommunication.utils;

import android.app.Activity;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhangyaru on 2017/10/16.
 */

public class MsgNumberFileUtils {
    // 写到本地文件
    private static String dir = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/BeiDouCommunication/msgLog.txt";

    /**
     * 保存用户输入的内容到文件
     */
    public static void writeToTxt(String content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dir, true);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
