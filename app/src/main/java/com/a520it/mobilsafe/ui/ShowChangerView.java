package com.a520it.mobilsafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a520it.mobilsafe.R;

/**
 * @author 邱永恒
 * @time 2016/7/29  20:45
 * @desc ${TODD}
 */
public class ShowChangerView extends RelativeLayout{
    private TextView tv_bigtitle;
    private TextView tv_theme;

    public ShowChangerView(Context context) {
        this(context,null);
        initView(context);
    }

    public ShowChangerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        String addressstyle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "addressstyle");
        String themestyle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "themestyle");

        //给组件设置值
        tv_bigtitle.setText(addressstyle);
        tv_theme.setText(themestyle);
    }

    /**
     * 初始化view
     * @param context
     */
    private void initView(Context context) {
        //this, 表示父容器, 直接把布局文件加到父容器中
        View.inflate(context, R.layout.ui_change_view, this);
        tv_bigtitle = (TextView) findViewById(R.id.tv_bigtitle);
        tv_theme = (TextView) findViewById(R.id.tv_theme);
    }

    /**
     * 给主题栏设置值
     * @param text
     */
    public void setDesc(String text) {
        tv_theme.setText(text);
    }


}
