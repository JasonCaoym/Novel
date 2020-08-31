package com.duoyue.app.ui.adapter;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.zydm.base.utils.ViewUtils;

public class BookNewListItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = ViewUtils.dp2px(16);
        } else if (parent.getChildAdapterPosition(view) == parent.getLayoutManager().getItemCount() - 1) {
            outRect.left = ViewUtils.dp2px(9);
            outRect.right = ViewUtils.dp2px(16);
        } else {
            outRect.left = ViewUtils.dp2px(9);
        }
    }
}
