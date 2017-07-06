package com.a520it.mobilsafe.bean;

import android.graphics.drawable.Drawable;

/**
 * @author 邱永恒
 * @time 2016/8/1  0:48
 * @desc ${TODD}
 */
public class ProcessInfo {

    /**
     * 条目是否被选中
     */
    private boolean checked;

    /**
     * 判断是否被选中的方法
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 设置选中状态
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * 应用图标
     */
    private Drawable icon;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 内存大小
     */
    private long memsize;
    /**
     * 是否是用户进程
     */
    private boolean isUserTask;
    /**
     * 包名
     */
    private String packName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMemsize() {
        return memsize;
    }

    public void setMemsize(long memsize) {
        this.memsize = memsize;
    }

    public boolean isUserTask() {
        return isUserTask;
    }

    public void setUserTask(boolean userTask) {
        this.isUserTask = userTask;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }
}
