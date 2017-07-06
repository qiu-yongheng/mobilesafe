package com.a520it.lancher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv = (GridView)findViewById(R.id.gv);

        //获取安装包管理器
        PackageManager pm = getPackageManager();

        //查询符合某种意图的所有的activity

        Intent intent = new Intent();

        //配置如下activity的才会产生桌面图标
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        //参数二:过滤获取可用启动的activity
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);


        final ArrayList<Drawable> icons = new ArrayList<>();
        final ArrayList<String> names = new ArrayList<>();
        for (ResolveInfo info : infos) {
            //获取程序图标
            Drawable icon = info.activityInfo.applicationInfo.loadIcon(pm);
            icons.add(icon);
            String name = info.activityInfo.applicationInfo.loadLabel(pm).toString();
            names.add(name);
        }

        //绑定适配器
        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return icons.size();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView iv = new ImageView(getApplicationContext());
                iv.setImageDrawable(icons.get(position));

                //获取textView控件
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.RED);
                tv.setText(names.get(position));

                //新建一个线性布局
                LinearLayout ll = new LinearLayout(getApplicationContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(iv);
                ll.addView(tv);

                return ll;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

        });
    }
}
