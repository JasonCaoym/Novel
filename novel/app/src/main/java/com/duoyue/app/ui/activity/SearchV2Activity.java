package com.duoyue.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.alibaba.android.arouter.facade.annotation.Route;
import com.duoyue.app.bean.*;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.presenter.SearchV2Presenter;
import com.duoyue.app.ui.adapter.search.SearchResultAdapter;
import com.duoyue.app.ui.adapter.search.SearchV2Adapter;
import com.duoyue.app.ui.view.SearchV2View;
import com.duoyue.app.upgrade.CacheUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.KeyboardUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullableLayoutManager;
import com.zzdm.ad.router.BaseData;
import com.zzdm.ad.router.RouterPath;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Route(path = RouterPath.Book.PATH_SEARCH)
public class SearchV2Activity extends BaseActivity {

    private ImageView mIv_back;
    private TextView mTv_search;
    private RecyclerView mRv_list;

    private SearchV2Presenter searchV2Presenter;

    private SearchV2Adapter searchV2Adapter;
    private SearchResultAdapter searchResultAdapter;

    private List<SearchBean> searchBeans;
    private List<SearchKeyWordBean> strings;
    private List<String> authList;


    private EditText editText;

    private PromptLayoutHelper mPromptLayoutHelper;

    private RecyclerView recyclerView;

    private List<String> mList;

    private ImageView imageView;
    private String prevPageId;

    private int mSeacrhType;

    private final List<SearchCountBean> keyWord = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_v2);

        initViews();

        initData();
    }

    private void initData() {
        prevPageId = getIntent().getStringExtra(BaseActivity.ID_KEY);
        if (!StringFormat.isEmpty(prevPageId) && prevPageId.equalsIgnoreCase("notification")) {
            // 上报通知栏搜索点击
            FuncPageStatsApi.notifySearch();
            prevPageId = "";
        }
        if (prevPageId.equalsIgnoreCase(PageNameConstants.BOOKSHELF)) {
            FuncPageStatsApi.searchShow(prevPageId, 1);
        } else if (prevPageId.equalsIgnoreCase(PageNameConstants.BOOK_CITY)) {
            FuncPageStatsApi.searchShow(prevPageId, 2);
        } else if (prevPageId.equalsIgnoreCase(PageNameConstants.CATEGORY)) {
            FuncPageStatsApi.searchShow(prevPageId, 3);
        } else {
            FuncPageStatsApi.searchShow(prevPageId, 4);
        }
        searchBeans = new ArrayList<>();
        strings = new ArrayList<>();
        authList = new ArrayList<>();

        searchV2Adapter = new SearchV2Adapter(SearchV2Activity.this, searchBeans, onClickListener);
        mRv_list.setAdapter(searchV2Adapter);
        PullableLayoutManager linearLayoutManager = new PullableLayoutManager(SearchV2Activity.this);
        mRv_list.setLayoutManager(linearLayoutManager);

        searchV2Presenter = new SearchV2Presenter(searchV2View);
        searchV2Presenter.loadData();

        searchResultAdapter = new SearchResultAdapter(this, strings, onClickListener, authList);
        recyclerView.setAdapter(searchResultAdapter);
        PullableLayoutManager linearLayoutManager2 = new PullableLayoutManager(SearchV2Activity.this);
        recyclerView.setLayoutManager(linearLayoutManager2);
    }

    private void searchCount(String string, int source) {
        if (!TextUtils.isEmpty(string)) {
            keyWord.clear();
            SearchCountBean searchCountBean = new SearchCountBean();
            searchCountBean.setOperator("SEARCH_KEYWORD");
            searchCountBean.setPageId("SEARCH");
            searchCountBean.setNowPage("SEARCH_RESULT");
            searchCountBean.setModelId(26);
            searchCountBean.setWord(string);
            searchCountBean.setSource(source);
            keyWord.add(searchCountBean);
            searchV2Presenter.searchCount(keyWord);
        }
    }


    private void initLocalData() {
        mList = CacheUtil.getInstance(this).getDataList("Local");
        SearchBean searchBean3 = new SearchBean();
        searchBean3.setType(SearchBean.TYME_THREE);
        searchBean3.setStringList(mList);
        searchBeans.add(searchBean3);
        searchV2Adapter.notifyItemRangeInserted(searchBeans.size() - 1, searchBeans.size());
    }

    private void initViews() {
        mRv_list = findViewById(R.id.rv_list);

        recyclerView = findViewById(R.id.rv_search_list);

        mIv_back = findViewById(R.id.toolbar_back);
        mTv_search = findViewById(R.id.cancel_btn);
        editText = findViewById(R.id.search_edit);
        editText.addTextChangedListener(textWatcher);
        mIv_back.setOnClickListener(onClickListener);
        mTv_search.setOnClickListener(onClickListener);


        editText.setOnEditorActionListener(onEditorActionListener);

        imageView = findViewById(R.id.iv_clean);
        imageView.setOnClickListener(onClickListener);

        mPromptLayoutHelper = getPromptLayoutHelper();
    }

    PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findViewById(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    public String getCurrPageId() {
        return PageNameConstants.SEARCH;
    }

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
                        refreshLocalData(editText.getText().toString().trim(), true);
                        //点击键盘搜索键.
                        FunctionStatsApi.sButtonClick();
                        FuncPageStatsApi.searchBtnClick(prevPageId, 1);
                        searchCount(editText.getText().toString().trim(), 0);
                        FuncPageStatsApi.searchKeyWordClick();
                    }
                }
            }
            return false;
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.toolbar_back:
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        mSeacrhType = 0;
                        editText.setText("");
                    } else {
                        finish();
                    }
                    break;
                case R.id.iv_clean:
                    mSeacrhType = 0;
                    editText.setText("");
                    break;
                case R.id.iv_item_hot:
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(SearchV2Activity.this,
                            StringFormat.toString(v.getTag(R.id.tag_item)), new BaseData(getPageName()), PageNameConstants.SEARCH, 9, "");
                    //飙升热门大作.
                    FunctionStatsApi.sRecommBookClick(StringFormat.toString(v.getTag()));
                    FuncPageStatsApi.searchRecomBookClick((long) (int) v.getTag(R.id.tag_item), prevPageId);
                    break;
                case R.id.iv_delete:
                    //清理历史记录.
                    searchV2Adapter.notifyItemRemoved(searchBeans.size() - 1);
                    mList.clear();
                    FunctionStatsApi.sClearHistoryClick();
                    FuncPageStatsApi.searchHistoryClear(prevPageId);
                    CacheUtil.getInstance(SearchV2Activity.this).setDataList("Local", mList);
                    break;
                case R.id.cancel_btn:
                    mSeacrhType = 1;
                    //搜索按钮.
                    if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                        ToastUtils.showLimited("搜索内容不能为空");
                    } else {
                        if (editText.getText().toString().trim().length() > 100) {
                            ToastUtils.showLimited("搜索内容不能超过100字");
                        } else {
                            refreshLocalData(editText.getText().toString().trim(), true);
                            FunctionStatsApi.sButtonClick();
                            FuncPageStatsApi.searchBtnClick(prevPageId, 2);
                            searchCount(editText.getText().toString().trim(), 0);
                            FuncPageStatsApi.searchKeyWordClick();
                        }
                    }
                    break;
                case R.id.xll_item:
                    mSeacrhType = 0;
                    //点击热搜词.
                    refreshLocalData(v.getTag().toString(), true);
                    FunctionStatsApi.sHotWordClick();
                    FuncPageStatsApi.searchHotClick(prevPageId);
                    searchCount(v.getTag().toString(), 4);
                    FuncPageStatsApi.searchKeyWordClick();
                    break;
                case R.id.xrl_result:
                    mSeacrhType = 0;
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        //点击搜索结果.
                        if (v.getTag() != null) {
                            if (v.getTag() instanceof String) {
                                refreshLocalData(v.getTag().toString(), false);
                                Intent intent = new Intent(SearchV2Activity.this, SearchResultWorksListActivity.class);
                                intent.putExtra(SearchResultWorksListActivity.PREVPAGEID, true);
                                intent.putExtra(SearchResultWorksListActivity.KEY_WORD, (String) v.getTag());
                                startActivity(intent);
                                FuncPageStatsApi.searchResultAuthClick();
                                searchCount(v.getTag().toString(), 2);
                                FuncPageStatsApi.searchKeyWordClick();
                                editText.setText("");
                            } else {
                                SearchKeyWordBean searchKeyWordBean = (SearchKeyWordBean) v.getTag();
                                refreshLocalData(Html.fromHtml(searchKeyWordBean.bookName).toString(), true);
                                FunctionStatsApi.sKeywordsLenovoClick(searchKeyWordBean.bookId);
                                FuncPageStatsApi.searchKeyListClick(prevPageId);
                                searchCount(Html.fromHtml(searchKeyWordBean.bookName).toString(), 1);
                                FuncPageStatsApi.searchKeyWordClick();
                            }
                        }
                    } else {
                        //点击历史记录
                        FunctionStatsApi.sHistoryClick();
                        FuncPageStatsApi.searchHistoryClick(prevPageId);
                        refreshLocalData(Html.fromHtml((String) v.getTag()).toString(), true);
                        searchCount(Html.fromHtml((String) v.getTag()).toString(), 3);
                    }
                    break;
            }
        }
    };

    void refreshLocalData(String data, boolean isJump) {
        if (mList != null && mList.size() == 10) {
            searchV2Adapter.notifyItemRemoved(mList.size() - 1);
            mList.remove(mList.size() - 1);
        }
        if (mList != null && searchBeans != null && searchBeans.size() > 0 && searchBeans.get(searchBeans.size() - 1).getStringList() != null) {
            if (!mList.contains(data)) {
                searchBeans.get(searchBeans.size() - 1).getStringList().add(0, data);
                searchV2Adapter.notifyItemRangeInserted(searchBeans.size() - 1, searchBeans.size());
            } else {
                searchBeans.get(searchBeans.size() - 1).getStringList().remove(data);
                searchBeans.get(searchBeans.size() - 1).getStringList().add(0, data);
                searchV2Adapter.notifyItemChanged(searchBeans.size() - 1);
            }
        }

        CacheUtil.getInstance(this).setDataList("Local", mList);
        if (isJump) {
            Intent intent1 = new Intent(SearchV2Activity.this, SearchResultListActivity.class);
            intent1.putExtra("key", data);
            intent1.putExtra("searchType", mSeacrhType);
            startActivityForResult(intent1, 1001);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        editText.setText("");
        mSeacrhType = 0;
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
            if (TextUtils.isEmpty(s)) {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    strings.clear();
                    authList.clear();
                    recyclerView.setVisibility(View.GONE);
                }
                if (imageView.getVisibility() == View.VISIBLE) {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                if (imageView.getVisibility() == View.GONE) {
                    imageView.setVisibility(View.VISIBLE);
                }
                searchV2Presenter.keyWord(s.toString(), mSeacrhType);
            }

        }
    };


    private SearchV2View searchV2View = new SearchV2View() {
        @Override
        public void showLoading() {
            mPromptLayoutHelper.showLoading();

        }

        @Override
        public void dismissLoading() {

            mPromptLayoutHelper.hideLoading();

        }

        @Override
        public void showEmpty() {
            mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
        }

        @Override
        public void showNetworkError() {
            SearchV2MoreListBean searchV2MoreListBean = DataCacheManager.getInstance().getSearchV2MoreListBean();
            SearchV2ListBean searchV2ListBean = DataCacheManager.getInstance().getSearchV2ListBean();
            if (searchV2MoreListBean != null && searchV2ListBean != null) {
                showMoreComment(searchV2MoreListBean);
                showComment(searchV2ListBean);
            } else if (searchV2MoreListBean == null && searchV2ListBean != null) {
                showComment(searchV2ListBean);
            } else if (searchV2MoreListBean != null && searchV2ListBean == null) {
                showMoreComment(searchV2MoreListBean);
            } else {
                mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        searchV2Presenter.loadData();
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
        public void showComment(SearchV2ListBean commentList) {
            SearchBean searchBean = new SearchBean();
            searchBean.setType(SearchBean.TYME_TWO);
            searchBean.setSearchV2ListBean(commentList);
            searchBeans.add(searchBean);
            searchV2Adapter.notifyItemRangeInserted(searchBeans.size() - 1, searchBeans.size());
            initLocalData();
        }

        @Override
        public void showMoreComment(SearchV2MoreListBean commentList) {
            SearchBean searchBean2 = new SearchBean();
            searchBean2.setType(SearchBean.TYME_ONE);
            searchBean2.setSearchV2MoreListBean(commentList);
            searchBeans.add(searchBean2);
            searchV2Adapter.notifyItemRangeInserted(searchBeans.size() - 1, searchBeans.size());
        }

        @Override
        public void showKeyWord(SearchResuleBean searchResuleBean) {
            strings.clear();
            authList.clear();
            //图书
            if (searchResuleBean.getMoreList() != null && !searchResuleBean.getMoreList().isEmpty()) {
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                strings.addAll(searchResuleBean.getMoreList());
                searchResultAdapter.notifyItemRangeChanged(0, strings.size());
            } else {
                recyclerView.setVisibility(View.GONE);
            }
            //作者
            if (searchResuleBean.getAuthList() != null && !searchResuleBean.getAuthList().isEmpty()) {
                authList.addAll(searchResuleBean.getAuthList());
                searchResultAdapter.notifyItemRangeChanged(0, strings.size() + authList.size());
            }

        }
    };

    @Override
    public void finish() {
        super.finish();
        KeyboardUtils.hideKeyboard(getActivity(), editText);
        if (!Utils.isExsitMianActivity(this, HomeActivity.class)) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            editText.setText("");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheUtil.getInstance(this).onDestroy();
        if (searchV2Presenter != null) searchV2Presenter.onDistory();
        if (!keyWord.isEmpty()) keyWord.clear();
        if (textWatcher != null) editText.removeTextChangedListener(textWatcher);

    }
}
