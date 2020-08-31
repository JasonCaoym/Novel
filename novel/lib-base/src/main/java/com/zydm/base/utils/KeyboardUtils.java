package com.zydm.base.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jia on 15-11-27.
 */
public class KeyboardUtils {

    public static void showKeyboard(Context context, View tokenView) {
        if (null == context || null == tokenView) return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(tokenView, InputMethodManager.SHOW_FORCED);
    }

    public static void hideKeyboard(Context context, View tokenView) {
        if (null == context || null == tokenView) return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tokenView.getWindowToken(), 0);
    }
}
