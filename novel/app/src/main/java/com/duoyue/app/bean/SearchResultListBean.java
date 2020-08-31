package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResultListBean {

    @SerializedName("list")
    private List<SearchResultBean> commentList;
    @SerializedName("authInfo")
    private SearchResultAuthBean authInfo;
    @SerializedName("total")
    private int total;
    @SerializedName("nextCursor")
    private int nextCursor;

    public List<SearchResultBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<SearchResultBean> commentList) {
        this.commentList = commentList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(int nextCursor) {
        this.nextCursor = nextCursor;
    }

    public SearchResultAuthBean getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(SearchResultAuthBean authInfo) {
        this.authInfo = authInfo;
    }

}
