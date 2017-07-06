package com.a520it.mobilsafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.a520it.mobilsafe.db.AppLockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/8/3  17:01
 * @desc ${TODD}
 */
public class AppLockDao {

    private  AppLockDBOpenHelper helper;
    private String table = "lockinfo";
    private Context context;

    /**
     * 谁使用dao, 就初始化数据库
     */
    public AppLockDao(Context context) {
        this.helper = new AppLockDBOpenHelper(context);
        this.context = context;
    }

    /**
     * 添加一条锁定应用程序的包名
     * @param packName 包名
     * @return
     */
    public boolean add (String packName) {
        //获取数据库
        SQLiteDatabase db = helper.getWritableDatabase();

        //执行添加的方法
        ContentValues values = new ContentValues();
        values.put("packName", packName);
        long result = db.insert(table, null, values);
        db.close();

        if (result != -1) {
            Uri uri = Uri.parse("content://a520it.mobilsafe.wangwang");
            context.getContentResolver().notifyChange(uri, null);

            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除一条锁定应用程序的包名
     * @param packName
     * @return
     */
    public boolean delete (String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();

        int result = db.delete(table, "packname=?", new String[]{packName});
        db.close();

        if (result > 0) {
            Uri uri = Uri.parse("content://a520it.mobilsafe.wangwang");
            context.getContentResolver().notifyChange(uri, null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询应用程序的包名是否被锁定
     * @param packName
     * @return
     */
    public boolean find (String packName) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(table, null, "packname=?", new String[]{packName}, null, null, null);
        boolean result = cursor.moveToNext();

        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查找所有数据
     * @return
     */
    public List<String> findAll () {
        ArrayList<String> packNames = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(table, new String[]{"packname"}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            packNames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packNames;
    }
}
