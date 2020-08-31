package com.duoyue.app.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.duoyue.app.bean.CategoryGroupBean;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.event.ReadingTasteEvent;
import com.duoyue.app.presenter.CategoryPresenter;
import com.duoyue.app.presenter.NewCategoryPresenter;
import com.duoyue.app.ui.adapter.category.NewCategoryAdapter;
import com.duoyue.app.ui.adapter.category.SimpleRecyclerAdapter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.Callable;


/**
 * 男女书籍分类
 */

public class BookCategoryFragment extends BaseFragment {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookCategoryFragment";
    private RecyclerView mCategoryRecyclerView;
    private static final int CATEGORY_COLUMNS = 2;
    private PromptLayoutHelper mPromptLayoutHelper;
    private NewCategoryAdapter mCategoryAdapter;
    private List<CategoryBean> mRightCategoryList;
    private boolean hasDrawed;
    /**
     * 1：男生
     * 2：女生
     * 3：图书
     */
    private int type;
    /**
     * 获取标题.
     */
    private String mTitle;


    private NewCategoryPresenter.onScollListener listener;

    @Override
    public void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.book_category_fragment);
        initView();
        loadCategoryData();
        //获取标题.
        if (type == StartGuideMgr.SEX_MAN) {
            //男生
            mTitle = ViewUtils.getString(R.string.male);
        } else if (type == StartGuideMgr.SEX_WOMAN) {
            mTitle = ViewUtils.getString(R.string.female);
        } else {
            mTitle = ViewUtils.getString(R.string.book);
        }
        //注册EventBus.
        EventBus.getDefault().register(this);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        type = args.getInt("type", StartGuideMgr.SEX_MAN);
    }

    /**
     * 初始化View.
     */
    private void initView() {
        mCategoryRecyclerView = findView(R.id.category_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), CATEGORY_COLUMNS);
        mCategoryRecyclerView.setLayoutManager(gridLayoutManager);
        mCategoryAdapter = new NewCategoryAdapter(getActivity());
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setOnItemClickListener(categoryBeanOnItemClickListener);
        mCategoryRecyclerView.addOnScrollListener(onScrollListener);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (listener != null) {
                    listener.onStopScoll();
                }

            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                if (listener != null) {
                    listener.onStartScoll();
                }
            }

        }
    };

    public void setOnScollListener(NewCategoryPresenter.onScollListener onScollListener) {
        this.listener = onScollListener;
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null && promptView != null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    public void showLoading() {
        PromptLayoutHelper helper = getPromptLayoutHelper();
        if(helper != null){
            helper.showLoading();
        }
    }

    public void dismissLoading() {
        PromptLayoutHelper helper = getPromptLayoutHelper();
        if(helper != null){
            helper.hide();
        }
    }

    /**
     * 加载分类数据.
     */
    @SuppressLint("CheckResult")
    public void loadCategoryData() {
        showLoading();
        Single.fromCallable(new Callable<List<CategoryGroupBean>>() {
            @Override
            public List<CategoryGroupBean> call() {
                return CategoryPresenter.getCategory();
            }
        }).subscribeOn(MtSchedulers.io()).
                observeOn(MtSchedulers.mainUi()).
                subscribeWith(new SingleObserver<List<CategoryGroupBean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<CategoryGroupBean> categoryGroupBeans) {
                        dismissLoading();
                        if (categoryGroupBeans != null && categoryGroupBeans.size() > 0) {
                            DataCacheManager.getInstance().setCategoryRight(categoryGroupBeans);
                            mRightCategoryList = categoryGroupBeans.get(type - 1).categoryList;
//                            mCategoryRecyclerView.addItemDecoration(new RecyclerItemDecoration(6, 2));
                            mCategoryAdapter.setListData(mRightCategoryList);
                            updateCategory();
                        } else {
                            Logger.e(TAG, "onLoadFail: 加载分类数据失败");
                        }
                        hasDrawed = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoading();
                        showNetworkError();
                    }
                });

    }


    private SimpleRecyclerAdapter.OnItemClickListener<CategoryBean> categoryBeanOnItemClickListener = new SimpleRecyclerAdapter.OnItemClickListener<CategoryBean>() {
        @Override
        public void onItemClick(CategoryBean categoryBean, int index) {
            //设置分类所属性别.
            categoryBean.setSex(type);
            //点击进入分类.
            StatisHelper.onEvent().classifyClick(categoryBean.getName());
            ActivityHelper.INSTANCE.gotoCategoryBookList(getActivity(), categoryBean);
            //点击右侧分类.
            FunctionStatsApi.cCategoryClick(categoryBean.getId());
            FuncPageStatsApi.categoryListClick(StringFormat.parseLong(categoryBean.getId(), 0));
        }
    };

    public void showNetworkError() {
        List<CategoryGroupBean> categoryRight = DataCacheManager.getInstance().getCategoryRight();
        if (categoryRight != null && categoryRight.size() > 0) {
            mRightCategoryList = categoryRight.get(type - 1).categoryList;
//                            mCategoryRecyclerView.addItemDecoration(new RecyclerItemDecoration(6, 2));
            mCategoryAdapter.setListData(mRightCategoryList);
            updateCategory();
        } else {

            PromptLayoutHelper helper = getPromptLayoutHelper();
            if(helper != null){
                helper.showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadCategoryData();
                    }
                });
            }
        }
    }

    /**
     * 更新类别
     */
    private void updateCategory() {
        //获取性别.
        long sex = StartGuideMgr.getChooseSex();
    }

    public boolean hasDrawed() {
        return hasDrawed;
    }

    /**
     * 阅读品味设置.
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void readingTasteEvent(ReadingTasteEvent event) {
        //更新分类数据.
        updateCategory();
    }

    @Override
    public String getPageName() {
        return ViewUtils.getString(R.string.tab_category) + "-" + mTitle;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销EventBus.
        EventBus.getDefault().unregister(this);
    }
}
