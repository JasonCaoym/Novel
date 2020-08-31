package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchV2ListBean {

    @SerializedName("list")
    private List<SearchHotBean> commentList;


    @SerializedName("billboardTitle")
    private String billboardTitle;

    public List<SearchHotBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<SearchHotBean> commentList) {
        this.commentList = commentList;
    }

    public String getBillboardTitle() {
        return billboardTitle;
    }

    public void setBillboardTitle(String billboardTitle) {
        this.billboardTitle = billboardTitle;
    }
}
