package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.IntentUtils;
import com.a520it.mobilsafe.utils.SmsTools;
import com.a520it.mobilsafe.utils.ToastUtils;

/**
 * @author 邱永恒
 * @time 2016/7/28  23:28
 * @desc ${TODD}
 */
public class AToolActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
    }

    public void selectAddress(View v) {
        //跳转界面
        IntentUtils.statrtIntent(AToolActivity.this, QuestAddressActivity.class);
    }

    public void commonNumberQuery(View v) {
        //跳转界面
        IntentUtils.statrtIntent(AToolActivity.this, CommonNumberActivity.class);
    }

    public void smsBackup(View v) {
        //创建一个进度对话框
        final ProgressDialog dialog = new ProgressDialog(this);
        //水平显示
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("短信备份");
        new Thread(){
            public void run(){
                boolean result = SmsTools.backupSms(AToolActivity.this, new SmsTools.BackupSmsCallBack() {
                    @Override
                    public void beforeSmsBackup(int max) {
                        dialog.setMax(max);
                    }

                    @Override
                    public void onSmsBackup(int process) {
                        dialog.setProgress(process);
                    }
                }, "backup.xml");

                if (result) {
                    ToastUtils.showToast(AToolActivity.this, "备份成功");
                } else {
                    ToastUtils.showToast(AToolActivity.this, "备份失败");
                }
                dialog.dismiss();
            }
        }.start();

        dialog.show();
    }


    public void appLock(View v) {

        //跳转界面
        IntentUtils.statrtIntent(this, AppLockActivity.class);
    }
}
