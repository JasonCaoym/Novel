package com.duoyue.lib.base.devices;

import com.duoyue.lib.base.log.Logger;

import java.io.Closeable;

/**
 * @author caoym
 * @data 2019/4/28  19:52
 */
public class IOUtil
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#IOUtil";

    public static void close(Closeable obj)
    {
        if (obj != null)
        {
            try
            {
                obj.close();
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "close: 关闭对象失败: {}", throwable);
            }
        }
    }
}
