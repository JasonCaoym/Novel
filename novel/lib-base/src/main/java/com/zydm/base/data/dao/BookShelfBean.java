package com.zydm.base.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import com.zydm.base.data.base.IIdGetter;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BookShelfBean implements IIdGetter, Parcelable {

    @Id
    public String bookId;
    public String bookName = "";
    public String bookCover = "";
    public String resume = "";
    public int chapterCount = 0;
    public long wordCount = 0;
    public boolean isFinish = false;
    public long updateTime = 0;
    public long currTime = 0;
    public long addTime = 0;
    public String author = "";
    public boolean isAddLocalDb;    //是否只是本地保存，未同步到服务器

//    @Transient
    private int mReadChapter;

    @Transient
    public boolean mIsUpdate;

    @Transient
    public boolean mIsSelect;

    @Transient
    public boolean mIsInShelf;

    @Transient
    public boolean mIsEditMode;

//    @Transient
    private long lastReadTime;

    //    @Generated(hash = 1143830554)
    public BookShelfBean(String bookId, String bookName, String bookCover, String resume,
                         int chapterCount, long wordCount, boolean isFinish, long updateTime,
                         long currTime, long addTime, String author, int readChapter, long lastReadTime) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookCover = bookCover;
        this.resume = resume;
        this.chapterCount = chapterCount;
        this.wordCount = wordCount;
        this.isFinish = isFinish;
        this.updateTime = updateTime;
        this.currTime = currTime;
        this.addTime = addTime;
        this.author = author;
        this.mReadChapter = readChapter;
        this.lastReadTime = lastReadTime;
    }


    @Keep
    public BookShelfBean(String bookId, String bookName, String bookCover, String resume,
                         int chapterCount, long wordCount, boolean isFinish, long updateTime,
                         long currTime, long addTime, String author, boolean isAddLocalDb) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookCover = bookCover;
        this.resume = resume;
        this.chapterCount = chapterCount;
        this.wordCount = wordCount;
        this.isFinish = isFinish;
        this.updateTime = updateTime;
        this.currTime = currTime;
        this.addTime = addTime;
        this.author = author;
        this.isAddLocalDb = isAddLocalDb;
    }

//    @Generated(hash = 1462228839)
    public BookShelfBean() {
    }

    @Override
    public String getId() {
        return bookId;
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

    public String getBookCover() {
        return this.bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getResume() {
        return this.resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
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

    public long getCurrTime() {
        return this.currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }

    public long getAddTime() {
        return this.addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getReadChapter() {
        return mReadChapter;
    }

    public void setReadChapter(int readChapter) {
        this.mReadChapter = readChapter;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public boolean getIsAddLocalDb() {
        return isAddLocalDb;
    }

    public void setIsAddLocalDb(boolean addLocalDb) {
        isAddLocalDb = addLocalDb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookId);
        dest.writeString(this.bookName);
        dest.writeString(this.bookCover);
        dest.writeString(this.resume);
        dest.writeInt(this.chapterCount);
        dest.writeLong(this.wordCount);
        dest.writeByte(this.isFinish ? (byte) 1 : (byte) 0);
        dest.writeLong(this.updateTime);
        dest.writeLong(this.currTime);
        dest.writeLong(this.addTime);
        dest.writeString(this.author);
        dest.writeByte(this.isAddLocalDb ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mReadChapter);
        dest.writeByte(this.mIsUpdate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsSelect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsInShelf ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsEditMode ? (byte) 1 : (byte) 0);
        dest.writeLong(this.lastReadTime);
    }


    public int getMReadChapter() {
        return this.mReadChapter;
    }


    public void setMReadChapter(int mReadChapter) {
        this.mReadChapter = mReadChapter;
    }

    protected BookShelfBean(Parcel in) {
        this.bookId = in.readString();
        this.bookName = in.readString();
        this.bookCover = in.readString();
        this.resume = in.readString();
        this.chapterCount = in.readInt();
        this.wordCount = in.readLong();
        this.isFinish = in.readByte() != 0;
        this.updateTime = in.readLong();
        this.currTime = in.readLong();
        this.addTime = in.readLong();
        this.author = in.readString();
        this.isAddLocalDb = in.readByte() != 0;
        this.mReadChapter = in.readInt();
        this.mIsUpdate = in.readByte() != 0;
        this.mIsSelect = in.readByte() != 0;
        this.mIsInShelf = in.readByte() != 0;
        this.mIsEditMode = in.readByte() != 0;
        this.lastReadTime = in.readLong();
    }


    @Generated(hash = 754231255)
    public BookShelfBean(String bookId, String bookName, String bookCover, String resume, int chapterCount,
            long wordCount, boolean isFinish, long updateTime, long currTime, long addTime, String author,
            boolean isAddLocalDb, int mReadChapter, long lastReadTime) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookCover = bookCover;
        this.resume = resume;
        this.chapterCount = chapterCount;
        this.wordCount = wordCount;
        this.isFinish = isFinish;
        this.updateTime = updateTime;
        this.currTime = currTime;
        this.addTime = addTime;
        this.author = author;
        this.isAddLocalDb = isAddLocalDb;
        this.mReadChapter = mReadChapter;
        this.lastReadTime = lastReadTime;
    }

    public static final Creator<BookShelfBean> CREATOR = new Creator<BookShelfBean>() {
        @Override
        public BookShelfBean createFromParcel(Parcel source) {
            return new BookShelfBean(source);
        }

        @Override
        public BookShelfBean[] newArray(int size) {
            return new BookShelfBean[size];
        }
    };
}
