package com.duoyue.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.duoyue.app.adapter.RankCategoryAdapter;
import com.duoyue.app.adapter.ViewPagerAdapter;
import com.duoyue.app.bean.BookCategoryListBean;
import com.duoyue.app.bean.BookRankCategoryBean;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.presenter.BookCategoryPresenter;
import com.duoyue.app.ui.fragment.BookRankFragment;
import com.duoyue.app.ui.view.BookCategoryView;
import com.duoyue.app.ui.view.PagerTitleIndexView;
import com.duoyue.app.ui.widget.HXLinePagerIndicator;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.ViewUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookRankActivity extends BaseActivity implements BookCategoryView {

    private static final String TAG = "app#BookRankActivity";

    public static final String CLASSID = "ClassId";
    public static final String SELECTEDID = "SELECTEDID";
    /**
     * 男生
     */
    public static final int MALE = 1;
    /**
     * 女生
     */
    public static final int FEMALE = 2;

    private ViewPager mViewPager;
    private ViewPagerAdapter pagerAdapter;
    private MagicIndicator magicIndicator;
    private RecyclerView mRecyclerView;
    private RankCategoryAdapter categoryAdapter;
    private BookCategoryPresenter categoryPresenter;
    private List<BookRankCategoryBean> maleList = new ArrayList<>();
    private List<BookRankCategoryBean> femaleList = new ArrayList<>();
    private int curCategoryIndex;
    private List<String> mTabTitle;

    /**
     * 频道(1:男生;2:女生)
     */
    private int mFrequency;

    //书城页面排行榜跳转过来
    private int classid = -1;
    private int mSelect = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_rank_activity);
        Intent intent = getIntent();
        if (intent != null) {
            classid = intent.getIntExtra(CLASSID, -1);
            mSelect = intent.getIntExtra(SELECTEDID, -1);
            Logger.e(TAG, mSelect + "onPageSelected position = " + classid);
        }
        initCategoryView();
        initViewPager();
        initTab();

        categoryPresenter = new BookCategoryPresenter(this);
    }

    private void initCategoryView() {
        mRecyclerView = findViewById(R.id.book_rank_category);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new RankCategoryAdapter(this, new RankCategoryAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                BookRankCategoryBean categoryBean = (BookRankCategoryBean) view.getTag();
                if (categoryBean != null) {
                    if (categoryBean.getSelected()) {
                        return;
                    }
                    long curCategoryId = categoryBean.getId();
                    if (mViewPager.getCurrentItem() == 0) {
                        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {

                            changeCategoryStatus(curCategoryId, maleList);
                            categoryAdapter.setCategoryList(maleList);
                            curCategoryIndex = maleList.indexOf(categoryBean);
                        } else {
                            changeCategoryStatus(curCategoryId, femaleList);
                            categoryAdapter.setCategoryList(femaleList);
                            curCategoryIndex = femaleList.indexOf(categoryBean);
                        }
                    } else {
                        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                            changeCategoryStatus(curCategoryId, femaleList);
                            categoryAdapter.setCategoryList(femaleList);
                            curCategoryIndex = femaleList.indexOf(categoryBean);
                        } else {
                            changeCategoryStatus(curCategoryId, maleList);
                            categoryAdapter.setCategoryList(maleList);
                            curCategoryIndex = maleList.indexOf(categoryBean);
                        }
                    }
                    // 刷新书籍列表
                    BookRankFragment currFramgnet = (BookRankFragment) pagerAdapter.getCurrentFragment();
                    currFramgnet.setCategoryId(curCategoryId, mFrequency);
                    currFramgnet.cleanData();
                    //数据统计
                    FuncPageStatsApi.bookCityRankTabClick((int) curCategoryId);
                }
            }
        });
        mRecyclerView.setAdapter(categoryAdapter);
    }

    private void changeCategoryStatus(long currId, List<BookRankCategoryBean> list) {
        List<BookRankCategoryBean> tmpList = new ArrayList<>(list);
        for (BookRankCategoryBean item : tmpList) {
            if (item.getId() == currId) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }
        list.clear();
        list.addAll(tmpList);
        tmpList.clear();
    }

    public String getCurrPageId() {
        return PageNameConstants.RANK;
    }

    private void initViewPager() {
        magicIndicator = findView(R.id.magic_indicator);
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
                // 滑动到另一个界面后
                if (maleList.size() > 0 && femaleList.size() > 0) {
                    BookRankCategoryBean firstCategory;
                    if (position == 0) {
                        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                            firstCategory = maleList.get(curCategoryIndex);
                            changeCategoryStatus(firstCategory.getId(), maleList);
                            categoryAdapter.setCategoryList(maleList);
                            //选中男生频道.
                            mFrequency = MALE;
                        } else {
                            firstCategory = femaleList.get(curCategoryIndex);
                            changeCategoryStatus(firstCategory.getId(), femaleList);
                            categoryAdapter.setCategoryList(femaleList);
                            //选中女生频道.
                            mFrequency = FEMALE;
                        }
                    } else {
                        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                            firstCategory = femaleList.get(curCategoryIndex);
                            changeCategoryStatus(firstCategory.getId(), femaleList);
                            categoryAdapter.setCategoryList(femaleList);
                            //选中女生频道.
                            mFrequency = FEMALE;
                        } else {
                            firstCategory = maleList.get(curCategoryIndex);
                            changeCategoryStatus(firstCategory.getId(), maleList);
                            categoryAdapter.setCategoryList(maleList);
                            //选中男生频道.
                            mFrequency = MALE;
                        }
                    }
                    // 刷新书籍列表
                    BookRankFragment currFramgnet = (BookRankFragment) pagerAdapter.getFragmentByIndex(position);
                    currFramgnet.setCategoryId(firstCategory.getId(), mFrequency);
                } else {
                    BookRankFragment currFramgnet = (BookRankFragment) pagerAdapter.getFragmentByIndex(position);
                    currFramgnet.setHasCategory(false);
                    currFramgnet.showNetworkError();
                }

            }
        });
        List<BookRankFragment> fragments = new ArrayList<>();

        // 男生
        BookRankFragment maleFragment = new BookRankFragment();
        Bundle maleBundle = new Bundle();
        maleBundle.putBoolean("is_male", true);
        maleFragment.setArguments(maleBundle);
        maleFragment.setCategoryPresenter(categoryPresenter);
        // 女生
        Bundle femaleBundle = new Bundle();
        BookRankFragment femaleFragment = new BookRankFragment();
        femaleBundle.putBoolean("is_male", false);
        femaleFragment.setArguments(femaleBundle);
        femaleFragment.setCategoryPresenter(categoryPresenter);

        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
            mFrequency = MALE;
            mTabTitle = Arrays.asList("男生", "女生");
            fragments.add(maleFragment);
            fragments.add(femaleFragment);
        } else {
            mFrequency = FEMALE;
            mTabTitle = Arrays.asList("女生", "男生");
            fragments.add(femaleFragment);
            fragments.add(maleFragment);
        }

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, mTabTitle);
        mViewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void initActivityConfig(ActivityConfig activityConfig) {
        super.initActivityConfig(activityConfig);
        activityConfig.isStPage = false;
    }

    private void initTab() {
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTabTitle == null ? 0 : mTabTitle.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new PagerTitleIndexView(context);
                simplePagerTitleView.setPadding(ViewUtils.dp2px(25), 0, ViewUtils.dp2px(25), 0);
                simplePagerTitleView.setText(mTabTitle.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.text_black_333));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.standard_red_main_color_c1));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                HXLinePagerIndicator indicator = new HXLinePagerIndicator(context);
                indicator.setRoundRadius(ViewUtils.dp2px(10f));
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setColors(getResources().getColor(R.color.standard_red_main_color_c1));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showError() {
        BookRankFragment currFramgnet = (BookRankFragment) pagerAdapter.getCurrentFragment();
        currFramgnet.setHasCategory(false);
        currFramgnet.showNetworkError();
    }

    @Override
    public void updateCategory(BookCategoryListBean maleBean, BookCategoryListBean femaleBean) {
        maleList.clear();
        femaleList.clear();
        maleList.addAll(maleBean.getItems());
        femaleList.addAll(femaleBean.getItems());

        long curCategoryId;
        curCategoryIndex = 0;


        if (mSelect != -1 && mSelect != StartGuideMgr.getChooseSex()) {
            if (mSelect == StartGuideMgr.SEX_MAN) {
                mFrequency = 2;
                mViewPager.setCurrentItem(mFrequency);
                curCategoryId = classid;
                categoryAdapter.setCategoryList(maleList);
                changeCategoryStatus(classid, maleList);
            } else {
                mFrequency = 1;
                mViewPager.setCurrentItem(mFrequency);
                curCategoryId = classid;
                categoryAdapter.setCategoryList(femaleList);
                changeCategoryStatus(classid, femaleList);
            }

        } else {
            if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                mFrequency = 1;
                curCategoryId = classid != -1 ? classid : maleList.get(0).getId();
                categoryAdapter.setCategoryList(maleList);
                if (classid != -1) {
                    changeCategoryStatus(classid, maleList);
                }
            } else {
                mFrequency = 2;
                curCategoryId = classid != -1 ? classid : femaleList.get(0).getId();
                categoryAdapter.setCategoryList(femaleList);
                if (classid != -1) {
                    changeCategoryStatus(classid, femaleList);
                }
            }
        }
        ((BookRankFragment) pagerAdapter.getCurrentFragment()).setCategoryId(curCategoryId, mFrequency);
    }
}
