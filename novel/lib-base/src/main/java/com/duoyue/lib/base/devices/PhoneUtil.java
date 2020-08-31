package com.duoyue.lib.base.devices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.io.FileAccesser;
import com.duoyue.lib.base.log.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.POWER_SERVICE;

public class PhoneUtil {
    private static final String TAG = "Base#PhoneUtil";

    /**
     * MEID
     */
    private static String MEID;

    /**
     * IMEI
     */
    private static String IMEI;

    /**
     * IMSI
     */
    private static String IMSI;

    /**
     * ROM信息(0:ROM名称;1:ROM版本)
     */
    private static String[] sRomInfoArray;

    /**
     * 获取设备Id
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Throwable throwable) {
            Logger.e(TAG, "getIMEI: failed!", throwable);
        }
        return null;
    }

    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        if (TextUtils.isEmpty(IMEI)) {
            IMEI = getIMEI(context, 0);
        }
        if (TextUtils.isEmpty(IMEI)) {
            IMEI = getIMEI(context, 1);
        }
        return IMEI;
    }

    /**
     * 获取IMEI
     *
     * @param context
     * @param simId
     * @return
     */
    public static String getIMEI(Context context, int simId) {
        if (!StringFormat.isEmpty(IMEI)) {
            return IMEI;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            try {
                switch (i) {
                    case 0:
                        IMEI = tm.getDeviceId();
                        break;
                    case 1:
                        Method getDeviceIdGemini = tm.getClass().getDeclaredMethod("getDeviceIdGemini", new Class[]{int.class});
                        getDeviceIdGemini.setAccessible(true);
                        IMEI = (String) getDeviceIdGemini.invoke(tm, simId);
                        break;
                    case 2:
                        Method getDefault = tm.getClass().getDeclaredMethod("getDefault", new Class[]{int.class});
                        getDefault.setAccessible(true);
                        TelephonyManager t = (TelephonyManager) getDefault.invoke(tm, simId);
                        IMEI = t.getDeviceId();
                        break;
                }
                if (!TextUtils.isEmpty(IMEI)) {
                    return IMEI;
                }
            } catch (Throwable throwable) {
                builder.append("\t" + throwable.getClass().getName() + "->" + throwable.getMessage());
            }
        }
        Logger.e(TAG, "getIMEI: 获取IMEI: {}失败: {}", simId, builder.toString());
        return "";
    }


    /**
     * IMEI 1号
     * @param context
     * @return
     */
    public static String getIMEI_1(Context context){
        if (PhoneUtil.checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return  tm != null ? tm.getDeviceId() : "";
        } else {
            return "";
        }
    }

    /**
     * IMEI 2号
     * @param context
     * @return
     */
    public static String getIMEI_2(Context context) {
        if (PhoneUtil.checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class clazz = tm.getClass();
            try {
                Method getImei = clazz.getDeclaredMethod("getImei", int.class);
                if (getImei.invoke(tm, 1) != null) {
                    return getImei.invoke(tm, 1).toString();
                } else {
                    return "";
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取IMSI.
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        if (TextUtils.isEmpty(IMSI)) {
            IMSI = getIMSI(context, 0);
        }
        if (TextUtils.isEmpty(IMSI)) {
            IMSI = getIMSI(context, 1);
        }
        return IMSI;
    }

    /**
     * 获取IMSI
     *
     * @param simId
     * @return
     */
    private static String getIMSI(Context context, int simId) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            try {
                switch (i) {
                    case 0:
                        IMSI = tm.getSubscriberId();
                        break;
                    case 1:
                        Method getSubscriberIdGemini = tm.getClass().getDeclaredMethod("getSubscriberIdGemini", new Class[]{int.class});
                        getSubscriberIdGemini.setAccessible(true);
                        IMSI = (String) getSubscriberIdGemini.invoke(tm, simId);
                        break;
                    case 2:
                        Method getDefault = tm.getClass().getDeclaredMethod("getDefault", new Class[]{int.class});
                        getDefault.setAccessible(true);
                        TelephonyManager t = (TelephonyManager) getDefault.invoke(tm, simId);
                        IMSI = t.getSubscriberId();
                        break;
                }
                if (!TextUtils.isEmpty(IMSI)) {
                    return IMSI;
                }
            } catch (Throwable throwable) {
                builder.append("\t" + throwable.getClass().getName() + "->" + throwable.getMessage());
            }
        }
        Logger.e(TAG, "getIMSI: 获取IMSI:{} 失败: {}", simId, builder.toString());
        return "";
    }

    /**
     * 获取MEID
     *
     * @param context
     * @return
     */
    public static String getMEID(Context context) {
        if (!StringFormat.isEmpty(MEID)) {
            return MEID;
        }
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //String imei1 = manager.getDeviceId();
            //String imei2 = (String) method.invoke(manager, 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                MEID = manager.getMeid(2);
            }
            if (StringFormat.isEmpty(MEID)) {
                try {
                    Class clazz = Class.forName("android.os.SystemProperties");
                    Method methodGet = clazz.getMethod("get", String.class, String.class);
                    MEID = (String) methodGet.invoke(null, "ril.cdma.meid", "");
                } catch (Throwable throwable) {
                    //Logger.e(TAG, "getMEID: {}", throwable);
                }
            }
            if (StringFormat.isEmpty(MEID)) {
                try {
                    Method method = manager.getClass().getMethod("getDeviceId", int.class);
                    MEID = (String) method.invoke(manager, 1);
                } catch (Throwable throwable) {
                    //Logger.e(TAG, "getMEID: {}", throwable);
                }
            }
            return MEID;
        } catch (Throwable throwable) {
            Logger.e(TAG, "getMEID: {}", throwable);
            return MEID;
        }
    }

    public static String getSimOperator(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimOperator();
        } catch (Throwable throwable) {
            Logger.e(TAG, "getSimOperator: failed!", throwable);
        }
        return null;
    }

    public static String getPhoneNumber(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        } catch (Throwable throwable) {
            Logger.e(TAG, "getPhoneNumber: failed!", throwable);
        }
        return null;
    }

    /**
     * 获取设备序列号
     *
     * @param context
     * @return
     */
    public static String getSimSerialNumber(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber();
        } catch (Throwable throwable) {
            Logger.e(TAG, "getSimSerialNumber: {}", throwable);
            return "";
        }
    }

    public static String getMac(Context context) {
        String mac = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mac = getMac7();
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                mac = getMac6();
            } else {
                mac = getMac5(context);
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "getMac: failed!", throwable);
        }
        if (mac != null) {
            ///mac = mac.replaceAll("[^\\w\\d]", "");
        }
        return mac;
    }

    private static String getMac5(Context context) throws Throwable {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        if (info != null) {
            return info.getMacAddress();
        }
        return null;
    }

    private static String getMac6() throws Throwable {
        File file = new File("/sys/class/net/wlan0/address");
        byte[] data = FileAccesser.readBytes(file);
        if (data != null) {
            return new String(data);
        }
        return null;
    }

    private static String getMac7() throws Throwable {
        InetAddress ip = getLocalAddress();
        if (ip != null) {
            byte[] address = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < address.length; i++) {
                if (i != 0) {
                    builder.append(":");
                }
                String hex = Integer.toHexString(address[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                builder.append(hex);
            }
            return builder.toString().toLowerCase();
        }
        return null;
    }

    private static InetAddress getLocalAddress() throws Throwable {
        Enumeration<NetworkInterface> networkEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkEnumeration.nextElement();
            Enumeration<InetAddress> ipEnumeration = networkInterface.getInetAddresses();
            while (ipEnumeration.hasMoreElements()) {
                InetAddress ip = ipEnumeration.nextElement();
                if (!ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {
                    return ip;
                }
            }
        }
        return null;
    }

    public static NetType getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetType.WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS: //移动或联通
                    case TelephonyManager.NETWORK_TYPE_EDGE:  //移动或联通
                    case TelephonyManager.NETWORK_TYPE_CDMA: //电信
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetType.MOBIL_2G;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0: //电信
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //电信
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_UMTS: //联通
                        return NetType.MOBIL_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                        return NetType.MOBIL_4G;
                    default:
                        String subTypeName = info.getSubtypeName();
                        if (!TextUtils.isEmpty(subTypeName) && (subTypeName.equalsIgnoreCase("TD-SCDMA") || subTypeName.equalsIgnoreCase("WCDMA"))) {
                            return NetType.MOBIL_3G;
                        }
                        break;
                }
            }
        }
        return NetType.UNKNOWN;
    }

    /**
     * 获取当前的网络状态 :WIFI网络1：4G网络-2：3G网络-3：2G网络-4  其他:-5
     */
    public static String getNetwork(Context context) {
        int type = 5;
        NetType networkType = getNetworkType(context);
        switch (networkType) {
            case WIFI:
                type = 1;
                break;
            case MOBIL_4G:
                type = 2;
                break;
            case MOBIL_3G:
                type = 3;
                break;
            case MOBIL_2G:
                type = 4;
                break;
            case UNKNOWN:
                type = 5;
                break;
        }
        return String.valueOf(type);
    }


    public static Location getLocation(Context context) {
        Location location = null;
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (Throwable throwable) {
                Logger.e(TAG, "getLocation: gps", throwable);
            }
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "getLocation: network", throwable);
        }
        return location;
    }

    public static CellLocation getCellLocation(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getCellLocation();
        } catch (Throwable throwable) {
            Logger.e(TAG, "getCellLocation: failed!", throwable);
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    public static int getCpuNum() {
        int cpuNum = 0;
        File folder = new File("/sys/devices/system/cpu");
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().matches("cpu[\\d]+")) {
                    cpuNum++;
                }
            }
        }
        return Math.max(1, cpuNum);
    }

    public static long getCpuFrequency() {
        long frequency = 0;
        try {
            File file = new File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            byte[] data = FileAccesser.readBytes(file);
            if (data != null) {
                Pattern pattern = Pattern.compile("[\\d]+");
                Matcher matcher = pattern.matcher(new String(data));
                if (matcher.find()) {
                    frequency = Long.parseLong(matcher.group());
                }
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "getCpuFrequency: failed!", throwable);
        }
        return frequency;
    }

    /**
     * 获取ROM名称
     *
     * @return
     */
    public static String getRomName() {
        return getRomInfo()[0];
    }

    /**
     * 获取ROM版本
     *
     * @return
     */
    public static String getRomVersion() {
        return getRomInfo()[1];
    }

    private static final String ROM_PROPERTY_ALIYUN = "ro.yunos.version";/*阿里云*/
    private static final String ROM_PROPERTY_MIUI = "ro.miui.ui.version.name";/*小米*/
    private static final String ROM_NAME_ALIYUN = "aliyun";/*阿里云*/
    private static final String ROM_NAME_MIUI = "xiaomi";/*小米*/

    /**
     * 获取ROM信息
     *
     * @return 0:ROM名称;1:ROM版本
     */
    private static String[] getRomInfo() {
        if (sRomInfoArray != null) {
            return sRomInfoArray;
        }
        try {
            sRomInfoArray = new String[]{"", ""};
            Pattern pattern = Pattern.compile("[\\S]+");
            String version = SystemUtil.getSystemProperty(ROM_PROPERTY_ALIYUN);
            Matcher matcher = pattern.matcher(version);
            if (matcher.find()) {
                sRomInfoArray[0] = ROM_NAME_ALIYUN;
                sRomInfoArray[1] = matcher.group();
                return sRomInfoArray;
            }
            version = SystemUtil.getSystemProperty(ROM_PROPERTY_MIUI);
            matcher = pattern.matcher(version);
            if (matcher.find()) {
                sRomInfoArray[0] = ROM_NAME_MIUI;
                sRomInfoArray[1] = matcher.group();
                return sRomInfoArray;
            }
            sRomInfoArray[0] = Build.MANUFACTURER;
            sRomInfoArray[1] = Build.DISPLAY;
            return sRomInfoArray;
        } catch (Throwable throwable) {
            Logger.e(TAG, "getRomInfo: {}", throwable);
            return sRomInfoArray;
        }
    }

    public static int[] getScreenSize(Context context) {
        int[] size = new int[2];
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            size[0] = Math.min(metrics.widthPixels, metrics.heightPixels);
            size[1] = Math.max(metrics.widthPixels, metrics.heightPixels);
        } catch (Throwable throwable) {
            Logger.e(TAG, "getScreenSize: {}", throwable);
        }
        return size;
    }

    public static long getAvailableExtSpace() {
        return getAvailableSpace(Environment.getExternalStorageDirectory());
    }

    public static long getAvailableDataSpace() {
        return getAvailableSpace(Environment.getDataDirectory());
    }

    private static long getAvailableSpace(File file) {
        return file.getFreeSpace();
    }

    public static long getTotalMem() {
        long mem = 0;
        try {
            File file = new File("/proc/meminfo");
            byte[] data = FileAccesser.readBytes(file);
            if (data != null) {
                Pattern pattern = Pattern.compile("MemTotal[\\D]+([\\d]+)[\\D]+kB");
                Matcher matcher = pattern.matcher(new String(data));
                if (matcher.find()) {
                    mem = Long.parseLong(matcher.group(1)) * 1024;
                }
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "getTotalMem: failed!", throwable);
        }
        return mem;
    }

    public static long getAvailableMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);
        return info.availMem;
    }

    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isScreenOn();
        } else {
            return pm.isInteractive();
        }
    }

    public static String getAndroidID(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable throwable) {
            Logger.e(TAG, "getAndroidID: failed!", throwable);
        }
        return null;
    }

    public static int getAndroidVC() {
        return Build.VERSION.SDK_INT;
    }

    public static String getAndroidVN() {
        return Build.VERSION.RELEASE;
    }

    public static String getAndroidSV() {
        return Build.DISPLAY;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    public static String getUserAgent() {
        return System.getProperty("http.agent");
    }

    public static String getSystemProperty(String key, String defaultValue) {
        try {
            Class clsSystemProperties = Class.forName("android.os.SystemProperties");
            Method medGet = clsSystemProperties.getDeclaredMethod("get", new Class[]{String.class, String.class});
            medGet.setAccessible(true);
            return (String) medGet.invoke(clsSystemProperties, new Object[]{key, defaultValue});
        } catch (Throwable throwable) {
            Logger.e(TAG, "getSystemProperty: ", key, throwable);
        }
        return defaultValue;
    }

    /**
     * 获取WiFi热点列表.
     *
     * @param context
     * @return
     */
    public static List<String> getWifiListInfo(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (SystemUtil.checkPermission(context, context.getPackageName(), Manifest.permission.CHANGE_WIFI_STATE)) {
                wm.startScan();
            }
            List<ScanResult> scanResults = wm.getScanResults();
            if (scanResults != null && scanResults.size() != 0) {
                List<String> wifiInfo = new ArrayList<>();
                Map<String, String> wifi = new HashMap<>();
                for (ScanResult result : scanResults) {
                    String ssid = result.SSID;
                    if (!TextUtils.isEmpty(ssid)) {
                        ssid = ssid.replaceAll("\"", "");
                        ssid = ssid.replaceAll("\\\\", "");
                    }

                    if (!wifi.containsKey(ssid)) {
                        wifi.put(ssid, ssid);
                        wifiInfo.add(ssid);
                    }
                }
                return wifiInfo;
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "getWifiListInfo: {}", throwable);
        }
        return null;
    }


    /**
     * 检查权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        try {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable throwable) {
            Logger.e(TAG, "checkPermission: {}, {}", permission, throwable);
            return false;
        }
    }

    public static boolean setBatteryOptimization(Activity activity) {
        boolean hasIgnored = isIgnorBatteryOptimization(activity);
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(intent, 1);
            } else {
                Logger.e("App#", "-------------- 当前版本不支持电池优化 --------------------");
            }
        } else {
            Logger.e("App#", "-------------- 已忽略电池优化 --------------------");
        }

        return hasIgnored;
    }

    public static boolean isIgnorBatteryOptimization(Activity activity) {
        PowerManager powerManager = (PowerManager) activity.getSystemService(POWER_SERVICE);

        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        } else {
            hasIgnored = true;
        }

        return hasIgnored;
    }

    /**
     * 获取运营商
     *
     * @return 运营商 1:移动  2.联通 3.电信 4.其他
     */
    public static String getProvider(Context context) {
        String provider = "4";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String IMSI = telephonyManager.getSubscriberId();
//            Log.d(TAG, "getProvider.IMSI:" + IMSI);
            if (IMSI == null) {
                if (TelephonyManager.SIM_STATE_READY == telephonyManager.getSimState()) {
                    String operator = telephonyManager.getSimOperator();
//                    Log.d(TAG, "getProvider.operator:" + operator);
                    if (operator != null) {
                        if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                            provider = "1";
                        } else if (operator.equals("46001")) {
                            provider = "2";
                        } else if (operator.equals("46003")) {
                            provider = "3";
                        }
                    }
                }
            } else {
                if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                    provider = "1";
                } else if (IMSI.startsWith("46001")) {
                    provider = "2";
                } else if (IMSI.startsWith("46003")) {
                    provider = "3";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return provider;
    }

    public static String getWifiIP(Context context) {
        String ip = null;
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            ip = "" + (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                    + "." + (i >> 24 & 0xFF);
        }
        return ip;
    }

    public static String getMobileIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * 获取当前进程名
     */
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

}
