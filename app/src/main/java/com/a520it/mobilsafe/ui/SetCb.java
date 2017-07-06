package com.a520it.mobilsafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a520it.mobilsafe.R;

public class SetCb extends RelativeLayout {

    private TextView tv_description;
    private CheckBox cb_set;

    public SetCb(Context context, AttributeSet attrs) {
        super(context, attrs);
        intView(context);

        String bigtitle = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "bigtitle");

        //给控件设置值
        tv_description.setText(bigtitle);
    }

    /**
     * 初始化控件
     */
    private void intView(Context context) {
        //根据布局文件创建控件
        View.inflate(context, R.layout.ui_set_cb, this);

        //获取控件中的组件
        tv_description = (TextView) findViewById(R.id.tv_description);
        cb_set = (CheckBox) findViewById(R.id.cb_set);
    }

    public void setChecked(boolean checked) {
        cb_set.setChecked(checked);
    }

    public boolean isChecked() {
        return cb_set.isChecked();
    }
}