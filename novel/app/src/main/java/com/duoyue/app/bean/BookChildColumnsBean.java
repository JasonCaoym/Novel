package com.duoyue.app.bean;

import android.view.ViewGroup;
import com.google.gson.annotations.SerializedName;
import kotlinx.android.parcel.Parcelize;

import java.util.List;
@Parcelize
public class BookChildColumnsBean {

    @SerializedName("childColumnName")
    private String childColumnName;

    private int index;
    @SerializedName("books")
    private List<BookCityItemBean> books;


    private int classId;

    private int columnType;

    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;

    private String desc;
    public List<BookCityItemBean> getBooks() {
        return books;
    }

    public void setBooks(List<BookCityItemBean> books) {
        this.books = books;
    }


    public String getChildColumnName() {
        return childColumnName;
    }

    public void setChildColumnName(String childColumnName) {
        this.childColumnName = childColumnName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }
}
