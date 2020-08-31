package com.duoyue.app.ui.adapter.search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class SearchHotViewHolder extends RecyclerView.ViewHolder {

    public static final int ITEM_TYPE_ONE = 1;
    public static final int ITEM_TYPE_TWO = 2;


    public TextView mTv_hot, mTv_all_one, mTv_all_two;
    public ImageView mIv_hot, mTv_fire;

    public XLinearLayout xRelativeLayout;

    public SearchHotViewHolder(@NonNull View itemView, int type) {
        super(itemView);
        if (type == ITEM_TYPE_ONE) {
            mTv_hot = itemView.findViewById(R.id.tv_item_hot);
            mIv_hot = itemView.findViewById(R.id.iv_item_hot);
        } else {
            mTv_all_one = itemView.findViewById(R.id.tv_all);
            mTv_all_two = itemView.findViewById(R.id.tv_item_all);
            mTv_fire = itemView.findViewById(R.id.iv_fire);
            xRelativeLayout = itemView.findViewById(R.id.xll_item);
        }

    }
}
