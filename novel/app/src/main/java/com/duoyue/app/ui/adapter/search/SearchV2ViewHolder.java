package com.duoyue.app.ui.adapter.search;

import android.graphics.Typeface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.SearchBean;
import com.duoyue.mianfei.xiaoshuo.R;

public class SearchV2ViewHolder extends RecyclerView.ViewHolder {


    public TextView mTv_hot, mTv_all, mTv_local;

    public RecyclerView mRv_hot, mRv_all, mRv_local;

    public ImageView imageView;

    public View view, getView;

    public SearchV2ViewHolder(View itemView, int itemtype) {
        super(itemView);
//        Typeface typeFace = TitleTypeface.getTypeFace(itemView.getContext().getApplicationContext());
        switch (itemtype) {
            case SearchBean
                    .TYME_ONE:
                mTv_all = itemView.findViewById(R.id.tv_all);
//                mTv_all.setTypeface(typeFace);
                mRv_all = itemView.findViewById(R.id.rv_all);
                SearchHotGridLayoutManager gridLayoutManager2 = new SearchHotGridLayoutManager(itemView.getContext(), 2);
                mRv_all.setLayoutManager(gridLayoutManager2);
                getView = itemView.findViewById(R.id.view_x);

                break;
            case SearchBean
                    .TYME_TWO:
                mTv_hot = itemView.findViewById(R.id.tv_hot);
//                mTv_hot.setTypeface(typeFace);
                mRv_hot = itemView.findViewById(R.id.rv_hot_list);
                SearchHotGridLayoutManager gridLayoutManager = new SearchHotGridLayoutManager(itemView.getContext(), 4);
                mRv_hot.setLayoutManager(gridLayoutManager);
                view = itemView.findViewById(R.id.view);
                break;

            case SearchBean
                    .TYME_THREE:
                mRv_local = itemView.findViewById(R.id.rv_local);
                mTv_local = itemView.findViewById(R.id.tv_local);
//                mTv_local.setTypeface(typeFace);
                imageView = itemView.findViewById(R.id.iv_delete);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
                mRv_local.setLayoutManager(linearLayoutManager);
                break;
        }
    }
}
