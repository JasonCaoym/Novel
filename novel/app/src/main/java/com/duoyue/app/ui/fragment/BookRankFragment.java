package com.duoyue.app.ui.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookCategoryPresenter;
import com.duoyue.app.presenter.BookRankPresenter;
import com.duoyue.app.presenter.NewCategoryPresenter;
import com.duoyue.app.ui.activity.BookRankActivity;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.app.ui.view.RankItemView;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.ui.list.item.TextItemView;
import com.duoyue.mianfei.xiaoshuo.read.utils.ReduceUtils;
import com.duoyue.mod.ad.view.AdListItemView;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableListView;

import java.util.ArrayList;
import java.util.List;

public class BookRankFragment extends BaseFragment implements BookPageView {

    private static final String TAG = "app#BookRankFragment";
    private OnScrollTopListener mOnScrollTopListener;

    private ListAdapter mAdapter;
    private ListView listView;
    private Presenter rankPresenter;
    protected PromptLayoutHelper mPromptLayoutHelper;
    protected PullToRefreshLayout mPullLayout;
    private long categoryId;

    /**
     * 频道(1:男生;2:女生)
     */
    private String mFrequency;
    private int pageIndex = 1;
    private BookCategoryPresenter categoryPresenter;
    private NewCategoryPresenter newCategoryPresenter;
    private List<Object> bookList = new ArrayList<>();
    private boolean hasCategory = true;
    //    private boolean showAd;
    private SparseArray<Integer> integerSparseArray = new SparseArray<>();

    /**
     * 标题.
     */
    private String mTitle;

    /**
     * @return
     */
    private ListAdapter getAdapter() {
        return new AdapterBuilder()
                .putItemClass(RankItemView.class)
                .putItemClass(TextItemView.class)
                .putItemClass(AdListItemView.class)
                .builderListAdapter(getActivity());
    }

    private NewCategoryPresenter.onScollListener listener;

    public void setOnScollListener(NewCategoryPresenter.onScollListener onScollListener) {
        this.listener = onScollListener;
    }

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        View view = setContentView(R.layout.book_list_fragment);

        listView = view.findViewById(R.id.list_view);
        mAdapter = getAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView p0, int p1, int p2, int p3) {
                if (isVisibleToUser() && listView instanceof PullableListView && ((PullableListView) listView).isReadyForPullDown()) {
                    if (mOnScrollTopListener != null) {
                        mOnScrollTopListener.onScrollTop();
                    }
                }
                //统计书籍曝光需要.
                try {
                    listView.requestLayout();
                } catch (Throwable throwable) {
                    Logger.e(TAG, "onScroll: {}", throwable);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView p0, int p1) {
                if (p1 == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (listener != null) {
                        listener.onStopScoll();
                    }

                } else if (p1 == AbsListView.OnScrollListener.SCROLL_STATE_FLING || p1 == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (listener != null) {
                        listener.onStartScoll();
                    }
                }

            }
        });
        boolean isMale = getArguments().getBoolean("is_male");
        mTitle = ViewUtils.getString(isMale ? R.string.male : R.string.female);
        initPullLayout();
        rankPresenter = new BookRankPresenter(getActivity(), this);
    }

    public void setCategoryPresenter(BookCategoryPresenter presenter) {
        this.categoryPresenter = presenter;
    }

    public void setCategoryPresenter(NewCategoryPresenter presenter) {
        this.newCategoryPresenter = presenter;
    }

    public void setCategoryId(long categoryId, int frequency) {
        this.categoryId = categoryId;
        mFrequency = String.valueOf(frequency);
        pageIndex = 1;
        bookList.clear();
        rankPresenter.loadPageData(categoryId, pageIndex, false);
//        listView.smoothScrollToPosition(0);
        listView.setSelection(0);
    }

    @Override
    public String getPageName() {
        return ViewUtils.getString(R.string.entrances_rank) + "-" + mTitle;
    }

    @Override
    public void showLoading() {
        getPromptLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
        getPromptLayoutHelper().hide();
    }

    @Override
    public void showEmpty() {
        bookList.clear();
        categoryId = 1;
        mFrequency = "0";
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
    }

    @Override
    public void showNetworkError() {
        bookList.clear();
        categoryId = 1;
        mFrequency = "0";
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showLoading();
                if (!hasCategory) {
                    if (categoryPresenter != null) {
                        categoryPresenter.getBookListData();
                    }

                    if (newCategoryPresenter != null) {
                        newCategoryPresenter.getBookListData();
                    }

                } else {
                    if (rankPresenter != null) {
                        pageIndex = 1;
                        rankPresenter.loadPageData(categoryId, pageIndex, false);
                    }
                }
            }
        });
    }

    @Override
    public void showForceUpdateFinish(int result) {
        mPullLayout.refreshFinish(result);
    }

    @Override
    public void showLoadMoreFinish(int result) {
        mPullLayout.loadMoreFinish(result);
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        PromptLayoutHelper helper = null;
        if (mPullLayout != null) {
            helper = mPullLayout.getPromptLayoutHelper();
        }
        if (helper != null) {
            return helper;
        }
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    public void setHasCategory(boolean hasCategory) {
        this.hasCategory = hasCategory;
    }

    private void initPullLayout() {
        onInitPullLayout((PullToRefreshLayout) findView(R.id.pull_layout));
    }

    protected void onInitPullLayout(PullToRefreshLayout pullLayout) {
        if (null == pullLayout) {
            return;
        }
        mPullLayout = pullLayout;

        pullLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                onPullRefresh();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                rankPresenter.loadMoreData(++pageIndex);
            }
        });
    }

    protected void onPullRefresh() {
        integerSparseArray.clear();
        if (!hasCategory) {
            if (categoryPresenter != null) {
                categoryPresenter.getBookListData();
            }
            if (newCategoryPresenter != null) {
                newCategoryPresenter.getBookListData();
            }
            return;
        }
        pageIndex = 1;
        bookList.clear();
        rankPresenter.loadPageData(categoryId, pageIndex, false);

    }

    public void onVisibleToUserChanged(boolean isVisibleToUser) {
        super.onVisibleToUserChanged(isVisibleToUser);
        if (rankPresenter == null) {
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rankPresenter != null) {
            rankPresenter.onPageDestroy();
        }
    }

    @Override
    public void showPage(List<Object> list) {
        if (list != null) {
            if (pageIndex == 1) {
                bookList.addAll(list);
                //清理已记录曝光的书籍列表, 刷新数据重新开始计算.
                if (getActivity() instanceof BookRankActivity) {
                    BookExposureMgr.refreshBookData(BookExposureMgr.PAGE_ID_BOOK_CITY_RANK);
                } else {
                    BookExposureMgr.refreshBookData(BookExposureMgr.PAGE_ID_CATEGORY_RANK);
                }
            } else {
                List<Object> list1 = ReduceUtils.booksRankToRepeat(bookList, list);
                bookList.addAll(list1);
            }
//            if (showAd) {
//                showAd = false;
//                if (bookList.size() > 3) {
//                    List<Object> topList = new ArrayList<>(bookList.subList(0, 3));
//                    topList.add(rankPresenter.getAdData());
//                    topList.addAll(bookList.subList(3, bookList.size()));

//                    bookList.clear();
//                    bookList = new ArrayList<>(topList);
            //设置统计参数-频道(1:男生;2:女生)
            mAdapter.addExtParam(ListAdapter.EXT_KEY_RANK_FREQUENCY, mFrequency);
            //设置统计参数-榜单Id.
            mAdapter.addExtParam(ListAdapter.EXT_KEY_RANK_ID, String.valueOf(categoryId));
            //设置统计参数-页面Id.
            if (getActivity() instanceof BookRankActivity) {
                mAdapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.PAGE_ID_BOOK_CITY_RANK);
            } else {
                mAdapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.PAGE_ID_CATEGORY_RANK);
            }
            mAdapter.setData(bookList);
//                }
//            } else {
//                mAdapter.setData(bookList);
//            }
        }
    }

    @Override
    public void showAdPage(Object adObject,boolean isBanner) {
        if (bookList.size() == 0) {
            // 广告加载失败，等待书籍数据返回
//            showAd = true;
        } else {
            if (bookList.size() > 3) {
                List<Object> topList = new ArrayList<>(bookList.subList(0, 3));
                topList.add(rankPresenter.getAdData());
                topList.addAll(bookList.subList(3, bookList.size()));

                bookList.clear();
                bookList = new ArrayList<>(topList);
                mAdapter.setData(bookList);
            }
        }
    }

    @Override
    public void showMorePage(List<BookCityItemBean> cityItemBeanList) {

    }

    @Override
    public void loadSiteData(BookSiteBean bookSiteBean) {

    }

    public void cleanData() {
        integerSparseArray.clear();
    }

    public interface OnScrollTopListener {
        void onScrollTop();
    }

    public interface Presenter {

        void loadMoreData(int pageIndex);

        void loadPageData(long categoryId, int pageIndex, boolean loadAd);

        void onPageDestroy();

        Object getAdData();
    }

}


