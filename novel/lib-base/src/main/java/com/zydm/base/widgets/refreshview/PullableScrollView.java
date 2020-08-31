package com.zydm.base.widgets.refreshview;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

public class PullableScrollView extends NestedScrollView implements IPullable{

    public PullableScrollView(Context context) {
        super(context);
    }

    public PullableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isReadyForPullDown() {
        return getScrollY() == 0;
    }

    @Override
    public boolean isReadyForPullUp() {
        return getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight());
    }

    @Override
    public void setPullToRefreshLayout(PullToRefreshLayout pullToRefreshLayout) {

    }
}
