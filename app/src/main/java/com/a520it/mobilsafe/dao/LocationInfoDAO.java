package com.a520it.mobilsafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author 邱永恒
 * @time 2016/7/29  0:00
 * @desc 根据手机号码的类型查询归属地
 */
public class LocationInfoDAO {
    public static String getLocation(String phone) {
        String address = phone;
        //判断号码的类型
        if (phone.matches("^1[35678]\\d{9}$")) {
            //是手机号码

            //获取数据库对象
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.a520it.mobilsafe/files/address.db",
                    null, SQLiteDatabase.OPEN_READONLY);

            //查询数据
            Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)",
                    new String[]{phone.substring(0, 7)});

            while (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
        } else {
            switch (phone.length()) {
                case 3:
                    if ("110".equals(phone)) {
                        address = "报警热线";
                    } else if ("120".equals(phone)) {
                        address = "急救热线";
                    } else if ("119".equals(phone)) {
                        address = "火警热线";
                    }
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服热线";
                    break;
                case 7:
                    if (!phone.startsWith("0") && !phone.startsWith("1")) {
                        address = "本地号码";
                    }
                    break;
                default:
                    if (phone.length() >= 10 && phone.startsWith("0")) {
                        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.a520it.mobilsafe/files/address.db",
                                null, SQLiteDatabase.OPEN_READONLY);

                        //查询区号
                        Cursor cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 3)});

                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }

                        cursor = db.rawQuery("select location from data2 where area=?", new String[]{phone.substring(1, 4)});

                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                    }
                    break;
            }
        }
        return address;
    }
}
