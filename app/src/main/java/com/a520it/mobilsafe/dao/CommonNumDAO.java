package com.a520it.mobilsafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author 邱永恒
 * @time 2016/7/30  13:36
 * @desc ${TODD}
 */
public class CommonNumDAO {
    /**
     * 查询最外层的listView个数
     * @param db
     * @return
     */
    public static int getGroupCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from classlist", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        return count;
    }

    /**
     * 查询没给父控件对应的子listView个数
     * @return
     */
    public static int getChildrenCount(SQLiteDatabase db, int groupPosition) {
        //安卓的索引是从0开始
        int newPosition = groupPosition + 1;
        //获取要查询的表名
        String tableName = "table" + newPosition;

        //查询
        Cursor cursor = db.rawQuery("select count(*) from " + tableName, null);
        cursor.moveToNext();
        int childCount = cursor.getInt(0);
        return childCount;
    }

    /**
     * 查询分组的名称
     * @return
     */
    public static String getGroupView(SQLiteDatabase db, int groupPosition) {
        String name = null;
        int newPosition = groupPosition + 1;

        //查询
        Cursor cursor = db.query("classlist", new String[]{"name"}, "idx=?", new String[]{String.valueOf(newPosition)}, null, null, null);

        cursor.moveToNext();
        name = cursor.getString(0);
        return name;
    }

    /**
     * 查询每个分组里孩子的名称
     * @param db
     * @param groupPosition
     * @param childPosition
     * @return
     */
    public static String getChilderView(SQLiteDatabase db, int groupPosition, int childPosition) {
        int newGroupPosition = groupPosition + 1;
        int newChildPosition = childPosition + 1;

        //获取分组表名
        String table = "table" + newGroupPosition;

        Cursor cursor = db.query(table, new String[]{"number", "name"}, "_id=?", new String[]{String.valueOf(newChildPosition)}, null, null, null);

        cursor.moveToNext();
        String number = cursor.getString(0);
        String name = cursor.getString(1);

        return name + "\n" + number;

    }
}
