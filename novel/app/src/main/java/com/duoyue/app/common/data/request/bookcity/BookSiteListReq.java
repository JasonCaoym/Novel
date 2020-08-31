package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/suspension/v1/site", domain = DomainType.BUSINESS)
public class BookSiteListReq extends JsonRequest {

    @SerializedName("site")
    public long site;

    @SerializedName("chan")
    public int chan;
}
