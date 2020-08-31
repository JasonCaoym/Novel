package com.duoyue.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import com.duoyue.app.bean.CategoryGroupBean;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.event.ReadingTasteEvent;
import com.duoyue.app.presenter.CategoryPresenter;
import com.duoyue.app.ui.adapter.category.LeftGroupAdapter;
import com.duoyue.app.ui.adapter.category.RightCategoryAdapter;
import com.duoyue.app.ui.adapter.category.RightGroupViewHolder;
import com.duoyue.app.ui.adapter.category.SimpleRecyclerAdapter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zzdm.ad.router.BaseData;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * 分类Fragment
 *
 * @author caoym
 * @data 2019/4/18  17:30
 */
public class CategoryFragment extends BaseFragment {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#CategoryFragment";

    /**
     * 栏目显示列数.
     */
    private static final int CATEGORY_COLUMNS = 2;

    /**
     * 左侧组列表
     */
    private RecyclerView mLeftGroupRecyclerView;

    /**
     * 左边分类组Adapter
     */
    private LeftGroupAdapter mLeftGroupAdapter;

    /**
     * 左边分类组信息列表
     */
    private List<CategoryGroupBean> mLeftGroupList;

    /**
     * 右边分类列表
     */
    private RecyclerView mRightCategoryRecyclerView;

    /**
     * 右边分类Adapter.
     */
    private RightCategoryAdapter mRightCategoryAdapter;

    /**
     * 右边分类信息列表
     */
    private List<CategoryBean> mRightCategoryList;

    /**
     * 用于记录左边与右边位置关系集合.
     */
    private Map<Integer, Integer> mIndexMap;

    /**
     * 最近一次点击分类时间.
     */
    private long mLastClickTime;

    private PromptLayoutHelper mPromptLayoutHelper;

    private int mIndex = -1;
    private int mTop = 0;

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateView(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setContentView(R.layout.category_fragment);
        //搜索入口.
        findView(R.id.search_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入搜索页面.
//                ActivityHelper.INSTANCE.gotoSearch(getActivity(), new BaseData(getPageName()));
                //搜索入口.
                FunctionStatsApi.cSearchClick();
            }
        });
        //初始化View.
        initView();
        //加载分类数据.
        loadCategoryData();
        //注册EventBus.
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化View.
     */
    private void initView() {
        //左侧组列表.
        mLeftGroupRecyclerView = findView(R.id.left_group_view);
        mLeftGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ((SimpleItemAnimator) mLeftGroupRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //左边分类组Adapter
        mLeftGroupAdapter = new LeftGroupAdapter();
        mLeftGroupAdapter.setListData(mLeftGroupList);
        mLeftGroupRecyclerView.setAdapter(mLeftGroupAdapter);
        // 左侧列表的点击事件
        mLeftGroupAdapter.setOnItemClickListener(new SimpleRecyclerAdapter.OnItemClickListener<CategoryGroupBean>() {
            @Override
            public void onItemClick(CategoryGroupBean groupBean, int index) {
                //左侧选中并滑到中间位置
                mLeftGroupAdapter.setSelectedPosition(index);
                moveToMiddle(mLeftGroupRecyclerView, index);
                // 右侧滑到对应位置
                mRightCategoryRecyclerView.smoothScrollToPosition(index == 0 ? 0 : mRightCategoryList.size() - 1);
//                ((GridLayoutManager) mRightCategoryRecyclerView.getLayoutManager()).scrollToPositionWithOffset(mIndexMap.get(index), 0);
                if (groupBean.groupId == 2) {
                    //点击女.
                    FunctionStatsApi.cGirlTabClick();
                } else {
                    //点击男.
                    FunctionStatsApi.cBoyTabClick();
                }
            }
        });
        //右侧分类列表.
        mRightCategoryRecyclerView = findView(R.id.right_category_view);
        // 右列表
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), CATEGORY_COLUMNS);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mRightCategoryList.get(position).getViewType() == CategoryBean.VIEW_TYPE_GROUP || mRightCategoryList.get(position).getViewType() == CategoryBean.VIEW_TYPE_NULL) {
                    return CATEGORY_COLUMNS;
                } else {
                    return 1;
                }
            }
        });
        mRightCategoryRecyclerView.setLayoutManager(gridLayoutManager);
        //右边分类Adapter
        mRightCategoryAdapter = new RightCategoryAdapter(getActivity());
        mRightCategoryAdapter.setListData(mRightCategoryList);
        mRightCategoryRecyclerView.setAdapter(mRightCategoryAdapter);
        mRightCategoryAdapter.setOnItemClickListener(new SimpleRecyclerAdapter.OnItemClickListener<CategoryBean>() {
            @Override
            public void onItemClick(CategoryBean categoryBean, int index) {
                //留白部门不能点击
                if (categoryBean.getViewType() != CategoryBean.VIEW_TYPE_NULL) {
                    if (!TimeTool.isTimeOut(mLastClickTime, 1000)) {
                        return;
                    }
                    //记录点击时间,
                    mLastClickTime = TimeTool.currentTimeMillis();
                    //点击进入分类.
                    StatisHelper.onEvent().classifyClick(categoryBean.getName());
                    ActivityHelper.INSTANCE.gotoCategoryBookList(getActivity(), categoryBean);
                    //点击右侧分类.
                    FunctionStatsApi.cCategoryClick(categoryBean.getId());
                }
            }
        });
        mRightCategoryRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取右侧列表的第一个可见Item的position
                int topPosition = ((GridLayoutManager) mRightCategoryRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                // 如果此项对应的是左边的大类的index
                if (mRightCategoryList.get(topPosition).getPosition() != -1) {
                    moveToMiddle(mLeftGroupRecyclerView, mRightCategoryList.get(topPosition).getPosition());
                    mLeftGroupAdapter.setSelectedPosition(mRightCategoryList.get(topPosition).getPosition());
                }
            }

            @Override
            public void onScrollToBottom() {
                if (mRightCategoryAdapter.getItemCount() > 0) {
                    RightGroupViewHolder holder = (RightGroupViewHolder) mRightCategoryRecyclerView.findViewHolderForAdapterPosition(mIndex - 1);
                    mTop = holder.mView.getTop();
                    if (mTop != 0) {
                        CategoryBean mCategoryBean = new CategoryBean();
                        mCategoryBean.setViewType(CategoryBean.VIEW_TYPE_NULL);
                        mCategoryBean.setNullHeight(mTop);
                        mRightCategoryList.add(mCategoryBean);
                        mRightCategoryAdapter.notifyItemInserted(mRightCategoryList.size() - 1);
                        mRightCategoryRecyclerView.smoothScrollBy(0, mTop);
                    }
                }
            }
        });
    }

    /**
     * 加载分类数据.
     */
    private void loadCategoryData() {
        showLoading();
        Single.fromCallable(new Callable<List<CategoryGroupBean>>() {

            @Override
            public List<CategoryGroupBean> call() {
                return CategoryPresenter.getCategory();
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribeWith(new SingleObserver<List<CategoryGroupBean>>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<CategoryGroupBean> categoryGroupBeans) {
                dismissLoading();
                if (categoryGroupBeans != null && categoryGroupBeans.size() > 0) {
                    mLeftGroupList = categoryGroupBeans;
                    updateCategory();
                } else {
                    Logger.e(TAG, "onLoadFail: 加载分类数据失败");
                }
            }

            @Override
            public void onError(Throwable e) {
                showNetworkError();
            }
        });

    }

    /**
     * 更新分类数据.
     */
    private void updateCategory() {
        Logger.e("time", "star : " + System.currentTimeMillis());
        if (StringFormat.isEmpty(mLeftGroupList)) {
            return;
        }
        //获取性别.
        long sex = StartGuideMgr.getChooseSex();


        Logger.i(TAG, "updateCategory: {}, {}", sex, (mLeftGroupList != null ? mLeftGroupList.size() : "NULL"));
        if (sex != StartGuideMgr.SEX_WOMAN) {
            sex = StartGuideMgr.SEX_MAN;
        }
        //将选择的性别放在前面.
        for (int index = 0; index < mLeftGroupList.size(); index++) {
            if (mLeftGroupList.get(index).groupId == sex) {
                //设置为选中状态.
                mLeftGroupList.get(index).isSelected = true;
                if (index != 0) {
                    Collections.swap(mLeftGroupList, index, 0);
                    mLeftGroupAdapter.resetSelectedPosition(0);
                }
                break;
            }
        }
        //右边分类信息列表
        mRightCategoryList = new ArrayList<>();
        CategoryBean categoryBean;
        CategoryGroupBean groupBean;


        for (int index = 0; index < mLeftGroupList.size(); index++) {
            groupBean = mLeftGroupList.get(index);
            categoryBean = new CategoryBean();
            //分类组Id.
            categoryBean.setId(String.valueOf(groupBean.groupId));
            //分类组名称.
            categoryBean.setName(groupBean.groupName);
            //设置View类型为分类组.
            categoryBean.setViewType(CategoryBean.VIEW_TYPE_GROUP);
            //标记分类组的位置，所有项的position默认是-1，如果是大类就添加position, 让它和左侧位置对应.
            categoryBean.setPosition(index);
            mRightCategoryList.add(categoryBean);
            Log.i("fsdf", "updateCategory: " + mRightCategoryList.size());
            mIndex = mRightCategoryList.size();

            List<CategoryBean> tmpCategoryList = groupBean.categoryList;
            for (int i = 0; i < tmpCategoryList.size(); i++) {
                categoryBean = tmpCategoryList.get(i);
                //设置View类型为分类.
                categoryBean.setViewType(CategoryBean.VIEW_TYPE_CATEGORY);
                mRightCategoryList.add(categoryBean);
            }

        }
        //用于记录左边与右边位置关系集合.
        mIndexMap = new HashMap<>();
        //点击左侧需要知道对应右侧的位置，用map先保存起来
        for (int i = 0; i < mRightCategoryList.size(); i++) {
            //获取分类位置.
            int position = mRightCategoryList.get(i).getPosition();
            //判断是否为分类组.
            if (position >= 0) {
                mIndexMap.put(position, i);
            }
        }

        //右侧列表滑到初始位置,避免选择阅读口味后造成选中错误问题
        mRightCategoryRecyclerView.scrollToPosition(0);

        //刷新左边分类组数据.
        if (mLeftGroupAdapter != null) {
            mLeftGroupAdapter.setListData(mLeftGroupList);
        }
        //刷新右边分类数据.
        if (mRightCategoryAdapter != null) {
            mRightCategoryAdapter.setListData(mRightCategoryList);
        }
        Logger.e("time", "end : " + System.currentTimeMillis());
    }

    /**
     * 阅读品味设置.
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void readingTasteEvent(ReadingTasteEvent event) {
        if (!StringFormat.isEmpty(mLeftGroupList)) {
            //初始化为非选中状态.
            mTop = 1;
            CategoryBean categoryBean = mRightCategoryList.get(mRightCategoryList.size() - 1);
            if (categoryBean.getViewType() == CategoryBean.VIEW_TYPE_NULL) {
                mRightCategoryAdapter.notifyItemRemoved(mRightCategoryList.size() - 1);
                mRightCategoryList.remove(mRightCategoryList.size() - 1);
            }
            for (CategoryGroupBean categoryGroupBean : mLeftGroupList) {
                categoryGroupBean.isSelected = false;
            }
        }
        //更新分类数据.
        updateCategory();
    }

    /**
     * 登录成功.
     */
    public void onLoginSucc() {
        //重新更新数据
        loadCategoryData();
    }

    private static void moveToMiddle(RecyclerView recyclerView, int position) {
        //先从RecyclerView的LayoutManager中获取当前第一项和最后一项的Position
        int firstItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        //中间位置
        int middle = (firstItem + lastItem) / 2;
        //取绝对值, index下标是当前的位置和中间位置的差，下标为index的view的top就是需要滑动的距离
        int index = (position - middle) >= 0 ? position - middle : -(position - middle);
        if (index >= recyclerView.getChildCount()) {
            recyclerView.scrollToPosition(position);
        } else {
            //如果当前位置在中间位置上面, 往下移动, 这里为了防止越界
            if (position < middle) {
                recyclerView.scrollBy(0, -recyclerView.getChildAt(index).getTop());
                // 在中间位置的下面，往上移动
            } else {
                recyclerView.scrollBy(0, recyclerView.getChildAt(index).getTop());
            }
        }
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findView(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }


    public void showLoading() {
        getPromptLayoutHelper().showLoading();
    }

    public void dismissLoading() {
        getPromptLayoutHelper().hide();
    }

    public void showEmpty() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
    }

    public void showNetworkError() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadCategoryData();
            }
        });
    }

    /**
     * 获取当前Tab名称
     *
     * @return
     */
    @Override
    public String getPageName() {
        return ViewUtils.getString(R.string.tab_category);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销EventBus.
        EventBus.getDefault().unregister(this);
    }
}
