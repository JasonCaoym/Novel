package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.app.bean.SearchCountBean;
import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@AutoPost(action = "/app/stats/v1/searchCount", domain = DomainType.BUSINESS)
public class SearchCountReq extends JsonRequest {

    @SerializedName("searchList")
    private List<SearchCountBean> searchList;
    @SerializedName("nonce")
    private String batchNumber;

    public List<SearchCountBean> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<SearchCountBean> searchList) {
        this.searchList = searchList;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

}
