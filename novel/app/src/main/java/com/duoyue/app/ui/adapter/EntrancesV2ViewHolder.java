package com.duoyue.app.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class EntrancesV2ViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView textView;
    public XRelativeLayout xRelativeLayout;

    public EntrancesV2ViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.iv_icon);
        textView = itemView.findViewById(R.id.tv_icon_text);
        xRelativeLayout = itemView.findViewById(R.id.xll_icon);
    }
}
