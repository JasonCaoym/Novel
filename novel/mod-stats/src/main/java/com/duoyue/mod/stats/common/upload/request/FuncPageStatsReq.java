package com.duoyue.mod.stats.common.upload.request;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 1.1.8新增的页面统计
 */
@AutoPost(action = "/app/stats/v1/page", domain = DomainType.BUSINESS)
public class FuncPageStatsReq extends JsonRequest {

    /**
     * 批次号
     */
    @SerializedName("nonce")
    private String batchNumber;
    private List<FuncPageStatsInfo> pageStats;


    public List<FuncPageStatsInfo> getPageStats() {
        return pageStats;
    }

    public void setPageStats(List<FuncPageStatsInfo> pageStats) {
        this.pageStats = pageStats;
    }

    public FuncPageStatsReq(String batchNumber, List<FuncPageStatsInfo> statsInfoList) {
        this.batchNumber = batchNumber;
        this.pageStats = statsInfoList;
    }
}
