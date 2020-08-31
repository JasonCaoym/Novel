package com.zydm.base.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.duoyue.lib.base.log.Logger;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;
import com.zydm.base.utils.*;

import java.lang.reflect.Method;
import java.util.Locale;

public class PhoneStatusManager {
    private static final String TAG = "PhoneStatusManager";

    public static final String NETWORK_CONNECTION_TYPE_UNKOWN = "unkown";
    private static final String NETWORK_CONNECTION_TYPE_2G = "2G";
    private static final String NETWORK_CONNECTION_TYPE_3G = "3G";
    private static final String NETWORK_CONNECTION_TYPE_4G = "4G";
    private static final String NETWORK_CONNECTION_TYPE_WIFI = "wifi";

    private static final String CN_COUNTRY_CODE = "CN";
    private static final String CN_LANGUAGE = "zh";

    private static final int MIN_WVGA_HEIGHT = 700;
    private static final int WVGA_HEIGHT = 800;
    private static final int MIN_HD_HEIGHT = 1180;
    private static final int HD_HEIGHT = 1280;
    public static final String KEY_IMEI_RANDOM = "imei_random";
    public static final String KEY_DEVELOPER_CHANNEL = "developer_channel_for_test";
    public static final String KEY_DEVELOPER_PKG_NAME = "developer_pkg_name_for_test";

    private static PhoneStatusManager sSelf = null;

    private String mDeviceMode = null;
    public String mImei = null;
    private String mSystemVer = null;
    public String mAppVer = "";
    private String mAppChannel = "";
    //    private String mAppChannel = "huaWeiAbroad";
    private int[] mResolution = null;
    private MTDeviceId mDeviceId;

    public static String CLIENT_TYPE = "android";
    private String mCountryCode;

    private PhoneStatusManager() {
    }

    public static PhoneStatusManager getInstance() {
        if (null == sSelf) {
            sSelf = new PhoneStatusManager();
        }
        return sSelf;
    }

    public String getDeviceModel() {
        if (TextUtils.isEmpty(mDeviceMode)) {
            mDeviceMode = getSystemProp("ro.product.model", "");
        }
        return mDeviceMode;
    }

    public String getImei() {
        return StringUtils.isBlank(mImei) ? getMtDeviceId().getImei() : mImei;
    }

    public String getAndroidId() {
        return getMtDeviceId().getAndroidId();
    }

    public String getDeviceIdSrc() {
        return StringUtils.isBlank(mImei) ? getMtDeviceId().getMtDeviceId() : mImei;
    }

    public String getEncryptedDeviceId() {
        String md5Str = MD5Utils.getStringMd5(getAppChannel() + "_" + getAppVersionName() + "_" + CLIENT_TYPE);
        String packageName = "com.zydm.ebk";
/*        if (BaseApplication.context.isTestEnv()) {
            packageName = getPackageNameOnlyForDeveloperTest();
        } else {
            packageName = BaseApplication.context.getPackageName();
        }*/
        md5Str = MD5Utils.getStringMd5(md5Str + packageName);
        String key = md5Str.substring(0, 16);
        String iv = md5Str.substring(16);
        try {
            return AesUtils.encryptString(key, iv, getDeviceIdSrc());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }

    public String getPackageNameOnlyForDeveloperTest() {
        String pkg = SPUtils.INSTANCE.getString(KEY_DEVELOPER_PKG_NAME);
        if (TextUtils.isEmpty(pkg)) {
            pkg = BaseApplication.context.globalContext.getPackageName();
        }
        return pkg;
    }

    public String getSystemVersion() {
        if (TextUtils.isEmpty(mSystemVer)) {
            mSystemVer = getSystemProp("ro.build.version.release", "");
        }
        return mSystemVer;
    }

    public boolean isSDCardMounted() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public boolean hasNetworkConnected() {
        NetworkInfo networkInfo = getActiveNetworkInfo();

        return (null != networkInfo) && networkInfo.isConnected();
    }

    public boolean isWifiConnected() {
        NetworkInfo networkInfo = getActiveNetworkInfo();

        if (null == networkInfo) {
            LogUtils.d(TAG, "isWifiConnected: networkInfo is null");
            return false;
        }

        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
                return networkInfo.isConnected();
            default:
                return false;
        }

    }

    public String getNetworkConnectionType() {
        if (isWifiConnected()) {
            return NETWORK_CONNECTION_TYPE_WIFI;
        } else {
            return getMobileDataNetworkType();
        }
    }

    public String getMobileDataNetworkType() {
        NetworkInfo networkInfo = getActiveNetworkInfo();

        if (null == networkInfo) {
            return NETWORK_CONNECTION_TYPE_UNKOWN;
        }

        switch (networkInfo.getSubtype()) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CONNECTION_TYPE_4G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CONNECTION_TYPE_3G;

            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CONNECTION_TYPE_2G;

            default:
                return NETWORK_CONNECTION_TYPE_UNKOWN;
        }
    }

    public int[] getResolution() {
        if (null != mResolution) {
            return mResolution;
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int curWidth = metrics.widthPixels;
        int curHeight = metrics.heightPixels;
        if (curHeight >= MIN_WVGA_HEIGHT && curHeight <= WVGA_HEIGHT) {
            curHeight = WVGA_HEIGHT;
        }
        if (curHeight >= MIN_HD_HEIGHT && curHeight <= HD_HEIGHT) {
            curHeight = HD_HEIGHT;
        }
        if (curWidth > 0 && curHeight > 0) {
            mResolution = new int[]{curWidth, curHeight};
        }
        return mResolution;
    }

    public Double[] getLongitudeAndLatitude() {
        Double[] longitudeAndLatitude = new Double[]{0.0, 0.0};
//        LocationManager locMgr = (LocationManager) getContext()
//                .getSystemService(Context.LOCATION_SERVICE);
//        try {
//            Context context = Injection.getAppInject().getApplication();
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return longitudeAndLatitude;
//            }
//            Location location = locMgr
//                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            if (null != location) {
//                longitudeAndLatitude[0] = location.getLongitude();
//                longitudeAndLatitude[1] = location.getLatitude();
//            }
//        } catch (Exception e) {
//        }
//        LogUtils.d(TAG, "longitudeAndLatitude: " + longitudeAndLatitude[0]
//                + ", " + longitudeAndLatitude[1]);
        return longitudeAndLatitude;
    }

    public String getAppVersionName() {
        if (!TextUtils.isEmpty(mAppVer)) {
            return mAppVer;
        }

        Context context = getContext();
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            String versionName = packageInfo.versionName;
            mAppVer = versionName == null ? Constants.EMPTY : versionName;
        } catch (NameNotFoundException exp) {
            LogUtils.e(TAG, exp.getLocalizedMessage(), exp);
        }
        return mAppVer;
    }

    public int getAppVersionCode() {
        Context context = getContext();
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Throwable exp) {
            LogUtils.e(TAG, exp.getLocalizedMessage(), exp);
        }
        return 0;
    }

    public String getAppChannel() {
        if (!StringUtils.isEmpty(mAppChannel)) {
            return mAppChannel;
        }

        String channel = "";
        if (BaseApplication.context.isTestEnv()) {
            channel = SPUtils.INSTANCE.getString(KEY_DEVELOPER_CHANNEL);
        }

        if (StringUtils.isEmpty(channel)) {
            channel = AppMetaData.INSTANCE.getValue("UMENG_CHANNEL");
        }
        mAppChannel = ChannelUtils.transformChannel(channel);
        return mAppChannel;
    }

    //测试环境开发者模式专用
    public void updateChannelForTest(String channel) {
        if (StringUtils.isBlank(channel)) {
            return;
        }
        mAppChannel = channel;
        SPUtils.INSTANCE.putString(PhoneStatusManager.KEY_DEVELOPER_CHANNEL, channel);
    }

    //测试环境开发者模式专用
    public void updatePkgForTest(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return;
        }
        SPUtils.INSTANCE.putString(PhoneStatusManager.KEY_DEVELOPER_PKG_NAME, packageName);
    }

    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }

    public Resources getResources() {
        return getContext().getResources();
    }

    private Context getContext() {
        return BaseApplication.context.globalContext;
    }

    public static String getSystemProp(String key, String defVal) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Object obj = c.newInstance();
            Method method = c.getMethod("get", String.class, String.class);
            return (String) method.invoke(obj, key, defVal);
        } catch (Exception e) {
            e.printStackTrace();
            return defVal;
        }
    }

    public MTDeviceId getMtDeviceId() {
        if (mDeviceId == null) {
            mDeviceId = new MTDeviceId(getContext());
        }
        return mDeviceId;
    }

    public void resetMtDeviceId() {
        mDeviceId = new MTDeviceId(getContext());
        mDeviceId.storeMTDeviceId();
    }

    public String getCountryCode() {
        if (!StringUtils.isBlank(mCountryCode)) {
            return mCountryCode;
        }

        TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String code = tm.getSimCountryIso();
        if (StringUtils.isBlank(code)) {
            code = Locale.getDefault().getCountry();
        }
        mCountryCode = StringUtils.isBlank(code) ? Constants.EMPTY : code;
        return mCountryCode;
    }

    public boolean isChina() {
        return CN_COUNTRY_CODE.equalsIgnoreCase(getCountryCode());
    }

    public boolean isChinese() {
        return CN_LANGUAGE.equalsIgnoreCase(Locale.getDefault().getLanguage());
    }
}
