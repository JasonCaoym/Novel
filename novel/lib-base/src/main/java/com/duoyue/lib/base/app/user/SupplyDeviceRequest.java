package com.duoyue.lib.base.app.user;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

/**
 * 补充IMSI、IMEI信息接口(授权通过后调用).
 * @author caoym
 * @data 2019/5/13  18:07
 */
@AutoPost(action = "/app/member/v1/supplyDevice", domain = DomainType.BUSINESS)
public class SupplyDeviceRequest  extends JsonRequest
{

}
