package com.duoyue.app.ui.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.duoyue.mianfei.xiaoshuo.R;

public class FixedOne2FourV2ViewHolder extends RecyclerView.ViewHolder {


    public RecyclerView recyclerView, mRv_more;

    public FixedOne2FourV2ViewHolder(@NonNull View itemView) {
        super(itemView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView = itemView.findViewById(R.id.rv_book_city);
        recyclerView.setLayoutManager(linearLayoutManager);
        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        mRv_more = itemView.findViewById(R.id.rv_column);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(),4);
        mRv_more.setLayoutManager(gridLayoutManager);


    }
}
