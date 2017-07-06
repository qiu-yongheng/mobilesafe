package com.a520it.mobilsafe.service;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.ToastUtils;

/**
 * @author 邱永恒
 * @time 2016/8/4  12:54
 * @desc ${TODD}
 */
public class EntenPassActivity extends Activity{

    private ImageView iv_enter_icon;
    private TextView tv_enter_appname;
    private EditText et_enter_pass;
    private PackageManager mPm;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterpass);

        //初始化控件
        iv_enter_icon = (ImageView) findViewById(R.id.iv_enter_icon);
        tv_enter_appname = (TextView) findViewById(R.id.tv_enter_appname);
        et_enter_pass = (EditText) findViewById(R.id.et_enter_pass);


        //获取intent传过来的数据
        Intent intent = getIntent();
        packageName = intent.getStringExtra("packageName");

        //创建一个包管理器
        mPm = getPackageManager();

        //获取程序名和图标
        try {
            String appName = mPm.getPackageInfo(packageName, 0).applicationInfo.loadLabel(mPm).toString().trim();
            Drawable icon = mPm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(mPm);

            //给控件赋值
            iv_enter_icon.setImageDrawable(icon);
            tv_enter_appname.setText(appName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 重写返回键, 返回到主界面
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }


    /**
     * 当失去焦点时, 关闭界面
     */
    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }


    public void enterClick(View v) {
        //获取输入的密码
        String pass = et_enter_pass.getText().toString().trim();
        if (TextUtils.isEmpty(pass)) {
            ToastUtils.showToast(EntenPassActivity.this, "密码不能为空");
        } else {
            if ("123".equals(pass)) {
                //发送一个自定义广播, 临时取消保护
                Intent intent = new Intent();
                intent.setAction("com.a520it.mobilsafe.wangwang");
                intent.putExtra("packageName", packageName);
                //发送广播
                sendBroadcast(intent);


                finish();
            } else {
                ToastUtils.showToast(EntenPassActivity.this, "密码输入错误");
            }
        }
    }
}
