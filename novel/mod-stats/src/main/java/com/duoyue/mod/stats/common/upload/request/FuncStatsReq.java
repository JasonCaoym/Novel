package com.duoyue.mod.stats.common.upload.request;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 功能统计请求类
 * @author caoym
 * @data 2019/4/8 23:38
 */
@AutoPost(action = "/app/stats/v1/count", domain = DomainType.BUSINESS)
public class FuncStatsReq extends JsonRequest
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
    @SerializedName("stats")
    private List<FuncStatsInfoReq> mStatsInfoList;

    public FuncStatsReq(String batchNumber, List<FuncStatsInfoReq> statsInfoList)
    {
        //批次号.
        mBatchNumber = batchNumber;
        //统计数据.
        mStatsInfoList = statsInfoList;
    }
}
