package com.duoyue.app.common.data.response.bookshelf;

/**
 * 更新书架书籍信息
 * @author caoym
 * @data 2019/3/30  16:52
 */
public class BookShelfAdInfoResp
{
    /**
     * 轮播文字
     */
    private String word;

    /**
     * 跳转类型(1:书籍详情;2:H5;)
     */
    private int jumpType;

    /**
     * 小说编号
     */
    private long bookId;

    /**
     * 落地页/详情接口
     */
    private String link;

    public BookShelfAdInfoResp()
    {

    }

    public String getWord() {
        return word;
    }

    public int getJumpType() {
        return jumpType;
    }

    public long getBookId() {
        return bookId;
    }

    public String getLink() {
        return link;
    }
}
