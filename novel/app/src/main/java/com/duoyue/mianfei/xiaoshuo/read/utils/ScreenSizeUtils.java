package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ScreenSizeUtils {

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }

}
