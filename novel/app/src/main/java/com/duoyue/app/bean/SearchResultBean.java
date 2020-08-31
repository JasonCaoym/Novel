package com.duoyue.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Random;

public class SearchResultBean implements Parcelable {

    private int bookId;
    private String bookName;
    private String bookCover;
    private String resume;
    private int chapterCount;
    private int wordCount;
    private double star;
    private String isFinish;
    private int lastChapter;
    private String updateTimes;
    private String currTime;
    private String author;
    private String from;
    private String billboardTitle;
    private String category;
    private int random = new Random().nextInt(95 - 80) + 80;

    private int type;


    private List<SearchRecommdBookBean> searchRecommdBookBeans;


    public SearchResultBean(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
        bookCover = in.readString();
        resume = in.readString();
        chapterCount = in.readInt();
        wordCount = in.readInt();
        star = in.readDouble();
        isFinish = in.readString();
        lastChapter = in.readInt();
        updateTimes = in.readString();
        currTime = in.readString();
        author = in.readString();
        from = in.readString();
        billboardTitle = in.readString();
        category = in.readString();
    }

    public static final Creator<SearchResultBean> CREATOR = new Creator<SearchResultBean>() {
        @Override
        public SearchResultBean createFromParcel(Parcel in) {
            return new SearchResultBean(in);
        }

        @Override
        public SearchResultBean[] newArray(int size) {
            return new SearchResultBean[size];
        }
    };

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getUpdateTimes() {
        return updateTimes;
    }

    public void setUpdateTimes(String updateTimes) {
        this.updateTimes = updateTimes;
    }

    public String getCurrTime() {
        return currTime;
    }

    public void setCurrTime(String currTime) {
        this.currTime = currTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBillboardTitle() {
        return billboardTitle;
    }

    public void setBillboardTitle(String billboardTitle) {
        this.billboardTitle = billboardTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public List<SearchRecommdBookBean> getSearchRecommdBookBeans() {
        return searchRecommdBookBeans;
    }

    public void setSearchRecommdBookBeans(List<SearchRecommdBookBean> searchRecommdBookBeans) {
        this.searchRecommdBookBeans = searchRecommdBookBeans;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
        dest.writeString(bookCover);
        dest.writeString(resume);
        dest.writeInt(chapterCount);
        dest.writeInt(wordCount);
        dest.writeDouble(star);
        dest.writeString(isFinish);
        dest.writeInt(lastChapter);
        dest.writeString(updateTimes);
        dest.writeString(currTime);
        dest.writeString(author);
        dest.writeString(from);
        dest.writeString(billboardTitle);
        dest.writeString(category);
    }


}
