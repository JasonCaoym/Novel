package com.duoyue.app.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duoyue.app.adapter.CategoryRankAdapter;
import com.duoyue.app.adapter.ViewPagerAdapter;
import com.duoyue.app.bean.BookCategoryListBean;
import com.duoyue.app.bean.BookRankCategoryBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.presenter.NewCategoryPresenter;
import com.duoyue.app.ui.view.NewCategoryView;
import com.duoyue.app.ui.view.ScaleTransitionPagerTitleView;
import com.duoyue.app.ui.widget.HXLinePagerIndicator;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.PromptLayoutHelper;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 新的分类界面
 * <p>
 * 1.2.4分类修改为一个新的界面
 */
public class NewCategoryActivity extends BaseActivity implements NewCategoryView {

    /**
     * 全部分类的rankId
     */
    private static final long ALL_CATEGORY_ID = -1;
    public static final String SELECTED = "SELECTED";

    private TextView serchTxt;
    private ViewPager viewpager;
    private List<Fragment> mFragments;
    private List<String> mTitles = Arrays.asList("男生", "女生", "图书");
    private ViewPagerAdapter pagerAdapter;

    private RecyclerView rankRecyclerView;
    private CategoryRankAdapter rankAdapter;
    private FrameLayout promptLayout;  //无数据时显示

    private NewCategoryPresenter newCategoryPresenter;
    protected PromptLayoutHelper mPromptLayoutHelper;

    private List<BookRankCategoryBean> maleList = new ArrayList<>();
    private List<BookRankCategoryBean> femaleList = new ArrayList<>();
    private List<BookRankCategoryBean> bookList = new ArrayList<>();
    //分类选项
    private BookRankCategoryBean allCategoryBean = new BookRankCategoryBean(ALL_CATEGORY_ID, "分类", true);

    /**
     * 男生榜单fragment
     */
    private BookRankFragment maleRankFragment;
    /**
     * 女生榜单fragment
     */
    private BookRankFragment femaleRankFragment;
    /**
     * 男生分类fragment
     */
    private BookCategoryFragment maleCategoryFragment;
    /**
     * 女生分类fragment
     */
    private BookCategoryFragment femaleCategoryFragment;
    /**
     * 图书分类fragment
     */
    private BookCategoryFragment bookCategoryFragment;

    /**
     * 上次榜单ID
     */
    private long lastRankId = ALL_CATEGORY_ID;

    /**
     * 当前选中的RankList的角标
     */
    private int currentRankListIndex = 0;

    /**
     * 当前选中的viewpager的角标
     */
    private int currentCategoryIndex = 0;

    private boolean isFirst = true;

    //    private ImageView imageView;
    private ObjectAnimator animator, objectAnimator;
    private BookSiteBean bookCityAdBean;
    private boolean isRead;
    private AdSiteBean flowAdSiteBean;

    private ImageView mTv_back;

    public void setIsRead() {
        if (isRead && bookCityAdBean != null) {
            isRead = false;
            FuncPageStatsApi.floatAdExpose(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.CATEGORY, "7 + " + PageNameConstants.CATEGORY + " + " + StartGuideMgr.getChooseSex());
        }
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_new_fragment);
        Intent intent = getIntent();
        if (intent != null) {
            currentCategoryIndex = intent.getIntExtra(SELECTED, -1);
            if (currentCategoryIndex != -1) {
                switch (currentCategoryIndex) {
                    case 3:
                        currentCategoryIndex = 2;
                        break;
                    case 2:
                    case 1:
                        if (StartGuideMgr.getChooseSex() == currentCategoryIndex) {
                            currentCategoryIndex = 0;
                        } else {
                            currentCategoryIndex = 1;
                        }
                        break;
                }
            }
        }
        initView();
        initRankListData();
        setupViewPager();
        initMagicIndex();
//        EventBus.getDefault().register(this);
    }

//    @Override
//    public void onCreateView(Bundle savedInstanceState) {
//        setContentView(R.layout.category_new_fragment);
//        initView();
//        initRankListData();
//        setupViewPager();
//        initMagicIndex();
//        EventBus.getDefault().register(this);
//    }

    private void initView() {
        serchTxt = findView(R.id.search_text);
        viewpager = findView(R.id.category_viewpager);
//        imageView = findView(R.id.recommend_float_button);
//        imageView.setOnClickListener(this);
        serchTxt.setOnClickListener(this);
        promptLayout = findView(R.id.load_prompt_layout);
        mTv_back = findView(R.id.toolbar_back);
        mTv_back.setOnClickListener(this);

        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                currentCategoryIndex = position;

                int currentCategoryId = getCurrentCategoryId();
                BookRankCategoryBean bean = allCategoryBean;

                //左侧榜单列表数据
                if (currentCategoryId == StartGuideMgr.SEX_MAN) {
                    bean = getBookRankCategoryBean(maleList, currentRankListIndex);
                    changeCategoryStatus(bean.getId(), maleList);
                    rankAdapter.setCategoryList(maleList);
                } else if (currentCategoryId == StartGuideMgr.SEX_WOMAN) {
                    bean = getBookRankCategoryBean(femaleList, currentRankListIndex);
                    changeCategoryStatus(bean.getId(), femaleList);
                    rankAdapter.setCategoryList(femaleList);
                } else if (currentCategoryId == StartGuideMgr.BOOK) {
                    bean = getBookRankCategoryBean(bookList, currentRankListIndex);
                    changeCategoryStatus(bean.getId(), bookList);
                    rankAdapter.setCategoryList(bookList);
                }

                if (bean.getId() != ALL_CATEGORY_ID) {
                    //榜单页面
                    // 刷新书籍列表
                    BookRankFragment currFramgnet = (BookRankFragment) pagerAdapter.getFragmentByIndex(position);
                    currFramgnet.setCategoryId(bean.getId(), currentCategoryId);
                }

                //数据统计
                if (currentCategoryId == StartGuideMgr.SEX_WOMAN) {
                    //女生.
                    FunctionStatsApi.cGirlTabClick();
                    FuncPageStatsApi.categoryTabClick(2);
                } else if (currentCategoryId == StartGuideMgr.SEX_MAN) {
                    //男生.
                    FunctionStatsApi.cBoyTabClick();
                    FuncPageStatsApi.categoryTabClick(1);
                } else {
                    //图书
                    FuncPageStatsApi.categoryTabClick(3);
                }
            }
        });
    }

    private void initRankListData() {
        rankRecyclerView = findView(R.id.book_rank_category);
        rankRecyclerView.setLayoutManager(new LinearLayoutManager(NewCategoryActivity.this));
        rankAdapter = new CategoryRankAdapter(NewCategoryActivity.this, new CategoryRankAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view) {
                BookRankCategoryBean categoryBean = (BookRankCategoryBean) view.getTag();
                if (categoryBean != null) {
                    if (categoryBean.getSelected()) {
                        return;
                    }

                    long currentRankId = categoryBean.getId();
                    int currentCategoryId = getCurrentCategoryId();

                    if (currentCategoryId == StartGuideMgr.SEX_MAN) {
                        //男生
                        changeCategoryStatus(currentRankId, maleList);
                        rankAdapter.setCategoryList(maleList);
                        currentRankListIndex = maleList.indexOf(categoryBean);
                    } else if (currentCategoryId == StartGuideMgr.SEX_WOMAN) {
                        //女生
                        changeCategoryStatus(currentRankId, femaleList);
                        rankAdapter.setCategoryList(femaleList);
                        currentRankListIndex = femaleList.indexOf(categoryBean);
                    } else if (currentCategoryId == StartGuideMgr.BOOK) {
                        //图书
                        changeCategoryStatus(currentRankId, bookList);
                        rankAdapter.setCategoryList(bookList);
                        currentRankListIndex = bookList.indexOf(categoryBean);
                    }
                    if (currentRankListIndex == -1) {
                        currentRankListIndex = 0;
                    }

                    if (lastRankId != ALL_CATEGORY_ID && currentRankId != ALL_CATEGORY_ID && pagerAdapter.getCurrentFragment() instanceof BookRankFragment) {
                        //在榜单之间切换，不需要切换fragment类型
                        // 刷新书籍列表
                        BookRankFragment currFramgnet = (BookRankFragment) pagerAdapter.getCurrentFragment();
                        currFramgnet.setCategoryId(currentRankId, currentCategoryId);
                    } else {
                        //需要切换fragment类型
                        setupViewPager();
                    }
                    lastRankId = categoryBean.getId();

                    if (currentRankId != ALL_CATEGORY_ID) {
                        //分类排行榜点击统计
                        FuncPageStatsApi.categoryRankTabClick((int) currentRankId);
                    }
                }
            }
        });
        rankRecyclerView.setAdapter(rankAdapter);

        //初始化左侧榜单列表数据
        newCategoryPresenter = new NewCategoryPresenter(this);
        newCategoryPresenter.getBookListData();

        //悬浮广告
//        newCategoryPresenter.loadSiteData();
    }

    private void initMagicIndex() {
        MagicIndicator magicIndicator = findView(R.id.category_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitles == null ? 0 : mTitles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setPadding(ViewUtils.dp2px(16f), 0, ViewUtils.dp2px(16f), ViewUtils.dp2px(3f));
                simplePagerTitleView.setText(mTitles.get(index));
                simplePagerTitleView.setTextSize(19);
//                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.text_gray_666));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.standard_red_main_color_c1));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index == viewpager.getCurrentItem()) {
                            return;
                        }
                        viewpager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                HXLinePagerIndicator indicator = new HXLinePagerIndicator(context);
                indicator.setRoundRadius(ViewUtils.dp2px(10f));
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineWidth(UIUtil.dip2px(context, 16));
                indicator.setColors(getResources().getColor(R.color.standard_red_main_color_c1));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
        magicIndicator.onPageSelected(currentCategoryIndex);

    }

    private void setupViewPager() {
        if (mFragments != null) {
            viewpager.removeAllViewsInLayout();
            mFragments.clear();
        }
        mFragments = new ArrayList<>();
        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
            mTitles = Arrays.asList("男生", "女生", "图书");
        } else {
            mTitles = Arrays.asList("女生", "男生", "图书");
        }

        long currentRankId = getCurrentRankId();
        int currentCategoryId = getCurrentCategoryId();

        if (currentRankId == ALL_CATEGORY_ID) {
            setupAllCategoryFragment();
        } else {
            setupRankFragment();
        }

        // 第二步：为ViewPager设置适配器
        if (pagerAdapter == null) {
            pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragments, mTitles);
        } else {
            pagerAdapter.setDat(mFragments, mTitles);
        }
        viewpager.setOffscreenPageLimit(mTitles.size());
        viewpager.setAdapter(pagerAdapter);
        viewpager.setCurrentItem(currentCategoryIndex);

        //加载数据
        if (currentRankId != ALL_CATEGORY_ID && pagerAdapter.getCurrentFragment() instanceof BookRankFragment) {
            //避免重复上报数据
            if (lastRankId != ALL_CATEGORY_ID || (isFirst && getCurrentCategoryId() == StartGuideMgr.getChooseSex())) {
                BookRankFragment currFramgnet = (BookRankFragment) pagerAdapter.getCurrentFragment();
                currFramgnet.setCategoryId(currentRankId, currentCategoryId);
            }
            isFirst = false;
        }
    }

//    private NewCategoryPresenter.onScollListener onScollListener = new NewCategoryPresenter.onScollListener() {
//        @Override
//        public void onStartScoll() {
//            if (objectAnimator != null) {
//                objectAnimator.cancel();
//            }
//            animator = ObjectAnimator.ofFloat(imageView, "translationX", imageView.getTranslationX(), 400f);
//            animator.setDuration(200);
//            animator.start();
//        }
//
//        @Override
//        public void onStopScoll() {
//            //防止重复跳动
//            if (animator != null) {
//                animator.cancel();
//            }
//            objectAnimator = ObjectAnimator.ofFloat(imageView, "translationX", imageView.getTranslationX(), 0f);
//            objectAnimator.setDuration(600);
//            objectAnimator.start();
//        }
//    };

    /**
     * 设置全部分类的fragment
     */
    private void setupAllCategoryFragment() {

        //男生
        if (maleCategoryFragment == null) {
            maleCategoryFragment = new BookCategoryFragment();
//            maleCategoryFragment.setOnScollListener(onScollListener);
            Bundle bundle = new Bundle();
            bundle.putInt("type", StartGuideMgr.SEX_MAN);
            maleCategoryFragment.setArguments(bundle);
        }

        //女生
        if (femaleCategoryFragment == null) {
            femaleCategoryFragment = new BookCategoryFragment();
//            femaleCategoryFragment.setOnScollListener(onScollListener);
            Bundle bundle = new Bundle();
            bundle.putInt("type", StartGuideMgr.SEX_WOMAN);
            femaleCategoryFragment.setArguments(bundle);
        }

        //图书
        if (bookCategoryFragment == null) {
            bookCategoryFragment = new BookCategoryFragment();
//            bookCategoryFragment.setOnScollListener(onScollListener);
            Bundle bundle = new Bundle();
            bundle.putInt("type", StartGuideMgr.BOOK);
            bookCategoryFragment.setArguments(bundle);
        }

        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
            mFragments.add(maleCategoryFragment);
            mFragments.add(femaleCategoryFragment);
        } else {
            mFragments.add(femaleCategoryFragment);
            mFragments.add(maleCategoryFragment);
        }
        mFragments.add(bookCategoryFragment);
    }

    /**
     * 设置榜单分类的fragment
     */
    private void setupRankFragment() {
        //男生
        if (maleRankFragment == null) {
            maleRankFragment = new BookRankFragment();
//            maleRankFragment.setOnScollListener(onScollListener);
            Bundle maleBundle = new Bundle();
            maleBundle.putBoolean("is_male", true);
            maleRankFragment.setArguments(maleBundle);
            maleRankFragment.setCategoryPresenter(newCategoryPresenter);
        }
        //女生
        if (femaleRankFragment == null) {
            femaleRankFragment = new BookRankFragment();
//            femaleRankFragment.setOnScollListener(onScollListener);
            Bundle maleBundle = new Bundle();
            maleBundle.putBoolean("is_male", false);
            femaleRankFragment.setArguments(maleBundle);
            femaleRankFragment.setCategoryPresenter(newCategoryPresenter);
        }

        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
            mFragments.add(maleRankFragment);
            mFragments.add(femaleRankFragment);
        } else {
            mFragments.add(femaleRankFragment);
            mFragments.add(maleRankFragment);
        }
        //图书
        mFragments.add(bookCategoryFragment);

    }


    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    /**
     * 阅读品味设置.
     *
     * @param event
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void readingTasteEvent(ReadingTasteEvent event) {
//        isRead = true;
//        // 重新初始化数据
//        setupViewPager();
//        initMagicIndex();
    //切换阅读口味需要切换广告位
//        newCategoryPresenter.loadSiteData();
//    }

    /**
     * 登录成功.
     */
    public void onLoginSucc() {
        // 重新初始化数据
        setupViewPager();
        initMagicIndex();
    }

    public void resumeByHomePressed() {
        FuncPageStatsApi.categoryShow(2);
        Logger.e("app#", "分类--从桌面启动");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.search_text) {
            //进入搜索页面.
            ActivityHelper.INSTANCE.gotoSearch(PageNameConstants.CATEGORY);
            //搜索入口.
//            FunctionStatsApi.cSearchClick();
            FuncPageStatsApi.categorySearch();
        } /*else if (v.getId() == R.id.recommend_float_button) {
            if (bookCityAdBean.getSuspensionSite().getType() == 1) { // 详情
                com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(getActivity(), "" + bookCityAdBean.getSuspensionSite().getBookId(), new BaseData(""),
                        PageNameConstants.BOOK_CITY, 17, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.CATEGORY + " + " + StartGuideMgr.getChooseSex());
            } else if (bookCityAdBean.getSuspensionSite().getType() == 3 && flowAdSiteBean != null) {
                com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoWeb(getActivity(), flowAdSiteBean.getLinkUrl());
                AdHttpUtil.click(flowAdSiteBean);
            } else { // H5
                com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoWeb(getActivity(), bookCityAdBean.getSuspensionSite().getLink());
            }
            //书城悬浮按钮.
            FuncPageStatsApi.floatAdClick(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.CATEGORY, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.CATEGORY + " + " + StartGuideMgr.getChooseSex());
        } */ else if (v.getId() == R.id.toolbar_back) {
            finish();
        }
    }

    /**
     * 获取当前Tab名称
     *
     * @return
     */
//    @Override
//    public String getPageName() {
//        return ViewUtils.getString(R.string.tab_category);
//    }
//
//    @Override
//    public List<Fragment> getChildFragment() {
//        return mFragments;
//    }
//
//    public boolean hasDrawed() {
//        if (!getChildFragment().isEmpty()) {
//            Fragment recomFragment = getChildFragment().get(0);
//            if (recomFragment != null) {
//                if (recomFragment instanceof BookCategoryFragment) {
//                    return ((BookCategoryFragment) recomFragment).hasDrawed();
//                }
//            }
//        }
//        return false;
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销EventBus.
//        EventBus.getDefault().unregister(this);
        //注销EventBus.
    }

    @Override
    public void showLoading() {
        getPromptLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
        getPromptLayoutHelper().hideLoading();
    }

    @Override
    public void showNetworkError() {
        List<BookCategoryListBean> categoryLeft = DataCacheManager.getInstance().getCategoryLeft();
        if (categoryLeft != null && categoryLeft.size() > 0) {//有缓存
            updateCategory(categoryLeft.get(0), categoryLeft.get(1));
        } else {
            getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newCategoryPresenter.getBookListData();
                    if (maleCategoryFragment != null) {
                        maleCategoryFragment.loadCategoryData();
                    }
                    if (femaleCategoryFragment != null) {
                        femaleCategoryFragment.loadCategoryData();
                    }
                    if (bookCategoryFragment != null) {
                        bookCategoryFragment.loadCategoryData();
                    }
                }
            });
        }
    }

    @Override
    public void updateCategory(BookCategoryListBean maleBean, BookCategoryListBean femaleBean) {
        maleList.clear();
        femaleList.clear();
        bookList.clear();
        maleList.addAll(maleBean.getItems());
        femaleList.addAll(femaleBean.getItems());

        allCategoryBean.setSelected(true);
        maleList.add(0, allCategoryBean);
        femaleList.add(0, allCategoryBean);
        bookList.add(0, allCategoryBean);


        if (currentCategoryIndex != -1) {
            switch (currentCategoryIndex) {
                case 2:
                    rankAdapter.setCategoryList(bookList);
                    break;
                case 1:
                    rankAdapter.setCategoryList(maleList);
                    break;
                case 0:
                    rankAdapter.setCategoryList(femaleList);
                    break;
            }
        } else {
            if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                rankAdapter.setCategoryList(maleList);
            } else if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_WOMAN) {
                rankAdapter.setCategoryList(femaleList);
            }
        }
    }

    @Override
    public void showSite(BookSiteBean bookSiteBean) {
//        if (bookSiteBean.getSuspensionSite().getType() != 3 && bookSiteBean.getSuspensionSite().getIconPath() == null) {
//            imageView.setVisibility(View.GONE);
//        } else {
//            bookCityAdBean = bookSiteBean;
//
//            if (!TextUtils.isEmpty(bookSiteBean.getSuspensionSite().getAdChannalCode())) {
//                flowAdSiteBean = AdConfigManger.getInstance().showAd(getActivity(),
//                        bookSiteBean.getSuspensionSite().getAdChannalCode());
//                if (flowAdSiteBean != null && !TextUtils.isEmpty(flowAdSiteBean.getPicUrl())) {
//                    IAdView adView = AdConfigManger.getInstance().getAdView(getActivity(),
//                            flowAdSiteBean.getChannelCode(), flowAdSiteBean);
//                    if (adView != null) {
//                        imageView.setVisibility(View.VISIBLE);
//                        adView.init(null, imageView, 30, null);
//                        adView.showAd();
//                    } else {
//                        imageView.setVisibility(View.GONE);
//                    }
//                } else {
//                    imageView.setVisibility(View.GONE);
//                }
//            } else {
//                flowAdSiteBean = null;
//                imageView.setVisibility(View.VISIBLE);
//                GlideUtils.INSTANCE.loadImageWidthNoCorner(getActivity(), bookSiteBean.getSuspensionSite().getIconPath(), imageView);
//            }
//
//            if (!isRead) {
//                Log.i("INSTANCE", "loadSiteData: ");
//                FuncPageStatsApi.floatAdExpose(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.CATEGORY, "7 + " + PageNameConstants.CATEGORY + " + " + StartGuideMgr.getChooseSex());
//            }
//        }


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

    /**
     * 获取index对应的BookRankCategoryBean
     *
     * @param beanList
     * @param index
     */
    private BookRankCategoryBean getBookRankCategoryBean(List<BookRankCategoryBean> beanList, int index) {
        if (beanList != null && beanList.size() > index) {
            return beanList.get(index);
        }
        return allCategoryBean;
    }

    /**
     * 获取当前选中的rankId
     */
    private long getCurrentRankId() {

        long currentRankId = ALL_CATEGORY_ID;

        int currentCategoryId = getCurrentCategoryId();
        if (currentCategoryId == StartGuideMgr.SEX_MAN) {
            //男生
            currentRankId = getBookRankCategoryBean(maleList, currentRankListIndex).getId();
        } else if (currentCategoryId == StartGuideMgr.SEX_WOMAN) {
            //女生
            currentRankId = getBookRankCategoryBean(femaleList, currentRankListIndex).getId();
        } else {
            //图书
            currentRankId = getBookRankCategoryBean(bookList, currentRankListIndex).getId();
//            currentRankId = ALL_CATEGORY_ID;
        }

        return currentRankId;
    }

    /**
     * 获取当前选中的categoryId
     */
    private int getCurrentCategoryId() {

        int currentCategoryId = StartGuideMgr.SEX_MAN;

        if (viewpager.getCurrentItem() == 0) {
            if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                currentCategoryId = StartGuideMgr.SEX_MAN;
            } else if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_WOMAN) {
                currentCategoryId = StartGuideMgr.SEX_WOMAN;
            }
        } else if (viewpager.getCurrentItem() == 1) {
            if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                currentCategoryId = StartGuideMgr.SEX_WOMAN;
            } else if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_WOMAN) {
                currentCategoryId = StartGuideMgr.SEX_MAN;
            }
        } else {
            currentCategoryId = StartGuideMgr.BOOK;
        }

        return currentCategoryId;
    }

    @NotNull
    @Override
    public String getCurrPageId() {
        return ViewUtils.getString(R.string.tab_category);
    }
}
