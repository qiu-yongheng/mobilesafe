package com.a520it.mobilsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.dao.LocationInfoDAO;

/**
 * @author 邱永恒
 * @time 2016/7/29  13:24
 * @desc ${TODD}
 */
public class ShowAddressService extends Service{

    private TelephonyManager tm;
    private MyListener listener;
    private InnerOutCallReceiver receiver;
    private WindowManager mWm;
    private TextView mTv_toast_address;
    private View mView;
    private SharedPreferences sp;
    private int mStartX;
    private int mStartY;
    private int mNewY;
    private int mNewX;
    private WindowManager.LayoutParams params;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 启动服务
     */
    @Override
    public void onCreate() {
        //获取系统的电话管理器
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        //监听电话的状态
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);



        //创建服务对象
        receiver = new InnerOutCallReceiver();
        IntentFilter filter = new IntentFilter();
        //给服务设置要监听的活动(播出电话时, 发送广播)
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        //注册服务
        registerReceiver(receiver, filter);

        super.onCreate();
    }

    /**
     * 停止服务
     */
    @Override
    public void onDestroy() {
        //取消监听电话
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;

        //取消注册广播接收者
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }

    //内部类
    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    //查询号码的归属地
                    String address = LocationInfoDAO.getLocation(incomingNumber);

                    //显示自定义吐司
                    showMyToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    if (mWm != null) {
                        //删除吐司
                        mWm.removeView(mView);
                        mWm = null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://接通状态

                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 去电号码归属地显示
     * 一个广播对应一个事件, 对应一个权限
     */
    private class InnerOutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取播出的手机号码
            String phone = getResultData();

            //获取号码归属地
            String address = LocationInfoDAO.getLocation(phone);

            //Toast.makeText(ShowAddressService.this, address, Toast.LENGTH_LONG).show();
            showMyToast(address);


        }
    }

    /**
     * 显示自定义吐司
     * @param address
     */
    public void showMyToast(String address) {
        mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        //获取当前选中的组件
        sp= getSharedPreferences("config", MODE_PRIVATE);
        int which = sp.getInt("which", 0);

        //创建数据
        int[] bgs = {R.drawable.call_locate_white, R.drawable.call_locate_orange,
                R.drawable.call_locate_blue, R.drawable.call_locate_gray,
                R.drawable.call_locate_green};

        //获取自定义吐司控件
        mView = View.inflate(ShowAddressService.this, R.layout.toast_address, null);

        //给控件设置触摸事件
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指放在屏幕上
                        // 1. 获取开始的坐标
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://手指在屏幕上移动
                        // 2. 获取移动后的坐标
                        mNewX = (int) event.getRawX();
                        mNewY = (int) event.getRawY();

                        // 3. 获取坐标的差值
                        int dx = mNewX - mStartX;
                        int dy = mNewY - mStartY;

                        // 4. 更新屏幕上控件的位置
                        params.x += dx;
                        params.y += dy;

                        //防止控件移除屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.y > mWm.getDefaultDisplay().getHeight() - mView.getHeight()) {
                            params.y = mWm.getDefaultDisplay().getHeight() - mView.getHeight();
                        }
                        if (params.x > mWm.getDefaultDisplay().getWidth() - mView.getWidth()) {
                            params.x = mWm.getDefaultDisplay().getWidth() - mView.getWidth();
                        }

                        mWm.updateViewLayout(mView, params);

                        // 5. 初始化开始坐标
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP://手指离开屏幕
                        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                        //获取编辑器
                        SharedPreferences.Editor edit = sp.edit();
                        //保存数据
                        edit.putInt("lastx", params.x);
                        edit.putInt("lasty", params.y);
                        //提交数据
                        edit.commit();
                        break;
                }
                return true;
            }
        });

        //获取组件
        mTv_toast_address = (TextView) mView.findViewById(R.id.tv_toast_address);

        //设置文本
        mTv_toast_address.setText(address);

        //设置背景
        mView.setBackgroundResource(bgs[which]);

        //设置真实的背景
        params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //把吐司默认显示在右上角
        params.gravity = Gravity.RIGHT + Gravity.TOP;


        //数据回显, 获取吐司最后在的位置
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        params.x = sp.getInt("lastx", 0);
        params.y = sp.getInt("lasty", 0);


        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

        mWm.addView(mView, params);
    }
}
