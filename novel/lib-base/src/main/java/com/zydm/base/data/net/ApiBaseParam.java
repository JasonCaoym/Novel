package com.zydm.base.data.net;

import com.zydm.base.common.BaseApplication;
import com.zydm.base.tools.PhoneStatusManager;

import java.util.Map;

public class ApiBaseParam {

    private static final String DEVICE_ID = "deviceId";
    private static final String DEVICE_MODEL = "model";
    private static final String SYSTEM_VERSION = "sysVer";
    private static final String DEVICE_RESOLUTION = "resolution";
    private static final String LOCATION = "loc";
    private static final String NETWORK_TYPE = "netType";
    private static final String APP_CHANNEL = "appChannel";
    private static final String APP_VERSION = "appVer";
    private static final String CLIENT = "client";
    private static final String IMEI = "imei";
    private static final String PACKAGE = "package";
    private static final String ANDROID_ID = "androidId";
    private static final String DEVICE_ID_SRC = "dis";

    public static void addCommonParam(Map<String, String> params) {
        PhoneStatusManager phone = PhoneStatusManager.getInstance();
        params.put(ApiBaseParam.DEVICE_ID, phone.getEncryptedDeviceId());
        params.put(ApiBaseParam.DEVICE_ID_SRC, phone.getDeviceIdSrc());
        params.put(ApiBaseParam.IMEI, phone.getImei());
        params.put(ApiBaseParam.ANDROID_ID, phone.getAndroidId());
        params.put(ApiBaseParam.DEVICE_MODEL, phone.getDeviceModel());
        params.put(ApiBaseParam.SYSTEM_VERSION, phone.getSystemVersion());
        params.put(ApiBaseParam.DEVICE_RESOLUTION, phone.getResolution()[0] + "*"
                + phone.getResolution()[1]);
        params.put(ApiBaseParam.LOCATION, phone.getLongitudeAndLatitude()[0] + "*"
                + phone.getLongitudeAndLatitude()[1]);
        params.put(ApiBaseParam.NETWORK_TYPE, phone.getNetworkConnectionType());
        params.put(ApiBaseParam.APP_CHANNEL, phone.getAppChannel());
        params.put(ApiBaseParam.APP_VERSION, phone.getAppVersionName());
        params.put(ApiBaseParam.CLIENT, PhoneStatusManager.CLIENT_TYPE);
        String packageName = "";
        if (BaseApplication.context.isTestEnv()) {
            packageName = PhoneStatusManager.getInstance().getPackageNameOnlyForDeveloperTest();
        } else {
            packageName = BaseApplication.context.globalContext.getPackageName();
        }
        params.put(PACKAGE, packageName);
    }
}
