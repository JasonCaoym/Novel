package com.duoyue.lib.base.crash;

import com.duoyue.lib.base.devices.PhoneUtil;
import com.google.gson.annotations.SerializedName;

public class PullDataList {

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
     * 拉取信息
     */
    @SerializedName("pullInfo")
    private String mPullInfo;

    /**
     * 接口url
     */
    @SerializedName("url")
    private String mUrl;
    /**
     * 操作 1拉取成功  2拉取失败
     */
    @SerializedName("operator")
    private String mOperator;


    public PullDataList(String pullDataInfo, String url, String operator) {
        //机型
        mPhoneModel = PhoneUtil.getModel();
        //品牌
        mPhoneBrand = PhoneUtil.getDeviceBrand();
        //系统版本号
        mSystemVersion = PhoneUtil.getAndroidVN();
        //拉取信息
        mPullInfo = pullDataInfo;
        //拉取接口地址
        mUrl = url;
        //操作类型
        mOperator = operator;
    }
}