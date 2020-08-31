package com.duoyue.app.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class RankingBooksListViewHolder extends RecyclerView.ViewHolder {
    //    mTv_rank_desc
    public TextView mTv_book_name, mTv_desc, mTv_word_count, mTv_rank_name, mTv_rank, nTv_count, mTv_more;
    public ImageView mIn_icon;
    public RecyclerView recyclerView;

    public ImageView imageView, mIv_icon;

    public XRelativeLayout xRelativeLayout;

    public XRelativeLayout cardView;

    public RankingBooksListViewHolder(@NonNull View itemView, boolean isRank) {
        super(itemView);
        if (isRank) {
            mTv_book_name = itemView.findViewById(R.id.tv_book_name);
            mTv_rank = itemView.findViewById(R.id.tv_rank);
            mTv_desc = itemView.findViewById(R.id.tv_desc);
            mTv_rank_name = itemView.findViewById(R.id.tv_rank_name);
//            mTv_rank_desc = itemView.findViewById(R.id.tv_rank_desc);
            mTv_word_count = itemView.findViewById(R.id.tv_word_count);
            mIn_icon = itemView.findViewById(R.id.book_cover);
            recyclerView = itemView.findViewById(R.id.rv_books);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            imageView = itemView.findViewById(R.id.iv_bg);
            cardView = itemView.findViewById(R.id.card_layout);
            mTv_more = itemView.findViewById(R.id.tv_more);
        } else {
            mTv_book_name = itemView.findViewById(R.id.tv_book_name);
            mTv_word_count = itemView.findViewById(R.id.tv_sum);
            nTv_count = itemView.findViewById(R.id.tv_word_count);
            xRelativeLayout = itemView.findViewById(R.id.xll_rank);
            mTv_rank = itemView.findViewById(R.id.tv_rank);
            mIv_icon = itemView.findViewById(R.id.iv_icon);
        }
    }
}
