package com.zydm.base.widgets.refreshview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import com.duoyue.lib.base.widget.XFrameLayout;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;
import com.zydm.base.tools.TooFastChecker;
import com.zydm.base.utils.LogUtils;
import com.zydm.base.widgets.PromptLayoutHelper;

public class PullToRefreshLayout extends XFrameLayout {

    private static final int RESULT_SHOW_TIME = 500;

    protected static final String TAG = "PullToRefreshLayout";
    public static final int INIT = 0;
    public static final int RELEASE_TO_REFRESH = 1;
    public static final int REFRESHING = 2;
    //    public static final int RELEASE_TO_LOAD = 3;
    public static final int LOADING = 4;
    public static final int RESULT = 5;

    private int mState = INIT;
    private ValueAnimator mCurAnim;
    private boolean mIsNeedChangeActionDown;
    private int mPullDist; // 向下拉为正，下上拉为负
    private OnScrollListener mScrollListener;
    private OnRefreshListener mListener;
    // 刷新成功
    public static final int SUCCEED = 0;
    // 刷新失败
    public static final int FAIL = 1;
    public static final int FAIL_TEMPORARY_NOT_DATA = 2;//暂无数据
    // 释放刷新的距离
    protected int mRefreshDist = RefreshViewHepler.DEFAULT_REFRESH_DIST;
    // 释放加载的距离
//    protected int mLoadMoreDist = LoadMoreViewHepler.DEFAULT_LOAD_MORE_DIST;
    private boolean mIsInit = false;

    protected RefreshViewHepler mRefreshView;
    protected LoadMoreViewHepler mLoadMoreView;
    private IPullable mPullable;
    protected View mPullContentView;
    protected PromptLayoutHelper mPromptLayout;
    private boolean mCanPullDown = true;
    private boolean mCanPullUp = true;
    private boolean mCanAutoLoadMore = true;
    private PullTexts mPullTexts;
    private GestureDetector mGestureDetector;
    private TooFastChecker mLoadMoreCooler = new TooFastChecker(Constants.SECOND_20);
    private boolean mHasMoreData = true;
    private Boolean mIsInterceptTouch;
    private int mLoadMoreBgColor = -1;
    private int mRefreshViewBgColor = -1;
    private int mPromptBgColor = Color.WHITE;
    private OnInitLayoutListener mOnInitLayoutListener;
    private boolean mIsByBeforehand = false;

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public PullToRefreshLayout(Context context, View pullContentView, IPullable pullable) {
        super(context);
        init(context);
        mPullContentView = pullContentView;
        addView(mPullContentView);
        setPullable(pullable);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, mPullGestureListener);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public final PromptLayoutHelper getPromptLayoutHelper() {
        if (null == mPromptLayout) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mPromptLayout = new PromptLayoutHelper((Activity) this.getContext());
            mPromptLayout.getPromptLayout().setLayoutParams(params);
            mPromptLayout.getPromptLayout().setBackgroundColor(mPromptBgColor);
            if (mIsInit) {
                this.addView(mPromptLayout.getPromptLayout());
            }
        }
        return mPromptLayout;
    }

    public void setPromptLayout(PromptLayoutHelper promptLayout) {
        mPromptLayout = promptLayout;
    }

    public View getPullContentView() {
        return mPullContentView;
    }

    public void setCanPullDown(boolean canPullDown) {
        this.mCanPullDown = canPullDown;
    }

    public void setCanPullUp(boolean canPullUp) {
        this.mCanPullUp = canPullUp;
    }

    @Override
    protected final void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mIsInit && this.getChildCount() > 0) {
            this.mPullContentView = getChildAt(0);
            IPullable pullable = null;
            if (mPullContentView instanceof IPullable) {
                pullable = (IPullable) mPullContentView;
            }
            setPullable(pullable);
            onInitLayout();
        }
        if (null != mPullContentView) {
            mRefreshView.layout(0);
            int pullContentViewH = mPullContentView.getMeasuredHeight();
            mLoadMoreView.layout(0, pullContentViewH);
        }
    }

    public void setOnInitLayoutListener(OnInitLayoutListener onInitLayoutListener) {
        this.mOnInitLayoutListener = onInitLayoutListener;
    }

    protected void onInitLayout() {
        if (mOnInitLayoutListener != null) {
            mOnInitLayoutListener.onInitLayout(this);
        }
    }

    public boolean isByBeforehand() {
        return mIsByBeforehand;
    }

    public interface OnInitLayoutListener {
        void onInitLayout(PullToRefreshLayout pullToRefreshLayout);
    }

    private void setPullable(IPullable pullable) {
        if (mIsInit) {
            return;
        }
        if (null == pullable) {
            throw new RuntimeException("pullable is null!");
        }
        this.mIsInit = true;
        this.mPullable = pullable;
        mPullable.setPullToRefreshLayout(this);
        mRefreshView = new RefreshViewHepler(getContext(), this);
        mLoadMoreView = new LoadMoreViewHepler(getContext(), this);
        mRefreshView.setPullTexts(mPullTexts);
        mLoadMoreView.setPullTexts(mPullTexts);
        if (mRefreshViewBgColor != -1) {
            mRefreshView.getView().setBackgroundColor(mRefreshViewBgColor);
        }
        if (mLoadMoreBgColor != -1) {
            mLoadMoreView.getView().setBackgroundColor(mLoadMoreBgColor);
        }
        if (null != mPromptLayout && mPromptLayout.getPromptLayout().getParent() == null) {
            removeView(mPromptLayout.getPromptLayout());
            this.addView(mPromptLayout.getPromptLayout());
        }
    }

    public void setPullTexts(PullTexts pullTexts) {
        this.mPullTexts = pullTexts;
        if (mRefreshView != null) {
            mRefreshView.setPullTexts(mPullTexts);
        }
        if (mLoadMoreView != null) {
            mLoadMoreView.setPullTexts(mPullTexts);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mIsInterceptTouch = null;
        }
        if (mIsInterceptTouch != null && !mIsInterceptTouch) {
            return super.dispatchTouchEvent(event);
        }

        boolean isActionUp = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsNeedChangeActionDown = false;
                stopAnim();
                break;
            case MotionEvent.ACTION_UP:
                isActionUp = true;
            case MotionEvent.ACTION_CANCEL:
                if (onTouchActionUp(isActionUp)) {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
        }

        mGestureDetector.onTouchEvent(event);
        if (mPullGestureListener.isOnScrollReturn()) {
            mIsNeedChangeActionDown = true;
//            LogUtils.d(TAG, "dispatchTouchEvent  self:" + event.getAction());
            return true;
        } else if (mIsNeedChangeActionDown) {
//            LogUtils.d(TAG, "dispatchTouchEvent  set down:" + event.getAction());
            mIsNeedChangeActionDown = false;
            event.setAction(MotionEvent.ACTION_DOWN);
        }
//        LogUtils.d(TAG, "dispatchTouchEvent  super:" + event.getAction());
        super.dispatchTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    private boolean onTouchActionUp(boolean isTriggerListener) {
        if (mPullDist == 0) {
            return false;
        }
        if (isTriggerListener && mListener != null) {
            if (RELEASE_TO_REFRESH == mState) {
                changeState(REFRESHING);
                mIsByBeforehand = false;
                mListener.onRefresh(this);
            }
        }
        int targetDist = 0;
        if (mPullDist >= mRefreshDist && REFRESHING == mState) {
            targetDist = mRefreshDist;
        }
        startAnim(targetDist);
        return true;
    }

    private PullOnGestureListener mPullGestureListener = new PullOnGestureListener() {

        @Override
        public boolean onScroll2(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            LogUtils.d(TAG, "distanceY:" +distanceY + " mPullDist:" +mPullDist + " isCanPullUp():" +isCanPullUp() + " isCanPullDown():" +isCanPullDown());
            if (mScrollListener != null) {
                mScrollListener.onScroll(distanceX, distanceY);
            }
            if (mIsInterceptTouch == null) {
                mIsInterceptTouch = Math.abs(distanceX) < Math.abs(distanceY);
                if (!mIsInterceptTouch) {
                    return false;
                }
            }
            if (mPullDist == 0) {
                if ((distanceY > 0 && !isCanPullUp()) || (distanceY < 0 && !isCanPullDown())) {
                    return false;
                } else if (distanceY == 0f) {
                    return false;
                }
            }
            if (distanceY == 0f) {
                return true;
            }
            int targetPullDist = calculateTargetPullDist(distanceY);
            if (isCrossZero(targetPullDist)) {
                changePullDist(0);
                checkStateOnTouch();
                return false;
            }
            changePullDist(targetPullDist);
            checkStateOnTouch();
            return true;
        }

        private int calculateTargetPullDist(float distanceY) {
            int baseRatio = Math.abs(mPullDist) <= mRefreshDist ? 2 : 3;
            float pullRatio = (float) (baseRatio + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * Math.abs(mPullDist)));
            int curAddDist = (int) -(distanceY / pullRatio);
            return mPullDist + curAddDist;
        }

        private boolean isCrossZero(int targetPullDist) {
            int sumAbs = Math.abs(targetPullDist + mPullDist);
            int absSum = Math.abs(targetPullDist) + Math.abs(mPullDist);
            return sumAbs < absSum;
        }
    };

    private boolean isCanPullDown() {
        return mCanPullDown && mPullable.isReadyForPullDown() && LOADING != mState;
    }

    private boolean isCanPullUp() {
        return mCanPullUp && mPullable.isReadyForPullUp() && REFRESHING != mState;
    }

    private void checkStateOnTouch() {
        if (mPullDist >= mRefreshDist) {
            if (INIT == mState) {
                changeState(RELEASE_TO_REFRESH);
            }
        } else if (RELEASE_TO_REFRESH == mState) {
            changeState(INIT);
        } else if (mPullDist < 0) {
            if (INIT == mState) {
                changeState(LOADING);
                mLoadMoreCooler.startTime();
                if (mListener != null) {
                    mIsByBeforehand = false;
                    mListener.onLoadMore(this);
                }
            }
        }
    }

    protected boolean isHasMoreData() {
        return mHasMoreData;
    }

    public void refreshFinish(int refreshResult) {
        if (!mIsInit) {
            return;
        }
        if (refreshResult == SUCCEED) {
            mHasMoreData = true;
        }
        LogUtils.d(TAG, "refreshFinish for loadMore:" + refreshResult + " " + this);
        if (INIT == mState) {
            return;
        }
        mRefreshView.refreshFinish(refreshResult);
        changeState(RESULT);
        postDelayedAnim(0, RESULT_SHOW_TIME);
    }

    public void loadMoreFinish(int refreshResult) {
        if (!mIsInit) {
            return;
        }
        if (refreshResult == SUCCEED) {
            mLoadMoreCooler.cancelDelay(Constants.MILLIS_300);
            mHasMoreData = true;
        } else if (refreshResult == FAIL_TEMPORARY_NOT_DATA) {
            mHasMoreData = false;
        }
        LogUtils.d(TAG, "loadMoreFinish:" + refreshResult + " " + this);
        if (mPullDist < 0) {
            mLoadMoreView.loadMoreFinish(refreshResult);
            changeState(RESULT);
            // 刷新结果停留1秒
            postDelayedAnim(0, RESULT_SHOW_TIME);
        } else if (mPullDist == 0) {
            changeState(INIT);
        }
    }

    public void setHasMoreData(boolean hasMoreData) {
        mHasMoreData = hasMoreData;
    }

    public void setLoadMoreViewBgColor(int color) {
        mLoadMoreBgColor = color;
        if (null == mLoadMoreView) {
            //mIsInit = false，此时不会立即生效
            return;
        }
        View loadMoreView = mLoadMoreView.getView();
        if (null == loadMoreView) {
            return;
        }
        loadMoreView.setBackgroundColor(color);
    }

    public void setRefreshViewBgColor(int color) {
        mRefreshViewBgColor = color;
        if (null == mRefreshView) {
            //mIsInit = false，此时不会立即生效
            return;
        }
        View refreshView = mRefreshView.getView();
        if (null == refreshView) {
            return;
        }
        refreshView.setBackgroundColor(color);
    }

    public void setPromtBgColor(int color) {
        mPromptBgColor = color;
        if (null == mPromptLayout) {
            //mIsInit = false，此时不会立即生效
            return;
        }
        View promptLayout = mPromptLayout.getPromptLayout();
        if (null == promptLayout) {
            return;
        }
        promptLayout.setBackgroundColor(color);
    }

    public int getRefreshViewBgColor() {
        return mRefreshViewBgColor;
    }

    public int getLoadMoreBgColor() {
        return mLoadMoreBgColor;
    }

    public void showRefreshing() {
        if (REFRESHING == mState || LOADING == mState) {
            return;
        }
        changeState(REFRESHING);
        startAnim(mRefreshDist);
    }

    private void postDelayedAnim(final int targetDist, int delayMillis) {
        BaseApplication.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnim(targetDist);
            }
        }, delayMillis);
    }

    private void startAnim(final int targetDist) {
        stopAnim();
        mCurAnim = ValueAnimator.ofInt(mPullDist, targetDist);
        int duration = (int) (Math.abs(mPullDist - targetDist) * 2.1);
        duration = Math.min(duration, Constants.SECOND_2);
        mCurAnim.setDuration(duration);
        mCurAnim.setInterpolator(new DecelerateInterpolator());
        mCurAnim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changePullDist((int) animation.getAnimatedValue());
            }
        });
        mCurAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (0 == mPullDist && mState != REFRESHING && mState != LOADING) {
                    changeState(INIT);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        mCurAnim.start();
    }

    private void stopAnim() {
        if (mCurAnim == null || !mCurAnim.isRunning()) {
            return;
        }
        mCurAnim.cancel();
    }

    private void changePullDist(int pullDist) {
        if (!isChangePullDist(mPullDist, pullDist)) {
            return;
        }
        mPullDist = pullDist;
        onPullDistChange(mPullDist);
    }

    protected boolean isChangePullDist(int oldPullDist, int newPullDist) {
        return oldPullDist != newPullDist;
    }

    private void changeState(int targetState) {
        if (mState == targetState) {
            return;
        }
        LogUtils.d(TAG, "changeState state= " + targetState + " mPullDist:" + mPullDist);
        mState = targetState;
        mRefreshView.onStateChanged(mState);
        mLoadMoreView.onStateChanged(mState);
    }

    protected void onPullDistChange(int pullDist) {
        movePrompt(pullDist);
        moveContentView(pullDist);
        moveRefreshView(pullDist);
        moveLoadMoreView(pullDist);
    }

    protected void onPullDist(int pullDist) {
        movePrompt(pullDist);
        moveRefreshView(pullDist);
        moveLoadMoreView(pullDist);
    }

    private void movePrompt(int pullDist) {
        if (mPromptLayout != null) {
            ViewGroup promptLayout = mPromptLayout.getPromptLayout();
            ViewCompat.setY(promptLayout, promptLayout.getTop() + pullDist);
        }
    }

    protected void moveContentView(int pullDist) {
        ViewCompat.setY(mPullContentView, mPullContentView.getTop() + pullDist);
    }

    protected void moveRefreshView(int pullDist) {
        if (pullDist < 0) {
            return;
        }
        View headView = mRefreshView.getView();
        ViewCompat.setY(headView, headView.getTop() + pullDist);
    }

    protected void moveLoadMoreView(int pullDist) {
        if (pullDist > 0) {
            return;
        }
        ViewGroup footView = mLoadMoreView.getView();
        ViewCompat.setY(footView, footView.getTop() + pullDist);
    }

    public void setCanAutoLoadMore(boolean canAutoLoadMore) {
        mCanAutoLoadMore = canAutoLoadMore;
    }

    protected boolean autoLoadMore() {
        if (!mCanAutoLoadMore) {
            return false;
        }
        if (!mCanPullUp || !isHasMoreData() || mState != INIT) {
            return false;
        }
        if (mListener == null || mLoadMoreCooler.isTooFast()) {
            return false;
        }
        LogUtils.d(TAG, "------onAutoLoadMore do----" + this);
        changeState(LOADING);
        mIsByBeforehand = true;
        mListener.onLoadMore(this);
        return true;
    }

    public int getPullState() {
        return mState;
    }

    /**
     * 滑动监听.
     */
    public interface OnScrollListener {
        /**
         * 滑动.
         *
         * @param distanceX
         * @param distanceY
         */
        void onScroll(float distanceX, float distanceY);
    }

    public interface OnRefreshListener {
        /**
         * 刷新操作
         */
        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

        /**
         * 加载操作
         */
        void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
    }
}
