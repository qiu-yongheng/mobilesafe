package com.a520it.mobilsafe;

import android.test.AndroidTestCase;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @author 邱永恒
 * @time 2016/8/4  19:56
 * @desc ${TODD}
 */
public class Md5Text extends AndroidTestCase{

    public void testMd5() throws Exception {
        //获取解析器
        MessageDigest digest = MessageDigest.getInstance("md5");

        InputStream is = getContext().getAssets().open("Application.mk");
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            digest.update(buffer, 0, len);
        }

        StringBuffer sb = new StringBuffer();

        byte[] result = digest.digest();

        for (byte b : result) {
            String s = Integer.toHexString(b & 0xff);

            if (s.length() == 1) {
                sb.append('0');
            }
            sb.append(s);
        }

        System.out.println("md5=" + sb.toString());
    }
}
