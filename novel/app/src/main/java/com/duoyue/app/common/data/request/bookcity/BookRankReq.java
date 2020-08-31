package com.duoyue.app.common.data.request.bookcity;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
@AutoPost(action = "/app/rankingList/v1/list", domain = DomainType.BUSINESS)
public class BookRankReq extends JsonRequest
{
    @SerializedName("classId")
    public long categoryId;

    @SerializedName("quePages")
    public int pageIndex;

}
