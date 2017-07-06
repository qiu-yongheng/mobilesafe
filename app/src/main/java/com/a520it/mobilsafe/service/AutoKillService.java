package com.a520it.mobilsafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 邱永恒
 * @time 2016/8/2  14:03
 * @desc ${TODD}
 */
public class AutoKillService extends Service{

    private ScreenLockReceiver receiver;
    private Timer timer;
    private TimerTask task;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new ScreenLockReceiver();
        IntentFilter filter = new IntentFilter();
        //设置要监听的事件
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //注册广播
        registerReceiver(receiver, filter);

        //创建定时器
        timer = new Timer();
        //创建定时任务
        task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("你能弄死我吗?");
            }
        };

        /**
         * 执行定时任务
         * 参数一: 定时任务
         * 参数二: 开始执行时间
         * 参数三: 循环执行时间
         */
        timer.schedule(task, 0, 5000);
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        //取消注册广播
        unregisterReceiver(receiver);
        receiver = null;

        //停止定时任务
        timer.cancel();
        task.cancel();
        super.onDestroy();
    }


    /**
     * 创建一个监听锁屏事件的广播
     */
    private class ScreenLockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取进程管理器
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            //获取正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo info : infos) {
                //杀掉进程(不能杀掉自己)
                am.killBackgroundProcesses(info.processName);
            }

        }
    }
}
