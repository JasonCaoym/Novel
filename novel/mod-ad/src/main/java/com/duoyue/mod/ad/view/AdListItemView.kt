package com.duoyue.mod.ad.view

import android.view.ViewGroup
import com.zydm.base.ui.item.AbsItemView
import com.zzdm.ad.R
import kotlinx.android.synthetic.main.ad_list_item.view.*

class AdListItemView: AbsItemView<ListItemCommAd>() {

    override fun onCreate() {
        setContentView(R.layout.ad_list_item)
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
        val adView = mItemData.mAd.getAdView()!!
        val container = mItemView.ad_layout

        if (container.getChildCount() > 0 && container.getChildAt(0) === adView) {
            return
        }

        if (container.getChildCount() > 0) {
            container.removeAllViews()
        }

        if (adView.getParent() != null) {
            (adView.getParent() as ViewGroup).removeView(adView)
        }
        mItemData.mAd.registerViewForInteraction(container, adView, adView)
        container.addView(adView)
        mItemData.mAd.render() // 调用render方法后sdk才会开始展示广告


    }
}
