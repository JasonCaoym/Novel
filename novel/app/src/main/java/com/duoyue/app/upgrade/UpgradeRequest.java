package com.duoyue.app.upgrade;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/upgraded/update/get", domain = DomainType.UPGRADE)
public class UpgradeRequest extends JsonRequest {

    @SerializedName("appVersionCode")
    public int appVersionCode;
}
