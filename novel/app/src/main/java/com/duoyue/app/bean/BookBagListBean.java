package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookBagListBean {


    @SerializedName("list")
    private List<BookNewUserBagStatusesBean> list;


    public List<BookNewUserBagStatusesBean> getList() {
        return list;
    }

    public void setList(List<BookNewUserBagStatusesBean> list) {
        this.list = list;
    }
}
