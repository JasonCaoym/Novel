package com.zydm.base.data.dao;


import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.bean.ListBean;

public class ChapterListBean extends ListBean<ChapterBean> {
    public BookRecordBean mOwnBook;
    public int mGroupIndex;
    /**
     * 书籍来源(1:运营上传;2:掌阅接口;3:掌阅爬虫接口)
     */
    public int from;
    @SerializedName("totalChapter")
    public int totalChapter;
}
