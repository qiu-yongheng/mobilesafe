package com.a520it.mobilsafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 邱永恒
 * @time 2016/7/27  1:05
 * @desc ${TODD}
 */
public class BlackNumberInfoHelp extends SQLiteOpenHelper {
    /**
     * 构造方法
     * @param context
     *  m520it.db  数据库,名称
     * null  游标工厂 默认给null
     * 1  数据版本
     */
    public BlackNumberInfoHelp(Context context) {
        super(context, "m520it.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       //创建数据库表
        db.execSQL("create table blacknumberinfo(_id Integer primary key autoincrement, phone varchar(20), mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
