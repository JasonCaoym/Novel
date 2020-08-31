package com.duoyue.app.common.data.request.bookdownload;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/chapterDownload/v1/continueDown", domain = DomainType.BUSINESS)
public class ChapterDownloadOptionReq extends JsonRequest {

    @SerializedName("bookId")
    public long bookId;

}
