package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.bean.BookItemBean;

import java.util.List;

public class BookListBean {

    @SerializedName("books")
    private List<BookCityItemBean> list;

    public List<BookCityItemBean> getList() {
        return list;
    }

    public void setList(List<BookCityItemBean> list) {
        this.list = list;
    }
}
