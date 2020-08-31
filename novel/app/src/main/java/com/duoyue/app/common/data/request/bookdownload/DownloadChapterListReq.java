package com.duoyue.app.common.data.request.bookdownload;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/chapterDownload/v1/downList", domain = DomainType.BUSINESS)
public class DownloadChapterListReq extends JsonRequest {

    @SerializedName("bookId")
    public long bookId;

    @SerializedName("seqNum")
    public int seqNum;      //起始当前章节序号

    @SerializedName("countNum")
    public int countNum;    //需要下载的章节条数

}
