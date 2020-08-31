package com.zzdm.tinker.service;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/upgraded/hotfix/get", domain = DomainType.UPGRADE)
public class TinkerRequest extends JsonRequest
{
    @SerializedName("appVersionCode")
    public int appVersionCode;

    @SerializedName("hotfixVersionCode")
    public String hotfixVersionCode;

}
