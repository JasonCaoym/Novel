package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/find/v1/peopleNearby", domain = DomainType.BUSINESS)
public class BookNewBookListHeaderReq extends JsonRequest {


    private int nextPage;


    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }
}
