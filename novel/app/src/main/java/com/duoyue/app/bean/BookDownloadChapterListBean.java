package com.duoyue.app.bean;

import java.util.List;

/**
 * 书籍下载的一组章节
 */
public class BookDownloadChapterListBean {

    private List<BookDownloadChapterBean> chapters;

    /**
     * 是否选中,非接口字段
     */
    private boolean isChecked;

    /**
     * 是否已经下载,非接口字段
     */
    private boolean isDownload;

    public List<BookDownloadChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(List<BookDownloadChapterBean> chapters) {
        this.chapters = chapters;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }
}
