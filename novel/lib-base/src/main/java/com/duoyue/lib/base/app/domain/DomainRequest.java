package com.duoyue.lib.base.app.domain;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/upgraded/doamin/get", domain = DomainType.UPGRADE)
public class DomainRequest extends JsonRequest
{

}
