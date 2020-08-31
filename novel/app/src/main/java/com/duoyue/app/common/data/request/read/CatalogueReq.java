package com.duoyue.app.common.data.request.read;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
@AutoPost(action = "/app/chapters/v1/list", domain = DomainType.BUSINESS)
public class CatalogueReq extends JsonRequest {
    @SerializedName("bookId")
    public String bookId;
    @SerializedName("count")
    public int count;
    @SerializedName("sort")
    public int sort;
    @SerializedName("startChapter")
    public int startChapter;

}
