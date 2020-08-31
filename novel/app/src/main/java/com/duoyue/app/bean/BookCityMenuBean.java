package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;
import kotlinx.android.parcel.Parcelize;

import java.util.List;

@Parcelize
public class BookCityMenuBean {
    @SerializedName("iconList")
    private List<BookCityMenuItemBean> iconList;

    private int type;

    public List<BookCityMenuItemBean> getIconList() {
        return iconList;
    }

    public void setIconList(List<BookCityMenuItemBean> iconList) {
        this.iconList = iconList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
