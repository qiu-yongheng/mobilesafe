package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.dao.LocationInfoDAO;
import com.a520it.mobilsafe.utils.ToastUtils;

/**
 * @author 邱永恒
 * @time 2016/7/28  23:33
 * @desc ${TODD}
 */
public class QuestAddressActivity extends Activity{

    private EditText mEt_address_phone;
    private TextView mTv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //获取控件
        mEt_address_phone = (EditText) findViewById(R.id.et_address_phone);
        mTv_address = (TextView) findViewById(R.id.tv_address);
    }

    /**
     * 点击获取手机归属地
     * @param v
     */
    public void questAddress(View v) {
        //获取输入框的数据
        String phone = mEt_address_phone.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast(QuestAddressActivity.this, "号码不能为空");

            //抖动输入框
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            mEt_address_phone.startAnimation(shake);
        } else {
            //查询数据库
            String location = LocationInfoDAO.getLocation(phone);
            mTv_address.setText("号码归属地: " + location);
        }
    }
}
