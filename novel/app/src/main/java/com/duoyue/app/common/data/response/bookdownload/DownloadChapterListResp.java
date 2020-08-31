package com.duoyue.app.common.data.response.bookdownload;

import com.duoyue.app.bean.BookDownloadChapterBean;

import java.util.List;

public class DownloadChapterListResp {

    List<BookDownloadChapterBean> chapters;

    public List<BookDownloadChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(List<BookDownloadChapterBean> chapters) {
        this.chapters = chapters;
    }

}
