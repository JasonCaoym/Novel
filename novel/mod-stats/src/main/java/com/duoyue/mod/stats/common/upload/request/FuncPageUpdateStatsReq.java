package com.duoyue.mod.stats.common.upload.request;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 1.2.3新增的更新统计
 */
@AutoPost(action = "/app/stats/v1/upgradeCount", domain = DomainType.BUSINESS)
public class FuncPageUpdateStatsReq extends JsonRequest {
    @SerializedName("nonce")
    private String batchNumber;
    private List<FuncPageStatsInfo> pageStats;

    public List<FuncPageStatsInfo> getPageStats() {
        return pageStats;
    }

    public void setPageStats(List<FuncPageStatsInfo> pageStats) {
        this.pageStats = pageStats;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
}
