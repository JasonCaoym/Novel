package com.duoyue.app.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.app.bean.BookChildColumnsBean;
import com.duoyue.mianfei.xiaoshuo.R;

import java.util.List;

public class FixedOne2ItemColumnAdapter extends RecyclerView.Adapter<FixedOne2ItemColumnAdapter.FixedOne2ItemColumnViewHolder> {

    private List<BookChildColumnsBean> childColumnsBeans;

    private Context mContext;

    private View.OnClickListener onClickListener;

    private int mType;
    private String mTag;
    private String channel;

    private int mClassId;
    private String mPage;

    public void setData(List<BookChildColumnsBean> beans, Context context, View.OnClickListener clickListener, int type, String tag, String channel) {
        this.childColumnsBeans = beans;
        this.mContext = context;
        this.onClickListener = clickListener;
        this.mType = type;
        this.mTag = tag;
        this.channel = channel;
        notifyDataSetChanged();
    }


    public void setClassId(String page,int classId) {
        this.mClassId = classId;
        this.mPage = page;

    }

    @NonNull
    @Override
    public FixedOne2ItemColumnViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_column_more, viewGroup, false);
        return new FixedOne2ItemColumnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixedOne2ItemColumnViewHolder fixedOne2ItemColumnViewHolder, int i) {
        FixedOne2FourAdapter fixedOne2FourAdapter = new FixedOne2FourAdapter(mContext, onClickListener, channel);
        fixedOne2FourAdapter.setData(childColumnsBeans.get(i).getBooks(), mType, mTag);
        fixedOne2FourAdapter.setPageId(mPage, mClassId);
        fixedOne2ItemColumnViewHolder.recyclerView.setAdapter(fixedOne2FourAdapter);
    }

    @Override
    public int getItemCount() {
        if (this.childColumnsBeans != null && !this.childColumnsBeans.isEmpty()) return childColumnsBeans.size();
        return 0;
    }

    class FixedOne2ItemColumnViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;

        public FixedOne2ItemColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_column);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 4);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    if (i == 0) {
                        return 4;
                    }
                    return 1;
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }
}
