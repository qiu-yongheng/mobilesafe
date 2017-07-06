package com.a520it.mobilsafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author 邱永恒
 * @time 2016/7/25  10:03
 * @desc ${TODD}
 */
public class ToastUtils {
    public static void showToast(final Activity context, final String str) {
        //判断是否在主线程
        if ("main".equals(Thread.currentThread().getName())) {
            //在主线程
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
