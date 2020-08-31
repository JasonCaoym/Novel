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
@AutoPost(action = "/app/books/v1/addStore", domain = DomainType.BUSINESS)
public class AddBookShelfReq extends JsonRequest
{
    //@AutoHeader(HeaderType.USER_AGENT)
    //public transient String userAgent;
    @AutoHeader(HeaderType.TOKEN)
    public transient String token;

    @SerializedName("books")
    private List<AddBookShelfInfoReq> mBookInfoList;

    public AddBookShelfReq(AddBookShelfInfoReq bookInfoReq)
    {
        mBookInfoList = new ArrayList<>();
        mBookInfoList.add(bookInfoReq);
    }

    public AddBookShelfReq(List<AddBookShelfInfoReq> bookInfoList)
    {
        mBookInfoList = bookInfoList;
    }
}
