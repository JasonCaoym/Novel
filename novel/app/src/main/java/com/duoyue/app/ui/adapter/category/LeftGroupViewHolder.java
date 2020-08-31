package com.duoyue.app.ui.adapter.category;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.bean.CategoryGroupBean;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.utils.ViewUtils;

/**
 * 左侧分类组ViewHolder
 * @author caoym
 * @data 2019/4/18  18:59
 */
public class LeftGroupViewHolder extends SimpleViewHolder<CategoryGroupBean>
{
    /**
     * 左侧显示组名称TextView
     */
    private TextView mGroupNameTextView;

    /**
     * 左侧显示组选中标签
     */
    //private View mSelectedMarkview;

    public LeftGroupViewHolder(View itemView, @Nullable SimpleRecyclerAdapter<CategoryGroupBean> adapter)
    {
        super(itemView, adapter);
        //左侧显示组名称
        mGroupNameTextView = itemView.findViewById(R.id.group_name_textview);
        //左侧显示组选中标签
        //mSelectedMarkview = itemView.findViewById(R.id.selected_mark_view);
    }

    @Override
    protected void refreshView(CategoryGroupBean groupBean)
    {
        mGroupNameTextView.setText(groupBean.groupName);
        //item点击后背景的变化
        if (groupBean.isSelected)
        {
            //mSelectedMarkview.setVisibility(View.VISIBLE);
            mGroupNameTextView.setBackgroundResource(R.color.white);
            mGroupNameTextView.setTextColor(ViewUtils.getColor(R.color.standard_red_main_color_c1));
        } else
        {
            //mSelectedMarkview.setVisibility(View.GONE);
            mGroupNameTextView.setBackgroundResource(R.color.transparent);
            mGroupNameTextView.setTextColor(ViewUtils.getColor(R.color.color_898989));
        }
    }
}
