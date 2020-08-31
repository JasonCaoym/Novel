package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchV2MoreListBean {

    @SerializedName("list")
    private List<SearchV2MoreBean> moreList;

    
    @SerializedName("hotSearchTitle")
    private String hotSearchTitle;

    public List<SearchV2MoreBean> getMoreList() {
        return moreList;
    }

    public void setMoreList(List<SearchV2MoreBean> moreList) {
        this.moreList = moreList;
    }

    public String getHotSearchTitle() {
        return hotSearchTitle;
    }

    public void setHotSearchTitle(String hotSearchTitle) {
        this.hotSearchTitle = hotSearchTitle;
    }
}
