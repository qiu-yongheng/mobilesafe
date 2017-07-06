package com.a520it.mobilsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 邱永恒
 * @time 2016/8/3  16:58
 * @desc ${TODD}
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper{
    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库表
        db.execSQL("create table lockinfo(_id integer primary key autoincrement,packname varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
