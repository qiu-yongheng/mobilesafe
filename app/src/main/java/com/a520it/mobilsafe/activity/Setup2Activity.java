package com.a520it.mobilsafe.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.ui.SetCb;
import com.a520it.mobilsafe.utils.IntentUtils;
import com.a520it.mobilsafe.utils.ToastUtils;

/**
 * @author 邱永恒
 * @time 2016/7/25  19:04
 * @desc ${TODD}
 */
public class Setup2Activity extends BaseActivity{

    private TelephonyManager tm;
    private SetCb cb_setup2;
    private String simSerialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        //获取手机的系统服务
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //获取手机串号
        simSerialNumber = tm.getSimSerialNumber();

        //获取单选框控件
        cb_setup2 = (SetCb) findViewById(R.id.cb_setup2);

        /**
         * 数据回显
         */
        //获取存储的SIM卡的信息

        String sim = sp.getString("sim", null);

        if (TextUtils.isEmpty(sim)) {
            //没有数据, 没选中
            cb_setup2.setChecked(false);
        }else {
            cb_setup2.setChecked(true);
        }


        //设置点击事件
        cb_setup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个编辑器
                SharedPreferences.Editor edit = sp.edit();

                //判断控件是否被选中
                if (cb_setup2.isChecked()) {
                    //选中, 点击后设置不选中
                    cb_setup2.setChecked(false);

                    //不存储SIM卡信息
                    edit.putString("sim", null);
                } else {
                    //没选中, 点击后设置选中
                    cb_setup2.setChecked(true);

                    //存储SIM卡信息
                    edit.putString("sim", simSerialNumber);
                }
                //提交数据
                edit.commit();
            }
        });
    }

    public void showNext() {
        //只有绑定SIM卡后才能进入下一个界面
        if (TextUtils.isEmpty(sp.getString("sim", null))) {
            ToastUtils.showToast(this, "手机防盗功能必须绑定SIM卡串号");
        } else {
            IntentUtils.statrtIntentAndFinish(Setup2Activity.this, Setup3Activity.class);
        }
    }

    /**
     * 上一步
     * @param
     */
    public void showPre() {
        IntentUtils.statrtIntentAndFinish(Setup2Activity.this, Setup1Activity.class);
    }
}
