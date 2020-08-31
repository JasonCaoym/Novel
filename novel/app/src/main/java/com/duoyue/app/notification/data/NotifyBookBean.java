package com.duoyue.app.notification.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotifyBookBean implements Serializable {

    static final long serialVersionUID = 0x3334282L;

    @SerializedName("bookId")
    private String bookId; // 书籍id
    @SerializedName("cover")
    private String cover; // 书籍封面
    @SerializedName("bookName")
    private String bookName; // 书籍名称
    @SerializedName("catName")
    private String bookCategory; // 类别名称：玄幻
    @SerializedName("star")
    private String grade; // 评分9.3

    /**
     * 一级分类Id逗号分隔
     */
    @SerializedName("catId")
    private String catId;
    @SerializedName("from")
    private int from;

    /**
     * 一级分类Id逗号分隔
     */
    @SerializedName("subCatId")
    private String subCatId;


    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }
}
