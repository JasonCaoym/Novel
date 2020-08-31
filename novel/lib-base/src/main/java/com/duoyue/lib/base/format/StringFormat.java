package com.duoyue.lib.base.format;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import com.duoyue.lib.base.log.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class StringFormat
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#StringFormat";

    public static int parseInt(String data, int defaultValue)
    {
        try
        {
            return Integer.parseInt(data);
        } catch (Throwable throwable)
        {
            //ignore
        }
        return defaultValue;
    }

    public static long parseLong(String data, long defaultValue)
    {
        try
        {
            return Long.parseLong(data);
        } catch (Throwable throwable)
        {
            //ignore
        }
        return defaultValue;
    }

    public static float parseFloat(String data, float defaultValue)
    {
        try
        {
            return Float.parseFloat(data);
        } catch (Throwable throwable)
        {
            //ignore
        }
        return defaultValue;
    }

    public static double parseDouble(String data, double defaultValue)
    {
        try
        {
            return Double.parseDouble(data);
        } catch (Throwable throwable)
        {
            //ignore
        }
        return defaultValue;
    }

    public static boolean parseBool(String data, boolean defaultValue)
    {
        try
        {
            return Boolean.parseBoolean(data);
        } catch (Throwable throwable)
        {
            //ignore
        }
        return defaultValue;
    }

    /**
     * 判断字符串是否为空.
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str)
    {
        return TextUtils.isEmpty(str);
    }

    /**
     * 判断List是否为空.
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection collection)
    {
        return collection == null || collection.isEmpty();
    }

    /**
     * 将Object类型转换为字符串.
     * @param obj
     * @return
     */
    public static String toString(Object obj)
    {
        return obj == null ? "" : obj.toString().trim();
    }

    /**
     * 把String[]转换为List<String>
     *
     * @param paramArray 参数数组
     * @return
     */
    public static List<String> stringArrayConvertList(String[] paramArray)
    {
        if (paramArray == null || paramArray.length <= 0)
        {
            return null;
        }
        List<String> paramList = new ArrayList<String>();
        //遍历参数数组.
        for (String param : paramArray)
        {
            if (TextUtils.isEmpty(param))
            {
                continue;
            }
            paramList.add(param);
        }
        return paramList;
    }

    /**
     * 生成指定位数的随机数
     * @param length
     * @return
     */
    public static String getRandom(int length)
    {
        String val = "";
        try {

            Random random = new Random();
            for (int i = 0; i < length; i++) {
                val += String.valueOf(random.nextInt(10));
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getRandom: {}, {}", length, throwable);
        }
        return val;
    }

    /**
     * 把List<String>转换为String[]
     *
     * @param paramList
     * @return
     */
    public static String[] listConvertStringArray(List<String> paramList)
    {
        if (paramList == null || paramList.isEmpty())
        {
            return null;
        }
        String[] paramArray = new String[paramList.size()];
        for (int index = 0; index < paramList.size(); index++)
        {
            paramArray[index] = paramList.get(index);
        }
        return paramArray;
    }

    public static void setTextViewColor(TextView textView, int color, int start, int end) {
        SpannableString spannableString = new SpannableString(textView.getText().toString());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

}
