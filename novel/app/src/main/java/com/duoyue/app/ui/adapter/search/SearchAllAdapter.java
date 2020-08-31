package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.SearchV2MoreBean;
import com.duoyue.mianfei.xiaoshuo.R;

import java.util.List;

public class SearchAllAdapter extends RecyclerView.Adapter<SearchHotViewHolder> {


    private Context mContext;

    private List<SearchV2MoreBean> searchHotBeans;

    private View.OnClickListener onClickListener;

    public SearchAllAdapter(Context context, List<SearchV2MoreBean> searchHotBeanList, View.OnClickListener clickListener) {

        this.mContext = context;
        this.searchHotBeans = searchHotBeanList;

        this.onClickListener = clickListener;

    }

    @NonNull
    @Override
    public SearchHotViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_search, viewGroup, false);
        return new SearchHotViewHolder(view, SearchHotViewHolder.ITEM_TYPE_TWO);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHotViewHolder searchHotViewHolder, int i) {
        if (i == 0) {
            searchHotViewHolder.mTv_all_one.setBackgroundResource(R.mipmap.bg_hoe_one);
        } else if (i == 1) {
            searchHotViewHolder.mTv_all_one.setBackgroundResource(R.mipmap.bg_hoe_two);
        } else if (i == 2) {
            searchHotViewHolder.mTv_all_one.setBackgroundResource(R.mipmap.bg_hoe_three);
        } else {
            searchHotViewHolder.mTv_all_one.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_d8d8d8));
        }
        searchHotViewHolder.mTv_all_one.setText(String.valueOf(i + 1));
        searchHotViewHolder.mTv_all_two.setText(searchHotBeans.get(i).getWord().trim());
        searchHotViewHolder.xRelativeLayout.setTag(searchHotBeans.get(i).getWord());
        searchHotViewHolder.xRelativeLayout.setOnClickListener(onClickListener);
        searchHotViewHolder.mTv_fire.setVisibility(searchHotBeans.get(i).getIsShowIcon() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        if (searchHotBeans != null && !searchHotBeans.isEmpty()) {
            return searchHotBeans.size();
        }
        return 0;
    }
}
