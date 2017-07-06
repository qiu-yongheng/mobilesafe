package com.a520it.getaddress;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取系统管理器
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        //指定精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //指定耗电量
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        //获取最好的定位方法
        String provider = lm.getBestProvider(criteria, true);

        lm.requestLocationUpdates(provider, 0, 0, new LocationListener() {

            //位置发生改变的时候回调
            @Override
            public void onLocationChanged(Location location) {
                //获取经度
                double longitude = location.getLongitude();
                //获取纬度
                double latitude = location.getLatitude();

                String text = "经度=" + longitude + ", 纬度=" +  latitude;

                //用代码创建一个控件
                TextView tv = new TextView(MainActivity.this);

                tv.setText(text);
                tv.setTextColor(Color.RED);
                tv.setTextSize(20);
                setContentView(tv);
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
