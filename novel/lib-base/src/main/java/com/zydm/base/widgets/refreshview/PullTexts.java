package com.zydm.base.widgets.refreshview;


import com.zydm.base.R;
import com.zydm.base.common.Constants;
import com.zydm.base.utils.ViewUtils;

/**
 * Created by yin on 2016/7/30.
 */
public class PullTexts {

    public String getResultText(int refreshResult, boolean isTop) {
        switch (refreshResult) {
            case PullToRefreshLayout.SUCCEED:
                return ViewUtils.getString(R.string.refresh_succeed);
            case PullToRefreshLayout.FAIL_TEMPORARY_NOT_DATA:
                return ViewUtils.getString(R.string.load_temporary_not_data);
            case PullToRefreshLayout.FAIL:
            default:
                return ViewUtils.getString(R.string.refresh_fail);

        }
    }

    public String getStateText(int state, boolean isTop) {
        switch (state) {
            case PullToRefreshLayout.INIT:
                return ViewUtils.getString(R.string.pull_to_refresh);
            case PullToRefreshLayout.RELEASE_TO_REFRESH:
                return ViewUtils.getString(R.string.release_to_refresh);
            case PullToRefreshLayout.REFRESHING:
                return ViewUtils.getString(R.string.refreshing);
            case PullToRefreshLayout.LOADING:
                if(isTop) {
                    return Constants.EMPTY;
                }
                return ViewUtils.getString(R.string.loading);
            case PullToRefreshLayout.RESULT:
            default:
                return Constants.EMPTY;
        }
    }
}
