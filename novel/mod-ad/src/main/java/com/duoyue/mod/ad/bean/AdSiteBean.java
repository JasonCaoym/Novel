package com.duoyue.mod.ad.bean;

import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.random.RandomService;
import com.google.gson.annotations.SerializedName;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * @author fanwentao
 * @data 2019-08-07
 * 新版广告平台
 */

public class AdSiteBean implements Serializable, RandomService {

    public static final long serialVersionUID = 0x943434;
    /**
     * id : 2
     * linkType : 5
     * jsStyle : 1
     * carrierOperator : 4
     * priority : 10
     * origin : 1
     * adType : 1
     * adAppId : 1101152570
     * adId : 8863364436303842593
     * showInteval : 1
     * clickInteval : 1
     */
    @SerializedName("id")
    private int id;
    @SerializedName("channelCode")
    private String channelCode;
    @SerializedName("linkType")
    private int linkType;
    @SerializedName("jsStyle")
    private int jsStyle;
    @SerializedName("carrierOperator")
    private String carrierOperator;
    @SerializedName("priority")
    private int priority;
    @SerializedName("origin")
    private int origin;
    @SerializedName("sdkAdType")
    private int adType;
    @SerializedName("sdkAdAppId")
    private String adAppId;
    @SerializedName("sdkAdId")
    private String adId;
    @SerializedName("showInteval")
    private int showInteval;
    @SerializedName("clickInteval")
    private int clickInteval;
    @SerializedName("showtimeRange")
    private String showtimeRange;
    @SerializedName("linkUrl")
    private String linkUrl;
    @SerializedName("picUrl")
    private String picUrl;
    @SerializedName("type")
    private int type;

    /**
     * 0:未知渲染方式
     * 1:模板
     * 2:自渲染
     */
    @SerializedName("renderType")
    private int renderType;

    /**
     * 宽高比(1:宽<高;2:宽>=高)
     */
    @SerializedName("aspectRatio")
    private int aspectRatio;

    @Transient
    private long lastShowTime;
    @Transient
    private long lastClickTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public int getJsStyle() {
        return jsStyle;
    }

    public void setJsStyle(int jsStyle) {
        this.jsStyle = jsStyle;
    }

    public String getCarrierOperator() {
        return carrierOperator;
    }

    public void setCarrierOperator(String carrierOperator) {
        this.carrierOperator = carrierOperator;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getRenderType() {
        return renderType;
    }

    public void setRenderType(int renderType) {
        this.renderType = renderType;
    }

    public int getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(int aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getAdAppId() {
        return adAppId;
    }

    public void setAdAppId(String adAppId) {
        this.adAppId = adAppId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public int getShowInteval() {
        return showInteval;
    }

    public void setShowInteval(int showInteval) {
        this.showInteval = showInteval;
    }

    public int getClickInteval() {
        return clickInteval;
    }

    public void setClickInteval(int clickInteval) {
        this.clickInteval = clickInteval;
    }

    @Override
    public int publishPriority() {
        return priority;
    }

    public String getShowtimeRange() {
        return showtimeRange;
    }

    public void setShowtimeRange(String showtimeRange) {
        this.showtimeRange = showtimeRange;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLastShowTime() {
        return lastShowTime;
    }

    public void setLastShowTime(long lastShowTime) {
        this.lastShowTime = lastShowTime;
    }

    public long getLastClickTime() {
        return lastClickTime;
    }

    public void setLastClickTime(long lastClickTime) {
        this.lastClickTime = lastClickTime;
    }

    @Override
    public String toString() {
        return "AdSiteBean{" +
                "id=" + id +
                ", channelCode='" + channelCode + '\'' +
                ", linkType=" + linkType +
                ", jsStyle=" + jsStyle +
                ", carrierOperator='" + carrierOperator + '\'' +
                ", priority=" + priority +
                ", origin=" + origin +
                ", adType=" + adType +
                ", adAppId='" + adAppId + '\'' +
                ", adId='" + adId + '\'' +
                ", showInteval=" + showInteval +
                ", clickInteval=" + clickInteval +
                ", showtimeRange='" + showtimeRange + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", type='" + type + '\'' +
                ", aspectRatio='" + aspectRatio + '\'' +
                ", renderType='" + renderType + '\'' +
                ", protocolCode=" + Constants.PROTOCOL_CODE +
                '}';
    }
}
