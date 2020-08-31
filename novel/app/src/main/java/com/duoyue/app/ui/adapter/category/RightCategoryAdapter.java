package com.duoyue.app.ui.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.data.bean.CategoryBean;

/**
 * 右侧分类信息Adapter
 *
 * @author caoym
 * @data 2019/4/18  18:59
 */
public class RightCategoryAdapter extends SimpleRecyclerAdapter<CategoryBean> {

    private Context mContext;

    public RightCategoryAdapter(Context context) {
        mContext = context;
    }

    @Override
    public SimpleViewHolder<CategoryBean> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CategoryBean.VIEW_TYPE_GROUP) {
            //分类组.
            return new RightGroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_header_view, parent, false), this);
        } else if (viewType == CategoryBean.VIEW_TYPE_NULL) {
            return new RightCategoryNullViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_null_item_view, parent, false), this);
        } else {
            //分类.
            return new RightCategoryViewHolder(mContext, LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_view, parent, false), this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mListData.get(position).getViewType();
    }
}
