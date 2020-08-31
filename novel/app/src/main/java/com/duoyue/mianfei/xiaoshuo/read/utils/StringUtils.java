package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class StringUtils {

    public static String dateConvert(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String getString(@StringRes int id) {
        return BaseApplication.context.globalContext.getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs) {
        return BaseApplication.context.globalContext.getResources().getString(id, formatArgs);
    }

    public static String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }

            if (c[i] > 32 && c[i] < 127)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    public static String fullToHalf(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }

            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String convertChapterTitle(String old, int seqNum) {
        if (TextUtils.isEmpty(old)) {
            return ViewUtils.getString(com.zydm.base.R.string.chapter_num, seqNum);
        } else if (!(old.contains("章") || old.contains("回") || old.contains("话"))) {
            return ViewUtils.getString(com.zydm.base.R.string.chapter_num, seqNum).concat("    ").concat(old);
        } else {
            return old;
        }
    }
}
