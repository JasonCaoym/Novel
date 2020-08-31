package com.duoyue.mianfei.xiaoshuo.read.page.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;

public class CoverPageAnim extends HorizonPageAnim {

    private Rect mSrcRect, mDestRect;
    private GradientDrawable mBackShadowDrawableLR;

    public CoverPageAnim(int w, int h, ViewGroup view, OnPageChangeListener listener) {
        super(w, h, view, listener);
        mSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mDestRect = new Rect(0, 0, mViewWidth, mViewHeight);
        int[] mBackShadowColors = new int[]{0x28000000, 0x00000000};
        mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (mIsCancel) {
            mNextLayer.isExtraAfterChapter = mCurLayer.isExtraAfterChapter;
            mNextLayer.isExtraChapterEnd = mCurLayer.isExtraChapterEnd;
            mNextLayer.offsetY = mCurLayer.offsetY;
            mNextLayer.isExtraAfterBook = mCurLayer.isExtraAfterBook;
            mNextLayer.bitmap = mCurLayer.bitmap.copy(Bitmap.Config.RGB_565, true);
            canvas.drawBitmap(mCurLayer.bitmap, 0, 0, null);
            if (mCurLayer.isExtraAfterChapter || mCurLayer.isExtraAfterBook || mCurLayer.isExtraChapterEnd) {
                if (mCurLayer.isExtraChapterEnd) {
                    mCurLayer.rootLayoutForExtra.setTranslationY(mCurLayer.offsetY);
                } else {
                    mCurLayer.rootLayoutForExtra.setTranslationY(0);
                }
                mCurLayer.rootLayoutForExtra.setTranslationX(0);
                mListener.drawExtra(canvas);
            }
            mTopLayer = mCurLayer;
        } else {
            canvas.drawBitmap(mNextLayer.bitmap, 0, 0, null);
            if (mNextLayer.isExtraAfterChapter || mNextLayer.isExtraAfterBook || mNextLayer.isExtraChapterEnd) {
                if (mNextLayer.isExtraChapterEnd) {
                    mNextLayer.rootLayoutForExtra.setTranslationY(mNextLayer.offsetY);
                } else {
                    mNextLayer.rootLayoutForExtra.setTranslationY(0);
                }
                mNextLayer.rootLayoutForExtra.setTranslationX(0);
                mListener.drawExtra(canvas);
            }
            mTopLayer = mNextLayer;
        }
    }

    @Override
    public void drawMove(Canvas canvas) {
        switch (mDirection) {
            case NEXT:
                int distance = (int) (mViewWidth - mStartX + mTouchX);
                if (distance > mViewWidth) {
                    distance = mViewWidth;
                }
                mSrcRect.left = mViewWidth - distance;
                mDestRect.right = distance;
                if (mCurLayer.isExtraAfterChapter || mCurLayer.isExtraAfterBook || mCurLayer.isExtraChapterEnd) {
                    mCurLayer.rootLayoutForExtra.setTranslationY(mCurLayer.offsetY);
                    mCurLayer.rootLayoutForExtra.setTranslationX(-mViewWidth + distance);
                    canvas.drawBitmap(mNextLayer.bitmap, 0, 0, null);
                    canvas.drawBitmap(mCurLayer.bitmap, mSrcRect, mDestRect, null);
                    mListener.drawExtra(canvas);
                } else if (mNextLayer.isExtraAfterChapter || mNextLayer.isExtraAfterBook || mNextLayer.isExtraChapterEnd) {
                    mNextLayer.rootLayoutForExtra.setTranslationY(mNextLayer.offsetY);
                    mNextLayer.rootLayoutForExtra.setTranslationX(0);
                    canvas.drawBitmap(mNextLayer.bitmap, 0, 0, null);
                    mListener.drawExtra(canvas);
                    canvas.drawBitmap(mCurLayer.bitmap, mSrcRect, mDestRect, null);
                } else {
                    canvas.drawBitmap(mNextLayer.bitmap, 0, 0, null);
                    canvas.drawBitmap(mCurLayer.bitmap, mSrcRect, mDestRect, null);
                }
                mTopLayer = mCurLayer;
                addShadow(distance, canvas);
                break;
            default:
                mSrcRect.left = (int) (mViewWidth - mTouchX);
                mDestRect.right = (int) mTouchX;
                if (mCurLayer.isExtraAfterChapter || mCurLayer.isExtraAfterBook || mCurLayer.isExtraChapterEnd) {
                    mCurLayer.rootLayoutForExtra.setTranslationY(mCurLayer.offsetY);
                    mCurLayer.rootLayoutForExtra.setTranslationX(0);
                    canvas.drawBitmap(mCurLayer.bitmap, 0, 0, null);
                    mListener.drawExtra(canvas);
                    canvas.drawBitmap(mNextLayer.bitmap, mSrcRect, mDestRect, null);
                } else if (mNextLayer.isExtraAfterChapter || mNextLayer.isExtraAfterBook || mNextLayer.isExtraChapterEnd) {
                    mNextLayer.rootLayoutForExtra.setTranslationY(mNextLayer.offsetY);
                    mNextLayer.rootLayoutForExtra.setTranslationX(-mViewWidth + mTouchX);
                    canvas.drawBitmap(mCurLayer.bitmap, 0, 0, null);
                    canvas.drawBitmap(mNextLayer.bitmap, mSrcRect, mDestRect, null);
                    mListener.drawExtra(canvas);
                } else {
                    canvas.drawBitmap(mCurLayer.bitmap, 0, 0, null);
                    canvas.drawBitmap(mNextLayer.bitmap, mSrcRect, mDestRect, null);
                }
                mTopLayer = mNextLayer;
                addShadow((int) mTouchX, canvas);
                break;
        }
    }

    private void addShadow(int left, Canvas canvas) {
        mBackShadowDrawableLR.setBounds(left, 0, left + 15, mScreenHeight);
        mBackShadowDrawableLR.draw(canvas);
    }

    @Override
    public void startAnim() {
        int dx;
        switch (mDirection) {
            case NEXT:
                if (mIsCancel) {
                    int dis = (int) ((mViewWidth - mStartX) + mTouchX);
                    if (dis > mViewWidth) {
                        dis = mViewWidth;
                    }
                    dx = mViewWidth - dis;
                } else {
                    dx = (int) -(mTouchX + (mViewWidth - mStartX));
                }
                break;
            default:
                if (mIsCancel) {
                    dx = (int) -mTouchX;
                } else {
                    dx = (int) (mViewWidth - mTouchX);
                }
                break;
        }

        int duration = (350 * Math.abs(dx)) / mViewWidth;
        mScroller.startScroll((int) mTouchX, 0, dx, 0, duration);
    }
}
