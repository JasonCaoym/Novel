package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/hesitateUser/v1/recommend", domain = DomainType.BUSINESS)
public class RandomPushReq extends JsonRequest {

    @SerializedName("repeatBookId")
    private long repeatBookId;

    public long getRepeatBookId() {
        return repeatBookId;
    }

    public void setRepeatBookId(long repeatBookId) {
        this.repeatBookId = repeatBookId;
    }
}
