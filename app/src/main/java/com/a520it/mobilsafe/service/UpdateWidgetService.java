package com.a520it.mobilsafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.receiver.MyAppWidgetReceiver;
import com.a520it.mobilsafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 邱永恒
 * @time 2016/8/2  15:19
 * @desc ${TODD}
 */
public class UpdateWidgetService extends Service{

    private Timer timer;
    private TimerTask task;
    private AppWidgetManager awm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //获取appwidget的管理器
        awm = AppWidgetManager.getInstance(this);

        //创建一个定时器
        timer = new Timer();

        //创建一个定时器任务
        task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("更新widget的内容");

                //获取广播
                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidgetReceiver.class);

                //获取一个远程的views(widget)
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                /**
                 * 给指定的item设置值
                 * 参数一: 要设置的item的ID
                 * 参数二: 要设置的值
                 */
                views.setTextViewText(R.id.process_count, "正在运行的软件:" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()));

                //获取剩余内存
                String availstr = Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getAvailRam(getApplicationContext()));
                views.setTextViewText(R.id.process_memory, "可用内存:" + availstr);


                //定义一个自定义的广播意图
                Intent intent = new Intent();
                //自定义一个杀死进程的意图
                intent.setAction("com.a520it.mobilsafe.killall");
                //定义一个延期的意图, 传递给别的进程, 给别的进程执行
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                //设置views中指定item的点击事件(发送一个意图)
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                //管理器更新数据
                awm.updateAppWidget(provider, views);

            }
        };
        timer.schedule(task, 0, 3000);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        task.cancel();
        timer = null;
        task = null;
        super.onDestroy();
    }


}
