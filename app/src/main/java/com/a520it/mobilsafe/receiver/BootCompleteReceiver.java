package com.a520it.mobilsafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author 邱永恒
 * @time 2016/7/26  12:36
 * @desc 当SIM卡串号改变时, 发送广播
 */
public class BootCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("手机启动了");
        Log.i("BootCompleteReceiver", "手机重启了");

        //获取sp里面的数据
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        //获取SIM卡的绑定状态
        boolean protect = sp.getBoolean("protect", false);

        if (protect) {
            Log.v("BootCompleteReceiver", "检查SIM卡的状态");
            // 1. 取出当前手机的SIM卡串号

            //获取手机的系统服务
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取SIM卡串号
            String realSim = tm.getSimSerialNumber() + "424fd";//模拟SIM卡发生改变

            // 2. 取出原来绑定的SIM卡串号
            String bindSim = sp.getString("sim", "");

            // 3. 判断两次串号是否一样
            if (realSim.equals(bindSim)) {
                //SIM卡没有变化
            } else {
                //SIM卡改变
                //发送短信给安全号码
                SmsManager.getDefault().sendTextMessage(sp.getString("safephone", ""), null, "sim changed!", null, null);
            }
        } else {
            Log.v("BootCompleteReceiver", "没有开启防盗保护");
        }


    }
}
