package com.duoyue.lib.base.crash;

import com.duoyue.lib.base.devices.PhoneUtil;
import com.google.gson.annotations.SerializedName;

public class CrashList {

    /**
     * 机型
     */
    @SerializedName("phoneModel")
    private String mPhoneModel;

    /**
     * 品牌
     */
    @SerializedName("phoneBrand")
    private String mPhoneBrand;


    /**
     * 系统版本号
     */
    @SerializedName("systemVersion")
    private String mSystemVersion;

    /**
     * 崩溃信息
     */
    @SerializedName("crashInfo")
    private String mCrashInfo;


    public CrashList(String crashInfo) {
        //机型
        mPhoneModel = PhoneUtil.getModel();
        //品牌
        mPhoneBrand = PhoneUtil.getDeviceBrand();
        //系统版本号
        mSystemVersion = PhoneUtil.getAndroidVN();
        //错误原因
        mCrashInfo = crashInfo;
    }
}