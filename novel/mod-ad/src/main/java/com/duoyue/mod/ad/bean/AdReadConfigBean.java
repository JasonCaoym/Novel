package com.duoyue.mod.ad.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 阅读插屏广告参数和免广告时间
 */
@Entity
public class AdReadConfigBean {
    /**
     * appId : 13
     * paramName : RD_PAGE_MIN1
     * paramValue : 5
     */
    @Id(autoincrement = true)
    private Long _id;
    private int appId;
    private String paramName;
    private String paramValue;
    @Generated(hash = 1075493747)
    public AdReadConfigBean(Long _id, int appId, String paramName,
            String paramValue) {
        this._id = _id;
        this.appId = appId;
        this.paramName = paramName;
        this.paramValue = paramValue;
    }
    @Generated(hash = 194414163)
    public AdReadConfigBean() {
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
    public String getParamName() {
        return this.paramName;
    }
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
    public String getParamValue() {
        return this.paramValue;
    }
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    
}
