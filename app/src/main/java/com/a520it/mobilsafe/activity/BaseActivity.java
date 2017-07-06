package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.ToastUtils;

/**
 * @author 邱永恒
 * @time 2016/7/25  20:39
 * @desc 控制页面跳转动画的基类
 */
public abstract class BaseActivity extends Activity{
    public SharedPreferences sp;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在基类中初始化后, 子类可以直接使用

        //获取sp对象存储数据
        sp = getSharedPreferences("config", MODE_PRIVATE);

        //创建一个监听器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            /**
             *
             * @param e1 手指的事件
             * @param e2 手指的事件
             * @param velocityX X轴方向的速度
             * @param velocityY Y轴方向的速度
             * @return 是否执行操作
             */@Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //Y轴移动的绝对值
                if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
                    ToastUtils.showToast(BaseActivity.this, "动作不合法");
                    return true;
                }

                //向右划(上一步)
                if (e2.getRawX() - e1.getRawX() > 100) {
                    showPre();
                    overridePendingTransition(R.anim.trans_pre_int, R.anim.trans_pre_out);
                    return true;
                }

                //向左划(下一步)
                if (e1.getRawX() - e2.getRawX() > 100) {
                    showNext();
                    overridePendingTransition(R.anim.trans_next_int, R.anim.trans_next_out);
                    return true;
                }


                return super.onFling(e1, e2, velocityX, velocityY);
            }

        });
    }

    @Override
    //设置屏幕的点击事件
    public boolean onTouchEvent(MotionEvent event) {
        //激活滑动事件监听
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //子类要实现的方法
    public abstract void showNext();
    public abstract void showPre();

    //点击的时候调用
    public void nextClick(View v) {
        showNext();
        overridePendingTransition(R.anim.trans_next_int, R.anim.trans_next_out);
    }

    public void preClick(View v) {
        showPre();
        overridePendingTransition(R.anim.trans_pre_int, R.anim.trans_pre_out);
    }
}
