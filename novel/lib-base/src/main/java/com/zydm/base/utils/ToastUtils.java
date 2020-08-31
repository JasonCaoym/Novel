package com.zydm.base.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import com.zydm.base.R;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;
import com.zydm.base.utils.toast.ToastCompat;

public class ToastUtils {

    private static final String TAG = "ToastUtils";

    private static String sWaitToToastStr;
    private static boolean sIsToasting;
    private static Runnable sToastingTask = new Runnable() {
        @Override
        public void run() {
            if (StringUtils.isBlank(sWaitToToastStr)) {
                sIsToasting = false;
                return;
            }
            show(sWaitToToastStr);
            sWaitToToastStr = null;
        }
    };

    private static String getString(int resId) {
        return ViewUtils.getString(resId);
    }

    public static void showLimited(int resId) {
        showLimited(getString(resId));
    }

    public static void showLimited(String str) {
        if (StringUtils.isBlank(str)) {
            return;
        }
        if (sIsToasting) {
            sWaitToToastStr = str;
        } else {
            show(str);
        }
    }

    public static void show(int resId) {
        show(getString(resId));
    }

    public static void show(final Toast toast) {
        final Handler mainHandler = BaseApplication.handler;
        mainHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    toast.show();
                    mainHandler.removeCallbacks(sToastingTask);
                    mainHandler.postDelayed(sToastingTask, Constants.SECOND_2);
                    sIsToasting = true;
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getLocalizedMessage(), e);
                }
            }
        });
    }

    public static void
    show(final String str) {
        final Handler mainHandler = BaseApplication.handler;
        mainHandler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    Toast toast = ToastCompat.makeText(BaseApplication.context.globalContext, str, Toast.LENGTH_SHORT);
                    if (toast != null) {
                        toast.show();
                        mainHandler.removeCallbacks(sToastingTask);
                        mainHandler.postDelayed(sToastingTask, Constants.SECOND_2);
                        sIsToasting = true;
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getLocalizedMessage(), e);
                }
            }
        });
    }

    public static void showRewardVideoToast(final String str) {
        BaseApplication.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    final CustomToast toast = new CustomToast(BaseApplication.context.globalContext, BaseApplication.handler);
                    toast.show(str, 10_000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 1000);
    }
}
