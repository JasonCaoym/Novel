package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecomHotBean {

    @SerializedName("bookList")
    private List<RecomHotItemBean> list;

    public List<RecomHotItemBean> getList() {
        return list;
    }

    public void setList(List<RecomHotItemBean> list) {
        this.list = list;
    }
}
