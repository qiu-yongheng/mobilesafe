package com.a520it.getaddress;

import android.test.AndroidTestCase;

/**
 * @author 邱永恒
 * @time 2016/7/27  16:10
 * @desc ${TODD}
 */
public class LocationTest extends AndroidTestCase{
    public void testLocation() throws Exception {
        //创建火星坐标对象
        ModifyOffset instance = ModifyOffset.getInstance(getContext().getAssets().open("axisoffset.dat"));

        PointDouble point = new PointDouble(113.22f, 20.52f);

        PointDouble pointDouble = instance.s2c(point);

        double x = pointDouble.x;
        double y = pointDouble.y;



    }
}
