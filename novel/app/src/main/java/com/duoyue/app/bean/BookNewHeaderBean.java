package com.duoyue.app.bean;

public class BookNewHeaderBean {

    private String uid;
    private String nickName;

    private float distance;
    private  String cover;

    private int sex;

    private boolean isSelected;
    private String lastReadTime;
    private BookNewBookInfoBean bookInfo;

    private boolean isLastData;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public BookNewBookInfoBean getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(BookNewBookInfoBean bookInfo) {
        this.bookInfo = bookInfo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(String lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public boolean isLastData() {
        return isLastData;
    }

    public void setLastData(boolean lastData) {
        isLastData = lastData;
    }
}
