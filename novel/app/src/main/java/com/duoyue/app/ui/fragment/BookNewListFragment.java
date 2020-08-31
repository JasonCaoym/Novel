package com.duoyue.app.ui.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.duoyue.app.bean.BookNewBookInfoBean;
import com.duoyue.app.bean.BookNewHeaderBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.event.ReadingTasteEvent;
import com.duoyue.app.presenter.BookNewListPresenter;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.app.ui.adapter.BookNewListHeaderV2Adapter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.platform.IAdView;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.LoadResult;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;
import com.zydm.base.data.dao.ShelfEvent;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.StringUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableLayoutManager;
import com.zydm.base.widgets.refreshview.PullableRecyclerView;
import com.zzdm.ad.router.BaseData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class BookNewListFragment extends BaseFragment implements BookShelfHelper.ShelfDaoObserver {
    private PullableRecyclerView pullableRecyclerView;
    private PullToRefreshLayout pullToRefreshLayout;

    private BookNewListPresenter bookNewListPresenter;

    //    private RecyclerAdapter recyclerAdapter;
    private BookNewListHeaderV2Adapter bookNewListHeaderV2Adapter;

    private PromptLayoutHelper mPromptLayoutHelper;

    private ImageView floatButton;

    private ObjectAnimator animator, objectAnimator;

    private BookSiteBean bookCityAdBean;

    private boolean isRead;
    private AdSiteBean flowAdSiteBean;

    private final List<BookNewHeaderBean> bookNewHeaderBeans = new ArrayList<>();

    private BookNewBookInfoBean bookNewBookInfoBean;
    private int mNextPage = 1;

    public void setIsRead() {
        if (isRead && bookCityAdBean != null) {
            isRead = false;
            FuncPageStatsApi.floatAdExpose(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.NEARREAD, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.NEARREAD + " + " + StartGuideMgr.getChooseSex());
        }
    }

    //                .putItemClass(BookNewListView.class)
//    private RecyclerAdapter getAdapter() {
//        return new AdapterBuilder()
//                .putItemClass(BookNewListHeaderV2View.class)
//                .builderRecyclerAdapter(getActivity());
//    }

    @Override
    public void onCreateView(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.fragment_new_book_list);

        initViews();

        initData();
        BookShelfHelper.getsInstance().addObserver(this);
        EventBus.getDefault().register(this);
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private void addToBookshelf(final int index) {
        if (bookNewBookInfoBean == null) return;
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return BookShelfPresenter.addFindBookShelf(bookNewBookInfoBean);
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (ReadHistoryMgr.HTTP_OK.equals(s)) {
                    //添加书架成功.
                    StatisHelper.onEvent().subscription(bookNewBookInfoBean.getName(), "发现页加入书架");
                    ToastUtils.showLimited(R.string.add_shelf_success);
                    bookNewListHeaderV2Adapter.notifyItemChanged(index);
                } else {
                    //添加书架失败.
                    ToastUtils.showLimited(s);
                }
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() == null) return;
            switch (v.getId()) {
                case R.id.xll_one:
                case R.id.xll_two:
                    ActivityHelper.INSTANCE.gotoBookDetails(getActivity(), "" + (int) v.getTag(), new BaseData(""),
                            PageNameConstants.NEARREAD, 24, PageNameConstants.NEAR_READ_BOOK);
                    FuncPageStatsApi.nearBookClick((long) (int) v.getTag());
                    break;
                case R.id.tv_join:
                    int index = (Integer) v.getTag();
                    bookNewBookInfoBean = bookNewHeaderBeans.get(index).getBookInfo();
                    addToBookshelf(index);
                    if (bookNewBookInfoBean != null)
                        FuncPageStatsApi.nearAddshelfClick(bookNewBookInfoBean.getBookId());
                    break;

                case R.id.tv_all_read:
                    com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper.INSTANCE.gotoReadForResult(getActivity(), String.valueOf((int) v.getTag()), new BaseData("发现-附近书友进入阅读器"), PageNameConstants.NEARREAD, PageNameConstants.NEAR_READ_BOOK, 999);
                    FuncPageStatsApi.nearGoReadClick((long) (int) v.getTag());
                    break;
            }
        }
    };

    void initData() {
//        recyclerAdapter = getAdapter();
        bookNewListHeaderV2Adapter = new BookNewListHeaderV2Adapter(getActivity(), bookNewHeaderBeans, onClickListener);
        PullableLayoutManager pullableLayoutManager = new PullableLayoutManager(getActivity());
        pullableRecyclerView.setLayoutManager(pullableLayoutManager);
        bookNewListPresenter = new BookNewListPresenter(pageView);
        bookNewListPresenter.loadHeaderData(mNextPage);
        pullableRecyclerView.setAdapter(bookNewListHeaderV2Adapter);
    }

    void initViews() {

        pullToRefreshLayout = findView(R.id.pull_layout);
        floatButton = findView(R.id.recommend_float_button);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookCityAdBean == null) return;
                if (bookCityAdBean.getSuspensionSite().getType() == 1) { // 详情
                    ActivityHelper.INSTANCE.gotoBookDetails(getActivity(), "" + bookCityAdBean.getSuspensionSite().getBookId(), new BaseData(""),
                            PageNameConstants.NEARREAD, 16, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.NEARREAD + " + " + StartGuideMgr.getChooseSex());
                } else if (bookCityAdBean.getSuspensionSite().getType() == 3 && flowAdSiteBean != null) {
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoWeb(getActivity(), flowAdSiteBean.getLinkUrl());
                    AdHttpUtil.click(flowAdSiteBean);
                } else { // H5
                    ActivityHelper.INSTANCE.gotoWeb(getActivity(), bookCityAdBean.getSuspensionSite().getLink());
                }
                //发现页悬浮按钮.
                FuncPageStatsApi.floatAdClick(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.NEARREAD,
                        PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.NEARREAD + " + " + StartGuideMgr.getChooseSex());
            }
        });
        pullToRefreshLayout.setOnRefreshListener(onRefreshListener);
        pullableRecyclerView = findView(R.id.recommend_listview);

        pullableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //防止重复跳动
                    if (animator != null) {
                        animator.cancel();
                    }
                    objectAnimator = ObjectAnimator.ofFloat(floatButton, "translationX", floatButton.getTranslationX(), 0f);
                    objectAnimator.setDuration(600);
                    objectAnimator.start();
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //防止重复跳动
                    if (objectAnimator != null) {
                        objectAnimator.cancel();
                    }
                    animator = ObjectAnimator.ofFloat(floatButton, "translationX", floatButton.getTranslationX(), 400f);
                    animator.setDuration(200);
                    animator.start();
                }
            }
        });


    }

    public void onLoginSucc() {
        if (bookNewListPresenter != null) {
            mNextPage = 1;
            bookNewListPresenter.loadHeaderData(mNextPage);
        }
    }

    /**
     * 阅读品味设置.
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void readingTasteEvent(ReadingTasteEvent event) {
        if (bookNewListPresenter != null) {
            isRead = true;
            pullableRecyclerView.smoothScrollToPosition(0);
            mNextPage = 1;
            bookNewListPresenter.loadHeaderData(mNextPage);
        }
    }

    public void resumeByHomePressed() {
        FuncPageStatsApi.intoDiscover(2);
        Logger.e("app#", "发现--从后台唤起");
    }

    private PullToRefreshLayout.OnRefreshListener onRefreshListener = new PullToRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
            BookExposureMgr.refreshBookData(FunPageStatsConstants.NEAR_READER_EXPOSE);
            pullToRefreshLayout.setCanPullUp(true);
            mNextPage = 1;
            bookNewListPresenter.loadHeaderData(mNextPage);
        }

        @Override
        public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
            mNextPage += 1;
            bookNewListPresenter.loadHeaderData(mNextPage);
        }
    };

    private BookNewListPresenter.PageView pageView = new BookNewListPresenter.PageView() {
        @Override
        public void showLoading() {
            getPromptLayoutHelper().showLoading();
        }

        @Override
        public void dismissLoading() {
            getPromptLayoutHelper().hideLoading();
        }

        @Override
        public void loadViewData(List<BookNewHeaderBean> list, int nextPage) {
            if (mNextPage == 1) {
                bookNewHeaderBeans.clear();
            }
            bookNewHeaderBeans.addAll(list);
            //最后一条
            if (nextPage == -1) {
                BookNewHeaderBean bookNewHeaderBean = new BookNewHeaderBean();
                bookNewHeaderBean.setLastData(true);
                bookNewHeaderBeans.add(bookNewHeaderBean);
                pullToRefreshLayout.setCanPullUp(false);
            }
            bookNewListHeaderV2Adapter.notifyDataSetChanged();
//            recyclerAdapter.setData(list);
        }

        @Override
        public void loadNullData() {
            getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
        }

        @Override
        public void loadError() {
            getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoading();
                    mNextPage = 1;
                    bookNewListPresenter.loadHeaderData(mNextPage);
                }
            });
        }

        @Override
        public void loadRefreshData(int result) {
            pullToRefreshLayout.refreshFinish(result);
        }

        @Override
        public void loadMoreData(int result) {
            pullToRefreshLayout.loadMoreFinish(result);
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
                    FuncPageStatsApi.floatAdExpose(bookCityAdBean.getSuspensionSite().getType() == 1 ? bookCityAdBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.NEARREAD, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.NEARREAD + " + " + StartGuideMgr.getChooseSex());
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().post(new BookListHeaderEvent());
        EventBus.getDefault().unregister(this);
        BookShelfHelper.getsInstance().removeObserver(this);
        if (bookNewListPresenter != null) bookNewListPresenter.onDistory();
    }

    @Override
    public void onShelfChange(@NonNull ShelfEvent event) {
        if (event.mType == ShelfEvent.TYPE_ADD || event.mType == ShelfEvent.TYPE_REMOVE) {
            //获取书架书籍信息.
            BookShelfBean bookShelfBean = !StringFormat.isEmpty(event.mChangeList) ? event.mChangeList.get(0) : null;
            if (bookShelfBean != null && !StringUtils.isEmpty(bookShelfBean.getBookId())) {
                //修改当前按钮为已添加书架状态.
                if (bookNewHeaderBeans.isEmpty()) return;
                for (BookNewHeaderBean bookNewHeaderBean : bookNewHeaderBeans) {
                    BookNewBookInfoBean infoBean = bookNewHeaderBean.getBookInfo();
                    if (infoBean == null) continue;
                    if (bookShelfBean.getBookId().equals(String.valueOf(infoBean.getBookId()))) {
                        bookNewListHeaderV2Adapter.notifyItemChanged(bookNewHeaderBeans.indexOf(bookNewHeaderBean));
                    }
                }
            }
        }
    }
}
