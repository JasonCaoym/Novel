package com.duoyue.app.common.data.request.bookrecord;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.dao.BookRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除阅读历史记录
 * @author caoym
 * @data 2019/4/17  15:38
 */
@AutoPost(action = "/app/books/v1/delLatestRead", domain = DomainType.BUSINESS)
public class RemoveBookRecordReq extends JsonRequest
{
    //@AutoHeader(HeaderType.USER_AGENT)
    //public transient String userAgent;
    //@AutoHeader(HeaderType.TOKEN)
    //public transient String token;
    @SerializedName("books")
    private List<RemoveBookRecordInfoReq> mBookRecordInfoList;

    public RemoveBookRecordReq(RemoveBookRecordInfoReq bookRecordBean) throws Throwable
    {
        mBookRecordInfoList = new ArrayList<>();
        mBookRecordInfoList.add(bookRecordBean);
    }
}
