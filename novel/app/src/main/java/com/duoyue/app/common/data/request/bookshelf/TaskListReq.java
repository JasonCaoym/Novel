package com.duoyue.app.common.data.request.bookshelf;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;


@AutoPost(action = "/app/v1/task/list", domain = DomainType.BUSINESS)
public class TaskListReq extends JsonRequest {
}
