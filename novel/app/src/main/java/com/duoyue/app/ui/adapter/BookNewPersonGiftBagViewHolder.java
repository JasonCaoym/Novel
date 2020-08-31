package com.duoyue.app.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;

public class BookNewPersonGiftBagViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView, mIv_triangle;
    public TextView textView, mText_bg, mText_time,mTv_title;


    public BookNewPersonGiftBagViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_bg);
        mIv_triangle = itemView.findViewById(R.id.image);
        textView = itemView.findViewById(R.id.tv_state);
        mText_time = itemView.findViewById(R.id.tv_time);
        mText_bg = itemView.findViewById(R.id.text_bg);
        mTv_title = itemView.findViewById(R.id.tv_title);
    }
}
