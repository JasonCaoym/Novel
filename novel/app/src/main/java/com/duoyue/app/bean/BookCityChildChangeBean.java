package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;
import kotlinx.android.parcel.Parcelize;

import java.util.List;
@Parcelize
public class BookCityChildChangeBean {
    @SerializedName("childColumns")
    private List<BookChildColumnsBean > childColumns;

    public List<BookChildColumnsBean> getChildColumns() {
        return childColumns;
    }

    public void setChildColumns(List<BookChildColumnsBean> childColumns) {
        this.childColumns = childColumns;
    }
}
