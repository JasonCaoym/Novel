package com.duoyue.app.notification.data;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/app/keepAlive/v1/recommend", domain = DomainType.BUSINESS)
public class NotifyPushRequest extends JsonRequest {
}
