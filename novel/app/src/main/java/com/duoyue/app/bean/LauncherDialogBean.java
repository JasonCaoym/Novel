package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LauncherDialogBean implements Serializable {

    static final long serialVersionUID = 0x9493L;

    @SerializedName("bookId")
    private long bookId;
    @SerializedName("bookName")
    private String bookName;
    @SerializedName("cover")
    private String cover;
    @SerializedName("authorName")
    private String authorName;
    @SerializedName("state")
    private int state;
    @SerializedName("weekDownPvMsg")
    private String weekDownPv;
    @SerializedName("resume")
    private String resume;

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

    public String getWeekDownPv() {
        return weekDownPv;
    }

    public void setWeekDownPv(String weekDownPv) {
        this.weekDownPv = weekDownPv;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }
}
