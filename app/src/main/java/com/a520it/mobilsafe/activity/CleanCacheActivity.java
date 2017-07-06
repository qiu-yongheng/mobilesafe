package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/8/3  22:18
 * @desc ${TODD}
 */
public class CleanCacheActivity extends Activity {

    private static final int SCAN_ALL = 1;
    private static final int SCANING = 2;
    private ListView lv_clean_cache;
    private TextView tv_clear_cache_name;
    private ProgressBar pb_cacher;
    private FrameLayout fra;
    private PackageManager pm;
    private String packageName;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_ALL:
                    fra.setVisibility(View.GONE);
                    lv_clean_cache.setAdapter(new CleanCacheAdapter());
                    break;
                case SCANING:
                    tv_clear_cache_name.setText("正在扫描:" + msg.obj);
                    break;
            }

        }
    };
    private ArrayList<CacheInfo> cacheInfos;
    private List<PackageInfo> mInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);

        //初始化控件
        lv_clean_cache = (ListView) findViewById(R.id.lv_clean_cache);
        tv_clear_cache_name = (TextView) findViewById(R.id.tv_clear_cache_name);
        pb_cacher = (ProgressBar) findViewById(R.id.pb_cacher);
        fra = (FrameLayout) findViewById(R.id.fra);
        //获取安装包管理器
        pm = getPackageManager();

        //扫描缓存
        Scancache();
    }



    /**
     * 扫描缓存
     */
    private void Scancache() {
        //存储有缓存的程序的集合
        cacheInfos = new ArrayList<CacheInfo>();

        //设置帧布局可见
        fra.setVisibility(View.VISIBLE);

        //耗时操作, 在子线程中进行
        new Thread() {
            public void run() {
                //获取所有的安装程序的信息
                mInfos = pm.getInstalledPackages(0);

                //设置进度条的最大进度
                pb_cacher.setMax(mInfos.size());
                int process = 0;


                for (PackageInfo info : mInfos) {
                    //设置进度
                    process++;
                    pb_cacher.setProgress(process);

                    //获取包名
                    packageName = info.packageName;

                    //获取pm所有的方法
                    Method[] methods = pm.getClass().getMethods();
                    for (Method method : methods) {
                        if ("getPackageSizeInfo".equals(method.getName())) {
                            //执行方法
                            try {
                                method.invoke(pm, packageName, new MyObserver());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    try {
                        sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                //给主线程发送消息
                Message msg = Message.obtain();
                msg.what = SCAN_ALL;
                handler.sendMessage(msg);

            }
        }.start();
    }

    class MyObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取有缓存的包名
            String packName = pStats.packageName;

            try {
                PackageInfo packageInfo = pm.getPackageInfo(packName, 0);

                //获取appname
                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                //获取图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);

                //返回主线程更新UI
                Message msg = Message.obtain();
                msg.what = SCANING;
                msg.obj = appName;
                handler.sendMessage(msg);

                if (pStats.cacheSize > 0) {
                    CacheInfo cacheInfo = new CacheInfo();

                    cacheInfo.cacheSize = pStats.cacheSize;
                    //获取有缓存的包名
                    cacheInfo.packName = pStats.packageName;
                    //获取appname
                    cacheInfo.appName = appName;
                    //获取图标
                    cacheInfo.icon = icon;

                    cacheInfos.add(cacheInfo);
                }


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 自定义适配器
     */
    class CleanCacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;

            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(CleanCacheActivity.this, R.layout.item_clean, null);

                holder = new ViewHolder();
                //获取控件
                holder.iv_clean_icon = (ImageView) view.findViewById(R.id.iv_clean_icon);
                holder.tv_clean_appname = (TextView) view.findViewById(R.id.tv_clean_appname);
                holder.tv_clean_cachesize = (TextView) view.findViewById(R.id.tv_clean_cachesize);
                holder.iv_clean_single = (ImageView) view.findViewById(R.id.iv_clean_single);

                view.setTag(holder);
            }

            //获取数据
            final CacheInfo cacheInfo = cacheInfos.get(position);

            holder.iv_clean_icon.setImageDrawable(cacheInfo.icon);
            holder.tv_clean_cachesize.setText(Formatter.formatShortFileSize(CleanCacheActivity.this, cacheInfo.cacheSize));
            holder.tv_clean_appname.setText(cacheInfo.appName);

            //设置点击事件
            holder.iv_clean_single.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + cacheInfo.packName));
                    startActivity(intent);
                }
            });

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


    class ViewHolder {
        ImageView iv_clean_icon;
        TextView tv_clean_appname;
        TextView tv_clean_cachesize;
        ImageView iv_clean_single;
    }


    class CleanItemIPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            ToastUtils.showToast(CleanCacheActivity.this, "清除状态:" + succeeded);
        }
    }


    class CacheInfo {
        Drawable icon;
        String appName;
        Long cacheSize;
        String packName;
    }


    /**
     * 设置清理图片的点击事件
     * @param v
     */
    public void cleancacheClick(View v) {

    }


    /**
     * 清理所有的缓存
     * @param v
     */
    public void cleanAll(View v) {
        Method[] methods = pm.getClass().getMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm, Long.MAX_VALUE, new CleanItemIPackageDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        //重新扫描缓存
        Scancache();
    }
}
