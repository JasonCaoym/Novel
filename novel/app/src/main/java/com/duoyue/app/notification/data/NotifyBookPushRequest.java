package com.duoyue.app.notification.data;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/app/keepAlive/v2/recommend", domain = DomainType.BUSINESS)
public class NotifyBookPushRequest extends JsonRequest {

    private String repeatBookId;

    public String getRepeatBookId() {
        return repeatBookId;
    }

    public void setRepeatBookId(String repeatBookId) {
        this.repeatBookId = repeatBookId;
    }
}
