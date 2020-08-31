package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/promote/v1/list", domain = DomainType.BUSINESS)
public class BookBannerAdReq extends JsonRequest {

    /**
     * 0 精选 1男 2女
     */
    @SerializedName("channel")
    private int channel;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
