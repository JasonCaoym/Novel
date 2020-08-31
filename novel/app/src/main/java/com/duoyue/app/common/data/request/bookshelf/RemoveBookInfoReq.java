package com.duoyue.app.common.data.request.bookshelf;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author caoym
 * @data 2019/4/15  10:24
 */
public class RemoveBookInfoReq
{
    @SerializedName("bookId")
    private long mBookId;

    /**
     * 书籍类型(1:普通书籍;2:推荐书籍)
     */
    @SerializedName("type")
    private int mType;

    public RemoveBookInfoReq(long bookId, int type)
    {
        mBookId = bookId;
        mType = type;
    }
}
