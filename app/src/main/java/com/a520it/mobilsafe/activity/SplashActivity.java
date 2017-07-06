package com.a520it.mobilsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a520it.mobilsafe.R;
import com.a520it.mobilsafe.dao.AntiviresDao;
import com.a520it.mobilsafe.utils.AppInfoUtils;
import com.a520it.mobilsafe.utils.IntentUtils;
import com.a520it.mobilsafe.utils.StringUtils;
import com.a520it.mobilsafe.utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 进行初始化的类
 */
public class SplashActivity extends Activity {
    private RelativeLayout rl_root;
    private TextView tv_version_name;
    private static final int NEED_UPDATE = 1;
    private SharedPreferences sp;


    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case NEED_UPDATE:
                    //弹出对话框
                    showUpdateDialog(msg);
                    break;
            }
        }
    };

    //显示对话框
    private void showUpdateDialog(Message msg) {
        //获取传入的数据
        final InfoMessage info = (InfoMessage) msg.obj;

        //创建一个对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //设置对话框的属性
        builder.setTitle("升级提示");
        builder.setMessage(info.desc);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            //下载最新的版本
            public void onClick(DialogInterface dialog, int which) {
                //创建断点下载工具对象
                HttpUtils httpUtils = new HttpUtils();

                //创建一个文件夹
                final File file = new File(Environment.getExternalStorageDirectory(), "xx.apk");

                /**参数一: 下载地址
                 * 参数二: 保存地址
                 * 参数三: 是否断点下载
                 * 参数四: 下载之后的回调方法
                 */
                httpUtils.download(info.url, file.getAbsolutePath(), false, new RequestCallBack<File>() {
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        Toast.makeText(SplashActivity.this, "下载成功", Toast.LENGTH_SHORT).show();

                        //自动安装
                        Intent intent=new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                        startActivity(intent);
                        //结束当前界面
                        SplashActivity.this.finish();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //跳转到主界面
                IntentUtils.statrtIntentAndFinish(SplashActivity.this, HomeActivity.class);
            }
        });
        //显示对话框
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //创建sp存储数据
        sp=getSharedPreferences("config",MODE_PRIVATE);

        //获取控件
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);

        //设置透明渐变动画
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(2000);
        //指定控件启动动画
        rl_root.startAnimation(aa);

        //设置版本名称
        tv_version_name.setText(AppInfoUtils.getVersionName(this));

        //联网检查当前的版本是否最新
        /**
         * 创建一个sp存储值, 默认values是false(只运行一次, 后面值会发生改变)
         * 当控件被点击的时候, 改变value的值
         */
        if (sp.getBoolean("update", false)) {
            IntentUtils.startActivityForDelayAndFinish(SplashActivity.this, HomeActivity.class, 2000L);
        } else {
            checkVersionCode();
        }
        //复制数据库
        copyBd("address.db");
        copyBd("commonnum.db");
        copyBd("antivirus.db");

        //检查更新数据库
        updateDb();

        //创建桌面图标
        createShortCut();

        //创建状态栏
        createNotication();
    }

    //生成通知栏
    private void createNotication() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("标题")
                        .setContentText("内容");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HomeActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     * 创建桌面图标
     */
    private void createShortCut() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);

        if (sp.getBoolean("shortcut", true)) {
            Intent intent = new Intent();
            //设置行为, 在桌面生成图标(lancher监听这个事件)
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

            //设置图标名
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "点我呀");
            //设置图标
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, android.graphics.BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

            //点击图标, 跳转到主界面
            Intent shortIntent = new Intent();
            shortIntent.setAction("com.a520it.mobilsafe.home");
            shortIntent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortIntent);
            //发送广播
            sendBroadcast(intent);

            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("shortcut", false);
            edit.commit();

        }


    }


    /**
     * 检查更新数据库
     */
    private void updateDb() {
        final String path = "http://192.168.34.164:8080/update.txt";
        //处理网络请求要在子线程中运行
        new Thread(){
            public void run(){
                try {
                    //获取url对象
                    URL url = new URL(path);
                    //new URL(getResources().getString(R.string.urlupdate));
                    //打开一个连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    conn.setRequestMethod("GET");
                    //判断服务器返回的状态
                    if (conn.getResponseCode() == 200) {
                        //获取输入流
                        InputStream is = conn.getInputStream();

                        //把流转换成字符串
                        String json = StringUtils.readStrean(is);

                        //解析json
                        JSONObject jsonObject = new JSONObject(json);

                        int version = jsonObject.getInt("version");
                        String md5 = jsonObject.getString("md5");
                        String type = jsonObject.getString("type");
                        String name = jsonObject.getString("name");
                        String desc = jsonObject.getString("desc");

                        //查询当前数据库版本
                        int dbVersion = AntiviresDao.getVersion();

                        //判断数据库是否需要更新
                        if (version > dbVersion) {
                            //更新
                            AntiviresDao.setVersion(version);
                            AntiviresDao.addVirusInfo(md5, type, desc, name);
                        } else {
                            ToastUtils.showToast(SplashActivity.this, "病毒库已最新");
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 复制数据库
     */
    private void copyBd(final String dbName) {
        new Thread(){
            public void run(){
                //获取文件保存路径
                File file = new File(getFilesDir(), dbName);

                if (file.exists() && file.length() > 0) {
                    ToastUtils.showToast(SplashActivity.this, "数据库已经存在");
                } else {
                    try {
                        //转换成输入流
                        InputStream is = getAssets().open(dbName);
                        FileOutputStream fos = new FileOutputStream(file);

                        byte[] buffer = new byte[1024];

                        int len = -1;

                        while((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        //关闭资源
                        is.close();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 检查版本是否最新
     */
    private void checkVersionCode() {
        //创建子线程
        new Thread(){
            public void run(){

                try {
                    // 1. 获取url
                    URL url = new URL(getString(R.string.serverurl));
                    // 2. 打开一个连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 3. 设置请求方法
                    conn.setRequestMethod("GET");
                    // 4. 设置访问超时时间
                    conn.setConnectTimeout(3000);
                    // 5. 判断服务器的返回状态
                    Log.v("av", conn.getResponseCode() + "");
                    if (conn.getResponseCode() == 200) {
                        InfoMessage info = new InfoMessage();

                        //获取服务器返回的输入流
                        InputStream is = conn.getInputStream();

                        //使用工具类把输入流转换成字符串
                        String jsonString = StringUtils.readStrean(is);

                        //解析json
                        JSONObject jsonObject = new JSONObject(jsonString);
                        int version = jsonObject.getInt("version");
                        String downloadurl = jsonObject.getString("downloadurl");
                        String desc = jsonObject.getString("desc");

                        //把数据传入对象中
                        info.url = downloadurl;
                        info.desc = desc;

                        Log.v("av", version + "");
                        //判断版本
                        if (version > AppInfoUtils.getVersionCode(SplashActivity.this)) {

                            //告诉主线程, 更新版本
                            Message msg = Message.obtain();
                            msg.obj = info;
                            msg.what = NEED_UPDATE;
                            handler.sendMessageDelayed(msg, 2000L);
                        } else {
                            //直接进入主界面
                            IntentUtils.startActivityForDelayAndFinish(SplashActivity.this, HomeActivity.class, 2000L);
                        }

                    }
                } catch (Exception e) {
                    ToastUtils.showToast(SplashActivity.this, "网络连接异常");
                    //直接进入主界面
                    IntentUtils.startActivityForDelayAndFinish(SplashActivity.this, HomeActivity.class, 2000L);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class InfoMessage {
        public String url;
        public String desc;
    }
}

