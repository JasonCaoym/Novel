package com.duoyue.app.notification.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NotifyPushListBean implements Serializable {

    static final long serialVersionUID = 0x434282L;

    @SerializedName("bookList")
    private List<NotifyPushBean> list;

    public List<NotifyPushBean> getList() {
        return list;
    }

    public void setList(List<NotifyPushBean> list) {
        this.list = list;
    }
}
