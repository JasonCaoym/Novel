package com.zydm.base.utils;

import android.util.Log;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;
import com.zydm.base.rx.ExceptionUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogUtils {

    private static final String LOG_FOLDER = "/mtwl/showlogcm1230";

    private static final String APP_LOG_TAG = "MTWL:";
    private static final String LOG_FILE_NAME = "cm.txt";
    private static final String TAG = "MTWL::LogUtils";
    private static final int SINGLE_LOG_LENGTH = 3000;
    public static boolean APP_DEBUG = true;
    private static boolean sIsSaveLog = false;

    private static SimpleDateFormat sFormatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSSS", Locale.US);

    public static void initDebugLogSwitch() {
        if (BaseApplication.context.isTestEnv()) {
            APP_DEBUG = true;
        } else {
            String testPath = StorageUtils.getSDCardDir() + LOG_FOLDER;
            File file = new File(testPath);
            APP_DEBUG = file.exists();
        }
    }

    public static void log(String tag, String msg) {
        log(tag, msg, sIsSaveLog);
    }

    public static void log(String tag, String msg, boolean isSave) {
        if (APP_DEBUG) {
            if (msg.length() > SINGLE_LOG_LENGTH) {
                Log.i(APP_LOG_TAG + tag, msg.substring(0, SINGLE_LOG_LENGTH));
                log(tag, msg.substring(SINGLE_LOG_LENGTH), false);
            } else {
                Log.i(APP_LOG_TAG + tag, msg);
            }

            if (isSave) {
                try {
                    saveToSDCard(formatLog(msg, tag, "i"));
                } catch (Exception e) {
                }
            }
        }
    }

    private static void tryToSaveLog(String tag, String msg, String level) {
        if (!sIsSaveLog) {
            return;
        }
        try {
            saveToSDCard(formatLog(msg, tag, level));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void v(String tag, String msg) {
        if (!APP_DEBUG) {
            return;
        }
        Log.v(APP_LOG_TAG + tag, msg);
        tryToSaveLog(tag, msg, "V");
    }

    public static void d(String tag, String msg) {
        if (!APP_DEBUG) {
            return;
        }
        Log.d(APP_LOG_TAG + tag, msg);
        tryToSaveLog(tag, msg, "D");
    }

    public static void d(String tag, String msg, Throwable throwable) {
        if (!APP_DEBUG) {
            return;
        }
        Log.d(APP_LOG_TAG + tag, msg, throwable);
        tryToSaveLog(tag, msg, "D");
    }

    public static void i(String tag, String msg) {
        if (!APP_DEBUG) {
            return;
        }
        Log.i(APP_LOG_TAG + tag, msg);
        tryToSaveLog(tag, msg, "I");
    }

    public static void e(String msg) {
        Log.e(APP_LOG_TAG, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(APP_LOG_TAG + tag, msg);
        tryToSaveLog(tag, msg, "E");
    }

    public static void e(String tag, String msg, Throwable e) {
        Log.e(APP_LOG_TAG + tag, msg, e);
        tryToSaveLog(tag, msg, "E");
    }

    public static void eToSave(String tag, String msg, Throwable e) {
        Log.e(APP_LOG_TAG + tag, msg, e);
        try {
            saveToSDCard(formatLog(msg, tag, "E"), e, "error" + System.currentTimeMillis() + ".txt");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void w(String tag, String msg) {
        if (!APP_DEBUG) {
            return;
        }
        Log.w(APP_LOG_TAG + tag, msg);
        tryToSaveLog(tag, msg, "W");
    }

    public static void w(String tag, String msg, Throwable e) {
        if (!APP_DEBUG) {
            return;
        }
        Log.w(APP_LOG_TAG + tag, msg, e);
        tryToSaveLog(tag, msg, "W");
    }

    private static void saveToSDCard(String content) {
        saveToSDCard(content, null, LOG_FILE_NAME);
    }

    private static void saveToSDCard(String content, Throwable throwable, String fileName) {
        if (StorageUtils.isSDCardMounted()) {
            try {
                String data = "";
                synchronized (sFormatter) {
//                    data = data
//                            + sFormatter.format(Calendar.getInstance()
//                            .getTime()) + "\n" + content + "\n";
                    data = content;
                    if (throwable != null) {
                        data += ExceptionUtils.getExceptionInfo(throwable);
                    }
                }
                String sdCardDir = StorageUtils.getHomeDirAbsolute();
                File file = new File(sdCardDir, fileName);
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.seek(file.length());
                raf.write(data.getBytes(Constants.UTF_8));
                raf.close();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    public static String getFunctionName() {
        StringBuffer sb = new StringBuffer();
        sb.append("-> ");
        sb.append(Thread.currentThread().getStackTrace()[3].getMethodName());
        sb.append("()");
        sb.append("-> ");
        return sb.toString();
    }

    public static String getThreadName() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(Thread.currentThread().getName());
            sb.append("-> ");
            sb.append(Thread.currentThread().getStackTrace()[3].getMethodName());
            sb.append("()");
            sb.append(" ");
        } catch (Exception e) {
            e(TAG, e.getMessage());
        }
        return sb.toString();
    }

    public static void printStack() {
        if (APP_DEBUG) {
            try {
                throw new Exception("printStack");
            } catch (Exception e) {
                printException(e);
            }
        }
    }

    private static void printException(Exception e) {
        if (APP_DEBUG) {
            Log.e("TAG", e.getLocalizedMessage(), e);
        }
    }

    private static String formatLog(String log, String type, String level) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        synchronized (sFormatter) {
            builder.append(sFormatter.format(Calendar.getInstance().getTime()));
        }
        builder.append("][");
        builder.append(type);
        builder.append("][");
        builder.append(level);
        builder.append("]");
        builder.append(log);
        builder.append("\r\n");
        return builder.toString();
    }
}
