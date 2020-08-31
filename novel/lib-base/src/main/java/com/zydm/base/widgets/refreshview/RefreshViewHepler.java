package com.zydm.base.widgets.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zydm.base.R;
import com.zydm.base.utils.StringUtils;
import com.zydm.base.utils.ViewUtils;

public class RefreshViewHepler {

    public static final int DEFAULT_REFRESH_DIST = 80;

    protected static final String TAG = "RefreshViewHepler";

    private Context mContext;
    private PullToRefreshLayout mPullToRefreshLayout;

    // 下拉头
    private ViewGroup mRefreshLayout;
    private TextView mRefreshStateTextView;
    private PullTexts mPullTexts = new PullTexts();

    public RefreshViewHepler(Context context, PullToRefreshLayout pullToRefreshLayout) {
        mContext = context;
        mPullToRefreshLayout = pullToRefreshLayout;
        init();
    }

    private void init() {
        mRefreshLayout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.pull_refresh_head_layout, null);
        mPullToRefreshLayout.addView(mRefreshLayout);
        int refreshViewColor = mPullToRefreshLayout.getRefreshViewBgColor();
        if(refreshViewColor != 0) {
            mRefreshLayout.setBackgroundColor(refreshViewColor);
        }
        mRefreshStateTextView = (TextView) mRefreshLayout.findViewById(R.id.state_tv);
        mPullToRefreshLayout.mRefreshDist = ViewUtils.getDimenPx(R.dimen.pull_head_h);
        if(mPullToRefreshLayout.getBackground() instanceof ColorDrawable) {
            mRefreshLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void layout(int pullDist) {
        mRefreshLayout.layout(0, pullDist - mRefreshLayout.getMeasuredHeight(),
                mRefreshLayout.getMeasuredWidth(), pullDist);
    }

    public View getView() {
        return mRefreshLayout;
    }

    protected int getMeasuredHeight() {
        if (null == mRefreshLayout) {
            return 0;
        }
        return mRefreshLayout.getMeasuredHeight();
    }

    protected int getMeasuredWidth() {
        if (null == mRefreshLayout) {
            return 0;
        }
        return mRefreshLayout.getMeasuredWidth();
    }

    public void refreshFinish(int refreshResult) {
        mRefreshStateTextView.setText(mPullTexts.getResultText(refreshResult, true));
    }

    public void setPullTexts(PullTexts pullTexts) {
        if (pullTexts == null) {
            return;
        }
        this.mPullTexts = pullTexts;
    }

    protected void onStateChanged(int state) {
//        if (!mPullToRefreshLayout.isHasMoreData()) {
//            mRefreshStateTextView.setText(R.string.load_temporary_not_data);
//            return;
//        }
        String stateText = mPullTexts.getStateText(state, true);
        if (StringUtils.isBlank(stateText)) {
            return;
        }
        mRefreshStateTextView.setText(stateText);
    }

}
