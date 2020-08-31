package com.duoyue.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.utils.GlideUtils;

import java.util.List;

/**
 * 阅读品味分类Adapter
 * @author caoym
 * @data 2019/4/30  11:37
 */
public class GuideCategoryAdapter extends RecyclerView.Adapter
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#GuideCategoryAdapter";

    /**
     * 监听类
     */
    private OnRecyclerViewListener mOnRecyclerViewListener;

    /**
     * 要展示的信息.
     */
    private List<CategoryBean> mCategoryBeanList;

    /**
     * 选中的分类Id列表.
     */
    private List<String> mSelectCategoryIdList;

    private Context mContext;

    /**
     * 构造方法.
     * @param categoryBeanList
     */
    public GuideCategoryAdapter(Context context, List<CategoryBean> categoryBeanList)
    {
        mContext = context;
        mCategoryBeanList = categoryBeanList;
    }

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener)
    {
        this.mOnRecyclerViewListener = onRecyclerViewListener;
    }

    /**
     * 设置要显示的分类数据.
     * @param categoryBeanList
     */
    public void setCategoryBeanList(List<CategoryBean> categoryBeanList, List<String> selectCategoryIdList)
    {
        this.mCategoryBeanList = categoryBeanList;
        mSelectCategoryIdList = selectCategoryIdList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.start_guide_classify_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        try {
            CategoryViewHolder holder = (CategoryViewHolder) viewHolder;
            //设置位置.
            holder.position = position;
            //获取分类数据.
            CategoryBean categoryBean = mCategoryBeanList.get(position);
            //分类图片
            //GlideUtils.INSTANCE.loadSimpleImage(mContext, categoryBean.getImage(), holder.categoryImg);
            GlideUtils.INSTANCE.loadImage(mContext, categoryBean.getImage(), holder.categoryImg, GlideUtils.INSTANCE.getBookRadius());
//            holder.categoryImg.setBackgroundResource(categoryBean.getImage());
            //分类名称
            holder.categoryName.setText(categoryBean.getName());
            //选择CheckBox
            holder.selectCheckbox.setChecked(categoryBean.isSelected());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "onBindViewHolder: {}, {}, {}", viewHolder, position, throwable);
        }
    }

    @Override
    public int getItemCount()
    {
        return mCategoryBeanList != null ? mCategoryBeanList.size() : 0;
    }

    /**
     * 获取指定位置分类数据.
     * @param position
     * @return
     */
    public CategoryBean getItemData(int position)
    {
        return mCategoryBeanList != null && mCategoryBeanList.size() > position ?  mCategoryBeanList.get(position) : null;
    }

    /**
     * 点击监听类
     */
    public interface OnRecyclerViewListener
    {
        void onItemClick(int position, CheckBox checkBox);
    }

    /**
     * ViewHolder
     */
    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        /**
         * 主View
         */
        public View rootView;

        /**
         * 分类图片
         */
        public ImageView categoryImg;

        /**
         * 分类名称
         */
        public TextView categoryName;

        /**
         * 选择CheckBox
         */
        public CheckBox selectCheckbox;

        /**
         * 索引.
         */
        public int position;

        public CategoryViewHolder(View itemView)
        {
            super(itemView);
            //主View.
            rootView = itemView.findViewById(R.id.classify_layout);
            //设置点击事件.
            rootView.setOnClickListener(this);
            //分类图片
            categoryImg = itemView.findViewById(R.id.classify_img);
            //分类名称
            categoryName = itemView.findViewById(R.id.classify_name);
            //选择CheckBox
            selectCheckbox = itemView.findViewById(R.id.select_status_checkbox);
        }
        @Override
        public void onClick(View v)
        {
            if (null != mOnRecyclerViewListener)
            {
                mOnRecyclerViewListener.onItemClick(position, selectCheckbox);
            }
        }
    }
}
