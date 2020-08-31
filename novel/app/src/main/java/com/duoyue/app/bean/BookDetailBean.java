package com.duoyue.app.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BookDetailBean implements Parcelable {

    private String bookId;
    private String bookName;
    private String cover;
    private String authorName;
    private int state;
    private float star;
    private int voteNum;
    private int wordCount;
    private int popularityNum;
    private int fansNum;
    private String recWords;
    private String resume;
    private String fromSource;
    private String issueTime;
    private int lastChapter;
    private String lastChapIssueTime;
    private String catName;
    private String tagsInfo;
    private String parentId;
    private BookDetailCategoryBean category;

    /**
     * 书籍来源(1:运营上传;2:掌阅接口;3:掌阅爬虫接口)
     */
    private int from;

    public BookDetailBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(bookName);
        dest.writeString(cover);
        dest.writeString(authorName);
        dest.writeInt(state);
        dest.writeFloat(star);
        dest.writeInt(voteNum);
        dest.writeInt(wordCount);
        dest.writeInt(popularityNum);
        dest.writeInt(fansNum);
        dest.writeInt(lastChapter);
        dest.writeString(recWords);
        dest.writeString(resume);
        dest.writeString(fromSource);
        dest.writeString(issueTime);
        dest.writeString(lastChapIssueTime);
        dest.writeString(catName);
        dest.writeInt(from);
        dest.writeString(parentId);
        dest.writeString(tagsInfo);
    }

    public static final Parcelable.Creator<BookDetailBean> CREATOR = new Parcelable.Creator<BookDetailBean>() {
        public BookDetailBean createFromParcel(Parcel in) {
            return new BookDetailBean(in);
        }

        public BookDetailBean[] newArray(int size) {
            return new BookDetailBean[size];
        }
    };

    private BookDetailBean(Parcel in) {
        bookId = in.readString();
        bookName = in.readString();
        cover = in.readString();
        authorName = in.readString();
        state = in.readInt();
        star = in.readFloat();
        voteNum = in.readInt();
        wordCount = in.readInt();
        popularityNum = in.readInt();
        fansNum = in.readInt();
        lastChapter = in.readInt();
        recWords = in.readString();
        resume = in.readString();
        fromSource = in.readString();
        issueTime = in.readString();
        lastChapIssueTime = in.readString();
        catName = in.readString();
        tagsInfo = in.readString();
        from = in.readInt();
        parentId = in.readString();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public int getVoteNum() {
        return voteNum;
    }

    public void setVoteNum(int voteNum) {
        this.voteNum = voteNum;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getPopularityNum() {
        return popularityNum;
    }

    public void setPopularityNum(int popularityNum) {
        this.popularityNum = popularityNum;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public String getRecWords() {
        return recWords;
    }

    public void setRecWords(String recWords) {
        this.recWords = recWords;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getLastChapIssueTime() {
        return lastChapIssueTime;
    }

    public void setLastChapIssueTime(String lastChapIssueTime) {
        this.lastChapIssueTime = lastChapIssueTime;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getFrom() {
        return from;
    }

    public String getTagsInfo() {
        return tagsInfo;
    }

    public void setTagsInfo(String tagsInfo) {
        this.tagsInfo = tagsInfo;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public BookDetailCategoryBean getCategory() {
        return category;
    }

    public void setCategory(BookDetailCategoryBean category) {
        this.category = category;
    }
}
