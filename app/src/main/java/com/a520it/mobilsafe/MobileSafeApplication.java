package com.a520it.mobilsafe;

import android.app.Application;
import android.os.Build;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

public class MobileSafeApplication extends Application {
    //开天地方法,老母子方法
    @Override
    public void onCreate() {
        Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHander());
        super.onCreate();
    }

    private class MyExceptionHander implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
           // Logger.i("MobileSafeApplication", "发生了异常,但是被哥捕获了..");
            //并不能把异常消灭掉,只是在应用程序关掉前,来一个留遗嘱的事件
            try {
                Field[] fields = Build.class.getDeclaredFields();
                StringBuffer sb = new StringBuffer();
                for(Field field:fields){
                    String value = field.get(null).toString();
                    String name  = field.getName();
                    sb.append(name);
                    sb.append(":");
                    sb.append(value);
                    sb.append("\n");
                }

                FileOutputStream out = new FileOutputStream("/mnt/sdcard/error.log");
                StringWriter wr = new StringWriter();
                PrintWriter err = new PrintWriter(wr);
                ex.printStackTrace(err);
                String errorlog = wr.toString();
                sb.append(errorlog);
                out.write(sb.toString().getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //?专注自杀,早死早超生
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
