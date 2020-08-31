package com.duoyue.app.common.data.request.bookrecord;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.dao.BookRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除所有的阅读历史记录
 * @author caoym
 * @data 2019/4/17  15:38
 */
@AutoPost(action = "/app/books/v1/delAllLatestRead", domain = DomainType.BUSINESS)
public class RemoveAllBookRecordReq extends JsonRequest
{
    public RemoveAllBookRecordReq()
    {
    }
}
