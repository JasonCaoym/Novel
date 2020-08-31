package com.zydm.base.data.dao;

import org.greenrobot.greendao.annotation.*;

import java.util.List;

@Entity
public class BookRecordBean {

    //所属的书的id
    @Id
    public String bookId;
    public String bookName;
    public String author;
    public String resume;
    public String bookCover;
    public int chapterCount;
    public long wordCount;
    public boolean isFinish;
    public long updateTime;

    //上次阅读时间
    public long lastRead;

    public String chapterTitle;
    //阅读到了第几章
    public int seqNum;
    //当前的页码
    public int pagePos;

    @Transient
    public List<List<ChapterBean>> bookChapterList;

    @Transient
    public boolean mIsInShelf;

//    @Generated(hash = 771705656)
    @Keep
    public BookRecordBean(String bookId, String bookName, String author,
            String resume, String bookCover, int chapterCount, long wordCount,
            boolean isFinish, long updateTime, long lastRead, String chapterTitle,
            int seqNum, int pagePos) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.resume = resume;
        this.bookCover = bookCover;
        this.chapterCount = chapterCount;
        this.wordCount = wordCount;
        this.isFinish = isFinish;
        this.updateTime = updateTime;
        this.lastRead = lastRead;
        this.chapterTitle = chapterTitle;
        this.seqNum = seqNum;
        this.pagePos = pagePos;
    }

//    @Generated(hash = 398068002)
    public BookRecordBean() {
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return this.bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getResume() {
        return this.resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getBookCover() {
        return this.bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public int getChapterCount() {
        return this.chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public long getWordCount() {
        return this.wordCount;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public boolean getIsFinish() {
        return this.isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getLastRead() {
        return this.lastRead;
    }

    public void setLastRead(long lastRead) {
        this.lastRead = lastRead;
    }

    public String getChapterTitle() {
        return this.chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public int getSeqNum() {
        return this.seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public int getPagePos() {
        return this.pagePos;
    }

    public void setPagePos(int pagePos) {
        this.pagePos = pagePos;
    }
}
