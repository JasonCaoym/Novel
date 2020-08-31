package com.duoyue.app.ui.adapter.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.app.bean.SearchRecommdBookBean;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

public class SearchRecommdBookAdapter extends RecyclerView.Adapter<SearchRecommdBookAdapter.ViewHolder> {

    private Context mContext;
    private List<SearchRecommdBookBean> searchRecommdBookBeans;

    private View.OnClickListener onClickListener;

    public SearchRecommdBookAdapter(Context context, List<SearchRecommdBookBean> recommdBookBeanList, View.OnClickListener clickListener) {
        this.mContext = context;
        this.searchRecommdBookBeans = recommdBookBeanList;
        this.onClickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_search, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mTv_hot.setText(searchRecommdBookBeans.get(i).getTitle());
        viewHolder.xLinearLayout.setOnClickListener(onClickListener);
        viewHolder.xLinearLayout.setTag(searchRecommdBookBeans.get(i).getBookId());
        GlideUtils.INSTANCE.loadImage(BaseContext.getContext(), searchRecommdBookBeans.get(i).getCover(), viewHolder.mIv_hot, GlideUtils.INSTANCE.getBookRadius());
        FuncPageStatsApi.searchRecommendResultList(searchRecommdBookBeans.get(i).getBookId());
    }

    @Override
    public int getItemCount() {
        if (this.searchRecommdBookBeans != null && !this.searchRecommdBookBeans.isEmpty())
            return this.searchRecommdBookBeans.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTv_hot;
        public ImageView mIv_hot;
        public XLinearLayout xLinearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv_hot = itemView.findViewById(R.id.tv_item_hot);
            mIv_hot = itemView.findViewById(R.id.iv_item_hot);
            xLinearLayout = itemView.findViewById(R.id.xll);
        }
    }
}
