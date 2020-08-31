package com.duoyue.app.ui.adapter.category;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.duoyue.app.bean.CategoryGroupBean;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;

/**
 * 左侧分类组信息Adapter
 * @author caoym
 * @data 2019/4/18  18:59
 */
public class LeftGroupAdapter extends SimpleRecyclerAdapter<CategoryGroupBean>
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#LeftGroupAdapter";

    /**
     * 选中的分类组位置.
     */
    private int mSelectedPosition;

    @Override
    public SimpleViewHolder<CategoryGroupBean> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new LeftGroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_group_layout, parent, false), this);
    }

    /**
     * 设置选中分类组.
     * @param position
     */
    public void setSelectedPosition(int position)
    {
        try
        {
            mListData.get(mSelectedPosition).isSelected = false;
            notifyItemChanged(mSelectedPosition);
            mListData.get(position).isSelected = true;
            notifyItemChanged(position);
            mSelectedPosition = position;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "setSelectedPosition: {}, {}", position, throwable);
        }
    }

    /**
     * 重置选中位置
     */
    public void resetSelectedPosition(int selected){
        mSelectedPosition = selected;
    }
}
