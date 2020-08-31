package com.zydm.base.widgets.refreshview;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;

public class PullableUtils {

    public static boolean isReadyForPullDown(AbsListView absListView) {
        if (absListView.getCount() <= 0) {
            // 没有item的时候也可以下拉刷新
            return true;
        } else // 滑到ListView的顶部了
            return absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0) != null
                    && absListView.getChildAt(0).getTop() >= absListView.getPaddingTop();
    }

    public static boolean isReadyForPullUp(AbsListView absListView) {
        int firstVisiblePosition = absListView.getFirstVisiblePosition();
        int lastVisiblePosition = absListView.getLastVisiblePosition();
        if (absListView.getCount() <= 0) {
            // 没有item的时候也可以上拉加载
            return false;
        } else if (lastVisiblePosition == (absListView.getCount() - 1)) {
            // 滑到底部了
            if (absListView.getChildAt(lastVisiblePosition - firstVisiblePosition) != null
                    && absListView.getChildAt(lastVisiblePosition - firstVisiblePosition).getBottom() <= absListView
                    .getMeasuredHeight() - absListView.getPaddingBottom()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReadyForPullDownWithRecycler(PullableRecyclerView recyclerView) {

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (null == layoutManager) {
            return false;
        }
        if (layoutManager.getChildCount() <= 0) {
            return recyclerView.isCanPullDownWhenEmpty();
        } else {
            if (layoutManager.findFirstVisibleItemPosition() == 0 && layoutManager.getChildAt(0) != null
                    && layoutManager.getChildAt(0).getTop() - getFirstItemDecorationFor(recyclerView) >= layoutManager.getPaddingTop()) {
                return true;
            }
        }
        return false;
    }

    private static int getFirstItemDecorationFor(PullableRecyclerView recyclerView) {
        RecyclerView.ItemDecoration decoration = recyclerView.getFirstItemDecoration();
        View child = recyclerView.getChildAt(0);
        if (decoration == null || child == null) {
            return 0;
        }
        Rect rect = new Rect();
        decoration.getItemOffsets(rect, child, recyclerView, null);
        return rect.top;
    }

    public static boolean isReadyForPullUpWithRecycler(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (null == layoutManager) {
            return false;
        }
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        if (layoutManager.getChildCount() <= 0) {
            return false;
        } else if (lastVisiblePosition == (layoutManager.getItemCount() - 1)) {
            if (layoutManager.getChildAt(lastVisiblePosition - firstVisiblePosition) != null
                    && layoutManager.getChildAt(lastVisiblePosition - firstVisiblePosition).getBottom() <= recyclerView
                    .getMeasuredHeight() - layoutManager.getPaddingBottom()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReadyForPullDown(View view) {
        if (view instanceof AbsListView) {
            return isReadyForPullDown((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return isReadyForPullDownWithRecycler((PullableRecyclerView) view);
        } else if (view instanceof IPullable) {
            return ((IPullable) view).isReadyForPullDown();
        } else {
            return false;
        }
    }
}
