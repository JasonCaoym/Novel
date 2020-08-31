package com.duoyue.app.ui.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

public class BookDetailNestedScrollView extends NestedScrollView {

    private OnScrollListener listener;

    public void setOnScrollListener(OnScrollListener listener) {
        this.listener = listener;
    }

    public BookDetailNestedScrollView(Context context) {
        super(context);
    }

    public BookDetailNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookDetailNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnScrollListener {
        void onScroll(int scrollY);

        void onStopScroll();

        void onStartScroll();
    }

    //滑动中
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            listener.onScroll(t);
        }
    }

    //滑动开始
    @Override
    public boolean startNestedScroll(int axes, int type) {
        if (listener != null) {
            listener.onStartScroll();
        }
        return super.startNestedScroll(axes, type);

    }

    //滑动结束
    @Override
    public void stopNestedScroll() {
        super.stopNestedScroll();
        if (listener != null) {
            listener.onStopScroll();
        }

    }
}
