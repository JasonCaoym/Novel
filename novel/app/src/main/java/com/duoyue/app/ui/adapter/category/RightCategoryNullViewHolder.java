package com.duoyue.app.ui.adapter.category;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.zydm.base.data.bean.CategoryBean;

/**
 * 右侧分类ViewHolder
 *
 * @author caoym
 * @data 2019/4/18  18:59
 */
public class RightCategoryNullViewHolder extends SimpleViewHolder<CategoryBean> {

    private View view;

    public RightCategoryNullViewHolder(View itemView, @Nullable SimpleRecyclerAdapter<CategoryBean> adapter) {
        super(itemView, adapter);

        this.view = itemView.findViewById(R.id.view);
    }

    @Override
    protected void refreshView(CategoryBean categoryBean) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.height = categoryBean.getNullHeight();
//        if (categoryBean.getNullHeight() < 0) {
//            lp.bottomMargin += categoryBean.getNullHeight();
//        } else {
//        }
//        lp.bottomMargin =
//        lp.setMargins(0,0,0,categoryBean.getNullHeight());
//        lp.height =  categoryBean.getNullHeight();
//        lp.setMargins(0, 0, 0, categoryBean.getNullHeight());
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, categoryBean.getNullHeight());
//        view.setLayoutParams(layoutParams);
    }
}
