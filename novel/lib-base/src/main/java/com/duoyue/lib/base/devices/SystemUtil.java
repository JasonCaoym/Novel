package com.duoyue.lib.base.devices;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import com.duoyue.lib.base.crypto.MD5;
import com.duoyue.lib.base.log.Logger;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SystemUtil
{
    private static final String TAG = "Base#SystemUtil";

    public static PackageInfo getPackageInfo(Context context, String pkg)
    {
        try
        {
            return context.getPackageManager().getPackageInfo(pkg, 0);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getPackageInfo: failed!", throwable);
        }
        return null;
    }

    public static PackageInfo getApkInfo(Context context, File file)
    {
        try
        {
            String path = file.getAbsolutePath();
            PackageInfo info = context.getPackageManager().getPackageArchiveInfo(path, 0);
            if (info != null)
            {
                info.applicationInfo.sourceDir = path;
                info.applicationInfo.publicSourceDir = path;
            }
            return info;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getApkInfo: failed!", throwable);
        }
        return null;
    }

    public static ApplicationInfo getApplicationInfo(Context context, String pkg)
    {
        try
        {
            return context.getPackageManager().getApplicationInfo(pkg, 0);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getApplicationInfo: failed!", throwable);
        }
        return null;
    }

    public static String getSignature(Context context, String pkg)
    {
        try
        {
            PackageInfo info = context.getPackageManager().getPackageInfo(pkg, PackageManager.GET_SIGNATURES);
            return MD5.getMD5(info.signatures[0].toByteArray());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getSignature: failed!", throwable);
        }
        return "";
    }

    /**
     * 修改NavigationBar背景颜色.
     * @param activity
     * @param colorResId
     */
    public static void setNavigationBarColor(Activity activity, int colorResId)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            try {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "setNavigationBarColor: {}, {}", activity, throwable);
            }
        }
    }

    /**
     * 解决在Android P上的提醒弹窗 （Detected problems with API compatibility(visit g.co/dev/appcompat for more info).
     */
    public static void closeAndroidPDialog()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
        {
            //Android 9.0以下的系统版本不需要处理.
            return;
        }
        try
        {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        }  catch (Throwable throwable)
        {
            Logger.e(TAG, "closeAndroidPDialog: {}", throwable);
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "closeAndroidPDialog: {}", throwable);
        }
    }

    /**
     * 跳转到系统授权页面
     * @param activity
     * @param requestCode
     */
    public static void gotoPermissionPage(Activity activity, int requestCode)
    {
        Intent intent = null;
        /*try
        {
            //获取当前应用包名.
            String pkgName = activity.getPackageName();
           switch (Build.MANUFACTURER)
            {
                case "HUAWEI":
                    //华为.
                    intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
                    intent.setComponent(comp);
                    break;
                case "vivo":
                    intent = activity.getPackageManager().getLaunchIntentForPackage("com.vivo.securedaemonservice");
                    break;
                case "OPPO":
                    //goOppoMainager();
                    break;
                case "Coolpad":
                    intent = activity.getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
                    break;
                case "Meizu":
                    intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra("packageName", pkgName);
                    break;
                case "Xiaomi":
                    //获取Miui版本号.
                    String rom = getMiuiVersion();
                    intent=new Intent();
                    if ("V6".equals(rom) || "V7".equals(rom))
                    {
                        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                        intent.putExtra("extra_pkgname", pkgName);
                    } else if ("V8".equals(rom) || "V9".equals(rom))
                    {
                        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                        intent.putExtra("extra_pkgname", pkgName);
                    }
                    break;
                case "samsung":
                    //三星4.3可以直接跳转
                    break;
                case "Sony":
                    //索尼
                    intent = new Intent(pkgName);
                    comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
                    intent.setComponent(comp);
                    break;
                case "LG":
                    intent = new Intent(pkgName);
                    comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
                    intent.setComponent(comp);
                    break;
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "gotoPermissionPage: {}", throwable);
        }*/
        if (intent == null)
        {
            try
            {
                //使用系统默认方式启用授权页面.
                Uri packageURI = Uri.fromParts("package", activity.getPackageName(), null);
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "gotoPermissionPage: default: {}", throwable);
            }
        }
        //进入授权页面.
        try {
            activity.startActivityForResult(intent, requestCode);
            //activity.startActivity(intent);
        } catch (Throwable th) {
            Logger.e(TAG, "gotoPermissionPage: {}, {}", intent, th);
        }
    }

    /**
     * 获取Miui版本号.
     * @return
     */
    private static String getMiuiVersion()
    {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (Throwable throwable) {
            Logger.e(TAG, "getMiuiVersion: {}", throwable);
            return null;
        } finally
        {
            try
            {
                if (input != null)
                {
                    input.close();
                }
            } catch (Throwable throwable) {
            }
        }
        Logger.d(TAG, "getMiuiVersion: {}", line);
        return line;
    }

    /**
     * 检查是否授权指定权限.
     * @param context
     * @param pkg
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String pkg, String permission)
    {
        try
        {
            return context.getPackageManager().checkPermission(permission, pkg) == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "checkPermission: failed!", throwable);
        }
        return false;
    }

    public static String getSystemProperty(String propName)
    {
        String[] result = executeCMD("getprop " + propName);
        return result[0];
    }

    public static String[] executeCMD(String cmd)
    {
        String[] result = new String[]{"", ""};
        try
        {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = null;
            InputStream errorStream = null;
            ByteArrayOutputStream outputStream = null;
            try
            {
                inputStream = process.getInputStream();
                errorStream = process.getErrorStream();
                outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, length);
                }
                result[0] = new String(outputStream.toByteArray());

                outputStream.reset();
                while ((length = errorStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, length);
                }
                result[1] = new String(outputStream.toByteArray());
            } finally
            {
                IOUtil.close(inputStream);
                IOUtil.close(errorStream);
                IOUtil.close(outputStream);
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "executeCMD: {}", throwable);
        }
        return result;
    }
}
