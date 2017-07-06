package com.a520it.mobilsafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/28  20:17
 * @desc 动态判断服务是否被开启
 */
public class ServiceUtils {
    public static boolean isServiceRunning(Context context, String clazz) {
        //获取系统中管理活动的服务
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取正在运行的服务
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(1000);

        for (ActivityManager.RunningServiceInfo info: runningServices) {
            //获取服务名
            String serviceName = info.service.getClassName();

            //判断指定的服务是否开启
            if (clazz.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
