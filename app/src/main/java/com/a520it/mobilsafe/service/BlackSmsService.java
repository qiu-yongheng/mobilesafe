package com.a520it.mobilsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.a520it.mobilsafe.dao.BlackNumberInfoDao;
import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * @author 邱永恒
 * @time 2016/7/28  13:22
 * @desc ${TODD}
 */
public class BlackSmsService extends Service{

    private BlackNumberInfoDao mDao;
    private BlackSmsReceiver receiver;
    private TelephonyManager tm;
    private PhoneStateListener mListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 启动服务的方法
     */
    @Override
    public void onCreate() {
        System.out.println("服务启动了");
        //初始化dao
        mDao = new BlackNumberInfoDao(this);

        //获取电话管理的服务
        tm =(TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        //注册电话呼叫状态的监听器
        mListener = new PhoneListen();

        //正在监听电话打进来
        tm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

        //启动广播接收者
        receiver = new BlackSmsReceiver();
        //设置过滤器
        IntentFilter filter = new IntentFilter();

        //设置关系短信到来的动作
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //设置广播的优先级最大
        filter.setPriority(Integer.MAX_VALUE);

        //代码注册广播接收者
        registerReceiver(receiver, filter);
        super.onCreate();
    }

    /**
     * 服务关闭的方法
     */
    @Override
    public void onDestroy() {
        System.out.println("服务关闭了");
        //取消注册广播接收者
        unregisterReceiver(receiver);
        //把广播接收者设置为null, 等待GC
        receiver = null;

        //取消电话监听
        tm.listen(mListener, PhoneStateListener.LISTEN_NONE);
        tm = null;
        super.onDestroy();
    }

    /**
     * 在服务内部定义广播接收者
     * 1. 当短信到来的时候被接收到
     * 2. 获取发过来的短信地址(电话号码)
     * 3. 拿到地址后, 去数据库中查询该地址的mode
     */
    private class BlackSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取全部短信内容
            Object[] objs = (Object[]) intent.getExtras().get("pdus");

            for (Object obj : objs) {
                //获取短信信息
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);

                //获取短信地址
                String address = smsMessage.getOriginatingAddress();

                //;获取短信内容
                String body = smsMessage.getDisplayMessageBody();

                //根据手机号码查询模式
                String mode = mDao.find(address);

                if ("2".equals(mode) || "3".equals(mode)) {
                    System.out.println("黑名单短信, 拦截");
                    //拦截短信
                    abortBroadcast();
                }

                if (body.contains("yuepao")) {
                    System.out.println("黑名单短信, 拦截");
                    //拦截短信
                    abortBroadcast();
                }
            }
        }
    }

    class PhoneListen extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state) {
                //电话打进来的状态
                case TelephonyManager.CALL_STATE_IDLE://空闲状态

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接通状态

                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    //根据手机号码查询mode
                    String mode = mDao.find(incomingNumber);

                    if ("1".equals(mode) || "3".equals(mode)) {
                        //拦截电话
                        endCall();

                        //删除通话记录
                        deleterCallLog(incomingNumber);

                        //创建数据库的内容观察者, 观察数据库的变化
                        Uri uri = Uri.parse("content://call_log/calls");

                        //通过内容提供者注册内容观察者
                        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                           //数据库发生改变, 就回调这个方法
                            @Override
                            public void onChange(boolean selfChange) {

                                //当产生通话记录时, 调用
                                deleterCallLog(incomingNumber);
                                super.onChange(selfChange);
                            }
                        });
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 调用系统的方法挂掉电话
     */
    private void endCall() {
        //使用反射获取系统隐藏的类
        try {
            Class<?> clazz = BlackSmsService.class.getClassLoader().loadClass("android.os.ServiceManager");

            //获取私有方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            //执行方法
            IBinder b = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(b);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *删除通话记录
     */
    private void deleterCallLog(String incomingNumber) {
       //获取内容提供者
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{incomingNumber});
    }
}
