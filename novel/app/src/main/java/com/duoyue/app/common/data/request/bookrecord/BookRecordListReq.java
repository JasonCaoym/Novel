package com.duoyue.app.common.data.request.bookrecord;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.dao.BookRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取阅读历史记录列表
 * @author caoym
 * @data 2019/4/16  16:52
 */
@AutoPost(action = "/app/books/v1/latestReadList", domain = DomainType.BUSINESS)
public class BookRecordListReq extends JsonRequest
{
    public BookRecordListReq()
    {
    }
}
