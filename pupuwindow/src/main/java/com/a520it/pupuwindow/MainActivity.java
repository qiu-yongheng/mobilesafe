package com.a520it.pupuwindow;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
    }

    public void myClick(View v) {
        TextView textView = new TextView(this);
        textView.setText("我是悬浮窗口");
        textView.setBackgroundColor(Color.YELLOW);

        Random random = new Random();
        //创建一个悬浮窗口
        PopupWindow pw = new PopupWindow(textView, 200, 200);
        pw.showAtLocation(iv, Gravity.LEFT + Gravity.TOP, 100 + random.nextInt(), 200 + random.nextInt());

    }
}
