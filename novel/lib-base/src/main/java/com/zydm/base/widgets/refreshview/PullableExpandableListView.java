package com.zydm.base.widgets.refreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import com.zydm.base.R;

public class PullableExpandableListView extends ExpandableListView implements IPullable{

    private static final String TAG = "PullableExpandableListView";

    private OnScrollListenerSet mOnScrollListenerSet;
    private PullToRefreshLayout mPullToRefreshLayout;

    private OnScrollListener mOnScrollListenerForLoad = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
//            if (customScrollListener != null) {
//                customScrollListener.onScrollStateChanged(view, scrollState);
//            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            if (customScrollListener != null) {
//                customScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//            }
            if (mPullToRefreshLayout == null) {
                return;
            }
            if (firstVisibleItem > 0 && firstVisibleItem + visibleItemCount >= totalItemCount - 4) {
//                LogUtils.d(TAG, "onScroll LoadMore firstVisibleItem:" +firstVisibleItem+ " visibleItemCount:" + visibleItemCount + " totalItemCount:" +totalItemCount);
                mPullToRefreshLayout.autoLoadMore();
            }
        }
    };


    public PullableExpandableListView(Context context) {
        super(context);
        initSetting();
    }

    public PullableExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting();
    }

    public PullableExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetting();
    }

    private void initSetting() {
        setSelector(R.color.transparent);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setOnScrollListener(mOnScrollListenerForLoad);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        if (null == mOnScrollListenerSet) {
            mOnScrollListenerSet = new OnScrollListenerSet();
            super.setOnScrollListener(mOnScrollListenerSet);
        }
        mOnScrollListenerSet.add(listener);
    }

    @Override
    public boolean isReadyForPullDown() {
        return PullableUtils.isReadyForPullDown(this);
    }

    @Override
    public boolean isReadyForPullUp() {
        return PullableUtils.isReadyForPullUp(this);
    }

    @Override
    public void setPullToRefreshLayout(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshLayout = pullToRefreshLayout;
    }
}
