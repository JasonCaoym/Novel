package com.duoyue.app.common.data.request.bookdownload;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

/**
 * 书籍下载章节列表
 */

@AutoPost(action = "/app/chapterDownload/v1/list", domain = DomainType.BUSINESS)
public class ChapterDownloadReq extends JsonRequest {

    @SerializedName("quePages")
    public int quePages;   //当前页面

    @SerializedName("bookId")
    public long bookId;

    /**
     * 0 正序 1倒序
     */
    @SerializedName("order")
    public int order;

}
