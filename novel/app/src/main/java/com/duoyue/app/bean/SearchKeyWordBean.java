package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

public class SearchKeyWordBean {
    @SerializedName("bookId")
    public int bookId;
    @SerializedName("bookName")
    public String bookName;


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
}
