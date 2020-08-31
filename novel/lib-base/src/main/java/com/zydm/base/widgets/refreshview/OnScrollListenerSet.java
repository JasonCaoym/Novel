package com.zydm.base.widgets.refreshview;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import java.util.HashSet;

class OnScrollListenerSet implements OnScrollListener {

    private HashSet<OnScrollListener> mScrollListenerList = new HashSet<OnScrollListener>();
    
    public void add(OnScrollListener listener) {
        mScrollListenerList.add(listener);
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        for (OnScrollListener onScrollListener : mScrollListenerList) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        for (OnScrollListener onScrollListener : mScrollListenerList) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
