package com.duoyue.mianfei.xiaoshuo.read.page.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;

public abstract class HorizonPageAnim extends PageSwitcher {

    protected Layer mCurLayer;
    protected Layer mNextLayer;
    protected Layer mTopLayer;
    protected boolean mIsCancel = false;
    private int mMoveX = 0;
    private int mMoveY = 0;
    private boolean isMove = false;
    private boolean isNext = false;
    private boolean noNext = false;
    private boolean isRunning = false;

    public HorizonPageAnim(int w, int h, ViewGroup view, OnPageChangeListener listener) {
        this(w, h, 0, 0, view, listener);
    }

    public HorizonPageAnim(int w, int h, int marginWidth, int marginHeight,
                           ViewGroup view, OnPageChangeListener listener) {
        super(w, h, marginWidth, marginHeight, view, listener);
        mCurLayer = new Layer();
        mNextLayer = new Layer();
        FrameLayout layout = (FrameLayout) view.getChildAt(0);
        mCurLayer.rootLayoutForExtra = layout;
        mNextLayer.rootLayoutForExtra = layout;
        mCurLayer.bitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
        mTopLayer = mCurLayer;
        mNextLayer.bitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.RGB_565);
    }

    public void changePage() {
        Layer layer = mCurLayer;
        mCurLayer = mNextLayer;
        mNextLayer = layer;
    }

    public abstract void drawStatic(Canvas canvas);

    public abstract void drawMove(Canvas canvas);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        setTouchPoint(x, y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMoveX = 0;
                mMoveY = 0;
                isMove = false;
                noNext = false;
                isNext = false;
                isRunning = false;
                mIsCancel = false;
                setStartPoint(x, y);
                abortAnim();
                break;
            case MotionEvent.ACTION_MOVE:
                final int slop = ViewConfiguration.get(mView.getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - x) > slop || Math.abs(mStartY - y) > slop;
                }
                if (isMove) {
                    mListener.onMoveing();
                    if (mMoveX == 0 && mMoveY == 0) {
                        if (x - mStartX > 0) {
                            isNext = false;
                            boolean hasPrev = mListener.hasPrev();
                            setDirection(Direction.PRE);
                            if (!hasPrev) {
                                noNext = true;
                                return true;
                            }
                        } else {
                            isNext = true;
                            boolean hasNext = mListener.hasNext();
                            setDirection(Direction.NEXT);
                            if (!hasNext) {
                                noNext = true;
                                return true;
                            }
                        }
                    } else {
                        if (isNext) {
                            mIsCancel = x - mMoveX > 0;
                        } else {
                            mIsCancel = x - mMoveX < 0;
                        }
                    }

                    mMoveX = x;
                    mMoveY = y;
                    isRunning = true;
                    mView.invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    isNext = x >= mScreenWidth / 2;
                    if (isNext) {
                        boolean hasNext = mListener.hasNext();
                        setDirection(Direction.NEXT);
                        if (!hasNext) {
                            return true;
                        }
                    } else {
                        boolean hasPrev = mListener.hasPrev();
                        setDirection(Direction.PRE);
                        if (!hasPrev) {
                            return true;
                        }
                    }
                }

                if (mIsCancel) {
                    mListener.pageCancel();
                }
                mListener.actionUp();

                if (!noNext) {
                    isRunning = true;
                    startAnim();
                    mView.invalidate();
                }

                break;
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isRunning) {
            drawMove(canvas);
        } else {
            if (mIsCancel) {
                mNextLayer.bitmap = mCurLayer.bitmap.copy(Bitmap.Config.RGB_565, true);
                mNextLayer.isExtraChapterEnd = mCurLayer.isExtraChapterEnd;
                mNextLayer.isExtraAfterChapter = mCurLayer.isExtraAfterChapter;
                mNextLayer.isExtraAfterBook = mCurLayer.isExtraAfterBook;
            }
            drawStatic(canvas);
        }
    }

    @Override
    public void scrollAnim() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();

            setTouchPoint(x, y);

            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) {
                isRunning = false;
            }
            mView.postInvalidate();
        }
    }

    @Override
    public void abortAnim() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            isRunning = false;
            setTouchPoint(mScroller.getFinalX(), mScroller.getFinalY());
            mView.postInvalidate();
        }
    }

    @Override
    public Layer getBgBitmap() {
        return mNextLayer;
    }

    @Override
    public Layer getNextBitmap() {
        return mNextLayer;
    }

    @Override
    public Layer getTopBitmap() {
        return mTopLayer;
    }

    /**
     * 下一页翻页
     */
    public void nextPage(){
        int x = mViewWidth - mViewWidth / 6;
        int y = mViewHeight / 2;
        setMouseClick(x, y);
    }

    /**
     * 上一页翻页
     */
    public void prePage(){
        int x = mViewWidth / 6;
        int y = mViewHeight / 2;
        setMouseClick(x, y);
    }

    public void setMouseClick(int x, int y){
        MotionEvent evenDownt = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0);
        onTouchEvent(evenDownt);
        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, x, y, 0);
        onTouchEvent(eventUp);
        evenDownt.recycle();
        eventUp.recycle();
    }
}
