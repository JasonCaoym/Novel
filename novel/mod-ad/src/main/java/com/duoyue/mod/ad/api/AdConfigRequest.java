package com.duoyue.mod.ad.api;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/app/ad/v1/config", domain = DomainType.BUSINESS)
public class AdConfigRequest extends JsonRequest {

}
