package com.zzdm.tinker.net.bean;


public class ApiTinkerResult {

    /**
     * hotfixVersionCode : 2
     * downloadUrl : dddddddd
     * hotfixVersionName : V1.0.2H
     * md5
     */

    private int hotfixVersionCode;
    private String downloadUrl;
    private String hotfixVersionName;
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getHotfixVersionCode() {
        return hotfixVersionCode;
    }

    public void setHotfixVersionCode(int hotfixVersionCode) {
        this.hotfixVersionCode = hotfixVersionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getHotfixVersionName() {
        return hotfixVersionName;
    }

    public void setHotfixVersionName(String hotfixVersionName) {
        this.hotfixVersionName = hotfixVersionName;
    }

}
