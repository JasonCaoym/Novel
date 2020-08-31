package com.duoyue.app.common.data.request.read;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

/**
 *
 */
@AutoPost(action = "/app/v1/task/status", domain = DomainType.BUSINESS)
public class ReadTaskReq extends JsonRequest {
}
