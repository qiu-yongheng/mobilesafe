package com.a520it.mobilsafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/25  23:48
 * @desc 获取联系人信息
 */
public class ContactInfoUtils {
    //把数据封装到对象中
    public static List<ContactInfo> getContactInfo(Context context) {
        //得到内容提供者的解析器
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");

        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        //查询
        //参数二: 要查询的表名
        Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);

        //创建一个集合存放数据
        ArrayList<ContactInfo> contactInfos = new ArrayList<>();

        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            //创建内部类对象
            ContactInfo info = new ContactInfoUtils().new ContactInfo();

            //通过查询到的id查询data
            Cursor dataCursor = resolver.query(dataUri, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);



            while (dataCursor.moveToNext()) {
                //获取第一列的数据(data1)
                String data1 = dataCursor.getString(0);

                String mimetype = dataCursor.getString(1);

                if ("vnd.android.cursor.item/name".equals(mimetype)) {
                    //用户名
                    info.name = data1;
                } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    //电话
                    info.phone = data1;
                }
            }

            //添加数据到集合
            contactInfos.add(info);

        }
        return contactInfos;
    }

    //内部类
    public class ContactInfo {
        public String name;
        public String phone;
    }
}
