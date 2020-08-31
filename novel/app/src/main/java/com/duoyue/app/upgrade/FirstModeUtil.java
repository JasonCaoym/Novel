package com.duoyue.app.upgrade;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class FirstModeUtil {

    public static final String TAG = "First";

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    private static volatile FirstModeUtil readModeUtil;

    public static FirstModeUtil getInstance(Context context) {
        if (readModeUtil == null) {
            synchronized (FirstModeUtil.class) {
                if (readModeUtil == null) {
                    readModeUtil = new FirstModeUtil();
                    sharedPreferences = context.getSharedPreferences("FirstModeUtil", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                }
            }
        }
        return readModeUtil;
    }

    public void setMode(String first) {
        editor.clear();
        editor.putString(TAG, first);
        editor.commit();
    }

    public String getMode() {
        return sharedPreferences.getString(TAG, null);
    }

    public void onDestroy() {
        editor.clear();
        editor.commit();
        editor = null;
        sharedPreferences = null;
        readModeUtil = null;
    }
}
