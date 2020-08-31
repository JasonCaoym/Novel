package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/search/v1/authBookList", domain = DomainType.BUSINESS)
public class SearchResultAuthListReq extends JsonRequest {

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("currentCursor")
    private int currentCursor;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getCurrentCursor() {
        return currentCursor;
    }

    public void setCurrentCursor(int currentCursor) {
        this.currentCursor = currentCursor;
    }

}
