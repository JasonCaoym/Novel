package com.duoyue.app.ui.adapter.search;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class SearchResultListViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public TextView mTv_name, mTv_desc, mTv_author, mTv_fen, mTv_zi, mTv_title,mTv_sum;

    public XRelativeLayout xRelativeLayout;

    public XLinearLayout xLinearLayout;

    public View view;

    public RecyclerView recyclerView;

    public SearchResultListViewHolder(@NonNull View itemView, int itemType) {
        super(itemView);
        if (itemType == 102) {
            mTv_name = itemView.findViewById(R.id.tv_author);
            mTv_desc = itemView.findViewById(R.id.tv_count);
            xLinearLayout = itemView.findViewById(R.id.xll_auth);
        } else if (itemType == 104) {
            mTv_name = itemView.findViewById(R.id.tv_author);
            mTv_sum = itemView.findViewById(R.id.tv_sum);
            recyclerView = itemView.findViewById(R.id.rv_rm_book);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 4);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
        } else {
            imageView = itemView.findViewById(R.id.iv_icon);
            mTv_name = itemView.findViewById(R.id.tv_book_name);
            mTv_desc = itemView.findViewById(R.id.tv_desc);
            mTv_author = itemView.findViewById(R.id.tv_author);
            mTv_fen = itemView.findViewById(R.id.tv_fen);
            mTv_zi = itemView.findViewById(R.id.tv_zi);
            mTv_title = itemView.findViewById(R.id.tv_title);
            xRelativeLayout = itemView.findViewById(R.id.xrl_search);
            view = itemView.findViewById(R.id.view_top);
        }

    }
}
