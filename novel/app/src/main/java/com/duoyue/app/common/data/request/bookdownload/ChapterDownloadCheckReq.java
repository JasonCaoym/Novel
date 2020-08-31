package com.duoyue.app.common.data.request.bookdownload;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;


@AutoPost(action = "/app/chapterDownload/v1/check", domain = DomainType.BUSINESS)
public class ChapterDownloadCheckReq extends JsonRequest {

    @SerializedName("chapterCount")
    public int chapterCount;       //章节数量

    @SerializedName("chapterSeqNumStr")
    public String chapterSeqNumStr; //下载章节序号用逗号拼接

    @SerializedName("bookId")
    public long bookId;             //书籍id

}
