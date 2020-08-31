package com.zydm.base.data.dao;

import com.google.gson.annotations.SerializedName;
import org.greenrobot.greendao.annotation.*;

import java.io.Serializable;

@Entity
public class ChapterBean implements Serializable, IChapter {
    private static final long serialVersionUID = 56423411313L;

    @SerializedName("title")
    public String chapterTitle;

    @Id(autoincrement = true)
    private long _id;

    @Index
    public String bookId;

    public boolean isRead;

    @Transient
    public boolean isSelect;

    @SerializedName("id")
    public int chapterId;

    public int seqNum;

    @Transient
    public String content;

    @Transient
    public boolean isDownload;

//    @Generated(hash = 1166062441)
    @Keep
    public ChapterBean(String chapterTitle, long _id, String bookId, boolean isRead,
            int chapterId, int seqNum) {
        this.chapterTitle = chapterTitle;
        this._id = _id;
        this.bookId = bookId;
        this.isRead = isRead;
        this.chapterId = chapterId;
        this.seqNum = seqNum;
    }

//    @Generated(hash = 1028095945)
    public ChapterBean() {
    }

    public String getChapterTitle() {
        return this.chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public int getChapterId() {
        return this.chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getSeqNum() {
        return this.seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    @Override
    public String getTitle() {
        return chapterTitle;
    }

    @Override
    public boolean isSelect() {
        return isSelect;
    }

    @Override
    public boolean isRead() {
        return isRead;
    }
}
