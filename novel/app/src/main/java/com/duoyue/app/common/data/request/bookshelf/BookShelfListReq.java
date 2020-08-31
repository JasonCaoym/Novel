package com.duoyue.app.common.data.request.bookshelf;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author caoym
 * @data 2019/4/1  15:38
 */
@AutoPost(action = "/app/books/v1/storedList", domain = DomainType.BUSINESS)
public class BookShelfListReq extends JsonRequest
{
    //@AutoHeader(HeaderType.USER_AGENT)
    //public transient String userAgent;
    @AutoHeader(HeaderType.TOKEN)
    public transient String token;

    public BookShelfListReq()
    {
    }
}
