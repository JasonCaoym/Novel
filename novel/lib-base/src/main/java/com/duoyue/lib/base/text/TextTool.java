package com.duoyue.lib.base.text;

import android.text.TextUtils;
import com.duoyue.lib.base.log.Logger;

/**
 * @author caoym
 * @data 2019/3/25  23:05
 */
public class TextTool
{
    /**
     * 日志Tag
     */
    private static final String APP_TAG = "Base@TextTool";

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
     * 将Object类型转换为字符串.
     * @param obj
     * @return
     */
    public static String toString(Object obj)
    {
        return obj == null ? "" : obj.toString().trim();
    }

    /**
     * 将Object类型转换为Long
     *
     * @param obj
     * @param defaultValue
     * @return
     */
    public static Long toLong(Object obj, Long defaultValue)
    {
        try
        {
            if (obj != null)
            {
                return Long.valueOf(toString(obj));
            }
        } catch (Throwable throwable)
        {
            Logger.e(APP_TAG, "toLong: {}, 异常:{}", obj, throwable);
        }
        return defaultValue;
    }
}
