package com.duoyue.app.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookListPresenter;
import com.duoyue.app.ui.view.BookItemView;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.ui.list.SelectedActivity;
import com.duoyue.mianfei.xiaoshuo.read.utils.ReduceUtils;
import com.duoyue.mod.ad.view.AdListItemView;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends BaseFragment implements BookPageView {

    private PullToRefreshLayout mPullLayout;

    private BookListFragment.Presenter presenter;
    private ListView listView;
    protected PromptLayoutHelper mPromptLayoutHelper;

    private List<Object> bookList = new ArrayList<>();
    private ListAdapter mAdapter;
    private int pageIndex = 1;
    private int adSite;
    private int mChan;
    private String prevPageId;
    private String pageId;

    /**
     * 标题
     */
    private String mTitle;
    private String mChannel;
    private int type;

    @Override
    public void onCreateView(@Nullable Bundle savedInstanceState) {
        View view = setContentView(R.layout.book_list_fragment);

        initView(view);
        initData();
    }

    private void initView(View view) {
        initPullLayout();
        listView = view.findViewById(R.id.list_view);
        mAdapter = getAdapter();
        listView.setAdapter(mAdapter);

        //统计书籍曝光需要.
//        try {
//            listView.requestLayout();
//        } catch (Throwable throwable) {
//            Logger.e(TAG, "onScroll: {}", throwable);
//        }
    }

    private void initData() {
        type = getArguments().getInt("type");
        adSite = getArguments().getInt("adSite");
        mChan = getArguments().getInt("chan");
        prevPageId = getArguments().getString(ListAdapter.EXT_KEY_PARENT_ID);
        int modelId = getArguments().getInt(ListAdapter.EXT_KEY_MODEL_ID);
        mChannel = getArguments().getString(BookExposureMgr.PAGE_CHANNEL);
        mAdapter.addExtParam(ListAdapter.EXT_KEY_PARENT_ID, prevPageId);
        mAdapter.addExtParam(ListAdapter.EXT_KEY_MODEL_ID, "" + modelId);
        mAdapter.addExtParam(BookExposureMgr.PAGE_CHANNEL, mChannel);

        //设置统计参数-页面Id.
        if (getActivity() instanceof SelectedActivity) {//精品
            mAdapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.BOOK_CITY_BOUTIQUE);
            pageId = BookExposureMgr.BOOK_CITY_BOUTIQUE;
        } else {
            if (modelId == 8) {//完结
                mAdapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.BOOK_CITY_FINISH);
                pageId = BookExposureMgr.BOOK_CITY_FINISH;
            } else {//新书
                mAdapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.BOOK_CITY_NEW_BOOK);
                pageId = BookExposureMgr.BOOK_CITY_NEW_BOOK;
            }
        }
    }

    @Override
    public void onFragmentResume(boolean isFirst, boolean isViewDestroyed) {
        super.onFragmentResume(isFirst, isViewDestroyed);
        if (isFirst) {
            presenter = new BookListPresenter(getActivity(), this, type, mChan);
            showLoading();
            onPullRefresh();
        }
    }

    /**
     * 设置标题.
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    private ListAdapter getAdapter() {
        return new AdapterBuilder()
                .putItemClass(BookItemView.class)
                .putItemClass(AdListItemView.class)
                .builderListAdapter(getActivity());
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
                presenter.loadMoreData(++pageIndex);
            }
        });
    }

    protected void onPullRefresh() {
        bookList.clear();
        pageIndex = 1;
        presenter.loadPageData(pageIndex, false);
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    @Override
    public void showPage(List<Object> list) {
        if (list != null) {
            if (pageIndex == 1) {
                bookList.addAll(list);
                //清理已记录曝光的书籍列表, 刷新数据重新开始计算.
                BookExposureMgr.refreshBookData(pageId);
            } else {
                List<Object> list1 = ReduceUtils.booksToRepeat(bookList, list);
                bookList.addAll(list1);
            }
            mAdapter.setData(bookList);
        }
    }

    @Override
    public void showAdPage(Object adObject, boolean isBanner) {

    }

    @Override
    public void showMorePage(List<BookCityItemBean> cityItemBeanList) {

    }

    @Override
    public void loadSiteData(BookSiteBean bookSiteBean) {

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
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
    }

    @Override
    public void showNetworkError() {
        bookList.clear();
        if (pageIndex > 1) {
            --pageIndex;
        }
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showLoading();
                presenter.loadPageData(pageIndex, false);
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

    @Override
    public String getPageName() {
        return mTitle;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onPageDestroy();
        }
    }

    public interface Presenter {
        void loadMoreData(int pageIndex);

        void loadPageData(int pageIndex, boolean loadAd);

        void onPageDestroy();

        Object getAdData();
    }
}
