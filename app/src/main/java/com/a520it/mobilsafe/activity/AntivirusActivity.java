package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.dao.AntiviresDao;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author 邱永恒
 * @time 2016/8/4  20:15
 * @desc ${TODD}
 */
public class AntivirusActivity extends Activity {

    private ImageView iv_scan;
    private LinearLayout ll_container;
    private ProgressBar pb_scanner;
    private PackageManager mPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);

        //初始化控件
        initView();

        //创建一个旋转动画
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1500);
        //无限循环
        ra.setRepeatCount(Animation.INFINITE);
        //启动动画
        iv_scan.startAnimation(ra);


        //扫描病毒
        scanvirus();

    }

    /**
     * 扫描病毒
     */
    private void scanvirus() {
        new Thread() {
            public void run() {
                //获取安装包管理器
                mPm = getPackageManager();

                //获取手机中的所有的APK包
                List<PackageInfo> infos = mPm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES + PackageManager.GET_SIGNATURES);

                //设置进度条的最大进度
                pb_scanner.setMax(infos.size());
                int process = 0;

                for (PackageInfo info : infos) {
                    //设置进度条的进度
                    process++;
                    pb_scanner.setProgress(process);

                    try {
                        String path = info.applicationInfo.sourceDir;
                        File file = new File(path);

                        MessageDigest digest = MessageDigest.getInstance("md5");
                        FileInputStream fis = new FileInputStream(file);

                        byte[] buffer = new byte[1024];
                        int len = -1;

                        while ((len = fis.read(buffer)) != -1) {
                            digest.update(buffer, 0, len);
                        }

                        byte[] result = digest.digest();
                        StringBuffer sb = new StringBuffer();

                        //将字节转换成String类型
                        for (byte b : result) {
                            String s = Integer.toHexString(b & 0xff);
                            if (s.length() == 1) {
                                sb.append('0');
                            }
                            sb.append(s);
                        }
                        String md5 = sb.toString();

                        //获取程序名
                        final String appName = info.applicationInfo.loadLabel(mPm).toString();
                        //检查md5是否存在数据库
                        final String virus = AntiviresDao.isVirus(md5);

                        //更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //创建textview
                                TextView tv = new TextView(getApplicationContext());
                                if (virus != null) {
                                    tv.setText(appName + "发现病毒");
                                    tv.setTextColor(Color.RED);
                                    tv.setTextSize(20);
                                } else {
                                    tv.setText(appName + "没有病毒");
                                    tv.setTextColor(Color.GREEN);
                                    tv.setTextSize(20);
                                }
                                //添加textView到线性布局
                                //参数二: 控制textView显示的方向
                                ll_container.addView(tv, 0);
                            }
                        });
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        pb_scanner = (ProgressBar) findViewById(R.id.pb_scanner);
    }
}
