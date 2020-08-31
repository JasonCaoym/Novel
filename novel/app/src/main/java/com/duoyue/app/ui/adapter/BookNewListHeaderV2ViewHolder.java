package com.duoyue.app.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;

public class BookNewListHeaderV2ViewHolder extends RecyclerView.ViewHolder {

    public ImageView mIv_icon2, mIv_icon, mIv_sex;

    public TextView mTv_name, mTv_read, mTv_mi, mTv_book_name, mTv_desc, mTv_state, mTv_all_read, mTv_join;
    public XRelativeLayout mXrl_one, mXrl_two;

    public BookNewListHeaderV2ViewHolder(@NonNull View itemView) {
        super(itemView);
        mIv_icon2 = itemView.findViewById(R.id.iv_icon2);
        mIv_sex = itemView.findViewById(R.id.iv_sex);
        mTv_name = itemView.findViewById(R.id.tv_name);
        mTv_read = itemView.findViewById(R.id.tv_read);
        mTv_mi = itemView.findViewById(R.id.tv_mi);
        mIv_icon = itemView.findViewById(R.id.iv_icon);
        mTv_book_name = itemView.findViewById(R.id.tv_book_name);
        mTv_desc = itemView.findViewById(R.id.tv_desc);
        mTv_state = itemView.findViewById(R.id.tv_state);
        mTv_all_read = itemView.findViewById(R.id.tv_all_read);
        mTv_join = itemView.findViewById(R.id.tv_join);
        mXrl_one = itemView.findViewById(R.id.xll_one);
        mXrl_two = itemView.findViewById(R.id.xll_two);
    }
}
