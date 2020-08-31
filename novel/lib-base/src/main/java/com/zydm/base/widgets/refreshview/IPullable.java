package com.zydm.base.widgets.refreshview;


public interface IPullable {
    /**
     * 判断是否可以下拉，如果不需要下拉功能可以直接return false
     * 
     * @return true如果可以下拉否则返回false
     */
    boolean isReadyForPullDown();

    /**
     * 判断是否可以上拉，如果不需要上拉功能可以直接return false
     * 
     * @return true如果可以上拉否则返回false
     */
    boolean isReadyForPullUp();

    void setPullToRefreshLayout(PullToRefreshLayout pullToRefreshLayout);
}

