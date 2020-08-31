package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

public class BookCityAdBean {
    @SerializedName("id")
    private int id;
    @SerializedName("bookId")
    private long bookId;
    @SerializedName("jumpType")
    private int type;
    @SerializedName("link")
    private String link;
    @SerializedName("cover")
    private String cover;
    @SerializedName("iconPath")
    private String iconPath;
    @SerializedName("adChannelCode")
    private String adChannalCode;


    public String getAdChannalCode() {
        return adChannalCode;
    }

    public void setAdChannalCode(String adChannalCode) {
        this.adChannalCode = adChannalCode;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
