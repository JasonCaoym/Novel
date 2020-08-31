package com.duoyue.lib.base.app.http;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class JsonRequest implements Serializable
{
    @AutoHeader(HeaderType.TOKEN)
    public transient String token;

    @AutoParams(ParamsType.MID)
    @SerializedName("mid")
    public String mid;

    @AutoParams(ParamsType.UID)
    @SerializedName("uid")
    public String uid;

    @AutoParams(ParamsType.APP_ID)
    @SerializedName("appId")
    public long appId;

    @AutoParams(ParamsType.CHANNEL_CODE)
    @SerializedName("channelCode")
    public String channelCode;

    @AutoParams(ParamsType.VERSION)
    @SerializedName("version")
    public String version;

    @AutoParams(ParamsType.TIMESTAMP)
    @SerializedName("timestamp")
    public long timestamp;

    @AutoParams(ParamsType.IMEI)
    @SerializedName("imei")
    public String imei;

    @AutoParams(ParamsType.IMSI)
    @SerializedName("imsi")
    public String imsi;

    @AutoParams(ParamsType.MEID)
    @SerializedName("meid")
    public String meid;

    @AutoParams(ParamsType.ANDROID_ID)
    @SerializedName("androidId")
    public String androidId;

    /**
     * 省份
     */
    @AutoParams(ParamsType.PROVINCE)
    @SerializedName("province")
    public String province;

    /**
     * 城市
     */
    @AutoParams(ParamsType.CITY)
    @SerializedName("city")
    public String city;

    /**
     * wifi列表; 逗号分隔
     */
    @AutoParams(ParamsType.WIFIS)
    @SerializedName("wifis")
    public String wifis;

    /**
     * 网络环境 1: wifi 2:4G 3:3G 4:2G 5:other
     */
    @AutoParams(ParamsType.NETWORK)
    @SerializedName("network")
    public String network;

    /**
     * 运营商 1:移动  2.联通 3.电信 4.其他
     */
    @AutoParams(ParamsType.MOBILE)
    @SerializedName("mobile")
    public String mobile;

    /**
     * 纬度
     */
    @AutoParams(ParamsType.LATITUDE)
    @SerializedName("latitude")
    public double latitude;

    /**
     * 经度
     */
    @AutoParams(ParamsType.LONGITUDE)
    @SerializedName("longitude")
    public double longitude;

    /**
     * 协议版本号
     */
    @AutoParams(ParamsType.PROTOCOL_CODE)
    @SerializedName("protocolCode")
    public int protocolCode;

}
