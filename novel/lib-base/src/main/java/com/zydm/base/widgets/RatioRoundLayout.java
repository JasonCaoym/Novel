package com.zydm.base.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.zydm.base.R;


/**
 * Created by jia on 15-11-19.
 */
public class RatioRoundLayout extends RelativeLayout {

    private static final float DELT = 0.0f;

    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;
    private float whRatio;

    private Paint roundPaint;
    private Paint imagePaint;

    public RatioRoundLayout(Context context) {
        this(context, null);
    }

    public RatioRoundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioRoundLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundRatioLayout);
        float radius = ta.getDimension(R.styleable.RoundRatioLayout_angleRadius, 0);
        topLeftRadius = ta.getDimension(R.styleable.RoundRatioLayout_tlRadius, radius);
        topRightRadius = ta.getDimension(R.styleable.RoundRatioLayout_trRadius, radius);
        bottomLeftRadius = ta.getDimension(R.styleable.RoundRatioLayout_blRadius, radius);
        bottomRightRadius = ta.getDimension(R.styleable.RoundRatioLayout_brRadius, radius);
        whRatio = ta.getFloat(R.styleable.RoundRatioLayout_whRatio, 1f);
        ta.recycle();
        roundPaint = new Paint();
        roundPaint.setColor(Color.WHITE);
        roundPaint.setAntiAlias(true);
        roundPaint.setStyle(Paint.Style.FILL);
        roundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        imagePaint = new Paint();
        imagePaint.setXfermode(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //父控件是否是固定值或者是match_parent
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            //得到父容器的宽度
            int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            //得到子控件的宽度
            int childWidth = parentWidth - getPaddingLeft() - getPaddingRight();
            //计算子控件的高度
            int childHeight = (int) (childWidth / whRatio + 0.5f);
            //计算父控件的高度
            int parentHeight = childHeight + getPaddingBottom() + getPaddingTop();

            //测量子控件,固定孩子的大小
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
            //测量
            setMeasuredDimension(parentWidth, parentHeight);
            super.onMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), imagePaint, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        if (topLeftRadius<=0 && topRightRadius<=0 && bottomLeftRadius<=0 && bottomRightRadius<=0) {
            return;
        }
        drawTopLeft(canvas);
        drawTopRight(canvas);
        drawBottomLeft(canvas);
        drawBottomRight(canvas);
        canvas.restore();
    }

    private void drawTopLeft(Canvas canvas) {
        if (topLeftRadius > 0) {
            Path path = new Path();
            path.moveTo(0, topLeftRadius+DELT);
            path.lineTo(0, 0);
            path.lineTo(topLeftRadius+DELT, 0);
            path.arcTo(new RectF(0, 0, topLeftRadius * 2+DELT, topLeftRadius * 2+DELT),
                    -90, -90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }

    private void drawTopRight(Canvas canvas) {
        if (topRightRadius > 0) {
            int width = getWidth();
            Path path = new Path();
            path.moveTo(width - topRightRadius - DELT, 0);
            path.lineTo(width, 0);
            path.lineTo(width, topRightRadius+DELT);
            path.arcTo(new RectF(width - 2 * topRightRadius - DELT, 0, width,
                    topRightRadius * 2 + DELT), 0, -90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }

    private void drawBottomLeft(Canvas canvas) {
        if (bottomLeftRadius > 0) {
            int height = getHeight();
            Path path = new Path();
            path.moveTo(0, height - bottomLeftRadius - DELT);
            path.lineTo(0, height);
            path.lineTo(bottomLeftRadius + DELT, height);
            path.arcTo(new RectF(0, height - 2 * bottomLeftRadius - DELT,
                    bottomLeftRadius * 2 + DELT, height), 90, 90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }

    private void drawBottomRight(Canvas canvas) {
        if (bottomRightRadius > 0) {
            int height = getHeight();
            int width = getWidth();
            Path path = new Path();
            path.moveTo(width - bottomRightRadius - DELT, height);
            path.lineTo(width, height);
            path.lineTo(width, height - bottomRightRadius - DELT);
            path.arcTo(new RectF(width - 2 * bottomRightRadius - DELT, height - 2
                    * bottomRightRadius - DELT, width, height), 0, 90);
            path.close();
            canvas.drawPath(path, roundPaint);
        }
    }
}
