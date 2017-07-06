package com.a520it.mobilsafe;

import android.test.AndroidTestCase;

import com.a520it.mobilsafe.dao.BlackNumberInfoDao;

import java.util.Random;

/**
 * @author 邱永恒
 * @time 2016/7/27  11:56
 * @desc ${TODD}
 */
public class DbText extends AndroidTestCase{
    //初始化操作不能写在成员变量中, 要写在测试框架中(不然回报空指针)
    private BlackNumberInfoDao dao;

    @Override
    protected void setUp() throws Exception {
        dao = new BlackNumberInfoDao(getContext());
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        dao = null;
        super.tearDown();
    }

    public void testAdd() {
        //创建随机数
        Random random = new Random();
        for (int i = 0; i<100; i++) {
            dao.add("10086" + i, random.nextInt(3) + 1 + "");
        }
    }

    public void TestSms() {

    }
}
