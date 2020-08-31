package com.zydm.base.data.bean;

import java.io.Serializable;

public class ChapterUrlBean implements Serializable {

    static final long serialVersionUID = 0x432424142L;

    public String secret;
    public String content;
    public String bookName;
    public int chapterId;
    public String chapterTitle;
    public String format;
    public long wordCount;
    public int seqNum;
    public int chapterCount;
    public String bookId;
    public String url;
    /**
     * 书籍来源(1:运营上传;2:掌阅接口;3:掌阅爬虫接口)
     */
    public int from;
}
