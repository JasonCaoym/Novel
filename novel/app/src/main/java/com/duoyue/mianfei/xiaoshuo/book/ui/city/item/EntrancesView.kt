//package com.duoyue.mianfei.xiaoshuo.book.ui.city.item
//
//import android.view.View
//import android.widget.TabHost
//import android.widget.TextView
//import com.duoyue.app.bean.BookMenuBean
//import com.duoyue.app.event.TabSwitchEvent
//import com.duoyue.mianfei.xiaoshuo.R
//import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper
//import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity
//import com.duoyue.mod.ad.utils.AdConstants
//import com.duoyue.mod.stats.FuncPageStatsApi
//import com.duoyue.mod.stats.FunctionStatsApi
//import com.duoyue.mod.stats.common.PageNameConstants
//import com.zydm.base.statistics.umeng.StatisHelper
//import com.zydm.base.ui.item.AbsItemView
//import com.zydm.base.utils.ViewUtils
//import kotlinx.android.synthetic.main.book_city_entrances_layout.view.*
//import org.greenrobot.eventbus.EventBus
//
//class EntrancesView: AbsItemView<BookMenuBean>() {
//
//    override fun onCreate() {
//        setContentView(R.layout.book_city_entrances_layout)
//        mItemView.entrances_category.setOnClickListener(this)
//        mItemView.entrances_complete.setOnClickListener(this)
//        mItemView.entrances_rank.setOnClickListener(this)
//        mItemView.entrances_select.setOnClickListener(this)
//        mItemView.entrances_new.setOnClickListener(this)
//    }
//
//    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
//
//    }
//
//    override fun onClick(view: View) {
//        super.onClick(view)
//
//        if (noDoubleListener()){
//            if (view is TextView) {
//                StatisHelper.onEvent().quickClick(view.text.toString())
//            }
//
//            when(view.id) {
//                R.id.entrances_category ->
//                {
//                    EventBus.getDefault().post(TabSwitchEvent(HomeActivity.CATEGORY))
//                    //点击分类.
//                    FunctionStatsApi.bcCategoryIconClick()
//                    FuncPageStatsApi.bookCityIconClick(5)
//                }
//                R.id.entrances_complete -> {
//                    ActivityHelper.gotoBookList(mActivity, ViewUtils.getString(R.string.entrances_complete), 3,
//                        AdConstants.Position.BOOK_FINISH, PageNameConstants.BOOKSTORE_FINISH)
//                    //点击完结.
//                    FunctionStatsApi.bcCompleteIconClick()
//                    FuncPageStatsApi.bookCityIconClick(4)
//                }
//                R.id.entrances_rank ->
//                {
//                    ActivityHelper.gotoRank(mActivity)
//                    //点击榜单.
//                    FunctionStatsApi.bcRankIconClick()
//                    FuncPageStatsApi.bookCityIconClick(1)
//                }
//                R.id.entrances_select ->
//                {
//                    ActivityHelper.gotoJingXuan(mActivity)
//                    //点击精选.
//                    FunctionStatsApi.bcFeaturedIconClick()
//                    FuncPageStatsApi.bookCityIconClick(2)
//                }
//                R.id.entrances_new ->
//                {
//                    ActivityHelper.gotoBookList( mActivity, ViewUtils.getString(R.string.entrances_new), 4,
//                        AdConstants.Position.BOOK_NEWS, PageNameConstants.BOOKSTORE_NEW)
//                    //点击榜单.
//                    FunctionStatsApi.bcNewIconClick()
//                    FuncPageStatsApi.bookCityIconClick(3)
//                }
//            }
//        }
//    }
//}
