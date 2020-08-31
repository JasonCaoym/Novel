package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchRecommdBookListBean {

    @SerializedName("bookList")
    private List<SearchRecommdBookBean> bookList;

    public List<SearchRecommdBookBean> getBookList() {
        return bookList;
    }

    public void setBookList(List<SearchRecommdBookBean> bookList) {
        this.bookList = bookList;
    }
}
