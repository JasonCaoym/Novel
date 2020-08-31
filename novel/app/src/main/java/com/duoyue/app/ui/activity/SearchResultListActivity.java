package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duoyue.app.bean.SearchCountBean;
import com.duoyue.app.bean.SearchKeyWordBean;
import com.duoyue.app.bean.SearchRecommdBookListBean;
import com.duoyue.app.bean.SearchResuleBean;
import com.duoyue.app.bean.SearchResultAuthBean;
import com.duoyue.app.bean.SearchResultBean;
import com.duoyue.app.bean.SearchResultListBean;
import com.duoyue.app.bean.SearchV2ListBean;
import com.duoyue.app.bean.SearchV2MoreListBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.presenter.SearchResultListPresenter;
import com.duoyue.app.presenter.SearchV2Presenter;
import com.duoyue.app.ui.adapter.search.SearchHotAdapter;
import com.duoyue.app.ui.adapter.search.SearchResultAdapter;
import com.duoyue.app.ui.adapter.search.SearchResultListAdapter;
import com.duoyue.app.ui.view.SearchResultListView;
import com.duoyue.app.ui.view.SearchV2View;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.data.bean.RandomPushBean;
import com.duoyue.mianfei.xiaoshuo.presenter.RandomPushBookDialogPresenter;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.LoadResult;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.KeyboardUtils;
import com.zydm.base.utils.SharePreferenceUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableLayoutManager;
import com.zydm.base.widgets.refreshview.PullableRecyclerView;
import com.zzdm.ad.router.BaseData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SearchResultListActivity extends BaseActivity {

    private SearchResultListPresenter searchResultListPresenter;

    private PullableRecyclerView pullableListView;

    private SearchResultListAdapter searchResultListAdapter;

    private List<Object> searchResultBeans;
    private List<String> authList;

    private RecyclerView recyclerView;

    private EditText editText;

    private SearchV2Presenter searchV2Presenter;

    private SearchResultAdapter searchResultAdapter;

    private List<SearchKeyWordBean> strings;

    private ImageView imageView;

    private TextView textView;

    private PullToRefreshLayout pullToRefreshLayout;

    private int index = 1;
    private int lastIndex;

    private View view;

    private RecyclerView mRv_null_list;

    private ImageView mTv_back;

    private PromptLayoutHelper mPromptLayoutHelper;

    private String key;

    private int mSeacrhType;

    //随机推书
    private int times;
    private Handler mHandler;
    private Runnable mTask;
    private RandomPushBookDialogPresenter mRandomPushBookDialogPresenter;
    private final List<SearchCountBean> keyWord = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_list);
        initViews();
        initData();

    }

    void initViews() {
        pullableListView = findViewById(R.id.lv_list);
        PullableLayoutManager linearLayoutManager = new PullableLayoutManager(this);
        pullableListView.setLayoutManager(linearLayoutManager);

        recyclerView = findViewById(R.id.rv_search_list);
        recyclerView.setLayoutManager(new PullableLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(recyclerView.getContext()).resumeRequestsRecursive();
                } else {
                    Glide.with(recyclerView.getContext()).pauseAllRequests();
                }
            }
        });

        editText = findViewById(R.id.search_edit);
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.setTag(true);
        editText.addTextChangedListener(textWatcher);
        imageView = findViewById(R.id.iv_clean);
        imageView.setOnClickListener(onClickListener);

        textView = findViewById(R.id.cancel_btn);
        mRv_null_list = findViewById(R.id.rv_null_list);
        textView.setOnClickListener(onClickListener);

        pullToRefreshLayout = findViewById(R.id.pull_layout);
        pullToRefreshLayout.setOnRefreshListener(onRefreshListener);
        view = findViewById(R.id.view_null);
        mTv_back = findViewById(R.id.toolbar_back);
        mTv_back.setOnClickListener(onClickListener);
    }


    PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findViewById(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private PullToRefreshLayout.OnRefreshListener onRefreshListener = new PullToRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
            BookExposureMgr.refreshBookData(FunPageStatsConstants.EP_SEARCH_RESULT);
            index = 1;
            searchResultBeans.clear();
            searchResultListPresenter.loadData(editText.getText().toString().trim(), index, mSeacrhType);
        }

        @Override
        public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
            if (lastIndex != -1) {
                searchResultListPresenter.loadData(editText.getText().toString().trim(), ++index, mSeacrhType);
            } else {
                pullToRefreshLayout.loadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
            }
        }
    };

    private void searchCount(String string, int source) {
        if (!TextUtils.isEmpty(string)) {
            keyWord.clear();
            SearchCountBean searchCountBean = new SearchCountBean();
            searchCountBean.setWord(string);
            searchCountBean.setSource(source);
            searchCountBean.setOperator("SEARCH_KEYWORD");
            searchCountBean.setPageId("SEARCH");
            searchCountBean.setNowPage("SEARCH_RESULT");
            searchCountBean.setModelId(26);
            keyWord.add(searchCountBean);
            if (searchV2Presenter != null) searchV2Presenter.searchCount(keyWord);
        }
    }

    void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            searchResultBeans = new ArrayList<>();
            searchResultListPresenter = new SearchResultListPresenter(searchResultListView);
            key = Html.fromHtml(intent.getStringExtra("key")).toString();
            editText.setText(key);
            editText.setSelection(editText.getText().toString().trim().length());
            mSeacrhType = intent.getIntExtra("searchType", -1);
            searchResultListPresenter.loadData(key, index, mSeacrhType);
            searchResultListAdapter = new SearchResultListAdapter(this, onClickListener, searchResultBeans, key);
            pullableListView.setAdapter(searchResultListAdapter);
        }

        searchV2Presenter = new SearchV2Presenter(searchV2View);

        strings = new ArrayList<>();
        authList = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(this, strings, onClickListener, authList);
        recyclerView.setAdapter(searchResultAdapter);
        BookExposureMgr.refreshBookData(FunPageStatsConstants.EP_SEARCH_RESULT);
    }

    public String getCurrPageId() {
        return PageNameConstants.SEARCH_RESULT;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (imageView.getVisibility() == View.GONE) {
                imageView.setVisibility(View.VISIBLE);
            }
            if (editText.getTag() != null) {
                editText.setTag(null);
                return;
            }
            if (TextUtils.isEmpty(s)) {
                finish();
            } else {
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                }
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                if (searchV2Presenter != null) {
                    searchV2Presenter.keyWord(s.toString(), mSeacrhType);
                }
            }

        }
    };
    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //点击搜索要做的操作
                if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                    ToastUtils.showLimited("搜索内容不能为空");
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                } else {
                    mSeacrhType = 1;

                    if (editText.getText().toString().trim().length() > 100) {
                        ToastUtils.showLimited("搜索内容不能超过100字");
                    } else {
                        mTv_back.setTag(null);
                        index = 1;
                        refreshData(editText.getText().toString().trim());
                        //点击键盘搜索键.
                        FunctionStatsApi.sButtonClick();
                        KeyboardUtils.hideKeyboard(getActivity(), editText);
                        FuncPageStatsApi.searchResultBtnClick(1);
                        searchCount(editText.getText().toString().trim(), 0);
                    }
                }
            }
            return true;
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.toolbar_back:
                case R.id.iv_clean:
                    finish();
                    break;
                case R.id.cancel_btn:
                    mSeacrhType = 1;
                    //点击搜索按钮.

                    if (editText.getText().toString().trim().length() > 100) {
                        ToastUtils.showLimited("搜索内容不能超过100字");
                    } else {
                        mTv_back.setTag(null);
                        index = 1;
                        refreshData(editText.getText().toString().trim());
                        KeyboardUtils.hideKeyboard(getActivity(), editText);
                        FunctionStatsApi.sButtonClick();
                        FuncPageStatsApi.searchResultBtnClick(2);
                        searchCount(editText.getText().toString().trim(), 0);
                    }
                    break;
                case R.id.xrl_result:
                    mSeacrhType = 0;
                    //点击搜索结果.
                    if (v.getTag() == null) return;
                    if (v.getTag() instanceof String) {
                        editText.setText(Html.fromHtml(v.getTag().toString()).toString());
                        editText.setSelection(editText.getText().length());
                        refreshData(editText.getText().toString().trim());
                        searchCount(Html.fromHtml(v.getTag().toString()).toString(), 2);
                        Intent intent = new Intent(SearchResultListActivity.this, SearchResultWorksListActivity.class);
                        intent.putExtra(SearchResultWorksListActivity.KEY_WORD, (String) v.getTag());
                        intent.putExtra(SearchResultWorksListActivity.PREVPAGEID, false);
                        startActivity(intent);
                        FuncPageStatsApi.searchResultAuthClick();
                    } else {
                        SearchKeyWordBean searchKeyWordBean = (SearchKeyWordBean) v.getTag();
                        editText.setText(Html.fromHtml(searchKeyWordBean.bookName).toString());
                        editText.setSelection(editText.getText().length());
                        refreshData(searchKeyWordBean.bookName);
                        FunctionStatsApi.sKeywordsLenovoClick(searchKeyWordBean.bookId);
                        searchCount(Html.fromHtml(searchKeyWordBean.bookName).toString(), 1);
                    }
                    KeyboardUtils.hideKeyboard(getActivity(), editText);
                    break;
                case R.id.xrl_search:
                    FunctionStatsApi.sEmptyStateBookClick(StringFormat.parseLong(StringFormat.toString(v.getTag()), -1));
                    ActivityHelper.INSTANCE.gotoBookDetails(SearchResultListActivity.this, StringFormat.toString(v.getTag()),
                            new BaseData(getPageName()), PageNameConstants.SEARCH_RESULT, 11, "");
                    break;
                case R.id.xll:
                    mSeacrhType = 0;
                    FuncPageStatsApi.searchRecommendResultClickList((long) (int) v.getTag());
                    //点击搜索结果.
                    ActivityHelper.INSTANCE.gotoBookDetails(SearchResultListActivity.this, StringFormat.toString(v.getTag()),
                            new BaseData(getPageName()), PageNameConstants.SEARCH_RESULT, 26, "");
                    break;
                case R.id.iv_item_hot: // 空书籍推荐
                    FuncPageStatsApi.searchEmptyClick((int) v.getTag(R.id.tag_item));
                    ActivityHelper.INSTANCE.gotoBookDetails(SearchResultListActivity.this, ("" + (int) v.getTag(R.id.tag_item)),
                            new BaseData(getPageName()), PageNameConstants.SEARCH_RESULT, 10, "");
                    break;
                case R.id.xll_auth:
                    Intent intent = new Intent(SearchResultListActivity.this, SearchResultWorksListActivity.class);
                    intent.putExtra(SearchResultWorksListActivity.KEY_WORD, (String) v.getTag());
                    intent.putExtra(SearchResultWorksListActivity.PREVPAGEID, false);
                    startActivity(intent);
                    FuncPageStatsApi.searchResultAuthClick();
                    break;
            }
        }
    };

    void refreshData(String data) {
        searchResultBeans.clear();
        searchResultListAdapter.notifyDataSetChanged();

        searchResultListPresenter.loadData(data, 1, mSeacrhType);

        strings.clear();
        authList.clear();
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
        }

        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    private SearchV2View searchV2View = new SearchV2View() {
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
                pullToRefreshLayout.loadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
            }
        }

        @Override
        public void showNetworkError() {

        }

        @Override
        public void showPage(SearchV2ListBean bookDetailBean) {

        }

        @Override
        public void showAdPage(Object adObject) {

        }

        @Override
        public void showComment(SearchV2ListBean commentList) {
            SearchHotAdapter searchHotAdapter = new SearchHotAdapter(SearchResultListActivity.this, commentList.getCommentList(), onClickListener, false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchResultListActivity.this, 3);
            mRv_null_list.setAdapter(searchHotAdapter);
            mRv_null_list.setLayoutManager(gridLayoutManager);
        }

        @Override
        public void showMoreComment(SearchV2MoreListBean commentList) {

        }

        @Override
        public void showKeyWord(SearchResuleBean searchResuleBean) {
            authList.clear();
            strings.clear();
            if (searchResuleBean.getMoreList() != null && !searchResuleBean.getMoreList().isEmpty()) {
                strings.addAll(searchResuleBean.getMoreList());
                searchResultAdapter.notifyItemRangeChanged(0, strings.size());
            } else {
                recyclerView.setVisibility(View.GONE);
            }
            if (searchResuleBean.getAuthList() != null && !searchResuleBean.getAuthList().isEmpty()) {
                authList.addAll(searchResuleBean.getAuthList());
                searchResultAdapter.notifyItemRangeChanged(0, strings.size() + authList.size());
            }
        }
    };

    private SearchResultListView searchResultListView = new SearchResultListView() {
        @Override
        public void showLoading() {
            getPromptLayoutHelper().showLoading();
        }

        @Override
        public void dismissLoading() {
            getPromptLayoutHelper().hideLoading();
        }

        @Override
        public void showEmpty() {
            getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
        }

        @Override
        public void showNetworkError() {

            getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mTv_back.setTag(null);
                    showLoading();
                    searchResultListPresenter.loadData(key, 1, mSeacrhType);
                }
            });

            if (index == 1) {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
            } else {
                pullToRefreshLayout.loadMoreFinish(PullToRefreshLayout.FAIL);
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
            if (searchResultListBean.getTotal() == 0 && searchResultBeans.isEmpty()) {
                //数据返回为空
                view.setVisibility(View.VISIBLE);
                searchV2Presenter.loadMoreData();
                searchCount(editText.getText().toString().trim(), 1);
            } else {
                if (index == 1) {
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    pullToRefreshLayout.loadMoreFinish(searchResultListBean.getNextCursor() == -1 ? LoadResult.LOAD_MORE_FAIL_NO_DATA : PullToRefreshLayout.SUCCEED);
                }
                lastIndex = searchResultListBean.getNextCursor();
//                List<SearchResultBean> lists = ReduceUtils.booksSearchToRepeat(searchResultBeans, searchResultListBean.getCommentList());
                if (index == 1 && searchResultListBean.getAuthInfo() != null) {
                    searchResultBeans.add(searchResultListBean.getAuthInfo());
//                    searchResultListAdapter.setData(searchResultListBean.getAuthInfo());
                }
//                else {
//                    searchResultListAdapter.setData(null);
//                }
                if (searchResultListBean.getCommentList() == null) return;
                List<SearchResultBean> beans = searchResultListBean.getCommentList();
                for (SearchResultBean resultBean : beans) {
                    resultBean.setType(102);
                    searchResultBeans.add(resultBean);
                }
                if (index != 1) {
                    searchResultListAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void showRecommdBookList(SearchRecommdBookListBean searchRecommdBookListBean) {
            if (searchRecommdBookListBean.getBookList() == null) return;
            SearchResultBean searchResultBean = new SearchResultBean(Parcel.obtain());
            searchResultBean.setType(103);
            searchResultBean.setSearchRecommdBookBeans(searchRecommdBookListBean.getBookList());
            if (searchResultBeans.size() >= 4) {
                searchResultBeans.set(searchResultBeans.get(0) instanceof SearchResultAuthBean ? 4 : 3, searchResultBean);
            } else {
                searchResultBeans.set(searchResultBeans.size(), searchResultBean);
            }
            searchResultListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        boolean isShow = SharePreferenceUtils.getBoolean(this, SharePreferenceUtils.IS_IN_DETAIL, false);
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
    protected void onPause() {
        super.onPause();
        if (mHandler != null && mTask != null) {
            mHandler.removeCallbacks(mTask);
        }
        times = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mTask != null) {
            mTask = null;
        }
        if (searchV2Presenter != null) searchV2Presenter.onDistory();
        if (mRandomPushBookDialogPresenter != null) {
            mRandomPushBookDialogPresenter.destroy();
        }

        if (searchResultListPresenter != null) {
            searchResultListPresenter.onDeroty();
        }


        KeyboardUtils.hideKeyboard(getActivity(), editText);

        editText.removeTextChangedListener(textWatcher);
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            times++;
            //时间单位是秒
            int duration = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.RANDOM_PUSH_TIME_SEARCH, 10);
            if (times < duration) {
                mHandler.postDelayed(this, 1000);
            } else {
                if (PhoneUtil.isNetworkAvailable(SearchResultListActivity.this)) {
                    String title = editText.getText().toString();
                    mRandomPushBookDialogPresenter = new RandomPushBookDialogPresenter(SearchResultListActivity.this, getSupportFragmentManager(), PageNameConstants.SEARCH_RESULT_OUT, title);
                    if (searchResultBeans != null && searchResultBeans.size() > 0) {//有搜索结果
                        mRandomPushBookDialogPresenter.preLoadNextChapter(getBookBean());
                    } else {
                        mRandomPushBookDialogPresenter.loadData(0);
                    }
                }
                mHandler.removeCallbacks(mTask);
                mHandler = null;
                mTask = null;
            }
        }
    }

    @NotNull
    private RandomPushBean.BookBean getBookBean() {
        Object object = searchResultBeans.get(0);
        if (object instanceof SearchResultAuthBean) {
            object = searchResultBeans.get(1);
        }
        SearchResultBean searchResultBean = (SearchResultBean) object;
        RandomPushBean.BookBean bookBean = new RandomPushBean.BookBean();
        bookBean.setAuthorName(Html.fromHtml(searchResultBean.getAuthor()).toString());
        bookBean.setBookId(searchResultBean.getBookId());
        bookBean.setBookName(Html.fromHtml(searchResultBean.getBookName()).toString());
        bookBean.setCatName(searchResultBean.getCategory());
        bookBean.setCover(searchResultBean.getBookCover());
        bookBean.setState(Integer.parseInt(searchResultBean.getIsFinish()));
        bookBean.setStar(searchResultBean.getStar());
        bookBean.setWordCount(searchResultBean.getWordCount());
        bookBean.setResume(searchResultBean.getResume());
        bookBean.setLastChapter(searchResultBean.getLastChapter());
        return bookBean;
    }
}
