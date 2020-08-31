package com.duoyue.app.common.data.response.bookrecord;

import com.zydm.base.data.dao.BookRecordBean;

/**
 * 历史阅读记录信息
 * @author caoym
 * @data 2019/4/22  09:52
 */
public class BookRecordInfoResp
{
    /**
     * 书籍Id.
     */
    private long bookId;

    /**
     * 书籍名称.
     */
    private String name;

    /**
     * 书籍封面图.
     */
    private String cover;

    /**
     * 书籍状态(1:更新中;2:已完结;3:断更).
     */
    private int state;

    /**
     * 书籍的最新章节
     */
    private int lastChapter;

    /**
     * 最后阅读章节
     */
    private int lastReadChapter;

    /**
     * 最后阅读章节名称
     */
    private String lastReadChapterName;

    /**
     * 最后阅读时间
     */
    private long lastReadTime;

    /**
     * 最后阅读页数.
     */
    private int pageNum;

    public BookRecordInfoResp()
    {
    }

    public long getBookId() {
        return bookId;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }

    public int getState()
    {
        return state;
    }

    public int getLastChapter()
    {
        return lastChapter;
    }

    public int getLastReadChapter() {
        return lastReadChapter;
    }

    public String getLastReadChapterName() {
        return lastReadChapterName;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public int getPageNum() {
        return pageNum;
    }

    public BookRecordBean toBookRecordBean()
    {
        BookRecordBean bookRecordBean = new BookRecordBean();
        //书籍Id.
        bookRecordBean.setBookId(String.valueOf(getBookId()));
        //书籍名称.
        bookRecordBean.setBookName(getName());
        //书籍封面图.
        bookRecordBean.setBookCover(getCover());
        //书籍是否完结.
        bookRecordBean.setIsFinish(getState() == 2);
        //最新章节数.
        bookRecordBean.setChapterCount(getLastChapter());
        //最后阅读章节
        bookRecordBean.setSeqNum(getLastReadChapter());
        //最后阅读章节名称
        bookRecordBean.setChapterTitle(getLastReadChapterName());
        //最后阅读时间
        bookRecordBean.setLastRead(getLastReadTime());
        //最后阅读页数.
        bookRecordBean.setPagePos(getPageNum());
        return bookRecordBean;
    }
}
