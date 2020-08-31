package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedConfigBean {

    @SerializedName("configList")
    private List<FeedConfigItemBean> configList;

    public List<FeedConfigItemBean> getConfigList() {
        return configList;
    }

    public void setConfigList(List<FeedConfigItemBean> configList) {
        this.configList = configList;
    }
}
