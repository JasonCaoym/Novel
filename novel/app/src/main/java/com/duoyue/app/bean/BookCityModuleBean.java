package com.duoyue.app.bean;

import kotlinx.android.parcel.Parcelize;

import java.util.List;

@Parcelize
public class BookCityModuleBean {

    // 0:1+4封面  1: 3行 2:1+4宫格
    public static final int ONE_2_FOUR = 0;
    public static final int THREE = 1;
    public static final int ONE_2_DOUBLE = 2;
    public static final int ONE = 3;
    public static final int ONE_V2_FOUR = 4;
    public static final int ONE_N = 5;

    //0:精选;1:男生;2:女生
    private int type;
    //0:人工;1:非人工
    private int mtType;

    private String title;
    private String id;
    private int style;
    //人气 RQJN RQJV    时间 SJJN  SJJV
    private String tag;
    private String typeId;
    private boolean isLastPosition;
    //    @Convert(/**指定转换器**/converter = BookCityConverter.class,/**指定数据库中的列字段**/columnType =String.class )

    private List<BookChildColumnsBean> childColumns;

    public boolean isLastPosition() {
        return isLastPosition;
    }

    public void setLastPosition(boolean lastPosition) {
        isLastPosition = lastPosition;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

//    public List<BookCityItemBean> getBooks() {
//        return books;
//    }
//
//    public void setBooks(List<BookCityItemBean> books) {
//        this.books = books;
//    }

    public List<BookChildColumnsBean> getChildColumns() {
        return childColumns;
    }

    public void setChildColumns(List<BookChildColumnsBean> childColumns) {
        this.childColumns = childColumns;
    }

    public int getMtType() {
        return mtType;
    }

    public void setMtType(int mtType) {
        this.mtType = mtType;
    }
}
