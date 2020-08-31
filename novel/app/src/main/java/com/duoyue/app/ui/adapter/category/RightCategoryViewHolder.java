package com.duoyue.app.ui.adapter.category;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.utils.GlideUtils;

/**
 * 右侧分类ViewHolder
 *
 * @author caoym
 * @data 2019/4/18  18:59
 */
public class RightCategoryViewHolder extends SimpleViewHolder<CategoryBean> {
    /**
     * 分类图片
     */
    public ImageView categoryImage;

    /**
     * 分类名称
     */
    public TextView categoryName;

    /**
     * 分类标签
     */
    public TextView categoryTag;

    //private Typeface typeFace;

    private Context mContext;

    public RightCategoryViewHolder(Context context, View itemView, @Nullable SimpleRecyclerAdapter<CategoryBean> adapter) {
        super(itemView, adapter);
        mContext = context;
        //分类图片
        categoryImage = itemView.findViewById(R.id.category_image_imageview);
        //分类名称
        categoryName = itemView.findViewById(R.id.category_name_textview);
        //分类子标签
        categoryTag = itemView.findViewById(R.id.category_tag_textview);
        //typeFace = TitleTypeface.getTypeFace(context.getApplicationContext());

    }

    @Override
    protected void refreshView(CategoryBean categoryBean) {
        //分类图片
        //GlideUtils.INSTANCE.loadSimpleImage(mContext, categoryBean.getImage(), categoryImage);
        GlideUtils.INSTANCE.loadImage(BaseContext.getContext(), categoryBean.getImage(), categoryImage, GlideUtils.INSTANCE.getBookRadius());
        //分类名称
        categoryName.setText(categoryBean.getName());

        if(!TextUtils.isEmpty(categoryBean.getMtSubCateName())){
            categoryTag.setText(categoryBean.getMtSubCateName());
        }else if(categoryBean.getSubCategories() != null && categoryBean.getSubCategories().size() > 0){
            String tags = "";
            if(categoryBean.getSubCategories().size() == 1){
                tags = categoryBean.getSubCategories().get(0).getName();
            }else {
                tags = categoryBean.getSubCategories().get(0).getName() + "/" + categoryBean.getSubCategories().get(1).getName();
            }
            categoryTag.setText(tags);
        }else{
            categoryTag.setText("");
        }

//        categoryName.setTypeface(typeFace);

    }
}
