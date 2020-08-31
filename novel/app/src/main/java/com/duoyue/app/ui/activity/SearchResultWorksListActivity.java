package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import com.duoyue.app.bean.SearchRecommdBookListBean;
import com.duoyue.app.bean.SearchResultBean;
import com.duoyue.app.bean.SearchResultListBean;
import com.duoyue.app.bean.SearchV2ListBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.SearchResultListPresenter;
import com.duoyue.app.ui.adapter.SearchResultWorksListAdapter;
import com.duoyue.app.ui.view.SearchResultListView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.LoadResult;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableLayoutManager;
import com.zydm.base.widgets.refreshview.PullableRecyclerView;
import com.zzdm.ad.router.BaseData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SearchResultWorksListActivity extends BaseActivity {

    public static final String KEY_WORD = "keyWord";
    public static final String PREVPAGEID = "prevPageId";


    private PullToRefreshLayout pull;
    private PullableRecyclerView pullableRecyclerView;

    private SearchResultWorksListAdapter searchResultWorksListAdapter;

    private String keyWord;

    private SearchResultListPresenter searchResultListPresenter;

    private int index = 1;
    private int lastIndex;

    private PromptLayoutHelper mPromptLayoutHelper;

    private List<SearchResultBean> searchResultBeans;


    @NotNull
    @Override
    public String getCurrPageId() {
        return PageNameConstants.SEARCH_AUTH_RESULT;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_works_list);
        pull = findViewById(R.id.pull_layout);
        pull.setOnRefreshListener(onRefreshListener);
        pullableRecyclerView = findViewById(R.id.rv_list);
        Intent intent = getIntent();
        if (intent != null) {
            searchResultBeans = new ArrayList<>();
            keyWord = intent.getStringExtra(KEY_WORD);
            searchResultListPresenter = new SearchResultListPresenter(searchResultListView);
            searchResultWorksListAdapter = new SearchResultWorksListAdapter(this, searchResultBeans, onClickListener, intent.getBooleanExtra(PREVPAGEID, true));
            PullableLayoutManager pullableLayoutManager = new PullableLayoutManager(this);
            pullableRecyclerView.setAdapter(searchResultWorksListAdapter);
            pullableRecyclerView.setLayoutManager(pullableLayoutManager);
            getPromptLayoutHelper().showLoading();
            searchResultListPresenter.loadData(Html.fromHtml(keyWord).toString(), index);
            setToolBarLayout(Html.fromHtml(keyWord).toString() + "的作品");
        }
        BookExposureMgr.refreshBookData(BookExposureMgr.SEARCH_AUTH_RESULT);
    }

    PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findViewById(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActivityHelper.INSTANCE.gotoBookDetails(SearchResultWorksListActivity.this, ("" + (int) v.getTag()),
                    new BaseData(getPageName()), PageNameConstants.SEARCH_AUTH_RESULT, 25, "");
        }
    };

    private PullToRefreshLayout.OnRefreshListener onRefreshListener = new PullToRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
            BookExposureMgr.refreshBookData(BookExposureMgr.SEARCH_AUTH_RESULT);
            index = 1;
            searchResultBeans.clear();
            searchResultListPresenter.loadData(Html.fromHtml(keyWord).toString(), index);
        }

        @Override
        public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
            if (lastIndex != -1) {
                searchResultListPresenter.loadData(Html.fromHtml(keyWord).toString(), ++index);
            } else {
                pull.loadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
            }
        }
    };


    private SearchResultListView searchResultListView = new SearchResultListView() {
        @Override
        public void showLoading() {

        }

        @Override
        public void dismissLoading() {
            getPromptLayoutHelper().hideLoading();
        }

        @Override
        public void showEmpty() {
            if (index == 1) {
                mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
            } else {
                pull.loadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
            }
        }

        @Override
        public void showNetworkError() {
            if (searchResultBeans != null && !searchResultBeans.isEmpty()) {
                if (index == 1) {
                    pull.refreshFinish(LoadResult.FORCE_UPDATE_FAIL);
                } else {
                    if (index > 1) --index;
                    pull.loadMoreFinish(LoadResult.LOAD_MORE_FAIL);
                }
            } else {
                getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getPromptLayoutHelper().showLoading();
                        index = 1;
                        searchResultListPresenter.loadData(Html.fromHtml(keyWord).toString(), index);
                    }
                });
            }

        }

        @Override
        public void showPage(SearchV2ListBean bookDetailBean) {

        }

        @Override
        public void showAdPage(Object adObject) {

        }

        @Override
        public void showComment(SearchResultListBean searchResultListBean) {
            if (index == 1) {
                pull.refreshFinish(LoadResult.FORCE_UPDATE_SUCCEED);
            } else {
                pull.loadMoreFinish(LoadResult.LOAD_MORE_SUCCEED);
            }

            lastIndex = searchResultListBean.getNextCursor();
            searchResultBeans.addAll(searchResultListBean.getCommentList());
            searchResultWorksListAdapter.notifyDataSetChanged();
        }

        @Override
        public void showRecommdBookList(SearchRecommdBookListBean searchRecommdBookListBean) {
            
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchResultListPresenter != null) searchResultListPresenter.onDeroty();
    }
}
