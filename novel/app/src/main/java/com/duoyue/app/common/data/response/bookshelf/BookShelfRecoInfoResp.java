package com.duoyue.app.common.data.response.bookshelf;

import com.stx.xhb.xbanner.entity.SimpleBannerInfo;

/**
 * 书架每日推荐书籍信息
 * @author wangt
 * @date 2019/06/04
 */
public class BookShelfRecoInfoResp extends SimpleBannerInfo {

    /**
     * 书籍id
     */
    private long bookId;

    /**
     * 书籍名称
     */
    private String bookName;

    /**
     * 书籍封面
     */
    private String cover;

    /**
     * 书评
     */
    private String bookComment;

    /**
     * 评论人
     */
    private String commentator;

    /**
     * 跳转地址：1书籍详情页，2阅读器
     */
    private int innerUrl;

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
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

    public String getBookComment() {
        return bookComment;
    }

    public void setBookComment(String bookComment) {
        this.bookComment = bookComment;
    }

    public String getCommentator() {
        return commentator;
    }

    public void setCommentator(String commentator) {
        this.commentator = commentator;
    }

    public int getInnerUrl() {
        return innerUrl;
    }

    public void setInnerUrl(int innerUrl) {
        this.innerUrl = innerUrl;
    }

    @Override
    public Object getXBannerUrl() {
        return null;
    }
}
