package com.zydm.base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;
import android.view.WindowManager;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;

import java.util.List;
import java.util.Locale;

public class SysUtils {

    private static final int VERSION_CODES_LOLLIPOP = 21;

    private static final int VERSION_CODES_LOLLIPOP_PLUS = 22;
    private static final String TAG = "SysUtils";

    private static final String SIGN_SHA1 = "843415A535917FE07330B1BC60815AEAF85430C0";

    //判断系统通知是否打开
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static boolean isLollipop() {

        return Build.VERSION.SDK_INT >= VERSION_CODES_LOLLIPOP;
    }

    // 判断是否开启了自动亮度调节
    public static boolean isAutoBrightness(Activity act) {
        boolean automicBrightness = false;
        ContentResolver aContentResolver = act.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(aContentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
            //无法获取亮度
        }
        return automicBrightness;
    }

    // 改变亮度
    public static void setLightness(Activity act, int value) {
        try {
            Settings.System.putInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
            WindowManager.LayoutParams lp = act.getWindow().getAttributes();
            lp.screenBrightness = (value <= 0 ? 1 : value) / 255f;
            act.getWindow().setAttributes(lp);
        } catch (Exception e) {
            //无法改变亮度
        }
    }

    // 获取亮度
    public static int getLightness(Activity act) {
        return Settings.System.getInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
    }

    // 停止自动亮度调节
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    // 开启亮度自动调节
    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 保存亮度设置状态
     *
     * @param resolver
     * @param brightness
     */
    public static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = android.provider.Settings.System
                .getUriFor("screen_brightness");
        android.provider.Settings.System.putInt(resolver, "screen_brightness",
                brightness);
        // resolver.registerContentObserver(uri, true, myContentObserver);
        resolver.notifyChange(uri, null);
    }

    /**
     * 判断系统通知是否打开
     *
     * @param context
     * @return
     */
    public static boolean isNotificationEnabled(Context context) {

        //api19之下统一返回true
        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        boolean notificationsEnabled = compat.areNotificationsEnabled();
        return notificationsEnabled;
    }

    /**
     * 跳转到设置的应用详情页
     *
     * @param context
     */
    public static void gotoSettingAppInfo(Context context) {
        String packageName = BaseApplication.context.globalContext.getPackageName();
        Uri uri = Uri.parse(Constants.PACKAGE + Constants.COLON + packageName);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
        context.startActivity(intent);
    }


    public static String getSignMd5() {
        try {
            byte[] bytes = getSignBytes();
            return MD5Utils.getMd5(bytes, MD5Utils.MD5);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }

    public static String getSignSha1() {
        try {
            byte[] bytes = getSignBytes();
            return MD5Utils.getMd5(bytes, MD5Utils.SHA1);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }

    public static String getSignSha265() {
        try {
            byte[] bytes = getSignBytes();
            return MD5Utils.getMd5(bytes, MD5Utils.SHA265);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }

    public static String getSignBase64() {
        try {
            byte[] bytes = getSignBytes();
            return Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY;
    }

    public static void test() {
        String signMd5 = getSignMd5();
        String signSha1 = getSignSha1();
        String signSha265 = getSignSha265();
        LogUtils.d(TAG, "signMd5:" + signMd5);
        LogUtils.d(TAG, "signSha1:" + signSha1);
        LogUtils.d(TAG, "signSha265:" + signSha265);
    }

    private static byte[] getSignBytes() throws PackageManager.NameNotFoundException {
        Context context = BaseApplication.context.globalContext;
        String packageName = context.getPackageName();
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        Signature[] signatures = packageInfo.signatures;
        return signatures[0].toByteArray();
    }

    public static boolean isSignatureOfficial() {
        return StringUtils.equalsIgnoreCase(getSignSha1(), SIGN_SHA1);
    }

    public static boolean isApkInstalled(Context context, String pkgName) {
        PackageManager pm;
        if ((pm = context.getApplicationContext().getPackageManager()) == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if (pkgName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isClientAvailable(Context context, String pkgName) {
        PackageManager pm;
        if ((pm = context.getApplicationContext().getPackageManager()) == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if (pkgName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断app是否启动过，应用于H5打开app的情况。判断当前应用栈中activity的数量，数量大于1就相当于打开过
     */
    public static boolean isAPPNotLaunched(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        return tasks.get(0).numActivities <= 1;
    }

    public static boolean isServiceRunning(Context context, String serviceClassName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClassName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean getInstalledPackages(Context context) {
        PackageManager pm;
        if ((pm = context.getApplicationContext().getPackageManager()) == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            if ((info.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM) <= 0) {
                LogUtils.d(TAG, info.packageName + "    " + info.applicationInfo.loadLabel(pm));
            }
        }
        return false;
    }
}
