package com.a520it.mobilsafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 邱永恒
 * @time 2016/7/23  18:27
 * @desc 将字符流转换成字符串
 */
public class StringUtils {
    public static String readStrean(InputStream is) {
        try {
            //获取字节数组输出流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //创建一个缓冲字节数组
            byte[] buffer = new byte[1024];
            int len = -1;

            while((len = is.read(buffer)) != -1) {
                //把数据写到输出流中
                baos.write(buffer, 0, len);
            }
            //关闭流
            is.close();
            baos.close();

            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
