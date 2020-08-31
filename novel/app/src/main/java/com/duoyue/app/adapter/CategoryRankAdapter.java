package com.duoyue.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.duoyue.app.bean.BookRankCategoryBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类页面的榜单适配器
 */
public class CategoryRankAdapter extends RecyclerView.Adapter<CategoryRankAdapter.CategoryHolderView> {

    private List<BookRankCategoryBean> categoryList = new ArrayList<>();
    private Context mContext;
    private int seletedColor;
    private int normalColor;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onClick(View view);
    }

    public CategoryRankAdapter(Context context, OnItemClickListener clickListener) {
        mContext = context;
        this.clickListener = clickListener;
        seletedColor = mContext.getResources().getColor(R.color.standard_red_main_color_c1);
        normalColor = mContext.getResources().getColor(R.color.color_898989);
    }

    public void setCategoryList(List<BookRankCategoryBean> list) {
        categoryList.clear();
        if (list != null) {
            categoryList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public CategoryHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryHolderView(LayoutInflater.from(mContext).inflate(R.layout.book_rank_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolderView holder, int position) {
        holder.bindData(categoryList.get(position));
        holder.itemView.setTag(categoryList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryHolderView extends RecyclerView.ViewHolder {

        private TextView tvName;

        public CategoryHolderView(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.book_rank_category_name);
        }

        public void bindData(BookRankCategoryBean categoryBean) {
            tvName.setText(categoryBean.getName());
            if (categoryBean.getSelected()) {
                tvName.setTextColor(seletedColor);
                tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                tvName.setTextColor(normalColor);
                tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_F7F7F7));
            }
        }
    }

}
