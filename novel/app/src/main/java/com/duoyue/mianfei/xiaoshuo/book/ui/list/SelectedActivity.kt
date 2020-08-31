package com.duoyue.mianfei.xiaoshuo.book.ui.list

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.duoyue.app.adapter.ViewPagerAdapter
import com.duoyue.app.common.mgr.BookExposureMgr
import com.duoyue.app.common.mgr.StartGuideMgr
import com.duoyue.app.ui.fragment.BookListFragment
import com.duoyue.app.ui.view.PagerTitleIndexView
import com.duoyue.app.ui.widget.HXLinePagerIndicator
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mod.ad.utils.AdConstants
import com.duoyue.mod.stats.common.PageNameConstants
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.ui.item.ListAdapter
import com.zydm.base.utils.ViewUtils
import kotlinx.android.synthetic.main.selected_tab_activity.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import java.util.*

class SelectedActivity: BaseActivity() {

    private var mTabTitle = Arrays.asList("男生", "女生")
    private  var channel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selected_tab_activity)
        channel = intent.getStringExtra(BookExposureMgr.PAGE_CHANNEL)
        initViewPager()
        initTab()
    }

    private fun initTab() {
        val commonNavigator = CommonNavigator(activity)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return if (mTabTitle == null) 0 else mTabTitle.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = PagerTitleIndexView(context)
                simplePagerTitleView.setPadding(ViewUtils.dp2px(25f), 0, ViewUtils.dp2px(25f), 0)
                simplePagerTitleView.text = mTabTitle[index]
                simplePagerTitleView.textSize = 18f
                simplePagerTitleView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                simplePagerTitleView.normalColor = resources.getColor(R.color.text_black_333)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.standard_red_main_color_c1)
                simplePagerTitleView.setOnClickListener { view_pager.setCurrentItem(index) }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = HXLinePagerIndicator(context)
                indicator.roundRadius = ViewUtils.dp2px(10f).toFloat()
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineWidth = UIUtil.dip2px(context, 20.0).toFloat()
                indicator.setColors(resources.getColor(R.color.standard_red_main_color_c1))
                return indicator
            }
        }
        magic_indicator.navigator = commonNavigator
        ViewPagerHelper.bind(magic_indicator, view_pager)
    }

    override fun getCurrPageId(): String {
        return PageNameConstants.BOOKSTORE_SELECT
    }

    private fun initViewPager() {
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                magic_indicator.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                magic_indicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                magic_indicator.onPageSelected(position)
            }
        })

        val fragments = ArrayList<BookListFragment>()

        var maleFragment = BookListFragment()
        //设置标题(精品-男生).
        maleFragment.setTitle(ViewUtils.getString(R.string.entrances_select) + "-" + ViewUtils.getString(R.string.male))
        var maleBundle = Bundle()
        maleBundle.putInt("type", 0)
        maleBundle.putInt("adSite", AdConstants.Position.CHOICENESS)
        maleBundle.putInt(ListAdapter.EXT_KEY_MODEL_ID, 6)
        maleBundle.putString(ListAdapter.EXT_KEY_PARENT_ID, PageNameConstants.BOOKSTORE_SELECT)
        maleBundle.putString(BookExposureMgr.PAGE_CHANNEL, channel)

        maleFragment.arguments = maleBundle

        var femaleFragment = BookListFragment()
        //设置标题(精品-女生).
        femaleFragment.setTitle(ViewUtils.getString(R.string.entrances_select) + "-" + ViewUtils.getString(R.string.female))
        var femaleBundle = Bundle()
        femaleBundle.putInt("type", 1)
        femaleBundle.putInt("adSite", AdConstants.Position.CHOICENESS)
        femaleBundle.putInt(ListAdapter.EXT_KEY_MODEL_ID, 6)
        femaleBundle.putString(ListAdapter.EXT_KEY_PARENT_ID, PageNameConstants.BOOKSTORE_SELECT)
        femaleBundle.putString(BookExposureMgr.PAGE_CHANNEL, channel)
        femaleFragment.arguments = femaleBundle

        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
            mTabTitle = Arrays.asList("男生", "女生")
            fragments.add(maleFragment)
            fragments.add(femaleFragment)
        } else {
            mTabTitle = Arrays.asList("女生", "男生")
            fragments.add(femaleFragment)
            fragments.add(maleFragment)
        }

        view_pager.adapter = ViewPagerAdapter(supportFragmentManager, fragments, mTabTitle)

    }

    override fun initActivityConfig(activityConfig: ActivityConfig) {
        super.initActivityConfig(activityConfig)
        activityConfig.isStPage = false
    }

}