package com.zydm.base.widgets.refreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import com.zydm.base.R;


public class PullableGridView extends GridView implements IPullable {

    private OnScrollListenerSet mOnScrollListenerSet;

    public PullableGridView(Context context) {
        super(context);
        initSetting();
    }

    public PullableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting();
    }

    public PullableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSetting();
    }

    private void initSetting() {
        setSelector(R.color.transparent);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
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
    }
}
