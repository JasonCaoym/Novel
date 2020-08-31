package com.duoyue.app.bean;

import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

public class BookDownloadTask {

    private long bookId;

    private String bookName;

    /**
     * 下载章节列表
     */
    private List<BookDownloadDBBean> downloadDBBeans;

    /**
     * 任务总数
     */
    private int total;

    /**
     * 已下载数
     */
    private int progress;

    /**
     * 重试次数
     */
    public int retryCount;

    public BookDownloadTask(long bookId, String bookName, List<BookDownloadDBBean> downloadDBBeans, int total, int progress) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.downloadDBBeans = downloadDBBeans;
        this.total = total;
        this.progress = progress;
    }

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

    public List<BookDownloadDBBean> getDownloadDBBeans() {
        return downloadDBBeans;
    }

    public void setDownloadDBBeans(List<BookDownloadDBBean> downloadDBBeans) {
        this.downloadDBBeans = downloadDBBeans;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
