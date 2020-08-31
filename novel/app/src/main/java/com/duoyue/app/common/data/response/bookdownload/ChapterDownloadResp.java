package com.duoyue.app.common.data.response.bookdownload;

import com.duoyue.app.bean.BookDownloadChapterListBean;

import java.util.List;

public class ChapterDownloadResp {

    private List<BookDownloadChapterListBean> collect;

    /**
     * 章节单价
     */
    private int price;

    /**
     * 用户已下载章节，逗号拼接
     */
    private String seqNumStr;

    public List<BookDownloadChapterListBean> getCollect() {
        return collect;
    }

    public void setCollect(List<BookDownloadChapterListBean> collect) {
        this.collect = collect;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSeqNumStr() {
        return seqNumStr;
    }

    public void setSeqNumStr(String seqNumStr) {
        this.seqNumStr = seqNumStr;
    }
}
