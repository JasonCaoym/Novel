package com.zydm.base.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.zydm.base.R;

/**
 * Created by jia on 15-11-19.
 */
public class RatioLayout extends RelativeLayout {

    private static final String TAG = "RatioLayout";
    private float mWHRatio = 1f;
    private boolean mIsBaseH = false;
    private boolean mIsLockRatio = true;
    private boolean mCanUnLockRatio = false;
    private boolean mIsIgnorePadding = false;

    public RatioLayout(Context context) {
        super(context);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttr(context, attrs);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttr(context, attrs);
    }

    private void readAttr(Context context, AttributeSet attrs) {
        TypedArray typed = context.obtainStyledAttributes(attrs,
                R.styleable.RatioLayout);
        mWHRatio = typed.getFloat(R.styleable.RatioLayout_wh_ratio, 1f);
        mIsBaseH = typed.getBoolean(R.styleable.RatioLayout_is_base_h, false);
        mIsIgnorePadding = typed.getBoolean(R.styleable.RatioLayout_is_ignore_padding, false);
//        LogUtils.d(TAG, "whRatio:" + mWHRatio);
        typed.recycle();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!mIsLockRatio) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (mIsBaseH) {
            int parentHeight = getMeasuredHeight(heightMeasureSpec);
            if (parentHeight <= 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            int childHeight;
            int childWidth;
            int parentWidth;
            if (mIsIgnorePadding) {
                parentWidth = (int) (parentHeight * mWHRatio + 0.5f);
                childHeight = parentHeight - getPaddingBottom() - getPaddingTop();
                childWidth = parentWidth - getPaddingLeft() - getPaddingRight();
            } else {
                childHeight = parentHeight - getPaddingBottom() - getPaddingTop();
                childWidth = (int) (childHeight * mWHRatio + 0.5f);
                parentWidth = childHeight + getPaddingLeft() + getPaddingRight();
            }

            //测量子控件,固定孩子的大小
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
            //测量
            setMeasuredDimension(parentWidth, parentHeight);
            super.onMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {

            int parentWidth = getMeasuredWidth(widthMeasureSpec);
            if (parentWidth <= 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            int childWidth;
            int childHeight;
            int parentHeight;
            if (mIsIgnorePadding) {
                parentHeight = (int) (parentWidth / mWHRatio + 0.5f);
                childWidth = parentWidth - getPaddingLeft() - getPaddingRight();
                childHeight = parentHeight - getPaddingBottom() - getPaddingTop();
            } else {
                childWidth = parentWidth - getPaddingLeft() - getPaddingRight();
                childHeight = (int) (childWidth / mWHRatio + 0.5f);
                parentHeight = childHeight + getPaddingBottom() + getPaddingTop();
            }

            //测量子控件,固定孩子的大小
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
            //测量
            setMeasuredDimension(parentWidth, parentHeight);
            super.onMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    private int getMeasuredWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        return mode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(widthMeasureSpec) : getMeasuredWidth();
    }

    private int getMeasuredHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        return mode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(heightMeasureSpec) : getMeasuredHeight();
    }

    public void setWhRatio(float wh_ratio) {
        mIsLockRatio = true;
        mWHRatio = wh_ratio;
    }

    public float getWHRatio() {
        return mWHRatio;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (mCanUnLockRatio) {
            mIsLockRatio = false;
        }
        super.setLayoutParams(params);
    }

    public void setCanUnLockRatio(boolean isCanUnLockRatio) {
        this.mCanUnLockRatio = isCanUnLockRatio;
    }
}
