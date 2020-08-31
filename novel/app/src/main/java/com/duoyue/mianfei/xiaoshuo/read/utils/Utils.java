package com.duoyue.mianfei.xiaoshuo.read.utils;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.*;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.zzdm.tinker.util.SampleApplicationContext.application;

public class Utils {
    /*public static void regiesterJiGuang() {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null) return;

        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
        tagAliasBean.alias = "123456"*//*userInfo.uid*//*;
        Logger.d("JIG", "############" + UserManager.getInstance().getUserInfo().uid);
        tagAliasBean.isAliasAction = true;
        sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(application, sequence, tagAliasBean);
    }*/

    /**
     * 判断某一个类是否存在任务栈里面
     *
     * @return
     */
    public static boolean isExsitMianActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }

    /**
     * 判断某activity是否处于栈顶
     *
     * @return true在栈顶 false不在栈顶
     */
    public static boolean isActivityTop(Context context, Class cls) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */

    public static int dp2px(float dpValue) {
        float scale = application.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    public static int sp2px(float spValue) {
        float fontScale = application.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return application.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        return application.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取剪贴板内容
     */

    public static String getClipText() {
        String content = "";
        try {
            ClipboardManager cm = (ClipboardManager) application.getSystemService(CLIPBOARD_SERVICE);
            ClipData data = cm.getPrimaryClip();
            if(data == null){
                return "";
            }
            ClipData.Item item = data.getItemAt(0);
            content = item.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 清除剪贴板
     */
    public static void cleanClipText() {
        ClipboardManager cm = (ClipboardManager) application.getSystemService(CLIPBOARD_SERVICE);
        // 清除剪贴板
        ClipData clip = ClipData.newPlainText("", "");
        cm.setPrimaryClip(clip);
    }

    private static final int MIN_DELAY_TIME = 500;  // 两次点击间隔不能少于500ms
    private static long lastClickTime;

    /**
     * 防止快速点击时响应多次
     *
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static boolean isFastClick(long time) {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= time) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    /**
     * 解决OPPO R9手机 java.util.concurrent.TimeoutException: android.content.res.AssetManager.finalize() timed out after 120 seconds的超时问题
     */
    public static void fix() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get(null));

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断字符串以XX开头
     *
     * @return
     */
    public static boolean isStartsWith(String string, String key) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        if (string.startsWith(key)) {
            return true;
        }
        return false;
    }

    /**
     * 生成一个startNum 到 endNum之间的随机数(不包含endNum的随机数)
     * @param startNum
     * @param endNum
     * @return
     */
    public static int getNum(int startNum,int endNum){
        if(endNum > startNum){
            Random random = new Random();
            return random.nextInt(endNum - startNum) + startNum;
        }
        return 0;
    }
}
