package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.bean.ProcessInfo;
import com.a520it.mobilsafe.domain.TaskInfoProvider;
import com.a520it.mobilsafe.utils.IntentUtils;
import com.a520it.mobilsafe.utils.SystemInfoUtils;
import com.a520it.mobilsafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/31  21:43
 * @desc 进程管理的界面
 */
public class TaskManagerActivity extends Activity{
    private TextView tv_process_count;
    private TextView tv_memory_info;
    private long availRam;
    private long totalRam;
    private int runningProcessCount;
    private LinearLayout ll_task_loading;
    private ListView lv_task_manager;
    private ArrayList<ProcessInfo> userProcessInfos;
    private ArrayList<ProcessInfo> systemProcessInfos;

    private TaskInfoAdapter adapter;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            //隐藏加载界面
            ll_task_loading.setVisibility(View.GONE);

            adapter = new TaskInfoAdapter();
            lv_task_manager.setAdapter(adapter);
        }
    };
    private ProcessInfo info;
    private TextView tv_lens;
    private ProcessInfo processInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        //初始化控件
        initView();

        //获取剩余内存
        availRam = SystemInfoUtils.getAvailRam(this);
        //获取总内存
        totalRam = SystemInfoUtils.getTotalRam(this);
        //获取当前进程数量
        runningProcessCount = SystemInfoUtils.getRunningProcessCount(this);
        tv_process_count.setText("当前运行的进程:" + runningProcessCount + "个");
        tv_memory_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, availRam) + "/" + Formatter.formatFileSize(this, totalRam));

        //获取数据
        fillData();


        //设置listView的滚动事件
        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userProcessInfos != null && systemProcessInfos != null) {
                    if (firstVisibleItem > userProcessInfos.size()) {
                        tv_lens.setText("系统进程:" + systemProcessInfos.size() + "个");
                    } else {
                        tv_lens.setText("用户进程:" + userProcessInfos.size() + "个");
                    }
                }
            }
        });


        //设置listView的点击事件
        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("我被点了");
                //获取点击的item
                if (position == 0 | position == (userProcessInfos.size() + 1)) {
                    return;
                } else if (position <= userProcessInfos.size()) {
                    //用户进程
                    int location = position -1;
                    info = userProcessInfos.get(location);
                } else {
                    //系统进程
                    int location = position - 1 - userProcessInfos.size() - 1;
                    info = systemProcessInfos.get(location);
                }

                System.out.println("设置选中状态");
               //判断是否选中
                if (info.getPackName().equals(getPackageName())) {
                    return;
                } else {
                    if (info.isChecked()) {
                        info.setChecked(false);
                    } else {
                        info.setChecked(true);
                    }
                }

                //刷新界面(就在主线程)
                adapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 获取数据
     */
    private void fillData() {
        //显示加载界面
        ll_task_loading.setVisibility(View.VISIBLE);

        //耗时操作, 创建子线程
        new Thread(){
            public void run(){
                //获取所有的进程信息
                List<ProcessInfo> infos = TaskInfoProvider.getRunningProcessInfos(TaskManagerActivity.this);

                //创建存储系统和用户进程的集合
                userProcessInfos = new ArrayList<ProcessInfo>();
                systemProcessInfos = new ArrayList<ProcessInfo>();

                for (ProcessInfo info: infos) {
                    if (info.isUserTask()) {
                        //用户进程
                        userProcessInfos.add(info);
                    } else {
                        //系统进程
                        systemProcessInfos.add(info);
                    }
                }
                //通知界面更新
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView)findViewById(R.id.tv_memory_info);

        ll_task_loading = (LinearLayout) findViewById(R.id.ll_task_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);

        tv_lens = (TextView) findViewById(R.id.tv_lens);
    }




    class TaskInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (getSharedPreferences("config", MODE_PRIVATE).getBoolean("showsystem", true)) {
                return userProcessInfos.size() + 1 + systemProcessInfos.size() + 1;
            } else {
                return userProcessInfos.size() + 1;
            }
//            return userProcessInfos.size() + 1 + systemProcessInfos.size() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("用户进程:" + userProcessInfos.size() + "个");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position == userProcessInfos.size() + 1) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText("系统进程:" + systemProcessInfos.size() + "个");
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                return tv;
            } else if (position <= userProcessInfos.size()) {
                //显示用户进程
                int location = position - 1;
                info = userProcessInfos.get(location);
            } else {
                //显示系统进程
                int location = position - 1 - userProcessInfos.size() - 1;
                info = systemProcessInfos.get(location);
            }


            View view = null;
            ViewHolder holder = null;


            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();

                view = View.inflate(TaskManagerActivity.this, R.layout.item_taskinfo, null);
                holder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tv_task_appname = (TextView) view.findViewById(R.id.tv_task_appname);
                holder.tv_task_size = (TextView) view.findViewById(R.id.tv_task_size);
                holder.cb_task_change = (CheckBox) view.findViewById(R.id.cb_task_change);

                view.setTag(holder);
            }

            //获取数据
            holder.iv_task_icon.setImageDrawable(info.getIcon());
            holder.tv_task_appname.setText(info.getAppName());
            holder.tv_task_size.setText(Formatter.formatFileSize(TaskManagerActivity.this, info.getMemsize()));

            //隐藏手机卫士的chekBox
            if (info.getPackName().equals(getPackageName())) {
                holder.cb_task_change.setVisibility(View.INVISIBLE);
            } else {
                holder.cb_task_change.setVisibility(View.VISIBLE);
                holder.cb_task_change.setChecked(info.isChecked());
            }
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

        class ViewHolder {
            ImageView iv_task_icon;
            TextView tv_task_appname;
            TextView tv_task_size;
            CheckBox cb_task_change;
        }
    }


    /**
     * 全选进程
     * @param v
     */
    public void selectAll(View v) {
        for (ProcessInfo info : userProcessInfos) {
            if (info.getPackName().equals(getPackageName())) {
                //跳出一次循环
                continue;
            }
            info.setChecked(true);
        }

        for (ProcessInfo info : systemProcessInfos) {
            info.setChecked(true);
        }

        //刷新界面
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选进程
     * @param v
     */
    public void conSelect(View v) {
        for (ProcessInfo info : userProcessInfos) {
//            if (info.isChecked()) {
//                info.setChecked(false);
//            } else {
//                info.setChecked(true);
//            }
//
            //简化写法
            if (info.getPackName().equals(getPackageName())) {
                continue;
            }
            info.setChecked(!info.isChecked());
        }

        for (ProcessInfo info : systemProcessInfos) {
//            if (info.isChecked()) {
//                info.setChecked(false);
//            } else {
//                info.setChecked(true);
//            }

            info.setChecked(!info.isChecked());
        }

        //刷新界面
        adapter.notifyDataSetChanged();
    }

    /**
     * 杀死进程
     * @param v
     */
    public void killSelect(View v) {
        int count = 0;
        long saveMemory = 0;

        //获取进程管理器
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //创建一个集合存放被杀掉的进程
        ArrayList<ProcessInfo> killedprocess = new ArrayList<ProcessInfo>();

        for (ProcessInfo info : userProcessInfos) {
            //判断是否选中
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackName());

                //记录杀死的进程和节约的内存
                count++;
                saveMemory += info.getMemsize();

                //把杀死的进程放在集合中, 最后统一处理
                killedprocess.add(info);
            }
        }

        for (ProcessInfo info : systemProcessInfos) {
            //判断是否选中
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackName());

                //记录杀死的进程和节约的内存
                count++;
                saveMemory += info.getMemsize();

                killedprocess.add(info);
            }
        }

        ToastUtils.showToast(this, "清理了" + count + "个进程, 释放了" + Formatter.formatFileSize(this, saveMemory) + "的内存");

        //刷新数据和界面
        //fillData();

        //更新进程数和剩余内存
        runningProcessCount -= count;
        availRam += saveMemory;
        tv_process_count.setText("运行进程:" + runningProcessCount + "个");
        tv_memory_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, availRam) + "/" + Formatter.formatFileSize(this, totalRam));

        /**
         * 不能在遍历集合的时候删除对象, 之后统一删除
         */
        for (ProcessInfo info : killedprocess) {
            if (info.isUserTask()) {
                userProcessInfos.remove(info);
            } else {
                systemProcessInfos.remove(info);
            }
        }
        //刷新界面
        adapter.notifyDataSetChanged();
    }

    /**
     * 进程设置
     * @param v
     */
    public void setClick(View v) {
        IntentUtils.statrtIntent(this, TaskSetActivity.class);
    }

    /**
     * 界面在获取焦点是刷新数据
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
