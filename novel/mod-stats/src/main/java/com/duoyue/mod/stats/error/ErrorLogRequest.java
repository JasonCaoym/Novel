package com.duoyue.mod.stats.error;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.google.gson.annotations.SerializedName;

/**
 * 补充IMSI、IMEI信息接口(授权通过后调用).
 * @author caoym
 * @data 2019/5/13  18:07
 */
@AutoPost(action = "/app/userMobileInfo/v1/error", domain = DomainType.ERROR)
public class ErrorLogRequest extends JsonRequest
{
    /**
     * 机型
     */
    @SerializedName("model")
    private String mModel;

    /**
     * 屏幕宽
     */
    @SerializedName("width")
    private int mWidth;

    /**
     * 屏幕高
     */
    @SerializedName("height")
    private int mHeight;

    /**
     * CPU核数
     */
    @SerializedName("cn")
    private int mCPUNumber;

    /**
     * cpu速度
     */
    @SerializedName("cpuSpeed")
    private String mCPUSpeed;

    /**
     * romName
     */
    @SerializedName("romName")
    private String mROMName;

    /**
     * romVersion
     */
    @SerializedName("romVersion")
    private String mROMVersion;

    /**
     * 系统版本号
     */
    @SerializedName("sysVersion")
    private String mSysVersion;

    /**
     * MAC地址
     */
    @SerializedName("mac")
    private String mMAC;

    /**
     * AndroidId
     */
    @SerializedName("android")
    private String mAndroidId;

    /**
     * 设备Id
     */
    @SerializedName("deviceId")
    private String mDeviceId;

    /**
     * 序列号
     */
    @SerializedName("seq")
    private String mSeqNumber;

    /**
     * 错误类型
     */
    @SerializedName("errorType")
    private String  mErrorType;

    /**
     * 错误原因
     */
    @SerializedName("errorMsg")
    private String  mErrorMsg;

    /**
     * 书籍id
     */
    @SerializedName("bookId")
    private long  mBookId;

    public ErrorLogRequest(String errorType, String errorMsg)
    {
        //机型
        mModel = PhoneUtil.getModel();
        //获取屏幕尺寸.
        int[] screenSizeArray = PhoneUtil.getScreenSize(BaseContext.getContext());
        //屏幕宽
        mWidth = screenSizeArray[0];
        //屏幕高
        mHeight = screenSizeArray[1];
        //CPU核数
        mCPUNumber = PhoneUtil.getCpuNum();
        //cpu速度
        mCPUSpeed = String.valueOf(PhoneUtil.getCpuFrequency());
        //romName
        mROMName = PhoneUtil.getRomName();
        //RomVersion
        mROMVersion = PhoneUtil.getRomVersion();
        //系统版本号
        mSysVersion = PhoneUtil.getAndroidVN();
        //MAC地址
        mMAC = PhoneUtil.getMac(BaseContext.getContext());
        //AndroidId
        mAndroidId = PhoneUtil.getAndroidID(BaseContext.getContext());
        //设备Id
        mDeviceId = PhoneUtil.getDeviceId(BaseContext.getContext());
        //序列号
        mSeqNumber = PhoneUtil.getSimSerialNumber(BaseContext.getContext());
        //错误类型
        mErrorType = errorType;
        //错误原因
        mErrorMsg = errorMsg;
        //书籍id
        //mBookId = 0;
    }


}
