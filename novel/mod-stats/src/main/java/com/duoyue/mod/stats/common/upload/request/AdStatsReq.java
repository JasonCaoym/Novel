package com.duoyue.mod.stats.common.upload.request;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 广告统计请求类
 * @author caoym
 * @data 2019/4/8 23:38
 */
@AutoPost(action = "/app/ad/v1/adStats", domain = DomainType.BUSINESS)
public class AdStatsReq extends JsonRequest
{
    //@AutoHeader(HeaderType.USER_AGENT)
    //public transient String userAgent;
    //@AutoHeader(HeaderType.TOKEN)
    //public transient String token;

    /**
     * 批次号
     */
    @SerializedName("nonce")
    private String mBatchNumber;

    /**
     * 统计信息列表.
     */
    @SerializedName("adStatsRequest")
    private List<AdStatsInfoReq> mStatsInfoList;

    public AdStatsReq(String batchNumber, List<AdStatsInfoReq> statsInfoList)
    {
        //批次号.
        mBatchNumber = batchNumber;
        //统计数据.
        mStatsInfoList = statsInfoList;
    }
}
