package com.duoyue.app.upgrade;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class ReadModeUtil {

    public static final String TAG = "BOOKID";

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    private static volatile ReadModeUtil readModeUtil;

    public static ReadModeUtil getInstance(Context context) {
        if (readModeUtil == null) {
            synchronized (ReadModeUtil.class) {
                if (readModeUtil == null) {
                    readModeUtil = new ReadModeUtil();
                    sharedPreferences = context.getSharedPreferences("ReadModeUtil", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                }
            }
        }
        return readModeUtil;
    }

    public void setMode(String bookid) {
        editor.clear();
        editor.putString(TAG, bookid);
        editor.commit();
    }

    public String getMode() {
        return sharedPreferences.getString(TAG, null);
    }

    public void onDestroy() {
        if (editor != null) {
            editor.clear();
            editor.commit();
            editor = null;
        }
        sharedPreferences = null;
        readModeUtil = null;
    }
}
