package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/bookStore/v1/youLike", domain = DomainType.BUSINESS)
public class BookGuessReq extends JsonRequest {

    /**
     * 页数
     */
    @SerializedName("quePages")
    private int quePages;

    /**
     *  书城首页出现的所有书籍id（猜你喜欢分栏除外），逗号分隔
     */
    @SerializedName("repeatBookId")
    private String repeatBookId;

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
}
