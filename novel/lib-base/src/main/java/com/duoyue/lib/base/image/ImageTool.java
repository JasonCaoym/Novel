package com.duoyue.lib.base.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.duoyue.lib.base.log.Logger;

import static com.zydm.base.utils.ViewUtils.getResources;

/**
 * @author caoym
 * @data 2019/3/29  18:43
 */
public class ImageTool
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#ImageTool";

    /**
     * 通过资源id获取Drawable
     * @param context
     * @param resId
     * @return
     */
    public static Drawable getDrawable(Context context, int resId)
    {
        try
        {
            return context.getResources().getDrawable(resId);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getDrawable: {}, {}, 异常:{}", context, resId, throwable);
            return null;
        }
    }
}
