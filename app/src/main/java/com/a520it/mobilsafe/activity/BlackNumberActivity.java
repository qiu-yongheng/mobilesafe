package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.bean.BlackNumberBean;
import com.a520it.mobilsafe.dao.BlackNumberInfoDao;
import com.a520it.mobilsafe.utils.ToastUtils;

import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/7/27  10:15
 * @desc ${TODD}
 */
public class BlackNumberActivity extends Activity{
    private ListView lv_blacknumber;
    private List<BlackNumberBean> mAll;

    private BlackNumberAdapter mAdapter;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            //发送消息后, 隐藏正在加载的界面
            ll_loading.setVisibility(View.INVISIBLE);
            //显示listView
            lv_blacknumber.setVisibility(View.VISIBLE);

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            } else {
                //绑定适配器
                mAdapter = new BlackNumberAdapter();
                lv_blacknumber.setAdapter(mAdapter);
            }

        }
    };
    private AlertDialog.Builder mBuilder;
    private AlertDialog dialog;
    private EditText et_blackphone;
    private RadioGroup rg_mode;
    private Button bt_ok;
    private Button bt_cancle;
    private ImageView iv_deleter;
    private TextView tv_blackphone;
    private AlertDialog dismissdialog;
    private LinearLayout ll_loading;
    private int startIndex = 0;//从哪开始查
    private int maxCount = 20;//每页查询20条数据
    private BlackNumberInfoDao dao;
    private int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);

        //获取控件
        lv_blacknumber = (ListView)  findViewById(R.id.lv_blacknumber);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        //填充数据
        fillData();

        //获取数据的数量
        dao = new BlackNumberInfoDao(this);
        count = dao.getCount();

        //监听滑动的动作
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE ://滑动完之后的空闲状态
                        //得到listView界面最后一个可见条目在集合里的位置
                        int position = lv_blacknumber.getLastVisiblePosition();
                        //集合的大小, 从1开始
                        int size = mAll.size();

                        //当滑动到最后一个条目时
                        if (position == (size - 1)) {
                            //更新开始显示数据的索引
                            startIndex += maxCount;

                            //当数据显示完时, 提醒用户不要再刷
                            System.out.println("count = " + count);
                            System.out.println("startIndex = " + startIndex);
                            if (startIndex >= count) {
                                ToastUtils.showToast(BlackNumberActivity.this, "没有更多数据了");
                                return;
                            }
                            //填充数据
                            fillData();

                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸滚动

                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滑翔, 惯性滚动

                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


    }

    private void fillData() {
        //发送消息前, 显示正在加载的界面
        ll_loading.setVisibility(View.VISIBLE);
        //隐藏listView
        lv_blacknumber.setVisibility(View.GONE);
        //在子线程中查询数据
        new Thread(){
            public void run(){
                //获取DAO
                BlackNumberInfoDao dao = new BlackNumberInfoDao(BlackNumberActivity.this);

                if (mAll != null) {
                    //集合不为空, 添加数据到集合中
                    mAll.addAll(dao.findPart(startIndex, maxCount));
                } else {
                    //查询全部数据
                    mAll = dao.findPart(startIndex, maxCount);
                }
                //告诉主线程, 数据获取到了
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    public void addPhone(View v) {
        // 1. 创建对话框的创建类
        mBuilder = new AlertDialog.Builder(BlackNumberActivity.this);
        // 2. 获取布局控件
        View view = View.inflate(BlackNumberActivity.this, R.layout.dialog_addblackphone, null);
        // 3. 对话框绑定控件
        mBuilder.setView(view);

        //获取组件
        et_blackphone = (EditText) view.findViewById(R.id.et_blackphone);
        rg_mode = (RadioGroup) view.findViewById(R.id.rg_mode);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);
        bt_cancle = (Button) view.findViewById(R.id.bt_cancel);

        //设置按钮的点击事件
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏对话框
                dialog.dismiss();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入的号码
                String phone = et_blackphone.getText().toString().trim();

                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(BlackNumberActivity.this, "黑名单号码不能为空");
                    return;
                }

                //获取点击按钮的id
                int id = rg_mode.getCheckedRadioButtonId();

                //默认全部选中
                String mode = "3";
                switch (id) {
                    case R.id.rb_phone:
                        mode = "1";
                        break;
                    case R.id.rb_sms:
                        mode = "2";
                        break;
                    case R.id.rb_all:
                        mode = "3";
                        break;
                }

                //添加数据到数据库
                BlackNumberInfoDao dao = new BlackNumberInfoDao(BlackNumberActivity.this);
                boolean result = dao.add(phone, mode);

                //刷新界面
                if (result) {
                    //添加成功
                    ToastUtils.showToast(BlackNumberActivity.this, "添加成功");

                    BlackNumberBean bean = new BlackNumberBean();
                    bean.setMode(mode);
                    bean.setPhone(phone);

                    //添加数据对象到集合(listview中的数据是从集合中获取的)
                    mAll.add(0, bean);

                    //通知Listview刷新界面
                    mAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showToast(BlackNumberActivity.this, "添加失败");
                }
                //隐藏对话框
                dialog.dismiss();
            }
        });
        //显示对话框
        mBuilder.setView(view, 0, 0, 0, 0);
        dialog = mBuilder.show();
    }





    class BlackNumberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAll.size();
        }

        /**
         * 复用convertView 可以解决每次创建控件, 内存溢出问题
         * @param position
         * @param convertView 历史回收的view对象
         * @param viewGroup
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = View.inflate(BlackNumberActivity.this, R.layout.item_blackname, null);
                //获取组件
                holder.tv_blackphone = (TextView) convertView.findViewById(R.id.tv_blackphone);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_deleter = (ImageView) convertView.findViewById(R.id.iv_deleter);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //获取数据
            final BlackNumberBean bean = mAll.get(position);

            //给控件设置值
            holder.tv_blackphone.setText(bean.getPhone());

            String mode = bean.getMode();
            if ("1".equals(mode)) {
                holder.tv_mode.setText("电话拦截");
            } else if ("2".equals(mode)) {
                holder.tv_mode.setText("短信拦截");
            } else if ("3".equals(mode)) {
                holder.tv_mode.setText("全部拦截");
            }



            //设置删除按钮的点击事件
            holder.iv_deleter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    tv_blackphone = (TextView) view.findViewById(R.id.tv_blackphone);
//                    final String phone = tv_blackphone.getText().toString().trim();

                    mBuilder = new AlertDialog.Builder(BlackNumberActivity.this);
                    //设置对话框的属性
                    mBuilder.setTitle("警告");
                    mBuilder.setMessage("确定要删除这个黑名单吗?");
                    mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //根据号码删除数据库中的数据
                            BlackNumberInfoDao dao = new BlackNumberInfoDao(BlackNumberActivity.this);
                            boolean deleter = dao.deleter(bean.getPhone());

                            if (deleter) {
                                ToastUtils.showToast(BlackNumberActivity.this, "删除成功");
                                //删除集合中的数据
                                mAll.remove(position);

                                //刷新界面
                                notifyDataSetChanged();
                            } else {
                                ToastUtils.showToast(BlackNumberActivity.this, "删除失败");
                            }
                        }
                    });
                    mBuilder.setNegativeButton("取消", null);
                    //显示对话框
                    dismissdialog = mBuilder.show();
                }
            });




            return convertView;
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
        TextView tv_blackphone;
        TextView tv_mode;
        ImageView iv_deleter;
    }
}
