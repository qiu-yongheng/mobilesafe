package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.IntentUtils;

/**
 * @author 邱永恒
 * @time 2016/7/25  18:27
 * @desc ${TODD}
 */
public class LostActivity extends Activity{
    private TextView tv_safe_phone;
    private ImageView iv_setup4_icon;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        //获取控件
        tv_safe_phone = (TextView) findViewById(R.id.tv_safe_phone);
        iv_setup4_icon = (ImageView) findViewById(R.id.iv_setup4_icon);

        //给控件设置值
        tv_safe_phone.setText(sp.getString("safephone", ""));
        //默认不选中(数据回显)
        boolean protect = sp.getBoolean("protect", false);

        if (protect) {
            iv_setup4_icon.setImageResource(R.drawable.lock);
        } else {
            iv_setup4_icon.setImageResource(R.drawable.unlock);
        }
    }

    //点击后, 跳转到设置界面
    public void reEnterSetup(View v) {
        IntentUtils.statrtIntentAndFinish(LostActivity.this, Setup1Activity.class);
    }
}
