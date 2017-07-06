package com.a520it.supermanager;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

   public void myClick(View v) {
        //获取手机的系统服务
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

       //判断当前设置是否有激活高级管理员权限
       //获取设备管理器
       ComponentName admin = new ComponentName(this, MyAdminReceiver.class);
       if (dpm.isAdminActive(admin)) {
           //锁屏
           dpm.lockNow();
           //设置锁屏密码
           dpm.resetPassword("123", 0);
       } else {
           Toast.makeText(this, "需要开启高级管理员权限", Toast.LENGTH_SHORT).show();
       }
    }

    //开启超级管理员权限
    public void startAdmin(View v) {
        //新建一个意图
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //获取设备管理器
        ComponentName admin = new ComponentName(this, MyAdminReceiver.class);

        //存入数据
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启一键激活");

        //启动设备管理器的界面
        startActivity(intent);

    }

    //卸载应用程序
    public void uninstallApp(View v) {
        //判断应用程序是否已经激活
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(this, MyAdminReceiver.class);

        if (dpm.isAdminActive(admin)) {
            //已经激活
            //移除超级管理员权限
            dpm.removeActiveAdmin(admin);

            //参照原码, 写卸载的方法
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }
}
