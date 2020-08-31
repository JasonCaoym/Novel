package com.duoyue.app.common.data.request.bookrecord;

import com.duoyue.lib.base.app.http.*;

/**
 * 阅读历史记录汇总
 * @author caoym
 * @data 2019/4/1  15:38
 */
@AutoPost(action = "/app/books/v1/historyGather", domain = DomainType.BUSINESS)
public class BookRecordGatherReq extends JsonRequest
{
    //@AutoHeader(HeaderType.USER_AGENT)
    //public transient String userAgent;
    @AutoHeader(HeaderType.TOKEN)
    public transient String token;

    public BookRecordGatherReq()
    {
    }
}
