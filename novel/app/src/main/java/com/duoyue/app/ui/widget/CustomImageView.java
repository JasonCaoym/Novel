package com.duoyue.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 自定义ImageView, 防止Bitmap被回收报异常.
 * @author caoym
 * @data 2019/5/23  16:23
 */
public class CustomImageView extends ImageView
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#CustomImageView";

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        try
        {
            super.onDraw(canvas);
        } catch (Throwable throwable)
        {
            //Logger.e(TAG, "onDraw: {}", throwable);
        }
    }
}
