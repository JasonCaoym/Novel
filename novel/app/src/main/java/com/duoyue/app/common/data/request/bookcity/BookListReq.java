package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/bookStore/v1/getByPage", domain = DomainType.BUSINESS)
public class BookListReq extends JsonRequest {

    @SerializedName("type")
    private int type;
    @SerializedName("quePages")
    private int pageIndex;
    @SerializedName("chan")
    private int chan;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getChan() {
        return chan;
    }

    public void setChan(int chan) {
        this.chan = chan;
    }
}
