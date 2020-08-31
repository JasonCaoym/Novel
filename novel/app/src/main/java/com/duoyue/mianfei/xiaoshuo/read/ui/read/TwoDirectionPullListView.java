package com.duoyue.mianfei.xiaoshuo.read.ui.read;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.duoyue.mianfei.xiaoshuo.R;

public class TwoDirectionPullListView extends ListView {

    private View headerView;
    private int headerViewHeight;
    private OnRefreshingListener mOnRefreshingListener;
    private View footerView;
    private int footerViewHeight;
    private boolean isLoadingMoreBottom;
    private boolean isLoadingMoreTop;
    private boolean mIsLoading;

    public TwoDirectionPullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
        setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && getLastVisiblePosition() == getCount() - 1 && !isLoadingMoreBottom) {
                    if (mOnRefreshingListener.hasBottomMore()) {
                        isLoadingMoreBottom = true;
                        showFooterView();
                        setSelection(getCount() - 1);
                        if (mOnRefreshingListener != null) {
                            mOnRefreshingListener.onLoadMoreBottom();
                        }
                    } else {
                        if (mOnRefreshingListener != null) {
                            mOnRefreshingListener.noBottomMore();
                        }
                    }
                }

                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && getFirstVisiblePosition() == 0 && !isLoadingMoreTop) {
                    if (mOnRefreshingListener.hasTopMore()) {
                        isLoadingMoreTop = true;
                        showHeaderView();
                        setSelection(0);
                        if (mOnRefreshingListener != null) {
                            mOnRefreshingListener.onLoadMoreTop();
                        }
                    } else {
                        if (mOnRefreshingListener != null) {
                            mOnRefreshingListener.noTopMore();
                        }
                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.load_layout, null);
        headerView.measure(0, 0);
        headerViewHeight = headerView.getMeasuredHeight();
        hideHeaderView();
        addHeaderView(headerView);
    }

    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.load_layout, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        hideFooterView();
        addFooterView(footerView);
    }

    private void hideFooterView() {
        int paddingTop = -footerViewHeight;
        setFooterViewPaddingTop(paddingTop);
    }

    private void showFooterView() {
        int paddingTop = 0;
        setFooterViewPaddingTop(paddingTop);
    }

    private void setFooterViewPaddingTop(int paddingTop) {
        footerView.setPadding(0, paddingTop, 0, 0);
    }

    private void hideHeaderView() {
        int paddingTop = -headerViewHeight;
        setHeaderViewPaddingTop(paddingTop);
    }

    private void showHeaderView() {
        int paddingTop = 0;
        setHeaderViewPaddingTop(paddingTop);
    }

    private void setHeaderViewPaddingTop(int paddingTop) {
        headerView.setPadding(0, paddingTop, 0, 0);
    }

    public void setOnRefreshingListener(OnRefreshingListener mOnRefreshingListener) {
        this.mOnRefreshingListener = mOnRefreshingListener;
    }

    public interface OnRefreshingListener {

        void onLoadMoreBottom();

        void onLoadMoreTop();

        boolean hasTopMore();

        boolean hasBottomMore();

        void noBottomMore();

        void noTopMore();
    }

    public void onLoadMoreBottomComplete() {
        hideFooterView();
        isLoadingMoreBottom = false;
    }

    public void onLoadMoreTopComplete() {
        hideHeaderView();
        isLoadingMoreTop = false;
    }

    public boolean isLoading() {
        return isLoadingMoreTop || isLoadingMoreBottom || mIsLoading;
    }

    public void setIsLoad(boolean isLoading) {
        mIsLoading = isLoading;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isLoading()) {
            return true;
        }
        return super.onTouchEvent(ev);
    }
}