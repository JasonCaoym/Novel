package com.duoyue.app.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;

public class FixedSixViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public TextView textView;

    public TextView mTv_read;

    public CardView cardView;

    public FixedSixViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.iv_bg);
        textView = itemView.findViewById(R.id.tv_name);
        mTv_read = itemView.findViewById(R.id.tv_read);
        cardView = itemView.findViewById(R.id.cv);
    }
}
