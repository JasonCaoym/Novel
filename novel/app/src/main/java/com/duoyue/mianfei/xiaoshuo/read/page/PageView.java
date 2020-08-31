package com.duoyue.mianfei.xiaoshuo.read.page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.read.page.animation.*;
import com.zydm.base.tools.TooFastChecker;

import static com.duoyue.mianfei.xiaoshuo.read.page.PageMode.COVER;

public class PageView extends FrameLayout {

    private int mViewWidth = 0;
    private int mViewHeight = 0;

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;
    private int mBgColor = Color.WHITE;
    private boolean canTouch = true;
    private boolean isPrepare = false;
    private RectF mCenterRectForMenu = null;
    private Rect mRuleRect = null;
    private RectF mVideoRect = null;
    private PageMode mPageMode;
    private PageSwitcher mPageAnim;
    private TooFastChecker tooFastChecker = new TooFastChecker(300);

    private PageSwitcher.OnPageChangeListener mPageAnimListener = new PageSwitcher.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return PageView.this.hasPrev();
        }

        @Override
        public boolean hasNext() {
            return PageView.this.hasNext();
        }

        @Override
        public void pageCancel() {
            mTouchListener.cancel();
            mPageLoader.pageCancel();
        }

        @Override
        public void drawExtra(Canvas canvas) {
            PageView.super.dispatchDraw(canvas);
        }

        @Override
        public void actionUp() {
//            mPageLoader.checkShowInteractionAd();
            mPageLoader.moveActionUp();
        }

        @Override
        public void onMoveing() {
            mPageLoader.onMoving();
        }
    };

    private TouchListener mTouchListener;

    private PageLoader mPageLoader;
    private FrameLayout mExtraRoot;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mExtraRoot = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mExtraRoot.setLayoutParams(params);
        addView(mExtraRoot, params);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("ggg", w + "..." + h + "..." + oldw + "..." + oldh);
        mViewWidth = w;
        mViewHeight = h;
        initSwitchAnim();
        if (mPageLoader != null) {
            mPageLoader.setDisplaySize(w, h);
        }
        isPrepare = true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        canvas.drawColor(mBgColor);
        mPageAnim.draw(canvas);
    }

    public void initSwitchAnim() {
        if (mViewWidth == 0 || mViewHeight == 0) {
            return;
        }
        mPageMode = COVER;
        mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
    }

    //设置翻页的模式
    public void setPageMode(PageMode pageMode) {
        mPageMode = pageMode;
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return;

        /*switch (mPageMode) {
//            case SIMULATION:
//                mPageAnim = new SimulationAnimation(mViewWidth, mViewHeight, this, mPageAnimListener);
//                break;
            case COVER:
                mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
//            case SLIDE:
//                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
//                break;
//            case NONE:
//                mPageAnim = new NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
//                break;
            case SCROLL:
                mPageAnim = new ScrollPageAnim(mViewWidth, mViewHeight, 0, mPageLoader.getMarginHeight(),
                        this, mPageAnimListener);
                break;
            default:
                mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
        }*/
    }

    public Layer getNextPage() {
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    public Layer getBgBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getBgBitmap();
    }

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private boolean hasPrevPage() {
        mTouchListener.prePage();
        return mPageLoader.pre();
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private boolean hasNextPage() {
        mTouchListener.nextPage();
        return mPageLoader.next();
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    public void abortAnimation() {
        mPageAnim.abortAnim();
    }

    public boolean autoPrevPage() {
        //滚动暂时不支持自动翻页
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageSwitcher.Direction.PRE);
            return true;
        }
    }

    public boolean autoNextPage() {
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageSwitcher.Direction.NEXT);
            return true;
        }
    }

    private void startPageAnim(PageSwitcher.Direction direction) {
        if (mTouchListener == null) return;
        //是否正在执行动画
        abortAnimation();
        if (direction == PageSwitcher.Direction.NEXT) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            //设置方向
            Boolean hasNext = hasNextPage();

            mPageAnim.setDirection(direction);
            if (!hasNext) {
                return;
            }
        } else {
            int x = 0;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            mPageAnim.setDirection(direction);
            //设置方向方向
            Boolean hashPrev = hasPrevPage();
            if (!hashPrev) {
                return;
            }
        }
        mPageAnim.startAnim();
        this.postInvalidate();
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    /**
     * 翻页 下一页
     */
    public void turnNextPage() {
        if(mPageAnim instanceof CoverPageAnim){
            ((CoverPageAnim)mPageAnim).nextPage();
        }
    }

    /**
     * 翻页 上一页
     */
    public void turnPrePage() {
        if(mPageAnim instanceof CoverPageAnim){
            ((CoverPageAnim)mPageAnim).prePage();
        }
    }

    /**
     * 判断是否使用当前页的页码来显示
     * @return
     */
    public boolean isCurPosition() {
        if (mPageAnim != null && mPageMode == PageMode.SCROLL) {
            return ((ScrollPageAnim)mPageAnim).isCurPosition();
        } else {
            return true;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (tooFastChecker.isTooFast() && event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        if (!canTouch && event.getAction() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                isMove = false;
                canTouch = mTouchListener.onTouch();
                mPageAnim.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }
                if (isMove) {
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                x = (int) event.getX();
                y = (int) event.getY();
                if (!isMove) {
                    mVideoRect = mPageLoader.getVideoRectF();
                    int offset = 0;
//                    if (mPageMode == PageMode.SCROLL) {
//                        offset = ((ScrollPageAnim)mPageAnim).getMoveOffset();
//                    }
//                    Logger.e("ad#point", "mVideoRect = " + mVideoRect + ", 匹配的点是（" + x + ", " + (y - offset) + ")");
                    if (mVideoRect != null && mVideoRect.contains(x, y - offset)
                            && (mPageAnim.getBgBitmap().isExtraChapterEnd || mPageAnim.getNextBitmap().isExtraChapterEnd)) {
                        if (mTouchListener != null) {
                            mTouchListener.showVideoDialog();
                        }
                        return true;
                    }

                    mRuleRect = mPageLoader.getRuleRect();
//                    Logger.e("ad#point", "mRule = " + mRuleRect);
                    if (mRuleRect != null && mRuleRect.contains(x, y - offset)
                            && (mPageAnim.getBgBitmap().isExtraChapterEnd || mPageAnim.getNextBitmap().isExtraChapterEnd)) {
                        if (mTouchListener != null) {
                            mTouchListener.showRuleDialog();
                        }
                        return true;
                    }

                    if (mCenterRectForMenu == null) {
                        mCenterRectForMenu = new RectF(mViewWidth * 3 / 10, 0,
                                mViewWidth * 7 / 10, mViewHeight);
                    }

                    if (mCenterRectForMenu.contains(x, y)) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        return true;
                    }
                }
                mPageAnim.onTouchEvent(event);
                this.postInvalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Layer layer = mPageAnim.getTopBitmap();
        if (!layer.isExtraAfterBook && !layer.isExtraAfterChapter && !layer.isExtraChapterEnd) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean hasNext() {
        Boolean hasNext = false;
        if (mTouchListener != null) {
            hasNext = mTouchListener.nextPage();
            if (hasNext) {
                hasNext = mPageLoader.next();
            }
        }
        return hasNext;
    }

    private boolean hasPrev() {
        Boolean hasPrev = false;
        if (mTouchListener != null) {
            hasPrev = mTouchListener.prePage();
            if (hasPrev) {
                hasPrev = mPageLoader.pre();
            }
        }
        return hasPrev;
    }

    /**
     * 防止由于界面刷新导致自动进入书籍末的推荐页面
     * @return
     */
    public boolean isCanStart() {
        if (mPageAnim != null && mPageAnim instanceof ScrollPageAnim) {
            return ((ScrollPageAnim) mPageAnim).isCanStart();
        }
        return true;
    }

    @Override
    public void computeScroll() {
        mPageAnim.scrollAnim();
        super.computeScroll();
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    public boolean isRunning() {
        return !mPageAnim.isRunning();
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public void drawNextPage() {
        if (mPageAnim instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAnim).changePage();
        }
        // -1表示图片  判断是否是图片加载方式不一样
        mPageLoader.onDraw(getNextPage(), false, (mBgColor == -1 || mBgColor == -2));
    }

    public void refreshPage() {
        drawCurPage(false);
    }

    public void drawCurPage(boolean isUpdate) {
        if (mPageLoader != null) {
            /*if (!isUpdate){
                if (mPageAnim instanceof ScrollPageAnim) {
                    ((ScrollPageAnim) mPageAnim).resetBitmap();
                }
            }*/
            mPageLoader.onDraw(getNextPage(), isUpdate, (mBgColor == -1 || mBgColor == -2));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPageAnim != null) {
            mPageAnim.abortAnim();
        }

        mPageLoader = null;
        mPageAnim = null;
    }

    public PageLoader getPageLoader(FragmentActivity activity, String prevPageId, String sourceStats, String bookId) {
        if (mPageLoader == null) {
            mPageLoader = new PageLoader(activity, this, prevPageId, sourceStats,bookId);
        }
        return mPageLoader;
    }

    public interface TouchListener {
        void center();

        boolean onTouch();

        boolean prePage();

        boolean nextPage();

        void cancel();

        void showRuleDialog();

        void showVideoDialog();
    }
}
