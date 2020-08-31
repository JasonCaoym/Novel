package com.duoyue.app.common.data.request.bookcity;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
@AutoPost(action = "/app/rankingList/v1/get", domain = DomainType.BUSINESS)
public class BookCategoryReq extends JsonRequest
{
    @SerializedName("sex")
    public int sex;

}
