package com.duoyue.app.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.duoyue.app.bean.BookChildColumnsBean;
import com.duoyue.mianfei.xiaoshuo.R;

import java.util.List;

public class FixedOne2ColumnAdapter extends RecyclerView.Adapter<FixedOne2ColumnAdapter.FixedOne2ColumnViewHolder> {

    private List<BookChildColumnsBean> childColumnsBeans;

    private Context mContext;

    private View.OnClickListener clickListener;

    public FixedOne2ColumnAdapter(Context context, View.OnClickListener onClickListener) {
        this.mContext = context;
        this.clickListener = onClickListener;
    }

    public void setData(List<BookChildColumnsBean> beans) {
        this.childColumnsBeans = beans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FixedOne2ColumnViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_more, viewGroup, false);
        return new FixedOne2ColumnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixedOne2ColumnViewHolder fixedOne2ColumnViewHolder, int i) {
        if (childColumnsBeans.get(i).getIndex() == i) {

            fixedOne2ColumnViewHolder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
//            fixedOne2ColumnViewHolder.textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            fixedOne2ColumnViewHolder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.color_898989_));
//            fixedOne2ColumnViewHolder.textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        fixedOne2ColumnViewHolder.textView.setText(childColumnsBeans.get(i).getChildColumnName());
        fixedOne2ColumnViewHolder.textView.setOnClickListener(clickListener);
        fixedOne2ColumnViewHolder.textView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if (this.childColumnsBeans != null && !this.childColumnsBeans.isEmpty()) return childColumnsBeans.size();
        return 0;
    }

    class FixedOne2ColumnViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public FixedOne2ColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_column);
        }
    }
}
