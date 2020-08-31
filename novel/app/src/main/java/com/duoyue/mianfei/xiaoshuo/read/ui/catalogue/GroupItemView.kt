package com.duoyue.mianfei.xiaoshuo.read.ui.catalogue

import android.util.TypedValue
import com.duoyue.mianfei.xiaoshuo.R
import com.zydm.base.ui.item.AbsItemView
import com.zydm.base.utils.ViewUtils
import kotlinx.android.synthetic.main.group_item_view.view.*

class GroupItemView : AbsItemView<CatalogueActivity.GroupItem>() {

    override fun onCreate() {
        setContentView(R.layout.group_item_view)
        mItemView.setOnClickListener(this)
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
        mItemView.group_name.text = mItemData.name
        if (mItemData.isReadGroup) {
            mItemView.group_name.setTextColor(ViewUtils.getColor(R.color.standard_red_main_color_c1))
            mItemView.group_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f)
        } else {
            mItemView.group_name.setTextColor(ViewUtils.getColor(R.color.standard_black_third_level_color_c5))
            mItemView.group_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
        }
    }
}
