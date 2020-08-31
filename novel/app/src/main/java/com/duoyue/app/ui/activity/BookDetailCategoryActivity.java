package com.duoyue.app.ui.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.TextView;
import com.duoyue.app.bean.CategoryBookBean;
import com.duoyue.app.bean.CategoryBookListBean;
import com.duoyue.app.listener.AppBarStateChangeListener;
import com.duoyue.app.presenter.BookDetailCategoryPresenter;
import com.duoyue.app.ui.view.CategoryBookItemView;
import com.duoyue.app.ui.view.CategoryBookRelativeLayout;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mod.ad.view.AdListItemView;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.LoadResult;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.RecyclerAdapter;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableRecyclerView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookDetailCategoryActivity extends BaseActivity implements BookDetailCategoryPresenter.PageView {

    private PullableRecyclerView pullableRecyclerView;
    private PullToRefreshLayout pullToRefreshLayout;

    private List<CategoryBookBean> categoryBookBeans;

    private BookDetailCategoryPresenter bookDetailCategoryPresenter;

    private int tagType = 1, tagSecondType = 1, tagThreeType = 1, nextPage = 1, type = 1, wordType = 0;

    private AppBarLayout appBarLayout;
    private String tag;
    private CoordinatorLayout.LayoutParams params;
    private RecyclerAdapter recyclerAdapter;

    private TextView tvPoputlarity, tvUpdateTime, tvGride, tvAll, tvContinue, tvFinish, tvUnLimited, tvCountFirst, tvCountSecond, tvCountThree, tvPoputlarityFloating, tvUpdateTimeFloating, tvGrideFloating;

    private XLinearLayout xLinearLayout, getxLinearLayout;

    private TextView mTv_top, mTv_down;
    private CategoryBookRelativeLayout floatingLayout;  //分类悬浮窗口
    private boolean isFirst = true; //是否第一次加载


    private PromptLayoutHelper mPromptLayoutHelper;

    private RecyclerAdapter getAdapter() {
        return new AdapterBuilder()
                .putItemClass(CategoryBookItemView.class)
                .putItemClass(AdListItemView.class)
                .builderRecyclerAdapter(getActivity());
    }


    public String getTag() {
        return tag;
    }

    @NotNull
    @Override
    public String getCurrPageId() {
        return PageNameConstants.CATEGORY;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.category_book_list_activity3);
        Intent intent = getIntent();
        if (intent != null) {
            tag = intent.getStringExtra("title");
            setToolBarLayout(tag);
        }
        initViews();
        initData();
    }

    void initData() {
        mPromptLayoutHelper = getPromptLayoutHelper();
        categoryBookBeans = new ArrayList<>();
        bookDetailCategoryPresenter = new BookDetailCategoryPresenter(this);
        bookDetailCategoryPresenter.loadBookData(tagType, tagSecondType, tagThreeType, nextPage, tag, type, wordType);
    }

    void initViews() {
        pullableRecyclerView = findViewById(R.id.list_view);
        pullToRefreshLayout = findViewById(R.id.pull_layout);
        pullToRefreshLayout.setOnRefreshListener(onRefreshListener);
//        beanList = new ArrayList<>();
//        bookDetailCategoryAdapter = new BookDetailCategoryAdapter(BookDetailCategoryActivity.this, beanList, onClickListener);
        recyclerAdapter = getAdapter();
        pullableRecyclerView.setAdapter(recyclerAdapter);


        tvPoputlarity = findViewById(R.id.category_populary);
        tvPoputlarity.setTag(1);
        tvUpdateTime = findViewById(R.id.category_update);
        tvUpdateTime.setTag(2);
        tvGride = findViewById(R.id.category_grade);
        tvGride.setTag(3);
        tvAll = findViewById(R.id.category_all);
        tvAll.setTag(1);
        tvContinue = findViewById(R.id.category_continue);
        tvContinue.setTag(2);
        tvFinish = findViewById(R.id.category_finish);
        tvFinish.setTag(3);
        tvUnLimited = findViewById(R.id.category_unlimite);
        tvUnLimited.setTag(1);
        tvCountFirst = findViewById(R.id.category_first);
        tvCountFirst.setTag(2);
        tvCountSecond = findViewById(R.id.category_second);
        tvCountSecond.setTag(3);
        tvCountThree = findViewById(R.id.category_three);
        tvCountThree.setTag(4);

        mTv_top = findViewById(R.id.tv_filter_up);
        mTv_top.setOnClickListener(onClickListener);
        mTv_down = findViewById(R.id.tv_filter_down);
        mTv_down.setOnClickListener(onClickListener);
        tvCountFirst.setText("30万以下");
        tvCountSecond.setText("30-100万");
        tvCountThree.setText("100万以上");
        //判断是否为男生分类.
//        if (mCategoryBean != null && mCategoryBean.getSex() == 0) {
//            tvCountFirst.setText("100万以下");
//            tvCountSecond.setText("100-300万");
//            tvCountThree.setText("300万以上");
//        } else if(mCategoryBean != null && mCategoryBean.getSex() == 2){
//            //图书分类,暂时不显示字数分类
//            layoutWordCount.setVisibility(View.GONE);
//        }
        tvPoputlarity.setOnClickListener(onClickListener);
        tvUpdateTime.setOnClickListener(onClickListener);
        tvGride.setOnClickListener(onClickListener);
        tvAll.setOnClickListener(onClickListener);
        tvContinue.setOnClickListener(onClickListener);
        tvFinish.setOnClickListener(onClickListener);
        tvUnLimited.setOnClickListener(onClickListener);
        tvCountFirst.setOnClickListener(onClickListener);
        tvCountSecond.setOnClickListener(onClickListener);
        tvCountThree.setOnClickListener(onClickListener);

        //悬浮窗口中的分类按钮
        tvPoputlarityFloating = findViewById(R.id.category_populary_floating);
        tvPoputlarityFloating.setTag(1);
        tvUpdateTimeFloating = findViewById(R.id.category_update_floating);
        tvUpdateTimeFloating.setTag(2);
        tvGrideFloating = findViewById(R.id.category_grade_floating);
        tvGrideFloating.setTag(3);
        tvPoputlarityFloating.setOnClickListener(onClickListener);
        tvUpdateTimeFloating.setOnClickListener(onClickListener);
        tvGrideFloating.setOnClickListener(onClickListener);


        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    pullToRefreshLayout.setCanPullDown(true);
                    floatingLayout.hide();

                    if (params.getBehavior() != null) {
                        mTv_top.setVisibility(View.GONE);
                    }
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    pullToRefreshLayout.setCanPullDown(false);
                    floatingLayout.display();

                    mTv_top.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    pullToRefreshLayout.setCanPullDown(false);
//                    floatingLayout.hide();

                    if (params.getBehavior() != null) {
                        mTv_top.setVisibility(View.GONE);
                    }
                }
            }
        });

        xLinearLayout = findViewById(R.id.xll_sub_category);
        getxLinearLayout = findViewById(R.id.xll_tag);
        xLinearLayout.setVisibility(View.GONE);
        getxLinearLayout.setVisibility(View.GONE);

        floatingLayout = findViewById(R.id.floating_layout);
        params = (CoordinatorLayout.LayoutParams) pullToRefreshLayout.getLayoutParams();
        pullToRefreshLayout.setOnScrollListener(new PullToRefreshLayout.OnScrollListener() {
            @Override
            public void onScroll(float distanceX, float distanceY) {
                if (params.getBehavior() == null) {
                    //这是appbar点击以后的展开状态
                    categoryMenuShrink();
                }
            }
        });
    }

    PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findViewById(R.id.loading_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.category_populary:
                case R.id.category_update:
                case R.id.category_grade:
                case R.id.category_populary_floating:
                case R.id.category_update_floating:
                case R.id.category_grade_floating:
                    type = (int) v.getTag();
                    tagSecondType = (int) v.getTag();
                    changeViewBg(2, (int) v.getTag());
                    break;

                case R.id.category_all:
                case R.id.category_continue:
                case R.id.category_finish:
                    changeViewBg(1, (int) v.getTag());
                    tagType = (int) v.getTag();
                    break;

                case R.id.category_unlimite:
                case R.id.category_first:
                case R.id.category_second:
                case R.id.category_three:
                    changeViewBg(3, (int) v.getTag());
                    if (v.getId() == R.id.category_unlimite) {
                        wordType = 0;
                    } else {
                        wordType = 2;
                    }
                    tagThreeType = (int) v.getTag();
                    break;

                case R.id.tv_filter_down:
                    categoryMenuExpand();
                    break;

                case R.id.tv_filter_up:
                    categoryMenuShrink();
                    break;

            }
            if (v.getId() != R.id.tv_filter_down) {
                nextPage = 1;
                bookDetailCategoryPresenter.loadBookData(tagType, tagSecondType, tagThreeType, nextPage, tag, type, wordType);
            }

        }
    };

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

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private PullToRefreshLayout.OnRefreshListener onRefreshListener = new PullToRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
            nextPage = 1;
            bookDetailCategoryPresenter.loadBookData(tagType, tagSecondType, tagThreeType, nextPage, tag, type, wordType);
        }

        @Override
        public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            bookDetailCategoryPresenter.loadBookData(tagType, tagSecondType, tagThreeType, ++nextPage, tag, type, wordType);

        }
    };

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

    @Override
    public void onLoadData(CategoryBookListBean categoryBookListBean) {

        if (nextPage == 1) {
            categoryBookBeans.clear();
            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        } else {
            pullToRefreshLayout.loadMoreFinish(categoryBookListBean.getNextPage() == -1 ? LoadResult.LOAD_MORE_FAIL_NO_DATA : PullToRefreshLayout.SUCCEED);
        }
        categoryBookBeans.addAll(categoryBookListBean.getList());

        recyclerAdapter.setData(categoryBookBeans);

        categoryMenuFold();
    }

    @Override
    public void onLoadErrorData() {
        mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPromptLayoutHelper.showLoading();
                nextPage = 1;
                bookDetailCategoryPresenter.loadBookData(tagType, tagSecondType, tagThreeType, nextPage, tag, type, wordType);
            }
        });
    }

    @Override
    public void onLoadNullData() {

        if (nextPage == 1) {
            mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
        } else {
            pullToRefreshLayout.loadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
        }
    }

    @Override
    public void onLoadNoData() {
        pullToRefreshLayout.setCanPullUp(true);
    }

    @Override
    public void showLoading() {
        mPromptLayoutHelper.showLoading();
    }

    @Override
    public void dismissLoading() {
        mPromptLayoutHelper.hideLoading();
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

}
