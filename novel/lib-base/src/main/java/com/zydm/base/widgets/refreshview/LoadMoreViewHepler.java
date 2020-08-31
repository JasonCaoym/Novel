package com.zydm.base.widgets.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zydm.base.R;
import com.zydm.base.utils.StringUtils;

public class LoadMoreViewHepler {

    protected static final String TAG = "LoadMoreViewHepler";

    public static final int DEFAULT_LOAD_MORE_DIST = -80;
    private Context mContext;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ViewGroup mLoadMoreLayout;
    private TextView mLoadStateTextView;
    private PullTexts mPullTexts = new PullTexts();

    public LoadMoreViewHepler(Context context, PullToRefreshLayout pullToRefreshLayout) {
        mContext = context;
        mPullToRefreshLayout = pullToRefreshLayout;
        init();
    }

    private void init() {
        mLoadMoreLayout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.pull_load_more_layout, null);
        mPullToRefreshLayout.addView(mLoadMoreLayout);
        int loadMoreBgColor = mPullToRefreshLayout.getLoadMoreBgColor();
        if(loadMoreBgColor != 0) {
            mLoadMoreLayout.setBackgroundColor(loadMoreBgColor);
        }
        mLoadStateTextView = (TextView) mLoadMoreLayout.findViewById(R.id.load_state_tv);
//        mPullToRefreshLayout.mLoadMoreDist = -ViewUtils.getDimenPx(R.dimen.pull_foot_h);
        if(mPullToRefreshLayout.getBackground() instanceof ColorDrawable) {
            mLoadMoreLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void layout(int pullDist, int pullContentViewH) {
        mLoadMoreLayout.layout(0, pullDist + pullContentViewH, mLoadMoreLayout.getMeasuredWidth(), pullDist
                + pullContentViewH + mLoadMoreLayout.getMeasuredHeight());
    }

    public ViewGroup getView() {
        return mLoadMoreLayout;
    }

    protected int getMeasuredHeight() {
        if (null == mLoadMoreLayout) {
            return 0;
        }
        return mLoadMoreLayout.getMeasuredHeight();
    }

    protected int getMeasuredWidth() {
        if (null == mLoadMoreLayout) {
            return 0;
        }
        return mLoadMoreLayout.getMeasuredWidth();
    }

    public void loadMoreFinish(int refreshResult) {
        mLoadStateTextView.setText(mPullTexts.getResultText(refreshResult, false));
    }

    protected void onStateChanged(int state) {
        if (!mPullToRefreshLayout.isHasMoreData()) {
            mLoadStateTextView.setText(R.string.load_temporary_not_data);
            return;
        }
        String stateText = mPullTexts.getStateText(state, false);
        if (StringUtils.isBlank(stateText)) {
            return;
        }
        mLoadStateTextView.setText(stateText);
    }

    public void setPullTexts(PullTexts pullTexts) {
        if (pullTexts == null) {
            return;
        }
        this.mPullTexts = pullTexts;
    }
}
