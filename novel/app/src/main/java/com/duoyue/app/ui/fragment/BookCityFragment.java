package com.duoyue.app.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.duoyue.app.adapter.ViewPagerAdapter;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.event.BookCitySearchEvent;
import com.duoyue.app.event.ReadingTasteEvent;
import com.duoyue.app.event.SearchVisiableEvent;
import com.duoyue.app.listener.AppBarStateChangeListener;
import com.duoyue.app.notification.NotificationsUtils;
import com.duoyue.app.ui.view.PagerTitleIndexView;
import com.duoyue.app.ui.widget.HXLinePagerIndicator;
import com.duoyue.app.upgrade.FirstModeUtil;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.presenter.RandomPushBookDialogPresenter;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.SharePreferenceUtils;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookCityFragment extends BaseFragment {

    ViewPager viewpager;
    List<Fragment> mFragments;
    List<String> mTitles = Arrays.asList("精选", "男生", "女生");
    private AppBarLayout appBarLayout;
    private ViewPagerAdapter adapter;
    private int searchHeight;
    private int appBarLayoutffset;
    /**
     * 是否选择性别为女.
     */
    private boolean isSexWoman = false;
    private boolean isFirstUse;
    private Handler mHandler;

    /**
     * 启动后停留书城的时间
     */
    private long times = 0;
    private Task mTask;
    private RandomPushBookDialogPresenter mRandomPushBookDialogPresenter;

    private boolean isRead;


    @Override
    public void onCreateView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.book_city_fragment);
        findView(R.id.toolbar_back).setVisibility(View.GONE);
        findView(R.id.search_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.INSTANCE.gotoSearch(PageNameConstants.BOOK_CITY);
                //书城搜索.
                FunctionStatsApi.bcSearchClick();
                FuncPageStatsApi.bookCitySearchClick();
            }
        });
        appBarLayout = findView(R.id.book_city_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    findView(R.id.view).setVisibility(View.INVISIBLE);

                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    findView(R.id.view).setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                }
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (appBarLayoutffset != 0) {
                        EventBus.getDefault().post(new SearchVisiableEvent(true, verticalOffset));

                    }
                } else {
                    EventBus.getDefault().post(new SearchVisiableEvent(false, verticalOffset));
                }
                appBarLayoutffset = verticalOffset;
            }
        });
//        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        appBarLayout.measure(w, h);
//        searchHeight = appBarLayout.getMeasuredHeight();

        viewpager = findView(R.id.book_city_viewpager);
        EventBus.getDefault().register(this);
//        isFirstUse = SPUtils.INSTANCE.getBoolean(SPUtils.INSTANCE.getSHARED_IS_FIRST_USE(), true);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (FirstModeUtil.getInstance(getActivity()).getMode() == null) {
            setupViewPager();
            initMagicIndex();
            NotificationsUtils.isFirstIn(getContext(), getFragmentManager(), PageNameConstants.BOOK_CITY);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchContentEvent(BookCitySearchEvent event) {
        if (!TextUtils.isEmpty(event.getMsg())) {
            ((TextView) findView(R.id.search_text)).setText(event.getMsg());
        }
    }

    /**
     * 阅读品味设置.
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void readingTasteEvent(ReadingTasteEvent event) {
        // 重新初始化数据
        boolean isFirstUse = SPUtils.INSTANCE.getBoolean(SPUtils.INSTANCE.getSHARED_IS_FIRST_USE(), true);
        if (!isFirstUse) {
            isRead = true;
            setupViewPager();
            initMagicIndex();
            appBarLayout.setExpanded(true, true);
        } else {
            isRead = false;
            setupViewPager();
            initMagicIndex();
            FuncPageStatsApi.bookCityShow(3);
            NotificationsUtils.isFirstIn(getContext(), getFragmentManager(), PageNameConstants.BOOK_CITY);
            randomPushShow();
        }
        FirstModeUtil.getInstance(getActivity()).setMode(null);
        SPUtils.INSTANCE.putBoolean(SPUtils.INSTANCE.getSHARED_IS_FIRST_USE(), false);
    }

    private void initMagicIndex() {
        MagicIndicator magicIndicator = findView(R.id.book_city_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitles == null ? 0 : mTitles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                final SimplePagerTitleView simplePagerTitleView = new PagerTitleIndexView(context);
                simplePagerTitleView.setPadding(ViewUtils.dp2px(25), 0, ViewUtils.dp2px(25), 0);
                simplePagerTitleView.setText(mTitles.get(index));
                simplePagerTitleView.setTextSize(17);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.text_black_333));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.standard_red_main_color_c1));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewpager.getCurrentItem() == index) {
                            return;
                        }
                        viewpager.setCurrentItem(index);
                        if (index == 0) {
                            //点击精选.
                            FunctionStatsApi.bcFeaturedTabClick();
                            if (isSexWoman) {
                                FuncPageStatsApi.bookCityTabClick(2);
                            } else {
                                FuncPageStatsApi.bookCityTabClick(1);
                            }
                        } else if (index == 1) {
                            if (isSexWoman) {
                                //点击女生.
                                FunctionStatsApi.bcGirlTabClick();
                                FuncPageStatsApi.bookCityTabClick(4);
                            } else {
                                //点击男生.
                                FunctionStatsApi.bcBoyTabClick();
                                FuncPageStatsApi.bookCityTabClick(3);
                            }
                        } else {
                            if (!isSexWoman) {
                                //点击女生.
                                FunctionStatsApi.bcGirlTabClick();
                                FuncPageStatsApi.bookCityTabClick(4);
                            } else {
                                //点击男生.
                                FunctionStatsApi.bcBoyTabClick();
                                FuncPageStatsApi.bookCityTabClick(3);
                            }
                        }
                        Glide.get(simplePagerTitleView.getContext()).clearMemory();
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
                Glide.get(context).clearMemory();
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);
    }

    private void setupViewPager() {
        if (mFragments != null) {
            viewpager.removeAllViewsInLayout();
            mFragments.clear();
        }
        mFragments = new ArrayList<>();
        if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
            mTitles = Arrays.asList("精选", "男生", "女生");
            //选择性别男.
            isSexWoman = false;
        } else {
            mTitles = Arrays.asList("精选", "女生", "男生");
            //选择性别女.
            isSexWoman = true;
        }
        for (int i = 0; i < mTitles.size(); i++) {
            Fragment listFragment = new BookRecomFragment();
            Bundle bundle = new Bundle();
            if (i == 0) {
                bundle.putInt("type", 0);
                bundle.putBoolean("isRead", isRead);
            } else if (i == 1) {
                if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                    bundle.putInt("type", 1);
                } else {
                    bundle.putInt("type", 2);
                }
            } else if (i == 2) {
                if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                    bundle.putInt("type", 2);
                } else {
                    bundle.putInt("type", 1);
                }
            }
            listFragment.setArguments(bundle);
            mFragments.add(listFragment);
        }
        // 第二步：为ViewPager设置适配器
        if (adapter == null) {
            adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mFragments, mTitles);
        } else {
            adapter.setDat(mFragments, mTitles);
        }
        viewpager.setOffscreenPageLimit(mTitles.size());
        viewpager.setAdapter(adapter);
    }

    public void setIsRead() {
        if (mFragments != null && !mFragments.isEmpty() && mFragments.get(0) != null) {
            BookRecomFragment bookRecomFragment = (BookRecomFragment) mFragments.get(0);
            if (bookRecomFragment != null) {
                bookRecomFragment.setIsRead();
            }
        }
    }

    /**
     * 登录成功.
     */
    public void onLoginSucc() {
        //重新更新数据
        setupViewPager();
        initMagicIndex();
    }

    /**
     * 获取当前Tab名称
     *
     * @return
     */
    @Override
    public String getPageName() {
        return ViewUtils.getString(R.string.tab_book_city);
    }

    /**
     * 检测制定View是否被遮住显示不全
     *
     * @return
     */
    public boolean isCover(View view) {
        boolean cover = false;
        Rect rect = new Rect();
        cover = view.getGlobalVisibleRect(rect);
        if (cover) {
            if (rect.width() >= view.getMeasuredWidth() && rect.height() >= view.getMeasuredHeight()) {
                return !cover;
            }
        }
        return true;
    }

    public void resumeByHomePressed() {
        FuncPageStatsApi.bookCityShow(2);
        Logger.e("app#", "书城--从桌面启动");
    }

    @Override
    public List<Fragment> getChildFragment() {
        return mFragments;
    }

    public boolean hasDrawed() {
        if (getChildFragment() != null && !getChildFragment().isEmpty()) {
            Fragment recomFragment = getChildFragment().get(0);
            if (recomFragment != null) {
                return ((BookRecomFragment) recomFragment).hasDrawed();
            }
        }
        return false;
    }

    @Override
    public void onFragmentResume(boolean isFirst, boolean isViewDestroyed) {
        super.onFragmentResume(isFirst, isViewDestroyed);
        //第一次进来没选性别,不弹,选完后再开始计时
        boolean isFirstUse = SPUtils.INSTANCE.getBoolean(SPUtils.INSTANCE.getSHARED_IS_FIRST_USE(), true);
        if (isFirstUse) {
            return;
        }
        randomPushShow();

    }

    /**
     * 随机推书
     */
    private void randomPushShow() {
        boolean isShow = SharePreferenceUtils.getBoolean(getContext(), SharePreferenceUtils.IS_IN_DETAIL, false);
        if (isShow) {
            if (mHandler != null && mTask != null) {
                mHandler.removeCallbacks(mTask);
            }
            return;
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mTask == null) {
            mTask = new Task();
        }
        mHandler.postDelayed(mTask, 1000);
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if (mHandler != null && mTask != null) {
            mHandler.removeCallbacks(mTask);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mRandomPushBookDialogPresenter != null) {
            mRandomPushBookDialogPresenter.destroy();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mTask != null) {
            mTask = null;
        }
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            times++;
            int duration = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.RANDOM_PUSH_TIME_BOOK_CITY, 5 * 60);
            if (times < duration) {
                mHandler.postDelayed(this, 1000);
            } else {
                if (PhoneUtil.isNetworkAvailable(getContext())) {
                    if (mRandomPushBookDialogPresenter == null) {
                        mRandomPushBookDialogPresenter = new RandomPushBookDialogPresenter(getActivity(), getFragmentManager(), PageNameConstants.BOOK_CITY_OUT, "");
                    }
                    mRandomPushBookDialogPresenter.loadData(0);
                }
                mHandler.removeCallbacks(mTask);
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
                mTask = null;
//                System.gc();
            }
        }
    }
}
