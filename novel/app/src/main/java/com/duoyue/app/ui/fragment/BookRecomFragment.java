package com.duoyue.app.ui.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.andview.refreshview.XRefreshView;
import com.bumptech.glide.Glide;
import com.duoyue.app.bean.BookBannerAdBean;
import com.duoyue.app.bean.BookBannerListBean;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookCityListBean;
import com.duoyue.app.bean.BookCityMenuBean;
import com.duoyue.app.bean.BookPeopleNewBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.bean.LastOneItemBean;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.event.BookCitySearchEvent;
import com.duoyue.app.event.SearchVisiableEvent;
import com.duoyue.app.presenter.BookSubfieldPresenter;
import com.duoyue.app.ui.view.BookBannerView;
import com.duoyue.app.ui.view.BookCityItemView;
import com.duoyue.app.ui.view.BookNewPersonGiftBagView;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.app.ui.view.EntrancesV2View;
import com.duoyue.app.ui.view.FixedMoreV2View;
import com.duoyue.app.ui.view.FixedOne2DoubleView;
import com.duoyue.app.ui.view.FixedOne2FourV2View;
import com.duoyue.app.ui.view.FixedSixView;
import com.duoyue.app.ui.view.FixedThreeView;
import com.duoyue.app.ui.view.LastOneItemView;
import com.duoyue.app.ui.view.RankingBooksListView;
import com.duoyue.app.ui.view.XCustomBanner;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.platform.IAdView;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.duoyue.mod.stats.common.upload.PageStatsUploadMgr;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.RecyclerAdapter;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.SmileyHeaderView;
import com.zydm.base.widgets.refreshview.XRefreshViewFooter;
import com.zzdm.ad.router.BaseData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BookRecomFragment extends BaseFragment implements BookPageView {

    private static final String TAG = "APP#BookRecomFragment";

    /**
     * 0：精选
     * 1：男生
     * 2：女生
     */
    private int type;
    private RecyclerAdapter mAdapter;
    private List<Object> bookList = new ArrayList<>();
    private final List<Object> tempBookList = new ArrayList<>();
    private volatile BookSubfieldPresenter presenter;
    protected PromptLayoutHelper mPromptLayoutHelper;
    private ImageView floatButton;
    //    private PullableRecyclerView listView;
    private RecyclerView recyclerView;
    //    private PullToRefreshLayout refreshLayout;
    private XRefreshView xRefreshView;
    private int maxBottomMargin;
    private int minBottomMargin;
    private int pageNum = 1;
    private boolean hasDrawed;
    /**
     * 获取标题.
     */
    private String mTitle;

    private BookSiteBean bookCityAdBean;
    private ObjectAnimator animator, objectAnimator;

    private SparseArray<Integer> sparseArray = new SparseArray<>();
    private LinearLayoutManager linearLayoutManager;
    private int count = 0;
    private int num = 0;
    private int mType;
    //轮播图view 和数据
    private XCustomBanner bannerView;
    private BookBannerListBean listBean;

    private boolean isRead;
    private boolean sIsScrolling;
    private AdSiteBean flowAdSiteBean;
    private XRefreshViewFooter xRefreshViewFooter;

    private SmileyHeaderView smileyHeaderView;

    public void setIsRead() {
        if (type == 0 && isRead && bookCityAdBean != null) {
            isRead = false;
            FuncPageStatsApi.floatAdExpose(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, mType, PageNameConstants.BOOK_CITY, "7 + " + PageNameConstants.BOOK_CITY + " + " + mType + " + " + StartGuideMgr.getChooseSex());
        }
    }

    /**
     * 获取Adapter
     * .putItemClass(ReadTasteView.class)
     *
     *
     * @return
     */
    private RecyclerAdapter getAdapter() {
        return new AdapterBuilder()
                .putItemClass(BookBannerView.class)
                .putItemClass(EntrancesV2View.class)
                .putItemClass(BookNewPersonGiftBagView.class)
                .putItemClass(FixedOne2FourV2View.class)
                .putItemClass(FixedOne2DoubleView.class)
                .putItemClass(FixedThreeView.class)
                .putItemClass(FixedMoreV2View.class)
                .putItemClass(FixedSixView.class)
                .putItemClass(RankingBooksListView.class)
                .putItemClass(BookCityItemView.class)
                .putItemClass(LastOneItemView.class)
                .builderRecyclerAdapter(getActivity());
    }


    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            type = args.getInt("type", 0);
            isRead = args.getBoolean("isRead");
            switch (type) {
                case 0:
                    if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                        mType = 1;
                    } else {
                        mType = 2;
                    }
                    break;
                case 1:
                    mType = 3;
                    break;
                case 2:
                    mType = 4;
                    break;
            }
        }
    }

    private XRefreshView.SimpleXRefreshListener xRefreshListener = new XRefreshView.SimpleXRefreshListener() {

        @Override
        public void onRefresh(boolean isPullDown) {
            onPullRefresh();
        }

        @Override
        public void onLoadMore(boolean isSilence) {
            if (tempBookList.size() > num) {
                int toIndex = num;
                int fromIndex = num += 3;
                if (fromIndex > tempBookList.size()) {
                    bookList.addAll(tempBookList.subList(toIndex, tempBookList.size()));
                } else {
                    bookList.addAll(tempBookList.subList(toIndex, fromIndex));
                }
                xRefreshView.stopLoadMore();
                mAdapter.notifyItemChangedSafe(bookList, bookList.size());
            } else {
                presenter.loadMoreData(pageNum);
                ++pageNum;
            }
        }

        @Override
        public void onRelease(float direction) {
            super.onRelease(direction);

            Log.i(TAG, "onRelease: 手放开");
        }
    };

    private void initViews() {
        EventBus.getDefault().register(this);

        xRefreshView = findView(R.id.pull_layout);
//        refreshLayout.setCanPullUp(true);
//        refreshLayout.setCanPullDown(true);
        recyclerView = findView(R.id.recommend_listview);
        recyclerView.setHasFixedSize(true);
        //注意 上下滚动
//        recyclerView.setNestedScrollingEnabled(true);
        smileyHeaderView = new SmileyHeaderView(getActivity());
        xRefreshView.setCustomHeaderView(smileyHeaderView);
//        xRefreshView.setAutoLoadMore(true);
        xRefreshViewFooter = new XRefreshViewFooter(getActivity());
        xRefreshView.setXRefreshViewListener(xRefreshListener);
//        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
//        xRefreshView.setPullLoadEnable(true);
//        xRefreshView.setAutoLoadMore(true);
        xRefreshView.setCustomFooterView(xRefreshViewFooter);
//        xRefreshView.enableReleaseToLoadMore(true);
//        xRefreshView.enableRecyclerViewPullUp(false);
//        xRefreshView.enablePullUpWhenLoadCompleted(true);
        xRefreshView.setPinnedContent(true);
        xRefreshView.setSilenceLoadMore(true);
//        xRefreshView.setMoveForHorizontal(true);
//        xRefreshView.setPullLoadEnable(true);
//        xRefreshView.setAutoLoadMore(true);
//        xRefreshView.enableReleaseToLoadMore(false);
//        xRefreshView.enableRecyclerViewPullUp(true);
//        xRefreshView.enablePullUpWhenLoadCompleted(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView view, int scrollState) {
                super.onScrollStateChanged(view, scrollState);

                if (view.getChildCount() <= 0) {
                    return;
                }
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) view.getLayoutManager();
//                if (linearLayoutManager!=null){
//                    int first = linearLayoutManager.findFirstVisibleItemPosition();
//                    int count = view.getChildCount();
//                    boolean b = first + count > linearLayoutManager.getItemCount();
//                }

                //判断是否为停止滑动状态
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (sIsScrolling) {
                        Glide.with(BookRecomFragment.this).resumeRequests();
                    }

                    if (bannerView != null) bannerView.startAutoPlay();
                    //防止重复跳动

                    if (floatButton != null && floatButton.getVisibility() == View.VISIBLE) {
                        if (animator != null && animator.isRunning()) {
                            animator.cancel();
                        }
                        objectAnimator = ObjectAnimator.ofFloat(floatButton, "translationX", floatButton.getTranslationX(), 0f);
                        objectAnimator.setDuration(600);
                        objectAnimator.start();
                    }

                } else if (scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    sIsScrolling = true;
                    Glide.with(BookRecomFragment.this).pauseRequests();
                    if (bannerView != null) bannerView.stopAutoPlay();
                    //防止重复跳动
                    if (floatButton != null && floatButton.getVisibility() == View.VISIBLE) {
                        if (objectAnimator != null && objectAnimator.isRunning()) {
                            objectAnimator.cancel();
                        }
                        animator = ObjectAnimator.ofFloat(floatButton, "translationX", floatButton.getTranslationX(), 400f);
                        animator.setDuration(200);
                        animator.start();
                    }

                } else if (scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    if (bannerView != null) bannerView.stopAutoPlay();
                    sIsScrolling = true;
                    Glide.with(BookRecomFragment.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);

                if (bookList.isEmpty()) return;
                //防止接口返回数据不准备
                if (linearLayoutManager.findLastVisibleItemPosition() >= bookList.size()) return;
                // 判断最后一个栏目不为空
                if (bookList.get(linearLayoutManager.findLastVisibleItemPosition()) == null) return;
                //加载更多属于猜你喜欢 不需要重复上报  阅读口味不属于分栏 不需要上报id
                if (bookList.get(linearLayoutManager.findLastVisibleItemPosition()) instanceof BookCityItemBean)
                    return;
                //曝光过的数据 就无须再次上报  除非刷新数据  切换阅读口味
                if (sparseArray.get(linearLayoutManager.findLastVisibleItemPosition()) == null) {
                    sparseArray.append(linearLayoutManager.findLastVisibleItemPosition(), count);
                    PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(++count, "", "BOOKSTORE", String.valueOf(mType), "EPFP", "");
                }
                //统计书籍曝光需要.
                try {
                    view.requestLayout();
                } catch (Throwable throwable) {
                    Logger.e(TAG, "onScroll: {}", throwable);
                }

            }
        });
//        refreshLayout.setOnRefreshListener(onRefreshListener);
        floatButton = findView(R.id.recommend_float_button);
        floatButton.setOnClickListener(onClickListener);
        //获取标题.
        if (type == 1) {
            //男生
            mTitle = ViewUtils.getString(R.string.male);
//            refreshLayout.setCanPullUp(false);

        } else if (type == 2) {
            //女生
            mTitle = ViewUtils.getString(R.string.female);
//            refreshLayout.setCanPullUp(false);

        } else {
            //精选
            mTitle = ViewUtils.getString(R.string.featured);
//            refreshLayout.setCanPullUp(true);
        }
        xRefreshView.setPullLoadEnable(type == 0);
    }

    private void initData() {
        mAdapter = getAdapter();
        //添加页面Id.
        mAdapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.PAGE_ID_CITY + type);
        mAdapter.addExtParam(BookExposureMgr.PAGE_CHANNEL, String.valueOf(mType));
        recyclerView.setAdapter(mAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onCreateView(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setContentView(R.layout.book_city_recom_fragment);
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initData();
    }


    @Override
    public String getPageName() {
        return ViewUtils.getString(R.string.tab_book_city) + "-" + mTitle;
    }


    //    private PullToRefreshLayout.OnRefreshListener onRefreshListener = new PullToRefreshLayout.OnRefreshListener() {
//        @Override
//        public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
////            if (type == 0) refreshLayout.setCanPullUp(true);
//            onPullRefresh();
//        }
//
//        @Override
//        public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
//            if (tempBookList.size() > num) {
//                int toIndex = num;
//                int fromIndex = num += 3;
//                if (fromIndex > tempBookList.size()) {
//                    bookList.addAll(tempBookList.subList(toIndex, tempBookList.size()));
//                } else {
//                    bookList.addAll(tempBookList.subList(toIndex, fromIndex));
//                }
//                refreshLayout.loadMoreFinish(LoadResult.LOAD_MORE_SUCCEED);
//                mAdapter.notifyItemChangedSafe(bookList, bookList.size());
//            } else {
//                presenter.loadMoreData(pageNum);
//                ++pageNum;
//            }
//
//        }
//    };
    //悬浮按钮点击
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (bookCityAdBean == null) return;
            if (bookCityAdBean.getSuspensionSite().getType() == 1) { // 详情
                ActivityHelper.INSTANCE.gotoBookDetails(getActivity(), "" + bookCityAdBean.getSuspensionSite().getBookId(), new BaseData(""),
                        PageNameConstants.BOOK_CITY, 17, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.BOOK_CITY + " + " + mType + " + " + StartGuideMgr.getChooseSex());
            } else if (bookCityAdBean.getSuspensionSite().getType() == 3 && flowAdSiteBean != null) {
                com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoWeb(getActivity(), flowAdSiteBean.getLinkUrl());
                AdHttpUtil.click(flowAdSiteBean);
            } else { // H5
                ActivityHelper.INSTANCE.gotoWeb(getActivity(), bookCityAdBean.getSuspensionSite().getLink());
            }
            //书城悬浮按钮.
            FuncPageStatsApi.floatAdClick(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, mType, PageNameConstants.BOOK_CITY, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.BOOK_CITY + " + " + mType + " + " + StartGuideMgr.getChooseSex());
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchContentEvent(SearchVisiableEvent event) {
        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            xRefreshView.setEnabled(event.isVisiable());
        } else {
            xRefreshView.setEnabled(true);
        }

//        refreshLayout.setCanPullDown(event.isVisiable());
        XRelativeLayout.LayoutParams params = (XRelativeLayout.LayoutParams) floatButton.getLayoutParams();
        if (maxBottomMargin == 0) {
            maxBottomMargin = params.bottomMargin;
            minBottomMargin = (int) (maxBottomMargin * 36f / 86f); // 本来应该距离底部的距离  36 : 86
        }
        params.bottomMargin = maxBottomMargin + event.getOffset();
        floatButton.setLayoutParams(params);
    }

    protected void onPullRefresh() {
        xRefreshView.setLoadComplete(false);
        tempBookList.clear();
        num = 0;
        listBean = null;
        pageNum = 1;
        sparseArray.clear();
        count = 0;
        presenter.loadBannerAd(true);
        bookList.clear();
        sparseArray.append(linearLayoutManager.findLastVisibleItemPosition(), ++count);
        PageStatsUploadMgr.getInstance().uploadFuncStatsNoNow(count, "", "BOOKSTORE", String.valueOf(mType), "EPFP", "");
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    @Override
    public void showLoading() {
        getPromptLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
        // 获取banner
        if (listBean != null) {
            final View view = recyclerView.getChildAt(bookList.indexOf(listBean));
            if (view != null) {
                bannerView = view.findViewById(R.id.banner);
            }
        }
        getPromptLayoutHelper().hideLoading();
    }

    @Override
    public void showEmpty() {
        hasDrawed = true;
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
    }

    @Override
    public void showNetworkError() {
        List<Object> list = new ArrayList<>();
        BookBannerAdBean bookBannerAdBean = null;
        hasDrawed = true;
        switch (type) {
            case 0://精选
                bookBannerAdBean = DataCacheManager.getInstance().getBookBannerAdBean();
                BookCityListBean jxList = DataCacheManager.getInstance().getJxList();
                if (jxList != null) {
                    List<Object> listJX = presenter.setListData(jxList);
                    list.addAll(listJX);
                }
                break;
            case 1://男
                bookBannerAdBean = DataCacheManager.getInstance().getManBookBannerAdBean();
                BookCityListBean manList = DataCacheManager.getInstance().getManList();
                if (manList != null) {
                    List<Object> listMan = presenter.setListData(manList);
                    list.addAll(listMan);
                }
                break;
            case 2://女
                bookBannerAdBean = DataCacheManager.getInstance().getWomanBookBannerAdBean();
                BookCityListBean womanList = DataCacheManager.getInstance().getWomanList();
                if (womanList != null) {
                    List<Object> listWoman = presenter.setListData(womanList);
                    list.addAll(listWoman);
                }
                break;
        }
        if (bookBannerAdBean != null) {
            showAdPage(bookBannerAdBean, true);
        }

        if (list != null && list.size() > 0) {
            showPage(list);
        } else {
            getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoading();
                    presenter.loadBannerAd(true);
                }
            });
        }
    }

    @Override
    public void showForceUpdateFinish(int result) {
//        if (result == 1) {
//            smileyHeaderView.setFail();
//            xRefreshView.stopRefresh(true);
//        } else {
//            xRefreshView.stopRefresh(true);
//        }
        xRefreshView.stopRefresh(result == 0);
//        refreshLayout.refreshFinish(result);
    }

    @Override
    public void showLoadMoreFinish(int result) {
        //最后一个没有更多了
        if (result == 2) {
            LastOneItemBean lastOneItemBean = new LastOneItemBean();
            bookList.add(lastOneItemBean);
            mAdapter.notifyItemChangedSafe(bookList, bookList.size());
            xRefreshView.setLoadComplete(true);
//            xRefreshView.stopLoadMore();
            recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        } else {
            xRefreshView.stopLoadMore(result == 0);
        }

//        if (type == 0 && result == LoadResult.LOAD_MORE_FAIL_NO_DATA) {
//            refreshLayout.setCanPullUp(false);
//            LastOneItemBean lastOneItemBean = new LastOneItemBean();
//            bookList.add(lastOneItemBean);
//            mAdapter.notifyItemChangedSafe(bookList, bookList.size());
//        }
//        refreshLayout.loadMoreFinish(result);
//        Glide.get(refreshLayout.getContext()).clearMemory();
    }

    @Override
    public void showPage(List<Object> list) {
        hasDrawed = true;
        if (type == 0) {
            tempBookList.addAll(list);
            num = 3;
            bookList.addAll(tempBookList.subList(0, num));
        } else {
            bookList.addAll(list);
        }

        //判断是否为第一页.
        if (pageNum == 1) {
            //清理已记录曝光的书籍列表, 刷新数据重新开始计算.
            BookExposureMgr.refreshBookData(BookExposureMgr.PAGE_ID_CITY + type);
        }
        mAdapter.setData(bookList);

        // 处理搜索提示语
        if (!TextUtils.isEmpty(presenter.getSearchTitle())) {
            EventBus.getDefault().post(new BookCitySearchEvent(presenter.getSearchTitle()));
        }
    }

    @Override
    public void showAdPage(Object adObject, boolean isBanner) {
        if (isBanner) {
            linearLayoutManager.scrollToPosition(0);
            bookList.clear();
        }

        BookBannerAdBean bookBannerAdBean = (BookBannerAdBean) adObject;
        if (bookBannerAdBean == null) return;
        //注意这里是与presneter 数据结匹配  改之前一定要看懂逻辑
        initBannerAndIcons(bookBannerAdBean, isBanner);
    }

    private void initBannerAndIcons(BookBannerAdBean bookBannerAdBean, boolean isBanner) {
        if (isBanner) {
            String isShowBanner = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.BANNER_SHOW);
            if (isShowBanner != null && isShowBanner.equals("0")) {
                if (bookBannerAdBean.getBannerSite() != null) {
                    listBean = new BookBannerListBean();
                    listBean.setList(bookBannerAdBean.getBannerSite());
                    listBean.setType(type);
                    bookList.add(listBean);
                }
            }
            if (bookBannerAdBean.getIconList() != null) {
                BookCityMenuBean bean = new BookCityMenuBean();
                bean.setIconList(bookBannerAdBean.getIconList());
                bean.setType(type);
                bookList.add(bean);
            }
        }


//        if (type == 0 && bookBannerAdBean.getNewUserBagStatuses() != null) {
//            BookPeopleNewBean peopleNewBean = new BookPeopleNewBean();
//            peopleNewBean.setNewUserBagStatuses(bookBannerAdBean.getNewUserBagStatuses());
//            peopleNewBean.setType(type);
//            if (isBanner) {
//                bookList.add(2, peopleNewBean);
//            } else {
//                bookList.set(2, peopleNewBean);
//                listView.getItemAnimator().setChangeDuration(0);
//                mAdapter.notifyItemChangedSafe(bookList, 2);
//            }
//        }
    }

    @Override
    public void showMorePage(List<BookCityItemBean> cityItemBeanList) {
        bookList.addAll(cityItemBeanList);
        mAdapter.notifyItemChangedSafe(bookList, bookList.size());
    }

    @Override
    public void loadSiteData(BookSiteBean bookSiteBean) {
        if (bookSiteBean.getSuspensionSite().getType() != 3 && bookSiteBean.getSuspensionSite().getIconPath() == null) {
            floatButton.setVisibility(View.GONE);
        } else {
            bookCityAdBean = bookSiteBean;
            // 判断是否悬浮广告，是否可现实
            if (!TextUtils.isEmpty(bookSiteBean.getSuspensionSite().getAdChannalCode())) {
                flowAdSiteBean = AdConfigManger.getInstance().showAd(getActivity(),
                        bookSiteBean.getSuspensionSite().getAdChannalCode());
                if (flowAdSiteBean != null && !TextUtils.isEmpty(flowAdSiteBean.getPicUrl())) {
                    IAdView adView = AdConfigManger.getInstance().getAdView(getActivity(),
                            flowAdSiteBean.getChannelCode(), flowAdSiteBean);
                    if (adView != null) {
                        floatButton.setVisibility(View.VISIBLE);
                        adView.init(null, floatButton, 30, null);
                        adView.showAd();
                    } else {
                        floatButton.setVisibility(View.GONE);
                    }
                } else {
                    floatButton.setVisibility(View.GONE);
                }
            } else {
                flowAdSiteBean = null;
                floatButton.setVisibility(View.VISIBLE);
                GlideUtils.INSTANCE.loadImageWidthNoCorner(getActivity(), bookSiteBean.getSuspensionSite().getIconPath(), floatButton);
            }
            if (!isRead) {
                FuncPageStatsApi.floatAdExpose(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, mType, PageNameConstants.BOOK_CITY, "7 + " + PageNameConstants.BOOK_CITY + " + " + mType + " + " + StartGuideMgr.getChooseSex());
            }
        }

    }

    /**
     * Fragment 可见时回调
     *
     * @param isFirst         是否是第一次显示
     * @param isViewDestroyed Fragment中的View是否被回收过。
     *                        存在这种情况:Fragment的View 被回收, 但是Fragment实例仍在.
     */
    @Override
    public void onFragmentResume(boolean isFirst, boolean isViewDestroyed) {
        super.onFragmentResume(isFirst, isViewDestroyed);
        if (isFirst) {
            presenter = new BookSubfieldPresenter(getActivity(), this, type);
            //加载悬浮广告  实时刷新
            presenter.loadBookSiteList(2, type);
        }

        if (bannerView != null) bannerView.startAutoPlay();
        //如果七天礼包进入书城就为空 那个没必要没有回到书城页面再去刷新七天礼包接口
        if (type == 0 && !isFirst) {
            if (bookList != null && !bookList.isEmpty() && bookList.size() >= 2 && bookList.get(2) instanceof BookPeopleNewBean) {
                BookPeopleNewBean bookPeopleNewBean = ((BookPeopleNewBean) bookList.get(2));
                if (bookPeopleNewBean != null && bookPeopleNewBean.getNewUserBagStatuses() != null && !bookPeopleNewBean.getNewUserBagStatuses().isEmpty()) {
                    presenter.loadBannerAd(false);
                }
            }
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        //获取BookBannerView.
        if (bannerView != null) bannerView.stopAutoPlay();
        if (bannerView != null) bannerView.setVisibleStartPaly(false);
    }

    public boolean hasDrawed() {
        return hasDrawed;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        floatButton.setAnimation(null);
        floatButton.clearAnimation();
        EventBus.getDefault().unregister(this);
        if (presenter != null) presenter.onDistory();
        tempBookList.clear();
        bookList.clear();
    }

    public interface Presenter {
        void loadMoreData(int pageNum);

        void loadPageData();

        void loadBannerAd(boolean isBannch);

//        void loadMenuBean();

//        Object getBannerBean();
//
//        BookCityAdBean getAdBean();

        String getSearchTitle();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GlideUtils.INSTANCE.cleanMemory(getActivity());
    }


}
