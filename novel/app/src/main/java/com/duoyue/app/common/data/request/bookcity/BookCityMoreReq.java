package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

@AutoPost(action = "/app/bookStore/v3/change", domain = DomainType.BUSINESS)
public class BookCityMoreReq extends JsonRequest {

    private String columnId;
    private String tag;
    private String typeId;
    private String repeatBookId;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRepeatBookId() {
        return repeatBookId;
    }

    public void setRepeatBookId(String repeatBookId) {
        this.repeatBookId = repeatBookId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
