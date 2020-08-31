package com.duoyue.lib.base.app.user;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.google.gson.annotations.SerializedName;

@AutoPost(action = "/app/member/v1/logout", domain = DomainType.BUSINESS)
public class CancelAccountRequest extends JsonRequest
{
    /**
     * SDK版本号
     */
    @SerializedName("sdkInt")
    public int sdkInt;

    /**
     * 系统版本名称
     */
    @SerializedName("release")
    public String release;

    @SerializedName("type")
    public int type; //1.游客,2.微信,3.QQ,4.微博,5手机

    @SerializedName("code")
    public String code;

    @SerializedName("vaildCode")
    public String vaildCode;

    public CancelAccountRequest()
    {
        sdkInt = PhoneUtil.getAndroidVC();
        release = PhoneUtil.getAndroidVN();
    }
}
