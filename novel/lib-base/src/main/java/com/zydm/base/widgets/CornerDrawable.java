package com.zydm.base.widgets;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CornerDrawable extends Drawable {
    private RectF mBounds;
    private float[] mRadius;
    private Paint mPaint;
    private Path mPath;

    public CornerDrawable() {
        super();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPath = new Path();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mBounds == null) {
            return;
        }

        mPath.reset();
        mPath.addRoundRect(mBounds, mRadius, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint != null) {
            mPaint.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mPaint != null) {
            mPaint.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBounds = new RectF(bounds);
        invalidateSelf();
    }

    @Override
    public int getIntrinsicWidth() {
        return mBounds == null ? super.getIntrinsicWidth() : (int) mBounds.width();
    }

    @Override
    public int getIntrinsicHeight() {
        return mBounds == null ? super.getIntrinsicHeight() : (int) mBounds.height();
    }

    public void setRadius(float[] radius) {
        mRadius = radius;
    }

    public void setRadius(float leftTop1, float leftTop2, float rightTop1, float rightTop2, float rightBottom1, float rightBottom2, float leftBottom1, float leftBottom2) {
        mRadius = new float[]{leftTop1, leftTop2, rightTop1, rightTop2, rightBottom1, rightBottom2, leftBottom1, leftBottom2};
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }
}
