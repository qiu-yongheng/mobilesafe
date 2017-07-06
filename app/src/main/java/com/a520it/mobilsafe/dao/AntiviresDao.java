package com.a520it.mobilsafe.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author 邱永恒
 * @time 2016/8/4  21:04
 * @desc 查询病毒的dao
 */
public class AntiviresDao {

    /**
     * 判断一个md5信息是否是病毒
     * @return
     */
    public static String isVirus(String md5) {
        String table = "datable";
        String desc = null;


        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.a520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        //查询md5
        Cursor cursor = db.query(table, new String[]{"desc"}, "md5=?", new String[]{md5}, null, null, null);

        while (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        //关闭资源
        cursor.close();
        db.close();
        return desc;
    }


    /**
     * 获取数据库版本
     * @return
     */
    public static int getVersion() {
        int version = 0;
        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.a520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("version", new String[]{"subcnt"}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            version = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return version;
    }


    /**
     * 设置数据库版本
     * @param version
     */
    public static void setVersion(int version) {
        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.a520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);

        ContentValues values = new ContentValues();
        values.put("subcnt", version);
        db.update("version", values, null, null);
        db.close();
    }

    /**
     * 添加一条病毒信息
     * @param md5
     * @param type
     * @param desc
     * @param name
     */
    public static void addVirusInfo(String md5, String type, String desc, String name) {
        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.a520it.mobilsafe/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("type", type);
        values.put("desc", desc);
        values.put("name", name);
        db.insert("datable", null, values);
        db.close();
    }
}
