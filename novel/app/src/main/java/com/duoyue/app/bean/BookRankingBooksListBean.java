package com.duoyue.app.bean;

import android.view.ViewGroup;
import kotlinx.android.parcel.Parcelize;

import java.util.List;

@Parcelize
public class BookRankingBooksListBean {

    private int classId;
    private String rankingName;
    private String mark;
    private int columnType;
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;

    private List<BookCityItemBean> books;


    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getRankingName() {
        return rankingName;
    }

    public void setRankingName(String rankingName) {
        this.rankingName = rankingName;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public List<BookCityItemBean> getBooks() {
        return books;
    }

    public void setBooks(List<BookCityItemBean> books) {
        this.books = books;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
