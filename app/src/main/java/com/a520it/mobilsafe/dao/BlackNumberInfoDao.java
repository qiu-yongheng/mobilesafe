package com.a520it.mobilsafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.a520it.mobilsafe.bean.BlackNumberBean;
import com.a520it.mobilsafe.db.BlackNumberInfoHelp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/27  1:12
 * @desc ${TODD}
 */
public class BlackNumberInfoDao {

    private final BlackNumberInfoHelp help;
    private String table = "blacknumberinfo";

    public BlackNumberInfoDao(Context context) {
        //初始化数据库帮助类
        help = new BlackNumberInfoHelp(context);
    }

    /**
     * 添加黑名单的号码
     * @param phone 需要添加的黑名单号码
     * @param mode 修改的模式
     * @return true修改成功   false修改失败
     */
    public boolean add(String phone, String mode) {
        //创建数据库对象
        SQLiteDatabase db = help.getWritableDatabase();

        //执行添加数据的方法
        ContentValues values = new ContentValues();

        //添加数据
        values.put("phone", phone);
        values.put("mode", mode);

        long insert = db.insert(table, null, values);
        db.close();

        if (insert != -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除黑名单号码的操作
     * @param phone 需要删除的黑名单号码
     * @return true删除成功    false删除失败
     */
    public boolean deleter(String phone) {
        SQLiteDatabase db = help.getWritableDatabase();

        int delete = db.delete(table, "phone=?", new String[]{phone});
        db.close();
        //返回的是受影响的数据个数
        if (delete != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 通过手机号码修改模式
     * @param phone
     * @param mode
     * @return
     */
    public boolean update(String phone, String mode) {
        SQLiteDatabase db = help.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mode", mode);
        //通过手机号码修改模式
        int update = db.update(table, values, "phone=?", new String[]{phone});
        if (update != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据手机号码查询模式
     * @param phone
     * @return
     */
    public String find(String phone) {
        String mode = null;
        SQLiteDatabase db = help.getReadableDatabase();

        Cursor cursor = db.query(table, new String[]{"mode"}, "phone=?", new String[]{phone}, null, null, null);

        while (cursor.moveToNext()) {
            //只有一行数据
            mode = cursor.getString(0);
        }
        //关闭资源
        cursor.close();
        db.close();
        return mode;
    }

    public List<BlackNumberBean> findAll() {
        ArrayList<BlackNumberBean> list = new ArrayList<>();
        SQLiteDatabase db = help.getReadableDatabase();

        Cursor cursor = db.query(table, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            //创建对象
            BlackNumberBean bean = new BlackNumberBean();

            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));

            bean.setPhone(phone);
            bean.setMode(mode);

            list.add(bean);

            //模拟睡10毫秒
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //关闭资源
        cursor.close();
        db.close();
        return list;
    }

    public List<BlackNumberBean> findPart(int startIndex, int maxCount) {
        //获取数据库对象
        SQLiteDatabase db = help.getReadableDatabase();

        //执行sql语句查询
        Cursor cursor = db.rawQuery("select phone, mode from blacknumberinfo order by _id desc limit ? offset ?",
                new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});

        //创建一个集合存放数据
        ArrayList<BlackNumberBean> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            //创建一个对象存储数据
            BlackNumberBean bean = new BlackNumberBean();

            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));

            bean.setPhone(phone);
            bean.setMode(mode);

            list.add(bean);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //关闭资源
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 获取所有数据的数量
     * @return
     */
    public int getCount() {
        SQLiteDatabase db = help.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumberinfo", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        return count;
    }
}
