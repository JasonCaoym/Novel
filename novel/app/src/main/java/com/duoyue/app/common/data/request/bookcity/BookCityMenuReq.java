package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/app/bookStore/v1/iconList", domain = DomainType.BUSINESS)
public class BookCityMenuReq extends JsonRequest {

    private int chan;

    public int getChan() {
        return chan;
    }

    public void setChan(int chan) {
        this.chan = chan;
    }
}
