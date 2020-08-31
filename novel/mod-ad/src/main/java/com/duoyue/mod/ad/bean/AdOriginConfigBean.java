package com.duoyue.mod.ad.bean;

import com.google.gson.annotations.Expose;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class AdOriginConfigBean implements Comparable<AdOriginConfigBean>{

    /**
     * origin : 1
     * adAppId : 123213
     * adId : 1
     * adType : 1
     * grade : A
     */
    @Id(autoincrement = true)
    private Long _id;
    private int origin;
    private String adAppId;
    private String adId;
    private int adType;
    private String grade;
    @Expose(deserialize = false, serialize = false)
    private int adSite = 0;

    @Generated(hash = 760643928)
    public AdOriginConfigBean(Long _id, int origin, String adAppId, String adId,
            int adType, String grade, int adSite) {
        this._id = _id;
        this.origin = origin;
        this.adAppId = adAppId;
        this.adId = adId;
        this.adType = adType;
        this.grade = grade;
        this.adSite = adSite;
    }
    @Generated(hash = 650797823)
    public AdOriginConfigBean() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public int getOrigin() {
        return this.origin;
    }
    public void setOrigin(int origin) {
        this.origin = origin;
    }
    public String getAdAppId() {
        return this.adAppId;
    }
    public void setAdAppId(String adAppId) {
        this.adAppId = adAppId;
    }
    public String getAdId() {
        return this.adId;
    }
    public void setAdId(String adId) {
        this.adId = adId;
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

    @Override
    public int compareTo(AdOriginConfigBean o) {
        if (o == null) {
            return 1;
        }
        if (getGrade().getBytes()[0] > o.getGrade().getBytes()[0]) {
            return 1;
        } else if (getGrade().getBytes()[0] < o.getGrade().getBytes()[0]) {
            return -1;
        } else {
            return 0;
        }
    }
    public int getAdSite() {
        return this.adSite;
    }
    public void setAdSite(int adSite) {
        this.adSite = adSite;
    }
}
