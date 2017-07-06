package com.a520it.mobilsafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.IntentUtils;
import com.a520it.mobilsafe.utils.ToastUtils;

/**
 * @author 邱永恒
 * @time 2016/7/25  19:04
 * @desc ${TODD}
 */
public class Setup3Activity extends BaseActivity {
    private EditText et_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        et_phone = (EditText) findViewById(R.id.et_phone);

        //数据的回显
        String safephone = sp.getString("safephone", "");
        et_phone.setText(safephone);
    }

    public void selectContact(View v) {
        //跳转到选中联系人的界面
        Intent intent = new Intent(this, SelectContactActivity.class);

        //启动界面, 接受返回的数据
        startActivityForResult(intent, 0);
    }


    @Override
    //接受返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            //把特殊字符过滤掉
            String phone = data.getStringExtra("phone").replace("-", "").trim();
            et_phone.setText(phone);

        }
    }

    public void showNext() {
        //获取输入框的数据
        String phone = et_phone.getText().toString().trim();
        SharedPreferences.Editor edit = sp.edit();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast(this, "安全号码不能为空");
        } else {
            IntentUtils.statrtIntentAndFinish(Setup3Activity.this, Setup4Activity.class);
            edit.putString("safephone", phone);
            edit.commit();
        }
    }

    /**
     * 上一步
     *
     * @param
     */
    public void showPre() {

        IntentUtils.statrtIntentAndFinish(Setup3Activity.this, Setup2Activity.class);
    }
}
