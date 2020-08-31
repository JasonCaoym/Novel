package com.duoyue.app.ui.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class FixedOne2FourViewHolder extends RecyclerView.ViewHolder {
    public static final int ITEM_ONE = 201;
    public static final int ITEM_TWO = 202;

    public ImageView imageView;
    public TextView textView;


    public TextView mTv_grade;

    public TextView mTv_resume;
    public TextView mTv_author;
    public TextView mTv_word_count;
    public TextView mTv_category;
    public TextView mTv_count;
    public TextView mTv_grade_x;

    public ImageView mIv_hot;

    public XRelativeLayout xRelativeLayout;


    public FixedOne2FourViewHolder(@NonNull View itemView, int type) {
        super(itemView);
        textView = itemView.findViewById(R.id.book_name);
        imageView = itemView.findViewById(R.id.book_cover);
        if (type == ITEM_ONE) {
            mTv_grade = itemView.findViewById(R.id.tv_grade);
            mTv_resume = itemView.findViewById(R.id.book_resume);
            mTv_author = itemView.findViewById(R.id.book_author);
            mTv_word_count = itemView.findViewById(R.id.book_word_count);
            mTv_category = itemView.findViewById(R.id.book_category);
            mTv_count = itemView.findViewById(R.id.book_count);
            mTv_grade_x = itemView.findViewById(R.id.book_grade);
            mIv_hot = itemView.findViewById(R.id.iv_new_hot_icon);
        }
        xRelativeLayout = itemView.findViewById(R.id.xll_item_two);
    }
}
