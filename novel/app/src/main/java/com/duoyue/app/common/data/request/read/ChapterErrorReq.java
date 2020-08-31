package com.duoyue.app.common.data.request.read;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
@AutoPost(action = "/app/chatper/v1/error", domain = DomainType.BUSINESS)
public class ChapterErrorReq extends JsonRequest {
    @SerializedName("bookId")
    private String bookId;
    @SerializedName("seqNum")
    private int seqNum;
    @SerializedName("bookName")
    private String bookName;
    @SerializedName("chapterTitle")
    private String chapterTitle;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }
}
