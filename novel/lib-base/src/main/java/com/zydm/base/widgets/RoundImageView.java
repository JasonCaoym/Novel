package com.zydm.base.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.zydm.base.R;

public class RoundImageView extends android.support.v7.widget.AppCompatImageView {

    /**
     * 图片的类型，圆形or圆角
     */
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;

    private static final int DEFAULT_STROKE_WIDTH = 0;
    private static final int DEFAULT_STROKE_COLOR = Color.WHITE;

    private int type = TYPE_ROUND;
    /**
     * 圆角大小的默认值
     */
    private static final int BODER_RADIUS_DEFAULT_DIP = 5;
    /**
     * 圆角的大小
     */
    private int mBorderRadius;

    /**
     * 绘图的Paint
     */
    private Paint mBitmapPaint;
    /**
     * 圆角的半径
     */
    private int mRadius;
    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private Matrix mMatrix;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader mBitmapShader;
    /**
     * view的宽度
     */
    private int mWidth;
    private RectF mRoundRect;
    private boolean mIsRound = false;
    private boolean mIsOnlyTopRound;
    private boolean mIsOnlyLeftRound;

    private Paint mStrokePaint;
    private float mStrokeWidth;
    private int mStrokeColor;

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    public RoundImageView(Context context) {
        this(context, null);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

        mBorderRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_borderRadius, BODER_RADIUS_DEFAULT_DIP);

        mStrokeWidth = a.getDimensionPixelSize(R.styleable.RoundImageView_stroke_width,
                DEFAULT_STROKE_WIDTH);
        mStrokeColor = a.getColor(R.styleable.RoundImageView_stroke_color, DEFAULT_STROKE_COLOR);

        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        type = a.getInt(R.styleable.RoundImageView_type, TYPE_ROUND);
        a.recycle();
    }

    protected void init() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 如果类型是圆形，则强制改变view的宽高一致，以小值为准
         */
        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }

        if (mRoundRect == null) {
            mRoundRect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }
    }

    /**
     * 初始化BitmapShader
     */
    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitamp(drawable);
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
        int dwidth = bmp.getWidth();
        int dheight = bmp.getHeight();

        int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vheight = getHeight() - getPaddingTop() - getPaddingBottom();

        float scale;
        float dx = 0, dy = 0;

        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;
        } else {
            scale = (float) vwidth / (float) dwidth;
            dy = (vheight - dheight * scale) * 0.5f;
        }

        mMatrix.setScale(scale * 1.0f, scale * 1.0f);

        mMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        mBitmapShader.setLocalMatrix(mMatrix);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ColorFilter filter = getColorFilter();
        if (filter != null) {
            mBitmapPaint.setColorFilter(filter);
        } else {
            mBitmapPaint.setColorFilter(null);
        }
        if (!mIsRound) {
            super.onDraw(canvas);
            return;
        }
        if (getDrawable() == null) {
            return;
        }
        setUpShader();

        if (type == TYPE_ROUND && mRoundRect != null) {
            if (mIsOnlyTopRound) {
                float[] radii = {mBorderRadius, mBorderRadius, mBorderRadius, mBorderRadius, 0f, 0f, 0f, 0f};
                Path path = new Path();
                path.addRoundRect(mRoundRect, radii, Path.Direction.CW);
                canvas.drawPath(path, mBitmapPaint);
            } else if (mIsOnlyLeftRound) {
                float[] radii = {mBorderRadius, mBorderRadius, 0f, 0f, 0f, 0f, mBorderRadius, mBorderRadius};
                Path path = new Path();
                path.addRoundRect(mRoundRect, radii, Path.Direction.CW);
                canvas.drawPath(path, mBitmapPaint);
            } else {
                canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mBitmapPaint);
                if (mStrokeWidth != 0) {
                    canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mStrokePaint);
                }
            }
        } else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
            // drawSomeThing(canvas);
        }
    }

    public void setOnlyTopRound(boolean isOnlyTopRound) {
        this.mIsOnlyTopRound = isOnlyTopRound;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 圆角图片的范围
        if (type == TYPE_ROUND)
            mRoundRect = new RectF(0, 0, w, h);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state).getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }

    }

    public void setBorderRadius(int borderRadius) {
        int pxVal = dp2px(borderRadius);
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal;
            invalidate();
        }
    }

    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE) {
                this.type = TYPE_CIRCLE;
            }
            requestLayout();
        }

    }

    public void setStrokeWidth(int strokeWidth) {
        int realStrokeW = dp2px(strokeWidth);
        if (mStrokeWidth == realStrokeW) {
            return;
        }
        mStrokeWidth = realStrokeW;
        invalidate();
    }

    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources()
                .getDisplayMetrics());
    }

    @Override
    public void setImageResource(int resId) {
        mIsRound = false;
        super.setImageResource(resId);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mIsRound = true;
        super.setImageBitmap(bm);
    }

    public void setOnlyLeftRound(boolean onlyLeftRound) {
        mIsOnlyLeftRound = onlyLeftRound;
    }
}