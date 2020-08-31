package com.duoyue.app.common.data.request.bookshelf;

import com.google.gson.annotations.SerializedName;

/**
 * @author caoym
 * @data 2019/4/15  10:24
 */
public class AddBookShelfInfoReq
{
    @SerializedName("bookId")
    private long mBookId;

    /**
     * 阅读章节
     */
    @SerializedName("lastReadChapter")
    private int mLastReadChapter;

    /**
     * 当前阅读到多少页, 未知则传0
     */
    @SerializedName("pageNum")
    private int mPageNum;

    /**
     * 上次下发的最新章节
     */
    @SerializedName("lastPushChapter")
    private int mLastPushChapter;

    /**
     * 添加方式(0:普通的添加到书架;1:置顶;2:取消置顶)
     */
    @SerializedName("topping")
    private int mTopping;

    /**
     * 客户端上报上次阅读时间
     */
    @SerializedName("clientLastReadTime")
    private long mClientLastReadTime;

    /**
     * 阅读章节名称
     */
    @SerializedName("lastReadChapterName")
    private String mLastReadChapterName;

    public AddBookShelfInfoReq(long bookId, int lastReadChapter, int pageNum, int lastPushChapter, int topping, long clientLastReadTime, String lastReadChapterName)
    {
        mBookId = bookId;
        mLastReadChapter = lastReadChapter > 0 ? lastReadChapter : 0;
        mPageNum = pageNum > 0 ? pageNum : 0;
        mLastPushChapter = lastPushChapter > 0 ? lastPushChapter : 0;
        mTopping = topping;
        mClientLastReadTime = clientLastReadTime > 0 ? clientLastReadTime : 0;
        mLastReadChapterName = lastReadChapterName;
    }
}
