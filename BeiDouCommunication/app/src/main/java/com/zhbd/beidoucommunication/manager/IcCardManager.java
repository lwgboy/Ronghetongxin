package com.zhbd.beidoucommunication.manager;

import android.content.Context;

import com.zhbd.beidoucommunication.MyApplication;
import com.zhbd.beidoucommunication.config.Constants;
import com.zhbd.beidoucommunication.db.DatabaseDao;
import com.zhbd.beidoucommunication.domain.BeidouMachineState;
import com.zhbd.beidoucommunication.domain.IcCardInfo;
import com.zhbd.beidoucommunication.utils.CommUtil;
import com.zhbd.beidoucommunication.utils.SharedPrefUtil;

import java.util.ArrayList;

/**
 * Created by zhangyaru on 2017/9/21.
 */

public class IcCardManager {
    DatabaseDao dao;

    public IcCardManager() {
        int userId = SharedPrefUtil.getInt(MyApplication.getContextObject(), Constants.USER_ID, 0);
        dao = DatabaseDao.getInstance(MyApplication.getContextObject(), userId);
    }

    /**
     * 更新数据库中的ic信息
     *
     * @param list
     */
    public void updateDatabase(ArrayList<IcCardInfo> list) {
        // 不存在就加入
        for (int i = 0; i < list.size(); i++) {
            IcCardInfo icInfo = list.get(i);
            // 从数据库中查找是否存在该卡号
            boolean isExist = dao.queryIcInfoByIcNumber(icInfo.getIcNumber());
            // 如果存在, 修改
            if (isExist) {
                // 把数据库中的数据更新为最新的
                int isUpdate = dao.updateEnterStateByIcNumber(
                        icInfo.getIcNumber(), icInfo.isControl(), icInfo.isQuiesce());

            } else {
                // 最后的发送时间设置为很久以前1970.1.1
                icInfo.setLastSendTime(CommUtil.initDate());
                // 如果ic信息不存在, 就把该信息插入数据库
                dao.addDateToIcInfo(icInfo);
            }
        }

    }


    public void close() {
        if (dao != null) {
            dao.close();
        }
    }
}
