package com.duoyue.app.upgrade.download;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpgradeMsgBean implements Serializable {

    public static final long serialVersionUID = 0x1656542L;
    @SerializedName("downloadUrl")
    private String downloadUrl;
    @SerializedName("appVersionCode")
    private int appVersionCode;
    @SerializedName("appVersionName")
    private String appVersionName;
    @SerializedName("md5")
    private String md5;
    @SerializedName("isForce")
    private int isForceUpdate;
    @SerializedName("size")
    private long size;
    @SerializedName("desc")
    private String desc;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(int isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
