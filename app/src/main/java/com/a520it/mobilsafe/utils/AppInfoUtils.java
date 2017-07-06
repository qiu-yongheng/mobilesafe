package com.a520it.mobilsafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author 邱永恒
 * @time 2016/7/23  13:51
 * @desc ${TODD}
 */
public class AppInfoUtils {
    public static String getVersionName(Context context) {
        //获取管理器
        PackageManager pm = context.getPackageManager();
        //获取版本信息
        String versionName = null;
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            //获取版本名称
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
    public static int getVersionCode(Context context) {
        //获取管理器
        PackageManager pm = context.getPackageManager();
        //获取版本信息
        int versionCode = 0;
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            //获取版本号
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
