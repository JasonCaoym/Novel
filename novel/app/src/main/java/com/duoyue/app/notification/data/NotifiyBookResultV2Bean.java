package com.duoyue.app.notification.data;

import java.io.Serializable;

public class NotifiyBookResultV2Bean implements Serializable {

    private int bookId;
    private String bookName;
    private String authorName;
    private String cover;
    private String catName;
    private Float star;
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public Float getStar() {
        return star;
    }

    public void setStar(Float star) {
        this.star = star;
    }
//    static final long serialVersionUID = 0x3331182L;
//
//    @SerializedName("male")
//    private List<NotifyBookBean> maleBookList;
//    @SerializedName("female")
//    private List<NotifyBookBean> femaleBookList;
//
//    public List<NotifyBookBean> getMaleBookList() {
//        return maleBookList;
//    }
//
//    public void setMaleBookList(List<NotifyBookBean> maleBookList) {
//        this.maleBookList = maleBookList;
//    }
//
//    public List<NotifyBookBean> getFemaleBookList() {
//        return femaleBookList;
//    }
//
//    public void setFemaleBookList(List<NotifyBookBean> femaleBookList) {
//        this.femaleBookList = femaleBookList;
//    }
}
