package com.a520it.mobilsafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.a520it.mobilsafe.service.UpdateWidgetService;

/**
 * @author 邱永恒
 * @time 2016/8/2  15:05
 * @desc ${TODD}
 */
public class MyAppWidgetReceiver extends AppWidgetProvider{
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        System.out.println("ondeleted");
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 关闭最后一个widget时调用
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        System.out.println("ondisabled");

        //停止服务
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        System.out.println("onenabled");
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("onreceive");

        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("onupdate");

        //更新数据的时候, 启动服务
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
