package com.duoyue.app.bean;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookRankListBean {

    @SerializedName("items")
    private List<BookRankItemBean> list;

    public List<BookRankItemBean> getList() {
        return list;
    }

    public void setList(List<BookRankItemBean> list) {
        this.list = list;
    }
}
