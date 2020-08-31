package com.zydm.base.widgets.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zydm.base.R;
import com.zydm.base.utils.StatusBarUtils;
import com.zydm.base.utils.ViewUtils;

public class GeneralPullZoomLayout extends PullToRefreshLayout{

    private int mBeginHeight;
    private boolean mZoomable = true;

    public GeneralPullZoomLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public GeneralPullZoomLayout(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
    }

    public GeneralPullZoomLayout(Context context, View pullContentView, IPullable pullable) {

        super(context, pullContentView, pullable);
    }

    @Override
    protected void onPullDistChange(int pullDist) {
        zoomHeaderOnPullLayout(pullDist);
    }

    private void zoomHeaderOnPullLayout(int pullDist) {
        if (pullDist <= 0) {
            mRefreshView.getView().setVisibility(INVISIBLE);
        } else {
            mRefreshView.getView().setVisibility(VISIBLE);
        }
        moveRefreshView(pullDist + StatusBarUtils.getStatusBarHeight(getContext()) + ViewUtils.dp2px(60));

        moveLoadMoreView(pullDist);

        if (!mZoomable || pullDist <= 0) {
            moveContentView(pullDist);
        }

        if (!mZoomable) {
            return;
        }

        if (!PullableUtils.isReadyForPullDown(mPullContentView)) {
            return;
        }
        View headerView = mPullContentView;
        if(mPullContentView instanceof ViewGroup) {
            headerView = ((ViewGroup) mPullContentView).getChildAt(0);
        }

        if (null == headerView) {
            return;
        }

        if (pullDist == 0 && headerView.getHeight() == mBeginHeight) {
            return;
        }

        ViewUtils.setViewHeight(headerView, mBeginHeight + pullDist);
    }

    @Override
    protected void onInitLayout() {

        super.onInitLayout();
        setRefreshViewBgColor(Color.TRANSPARENT);
        TextView headTextView = (TextView) findViewById(R.id.state_tv);
        headTextView.setTextColor(Color.WHITE);
        headTextView.setShadowLayer(4, 0, 0, ViewUtils.getColor(R.color.standard_text_color_gray));
    }

    public void setBeginHeight(int beginHeight) {
        mBeginHeight = beginHeight;
    }

    @Override
    protected boolean isChangePullDist(int oldPullDist, int newPullDist) {
        if (oldPullDist > mRefreshDist + 10 && oldPullDist < newPullDist) {
            return false;
        }
        return super.isChangePullDist(oldPullDist, newPullDist);
    }

    public void setZoomable(boolean zoomable) {
        mZoomable = zoomable;
    }

    public boolean isZoomable() {
        return mZoomable;
    }
}
