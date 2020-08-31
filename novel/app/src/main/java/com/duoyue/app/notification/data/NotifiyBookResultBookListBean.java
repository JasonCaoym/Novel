package com.duoyue.app.notification.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotifiyBookResultBookListBean {

    @SerializedName("bookList")
    private ArrayList<NotifiyBookResultV2Bean> bookList;


    public ArrayList<NotifiyBookResultV2Bean> getBookList() {
        return bookList;
    }

    public void setBookList(ArrayList<NotifiyBookResultV2Bean> bookList) {
        this.bookList = bookList;
    }
}
