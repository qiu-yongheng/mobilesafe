package com.a520it.mobilsafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 邱永恒
 * @time 2016/7/26  13:16
 * @desc 使用MD5加密字符串
 */
public class Md5Utils {
    public static String encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");

            //把字符串加密成字节数组
            byte[] result = digest.digest(text.getBytes());

            StringBuffer sb = new StringBuffer();

            //把每一个字节转成16进制
            for (byte b: result) {
                //转换成16进制
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append(0);
                }
                //添加进字符缓冲流
                sb.append(hex);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
