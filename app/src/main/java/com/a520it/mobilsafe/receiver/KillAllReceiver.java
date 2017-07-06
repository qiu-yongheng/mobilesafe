package com.a520it.mobilsafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/8/3  13:15
 * @desc 监听自定义意图, 杀死进程
 */
public class KillAllReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //创建一个进程管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            //根据包名杀死进程
            am.killBackgroundProcesses(info.processName);
        }
        Toast.makeText(context, "清理完毕", Toast.LENGTH_LONG).show();
    }
}
