package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.bean.AppInfo;
import com.a520it.mobilsafe.domain.AppInfoProvider;
import com.a520it.mobilsafe.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/30  16:58
 * @desc ${TODD}
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    private TextView tv_disk_avail;
    private TextView tv_sd_avail;
    private LinearLayout ll_app_loading;
    private ListView lv_app_manager;
    private List<AppInfo> appInfos;
    private ArrayList<AppInfo> userAppInfos;
    private ArrayList<AppInfo> systemAppInfos;

    private AppInfoAdapter adapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //隐藏加载界面
            ll_app_loading.setVisibility(View.GONE);
            adapter = new AppInfoAdapter();
            lv_app_manager.setAdapter(adapter);
        }
    };
    private AppInfo appInfo;
    private View convertView;
    private ViewHolder holder;
    private View view;
    private TextView tv_lens;
    private ImageView parent;
    private PopupWindow mPw;
    private UninstallReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        //注册广播接收者
        receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter();
        //设置要监听的事件
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        //注册广播
        registerReceiver(receiver, filter);

        //初始化控件
        initView();

        //拿到可用内存
        File dataFile = Environment.getDataDirectory();
        long dataSize = dataFile.getFreeSpace();
        //获取可用SD卡空间
        File sdFile = Environment.getExternalStorageDirectory();
        long sdSize = sdFile.getFreeSpace();

        //给控件赋值
        tv_disk_avail.setText("内存可用:" + Formatter.formatFileSize(this, dataSize));
        tv_sd_avail.setText("SD卡可用:" + Formatter.formatFileSize(this, sdSize));

        //给listView填充数据
        fillData();

        //给listView设置滚动监听
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            /**
             * 滚动的时候调用
             */
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItem, int totalItemCount) {
                //这里存在空指针, 要非空判断
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        tv_lens.setText("系统程序:" + systemAppInfos.size() + "个");
                    } else {
                        tv_lens.setText("用户程序:" + userAppInfos.size() + "个");
                    }
                }
                //先把把旧的popuwindow置空, 再创建新的
//                if (mPw != null && mPw.isShowing()) {
//                    mPw.dismiss();
//                    mPw = null;
//                }
                dismissPopupWindow();
            }
        });

        //给listView条目注册item的点击事件
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //根据position判断是哪个item被点击了
                if (position == 0) {
                    return;
                } else if (position == (userAppInfos.size() + 1)) {
                    return;
                } else if (position <= userAppInfos.size()) {
                    //减去最前面第一个textview占据的位置
                    int location = position - 1;
                    appInfo = userAppInfos.get(location);
                } else {
                    int location = position - 1 - userAppInfos.size() - 1;
                    appInfo = systemAppInfos.get(location);
                }
                System.out.println(appInfo.getPackName() + "被点击了");

                //自定义悬浮窗体
                View contentView = View.inflate(AppManagerActivity.this, R.layout.popup_app_item, null);

                //先把把旧的popuwindow置空, 再创建新的
//                if (mPw != null && mPw.isShowing()) {
//                    mPw.dismiss();
//                    mPw = null;
//                }
                dismissPopupWindow();

                //创建一个对话框
                mPw = new PopupWindow(contentView, -2, -2);
                //给popupwindow设置背景, 才能显示动画(设置透明背景)
                mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //第一个参数代表x轴, 第二个参数代表Y轴
                int[] location = new int[2];

                //view就是每一个item
                view.getLocationInWindow(location);

                //最后 一个参数的1表示, Y轴不变
                mPw.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 200, location[1]);

                //给悬浮框设置动画效果
                //透明的动画
                AlphaAnimation aa = new AlphaAnimation(0.2f, 0.2f);
                aa.setDuration(200);
                //缩放的动画
                ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(200);

                //动画集
                AnimationSet set = new AnimationSet(false);
                //添加动画
                set.addAnimation(aa);
                set.addAnimation(sa);

                //指定控件启动动画
                contentView.startAnimation(set);

                //获取控件中的组件, 设置点击事件
                LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);

                LinearLayout ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                ll_start.setOnClickListener(AppManagerActivity.this);

                LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
                ll_share.setOnClickListener(AppManagerActivity.this);

                LinearLayout ll_info = (LinearLayout) contentView.findViewById(R.id.ll_info);
                ll_info.setOnClickListener(AppManagerActivity.this);

            }
        });

    }

    /**
     * 填充数据
     */
    private void fillData() {
        //显示加载界面
        ll_app_loading.setVisibility(View.VISIBLE);

        //获取数据是耗时操作, 在子线程中加载
        new Thread() {
            public void run() {
                //获取应用程序信息的集合
                appInfos = AppInfoProvider.getAppInfos(getApplicationContext());

                //初始化两个集合
                userAppInfos = new ArrayList<>();
                systemAppInfos = new ArrayList<>();

                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUser()) {
                        //用户程序
                        userAppInfos.add(appInfo);
                    } else {
                        //系统程序
                        systemAppInfos.add(appInfo);
                    }
                }
                //通知界面更新
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initView() {
        tv_disk_avail = (TextView) findViewById(R.id.tv_disk_avail);
        tv_sd_avail = (TextView) findViewById(R.id.tv_sd_avail);

        ll_app_loading = (LinearLayout) findViewById(R.id.ll_app_loading);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        tv_lens = (TextView) findViewById(R.id.tv_lens);
        parent = (ImageView) findViewById(R.id.parent);
    }


    /**
     * 自定义适配器
     */
    class AppInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //开头显示标签
            if (position == 0) {
                TextView userTv = new TextView(getApplicationContext());
                userTv.setBackgroundColor(Color.GRAY);
                userTv.setTextColor(Color.WHITE);
                userTv.setText("用户程序:" + userAppInfos.size() + "个");
                return userTv;
            } else if (position == userAppInfos.size() + 1) {
                TextView systemTv = new TextView(getApplicationContext());
                systemTv.setBackgroundColor(Color.GRAY);
                systemTv.setTextColor(Color.WHITE);
                systemTv.setText("系统程序:" + systemAppInfos.size() + "个");
                return systemTv;
            } else if (position <= userAppInfos.size()) {
                //显示用户程序
                int location = position - 1;
                appInfo = userAppInfos.get(location);
            } else {
                //显示系统程序
                int location = position - 1 - userAppInfos.size() - 1;
                appInfo = systemAppInfos.get(location);
            }


            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
                //必须包含相对布局
            } else {
                view = View.inflate(getApplicationContext(), R.layout.item_appinfo, null);

                holder = new ViewHolder();

                //获取控件中的组件
                holder.iv_appIcon = (ImageView) view.findViewById(R.id.iv_appIcon);
                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_app_location = (TextView) view.findViewById(R.id.tv_app_location);
                holder.tv_app_size = (TextView) view.findViewById(R.id.tv_app_size);

                view.setTag(holder);

            }

            //获取数据
            holder.iv_appIcon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_name.setText(appInfo.getAppName());

            if (appInfo.isInRom()) {
                holder.tv_app_location.setText("手机内存");
            } else {
                holder.tv_app_location.setText("SD卡中");
            }

            holder.tv_app_size.setText(Formatter.formatFileSize(getApplicationContext(), appInfo.getApkSize()));


            return view;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


    }

    class ViewHolder {
        ImageView iv_appIcon;
        TextView tv_app_name;
        TextView tv_app_location;
        TextView tv_app_size;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_uninstall:
                uninstallApp();
                break;
            case R.id.ll_start:
                startApp();
                break;
            case R.id.ll_share:
                share();
                break;
            case R.id.ll_info:
                infoApp();
                break;
        }
        //隐藏悬浮框
        dismissPopupWindow();
    }

    /**
     * 查看应用信息
     */
    private void infoApp() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackName()));
        startActivity(intent);
    }

    /**
     * 卸载应用程序
     */
    private void uninstallApp() {
        /*<action android:name="android.intent.action.VIEW" />
        <action android:name="android.intent.action.DELETE" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="package" />*/

        Intent intent = new Intent();
        //判断是否是系统程序
        if (appInfo.isUser()) {
            intent.setAction("android.intent.action.VIEW");
            intent.setAction("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + appInfo.getPackName()));
            startActivity(intent);
        } else {
            ToastUtils.showToast(AppManagerActivity.this, "手机需要root, 才能删除系统程序");
        }

    }

    /**
     * 启动应用程序
     */
    private void startApp() {
        //获取安装包的管理器
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackName());
        if (intent != null) {
            startActivity(intent);
        } else {
            ToastUtils.showToast(AppManagerActivity.this, "设备无法开启");
        }
    }

    /**
     * 隐藏悬浮框
     */
    private void dismissPopupWindow() {
        if (mPw != null && mPw.isShowing()) {
            mPw.dismiss();
            mPw = null;
        }
    }

    /**
     * 分享应用程序
     */
    private void share() {
        //        <action android:name="android.intent.action.SEND" />
        //        <category android:name="android.intent.category.DEFAULT" />
        //        <data android:mimeType="text/plain" />
        Intent intent = new Intent();
        //隐式意图(启动带有该意图的程序)
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "快来下载啊, 宅男必备, 你懂的, 下载链接: " + appInfo.getPackName());

        startActivity(intent);
    }


    /**
     * 监听卸载事件的广播
     */
    private class UninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (appInfo.isUser()) {
                userAppInfos.remove(appInfo);
            } else {
                systemAppInfos.remove(appInfo);
            }
            //刷新界面
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
