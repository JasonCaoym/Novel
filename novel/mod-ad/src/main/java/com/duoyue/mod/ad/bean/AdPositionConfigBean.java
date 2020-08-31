package com.duoyue.mod.ad.bean;

import com.google.gson.annotations.Expose;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class AdPositionConfigBean {

    /**
     * appId : 13
     * adSite : 1
     * adType : 3
     * grade : A
     * showNum : 10
     * showSpace : 40
     * tap : 1
     */
    @Id(autoincrement = true)
    private Long _id;
    private int appId;
    private int adSite;
    private int adType;
    private String grade;
    private int showNum;
    private int showSpace;
    private int tap;
    // 不序列化和反序列化，记录显示时间
    @Expose(deserialize = true, serialize = false)
    private long showTime = 0;
    @Generated(hash = 717182814)
    public AdPositionConfigBean(Long _id, int appId, int adSite, int adType,
            String grade, int showNum, int showSpace, int tap, long showTime) {
        this._id = _id;
        this.appId = appId;
        this.adSite = adSite;
        this.adType = adType;
        this.grade = grade;
        this.showNum = showNum;
        this.showSpace = showSpace;
        this.tap = tap;
        this.showTime = showTime;
    }
    @Generated(hash = 1615873001)
    public AdPositionConfigBean() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public int getAppId() {
        return this.appId;
    }
    public void setAppId(int appId) {
        this.appId = appId;
    }
    public int getAdSite() {
        return this.adSite;
    }
    public void setAdSite(int adSite) {
        this.adSite = adSite;
    }
    public int getAdType() {
        return this.adType;
    }
    public void setAdType(int adType) {
        this.adType = adType;
    }
    public String getGrade() {
        return this.grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public int getShowNum() {
        return this.showNum;
    }
    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }
    public int getShowSpace() {
        return this.showSpace;
    }
    public void setShowSpace(int showSpace) {
        this.showSpace = showSpace;
    }
    public int getTap() {
        return this.tap;
    }
    public void setTap(int tap) {
        this.tap = tap;
    }
    public long getShowTime() {
        return this.showTime;
    }
    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

}
