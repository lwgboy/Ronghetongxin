package com.zhbd.beidoucommunication.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.domain.CommonMessage;
import com.zhbd.beidoucommunication.domain.EmailMessage;
import com.zhbd.beidoucommunication.domain.Friend;
import com.zhbd.beidoucommunication.domain.GroupMessage;
import com.zhbd.beidoucommunication.domain.IcCardInfo;
import com.zhbd.beidoucommunication.domain.SmsMessage;
import com.zhbd.beidoucommunication.domain.User;
import com.zhbd.beidoucommunication.event.DaoRowIdEvent;
import com.zhbd.beidoucommunication.utils.DataProcessingUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/29.
 */

public class DatabaseDao {
    private DatabaseHelper mMyDBHelper;
    private SQLiteDatabase db;
    /**
     * 消息表
     */
    private final String tab_common_message_name = "message";
    /**
     * 群组消息表
     */
    private final String tab_group_message_name = "group_message";
    /**
     * ic卡表
     */
    private final String tab_icinfo_name = "ic_info";
    /**
     * 群租表
     */
    private final String tab_group_name = "my_group";
    /**
     * 联系人表
     */
    private final String tab_friends_name = "friends";

    /**
     * 短信信息表
     */
    private final String tab_sms_name = "sms";

    /**
     * 邮件表
     */
    private final String tab_email_name = "tab_email";

    /**
     * 用户表
     */
    private final String tab_user_name = "user";


    private DatabaseDao(Context context, int userId) {
        mMyDBHelper = new DatabaseHelper(context, userId);
    }

    private static DatabaseDao dao;

    /**
     * 获取到实例
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseDao getInstance(Context context, int userId) {
        if (dao == null) {
            dao = new DatabaseDao(context, userId);
        }
        return dao;
    }

    /**
     * 为用户表表添加一条信息
     *
     * @param user
     * @return 是否添加成功
     */
    public boolean addDateToUser(User user) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", user.getUserId());
        values.put("nick_name", user.getNickName());
        values.put("phone_number", user.getPhoneNumber());
        values.put("ic_card_number", user.getIdCardNumber());
        values.put("password", user.getPassWord());
        long rowid = db.insert(tab_user_name, null, values);
        if (rowid >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 为ic信息表添加一条信息
     *
     * @param icInfo
     * @return 是否添加成功
     */
    public boolean addDateToIcInfo(IcCardInfo icInfo) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ic_number", icInfo.getIcNumber());
        values.put("is_control", icInfo.isControl() ? 1 : 0);
        values.put("is_quiesce", icInfo.isQuiesce() ? 0 : 1);
        values.put("service_frequency", icInfo.getServiceFrequency());
        values.put("grade", icInfo.getGrade());
        values.put("last_send_time", icInfo.getLastSendTime());
        long rowid = db.insert(tab_icinfo_name, null, values);
        if (rowid >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 为联系人表表添加一条信息
     *
     * @param friend
     * @return 是否添加成功
     */
    public boolean addDataToFriend(Friend friend) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", friend.getUserId());
        values.put("add_type", friend.getAddType());
        values.put("user_id", friend.getUserId());
        // 手机号码
        values.put("phone_number", friend.getPhoneNumber());
        // 身份证号
        values.put("id_card", friend.getIdCard());
        values.put("name", friend.getName());
        values.put("sim_number", friend.getSimNumber());
        long rowid = db.insert(tab_friends_name, null, values);
        if (rowid >= 0) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 向消息表中添加一条数据
     *
     * @param msg
     * @return 是否添加成功
     */
    public boolean addDataToMessage(CommonMessage msg) {
        //Log.e("rowid","调用了数据库添加方法");
        // 增删改查每一个方法都要得到数据库，然后操作完成后一定要关闭
        // getWritableDatabase(); 执行后数据库文件才会生成
        // 数据库文件利用DDMS可以查看，在 data/data/包名/databases 目录下即可查看
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender_name", msg.getSenderName());
        values.put("sender_number", msg.getSenderNumber());
        values.put("content", msg.getContent());
        values.put("second", msg.getSecond());
        values.put("time", msg.getTime());
        values.put("state", msg.getStatus());
        values.put("type", msg.getType());
        values.put("is_read", msg.getIsRead());
        // 插入数据
        long rowid = db.insert(tab_common_message_name, null, values);
        // 只有发送文字消息时才需要获得rowid,发送广播,接收的消息是不需要的
        if (Constants.MESSAGE_STATE_RECEIVER == msg.getStatus()) {
            if (rowid > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            // 查找最后一条信息,就是刚存进去的信息,得到_id
            Cursor cursor = db.rawQuery("select * from "
                    + tab_common_message_name + " order by _id desc limit 0,1", null);
            int _id = 0;
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    _id = cursor.getInt(cursor.getColumnIndex("_id"));
                }
            }
            // 发送事件
            EventBus.getDefault().post(new DaoRowIdEvent(_id));
            if (rowid >= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 向短信表中添加一条数据
     *
     * @param msg
     * @return 是否添加成功
     */
    public boolean addDataToSms(SmsMessage msg) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", msg.getSenderName());
        values.put("phone_number", msg.getPhoneNumber());
        values.put("content", msg.getContent());
        values.put("send_time", msg.getTime());
        values.put("state", msg.getStatus());
        values.put("is_read", msg.getIsRead());
        // 返回,显示数据添加在第几行
        // 加了现在连续添加了3行数据,突然删掉第三行,然后再添加一条数据返回的是4不是3
        // 因为自增长
        long rowid = db.insert(tab_sms_name, null, values);
        if (rowid >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 向邮件表中添加一条数据
     *
     * @param email
     * @return 是否添加成功
     */
    public boolean addDataToEmail(EmailMessage email) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("address", email.getAddress());
        values.put("content", email.getContent());
        values.put("send_time", email.getSendTime());
        // 返回,显示数据添加在第几行
        // 加了现在连续添加了3行数据,突然删掉第三行,然后再添加一条数据返回的是4不是3
        // 因为自增长
        long rowid = db.insert(tab_email_name, null, values);
        if (rowid >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据发送号码, 查找对应_id
     *
     * @param senderNumber 发送者id
     * @return
     */
    private int queryIdByUserId(int senderNumber) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM " + tab_common_message_name
                + " where sender_number=?", new String[]{String.valueOf(senderNumber)});
        int _id = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                _id = cursor.getInt(cursor.getColumnIndex("_id"));
            }
        }
        cursor.close();
        return _id;
    }

    /**
     * 根据USERID查找未读的消息数量
     *
     * @param userId
     * @return   未读消息条数
     */
    public int queryNoReadCountByUserId(int userId) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_common_message_name
                + " where sender_number=? and is_read=?", new String[]{
                String.valueOf(userId), String.valueOf(Constants.MESSAGE_NO_READ)});
        int count = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count++;
            }
        }
        cursor.close();
        return count;
    }

    /**
     * 根据手机号查找未读的短信数量
     *
     * @param phone
     * @return   未读消息条数
     */
    public int querySMSNoReadCountByPhone(String phone) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_sms_name
                + " where phone_number=? and is_read=?", new String[]{
                phone, String.valueOf(Constants.MESSAGE_NO_READ)});
        int count = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                count++;
            }
        }
        cursor.close();
        return count;
    }


    /**
     * 根据ic卡号删除某用户的所有消息记录
     *
     * @param icNumber ic号码
     * @return 是否删除成功
     */

    public boolean delDataforMsgInfoByIcNumber(int icNumber) {
        db = mMyDBHelper.getWritableDatabase();
        int delete = db.delete(tab_common_message_name, "sender_number=?", new String[]{icNumber + ""});
        if (delete == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据_Id删除好友
     *
     * @param _id
     * @return 是否删除成功
     */

    public boolean delDataforFriendBy_id(int _id) {
        db = mMyDBHelper.getWritableDatabase();
        int delete = db.delete(tab_friends_name, "_id=?", new String[]{_id + ""});
        if (delete == 0) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 查询消息数据库中去除重复的消息
     *
     * @return
     */
    public ArrayList<CommonMessage> queryMessageDistinct() {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.query(true, tab_common_message_name, null, null, null, "sender_number", null, null, null);
        ArrayList<CommonMessage> messages = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CommonMessage msg = new CommonMessage();
                msg.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                msg.setSenderNumber(cursor.getInt(cursor.getColumnIndex("sender_number")));
                msg.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
                msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msg.setTime(cursor.getString(cursor.getColumnIndex("time")));
                msg.setSecond(cursor.getInt(cursor.getColumnIndex("second")));
                msg.setStatus(cursor.getInt(cursor.getColumnIndex("state")));
                msg.setType(cursor.getInt(cursor.getColumnIndex("type")));
                msg.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                messages.add(msg);
            }
        }
        cursor.close();
        return messages;
    }


    /**
     * 查询消息数据库中去除重复的短信消息
     *
     * @return
     */
    public ArrayList<SmsMessage> querySmsDistinct() {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.query(true, tab_sms_name, null, null, null, "phone_number", null, null, null);
        ArrayList<SmsMessage> messages = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SmsMessage msg = new SmsMessage();
                msg.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                msg.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
                msg.setSenderName(cursor.getString(cursor.getColumnIndex("name")));
                msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msg.setTime(cursor.getString(cursor.getColumnIndex("send_time")));
                msg.setStatus(cursor.getInt(cursor.getColumnIndex("state")));
                msg.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                messages.add(msg);
            }
        }
        cursor.close();
        return messages;
    }

    /**
     * 去重复查询邮件
     *
     * @return
     */
    public ArrayList<EmailMessage> queryEmail() {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.query(true, tab_email_name, null, null, null, null, null, null, null);
        ArrayList<EmailMessage> emailList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                EmailMessage email = new EmailMessage();
                email.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                email.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                email.setContent(cursor.getString(cursor.getColumnIndex("content")));
                email.setSendTime(cursor.getString(cursor.getColumnIndex("send_time")));
                emailList.add(email);
            }
        }
        cursor.close();
        return emailList;
    }

    /**
     * 查询所有好友信息
     *
     * @return 查询到的所有好友信息
     */
    public ArrayList<Friend> queryFriensInfo() {
        db = mMyDBHelper.getReadableDatabase();
        ArrayList<Friend> friends = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_friends_name, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Friend friend = new Friend();
                friend.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                friend.setName(cursor.getString(cursor.getColumnIndex("name")));
                friend.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                friend.setAddType(cursor.getInt(cursor.getColumnIndex("add_type")));
                friend.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
                friend.setIdCard(cursor.getString(cursor.getColumnIndex("id_card")));
                friend.setSimNumber(cursor.getString(cursor.getColumnIndex("sim_number")));
                friends.add(friend);
            }
        }
        cursor.close(); // 记得关闭 corsor
        return friends;
    }

    /**
     * 查询所有ic卡信息
     *
     * @return 查询到的所有好友信息
     */
    public ArrayList<IcCardInfo> queryIcInfo() {
        db = mMyDBHelper.getReadableDatabase();
        ArrayList<IcCardInfo> icInfos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_icinfo_name, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                IcCardInfo icInfo = new IcCardInfo();
                icInfo.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                icInfo.setIcNumber(cursor.getInt(cursor.getColumnIndex("ic_number")));
                int isControl = cursor.getInt(cursor.getColumnIndex("is_control"));
                icInfo.setControl(isControl == 0 ? false : true);
                int isQuiesce = cursor.getInt(cursor.getColumnIndex("is_quiesce"));
                icInfo.setQuiesce(isQuiesce == 0 ? true : false);
                icInfo.setServiceFrequency((byte) cursor.getInt(cursor.getColumnIndex("service_frequency")));
                icInfo.setGrade((byte) cursor.getInt(cursor.getColumnIndex("grade")));
                icInfo.setLastSendTime(cursor.getString(cursor.getColumnIndex("last_send_time")));
                icInfos.add(icInfo);
            }
        }
        cursor.close(); // 记得关闭 corsor
        return icInfos;
    }


    public ArrayList<GroupMessage> queryGroupMessageByGroupId(int GroupId) {

        return null;
    }

    /**
     * 根据用户ID查找对应者的全部消息
     *
     * @param userId
     * @return
     */
    public ArrayList<CommonMessage> queryCommonMessageByUserId(int userId) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_common_message_name
                + " where sender_number=?", new String[]{String.valueOf(userId)});
        ArrayList<CommonMessage> messages = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CommonMessage msg = new CommonMessage();
                msg.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                msg.setSenderNumber(cursor.getInt(cursor.getColumnIndex("sender_number")));
                msg.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
                msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msg.setTime(cursor.getString(cursor.getColumnIndex("time")));
                msg.setSecond(cursor.getInt(cursor.getColumnIndex("second")));
                msg.setStatus(cursor.getInt(cursor.getColumnIndex("state")));
                msg.setType(cursor.getInt(cursor.getColumnIndex("type")));
                msg.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                messages.add(msg);
            }
        }
        cursor.close();
        return messages;
    }

    /**
     * 根据手机号查找对应的短信消息
     *
     * @param phoneNumber 手机号
     * @return
     */
    public ArrayList<SmsMessage> querySmsByPhoneNumber(String phoneNumber) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_sms_name
                + " where phone_number=?", new String[]{phoneNumber});
        ArrayList<SmsMessage> messages = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SmsMessage msg = new SmsMessage();
                msg.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                msg.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
                msg.setSenderName(cursor.getString(cursor.getColumnIndex("name")));
                msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msg.setTime(cursor.getString(cursor.getColumnIndex("send_time")));
                msg.setStatus(cursor.getInt(cursor.getColumnIndex("state")));
                msg.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                messages.add(msg);
            }
        }
        cursor.close();
        return messages;
    }

    /**
     * 根据userid查询用户其他信息
     *
     * @param userId
     * @return
     */
    public User queryUserInfoByUserId(int userId) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_user_name
                + " where user_id=?", new String[]{String.valueOf(userId)});
        User user = null;
        if (cursor.moveToNext()) {
            user = new User();
            user.setUserId(userId);
            user.setPassWord(cursor.getString(cursor.getColumnIndex("password")));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number")));
            user.setIdCardNumber(cursor.getString(cursor.getColumnIndex("id_card_number")));
            user.setNickName(cursor.getString(cursor.getColumnIndex("nick_name")));
        }
        cursor.close();
        return user;
    }

    /**
     * 根据userid查询好友名称
     *
     * @param userId
     * @return
     */
    public String queryFriendNameByUserId(int userId) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM " + tab_friends_name
                + " where user_id=?", new String[]{String.valueOf(userId)});
        String name = null;
        if (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        return name;
    }


    /**
     * 根据ic卡号查找ic卡是否存在
     *
     * @param icNumber
     * @return 该ic号码是否存在
     */
    public boolean queryIcInfoByIcNumber(int icNumber) {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_icinfo_name
                + " where ic_number=?", new String[]{String.valueOf(icNumber)});
        User user = null;
        if (cursor.moveToNext()) {
            return true;
        }
        cursor.close();
        return false;
    }


    /**
     * 根据_id修改好友的用户名称
     *
     * @param _id     行号
     * @param newName 新备注
     * @return 收到改变的行数
     */
    public int updateRamarkBy_Id(int _id, String newName) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        int updateResult = db.update(tab_friends_name, values, "_id=?",
                new String[]{String.valueOf(_id)});
        return updateResult;
    }

    /**
     * 根据userId修改消息状态
     *
     * @param userId    好友Id
     * @return 收到改变的行数
     */
    public int updateIsReadToHaveReadOfMsg(int userId) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", Constants.MESSAGE_HAVE_READ);
        int updateResult = db.update(tab_common_message_name, values, "sender_number=?",
                new String[]{String.valueOf(userId)});
        return updateResult;
    }

    /**
     * 查找是否有未发送的消息
     *
     * @return 查到的消息集合
     */
    public ArrayList<CommonMessage> queryMsgIfSending() {
        db = mMyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tab_common_message_name
                + " where state=?", new String[]{String.valueOf(Constants.MESSAGE_STATE_SENDING)});
        ArrayList<CommonMessage> messages = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CommonMessage msg = new CommonMessage();
                msg.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                msg.setSenderNumber(cursor.getInt(cursor.getColumnIndex("sender_number")));
                msg.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
                msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msg.setTime(cursor.getString(cursor.getColumnIndex("time")));
                msg.setSecond(cursor.getInt(cursor.getColumnIndex("second")));
                msg.setStatus(cursor.getInt(cursor.getColumnIndex("state")));
                msg.setType(cursor.getInt(cursor.getColumnIndex("type")));
                msg.setIsRead(cursor.getInt(cursor.getColumnIndex("is_read")));
                messages.add(msg);
            }
        }
        cursor.close();
        return messages;
    }


    /**
     * 根据手机号修改消息状态
     *
     * @param phone  y要修改的手机号
     * @return 收到改变的行数
     */
    public int updateIsReadToHaveReadOfSms(String phone) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", Constants.MESSAGE_HAVE_READ);
        int updateResult = db.update(tab_sms_name, values, "phone_number=?",
                new String[]{phone});
        return updateResult;
    }

    /**
     * 根据ic卡号修改ic卡的入站状态
     *
     * @param icNumber  ic卡号
     * @param isControl 是否抑制
     * @param isQuiesce 是否静默
     * @return 收到改变的行数
     */
    public int updateEnterStateByIcNumber(int icNumber, boolean isControl, boolean isQuiesce) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ic_number", icNumber);
        values.put("is_control", isControl ? 1 : 0);
        values.put("is_quiesce", isQuiesce ? 0 : 1);
        int updateResult = db.update(tab_icinfo_name, values, "ic_number=?",
                new String[]{String.valueOf(icNumber)});
        return updateResult;
    }


    /**
     * 根据行_id改变消息的发送状态
     *
     * @param _id       行号
     * @param sendState 发送状态
     * @return 收到改变的行数
     */
    public int updateStateBy_Id(int _id, byte sendState) {
        db = mMyDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", sendState);
        int updateResult = db.update(tab_common_message_name, values, "_id=?",
                new String[]{String.valueOf(_id)});
        return updateResult;
    }

    /**
     * 移除dao指向
     */
    public static void removeDao() {
        dao = null;
    }


    /**
     * 关闭数据库的方法
     */
    public void close() {
        if (db != null)
            db.close();
    }

}
