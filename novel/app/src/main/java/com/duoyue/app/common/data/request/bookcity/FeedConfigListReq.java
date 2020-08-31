package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/app/userFeedBack/v1/config", domain = DomainType.BUSINESS)
public class FeedConfigListReq extends JsonRequest {

    
}
