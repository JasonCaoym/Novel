package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

public class SearchV2MoreBean {

    @SerializedName("word")
    private String word;

    @SerializedName("from")
    private String from;

    @SerializedName("isShowIcon")
    private int isShowIcon;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getIsShowIcon() {
        return isShowIcon;
    }

    public void setIsShowIcon(int isShowIcon) {
        this.isShowIcon = isShowIcon;
    }
}
