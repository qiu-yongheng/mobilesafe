package com.a520it.mobilsafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

/**
 * @author 邱永恒
 * @time 2016/7/26  22:10
 * @desc ${TODD}
 */
public class LocationService extends Service {
    private SharedPreferences sp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //获取sp对象
        sp = getSharedPreferences("config", MODE_PRIVATE);

        //获取系统管理器
        final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        //设置精度和耗电量
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        //获取当前最好的定位方法
        String bestProvider = lm.getBestProvider(criteria, true);

        lm.requestLocationUpdates(bestProvider, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                String text = "j=" + longitude + ", w=" + latitude;

                //发送短信
                SmsManager.getDefault().sendTextMessage(sp.getString("safephone", null), null, text, null, null);

                //停止监听服务服务
                lm.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }
}
