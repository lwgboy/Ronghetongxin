package com.zhbd.beidoucommunication.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by zhangyaru on 2017/8/23.
 */

public class NetWorkUtil_copy {

    public static final String TAG = "NetWorkUtil";

    /**
     * ip地址
     */
//    public static final String DATA_CENTER_HOST = "192.168.10.105";
//    public static final String BASE_STATION_HOST = "192.168.1.103";
//    public static final String BASE_STATION_HOST = "192.168.2.165";
    public static final String BASE_STATION_HOST = "192.168.1.102";
    //    public static final String BASE_STATION_HOST = "192.168.2.165";
//        public static final String BASE_STATION_HOST = "192.168.173.1";
//    public static final String DATA_CENTER_HOST = "192.168.2.165";
//    public static final String DATA_CENTER_HOST = "192.168.173.1";
    public static final String DATA_CENTER_HOST = "192.168.1.102";
//    public static final String DATA_CENTER_HOST = "60.207.69.45";
//    public static final String HOST = "124.207.34.244";
    /**
     * 端口号
     */
//    public static final int PORT = 8888;
    public static final int PORT = 10003;

    private static Socket socket;
    private static OutputStream os;
    private static InputStream is;

    private static boolean netWorkIsAvailable;

    /**
     * 创建连接
     */
//    static {
//        try {
//            socket = new Socket(HOST, PORT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    /**
     * 检测网络状态
     *
     * @return
     */
    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public static boolean isNetConnect(int netMobile) {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;

        }
        return false;
    }



    /**
     * 单例模式返回socket对象
     */
    public synchronized static void getSocket() {
        // 检查网络是否可用
        boolean isAvailable = CommUtil.isNetWorkAvailable(MyApplication.getContextObject());
//        // 如果socket不为空并且当前网络状态和之前不符,就重新设置socket
        if (isAvailable != netWorkIsAvailable && socket != null) {
            socket = null;
        }
        if (socket == null) {
            Log.e(TAG,"new Socket()");
            try {
                String host = null;
                int port = 0;
                // 可用则请求网络
                if (isAvailable) {
                    netWorkIsAvailable = true;
                    host = DATA_CENTER_HOST;
                    port = PORT;
                    //socket = new Socket(DATA_CENTER_HOST, PORT);
                    // 不可用连接短信基站
                } else {
                    netWorkIsAvailable = false;
                    host = BASE_STATION_HOST;
                    port = PORT;
                    //socket = new Socket(BASE_STATION_HOST, PORT);
                }
                socket = new Socket(host, port);
                if (!socket.isConnected()) {
                    throw new ConnectException();
                }
            } catch (ConnectException e) {
                MyApplication.getInstance().netCount++;

                // 如果连了10次还没有连成功,就停止连接
                if (MyApplication.getInstance().netCount < 10) {
                    getSocket();
                } else {
                    SharedPrefUtil.putLong(MyApplication.getContextObject(),
                            Constants.NETWORK_STOP_TIME, 0);
                }
                Log.e(TAG,"重新连接socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        Log.e("error","socket1:" + socket.getInetAddress());
    }

    /**
     * 获取当前消息的接收来自哪里
     *
     * @return
     */
//    public static int getFromType() {
//        int type = 0;
//        if (socket != null) {
//            String hostAddress = socket.getInetAddress().getHostAddress();
//            if (DATA_CENTER_HOST.equals(hostAddress)) {
//                type = FROM_TYPE_INTENT;
//            } else if (BASE_STATION_HOST.equals(hostAddress)) {
//                type = FROM_TYPE_BEIDOU;
//            }
//        }
//        return type;
//    }

    /**
     * TCP发送数据
     *
     * @param bys 要发送的数据
     */
    public static void connectServerWithTCPSocket(final byte[] bys) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //Log.e(TAG, "发文字消息");

                    getSocket();
                    //Log.e(TAG, "获取到了socket");
//                    Log.e("error", "socket2:" + socket.getInetAddress());

                    //Log.e("error", "Network.connect:" + "getSentSocket()");
                    // 判断客户端和服务器是否连接成功
                    //Log.e("error", "Network.connect:" + Arrays.toString(bys));
                    if (socket == null) {
//                        Log.e("error", "socket:null");
                        throw new ConnectException();
                    }
                    boolean isConnected = socket.isConnected();
                    if (isConnected) {
                        //Log.e("error", "连接成功");
                        /** 或创建一个报文，使用BufferedWriter写入 **/
                        os = socket.getOutputStream();
                        os.write(bys);
                        os.flush();
                    }
                } catch (ConnectException e) {
                    Log.e("error", "断线了");
                    //getSentSocket();
                    connectServerWithTCPSocket(bys);
                    Log.e("error", "重连了");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 循环接收消息
     *
     * @return 接收到的消息
     */
    public static byte[] alwaysReceiver() {
//        Log.e("error","进来了接收消息");
        byte[] result = null;
        try {
            if (socket != null && socket.isConnected()) {
                // 设置超时时间
                is = socket.getInputStream();
                DataInputStream dis = new DataInputStream(is);
                // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
                byte buffer[] = new byte[10240];
                int temp = 0;
                // 判断流中是否有内容
                if (dis.available() != 0) {
                    // 只读取一次文件
                    temp = dis.read(buffer);
                    // 把数据写入到OuputStream对象中
                    result = new byte[temp];
                    // 把长度1024的数组中有内容的取出来放到小数组中
                    for (int i = 0; i < result.length; i++) {
                        result[i] = buffer[i];
                    }
                }
            } else {
                getSocket();
            }
//        } catch (ConnectException e) {
//            Log.e("error", "断线了");
//            getSentSocket();
//            alwaysReceiver();
//            Log.e("error", "重连了");
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void againConnect() {
//        getSentSocket();
        if (socket != null) {
            try {
                socket.sendUrgentData(0xFF);
            } catch (Exception ex) {
                socket = null;
                getSocket();
                Log.e("error", "断线了,重新连接");
            }
        }
    }

    public static void startThreadSocket() {
        Thread thread;//循环发送心跳包的线程
        final OutputStream outputStream;//输出流，用于发送心跳
        getSocket();
        if (socket == null) {
            return;
        }
        try {

            if (!socket.getKeepAlive()) {
                socket.setKeepAlive(true);//true，若长时间没有连接则断开
            }
            if (!socket.getOOBInline()) {
                socket.setOOBInline(true);//true,允许发送紧急数据，不做处理
            }
            outputStream = socket.getOutputStream();//获得socket的输出流
            final String socketContent = "Heart Beat+\n";
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(3 * 1000);//20s发送一次心跳
                            outputStream.write(socketContent.getBytes("GBK"));
                            outputStream.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket.isInputShutdown()) { //判断输入流是否为打开状态
            try {
                socket.shutdownInput();  //关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket.isOutputShutdown()) {  //判断输出流是否为打开状态
            try {
                socket.shutdownOutput(); //关闭输出流（如果是在给对方发送数据，发送完毕之后需要关闭输出，否则对方的InputStream可能会一直在等待状态）
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket.isConnected()) {  //判断是否为连接状态
            try {
                socket.close();  //关闭socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
