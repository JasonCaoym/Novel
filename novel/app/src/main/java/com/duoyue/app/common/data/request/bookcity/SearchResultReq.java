package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/search/v1/suggestion", domain = DomainType.BUSINESS)
public class SearchResultReq extends JsonRequest {

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("searchType")
    private int searchType;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }
}
