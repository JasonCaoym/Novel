package com.duoyue.app.common.data.request.bookrecord;

import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.lib.base.app.http.*;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

/**
 * 阅读品味
 * @author caoym
 * @data 2019/4/8 23:38
 */
@AutoPost(action = "/app/readtaste/v1/update", domain = DomainType.BUSINESS)
public class ReadingTasteReq extends JsonRequest
{
    //@AutoHeader(HeaderType.USER_AGENT)
    //public transient String userAgent;
    @AutoHeader(HeaderType.TOKEN)
    public transient String token;

    /**
     * 性别(1:男;2:女)
     */
    @SerializedName("sex")
    private int mSex;

    /**
     * 选中的类别id逗号分隔(如:1,2,3,)
     */
    @SerializedName("readTaste")
    private String mReadTaste;

    public ReadingTasteReq(JSONObject tasteJSONObj) throws Throwable
    {
        //性别.
        mSex = tasteJSONObj.optInt(StartGuideMgr.JSON_KEY_SEX, 1);
        //类别Id.
        mReadTaste = tasteJSONObj.optString(StartGuideMgr.JSON_KEY_CATEGORY, "");
    }
}
