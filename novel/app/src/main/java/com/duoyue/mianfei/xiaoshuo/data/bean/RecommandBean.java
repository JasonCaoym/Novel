package com.duoyue.mianfei.xiaoshuo.data.bean;

import java.io.Serializable;

public class RecommandBean implements Serializable {
    private long bookId;
    private int jumpType;
    private String link;
    private int status;//1 开启 2 关闭
    private String chapterTitle;
    private int lastReadChapter;
    private String authorName;
    private String weekDownPvMsg;
    private String bookName;
    public String cover;
    public int lastChapter;
    public int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public int getLastReadChapter() {
        return lastReadChapter;
    }

    public void setLastReadChapter(int lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getWeekDownPvMsg() {
        return weekDownPvMsg;
    }

    public void setWeekDownPvMsg(String weekDownPvMsg) {
        this.weekDownPvMsg = weekDownPvMsg;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }
}
