package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.SearchBean;
import com.duoyue.mianfei.xiaoshuo.R;

import java.util.List;

public class SearchV2Adapter extends RecyclerView.Adapter<SearchV2ViewHolder> {

    private List<SearchBean> searchBeans;
    private Context mContext;

    private View.OnClickListener mOnClickListener;


    public SearchV2Adapter(Context context, List<SearchBean> searchBeans, View.OnClickListener onClickListener) {
        this.searchBeans = searchBeans;
        this.mContext = context;
        this.mOnClickListener = onClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        if (searchBeans.get(position).getType() == SearchBean.TYME_ONE) {
            return SearchBean.TYME_ONE;
        } else if (searchBeans.get(position).getType() == SearchBean.TYME_TWO) {
            return SearchBean.TYME_TWO;
        } else {
            return SearchBean.TYME_THREE;
        }


    }

    @NonNull
    @Override
    public SearchV2ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == SearchBean.TYME_ONE) {
            View view2 = LayoutInflater.from(mContext).inflate(R.layout.item_all_book, viewGroup, false);
            return new SearchV2ViewHolder(view2, i);
        } else if (i == SearchBean.TYME_TWO) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_book, viewGroup, false);
            return new SearchV2ViewHolder(view, i);
        } else {
            View view3 = LayoutInflater.from(mContext).inflate(R.layout.item_local_book, viewGroup, false);
            return new SearchV2ViewHolder(view3, i);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull SearchV2ViewHolder searchV2ViewHolder, int i) {

        if (getItemViewType(i) == SearchBean.TYME_ONE) {
            searchV2ViewHolder.mTv_all.setText(searchBeans.get(i).getSearchV2MoreListBean().getHotSearchTitle());
            searchV2ViewHolder.mTv_all.setVisibility(TextUtils.isEmpty(searchBeans.get(i).getSearchV2MoreListBean().getHotSearchTitle()) ? View.GONE : View.VISIBLE);
            SearchAllAdapter searchAllAdapter = new SearchAllAdapter(mContext, searchBeans.get(i).getSearchV2MoreListBean().getMoreList(), mOnClickListener);
            searchV2ViewHolder.mRv_all.setAdapter(searchAllAdapter);
            searchV2ViewHolder.mRv_all.setVisibility(TextUtils.isEmpty(searchBeans.get(i).getSearchV2MoreListBean().getHotSearchTitle()) ? View.GONE : View.VISIBLE);
            searchV2ViewHolder.getView.setVisibility(TextUtils.isEmpty(searchBeans.get(i).getSearchV2MoreListBean().getHotSearchTitle()) ? View.GONE : View.VISIBLE);
        } else if (getItemViewType(i) == SearchBean.TYME_TWO) {
            searchV2ViewHolder.mTv_hot.setText(searchBeans.get(i).getSearchV2ListBean().getBillboardTitle());
            searchV2ViewHolder.mTv_hot.setVisibility(TextUtils.isEmpty(searchBeans.get(i).getSearchV2ListBean().getBillboardTitle()) ? View.GONE : View.VISIBLE);
            SearchHotAdapter searchHotAdapter = new SearchHotAdapter(mContext, searchBeans.get(i).getSearchV2ListBean().getCommentList(), mOnClickListener, true);
            searchV2ViewHolder.mRv_hot.setAdapter(searchHotAdapter);
            searchV2ViewHolder.mRv_hot.setVisibility(TextUtils.isEmpty(searchBeans.get(i).getSearchV2ListBean().getBillboardTitle()) ? View.GONE : View.VISIBLE);
            searchV2ViewHolder.view.setVisibility(TextUtils.isEmpty(searchBeans.get(i).getSearchV2ListBean().getBillboardTitle()) ? View.GONE : View.VISIBLE);
        } else {
            SearchResultHistoryAdapter searchResultAdapter = new SearchResultHistoryAdapter(mContext, searchBeans.get(i).getStringList(), mOnClickListener);
            searchV2ViewHolder.mTv_local.setText(searchBeans.get(i).getStringList().isEmpty() ? "" : "搜索历史");
            searchV2ViewHolder.imageView.setVisibility(searchBeans.get(i).getStringList().isEmpty() ? View.GONE : View.VISIBLE);
            searchV2ViewHolder.mRv_local.setAdapter(searchResultAdapter);
            searchV2ViewHolder.imageView.setOnClickListener(mOnClickListener);
        }
    }


    @Override
    public int getItemCount() {
        if (searchBeans != null && !searchBeans.isEmpty()) {
            return searchBeans.size();
        }
        return 0;
    }
}
