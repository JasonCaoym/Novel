package com.duoyue.app.common.data.request.bookrecord;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.dao.BookRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除阅读历史记录书籍信息
 * @author caoym
 * @data 2019/4/17  15:38
 */
public class RemoveBookRecordInfoReq
{
    /**
     * 书籍id
     */
    @SerializedName("bookId")
    private long mBookId;

    public RemoveBookRecordInfoReq(long bookId)
    {
        mBookId = bookId;
    }
}
