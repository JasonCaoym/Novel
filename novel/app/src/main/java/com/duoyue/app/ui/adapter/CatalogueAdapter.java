package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.ui.catalogue.CatalogueActivity.GroupItem;

import java.util.List;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.CatelogueViewHolder> {


    private Context mContext;
    private List<GroupItem> mList;


    public CatalogueAdapter(Context context, List<GroupItem> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }

    @NonNull
    @Override
    public CatelogueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_catalogue_view, viewGroup, false);
        return new CatelogueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatelogueViewHolder catelogueViewHolder, final int i) {
        catelogueViewHolder.title.setText(mList.get(i).getName());
        if (mList.get(i).isReadGroup()) {
            catelogueViewHolder.tick.setVisibility(View.VISIBLE);
        } else {
            catelogueViewHolder.tick.setVisibility(View.GONE);
        }

        catelogueViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mOnItemClickListener.onItemClick(v,i);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mList != null && !mList.isEmpty()) return mList.size();
        return 0;
    }

    public class CatelogueViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView tick;

        public CatelogueViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            tick = itemView.findViewById(R.id.iv_tick);
        }
    }
}
