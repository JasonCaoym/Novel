package com.duoyue.app.ui.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.data.bean.CategoryBean;

/**
 * 分类Adapter
 *
 * @author zp
 * @data
 */
public class NewCategoryAdapter extends SimpleRecyclerAdapter<CategoryBean> {
    private Context mContext;

    public NewCategoryAdapter(Context context) {
        mContext = context;
    }

    @Override
    public SimpleViewHolder<CategoryBean> onCreateViewHolder(ViewGroup parent, int viewType) {
        //分类.
        return new RightCategoryViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.new_category_item_view, parent, false), this);

    }
}
