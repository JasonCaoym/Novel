package com.duoyue.lib.base.app.user;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/phone/v1/sendValidateCode", domain = DomainType.BUSINESS)
public class VerifyRequest extends JsonRequest
{
    @SerializedName("code")
    public String code;
}
