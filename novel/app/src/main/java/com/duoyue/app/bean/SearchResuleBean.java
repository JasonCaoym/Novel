package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResuleBean {

    @SerializedName("bookBeanList")
    private List<SearchKeyWordBean> moreList;

    @SerializedName("authNameList")
    private List<String> authList;

    public List<SearchKeyWordBean> getMoreList() {
        return moreList;
    }

    public void setMoreList(List<SearchKeyWordBean> moreList) {
        this.moreList = moreList;
    }

    public List<String> getAuthList() {
        return authList;
    }

    public void setAuthList(List<String> authList) {
        this.authList = authList;
    }
}
