package com.zydm.base.data.bean;

import java.io.Serializable;

/**
 * 获取历史阅读记录汇总响应信息
 * @author caoym
 * @data 2019/3/30  16:52
 */
public class BookRecordGatherResp implements Serializable
{
    /**
     * 总阅读时长
     */
    private long totalReadTime;

    /**
     * 书架中书籍的数量
     */
    private int storedBookSize;
    /**
     * 书豆
     */
    private int bookBeans;

    /**
     * 兑换卡剩余时间
     */
    private long lastSec;

    public BookRecordGatherResp()
    {
    }

    public long getTotalReadTime() {
        return totalReadTime;
    }

    public int getStoredBookSize() {
        return storedBookSize;
    }

    public int getBookBeans() {
        return bookBeans;
    }

    public void setBookBeans(int bookBeans) {
        this.bookBeans = bookBeans;
    }

    public long getLastSec() {
        return lastSec;
    }

    public void setLastSec(long lastSec) {
        this.lastSec = lastSec;
    }
}
