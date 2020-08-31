package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.SearchHotBean;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class SearchHotAdapter extends RecyclerView.Adapter<SearchHotViewHolder> {


    private Context mContext;

    private List<SearchHotBean> searchHotBeans;

    private View.OnClickListener mOnClickListener;

    private Boolean aBoolean;

    public SearchHotAdapter(Context context, List<SearchHotBean> searchHotBeanList, View.OnClickListener onClickListener, boolean isNo) {

        this.mContext = context;
        this.searchHotBeans = searchHotBeanList;

        this.mOnClickListener = onClickListener;

        this.aBoolean = isNo;

    }

    @NonNull
    @Override
    public SearchHotViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (aBoolean) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_search, viewGroup, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_search_v2, viewGroup, false);
        }
        return new SearchHotViewHolder(view, SearchHotViewHolder.ITEM_TYPE_ONE);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHotViewHolder searchHotViewHolder, int i) {

        searchHotViewHolder.mTv_hot.setText(searchHotBeans.get(i).getName());
        GlideUtils.INSTANCE.loadImage(BaseContext.getContext(), searchHotBeans.get(i).getCover(), searchHotViewHolder.mIv_hot, GlideUtils.INSTANCE.getBookRadius());
        searchHotViewHolder.mIv_hot.setOnClickListener(mOnClickListener);
        searchHotViewHolder.mIv_hot.setTag(R.id.tag_item, searchHotBeans.get(i).getId());
    }

    @Override
    public int getItemCount() {
        if (searchHotBeans != null && !searchHotBeans.isEmpty()) {
            return (this.aBoolean ? 4 : searchHotBeans.size());
        }
        return 0;
    }
}
