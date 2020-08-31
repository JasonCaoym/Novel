package com.duoyue.app.common.data.response.bookshelf;

import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.BookShelfBean;

/**
 * 更新书架书籍信息
 * @author caoym
 * @data 2019/3/30  16:52
 */
public class BookShelfBookInfoResp
{
    /**
     * 书籍id
     */
    private long bookId;

    /**
     * 书籍名称
     */
    private String name;

    /**
     * 书籍的封面
     */
    private String cover;

    /**
     * 简介
     */
    private String resume;

    /**
     * 书籍状态(1:更新中;2:已完结;3:断更).
     */
    private int state;

    /**
     * 书籍的最新章节
     */
    private int lastChapter;

    /**
     * 上次服务器端下发给前端的最新章节
     */
    private int lastPushChapter;

    /**
     * 上次阅读的章节
     */
    private int lastReadChapter;

    /**
     * 上次阅读时间
     */
    private long lastReadTime;

    /**
     * 置顶时间(0表示未置顶)
     */
    private long toppingTime;

    //===============非协议字段================
    /**
     * 书籍类型(1:普通书籍;2:推荐书籍).
     */
    private int type = 1;

    public BookShelfBookInfoResp()
    {

    }

    public BookShelfBookInfoResp(BookShelfBean bookShelfBean)
    {
        if (bookShelfBean == null)
        {
            return;
        }
        //书籍id
        setBookId(Long.parseLong(bookShelfBean.getBookId()));
        //书籍名称
        setBookName(bookShelfBean.getBookName());
        //书籍的封面
        setBookCover(bookShelfBean.getBookCover());
        //书籍的简介
        setResume(bookShelfBean.getResume());
        //书籍状态(1:更新中;2:已完结;3:断更).
        if (bookShelfBean.isFinish)
        {
            //已完结
            setState(2);
        } else
        {
            //更新中
            setState(1);
        }
        //书籍的最新章节
        setLastChapter(bookShelfBean.getChapterCount());
        //上次服务器端下发给前端的最新章节
        setLastPushChapter(bookShelfBean.getChapterCount());;
        //上次阅读的章节
        setLastReadChapter(bookShelfBean.getReadChapter());
        //上次阅读时间
        setLastReadTime(bookShelfBean.getLastReadTime());
    }

    /**
     * 转换为BookShelfBean
     * @return
     */
    public BookShelfBean toBookShelfBean()
    {
        BookShelfBean bookShelfBean = new BookShelfBean();
        //书籍id
        bookShelfBean.setBookId(String.valueOf(getBookId()));
        //书籍名称
        bookShelfBean.setBookName(getBookName());
        //书籍的封面
        bookShelfBean.setBookCover(getBookCover());
        //书籍的最新章节
        bookShelfBean.setChapterCount(getLastChapter());
        //书籍简介
        bookShelfBean.setResume(getResume());
        //书籍状态
        bookShelfBean.setIsFinish(getState() == 2);
        //上次阅读时间
        bookShelfBean.setLastReadTime(getLastReadTime());
        //上次阅读章节
        bookShelfBean.setReadChapter(getLastReadChapter());
        return bookShelfBean;
    }

    /**
     * 更新书籍阅读信息.
     * @param recordBean
     */
    public boolean updateBookRecordInfo(BookRecordBean recordBean)
    {
        if (recordBean == null)
        {
            return false;
        }
        //上次阅读的章节
        setLastReadChapter(recordBean.getSeqNum());
        //上次阅读时间
        setLastReadTime(recordBean.getLastRead());
        return true;
    }

    public long getBookId()
    {
        return bookId;
    }

    public void setBookId(long bookId)
    {
        this.bookId = bookId;
    }

    public String getBookName()
    {
        return name;
    }

    public void setBookName(String bookName)
    {
        this.name = bookName;
    }

    public String getBookCover()
    {
        return cover;
    }

    public void setBookCover(String bookCover)
    {
        this.cover = bookCover;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }

    public int getLastPushChapter() {
        return lastPushChapter;
    }

    public void setLastPushChapter(int lastPushChapter) {
        this.lastPushChapter = lastPushChapter;
    }

    public int getLastReadChapter() {
        return lastReadChapter;
    }

    public void setLastReadChapter(int lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public long getToppingTime() {
        return toppingTime;
    }

    public void setToppingTime(long toppingTime) {
        this.toppingTime = toppingTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
