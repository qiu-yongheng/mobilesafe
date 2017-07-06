package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.bean.AppInfo;
import com.a520it.mobilsafe.dao.AppLockDao;
import com.a520it.mobilsafe.domain.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/8/3  16:24
 * @desc ${TODD}
 */
public class AppLockActivity extends Activity implements View.OnClickListener {
    private TextView tv_unlock;
    private TextView tv_lock;
    private LinearLayout ll_unlock;
    private LinearLayout ll_lock;
    private TextView tv_unlock_count;
    private TextView tv_lock_count;
    private List<AppInfo> infos;
    private View view;
    private ImageView iv_lock_icon;
    private TextView tv_lock_appname;
    private ImageView iv_lock;
    private ArrayList<AppInfo> userAppInfos;
    private ArrayList<AppInfo> systemAppInfos;
    private AppLockDao dao;
    private ArrayList<AppInfo> unlockInfos;
    private ArrayList<AppInfo> lockInfos;

    private UnLoakAdapter unLoakAdapter;
    private UnLoakAdapter lockAdapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            unLoakAdapter = new UnLoakAdapter(true);
            lv_unlock.setAdapter(unLoakAdapter);

            lockAdapter = new UnLoakAdapter(false);
            lv_lock.setAdapter(lockAdapter);
        }
    };
    private ListView lv_unlock;
    private ListView lv_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);


        //初始化控件
        initView();

        //设置按钮的点击事件
        tv_unlock.setOnClickListener(this);
        tv_lock.setOnClickListener(this);

        //初始化数据
        fillData();
    }

    private void fillData() {
        //耗时操作, 在子线程中执行
        new Thread() {
            public void run() {
                //获取手机中所有的应用程序信息
                infos = AppInfoProvider.getAppInfos(getApplicationContext());

                dao = new AppLockDao(getApplicationContext());

                //创建两个集合
                //                userAppInfos = new ArrayList<AppInfo>();
                //                systemAppInfos = new ArrayList<AppInfo>();

                unlockInfos = new ArrayList<AppInfo>();
                lockInfos = new ArrayList<AppInfo>();

                for (AppInfo info : infos) {
                    //判断包名是否在数据库中, 有, 就说明加锁了
                    if (dao.find(info.getPackName())) {
                        //加锁了
                        lockInfos.add(info);
                    } else {
                        //没加锁
                        unlockInfos.add(info);
                    }
                }
                //通知主线程刷新
                handler.sendEmptyMessage(0);

            }
        }.start();
    }

    private void initView() {
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);

        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

        tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
        tv_lock_count = (TextView) findViewById(R.id.tv_lock_count);

        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unlock:
                //切换界面
                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);

                //设置背景的图片
                tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_right_default);
                break;

            case R.id.tv_lock:
                //切换界面
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);

                tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                break;
        }
    }

    class UnLoakAdapter extends BaseAdapter {

        boolean isUnLockApp;

        public UnLoakAdapter(boolean isUnLockApp) {
            this.isUnLockApp = isUnLockApp;
        }

        @Override
        public int getCount() {
            if (isUnLockApp) {
                int count = unlockInfos.size();

                tv_unlock_count.setText("未加锁软件:" + count + "个");
                return count;
            } else {
                int count = lockInfos.size();

                tv_lock_count.setText("已加锁软件:" + count + "个");
                return count;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;
            final AppInfo info;

            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.item_lock, null);
                holder.iv_lock_icon = (ImageView) view.findViewById(R.id.iv_lock_icon);
                holder.tv_lock_appname = (TextView) view.findViewById(R.id.tv_lock_appname);
                holder.iv_lock = (ImageView) view.findViewById(R.id.iv_lock);

                view.setTag(holder);
            }


            //设置图标
            if (isUnLockApp) {
                info = unlockInfos.get(position);
                holder.iv_lock.setImageResource(R.drawable.list_button_lock_pressed);
            } else {
                info = lockInfos.get(position);
                holder.iv_lock.setImageResource(R.drawable.list_button_unlock_pressed);
            }


            //获取数据
            holder.iv_lock_icon.setImageDrawable(info.getIcon());
            holder.tv_lock_appname.setText(info.getAppName());

            //设置加锁图片的点击事件
            final View finalView = view;
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isUnLockApp) {
                        //给view设置平移动画
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0
                                , Animation.RELATIVE_TO_SELF, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(300);

                        finalView.startAnimation(ta);

                        //设置动画的监听器
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //添加应用到加锁界面, 并添加到数据库
                                unlockInfos.remove(info);
                                dao.add(info.getPackName());

                                lockInfos.add(info);
//                                unLoakAdapter.notifyDataSetChanged();
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0
                                , Animation.RELATIVE_TO_SELF, -1.0f,
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(300);


                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //原本是加锁
                                lockInfos.remove(info);
                                dao.delete(info.getPackName());

                                unlockInfos.add(info);
//                                lockAdapter.notifyDataSetChanged();
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        finalView.startAnimation(ta);
                    }

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
        ImageView iv_lock_icon;
        TextView tv_lock_appname;
        ImageView iv_lock;
    }
}
