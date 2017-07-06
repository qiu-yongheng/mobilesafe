package com.a520it.mobilsafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/31  21:48
 * @desc 获取手机操作系统信息的工具类
 */
public class SystemInfoUtils {
    /**
     * 获取正在运行的进程数量
     *
     * @param context 要获取手机的状态信息, 必须得到ActivityManager
     * @return
     */
    public static int getRunningProcessCount(Context context) {
        //获取进程管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //返回正在运行的系统进程
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }


    /**
     * 获取手机剩余可用内存(RAM)
     *
     * @param context
     * @return
     */
    public static long getAvailRam(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 获取手机的总内存(RAM)
     *
     * @param context
     * @return
     */
    public static long getTotalRam(Context context) {
        /*ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.totalMem;*/

        try {
            File file = new File("/proc/meminfo");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();

            //创建一个缓冲字符
            StringBuffer sb = new StringBuffer();

            for (char c: line.toCharArray()) {
                //把数字过滤出来
                if (c>='0' && c<='9') {
                    sb.append(c);
                }
            }

            //把KB转换成byte
            return Long.parseLong(sb.toString())*1024;

        } catch (Exception e) {
            e.printStackTrace();;
            return 0;
        }

    }


}
