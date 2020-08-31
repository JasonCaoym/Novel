package com.duoyue.mianfei.xiaoshuo.read.page.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.ViewGroup;

/**
 */

public class NonePageAnim extends HorizonPageAnim{

    public NonePageAnim(int w, int h, ViewGroup view, OnPageChangeListener listener) {
        super(w, h, view, listener);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (mIsCancel) {
            mNextLayer.isExtraAfterChapter = mCurLayer.isExtraAfterChapter;
            mNextLayer.bitmap = mCurLayer.bitmap.copy(Bitmap.Config.RGB_565, true);
            canvas.drawBitmap(mCurLayer.bitmap, 0, 0, null);
            if (mCurLayer.isExtraAfterChapter || mCurLayer.isExtraAfterBook) {
                mCurLayer.rootLayoutForExtra.setTranslationX(0);
                mListener.drawExtra(canvas);
            }
            mTopLayer = mCurLayer;
        } else {
            canvas.drawBitmap(mNextLayer.bitmap, 0, 0, null);
            if (mNextLayer.isExtraAfterChapter || mNextLayer.isExtraAfterBook) {
                mNextLayer.rootLayoutForExtra.setTranslationX(0);
                mListener.drawExtra(canvas);
            }
            mTopLayer = mNextLayer;
        }
    }

    @Override
    public void drawMove(Canvas canvas) {
        if (mIsCancel) {
            mNextLayer.isExtraAfterChapter = mCurLayer.isExtraAfterChapter;
            mNextLayer.bitmap = mCurLayer.bitmap.copy(Bitmap.Config.RGB_565, true);
            canvas.drawBitmap(mCurLayer.bitmap, 0, 0, null);
            if (mCurLayer.isExtraAfterChapter || mCurLayer.isExtraAfterBook) {
                mCurLayer.rootLayoutForExtra.setTranslationX(0);
                mListener.drawExtra(canvas);
            }
            mTopLayer = mCurLayer;
        } else {
            canvas.drawBitmap(mNextLayer.bitmap, 0, 0, null);
            if (mNextLayer.isExtraAfterChapter || mNextLayer.isExtraAfterBook) {
                mNextLayer.rootLayoutForExtra.setTranslationX(0);
                mListener.drawExtra(canvas);
            }
            mTopLayer = mNextLayer;
        }
    }

    @Override
    public void startAnim() {
    }
}
