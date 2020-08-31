package com.zydm.base.widgets.refreshview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 垂直列表recyclerview
 */
public class PullableRecyclerView extends RecyclerView implements IPullable {

    private static final String TAG = "PullableRecyclerView";
    private LinearLayoutManager mLayoutManager;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ItemDecoration mItemDecoration;
    private boolean mCanPullDownWhenEmpty;

    public PullableRecyclerView(Context context) {
        super(context);
        initSetting(context);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting(context);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSetting(context);
    }

    private void initSetting(Context context) {
        setHasFixedSize(true);
        setOverScrollMode(OVER_SCROLL_NEVER);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager == null) {
                    return;
                }
                if (mLayoutManager.findFirstVisibleItemPosition() > 0 &&
                        mLayoutManager.findLastVisibleItemPosition() >= getAdapter().getItemCount() - 4) {
                    if (mPullToRefreshLayout != null) {
                        mPullToRefreshLayout.autoLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof LinearLayoutManager) {
            mLayoutManager = (LinearLayoutManager) layout;
            super.setLayoutManager(layout);
        } else {
            throw new IllegalArgumentException("This PullableRecyclerView must use LinearLayoutManager Or GridLayoutManger!");
        }
    }

    //1.2.1稍作修改
    @Override
    public void setAdapter(Adapter adapter) {
        if (null == mLayoutManager) {
            setLayoutManager(new PullableLayoutManager(getContext()));
        }
        super.setAdapter(adapter);
    }

    @Override
    public boolean isReadyForPullDown() {
        return PullableUtils.isReadyForPullDownWithRecycler(this);
    }

    @Override
    public boolean isReadyForPullUp() {
        return PullableUtils.isReadyForPullUpWithRecycler(this);
    }

    @Override
    public void setPullToRefreshLayout(PullToRefreshLayout pullToRefreshLayout) {
        mPullToRefreshLayout = pullToRefreshLayout;
    }

    @Override
    public void addItemDecoration(ItemDecoration decor, int index) {
        super.addItemDecoration(decor, index);

        //第一个
        if (index <= 0) {
            mItemDecoration = decor;
        }
    }

    public ItemDecoration getFirstItemDecoration() {
        return mItemDecoration;
    }

    public void setCanPullDownWhenEmpty(boolean canPullDownWhenEmpty) {
        this.mCanPullDownWhenEmpty = canPullDownWhenEmpty;
    }

    public boolean isCanPullDownWhenEmpty() {
        return mCanPullDownWhenEmpty;
    }
}
