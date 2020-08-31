package com.duoyue.app.bean;

import java.io.Serializable;

/**
 * 全部章节下载bean
 */
public class AllChapterDownloadBean implements Serializable {

    private String id;

    private String seqNum;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
