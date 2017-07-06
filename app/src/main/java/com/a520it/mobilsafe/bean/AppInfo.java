package com.a520it.mobilsafe.bean;

import android.graphics.drawable.Drawable;

/**
 * @author 邱永恒
 * @time 2016/7/30  19:01
 * @desc ${TODD}
 */
public class AppInfo {
    /**
     * 应用程序的大小
     */
    private long apkSize;
    /**
     * 应用程序的名称
     */
    private String appName;
    /**
     *应用程序的图标
     */
    private Drawable icon;
    /**
     * 是否安装在手机内存中
     */
    private boolean inRom;
    /**
     * 应用程序的包名
     */
    private String packName;
    /**
     *是否是用户程序
     */
    private boolean isUser;

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}
