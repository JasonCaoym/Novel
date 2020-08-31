package com.duoyue.app.common.data.request.category;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

/**
 *
 */
@AutoPost(action = "/app/booksClassify/v1/list", domain = DomainType.BUSINESS)
public class CategoryBookListReq extends JsonRequest {
    @SerializedName("categoryId")
    public String categoryId;
    @SerializedName("tagType")
    public int firstTag;
    @SerializedName("tagSecondType")
    public int secondTag;
    @SerializedName("tagThreeType")
    public int threeTag;
    @SerializedName("tag")
    public String tag;
    @SerializedName("subCategoryId")
    public int subCategoryId;
    /**
     * 1.男生;2. 女生
     */
    @SerializedName("parentId")
    public int parentId;
    @SerializedName("nextPage")
    public int pageIndex;
}
