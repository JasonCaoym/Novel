package com.duoyue.app.ui.adapter.category;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.data.bean.CategoryBean;

/**
 * 右边分类组ViewHolder
 *
 * @author caoym
 * @data 2019/4/18  19:46
 */
public class RightGroupViewHolder extends SimpleViewHolder<CategoryBean> {
    /**
     * 分类组名称.
     */
    public TextView mGroupNameTextView;

    public  View mView;

    public RightGroupViewHolder(View itemView, @Nullable SimpleRecyclerAdapter<CategoryBean> adapter) {
        super(itemView, adapter);
        //分类组名称
        this.mView = itemView;
        mGroupNameTextView = itemView.findViewById(R.id.category_group_name);
    }

    @Override
    protected void refreshView(CategoryBean categoryBean) {
        mGroupNameTextView.setText(categoryBean.getName());
    }
}
