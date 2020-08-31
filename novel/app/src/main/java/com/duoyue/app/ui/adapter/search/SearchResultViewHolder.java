package com.duoyue.app.ui.adapter.search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class SearchResultViewHolder extends RecyclerView.ViewHolder {

    public TextView textView,mTv_auth;
    public XRelativeLayout xRelativeLayout;
    public View view;

    public ImageView imageView;

    public SearchResultViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.tv_search);
        textView = itemView.findViewById(R.id.tv_key_word);
        mTv_auth = itemView.findViewById(R.id.tv_auth_or_book);
        xRelativeLayout = itemView.findViewById(R.id.xrl_result);
        view = itemView.findViewById(R.id.view_line);
    }
}
