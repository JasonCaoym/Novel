package com.duoyue.mod.ad.bean;

import com.duoyue.mod.ad.utils.AdSiteBeanConverter;
import com.google.gson.annotations.SerializedName;
import org.greenrobot.greendao.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanwentao
 * @data 2019-08-07
 * 新版广告平台
 */
@Entity
public class AdConfigBean implements Serializable {

    public static final long serialVersionUID = 0xf4e42L;
    /**
     * channelCode : sk0001_sk01
     * dayShowInteval : 30
     * dayShowLimit : 100
     * showtimeRange : 0,23
     * advertiseResultVoList : [{"id":2,"linkType":5,"jsStyle":1,"carrierOperator":"4","priority":10,"origin":1,"adType":1,"adAppId":1101152570,"adId":8863364436303842593,"showInteval":1,"clickInteval":1},{"id":3,"linkType":5,"jsStyle":1,"carrierOperator":"4","priority":5,"origin":2,"adType":4,"adAppId":5001121,"adId":901121737,"showInteval":1,"clickInteval":1}]
     */
    @Id(autoincrement = true)
    private Long _id;
    @SerializedName("channelCode")
    @Index
    private String channelCode;
    @SerializedName("dayShowInteval")
    private int dayShowInteval;
    @SerializedName("dayShowLimit")
    private int dayShowLimit;
    @SerializedName("showtimeRange")
    private String showtimeRange;
    @SerializedName("advertiseResultVoList")
    @Convert(columnType = String.class, converter = AdSiteBeanConverter.class)
    private List<AdSiteBean> adSiteBeans;
    @Transient
    private long lastShowTime;
    @Transient
    private int showedCnt;


    @Generated(hash = 85745464)
    public AdConfigBean(Long _id, String channelCode, int dayShowInteval, int dayShowLimit, String showtimeRange, List<AdSiteBean> adSiteBeans) {
        this._id = _id;
        this.channelCode = channelCode;
        this.dayShowInteval = dayShowInteval;
        this.dayShowLimit = dayShowLimit;
        this.showtimeRange = showtimeRange;
        this.adSiteBeans = adSiteBeans;
    }

    @Generated(hash = 240604518)
    public AdConfigBean() {
    }


    public int getShowedCnt() {
        return showedCnt;
    }

    public void setShowedCnt(int showedCnt) {
        this.showedCnt = showedCnt;
    }

    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getChannelCode() {
        return this.channelCode;
    }
    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
    public int getDayShowInteval() {
        return this.dayShowInteval;
    }
    public void setDayShowInteval(int dayShowInteval) {
        this.dayShowInteval = dayShowInteval;
    }
    public int getDayShowLimit() {
        return this.dayShowLimit;
    }
    public void setDayShowLimit(int dayShowLimit) {
        this.dayShowLimit = dayShowLimit;
    }
    public String getShowtimeRange() {
        return this.showtimeRange;
    }
    public void setShowtimeRange(String showtimeRange) {
        this.showtimeRange = showtimeRange;
    }
    public List<AdSiteBean> getAdSiteBeans() {
        return this.adSiteBeans;
    }
    public void setAdSiteBeans(List<AdSiteBean> adSiteBeans) {
        this.adSiteBeans = adSiteBeans;
    }

    public long getLastShowTime() {
        return lastShowTime;
    }

    public void setLastShowTime(long lastShowTime) {
        this.lastShowTime = lastShowTime;
    }
}
