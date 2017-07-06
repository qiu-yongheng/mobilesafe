package com.a520it.mobilsafe.domain;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.bean.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/8/1  0:30
 * @desc 获取进程数据
 */
public class TaskInfoProvider {
    /**
     * 获取正在运行的进程的数据
     * @param context
     * @return
     */
    public static List<ProcessInfo> getRunningProcessInfos(Context context) {
        //创建集合
        ArrayList<ProcessInfo> processInfos = new ArrayList<>();

        //获取进程管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取安装包管理器
        PackageManager pm = context.getPackageManager();

        //获取所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : infos) {
            //每次循环都创建一个对象存储数据
            ProcessInfo processInfo = new ProcessInfo();

            //获取进程名
            String packName = info.processName;
            processInfo.setPackName(packName);

            //获取内存大小
            long memSize = am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty() * 1024;
            processInfo.setMemsize(memSize);

            try {
                //获取程序信息
                PackageInfo packageInfo = pm.getPackageInfo(packName, 0);

                //获取应用名
                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                processInfo.setAppName(appName);

                //获取应用图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                processInfo.setIcon(icon);

                /**
                 * 判断是否是系统程序
                 * return true, == 1, 系统进程
                 * == 0, 用户进程
                 */
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //系统进程
                    processInfo.setUserTask(false);
                } else {
                    //用户进程
                    processInfo.setUserTask(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

                //没有程序名, 设置默认值
                processInfo.setAppName(packName);
                processInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
            }
            processInfos.add(processInfo);
        }
        return processInfos;
    }
}
