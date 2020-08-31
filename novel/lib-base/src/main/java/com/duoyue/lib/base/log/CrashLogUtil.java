package com.duoyue.lib.base.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.duoyue.lib.base.crash.CrashLogPresenter;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashLogUtil {

    public static void collectDeviceInfo(Context ctx, Throwable ex) {
        Map<String, String> infos = new HashMap<String, String>();
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
        uploadLog(BuildCrashInfo(ex, infos));
        if (Logger.isDebug()) {
            writeLog(ctx, BuildCrashInfo(ex, infos));
        }
    }

    private static String BuildCrashInfo(Throwable ex, Map<String, String> infos) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

    public static void uploadLog(final String crashInfo) {
        CrashLogPresenter.uploadCrashLog(crashInfo);
    }

    public static synchronized void writeLog(final Context context, final String crashInfo) {
        if (null == crashInfo) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintStream writer = null;
                try {
                    if (context.getExternalCacheDir() == null) {
                        return;
                    }
                    Logger.e("crash", "crash文件路径：" + context.getExternalCacheDir().getAbsolutePath());
                    File rootFile = new File(context.getExternalCacheDir().getAbsolutePath());
                    if (!rootFile.exists()) {
                        rootFile.mkdirs();
                    }
                    File file = new File(rootFile.getPath(), "" + System.currentTimeMillis() + ".txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    StringBuffer date = new StringBuffer();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date.append(format.format(new Date())).append(" ");
                    writer = new PrintStream(new FileOutputStream(file, true), true);
                    writer.print(date.toString());
                    writer.print(crashInfo);
                    writer.print("\n");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        }).start();
    }

}
