package com.duoyue.app.common.data.response.bookshelf;

import com.duoyue.app.common.data.request.bookcity.DayRecommendBookBean;
import com.duoyue.lib.base.app.http.JsonResponse;

import java.util.List;

/**
 * 添加书架响应信息
 * @author caoym
 * @data 2019/3/30  16:52
 */
public class BookShelfListResp
{
    /**
     * 状态(0:失败;1:成功).
     */
    public int status;

    /**
     * 收藏书籍信息列表.
     */
    private List<BookShelfBookInfoResp> storedBookList;

    /**
     * 推荐书籍信息列表.
     */
    private List<BookShelfBookInfoResp> recommendBookList;

    /**
     * 推荐广告信息列表.
     */
    private List<BookShelfAdInfoResp> promoteSiteList;

    /**
     * 每日推荐书籍
     */
    private DayRecommendBookBean appRecommendBook;

    /**
     * 周阅读时长.
     */
    private long weekTotalReadTime;

    public BookShelfListResp()
    {
    }

    public BookShelfListResp(int status)
    {
        this.status = status;
    }

    public List<BookShelfBookInfoResp> getStoredBookList()
    {
        return storedBookList;
    }

    public List<BookShelfBookInfoResp> getRecommendBookList()
    {
        return recommendBookList;
    }

    public List<BookShelfAdInfoResp> getPromoteSiteList()
    {
        return promoteSiteList;
    }

    public DayRecommendBookBean getAppRecommendBook() {
        return appRecommendBook;
    }

    public long getWeekTotalReadTime() {
        return weekTotalReadTime;
    }
}
