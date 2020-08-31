package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

public class BookBannerItemBean {

    @SerializedName("bookId")
    private long bookId;
    @SerializedName("jumpType")
    private int type;
    @SerializedName("link")
    private String link;
    @SerializedName("cover")
    private String cover;
    /**
     * 推广编号.
     */
    @SerializedName("popId")
    private int popId;

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getPopId() {
        return popId;
    }
}
