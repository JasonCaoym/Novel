package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/app/find/v1/recommendBookList", domain = DomainType.BUSINESS)
public class BookNewBookListReq extends JsonRequest {


}
