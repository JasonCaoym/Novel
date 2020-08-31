package com.duoyue.app.common.data.request.bookrecord;

import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.dao.BookRecordBean;

/**
 * @author caoym
 * @data 2019/4/16  14:20
 */
public class AddBookRecordInfoReq
{
    /**
     * 书籍id
     */
    @SerializedName("bookId")
    private long mBookId;

    /**
     * 阅读的章节
     */
    @SerializedName("lastReadChapter")
    private int mLastReadChapter;

    /**
     * 阅读章节名称.
     */
    @SerializedName("lastReadChapterName")
    private String mLastReadChapterName;

    /**
     * 当前多少页,未知则传-1
     */
    @SerializedName("pageNum")
    private int mPageNum;

    /**
     * 上次服务器下发的最新章节
     */
    @SerializedName("lastPushChapter")
    private int mLastPushChapter;

    /**
     * 记录阅读历史记录时间.
     */
    @SerializedName("clientLastReadTime")
    private long mClientLastReadTime;

    /**
     * 构造方法
     * @param bookRecordBean
     * @throws Throwable
     */
    public AddBookRecordInfoReq(BookRecordBean bookRecordBean) throws Throwable
    {
        mBookId = Long.valueOf(bookRecordBean.getBookId());
        mLastReadChapter = bookRecordBean.getSeqNum();
        mLastReadChapterName = bookRecordBean.getChapterTitle();
        mPageNum = bookRecordBean.getPagePos();
        mLastPushChapter = bookRecordBean.getChapterCount();
        mClientLastReadTime = bookRecordBean.getLastRead();
    }
}
