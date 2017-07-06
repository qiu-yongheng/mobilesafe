package com.a520it.mobilsafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.receiver.AdminReceicer;
import com.a520it.mobilsafe.ui.SetCb;
import com.a520it.mobilsafe.utils.IntentUtils;

/**
 * @author 邱永恒
 * @time 2016/7/25  19:04
 * @desc ${TODD}
 */
public class Setup4Activity extends BaseActivity{
    private SetCb tv_setup4_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        //获取控件
        tv_setup4_status = (SetCb)findViewById(R.id.tv_setup4_status);

        //设置控件的选中状态
        tv_setup4_status.setChecked(sp.getBoolean("protect", false));

        //设置控件的点击事件
        tv_setup4_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取编辑器
                SharedPreferences.Editor edit = sp.edit();

                if (tv_setup4_status.isChecked()) {
                    //点击后, 不选中
                    tv_setup4_status.setChecked(false);

                    //添加数据(没开启)
                    edit.putBoolean("protect", false);
                } else {
                    tv_setup4_status.setChecked(true);
                    edit.putBoolean("protect", true);
                }
                //提交
                edit.commit();
            }
        });
    }

    public void startAdmin(View v) {
        //新建一个意图
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        //获取设备管理器
        ComponentName admin = new ComponentName(this, AdminReceicer.class);

        //存入数据
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启一键激活");

        //启动设备管理器的界面
        startActivity(intent);
    }



    /**
     * 下一步
     * @param
     */
    public void showNext() {
        IntentUtils.statrtIntentAndFinish(Setup4Activity.this, LostActivity.class);
    }

    /**
     * 上一步
     * @param
     */
    public void showPre() {
        IntentUtils.statrtIntentAndFinish(Setup4Activity.this, Setup3Activity.class);
    }
}
