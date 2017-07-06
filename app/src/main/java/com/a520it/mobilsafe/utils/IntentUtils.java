package com.a520it.mobilsafe.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * @author 邱永恒
 * @time 2016/7/23  19:08
 * @desc 界面跳转的工具类
 */
public class IntentUtils {
    /**
     * 延迟跳转
     * @param activity context
     * @param clazz 要跳转的界面
     * @param time  延迟时间
     */
    public static void startActivityForDelayAndFinish(final Activity activity, final Class clazz, final long time) {
        new Thread(){
            public void run(){
                //延迟2秒
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //跳转跳转界面
                Intent intent = new Intent(activity, clazz);
                activity.startActivity(intent);
                //结束之前的界面
                activity.finish();
            }
        }.start();
    }

    public static void statrtIntentAndFinish(Activity activity, Class clazz) {
        //跳转跳转界面
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void statrtIntent(Activity context, final Class clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }
}
