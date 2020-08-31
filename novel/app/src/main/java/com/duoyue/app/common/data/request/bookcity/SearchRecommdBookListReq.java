package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/search/v1/recommdBookList", domain = DomainType.BUSINESS)
public class SearchRecommdBookListReq extends JsonRequest {

    @SerializedName("bookIds")
    private String bookIds;

    public String getBookIds() {
        return bookIds;
    }

    public void setBookIds(String bookIds) {
        this.bookIds = bookIds;
    }
}
