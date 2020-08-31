package com.duoyue.app.common.data.response.bookdownload;

import com.duoyue.app.bean.AllChapterDownloadBean;

import java.util.List;

public class ChapterDownloadOptionResp {

    /**
     * 每个章节书豆单价
     */
    private int price;

    /**
     * 用户当前的书豆
     */
    private long beans;

    /**
     * 配置可下载的数量
     */
    private List<Integer> numList;

    /**
     * 没有阅读过的书籍，拿不到第一章标题，所以由服务器下发
     */
    private AllChapterDownloadBean chapterDownload;

    /**
     * 用户已下载章节，逗号拼接
     */
    private String seqNumStr;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getBeans() {
        return beans;
    }

    public void setBeans(long beans) {
        this.beans = beans;
    }

    public List<Integer> getNumList() {
        return numList;
    }

    public void setNumList(List<Integer> numList) {
        this.numList = numList;
    }

    public AllChapterDownloadBean getChapterDownload() {
        return chapterDownload;
    }

    public void setChapterDownload(AllChapterDownloadBean chapterDownload) {
        this.chapterDownload = chapterDownload;
    }

    public String getSeqNumStr() {
        return seqNumStr;
    }

    public void setSeqNumStr(String seqNumStr) {
        this.seqNumStr = seqNumStr;
    }
}
