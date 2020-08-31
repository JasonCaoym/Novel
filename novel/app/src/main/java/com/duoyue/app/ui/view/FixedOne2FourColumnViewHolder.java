package com.duoyue.app.ui.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;

public class FixedOne2FourColumnViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;

    public FixedOne2FourColumnViewHolder(@NonNull View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.tv_column);
    }
}
