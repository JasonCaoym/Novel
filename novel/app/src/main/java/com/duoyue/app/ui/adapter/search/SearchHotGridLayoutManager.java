package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class SearchHotGridLayoutManager extends GridLayoutManager {

    public SearchHotGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            return super.scrollHorizontallyBy(dx, recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
