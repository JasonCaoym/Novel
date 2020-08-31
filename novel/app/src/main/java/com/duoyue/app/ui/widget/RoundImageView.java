package com.duoyue.app.ui.widget;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆角ImageView
 *
 * @author caoyaming
 */
public class RoundImageView extends CustomImageView
{

    private Paint mPaint;

    public RoundImageView(Context context)
    {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mPaint = new Paint();

    }

    /**
     * 绘制圆形图片
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        try
        {
            Drawable drawable = getDrawable();
            if (null != drawable)
            {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Bitmap b = getCircleBitmap(bitmap, 14);
                final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
                final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
                mPaint.reset();
                canvas.drawBitmap(b, rectSrc, rectDest, mPaint);
            } else
            {
                super.onDraw(canvas);
            }
        } catch (Throwable throwable)
        {
        }
    }

    /**
     * 获取圆形图片方法
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    private Bitmap getCircleBitmap(Bitmap bitmap, int pixels)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        mPaint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        mPaint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, mPaint);
        return output;
    }
}
