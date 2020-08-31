package com.duoyue.app.ui.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.zydm.base.utils.ViewUtils;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    int mSpace;


    SpaceItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.right = 0;
            outRect.left = ViewUtils.dp2px(25);
        } else if (parent.getChildAdapterPosition(view) == parent.getLayoutManager().getItemCount()-1) {
            outRect.left = mSpace;
            outRect.right = ViewUtils.dp2px(25);
        } else {
            outRect.right = 0;
            outRect.left = mSpace;
        }
    }
}
