package com.duoyue.app.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;

public class BookDetailCommentViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivLogo;
    public TextView tvNick;
    public RatingBar ratingBar;
    public TextView tvContent;
    public View moreLayout;
    public RelativeLayout moreParentView;
    public TextView mTv_comment;

    public BookDetailCommentViewHolder(@NonNull View itemView) {
        super(itemView);

        ivLogo = itemView.findViewById(R.id.book_comment_item_logo);
        tvNick = itemView.findViewById(R.id.book_comment_item_nick);
        ratingBar = itemView.findViewById(R.id.book_comment_item_ratingbar);
        mTv_comment = itemView.findViewById(R.id.tv_comment);
        tvContent = itemView.findViewById(R.id.book_comment_item_comment);
        moreParentView = itemView.findViewById(R.id.booke_detail_resume_layout);
        moreLayout = itemView.findViewById(R.id.book_detail_open);
    }
}
