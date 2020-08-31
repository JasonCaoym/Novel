package com.duoyue.app.common.data.request.bookrecord;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.dao.BookRecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加阅读记录
 * @author caoym
 * @data 2019/4/1  15:38
 */
@AutoPost(action = "/app/books/v1/addLatestRead", domain = DomainType.BUSINESS)
public class AddBookRecordReq extends JsonRequest
{
    //@AutoHeader(HeaderType.USER_AGENT)
    //public transient String userAgent;
    @AutoHeader(HeaderType.TOKEN)
    public transient String token;

    @SerializedName("books")
    private List<AddBookRecordInfoReq> mBookRecordInfoList;

    public AddBookRecordReq(BookRecordBean bookRecordBean) throws Throwable
    {
        mBookRecordInfoList = new ArrayList<>();
        mBookRecordInfoList.add(new AddBookRecordInfoReq(bookRecordBean));
    }
}
