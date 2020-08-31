package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentListBean {

    @SerializedName("commentList")
    private List<CommentItemBean> commentList;


    public List<CommentItemBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentItemBean> commentList) {
        this.commentList = commentList;
    }
}
