package com.duoyue.app.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;


public class BookCityItemBean implements Parcelable {

    private String name;
    private long popularityNum;
    private String authorName;
    private String cover;
    private String resume;
    private long wordCount;
    @SerializedName("catName")
    private String categoryName;
    private long id;
    private String star;
    private int state;
    private int weekDownPv;
    private int realWeekRead;
    private int realWeekCollect;
    private boolean hot;
    private String categoryId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(popularityNum);
        dest.writeString(authorName);
        dest.writeString(cover);
        dest.writeString(resume);
        dest.writeLong(wordCount);
        dest.writeString(categoryName);
        dest.writeLong(id);
        dest.writeString(star);
        dest.writeInt(state);
        dest.writeByte((byte) (hot ? 1 : 0));
        dest.writeString(categoryId);
        dest.writeInt(weekDownPv);
    }

    public static final Creator<BookCityItemBean> CREATOR = new Creator<BookCityItemBean>() {
        public BookCityItemBean createFromParcel(Parcel in) {
            return new BookCityItemBean(in);
        }

        public BookCityItemBean[] newArray(int size) {
            return new BookCityItemBean[size];
        }
    };

    private BookCityItemBean(Parcel in) {
        name = in.readString();
        popularityNum = in.readLong();
        authorName = in.readString();
        cover = in.readString();
        resume = in.readString();
        wordCount = in.readLong();
        categoryName = in.readString();
        id = in.readLong();
        star = in.readString();
        state = in.readInt();
        weekDownPv = in.readInt();
        hot = in.readByte() != 0;
        categoryId = in.readString();
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPopularityNum() {
        return popularityNum;
    }

    public void setPopularityNum(long popularityNum) {
        this.popularityNum = popularityNum;
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

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public long getWordCount() {
        return wordCount;
    }

    public void setWordCount(long wordCount) {
        this.wordCount = wordCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getWeekDownPv() {
        return weekDownPv;
    }

    public void setWeekDownPv(int weekDownPv) {
        this.weekDownPv = weekDownPv;
    }

    public int getRealWeekRead() {
        return realWeekRead;
    }

    public void setRealWeekRead(int realWeekRead) {
        this.realWeekRead = realWeekRead;
    }

    public int getRealWeekCollect() {
        return realWeekCollect;
    }

    public void setRealWeekCollect(int realWeekCollect) {
        this.realWeekCollect = realWeekCollect;
    }
}
