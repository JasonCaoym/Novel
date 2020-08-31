package com.duoyue.app.ui.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.listener.AppBarStateChangeListener;
import com.duoyue.app.presenter.CategoryBookListPresenter;
import com.duoyue.app.presenter.CategoryPresenter;
import com.duoyue.app.ui.adapter.search.SearchV2LinearLayoutManager;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.app.ui.view.CategoryBookItemView;
import com.duoyue.app.ui.view.CategoryBookRelativeLayout;
import com.duoyue.app.ui.view.NewCategoryNotificationView;
import com.duoyue.lib.base.FlowXLayout;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.XFlowLayout;
import com.duoyue.lib.base.widget.XFrameLayout;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.utils.ReduceUtils;
import com.duoyue.mod.ad.view.AdListItemView;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.data.bean.SubCategoriesBean;
import com.zydm.base.data.tools.JsonUtils;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.ui.item.RecyclerAdapter;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryBookListActivity extends BaseActivity implements BookPageView {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#CategoryBookListActivity";

    public static final String CLASSID = "ClassId";
    public static final String TAG_N = "tag";
    public static final String CATEGORY_N = "category";
    public static final String SELECTEDID = "SELECTEDID";

    private CategoryBookListActivity.Presenter presenter;
    protected PromptLayoutHelper mPromptLayoutHelper;
    protected PromptLayoutHelper mLoadingLayoutHelper;
    private TextView tvPoputlarity;
    private TextView tvUpdateTime;
    private TextView tvGride;
    private TextView tvAll;
    private TextView tvContinue;
    private TextView tvFinish;
    private TextView tvUnLimited;
    private TextView tvCountFirst;
    private TextView tvCountSecond;
    private TextView tvCountThree;
    private LinearLayout layoutWordCount;   //字数布局

    //悬浮窗口中的分类按钮
    private TextView tvPoputlarityFloating;
    private TextView tvUpdateTimeFloating;
    private TextView tvGrideFloating;

    private CoordinatorLayout coordinatorLayout;
    private PullToRefreshLayout mPullLayout;
    private RecyclerView recyclerView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout toolBar;
    private CategoryBookRelativeLayout floatingLayout;  //分类悬浮窗口
    private XLinearLayout filterLayoutUp;       //分类菜单收缩
    private XLinearLayout filterLayoutDown;     //分类菜单展开
    private CoordinatorLayout.LayoutParams params;
    private XLinearLayout xllTag;
    private TextView tvTagsAll;          //不限标签
    private XFlowLayout flowLayout;       //标签布局
    private XLinearLayout xllSubCategory;           //二级分类
    private TextView tvSubCategoryAll;              //不限二级分类
    private XFlowLayout flowLayoutSubCategory;       //二级分类布局
    private LinearLayout promptLayout;  //无数据时显示
    private LinearLayout loadingLayout; //loading是显示


    private RecyclerAdapter adapter;
    private List<Object> bookList = new ArrayList<>();
    private int pageIndex = 1;
    private boolean canAppBarLayoutScroll = true;  //是否允许appbarlayout滚动
    private AppBarLayout.Behavior appBarLayoutBehavior;

    private CategoryBean mCategoryBean;
    private int firstTag = 1;
    private int secondTag = 1;
    private int threeTag = 1;
    private int sortType = 1;
    private List<String> tagList = new ArrayList<>();

    private String tag = "";    //标签
    private int mSubCategoryId = 0;  //二级分类

    private boolean isFirst = true; //是否第一次加载

    private int pid, cid;
    private String tagn, categoryn;
    private CategoryPresenter categoryPresenter;
    private Intent intent;


    private NewCategoryNotificationView newCategoryNotificationView = new NewCategoryNotificationView() {
        @Override
        public void showLoading() {
        }

        @Override
        public void dismissLoading() {

        }

        @Override
        public void showNetworkError() {
            getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, null);
        }

        @Override
        public void updateCategory(List<CategoryBean> categoryBeanList) {

            if (categoryBeanList == null) {
                getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
                return;
            }

            for (CategoryBean categoryBean : categoryBeanList) {
                if (categoryBean.getId().equals(String.valueOf(cid))) {
                    mCategoryBean = categoryBean;
                    break;
                }
            }
            if (mCategoryBean != null) {
                mCategoryBean.setSex(pid);
                if (mCategoryBean.getTags() != null && !TextUtils.isEmpty(tagn)) {
                    String[] tags = mCategoryBean.getTags().split(",");
                    if (tags.length > 0) {
                        for (int i = 0; i < tags.length; i++) {
                            if (tags[i].equals(tagn)) {
                                sortType = i + 1;
                                tag = tagn;
                                break;
                            }
                        }
                    }
                }

                ArrayList<SubCategoriesBean> beans = mCategoryBean.getSubCategories();
                if (beans != null && !beans.isEmpty() && !TextUtils.isEmpty(categoryn)) {
                    for (SubCategoriesBean subCategoriesBean : beans) {
                        if (categoryn.equals(String.valueOf(subCategoriesBean.getId()))) {
                            mSubCategoryId = subCategoriesBean.getId();

//                            threeTag = beans.indexOf(subCategoriesBean) + 1;
                            break;
                        }
                    }
                }

                initView();
                initClickView();
                initData();
                mPullLayout.setCanPullDown(false);
            }

        }

        @Override
        public void showSite(BookSiteBean bookSiteBean) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_book_list_activity2);

        intent = getIntent();
        if (intent != null) {
            pid = intent.getIntExtra(SELECTEDID, -1);
            cid = intent.getIntExtra(CLASSID, -1);
            tagn = intent.getStringExtra(TAG_N);
            categoryn = intent.getStringExtra(CATEGORY_N);
        }
        if (pid == -1 && cid == -1) {
            initView();
            initClickView();
            initData();
        } else {
            categoryPresenter = new CategoryPresenter(newCategoryNotificationView);
            categoryPresenter.loadCategory(pid);
        }

    }

    private void initView() {

        if (pid == -1 && cid == -1) {
            String json = intent.getStringExtra(BaseActivity.DATA_KEY);
            mCategoryBean = JsonUtils.parseJson(json, CategoryBean.class);
        }
        String title = mCategoryBean.getName();

        setToolBarLayout(title);
        initPullLayout();

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        layoutWordCount = findViewById(R.id.layout_word_count);

        recyclerView = findViewById(R.id.list_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //统计书籍曝光需要.
                if (recyclerView != null) {
                    try {
                        recyclerView.requestLayout();
                    } catch (Throwable throwable) {
                        Logger.e(TAG, "onScrolled: {}", throwable);
                    }
                }
            }
        });
        SearchV2LinearLayoutManager linearLayoutManager = new SearchV2LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = getAdapter();
        //添加页面Id.
        adapter.addExtParam(BookExposureMgr.PAGE_ID_KEY, BookExposureMgr.PAGE_ID_CATEGORY);
        //添加分类Id.
        adapter.addExtParam(ListAdapter.EXT_KEY_MODULE_ID, mCategoryBean.getId());
        recyclerView.setAdapter(adapter);

        params = (CoordinatorLayout.LayoutParams) mPullLayout.getLayoutParams();
        mPullLayout.setOnScrollListener(new PullToRefreshLayout.OnScrollListener() {
            @Override
            public void onScroll(float distanceX, float distanceY) {
                if (params.getBehavior() == null) {
                    //这是appbar点击以后的展开状态
                    categoryMenuShrink();
                }
            }
        });

        promptLayout = findViewById(R.id.prompt_layout);
        loadingLayout = findViewById(R.id.loading_layout);
        floatingLayout = findViewById(R.id.floating_layout);

        toolBar = findViewById(R.id.tool_bar);
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    mPullLayout.setCanPullDown(true);
                    floatingLayout.hide();

                    if (params.getBehavior() != null) {
                        filterLayoutUp.setVisibility(View.GONE);
                    }
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    mPullLayout.setCanPullDown(false);
                    floatingLayout.display();

                    filterLayoutUp.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    mPullLayout.setCanPullDown(false);
//                    floatingLayout.hide();

                    if (params.getBehavior() != null) {
                        filterLayoutUp.setVisibility(View.GONE);
                    }
                }
            }
        });

        filterLayoutUp = findViewById(R.id.filter_layout_up);
        filterLayoutUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryMenuShrink();
            }
        });

        filterLayoutDown = findViewById(R.id.filter_layout_down);
        filterLayoutDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryMenuExpand();
            }
        });

        //标签
        tvTagsAll = findViewById(R.id.tv_tags_all);
        tvTagsAll.setOnClickListener(otherClickListener);
        xllTag = findViewById(R.id.xll_tag);
        flowLayout = findViewById(R.id.flow_tags);
        if (TextUtils.isEmpty(mCategoryBean.getTags())) {
            xllTag.setVisibility(View.GONE);
        } else {
            xllTag.setVisibility(View.VISIBLE);
            final String[] tags = mCategoryBean.getTags().split(",");
            tagList.addAll(Arrays.asList(tags));
            flowLayout.setAdapter(Arrays.asList(tags), R.layout.item_category_tag, new FlowXLayout.ItemView<String>() {
                @Override
                public void getCover(final String item, final FlowXLayout.ViewHolder holder, View inflate, int position) {
                    final TextView tv = holder.getView(R.id.tv_text);
                    tv.setText(item);
                    if (!TextUtils.isEmpty(tagn) && tv.getText().toString().trim().equals(tagn)) {
                        tvTagsAll.setBackgroundColor(ContextCompat.getColor(CategoryBookListActivity.this, R.color.white));
                        tvTagsAll.setTextColor(ContextCompat.getColor(CategoryBookListActivity.this, R.color.color_666666));

                        tv.setBackgroundResource(R.drawable.category_btn_bg);
                        tv.setTextColor(ContextCompat.getColor(CategoryBookListActivity.this, R.color.standard_red_main_color_c1));
                    }
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateTag(tv);
                        }
                    });
                }
            });
        }

        //二级分类
        tvSubCategoryAll = findViewById(R.id.tv_sub_category_all);
        tvSubCategoryAll.setOnClickListener(otherClickListener);
        xllSubCategory = findViewById(R.id.xll_sub_category);
        flowLayoutSubCategory = findViewById(R.id.flow_sub_category);
        if (mCategoryBean.getSubCategories() == null || mCategoryBean.getSubCategories().isEmpty()) {
            xllSubCategory.setVisibility(View.GONE);
        } else {
            xllSubCategory.setVisibility(View.VISIBLE);
            flowLayoutSubCategory.setAdapter(mCategoryBean.getSubCategories(), R.layout.item_category_tag, new FlowXLayout.ItemView<SubCategoriesBean>() {
                @Override
                public void getCover(final SubCategoriesBean item, final FlowXLayout.ViewHolder holder, View inflate, int position) {
                    final TextView tv = holder.getView(R.id.tv_text);
                    tv.setText(item.getName());
                    if (!TextUtils.isEmpty(categoryn) && categoryn.equals(String.valueOf(mCategoryBean.getSubCategories().get(position).getId()))) {
                        tvSubCategoryAll.setBackgroundColor(ContextCompat.getColor(CategoryBookListActivity.this, R.color.white));
                        tvSubCategoryAll.setTextColor(ContextCompat.getColor(CategoryBookListActivity.this, R.color.color_666666));
                        tv.setBackgroundResource(R.drawable.category_btn_bg);
                        tv.setTextColor(ContextCompat.getColor(CategoryBookListActivity.this, R.color.standard_red_main_color_c1));
                    }
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateSubCategories(tv, item.getId());
                        }
                    });
                }
            });
        }
    }

    private void initClickView() {
        tvPoputlarity = findViewById(R.id.category_populary);
        tvUpdateTime = findViewById(R.id.category_update);
        tvGride = findViewById(R.id.category_grade);
        tvAll = findViewById(R.id.category_all);
        tvContinue = findViewById(R.id.category_continue);
        tvFinish = findViewById(R.id.category_finish);
        tvUnLimited = findViewById(R.id.category_unlimite);
        tvCountFirst = findViewById(R.id.category_first);
        tvCountSecond = findViewById(R.id.category_second);
        tvCountThree = findViewById(R.id.category_three);
        //判断是否为男生分类.
        if (mCategoryBean != null && mCategoryBean.getSex() == StartGuideMgr.SEX_MAN) {
            tvCountFirst.setText("100万以下");
            tvCountSecond.setText("100-300万");
            tvCountThree.setText("300万以上");
        } else if (mCategoryBean != null && mCategoryBean.getSex() == StartGuideMgr.BOOK) {
            //图书分类,暂时不显示字数分类
            layoutWordCount.setVisibility(View.GONE);
        }
        tvPoputlarity.setOnClickListener(this);
        tvUpdateTime.setOnClickListener(this);
        tvGride.setOnClickListener(this);
        tvAll.setOnClickListener(this);
        tvContinue.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
        tvUnLimited.setOnClickListener(this);
        tvCountFirst.setOnClickListener(this);
        tvCountSecond.setOnClickListener(this);
        tvCountThree.setOnClickListener(this);

        //悬浮窗口中的分类按钮
        tvPoputlarityFloating = findViewById(R.id.category_populary_floating);
        tvUpdateTimeFloating = findViewById(R.id.category_update_floating);
        tvGrideFloating = findViewById(R.id.category_grade_floating);

        tvPoputlarityFloating.setOnClickListener(this);
        tvUpdateTimeFloating.setOnClickListener(this);
        tvGrideFloating.setOnClickListener(this);
    }

    private void initData() {
        presenter = new CategoryBookListPresenter(this, this, mCategoryBean);
        showLoading();
        presenter.loadPageData(sortType, firstTag, secondTag, threeTag, tag, mSubCategoryId, pageIndex, false);
    }

    public String getCurrPageId() {
        return PageNameConstants.CATEGORY_DETAIL;
    }

    private void updateTag(TextView current) {
        if (!TextUtils.equals(tag, current.getText().toString())) {
            if (TextUtils.equals(tvTagsAll.getText().toString(), current.getText().toString())) {
                //不限标签
                if (TextUtils.equals(tag, "")) {
                    return;
                }
                tag = "";
                FuncPageStatsApi.categorySelecteLabel(0);
            } else {
                tag = current.getText().toString();
                if (tagList.contains(tag) && tagList.indexOf(tag) != -1) {
                    FuncPageStatsApi.categorySelecteLabel(1 + tagList.indexOf(tag));
                }
            }
            Resources resource = getResources();
            for (int i = 0; i < flowLayout.getChildCount(); i++) {
                if (flowLayout.getChildAt(i) instanceof XFrameLayout
                        && ((XFrameLayout) flowLayout.getChildAt(i)).getChildAt(0) instanceof TextView) {
                    TextView tv = (TextView) ((XFrameLayout) flowLayout.getChildAt(i)).getChildAt(0);
                    tv.setBackgroundColor(resource.getColor(R.color.white));
                    tv.setTextColor(resource.getColor(R.color.color_666666));
                    tvTagsAll.setBackgroundColor(resource.getColor(R.color.white));
                    tvTagsAll.setTextColor(resource.getColor(R.color.color_666666));
                }
            }
            current.setBackgroundResource(R.drawable.category_btn_bg);
            current.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));

            if (params.getBehavior() == null) {
                params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
            }
            showLoading();
            onPullRefresh();
        }
    }

    /**
     * 选中二级分类
     */
    private void updateSubCategories(TextView current, int subCategoriesId) {

        if (mSubCategoryId != subCategoriesId) {

            mSubCategoryId = subCategoriesId;

            Resources resource = getResources();
            for (int i = 0; i < flowLayoutSubCategory.getChildCount(); i++) {
                if (flowLayoutSubCategory.getChildAt(i) instanceof XFrameLayout
                        && ((XFrameLayout) flowLayoutSubCategory.getChildAt(i)).getChildAt(0) instanceof TextView) {
                    TextView tv = (TextView) ((XFrameLayout) flowLayoutSubCategory.getChildAt(i)).getChildAt(0);
                    tv.setBackgroundColor(resource.getColor(R.color.white));
                    tv.setTextColor(resource.getColor(R.color.color_666666));
                    tvSubCategoryAll.setBackgroundColor(resource.getColor(R.color.white));
                    tvSubCategoryAll.setTextColor(resource.getColor(R.color.color_666666));
                }
            }
            current.setBackgroundResource(R.drawable.category_btn_bg);
            current.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));

            if (params.getBehavior() == null) {
                params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
            }
            showLoading();
            onPullRefresh();
            FuncPageStatsApi.categorySelecteLabel2(mSubCategoryId);
        }
    }

    View.OnClickListener otherClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_tags_all:
                    updateTag((TextView) v);
                    break;
                case R.id.tv_sub_category_all:
                    updateSubCategories((TextView) v, 0);
                    break;
            }
        }
    };

    private RecyclerAdapter getAdapter() {
        return new AdapterBuilder()
                .putItemClass(CategoryBookItemView.class)
                .putItemClass(AdListItemView.class)
                .builderRecyclerAdapter(getActivity());
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
        adapter.setData(bookList);
        presenter.loadPageData(sortType, firstTag, secondTag, threeTag, tag, mSubCategoryId, pageIndex, false);
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);

        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    protected PromptLayoutHelper getLoadingLayoutHelper() {
        View promptView = findView(R.id.loading_prompt_layout);

        if (mLoadingLayoutHelper == null) {
            mLoadingLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mLoadingLayoutHelper;
    }

    AppBarLayout.Behavior.DragCallback dragCallback = new AppBarLayout.Behavior.DragCallback() {
        @Override
        public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
            return canAppBarLayoutScroll;
        }
    };

    @Override
    public void showPage(List<Object> list) {
        if (list != null) {
            if (pageIndex == 1) {
                bookList.addAll(list);
                //清理已记录曝光的书籍列表, 刷新数据重新开始计算.
                BookExposureMgr.refreshBookData(BookExposureMgr.PAGE_ID_CATEGORY);
            } else {
                List<Object> list1 = ReduceUtils.booksCategoryToRepeat(bookList, list);
                bookList.addAll(list1);
            }

            adapter.setData(bookList);

            //如果该分类图书较少，不到一页，就禁止上滑加载更多，优化体验。
            if (pageIndex == 1 && presenter.getNextPage() == -1) {
                mPullLayout.setCanPullUp(false);
            } else {
                mPullLayout.setCanPullUp(true);
            }

            promptLayout.setVisibility(View.GONE);
            canAppBarLayoutScroll = true;
            if (appBarLayoutBehavior == null) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                appBarLayoutBehavior = (AppBarLayout.Behavior) params.getBehavior();
                appBarLayoutBehavior.setDragCallback(dragCallback);
            }
            //默认折叠状态.
            //categoryMenuFold();
            //默认展开状态.
            categoryMenuOpen();
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
        loadingLayout.setVisibility(View.VISIBLE);
        getLoadingLayoutHelper().showLoading();
    }

    @Override
    public void dismissLoading() {
        loadingLayout.setVisibility(View.GONE);
        getLoadingLayoutHelper().hide();
    }

    @Override
    public void showEmpty() {
        bookList.clear();

        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);

        promptLayout.setVisibility(View.VISIBLE);
        //当列表为空时，禁止appbarlayout滑动
        canAppBarLayoutScroll = false;
        if (appBarLayoutBehavior == null) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            appBarLayoutBehavior = (AppBarLayout.Behavior) params.getBehavior();
            appBarLayoutBehavior.setDragCallback(dragCallback);
        }
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
                presenter.loadPageData(sortType, firstTag, secondTag, threeTag, tag, mSubCategoryId, pageIndex, false);
            }
        });

        promptLayout.setVisibility(View.VISIBLE);
        //当列表为空时，禁止appbarlayout滑动
        canAppBarLayoutScroll = false;
        if (appBarLayoutBehavior == null) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            appBarLayoutBehavior = (AppBarLayout.Behavior) params.getBehavior();
            appBarLayoutBehavior.setDragCallback(dragCallback);
        }
        appBarLayout.setExpanded(true, false);
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
    public void onClick(@NotNull View v) {
        super.onClick(v);
        boolean update = false;
        switch (v.getId()) {
            case R.id.category_populary:
            case R.id.category_populary_floating:
                //按人气.
                if (secondTag == 1) {
                    break;
                }
                sortType = 1;
                update = true;
                secondTag = 1;
                changeViewBg(2, secondTag);
                FunctionStatsApi.clSortPopularityClick();
                FuncPageStatsApi.categorySelecteFirst(1);
                break;
            case R.id.category_update:
            case R.id.category_update_floating:
                //按更新
                if (secondTag == 2) {
                    break;
                }
                sortType = 2;
                update = true;
                secondTag = 2;
                changeViewBg(2, secondTag);
                FunctionStatsApi.clSortUpdateClick();
                FuncPageStatsApi.categorySelecteFirst(2);
                break;
            case R.id.category_grade:
            case R.id.category_grade_floating:
                //按评分
                if (secondTag == 3) {
                    break;
                }
                sortType = 3;
                update = true;
                secondTag = 3;
                changeViewBg(2, secondTag);
                FunctionStatsApi.clSortRatingClick();
                FuncPageStatsApi.categorySelecteFirst(3);
                break;
            case R.id.category_all:
                //全部
                if (firstTag == 1) {
                    break;
                }
                update = true;
                firstTag = 1;
                changeViewBg(1, firstTag);
                FunctionStatsApi.clFilterAllClick();
                FuncPageStatsApi.categorySelecteStatus(1);
                break;
            case R.id.category_continue:
                //连载中
                if (firstTag == 2) {
                    break;
                }
                update = true;
                firstTag = 2;
                changeViewBg(1, firstTag);
                FunctionStatsApi.clFilterLoadingClick();
                FuncPageStatsApi.categorySelecteStatus(2);
                break;
            case R.id.category_finish:
                //已完结
                if (firstTag == 3) {
                    break;
                }
                update = true;
                firstTag = 3;
                changeViewBg(1, firstTag);
                FunctionStatsApi.clFilterFinishedClick();
                FuncPageStatsApi.categorySelecteStatus(3);
                break;
            case R.id.category_unlimite:
                //不限字数
                if (threeTag == 1) {
                    break;
                }
                update = true;
                threeTag = 1;
                changeViewBg(3, threeTag);
                FunctionStatsApi.clFilterUnlimitedWordCountClick();
                FuncPageStatsApi.categorySelecteWords(1, mCategoryBean.getSex());
                break;
            case R.id.category_first:
                //30万以下
                if (threeTag == 2) {
                    break;
                }
                update = true;
                threeTag = 2;
                changeViewBg(3, threeTag);
                FunctionStatsApi.clFilter30WanFollowingClick();
                FuncPageStatsApi.categorySelecteWords(2, mCategoryBean.getSex());
                break;
            case R.id.category_second:
                //30~80万
                if (threeTag == 3) {
                    break;
                }
                update = true;
                threeTag = 3;
                changeViewBg(3, threeTag);
                FunctionStatsApi.clFilter30To80WanClick();
                FuncPageStatsApi.categorySelecteWords(3, mCategoryBean.getSex());
                break;
            case R.id.category_three:
                //80万以上.
                if (threeTag == 4) {
                    break;
                }
                update = true;
                threeTag = 4;
                changeViewBg(3, threeTag);
                FunctionStatsApi.clFilter80WanAboveClick();
                FuncPageStatsApi.categorySelecteWords(4, mCategoryBean.getSex());
                break;
        }
        if (update) {
            if (params.getBehavior() == null) {
                params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
            }
            switch (v.getId()) {
                case R.id.category_populary_floating:
                case R.id.category_update_floating:
                case R.id.category_grade_floating:
                    scroll();
                    break;
            }
            showLoading();
            onPullRefresh();
        }
    }

    private void changeViewBg(int group, int selectPos) {
        Resources resource = getResources();
        switch (group) {
            case 1:
                tvAll.setBackgroundColor(resource.getColor(R.color.white));
                tvAll.setTextColor(resource.getColor(R.color.color_666666));
                tvContinue.setBackgroundColor(resource.getColor(R.color.white));
                tvContinue.setTextColor(resource.getColor(R.color.color_666666));
                tvFinish.setBackgroundColor(resource.getColor(R.color.white));
                tvFinish.setTextColor(resource.getColor(R.color.color_666666));

                switch (selectPos) {
                    case 1:
                        tvAll.setBackgroundResource(R.drawable.category_btn_bg);
                        tvAll.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                    case 2:
                        tvContinue.setBackgroundResource(R.drawable.category_btn_bg);
                        tvContinue.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                    case 3:
                        tvFinish.setBackgroundResource(R.drawable.category_btn_bg);
                        tvFinish.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                }
                break;
            case 2:
                tvPoputlarity.setBackgroundColor(resource.getColor(R.color.white));
                tvPoputlarity.setTextColor(resource.getColor(R.color.color_666666));
                tvUpdateTime.setBackgroundColor(resource.getColor(R.color.white));
                tvUpdateTime.setTextColor(resource.getColor(R.color.color_666666));
                tvGride.setBackgroundColor(resource.getColor(R.color.white));
                tvGride.setTextColor(resource.getColor(R.color.color_666666));

                tvPoputlarityFloating.setBackgroundColor(resource.getColor(R.color.white));
                tvPoputlarityFloating.setTextColor(resource.getColor(R.color.color_666666));
                tvUpdateTimeFloating.setBackgroundColor(resource.getColor(R.color.white));
                tvUpdateTimeFloating.setTextColor(resource.getColor(R.color.color_666666));
                tvGrideFloating.setBackgroundColor(resource.getColor(R.color.white));
                tvGrideFloating.setTextColor(resource.getColor(R.color.color_666666));

                switch (selectPos) {
                    case 1:
                        tvPoputlarity.setBackgroundResource(R.drawable.category_btn_bg);
                        tvPoputlarity.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        tvPoputlarityFloating.setBackgroundResource(R.drawable.category_btn_bg);
                        tvPoputlarityFloating.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                    case 2:
                        tvUpdateTime.setBackgroundResource(R.drawable.category_btn_bg);
                        tvUpdateTime.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        tvUpdateTimeFloating.setBackgroundResource(R.drawable.category_btn_bg);
                        tvUpdateTimeFloating.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                    case 3:
                        tvGride.setBackgroundResource(R.drawable.category_btn_bg);
                        tvGride.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        tvGrideFloating.setBackgroundResource(R.drawable.category_btn_bg);
                        tvGrideFloating.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                }
                break;
            case 3:
                tvUnLimited.setBackgroundColor(resource.getColor(R.color.white));
                tvUnLimited.setTextColor(resource.getColor(R.color.color_666666));
                tvCountFirst.setBackgroundColor(resource.getColor(R.color.white));
                tvCountFirst.setTextColor(resource.getColor(R.color.color_666666));
                tvCountSecond.setBackgroundColor(resource.getColor(R.color.white));
                tvCountSecond.setTextColor(resource.getColor(R.color.color_666666));
                tvCountThree.setBackgroundColor(resource.getColor(R.color.white));
                tvCountThree.setTextColor(resource.getColor(R.color.color_666666));

                switch (selectPos) {
                    case 1:
                        tvUnLimited.setBackgroundResource(R.drawable.category_btn_bg);
                        tvUnLimited.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                    case 2:
                        tvCountFirst.setBackgroundResource(R.drawable.category_btn_bg);
                        tvCountFirst.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                    case 3:
                        tvCountSecond.setBackgroundResource(R.drawable.category_btn_bg);
                        tvCountSecond.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                    case 4:
                        tvCountThree.setBackgroundResource(R.drawable.category_btn_bg);
                        tvCountThree.setTextColor(resource.getColor(R.color.standard_red_main_color_c1));
                        break;
                }
                break;
        }
    }

    /**
     * 默认折叠状态
     */
    private void categoryMenuFold() {
        if (isFirst) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
            if (behavior instanceof AppBarLayout.Behavior) {
                AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
                appBarLayoutBehavior.setTopAndBottomOffset(-(appBarLayout.getHeight() - floatingLayout.getHeight()));
                floatingLayout.setTranslationY(0);
            }
            isFirst = false;
        }
    }

    /**
     * 默认打开状态
     */
    private void categoryMenuOpen() {
        if (isFirst) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
            if (behavior instanceof AppBarLayout.Behavior) {
                AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
                appBarLayoutBehavior.setTopAndBottomOffset(0);
                floatingLayout.setTranslationY(0);
            }
            isFirst = false;
        }
    }

    /**
     * 分类菜单收缩
     */
    private void categoryMenuShrink() {
        appBarLayout.setExpanded(false, false);
        if (params.getBehavior() == null) {
            params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        }
        floatingLayout.display();
    }

    /**
     * 分类菜单展开
     */
    private void categoryMenuExpand() {
        floatingLayout.hideAnimator(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (appBarLayout != null) {
                    params.setBehavior(null);
                    appBarLayout.setExpanded(true, true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (appBarLayout != null) {
                    params.setBehavior(null);
                    appBarLayout.setExpanded(true, true);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 将CoordinatorLayout整体滑动到适合显示第一本书的位置
     */
    private void scroll() {
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
            int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.setTopAndBottomOffset(-(appBarLayout.getHeight() - floatingLayout.getHeight()));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BookDetailActivity.REQUEST_CODE_READ && data != null) {
            if (data.getBooleanExtra("exit", false)) {
                bookReopendFinish();
                finish();
            }
        }
    }

    /**
     * 如果书籍重复打开则直接退出，并通知详情页退出
     */
    private void bookReopendFinish() {
        Intent intent = new Intent();
        intent.putExtra("exit", true);
        setResult(BookDetailActivity.REQUEST_CODE_READ, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onPageDestroy();
    }

    public interface Presenter {
        void loadMoreData(int pageIndex);

        void loadPageData(int type, int firstTag, int secondTag, int threeTag, String tag, int subCategoryId, final int pageIndex, boolean loadAd);

        void onPageDestroy();

        Object getAdData();

        int getNextPage();
    }
}
