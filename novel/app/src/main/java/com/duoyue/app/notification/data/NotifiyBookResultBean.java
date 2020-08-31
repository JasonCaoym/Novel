package com.duoyue.app.notification.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NotifiyBookResultBean implements Serializable {

    static final long serialVersionUID = 0x3331182L;

    @SerializedName("male")
    private List<NotifyBookBean> maleBookList;
    @SerializedName("female")
    private List<NotifyBookBean> femaleBookList;

    public List<NotifyBookBean> getMaleBookList() {
        return maleBookList;
    }

    public void setMaleBookList(List<NotifyBookBean> maleBookList) {
        this.maleBookList = maleBookList;
    }

    public List<NotifyBookBean> getFemaleBookList() {
        return femaleBookList;
    }

    public void setFemaleBookList(List<NotifyBookBean> femaleBookList) {
        this.femaleBookList = femaleBookList;
    }
}
