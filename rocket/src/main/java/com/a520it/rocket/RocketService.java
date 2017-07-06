package com.a520it.rocket;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author 邱永恒
 * @time 2016/7/30  9:20
 * @desc ${TODD}
 */
public class RocketService extends Service{

    private WindowManager mWn;
    private WindowManager.LayoutParams params;
    private ImageView mImageView;
    private float mStartx;
    private float mStarty;
    private float mNewy;
    private float mNewx;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            mWn.updateViewLayout(mImageView, params);
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mWn = (WindowManager) getSystemService(WINDOW_SERVICE);

        //创建一个图片控件
        mImageView = new ImageView(this);

        //设置触摸事件
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指放在屏幕上
                        // 1. 获取开始坐标
                        mStartx = event.getRawX();
                        mStarty = event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE://手指在屏幕上移动
                        // 2. 获取移动后的坐标
                        mNewx = event.getRawX();
                        mNewy = event.getRawY();

                        // 3. 获取移动的差值
                        float dx = mNewx - mStartx;
                        float dy = mNewy - mStarty;

                        // 4. 更新控件的位置
                        params.x += dx;
                        params.y += dy;

                        //防止控件移除屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.y > mWn.getDefaultDisplay().getHeight() - mImageView.getHeight()) {
                            params.y = mWn.getDefaultDisplay().getHeight() - mImageView.getHeight();
                        }
                        if (params.x > mWn.getDefaultDisplay().getWidth() - mImageView.getWidth()) {
                            params.x = mWn.getDefaultDisplay().getWidth() - mImageView.getWidth();
                        }

                        mWn.updateViewLayout(mImageView, params);

                         // 5. 初始化开始坐标
                        mStartx = event.getRawX();
                        mStarty = event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP://手指离开屏幕
                        if (params.x > 100 && params.x < 300 && params.y >300) {
                            Toast.makeText(RocketService.this, "发射火箭", Toast.LENGTH_LONG).show();
                            new Thread(){
                                public void run(){
                                    for (int i = 0; i<12; i++) {
                                        try {
                                            sleep(50);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        params.y -= i * 25;
                                        //在主线程中更新UI
                                        handler.sendEmptyMessage(0);
                                    }
                                }
                            }.start();
                        }
                        break;

                }
                return true;
            }
        });

        //设置背景
        mImageView.setBackgroundResource(R.drawable.rocket);
        AnimationDrawable rocketAnimation = (AnimationDrawable) mImageView.getBackground();
        rocketAnimation.start();

        //设置真实的背景
        params = new WindowManager.LayoutParams();

        params.gravity = Gravity.LEFT + Gravity.TOP;

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

        mWn.addView(mImageView, params);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mWn.removeView(mImageView);
        mImageView = null;
        super.onDestroy();
    }
}
