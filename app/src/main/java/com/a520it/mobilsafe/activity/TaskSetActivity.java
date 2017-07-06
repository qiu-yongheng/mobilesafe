package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.service.AutoKillService;
import com.a520it.mobilsafe.ui.SetCb;

/**
 * @author 邱永恒
 * @time 2016/8/2  1:13
 * @desc ${TODD}
 */
public class TaskSetActivity extends Activity{

    private SetCb ui_task_showsysm;
    private SharedPreferences sp;
    private SetCb ui_task_closeclean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_set);

        sp = getSharedPreferences("config", MODE_PRIVATE);


        //获取控件
        ui_task_showsysm = (SetCb) findViewById(R.id.ui_task_showsysm);

        ui_task_showsysm.setChecked(sp.getBoolean("showsystem", true));

        //设置点击事件
        ui_task_showsysm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = sp.edit();

                if (ui_task_showsysm.isChecked()) {
                    ui_task_showsysm.setChecked(false);
                    edit.putBoolean("showsystem", false);
                } else {
                    ui_task_showsysm.setChecked(true);
                    edit.putBoolean("showsystem", true);
                }

                edit.commit();
            }
        });


        ui_task_closeclean = (SetCb) findViewById(R.id.ui_task_closeclean);
        
        ui_task_closeclean.setChecked(sp.getBoolean("closeclean", false));
        
        ui_task_closeclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = sp.edit();
                if (ui_task_closeclean.isChecked()) {
                    ui_task_closeclean.setChecked(false);
                    edit.putBoolean("closeclean", false);
                    Intent intent = new Intent(TaskSetActivity.this, AutoKillService.class);
                    stopService(intent);
                } else {
                    ui_task_closeclean.setChecked(true);
                    edit.putBoolean("closeclean", true);
                    Intent intent = new Intent(TaskSetActivity.this, AutoKillService.class);
                    startService(intent);
                }
                edit.commit();
            }
        });
    }
}
