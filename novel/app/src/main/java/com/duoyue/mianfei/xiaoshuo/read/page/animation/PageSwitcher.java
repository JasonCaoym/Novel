package com.duoyue.mianfei.xiaoshuo.read.page.animation;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public abstract class PageSwitcher {

    ViewGroup mView;
    Scroller mScroller;
    OnPageChangeListener mListener;
    Direction mDirection = Direction.NONE;
    boolean isRunning = false;
    int mScreenWidth;
    int mScreenHeight;
    int mMarginWidth;
    int mMarginHeight;
    int mViewWidth;
    int mViewHeight;
    float mStartX;
    float mStartY;
    float mTouchX;
    float mTouchY;
    float mLastX;
    float mLastY;

    public PageSwitcher(int w, int h, int marginWidth, int marginHeight, ViewGroup view, OnPageChangeListener listener) {
        mScreenWidth = w;
        mScreenHeight = h;

        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        mViewWidth = mScreenWidth - mMarginWidth * 2;
        mViewHeight = mScreenHeight - mMarginHeight * 2;

        mView = view;
        mListener = listener;

        mScroller = new Scroller(mView.getContext(), new LinearInterpolator());
    }

    public void setStartPoint(float x, float y) {
        mStartX = x;
        mStartY = y;

        mLastX = mStartX;
        mLastY = mStartY;
    }

    public void setTouchPoint(float x, float y) {
        mLastX = mTouchX;
        mLastY = mTouchY;

        mTouchX = x;
        mTouchY = y;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    public Direction getDirection() {
        return mDirection;
    }

    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void draw(Canvas canvas);

    public abstract void startAnim();

    public abstract void scrollAnim();

    public abstract void abortAnim();

    public abstract Layer getBgBitmap();

    public abstract Layer getNextBitmap();

    public abstract Layer getTopBitmap();

    public enum Direction {
        NONE(true), NEXT(true), PRE(true), UP(false), DOWN(false);
        public final boolean isHorizontal;

        Direction(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }
    }

    public interface OnPageChangeListener {
        boolean hasPrev();

        boolean hasNext();

        void pageCancel();

        void drawExtra(Canvas canvas);

        void actionUp();

        void onMoveing();
    }

}
