package com.duoyue.app.upgrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.duoyue.app.upgrade.download.UpgradeMsgBean;
import com.google.gson.Gson;


/**
 * Created by cangck on 2018/2/1.
 * packageName: com.android.launcher3.googelserver
 * project name : Launcher3-Pixel-v2.1
 */

public class UpgradeMsgUtils {
    private static final String KEY_FILE = "upgrade_msg";
    private static final String KEY_NOTIFICATION_IS_UPDATE = "is_update";
    private static final String KEY_NOTIFICATION_UPDATE = "msg_update";


    public static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(KEY_FILE, Context.MODE_PRIVATE);
    }

    /**
     * 保存版本更新新
     *
     * @param context
     */
    public static void storeUpdateInfo(Context context, boolean flag) {
        getSp(context).edit().putBoolean(KEY_NOTIFICATION_IS_UPDATE, flag).commit();
    }

    /**
     * 获取版本更新信息
     *
     * @param context
     * @return
     */
    public static boolean isHasUpdateInfo(Context context) {
        return getSp(context).getBoolean(KEY_NOTIFICATION_IS_UPDATE, false);
    }

    /**
     * 保存推送过来的消息
     *
     * @param context
     * @param msg
     */
    public static void storeUpdateMsg(Context context, String msg) {
        getSp(context).edit().putString(KEY_NOTIFICATION_UPDATE, msg).commit();
    }

    public static void clearUpdateMsg(Context context ) {
        getSp(context).edit()
                .remove(KEY_NOTIFICATION_IS_UPDATE)
                .commit();
    }

    /**
     * 获取通知更新服务
     *
     * @param context
     * @return
     */
    public static UpgradeMsgBean getUpgradeMsg(Context context) {
        String msg = getSp(context).getString(KEY_NOTIFICATION_UPDATE, "");
        return toMsgBean(msg, UpgradeMsgBean.class);
    }

    private static <T> T toMsgBean(String msg, Class<T> classType) {
        if (TextUtils.isEmpty(msg)) {
            return null;
        }
        return new Gson().fromJson(msg, classType);
    }

}
