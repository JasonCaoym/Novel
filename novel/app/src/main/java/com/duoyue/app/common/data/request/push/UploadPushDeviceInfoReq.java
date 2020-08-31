package com.duoyue.app.common.data.request.push;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.google.gson.annotations.SerializedName;

/**
 * 上报推送设备标识
 */
@AutoPost(action = "/app/push/v1/save", domain = DomainType.BUSINESS)
public class UploadPushDeviceInfoReq extends JsonRequest {
    @SerializedName("targetId")
    public String targetId;//设备推送标识(华为的为token OPPO的为registerid vivo/小米的为alias)
    @SerializedName("type")
    public int type;//1:huawei;2:oppo;3:vivo;4:xiaomi

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
