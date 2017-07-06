package com.a520it.mycache;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private PackageManager mPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            File file = new File(getCacheDir(), "haha.txt");
            FileOutputStream fis = new FileOutputStream(file);
            //写入数据
            fis.write("asdfjoashfhashdfl".getBytes());
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPm = getPackageManager();

        //通过反射获取隐藏的方法
        Method[] methods = mPm.getClass().getMethods();

        for (Method method : methods) {
            System.out.println(method.getName());
            System.out.println("------------");
        }

    }
}
