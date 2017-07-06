package com.a520it.mobilsafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author 邱永恒
 * @time 2016/7/30  14:55
 * @desc ${TODD}
 */
public class SmsTools {

    public interface BackupSmsCallBack {
        /**
         *
         * @param max
         */
        public void beforeSmsBackup(int max);
        public void onSmsBackup(int process);
    }
    /**
     * 备份用户短信
     * @param context 上下文
     * @param filename 备份后的文件名称
     * @return 是否备份成功
     */
    public static boolean backupSms(Context context, BackupSmsCallBack callback, String filename) {

        try {
            //访问别人的数据库, 需要内容提供者
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");

            //定义保存短信到哪个文件
            File file = new File(Environment.getExternalStorageDirectory(), filename);

            //创建一个写的输出流(写到指定的文件)
            FileOutputStream fos = new FileOutputStream(file);

            //得到XML的序列化器
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");

            //开始解析xml的头
            serializer.startDocument("utf-8", true);

            //开始解析标签
            serializer.startTag(null, "info");

            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "body", "type"}, null, null, null);

            int count = cursor.getCount();

            //设置进度条的最大值
            //传递参数
            callback.beforeSmsBackup(count);

            int process = 0;
            while(cursor.moveToNext()) {
                serializer.startTag(null, "sms");
                serializer.startTag(null, "address");
                String address = cursor.getString(0);
                serializer.text(address);
                serializer.endTag(null, "address");

                serializer.startTag(null, "date");
                String date = cursor.getString(1);
                serializer.text(date);
                serializer.endTag(null, "date");

                serializer.startTag(null, "body");
                String body = cursor.getString(2);
                serializer.text(body);
                serializer.endTag(null, "body");

                serializer.startTag(null, "type");
                String type = cursor.getString(3);
                serializer.text(type);
                serializer.endTag(null, "type");

                serializer.endTag(null, "sms");

                Thread.sleep(2000);

                process ++;

                //设置进度
                callback.onSmsBackup(process);
            }

            cursor.close();
            serializer.endTag(null, "info");
            serializer.endDocument();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
