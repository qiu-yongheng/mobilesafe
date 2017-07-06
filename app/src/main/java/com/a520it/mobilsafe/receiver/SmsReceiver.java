package com.a520it.mobilsafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.service.LocationService;

/**
 * @author 邱永恒
 * @time 2016/7/26  14:27
 * @desc ${TODD}
 */
public class SmsReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objs = (Object[]) intent.getExtras().get("pdus");

        //判断应用程序是否已经激活
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(context, AdminReceicer.class);

        for(Object obj: objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);

            String body = smsMessage.getMessageBody();

            if ("#*location*#".equals(body)) {
                System.out.println("返回手机的位置信息");

                //启动服务
                Intent serviceIntent = new Intent(context, LocationService.class);
                context.startService(serviceIntent);
                //把短信隐藏(终止短信的意图)
                abortBroadcast();
            } else if ("#*alarm*#".equals(body)) {
                System.out.println("播放报警音乐");
                if (dpm.isAdminActive(admin)) {
                    //创建播放器
                    MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                    player.setLooping(true);
                    //设置声道
                    player.setVolume(1.0f, 1.0f);
                    //启动播放器
                    player.start();
                }
                //隐藏短信
                abortBroadcast();

            } else if ("#*wipedate*#".equals(body)) {
                System.out.println("远程销毁数据");
                if (dpm.isAdminActive(admin)) {
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                }
                abortBroadcast();

            } else if ("#*lockscreen*#".equals(body)) {
                System.out.println("远程锁屏");
                if (dpm.isAdminActive(admin)) {
                    dpm.resetPassword("123", 0);
                    dpm.lockNow();
                }
                abortBroadcast();
            }
        }
    }
}
