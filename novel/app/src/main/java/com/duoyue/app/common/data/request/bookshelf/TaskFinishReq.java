package com.duoyue.app.common.data.request.bookshelf;

import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;


@AutoPost(action = "/app/v1/task/complete", domain = DomainType.BUSINESS)
public class TaskFinishReq extends JsonRequest {
    @SerializedName("taskId")
    private long taskId;

    public TaskFinishReq(long taskId) {
        this.taskId = taskId;
    }
}
