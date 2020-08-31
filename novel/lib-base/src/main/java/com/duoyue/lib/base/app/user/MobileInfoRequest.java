package com.duoyue.lib.base.app.user;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.google.gson.annotations.SerializedName;

/**
 * 用户手机信息请求类.
 * @author caoym
 * @data 2019/4/28  18:07
 */
@AutoPost(action = "/app/userMobileInfo/v1/stats", domain = DomainType.BUSINESS)
public class MobileInfoRequest extends JsonRequest
{
    /**
     * 机型
     */
    @SerializedName("model")
    public String model;

    /**
     * 屏幕宽
     */
    @SerializedName("width")
    public int width;

    /**
     * 屏幕高
     */
    @SerializedName("height")
    public int height;

    /**
     * CPU核数
     */
    @SerializedName("cn")
    public int cpuNum;

    /**
     * CPU速度
     */
    @SerializedName("cpuSpeed")
    public String cpuSpeed;

    /**
     * ROM名称
     */
    @SerializedName("romName")
    public String romName;

    /**
     * ROM版本
     */
    @SerializedName("romVersion")
    public String romVersion;

    /**
     * 系统版本号
     */
    @SerializedName("sysVersion")
    public String sysVersion;

    /**
     * Mac地址
     */
    @SerializedName("mac")
    public String mac;

    /**
     * AndroidId
     */
    @SerializedName("android")
    public String android;

    /**
     * 设备标识.
     */
    @SerializedName("deviceId")
    public String deviceId;

    /**
     * 序列号
     */
    @SerializedName("seq")
    public String seqNumber;

    public MobileInfoRequest()
    {
        //机型
        model = PhoneUtil.getModel();
        //获取屏幕大小.
        int[] screenSize = PhoneUtil.getScreenSize(BaseContext.getContext());
        //屏幕宽
        width = screenSize[0];
        //屏幕高
        height = screenSize[1];
        //CPU核数
        cpuNum = PhoneUtil.getCpuNum();
        //CPU速度
        cpuSpeed = String.valueOf(PhoneUtil.getCpuFrequency());
        //ROM名称
        romName = PhoneUtil.getRomName();
        //ROM版本
        romVersion = PhoneUtil.getRomVersion();
        //系统版本号
        sysVersion = PhoneUtil.getAndroidVN();
        //Mac地址
        mac = PhoneUtil.getMac(BaseContext.getContext());
        //AndroidId
        android = PhoneUtil.getAndroidID(BaseContext.getContext());
        //设备标识.
        deviceId = PhoneUtil.getDeviceId(BaseContext.getContext());
        //序列号
        seqNumber = PhoneUtil.getSimSerialNumber(BaseContext.getContext());
    }
}
