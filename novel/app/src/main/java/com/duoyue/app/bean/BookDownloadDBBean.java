package com.duoyue.app.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class BookDownloadDBBean {

    /**
     * 唯一ID，以 bookId_seqNum 的格式
     */
    @Id
    public String _id;

    public long bookId;

    public String bookName;

    /**
     * 章节id
     */
    public long chapterId;

    /**
     * 章节序号
     */
    public int seqNum;

    /**
     * 章节名称
     */
    public String title;

    /**
     * 下载url密钥
     */
    public String secret;

    /**
     * 加密的下载地址
     */
    public String url;

    /**
     * 下载状态，0 未下载  1 已下载
     */
    public int states;

    @Generated(hash = 851579305)
    public BookDownloadDBBean() {
    }


    @Generated(hash = 1380899840)
    public BookDownloadDBBean(String _id, long bookId, String bookName, long chapterId, int seqNum, String title,
            String secret, String url, int states) {
        this._id = _id;
        this.bookId = bookId;
        this.bookName = bookName;
        this.chapterId = chapterId;
        this.seqNum = seqNum;
        this.title = title;
        this.secret = secret;
        this.url = url;
        this.states = states;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStates() {
        return states;
    }

    public void setStates(int states) {
        this.states = states;
    }


    public long getChapterId() {
        return this.chapterId;
    }


    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }


    public String getBookName() {
        return this.bookName;
    }


    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

}
