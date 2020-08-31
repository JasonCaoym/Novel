package com.duoyue.app.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.presenter.ReadHistoryPresenter;
import com.duoyue.app.ui.adapter.ReadHistoryAdapter;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.LoadResult;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;
import com.zydm.base.data.dao.ShelfEvent;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableListView;
import com.zzdm.ad.router.BaseData;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 阅读历史
 *
 * @author caoym
 * @data 2019/4/16  19:13
 */
public class ReadHistoryActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, BookShelfHelper.ShelfDaoObserver, BookRecordHelper.RecordDaoObserver {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#ReadHistoryActivity";

    /**
     * 刷下历史记录列表.
     */
    private PullToRefreshLayout mPullToRefreshLayout;

    /**
     * 阅读历史列表Adapter
     */
    private ReadHistoryAdapter mReadHistoryAdapter;

    /**
     * 阅读历史列表ListView.
     */
    private PullableListView mHistoryListView;

    /**
     * 是否响应长按事件.
     */
    private boolean isRespLongPress;

    /**
     * Handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_history);
        //清空历史记录按钮.
        TextView clearAllBtn = findViewById(R.id.toolbar_right_tv);
        //设置按钮文本.
        clearAllBtn.setText(R.string.delete_all_history);
        //显示清空按钮.
        clearAllBtn.setVisibility(View.VISIBLE);
        clearAllBtn.setOnClickListener(this);
        //逛书城.
        findViewById(R.id.rh_go_book_city_btn).setOnClickListener(this);
        //阅读历史刷新组件.
        mPullToRefreshLayout = findViewById(R.id.rh_pull_layout);
        mPullToRefreshLayout.setOnRefreshListener(this);
        mPullToRefreshLayout.setCanPullDown(false);
        //设置滑动监听.
        mPullToRefreshLayout.setOnScrollListener(new PullToRefreshLayout.OnScrollListener() {
            @Override
            public void onScroll(float distanceX, float distanceY) {
                //发生上下拉倒, 不响应点击事件.
                isRespLongPress = false;
            }
        });
        //查询出所有的阅读历史记录.
        List<BookRecordBean> bookRecordBeanList = ReadHistoryPresenter.getPageHistoryDataList(0);
        //判断是否需要重新请求数据.
        if (StringFormat.isEmpty(bookRecordBeanList)) {
            //更新阅读历史数据.
            ReadHistoryMgr.updateRecordBookList();
        }
        //创建Adapter
        mReadHistoryAdapter = new ReadHistoryAdapter(this, bookRecordBeanList);
        //是否显示无书籍提示页面.
        isShowNoDataPage();
        //阅读历史ListView.
        mHistoryListView = findViewById(R.id.rh_list_view);
        //设置Adapter.
        mHistoryListView.setAdapter(mReadHistoryAdapter);
        //设置Touch事件.
        mHistoryListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //初始化响应长按事件标识.
                    isRespLongPress = true;
                }
                return false;
            }
        });
        //mHistoryListView.setOnScrollListener(null);
        //设置点击事件.
        mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isFastClick()) {
                    return;
                }
                try {
                    //获取阅读历史记录.
                    BookRecordBean bookRecordBean = mReadHistoryAdapter.getItem(position);
                    ActivityHelper.INSTANCE.gotoRead(ReadHistoryActivity.this, bookRecordBean.getBookId(),
                            new BaseData(getPageName()), PageNameConstants.READ_HISTORY, "");
                } catch (Throwable throwable) {
                    Logger.e(TAG, "onItemClick: {}, {}, {}", view, position, throwable);
                }
            }
        });
        //设置长按事件.
        mHistoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (isRespLongPress) {
                    //移除阅读历史记录.
                    removeConfirmation(false, mReadHistoryAdapter.getItem(position));
                }
                return isRespLongPress;
            }
        });
        //注册书架数据库数据变化监听器.
        BookShelfHelper.getsInstance().addObserver(this);
        //注册阅读历史数据库数据变化监听器.
        BookRecordHelper.getsInstance().addObserver(this);
    }

    @Override
    public void initStateBar(@Nullable View layoutTitle) {
        super.initStateBar(layoutTitle);
        setToolBarLayout(R.string.read_record);
    }

    public String getCurrPageId() {
        return PageNameConstants.READ_HISTORY;
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        //回调无数据接口.
        if (mPullToRefreshLayout != null) {
            mPullToRefreshLayout.refreshFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<BookRecordBean> bookRecordList = null;
                //调用更新数据接口.
                if (mReadHistoryAdapter != null) {
                    //查询出阅读历史记录信息.
                    bookRecordList = ReadHistoryPresenter.getPageHistoryDataList(mReadHistoryAdapter.getCount());
                    //更新Adapter数据.
                    mReadHistoryAdapter.addAllData(bookRecordList);
                }
                //是否显示无数据提示页面.
                isShowNoDataPage();
                //加载更多数据完成.
                mPullToRefreshLayout.loadMoreFinish(bookRecordList != null && !bookRecordList.isEmpty() ? LoadResult.LOAD_MORE_SUCCEED : LoadResult.LOAD_MORE_FAIL_NO_DATA);
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.toolbar_right_tv:
                //清空所有阅读历史记录.
                if (mReadHistoryAdapter != null && mReadHistoryAdapter.getCount() > 0) {
                    //显示确认框.
                    removeConfirmation(true, null);
                }
                break;
            case R.id.rh_go_book_city_btn:
                //逛书城.
                ActivityHelper.INSTANCE.gotoHome(this, new BaseData(getCurrPageName()));
                FuncPageStatsApi.bookCityShow(6);
                break;
        }
    }

    /**
     * 移除阅读历史记录
     *
     * @param isRemoveAll    是否移除所有历史记录
     * @param bookRecordBean 需要移除的书籍.
     */
    private void removeReadHistory(final boolean isRemoveAll, final BookRecordBean bookRecordBean) {
        if (!isRemoveAll && (bookRecordBean == null || StringFormat.isEmpty(bookRecordBean.getBookId()))) {
            Logger.e(TAG, "removeReadHistory: 书籍Id不能为空.");
            return;
        }
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() {
                //调用删除阅读历史记录接口.
                String result = isRemoveAll ? ReadHistoryMgr.removeAllReadHistory() : ReadHistoryMgr.removeReadHistory(Long.parseLong(bookRecordBean.getBookId()));
                if (ReadHistoryMgr.HTTP_OK.equals(result)) {
                    //删除成功.
                    if (isRemoveAll) {
                        //清理数据库.
                        BookRecordHelper.getsInstance().removeAllBook();
                    } else {
                        //清理数据库.
                        BookRecordHelper.getsInstance().removeBook(bookRecordBean.getBookId());
                    }
                }
                return result;
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String result) {
                if (ReadHistoryMgr.HTTP_OK.equals(result)) {
                    //刷新Adapter.
                    if (mReadHistoryAdapter != null) {
                        if (isRemoveAll) {
                            //删除所有.
                            mReadHistoryAdapter.clearAllData();
                        } else {
                            //删除指定书籍.
                            mReadHistoryAdapter.removeData(bookRecordBean.getBookId());
                        }
                        //是否显示无数据提示页面.
                        isShowNoDataPage();
                    }
                } else {
                    //显示失败提示.
                    ToastUtils.show(result);
                }
            }
        });
    }

    /**
     * 确认是否需要删除阅读历史记录.
     *
     * @param isAll          是否删除所有历史记录
     * @param bookRecordBean
     */
    private void removeConfirmation(final boolean isAll, final BookRecordBean bookRecordBean) {
        try {
            String message = isAll ? ViewUtils.getString(R.string.remove_all_reading_record) : ViewUtils.getString(R.string.remove_reading_record);
            SimpleDialog simpleDialog = new SimpleDialog.Builder(this).setCanceledOnTouchOutside(false).setTitle(message).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    removeReadHistory(isAll, bookRecordBean);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }).create();
            //显示Dialog.
            simpleDialog.show();
        } catch (Throwable throwable) {
            Logger.e(TAG, "removeConfirmation: {}, {}, {}", isAll, bookRecordBean, throwable);
        }
    }


    /**
     * 是否需要显示无数据提示页面.
     */
    private void isShowNoDataPage() {
        if (mReadHistoryAdapter == null || mPullToRefreshLayout == null) {
            return;
        }
        if (mReadHistoryAdapter.getCount() > 0) {
            //显示阅读历史记录列表.
            if (mPullToRefreshLayout.getVisibility() != View.VISIBLE) {
                mPullToRefreshLayout.setVisibility(View.VISIBLE);
            }
        } else {
            //隐藏阅读历史记录列表, 显示无数据提示页面.
            if (mPullToRefreshLayout.getVisibility() == View.VISIBLE) {
                mPullToRefreshLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRecordChange(BookRecordBean recordBean) {
        //更新阅读历史记录.
        if (mReadHistoryAdapter != null && recordBean != null) {
            mReadHistoryAdapter.updateReadHistory(recordBean);
        }
    }

    @Override
    public void onShelfChange(@NonNull ShelfEvent event) {
        if (event.mType == ShelfEvent.TYPE_ADD && mReadHistoryAdapter != null) {
            //添加书架.
            BookShelfBean bookShelfBean = event.mChangeList != null && !event.mChangeList.isEmpty() ? event.mChangeList.get(0) : null;
            if (bookShelfBean == null) {
                return;
            }
            //刷新Adapter.
            mReadHistoryAdapter.addBookShelf(bookShelfBean.getBookId());
        }
    }

    /**
     * 获取当前Activity名称
     *
     * @return
     */
    public static String getCurrPageName() {
        return ViewUtils.getString(R.string.read_history);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销书架数据库数据变化监听.
        BookShelfHelper.getsInstance().removeObserver(this);
        //注销阅读历史数据库数据变化监听.
        BookRecordHelper.getsInstance().removeObserver(this);
    }
}
