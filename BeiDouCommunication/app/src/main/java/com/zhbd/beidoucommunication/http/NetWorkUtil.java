package com.zhbd.beidoucommunication.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

/**
 * Created by zhangyaru on 2017/8/23.
 */

public class NetWorkUtil {

    public static final String TAG = "NetWorkUtil";


    public static final String SENT_SOCKET_STATUS = "sent_net_status";
    public static final String RECEIVE_SOCKET_STATUS = "receive_net_status";

    /**
     * ip地址
     */
    public static final String BASE_STATION_HOST = "172.19.191.1";
//    public static final String BASE_STATION_HOST = "192.168.2.165";
//    public static final String BASE_STATION_HOST = "192.168.10.102";
//    public static final String BASE_STATION_HOST = "192.168.173.1";
//    public static final String BASE_STATION_HOST = "192.168.173.1";
//    public static final String DATA_CENTER_HOST = "192.168.2.165";
//    public static final String DATA_CENTER_HOST = "192.168.173.1";
//    public static final String DATA_CENTER_HOST = "192.168.10.102";
//    public static final String DATA_CENTER_HOST = "60.207.69.45";
public static final String DATA_CENTER_HOST = "172.19.191.1";
//    public static final String HOST = "124.207.34.244";
    /**
     * 端口号
     */
//    public static final int PORT = 8888;
    public static final int PORT = 10003;

    //    private static Socket socket;
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
//    public synchronized static void getSocket() {
//        // 检查网络是否可用
//        boolean isAvailable = CommUtil.isNetWorkAvailable(MyApplication.getContextObject());
////        // 如果socket不为空并且当前网络状态和之前不符,就重新设置socket
//        if (isAvailable != netWorkIsAvailable && socket != null) {
//            socket = null;
//        }
//        if (socket == null) {
//            Log.e(TAG, "new Socket()");
//            try {
//                String host = null;
//                int port = 0;
//                // 可用则请求网络
//                if (isAvailable) {
//                    netWorkIsAvailable = true;
//                    host = DATA_CENTER_HOST;
//                    port = PORT;
//                    //socket = new Socket(DATA_CENTER_HOST, PORT);
//                    // 不可用连接短信基站
//                } else {
//                    netWorkIsAvailable = false;
//                    host = BASE_STATION_HOST;
//                    port = PORT;
//                    //socket = new Socket(BASE_STATION_HOST, PORT);
//                }
//                socket = new Socket(host, port);
//                if (!socket.isConnected()) {
//                    throw new ConnectException();
//                }
//            } catch (ConnectException e) {
//                MyApplication.getInstance().netCount++;
//
//                // 如果连了10次还没有连成功,就停止连接
//                if (MyApplication.getInstance().netCount < 10) {
//                    getSocket();
//                } else {
//                    SharedPrefUtil.putLong(MyApplication.getContextObject(),
//                            Constants.NETWORK_STOP_TIME, 0);
//                }
//                Log.e(TAG, "重新连接socket");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
////        Log.e("error","socket1:" + socket.getInetAddress());
//    }

    /**
     * TCP发送数据
     *
     * @param bys 要发送的数据
     */
    public static void connectServerWithTCPSocket(final byte[] bys) {
        Log.e(TAG, "connectServerWithTCPSocket");
        //Log.e(TAG, Arrays.toString(bys));
        Socket socket = MyApplication.getInstance().getReceiverSocket();
        try {
            if (socket != null) {
                os = socket.getOutputStream();
                os.write(bys);
                os.flush();
            }
        } catch (Exception e) {
            Log.e(TAG, "connectServerWithTCPSocket  socket断线了");
            e.printStackTrace();
        }
    }

    /**
     * 循环接收消息
     *
     * @return 接收到的消息
     */
    public static byte[] alwaysReceiver() {
        Socket receiveSocket = MyApplication.getInstance().getReceiverSocket();

        byte[] result = null;

        if (receiveSocket != null) {
//            Log.e(TAG, "接受消息");
            // 接受消息
            try {
                is = receiveSocket.getInputStream();

                DataInputStream dis = new DataInputStream(is);
                // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
                byte buffer[] = new byte[1024];
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
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "接受断线了");
            }
        }
        return result;
    }


    // 发送的Socket保持连接的方法(这个与发送执行在两条线程中)
//    public static void againConSentSocket() {
//        boolean status = SharedPrefUtil.getBoolean(MyApplication.getContextObject(), SENT_SOCKET_STATUS, false);
//
//        Socket sentSocket = MyApplication.getInstance().getSentSocket();
//        String address = "";
//        int port = PORT;
//        // 首先判断有没有网络
//        if (isNetConnect(getNetWorkState(MyApplication.getContextObject()))) {
//            // 连接数据中心
//            address = DATA_CENTER_HOST;
//        } else {
//            // 连接基站
//            address = BASE_STATION_HOST;
//        }
//
//        while (true) {
//            if (sentSocket == null) {
////                Log.e(TAG, "1");
//                try {
//                    sentSocket = new Socket();
//                    SocketAddress socketAddress = new InetSocketAddress(address, port);
//                    sentSocket.connect(socketAddress, 30000);
////                    Log.e(TAG, "2");
//                    SharedPrefUtil.putBoolean(MyApplication.getContextObject(), SENT_SOCKET_STATUS, true);
//                    status = true;
//                    while (status) {
////                        Log.e(TAG, "3");
//                        try {
//                            sentSocket.sendUrgentData(0xFF);
////                            Log.e(TAG, 0xFF + "--");
//                            MyApplication.getInstance().setSentSocket(sentSocket);
//                            Thread.sleep(100);
//                        } catch (Exception e1) {
//                            //Log.e(TAG, "服务器断开！！");
//                            MyApplication.getInstance().setSentSocket(null);
//                            sentSocket = null;
//                            status = false;
//                            SharedPrefUtil.putBoolean(MyApplication.getContextObject(), SENT_SOCKET_STATUS, false);
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "连接失败，3秒后重新连接" + e.getMessage());
//                }
////                try {
////                    Thread.sleep(3000);
////                } catch (Exception e) {
////                    Log.e(TAG, "4");
////                }
//            }
//        }
//    }

    public static void againConReceiveSocket() {
        boolean status = SharedPrefUtil.getBoolean(MyApplication.getContextObject(), RECEIVE_SOCKET_STATUS, true);
        Socket socket = MyApplication.getInstance().getReceiverSocket();

        String address = "";
        int port = PORT;
        // 首先判断有没有网络
        if (isNetConnect(getNetWorkState(MyApplication.getContextObject()))) {
            // 连接数据中心
            address = DATA_CENTER_HOST;
        } else {
            // 连接基站
            address = BASE_STATION_HOST;
        }
        while (true) {
            if (socket == null) {
//            if (!socket.isConnected()) {
                int count = 0;
                try {
                    socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress(address, port);
                    socket.connect(socketAddress);
                    status = true;
                    while (status) {
                        try {
//                            socket.sendUrgentData(0xEE);
                            os = socket.getOutputStream();
                            os.write(0XFF);
                            os.flush();
                            Log.e(TAG, "EE" + "---" + count);
                            count++;
                            MyApplication.getInstance().setReceiverSocket(socket);
                            SharedPrefUtil.putBoolean(MyApplication.getContextObject(), RECEIVE_SOCKET_STATUS, true);
                            // 60秒发送一次心跳
                            Thread.sleep(60 * 1000);
                        } catch (Exception e1) {
                            MyApplication.getInstance().setReceiverSocket(null);
//                            socket.connect(socketAddress);
                            socket = null;
                            status = false;
                            SharedPrefUtil.putBoolean(MyApplication.getContextObject(), RECEIVE_SOCKET_STATUS, false);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "连接失败，3秒后重新连接" + e.getMessage());
                }
            }
        }
    }

    /**
     * 关闭连接
     */
//    public void close() {
//        if (os != null) {
//            try {
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (socket.isInputShutdown()) { //判断输入流是否为打开状态
//            try {
//                socket.shutdownInput();  //关闭输入流
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (socket.isOutputShutdown()) {  //判断输出流是否为打开状态
//            try {
//                socket.shutdownOutput(); //关闭输出流（如果是在给对方发送数据，发送完毕之后需要关闭输出，否则对方的InputStream可能会一直在等待状态）
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (socket.isConnected()) {  //判断是否为连接状态
//            try {
//                socket.close();  //关闭socket
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//    }

}
