package com.duoyue.app.notification;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationManagerCompat;
import com.duoyue.app.ui.view.NotificationPermissionDialogFragment;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.zydm.base.utils.SharePreferenceUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotificationsUtils {

    private static final String TAG = "App#NotificationsUtils";
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;

        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);

            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                    AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断通知栏是否被关闭
     */
    public static void isNotifyEnable(Context context, FragmentManager fragmentManager, String currPageId) {
        boolean areNotificationsEnabled = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();

            if (areNotificationsEnabled) {
                Logger.e(TAG, "通知栏已经开启");
            } else {
                Logger.e(TAG, "通知栏已经关闭");
                long lastShowTime = SharePreferenceUtils.getLong(context, SharePreferenceUtils.DIALOG_SHOW_FIRST_TIME_EVERY_DAY, 0);
                if (lastShowTime == 0 || lastShowTime < TimeTool.getCurrDayBeginTime()) {//今天首次展示
                    showPermissionDialog(fragmentManager,currPageId);
                }else {
                    Logger.d(TAG, "isNotifyEnable: 今天已经展示过了");
                }
            }
        }
    }

    public static void requestPermission(Context context, int requestCode) {
        // TODO Auto-generated method stub
        // 6.0以上系统才可以判断权限

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        }
        return;
    }

    public static void toPermiddionSetting(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        } else if (Build.VERSION.SDK_INT >= 15) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        context.startActivity(intent);
    }

    public static void showPermissionDialog(FragmentManager fragmentManager, String currPageId) {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.setCurrPageId(currPageId);
        dialogFragment.show(fragmentManager, "notifyPermission");
    }

    /**
     * 判断今天是否首次进入书城,首次进入保存进入时间,检查通知权限
     */
    public static void isFirstIn(Context context, FragmentManager fragmentManager,String currPageId) {
        long lastTime = SharePreferenceUtils.getLong(context, SharePreferenceUtils.FIRST_TIME_EVERY_DAY, 0);
        if (lastTime == 0L || lastTime < TimeTool.getCurrDayBeginTime()) {//今天首次进入书城
            SharePreferenceUtils.putLong(context, SharePreferenceUtils.FIRST_TIME_EVERY_DAY, System.currentTimeMillis());
            // 检查通知栏权限
            isNotifyEnable(context, fragmentManager,currPageId);
        }
    }

}