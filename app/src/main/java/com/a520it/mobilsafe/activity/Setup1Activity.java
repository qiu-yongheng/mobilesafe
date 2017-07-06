package com.a520it.mobilsafe.activity;

import android.os.Bundle;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.utils.IntentUtils;

/**
 * @author 邱永恒
 * @time 2016/7/25  19:04
 * @desc ${TODD}
 */
public class Setup1Activity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    public void showNext() {
        IntentUtils.statrtIntentAndFinish(Setup1Activity.this, Setup2Activity.class);
    }

    public void showPre() {

    }
}
