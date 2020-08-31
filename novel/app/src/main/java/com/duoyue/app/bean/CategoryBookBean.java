package com.duoyue.app.bean;

import java.io.Serializable;

public class CategoryBookBean implements Serializable {

    static final long serialVersionUID = 0x4555442L;

    private long bookId;
    private String bookName;
    private String cover;
    private String resume;
    private String authorName;
    private int state;
    private int lastChapter;
    private int popularityNum;
    private int wordCount;
    private float star;
    private String updateTime;
    private String timeTip;
    private String from;
    private int type;

    /**
     * 字数类型
     */
    private int wordCountType;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWordCountType() {
        return wordCountType;
    }

    public void setWordCountType(int wordCountType) {
        this.wordCountType = wordCountType;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
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

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public int getPopularityNum() {
        return popularityNum;
    }

    public void setPopularityNum(int popularityNum) {
        this.popularityNum = popularityNum;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTimeTip() {
        return timeTip;
    }

    public void setTimeTip(String timeTip) {
        this.timeTip = timeTip;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
