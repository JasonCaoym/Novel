package com.duoyue.mod.stats.common.upload.request;

import com.google.gson.annotations.SerializedName;

/**
 * 广告统计数据.
 * @author caoym
 * @data 2019/4/9  0:29
 */
public class AdStatsInfoReq
{
    /**
     *  节点类型(开始请求:"START"、拉取成功:"PULLED"、拉取失败:"PULLFAIL"、展示成功:"SHOWED",展示失败:"SHOWFAIL",点击广告:"CLICK)
     */
    @SerializedName("operator")
    private String mOperator;

    /**
     * 广告id
     */
    @SerializedName("adId")
    private String mAdId;

    /**
     * 广告位类型(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     */
    @SerializedName("adSite")
    private int mAdSite;

    /**
     * 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     */
    @SerializedName("adType")
    private int mAdType;

    /**
     * 广告源(1:广点通2:穿山甲 3:百度)
     */
    @SerializedName("origin")
    private int mOrigin;

    /**
     * 次数
     */
    @SerializedName("num")
    private int mNum;

    public AdStatsInfoReq()
    {
    }

    public void setOperator(String operator) {
        this.mOperator = operator;
    }

    public void setAdId(String adId) {
        this.mAdId = adId;
    }

    public void setAdSite(int adSite) {
        this.mAdSite = adSite;
    }

    public void setAdType(int adType) {
        this.mAdType = adType;
    }

    public void setOrigin(int origin) {
        this.mOrigin = origin;
    }

    public void setNum(int num) {
        this.mNum = num;
    }
}
