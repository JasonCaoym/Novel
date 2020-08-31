package com.duoyue.mod.ad.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author fanwentao
 * @data 2019-08-07
 * 新版广告平台
 */
public class AdResponseBean {

    /**
     * status : ok
     * info : {"channelCode":"sk0001_sk01","dayShowInteval":30,"dayShowLimit":100,"showtimeRange":"0,23","advertiseResultVoList":[{"id":2,"linkType":5,"jsStyle":1,"carrierOperator":"4","priority":10,"origin":1,"adType":1,"adAppId":1101152570,"adId":8863364436303842593,"showInteval":1,"clickInteval":1},{"id":3,"linkType":5,"jsStyle":1,"carrierOperator":"4","priority":5,"origin":2,"adType":4,"adAppId":5001121,"adId":901121737,"showInteval":1,"clickInteval":1}]}
     */

    private String status;
    @SerializedName("info")
    private AdConfigBean info;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AdConfigBean getInfo() {
        return info;
    }

    public void setInfo(AdConfigBean info) {
        this.info = info;
    }

}
