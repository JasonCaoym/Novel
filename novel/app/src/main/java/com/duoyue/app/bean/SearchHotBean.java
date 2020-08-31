package com.duoyue.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchHotBean implements Parcelable {

    private int id;
    private String name;
    private String cover;
    private String resume;
    private int state;
    private int wordCount;
    private String lastChapter;
    private String updateTimes;
    private String currTime;
    private String from;
    private String billboardTitle;




    protected SearchHotBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        cover = in.readString();
        resume = in.readString();
        state = in.readInt();
        wordCount = in.readInt();
        lastChapter = in.readString();
        updateTimes = in.readString();
        currTime = in.readString();
        from = in.readString();
        billboardTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(cover);
        dest.writeString(resume);
        dest.writeInt(state);
        dest.writeInt(wordCount);
        dest.writeString(lastChapter);
        dest.writeString(updateTimes);
        dest.writeString(currTime);
        dest.writeString(from);
        dest.writeString(billboardTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getUpdateTimes() {
        return updateTimes;
    }

    public void setUpdateTimes(String updateTimes) {
        this.updateTimes = updateTimes;
    }

    public String getCurrTime() {
        return currTime;
    }

    public void setCurrTime(String currTime) {
        this.currTime = currTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBillboardTitle() {
        return billboardTitle;
    }

    public void setBillboardTitle(String billboardTitle) {
        this.billboardTitle = billboardTitle;
    }

    public static final Creator<SearchHotBean> CREATOR = new Creator<SearchHotBean>() {
        @Override
        public SearchHotBean createFromParcel(Parcel in) {
            return new SearchHotBean(in);
        }

        @Override
        public SearchHotBean[] newArray(int size) {
            return new SearchHotBean[size];
        }
    };
}
