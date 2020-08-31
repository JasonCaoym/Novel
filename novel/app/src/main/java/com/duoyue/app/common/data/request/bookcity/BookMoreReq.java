package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/bookStore/v1/list", domain = DomainType.BUSINESS)
public class BookMoreReq extends JsonRequest {

    /**
     * 页数
     */
    @SerializedName("quePages")
    private int quePages;

    /**
     *  该分栏书城首页出现的书籍id，逗号分隔
     */
    @SerializedName("repeatBookId")
    private String repeatBookId;

    /**
     *  分栏Id
     */
    @SerializedName("columnId")
    private String columnId;

    /**
     *  分栏Id
     */
    @SerializedName("tag")
    private String tag;

    /**
     *  分栏Id
     */
    @SerializedName("typeId")
    private String typeId;

    public int getQuePages() {
        return quePages;
    }

    public void setQuePages(int quePages) {
        this.quePages = quePages;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
