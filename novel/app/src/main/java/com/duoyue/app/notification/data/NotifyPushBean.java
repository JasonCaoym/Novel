package com.duoyue.app.notification.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotifyPushBean implements Serializable {

    static final long serialVersionUID = 0x43442L;

    @SerializedName("bookId")
    private long bookId;
    @SerializedName("bookName")
    private String bookName;
    @SerializedName("authorName")
    private String authorName;

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
