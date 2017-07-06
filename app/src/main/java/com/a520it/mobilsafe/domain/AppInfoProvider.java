package com.a520it.mobilsafe.domain;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.a520it.mobilsafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/30  18:56
 * @desc 业务类, 提供系统里面所用的应用程序信息
 */
public class AppInfoProvider {
    /**
     * 获取手机里面所有的安装的应用程序的信息(/data/app/xxx)(/system/app/xxx)
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {
        //创建一个集合存储数据
        ArrayList<AppInfo> appInfos = new ArrayList<>();

        //获取安装包的管理对象(可以获取所有安装包的信息)
        PackageManager pm = context.getPackageManager();

        //获取所有安装在设备上的包信息
        List<PackageInfo> packInfos = pm.getInstalledPackages(0);
        for (PackageInfo packInfo :packInfos){

            AppInfo appInfo = new AppInfo();

            //获取包名
            String packageName = packInfo.packageName;
            appInfo.setPackName(packageName);

            //获取软件名
            String appName = packInfo.applicationInfo.loadLabel(pm).toString();
            appInfo.setAppName(appName);

            //获取安装包的图标
            Drawable icon = packInfo.applicationInfo.loadIcon(pm);
            appInfo.setIcon(icon);

            //获取安装包的路径
            String path = packInfo.applicationInfo.sourceDir;
            //获取文件的长度
            File file = new File(path);
            long size = file.length();
            appInfo.setApkSize(size);

            //程序提交的答题卡
            int flags = packInfo.applicationInfo.flags;
            //与系统的Flag相与, 0代表应用程序, 1代表系统程序
            if ((flags & ApplicationInfo.FLAG_SYSTEM) ==0) {
                //用户程序
                appInfo.setUser(true);
            } else {
                //系统程序
                appInfo.setUser(false);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //手机系统内部空间
                appInfo.setInRom(true);
            } else {
                //sd卡
                appInfo.setInRom(false);
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
