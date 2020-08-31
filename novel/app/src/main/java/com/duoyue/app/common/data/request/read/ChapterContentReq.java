package com.duoyue.app.common.data.request.read;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/chatper/v1/detail", domain = DomainType.BUSINESS)
public class ChapterContentReq extends JsonRequest {
    @SerializedName("bookId")
    public String bookId;
    @SerializedName("seqNum")
    public int seqNum;

}
