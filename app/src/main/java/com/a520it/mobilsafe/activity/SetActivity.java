package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.service.BlackSmsService;
import com.a520it.mobilsafe.service.ShowAddressService;
import com.a520it.mobilsafe.service.WatchDogService;
import com.a520it.mobilsafe.ui.SetCb;
import com.a520it.mobilsafe.ui.ShowChangerView;
import com.a520it.mobilsafe.utils.ServiceUtils;

public class SetActivity extends Activity {
    private SharedPreferences sp;
    private RelativeLayout rl_set;
    private SetCb ui_set;
    private SetCb ui_blackphone;
    private SetCb ui_address;
    private String[] names;
    private ShowChangerView show_changer_view;
    private AlertDialog dialog;
    private SetCb ui_watchdog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);



        sp= getSharedPreferences("config", MODE_PRIVATE);

        //获取控件
        rl_set = (RelativeLayout) findViewById(R.id.rl_set);
        ui_set = (SetCb) findViewById(R.id.ui_set);
        show_changer_view = (ShowChangerView) findViewById(R.id.show_changer_view);
        ui_watchdog = (SetCb)findViewById(R.id.ui_watchdog);

        //准备数据
        names = new String[] {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

        //数据回显
        show_changer_view.setDesc(names[sp.getInt("which", 0)]);


        //设置控件默认没有选中
        ui_set.setChecked(sp.getBoolean("update", false));

        //给整个RL控件设置点击事件
        rl_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个编辑器对象
                SharedPreferences.Editor edit = sp.edit();

                //判断checkBox是否被选中
                if (ui_set.isChecked()) {
                    //没被选中
                    ui_set.setChecked(false);

                    //设置sp
                    edit.putBoolean("update", false);
                } else {
                    //被选中
                    ui_set.setChecked(true);

                    edit.putBoolean("update", true);
                }
                //提交
                edit.commit();
            }
        });


        //获取设置黑名单的控件
        ui_blackphone = (SetCb) findViewById(R.id.ui_blackphone);

        //设置点击事件
        ui_blackphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建编辑器
                SharedPreferences.Editor edit = sp.edit();

                //判断checkBox是否被选中
                if (ui_blackphone.isChecked()) {
                    ui_blackphone.setChecked(false);
                    //停止服务, 取消注册广播接收者
                    Intent intent = new Intent(SetActivity.this, BlackSmsService.class);
                    stopService(intent);
                } else {
                    ui_blackphone.setChecked(true);
                    //开启服务, 注册广播接收者
                    Intent intent = new Intent(SetActivity.this, BlackSmsService.class);
                    startService(intent);
                }
            }
        });

        //设置点击事件
        ui_watchdog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建编辑器
                SharedPreferences.Editor edit = sp.edit();

                //判断checkBox是否被选中
                if (ui_watchdog.isChecked()) {
                    ui_watchdog.setChecked(false);
                    //停止服务, 取消注册广播接收者
                    Intent intent = new Intent(SetActivity.this, WatchDogService.class);
                    stopService(intent);
                } else {
                    ui_watchdog.setChecked(true);
                    //开启服务, 注册广播接收者
                    Intent intent = new Intent(SetActivity.this, WatchDogService.class);
                    startService(intent);
                }
            }
        });


        //获取开启来电显示的控件
        ui_address = (SetCb) findViewById(R.id.ui_address);
        //设置点击事件
        ui_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建编辑器
                SharedPreferences.Editor edit = sp.edit();

                //判断控件是否被选中
                if (ui_address.isChecked()) {
                    ui_address.setChecked(false);
                    System.out.println("停止服务");

                    //停止服务
                    Intent intent = new Intent(SetActivity.this, ShowAddressService.class);
                    stopService(intent);
                } else {
                    ui_address.setChecked(true);
                    System.out.println("开启服务");

                    //开启服务
                    Intent intent = new Intent(SetActivity.this, ShowAddressService.class);
                    startService(intent);
                }
            }
        });
    }

    private void initView() {

    }

    /**
     * 当界面获取焦点的时候, 重新设置控件的选中状态
     */
    @Override
    protected void onStart() {
        //判断服务是否开启
        boolean result = ServiceUtils.isServiceRunning(this, "com.a520it.mobilsafe.service.BlackSmsService");
        //设置按钮的选中状态
        ui_blackphone.setChecked(result);

        //判断服务是否开启
        boolean addressResult = ServiceUtils.isServiceRunning(this, "com.a520it.mobilsafe.service.ShowAddressService");
        //设置按钮的选中状态
        ui_address.setChecked(addressResult);

        //判断看门狗服务是否开启
        boolean watchDog = ServiceUtils.isServiceRunning(this, "com.a520it.mobilsafe.service.WatchDogService");
        ui_watchdog.setChecked(watchDog);
        super.onStart();
    }

    public void changeAddressTheme(View v) {
        //创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //设置对话框属性
        builder.setTitle("归属地提示框风格");



        builder.setSingleChoiceItems(names, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                SharedPreferences.Editor edit = sp.edit();

                edit.putInt("which", which);
                edit.commit();

                show_changer_view.setDesc(names[which]);

                //隐藏对话框
                dialog.dismiss();
            }
        });

        dialog = builder.show();
    }



}