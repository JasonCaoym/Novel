package com.duoyue.app.event;

import com.duoyue.app.bean.BookDownloadDBBean;
import com.duoyue.app.bean.BookDownloadTask;

import java.util.List;

/**
 * 下载书籍Event
 */
public class BookDownloadEvent {

    /**
     * 下载中
     */
    public static final int DOWNLOADING = 0;
    /**
     * 下载完成
     */
    public static final int DOWNLOAD_COMPLETE = 1;
    /**
     * 下载中断
     */
    public static final int DOWNLOAD_ERROR = 2;

    private long bookId;

    private BookDownloadDBBean bookDownloadDBBean;

    private int downloadStates;     //0 下载中  1下载完成   2下载中断

    private int total;      //任务总数

    private int progress;   //进度

    private BookDownloadTask bookDownloadTask;

    /**
     * 一个章节下载完成时，使用该构造函数
     * @param bookId
     * @param bookDownloadDBBean
     * @param downloadStates
     * @param total
     * @param progress
     */
    public BookDownloadEvent(long bookId, BookDownloadDBBean bookDownloadDBBean, int downloadStates, int total, int progress) {
        this.bookId = bookId;
        this.bookDownloadDBBean = bookDownloadDBBean;
        this.downloadStates = downloadStates;
        this.total = total;
        this.progress = progress;
    }

    /**
     * 一个下载任务完成或者异常时，使用该构造函数
     * @param bookId
     * @param bookDownloadTask
     * @param downloadStates
     */
    public BookDownloadEvent(long bookId, BookDownloadTask bookDownloadTask, int downloadStates) {
        this.bookId = bookId;
        this.downloadStates = downloadStates;
        this.bookDownloadTask = bookDownloadTask;
    }

    public BookDownloadDBBean getBookDownloadDBBean() {
        return bookDownloadDBBean;
    }

    public int getDownloadStates() {
        return downloadStates;
    }

    public int getTotal() {
        return total;
    }

    public int getProgress() {
        return progress;
    }

    public long getBookId() {
        return bookId;
    }

    public BookDownloadTask getBookDownloadTask() {
        return bookDownloadTask;
    }
}
