package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/bookStore/v3/column", domain = DomainType.BUSINESS)
public class BookSubfieldReq extends JsonRequest {

    /**
     * 0 精选 1男 2女
     */
    @SerializedName("chan")
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
