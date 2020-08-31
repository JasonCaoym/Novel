package com.duoyue.app.common.data.request.bookcity;


import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/tag/v1/bookList", domain = DomainType.BUSINESS)
public class BookDetailTagReq extends JsonRequest {


    @SerializedName("tagType")
    private int tagType;
    @SerializedName("tagSecondType")
    private int tagSecondType;
    @SerializedName("tagThreeType")
    private int tagThreeType;
    @SerializedName("nextPage")
    private int nextPage;
    @SerializedName("tag")
    private String tag;


    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public int getTagSecondType() {
        return tagSecondType;
    }

    public void setTagSecondType(int tagSecondType) {
        this.tagSecondType = tagSecondType;
    }

    public int getTagThreeType() {
        return tagThreeType;
    }

    public void setTagThreeType(int tagThreeType) {
        this.tagThreeType = tagThreeType;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
