package com.a520it.toasttest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @author 邱永恒
 * @time 2016/7/29  19:20
 * @desc ${TODD}
 */
public class TestService extends Service{

    private WindowManager mWm;
    private TextView mTextView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取系统管理器
        mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //创建一个textview
        mTextView = new TextView(this);
        //设置文本
        mTextView.setText("哈哈, 逗你玩");
        mTextView.setTextColor(Color.RED);



        //创建一个可变参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;

        mWm.addView(mTextView, params);

    }


    @Override
    public void onDestroy() {
        //服务销毁时, 把窗口也销毁
        mWm.removeView(mTextView);
        super.onDestroy();
    }
}
