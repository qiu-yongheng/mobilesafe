package com.a520it.mobilsafe.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.IntentUtils;
import com.a520it.mobilsafe.utils.Md5Utils;
import com.a520it.mobilsafe.utils.ToastUtils;

public class HomeActivity extends Activity {
    private String[] names={"手机防盗","通讯卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","手机设置"};
    private int[]   icons={R.drawable.a,R.drawable.b,R.drawable.app,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i};
    private GridView gv_home;
    private EditText et_password;
    private EditText et_repeat_password;
    private Button bt_ok;
    private Button bt_cancle;
    private SharedPreferences sp;
    private AlertDialog.Builder mBuilder;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //创建sp存储数据
        sp=getSharedPreferences("config",MODE_PRIVATE);

        // 1. 获取控件
        gv_home = (GridView) findViewById(R.id.gv_home);

        // 2. 给控件绑定适配器
        gv_home.setAdapter(new HomeAdapter());

        // 3. 设置点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0://手机防盗
                        //获取保存的密码
                        String password = sp.getString("password", null);

                        //判断用户是否设置过密码
                        if (TextUtils.isEmpty(password)) {
                            //没有设置过
                            //弹出设置密码对话框
                            showSetPasswordDialog();
                        } else {
                            //有设置过
                            //弹出输入密码对话框
                            showEnterPasswordDialog();
                        }
                        break;
                    case 1://黑名单
                        IntentUtils.statrtIntent(HomeActivity.this, BlackNumberActivity.class);
                        break;
                    case 2://软件管理
                        IntentUtils.statrtIntent(HomeActivity.this, AppManagerActivity.class);
                        break;
                    case 3://进程管理
                        IntentUtils.statrtIntent(HomeActivity.this, TaskManagerActivity.class);
                        break;
                    case 4://流量管理
                        IntentUtils.statrtIntent(HomeActivity.this, TrafficManagerActivity.class);
                        break;
                    case 5://病毒查杀
                        IntentUtils.statrtIntent(HomeActivity.this, AntivirusActivity.class);
                        break;
                    case 6://缓存清理
                        IntentUtils.statrtIntent(HomeActivity.this, CleanCacheActivity.class);
                        break;
                    case 7://高级工具
                        IntentUtils.statrtIntent(HomeActivity.this, AToolActivity.class);
                        break;
                    case 8://手机设置
                        IntentUtils.statrtIntent(HomeActivity.this, SetActivity.class);
                        break;
                }
            }
        });
    }

    /**
     * 弹出确认密码对话框
     */
    private void showEnterPasswordDialog() {

        // 1. 创建对话框的创建类
        mBuilder = new AlertDialog.Builder(HomeActivity.this);
        // 2. 获取布局控件
        View view = View.inflate(HomeActivity.this, R.layout.dialog_enterpassword, null);

        // 3. 对话框绑定控件
        mBuilder.setView(view);

        //获取组件
        et_password = (EditText) view.findViewById(R.id.et_password);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);
        bt_cancle = (Button) view.findViewById(R.id.bt_cancel);

        //设置点击事件
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏对话框
                dialog.dismiss();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString().trim();

                // 判断密码是否为空
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.showToast(HomeActivity.this, "密码不能为空");
                    return;
                }

                // 判断密码是否正确
                if (Md5Utils.encode(password).equals(sp.getString("password", null))) {
                    ToastUtils.showToast(HomeActivity.this, "密码正确, 进入手机防盗界面");

                    //默认开启手机防盗
                    boolean finishsetup = sp.getBoolean("protect", false);
                    if (finishsetup) {
                        //没有勾选
                        //跳转到最后一个界面
                        IntentUtils.statrtIntent(HomeActivity.this, LostActivity.class);
                    } else {
                        //勾选关闭
                        //跳转到第一个设置界面
                        IntentUtils.statrtIntent(HomeActivity.this, Setup1Activity.class);
                    }
                } else {
                    ToastUtils.showToast(HomeActivity.this, "密码错误");
                }

                // 隐藏对话框
                dialog.dismiss();

            }
        });
        //显示对话框
        dialog = mBuilder.show();
    }

    /**
     * 弹出设置密码对话框
     */
    private void showSetPasswordDialog() {
        // 1. 创建对话框
       mBuilder = new AlertDialog.Builder(HomeActivity.this);


        // 2. 获取布局控件
        View view = View.inflate(HomeActivity.this, R.layout.dialog_setpassword, null);

        // 3. 绑定控件
        mBuilder.setView(view);

        //获取组件
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_repeat_password = (EditText) view.findViewById(R.id.et_repeat_password);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);
        bt_cancle = (Button) view.findViewById(R.id.bt_cancel);

        //设置点击事件
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 获取输入框里的数据
                String password = et_password.getText().toString().trim();
                String repPassword = et_repeat_password.getText().toString().trim();

                // 2. 判断密码是否为空
                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(repPassword)) {
                    ToastUtils.showToast(HomeActivity.this, "密码不能为空");
                    return;
                }

                // 3. 判断密码是否相同
                if (!password.equals(repPassword)) {
                    ToastUtils.showToast(HomeActivity.this, "两次输入的密码不一致");
                    return;
                }

                // 4. 保存密码
                //创建编辑器
                SharedPreferences.Editor edit = sp.edit();
                //添加数据
                edit.putString("password", Md5Utils.encode(password));
                //提交数据
                edit.commit();

                // 隐藏对话框
                dialog.dismiss();

                // 5. 显示输入密码的对话框
                showEnterPasswordDialog();
            }
        });


        // 4. 显示对话框
        dialog = mBuilder.show();
    }


    /**
     * 自定义适配器
     */
    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 9;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /**动态获取控件
             * 参数一: context
             * 参数二: 布局文件
             */
            View view = View.inflate(HomeActivity.this, R.layout.home_item, null);

            //获取里面的组件
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);

            //给组件设置值
            iv_icon.setImageResource(icons[position]);
            tv_name.setText(names[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }
}