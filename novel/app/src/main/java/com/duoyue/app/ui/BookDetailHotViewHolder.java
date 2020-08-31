package com.duoyue.app.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class BookDetailHotViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public TextView mTv_name, mTv_author, mTv_sum, mTv_desc, mTv_state;

    public XRelativeLayout xRelativeLayout;

    public XRelativeLayout xLinearLayout;

    public BookDetailHotViewHolder(@NonNull View itemView, boolean isHot) {
        super(itemView);
        imageView = itemView.findViewById(R.id.iv_header);
        mTv_name = itemView.findViewById(R.id.tv_book_name);
        if (isHot) {
            xRelativeLayout = itemView.findViewById(R.id.xrl_hot);
            mTv_author = itemView.findViewById(R.id.tv_author);
            mTv_sum = itemView.findViewById(R.id.tv_sum);
            mTv_sum = itemView.findViewById(R.id.tv_sum);
            mTv_desc = itemView.findViewById(R.id.tv_desc);
            mTv_state = itemView.findViewById(R.id.tv_state);
        } else {
            xLinearLayout = itemView.findViewById(R.id.xrl_other);
        }

    }
}
