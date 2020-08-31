package com.duoyue.app.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import com.duoyue.app.bean.BookBagListBean;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.BookMorePresenter;
import com.duoyue.app.ui.view.BookItemView;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.ReduceUtils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BookMoreActivity extends BaseActivity implements BookPageView {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookMoreActivity";

    private ListAdapter mAdapter;
    private ListView listView;
    private List<Object> bookList = new ArrayList<>();
    private BookMoreActivity.Presenter presenter;
    protected PromptLayoutHelper mPromptLayoutHelper;
    protected PullToRefreshLayout mPullLayout;

    private int pageIndex = 1;
    private String mColumnId;

    /**
     * 该分栏书城首页出现的书籍id，逗号分隔
     */
    private String repeatBookId = "";
    private String tag;
    private String typeId;

    private ListAdapter getAdapter() {
        return new AdapterBuilder()
                .putItemClass(BookItemView.class)
                .builderListAdapter(getActivity());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_more_activity);

        if (getIntent() != null) {
            mColumnId = getIntent().getStringExtra(BaseActivity.ID_KEY);
            tag = getIntent().getStringExtra("tag");
            typeId = getIntent().getStringExtra("typeId");
            List<BookCityItemBean> books = getIntent().getParcelableArrayListExtra("books");
            if (books != null) {
                for (int i = 0; i < books.size(); i++) {
                    BookCityItemBean bookCityItemBean = books.get(i);
                    if (bookCityItemBean == null) continue;
                    if (i == books.size() - 1) {
                        repeatBookId = repeatBookId + bookCityItemBean.getId();
                    } else {
                        repeatBookId = repeatBookId + bookCityItemBean.getId() + ",";
                    }
                }
            } else {
                finish();
                return;
            }
        } else {
            finish();
            return;
        }
        initView();
        ((PullToRefreshLayout) findView(R.id.pull_layout)).setCanPullUp(true);
        ((PullToRefreshLayout) findView(R.id.pull_layout)).setCanPullDown(true);
        presenter = new BookMorePresenter(this, this, mColumnId, repeatBookId, tag, typeId);
        showLoading();
        onPullRefresh();
        initPullLayout();
    }

    private void initView() {
        String title = getIntent().getStringExtra(BaseActivity.DATA_KEY);
//        if (!TextUtils.isEmpty(title)) {
//            ((TextView) findView(R.id.page_title)).setText(title);
//        }
//        findView(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        setToolBarLayout(title);
        listView = findViewById(R.id.list_view);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //统计书籍曝光需要.
                if (view != null)
                {
                    try {
                        view.requestLayout();
                    } catch (Throwable throwable)
                    {
                        Logger.e(TAG, "onScroll: {}", throwable);
                    }
                }
            }
        });
        mAdapter = getAdapter();
        //设置扩展参数-模块Id.
        mAdapter.addExtParam(ListAdapter.EXT_KEY_MODULE_ID, mColumnId);
        //添加页面Id.
        mAdapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.PAGE_ID_CITY_MORE);
        mAdapter.addExtParam(ListAdapter.EXT_KEY_PARENT_ID, "2");
        mAdapter.setData(bookList);
        listView.setAdapter(mAdapter);
    }

    public String getCurrPageId() {
        return "";
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
        presenter.loadPageData(pageIndex);
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
                BookExposureMgr.refreshBookData(BookExposureMgr.PAGE_ID_CITY_MORE);
            } else {
                List<Object> list1 = ReduceUtils.booksToRepeat(bookList, list);
                bookList.addAll(list1);
            }
            mAdapter.setData(bookList);
        }
    }


    @Override
    public void showAdPage(Object adObject,boolean isBanner) {

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
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showLoading();
                presenter.loadPageData(pageIndex);
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
    public boolean isVisibleToUser() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onPageDestroy();
    }

    public interface Presenter {
        void loadMoreData(int pageIndex);

        void loadPageData(int pageIndex);

        void onPageDestroy();
    }
}
