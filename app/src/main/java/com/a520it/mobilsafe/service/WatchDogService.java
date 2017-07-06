package com.a520it.mobilsafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.a520it.mobilsafe.dao.AppLockDao;

import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/8/4  10:58
 * @desc ${TODD}
 */
public class WatchDogService extends Service{

    private ActivityManager am;
    private boolean flag;
    private AppLockDao dao;
    private WatchDogReceiver receiver;
    private String stopPackageName;
    private List<ActivityManager.RunningTaskInfo> infos;
    private List<String> lockPackNames;
    private AppLockDbObServer obServer;
    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //定义一个广播接收者
        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.a520it.mobilsafe.wangwang");
        //监听锁屏事件
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);


        //注册一个内容观察者
        obServer = new AppLockDbObServer(new Handler());
        Uri uri = Uri.parse("content://a520it.mobilsafe.wangwang");
        getContentResolver().registerContentObserver(uri, true, obServer);


        dao = new AppLockDao(this);
        flag = true;

        //查询出所有的包名
        lockPackNames = dao.findAll();

        //获取进程管理器
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //应用程序被保护, 弹出输入密码的界面
        intent = new Intent(WatchDogService.this, EntenPassActivity.class);

        //开启看门狗
        startWatchDog();

        super.onCreate();
    }

    private void startWatchDog() {
        new Thread(){
            public void run(){
                while (flag) {
                    //记录当前时间
                    long startTime = System.currentTimeMillis();

                    //获取正在运行的任务栈
                    infos = am.getRunningTasks(1);
                    //获取栈顶的包名
                    String packageName = infos.get(0).topActivity.getPackageName();

                    //判断程序是否保存到数据库(是否加锁)
                    //优化成查内存
                    if (lockPackNames.contains(packageName)) {

                        //判断这个应用程序是否需要临时停止保护
                        if (packageName.equals(stopPackageName)) {
                            //需要临时停止保护
                        } else {



                            //不能在服务中启动一个活动, 需要设置
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //传递包名过去
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }

                    } else {
                        //应用程序没有保护
                    }
                    //记录运行结束后的时间
                    long endTime = System.currentTimeMillis();
                    System.out.println("一次循环花费的时间:" + (endTime - startTime));


                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(receiver);
        getContentResolver().unregisterContentObserver(obServer);
        obServer = null;
        receiver = null;

        super.onDestroy();
    }


    /**
     * 创建一个服务
     */
    private class WatchDogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取广播传过来的数据
            System.out.println("停止保护");

            //获取监听到的事件
            String action = intent.getAction();
            if ("com.a520it.mobilsafe.wangwang".equals(action)) {
            stopPackageName = intent.getStringExtra("packageName");
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //锁屏后, 重新保护加锁程序
                stopPackageName = null;
                //停止循环
                flag = false;
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //解锁, 开始监听
                flag = true;
                startWatchDog();
            }
        }
    }


    /**
     * 注册一个内容观察者
     */
    class AppLockDbObServer extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockDbObServer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            System.out.println("观察者观察到数据变化了");
            //重新查询数据库
            lockPackNames = dao.findAll();
        }
    }
}
