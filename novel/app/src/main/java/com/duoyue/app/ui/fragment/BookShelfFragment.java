package com.duoyue.app.ui.fragment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.data.response.bookshelf.BookShelfAdInfoResp;
import com.duoyue.app.common.data.response.bookshelf.BookShelfBookInfoResp;
import com.duoyue.app.common.data.response.bookshelf.BookShelfListResp;
import com.duoyue.app.common.data.response.bookshelf.BookShelfRecoInfoResp;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.common.mgr.BookShelfMgr;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.common.mgr.TaskMgr;
import com.duoyue.app.event.ReadingTasteEvent;
import com.duoyue.app.event.TabSwitchEvent;
import com.duoyue.app.event.TaskFinishEvent;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.app.ui.activity.SettingActivity;
import com.duoyue.app.ui.activity.TaskWebViewActivity;
import com.duoyue.app.ui.adapter.BookShelfRecyclerAdapter;
import com.duoyue.app.ui.view.BookShelfView;
import com.duoyue.app.ui.view.XCustomBanner;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.customshare.CustomShareManger;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.lib.base.widget.marqueeview.MarqueeView;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.data.bean.SignBean;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookRecordHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.TypefaceHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.platform.IAdView;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.LoadResult;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;
import com.zydm.base.data.dao.ShelfEvent;
import com.zydm.base.event.BookShelfUpdateEvent;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.ui.fragment.BaseFragment;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.BottomShareDialog;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.base.widgets.refreshview.PullToRefreshLayout;
import com.zydm.base.widgets.refreshview.PullableRecyclerView;
import com.zzdm.ad.router.BaseData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

/**
 * 书架Fragment
 *
 * @author caoym
 * @data 2019/4/10  20:57
 */
public class BookShelfFragment extends BaseFragment implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, BookRecordHelper.RecordDaoObserver, BookShelfHelper.ShelfDaoObserver, BookShelfView {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#BookShelfFragment";

    /**
     * 是否为编辑状态.
     */
    private boolean isEditMode = false;

    /**
     * 是否为全选状态.
     */
    private boolean isSelectAll;

    /**
     * AppBarLayout
     */
    private AppBarLayout mAppBarLayout;

    /**
     * 加载状态提示类.
     */
    private PromptLayoutHelper mPromptLayoutHelper;

    /**
     * 刷下书籍列表.
     */
    private PullToRefreshLayout mPullToRefreshLayout;

    /**
     * 书籍列表Adapter
     */
    private BookShelfRecyclerAdapter mBookAdapter;

    /**
     * 广告Adapter
     */
    private AdAdapter mAdAdapter;

    /**
     * 书籍列表ListView.
     */
    private PullableRecyclerView mBookRecyclerView;

    /**
     * 滚动广告的布局
     */
    private XLinearLayout recommendLayout;

    /**
     * 广告推荐ListView
     */
    private ListView mAdListView;

    /**
     * 广告滚动组件.
     */
    private MarqueeView mMarqueeView;

    /**
     * 阅读时长.
     */
    private TextView mReadingTimeTextView;

    /**
     * 头部默认模式操作栏.
     */
    private View mTopDefaultBarView;

    /**
     * 头部编辑模式操作栏.
     */
    private View mTopEditBarView;

    /**
     * 标题
     */
    private TextView mTitleTextView;

    /**
     * 签到按钮
     */
    private XRelativeLayout bsSignLayout;

    /**
     * 签到红点
     */
    private ImageView bsSignPoint;

    /**
     * 编辑状态-标题
     */
    private TextView mEditTextView;

    /**
     * 编辑状态底部操作栏
     */
    private View mBottomEditBarView;

    /**
     * 全选/取消全选按钮
     */
    private TextView mSelectAllTextView;

    /**
     * 置顶按钮
     */
    private TextView mToppingTextView;

    /**
     * 移除书架书籍
     */
    private TextView mRemoveBookTextView;

    /**
     * 分享按钮
     */
    private TextView mShareTextView;

    /**
     * 广告容器.
     */
    private ViewGroup mAdContainerLayout;

    /**
     * 每日推荐书籍——布局
     */
    private XRelativeLayout bsTopRecommendLayout;
    /**
     * 每日推荐书籍——推荐人
     */
//    private TextView tvReferrer;

    /**
     * 每日推荐书籍——书名
     */
//    private TextView tvBookName;

    /**
     * 每日推荐书籍——评论
     */
//    private TextView tvBookComment;

    /**
     * 每日推荐书籍——封面
     */
//    private ImageView ivBookCover;


    private XCustomBanner xCustomBanner;

    /**
     * 书架书籍为零时显示
     */
    private NestedScrollView bsNoDataLayout;

    /**
     * 书架可展示的书籍列表.
     */
    private volatile List<BookShelfBookInfoResp> mCanShowBookList;

    /**
     * 收藏的书籍信息列表
     */
    private List<BookShelfBookInfoResp> mBookInfoRespList;

    /**
     * 每日推荐书籍list
     */
    private List<BookShelfRecoInfoResp> infoResps;
    private final SparseArray<Integer> sparseArray = new SparseArray<>();
    //    private BookShelfRecoInfoResp bookShelfRecoInfoResp;
    private BookShelfRecoInfoResp mRecommendBookInfo;

    /**
     * 每日推荐书籍的id
     */
    private long dayRecommendBookId;

    /**
     * 推荐数据列表.
     */
    private List<BookShelfBookInfoResp> mRecommendBookList;

    /**
     * 推荐的广告信息列表.
     */
    private List<BookShelfAdInfoResp> mAdInfoRespList;

    /**
     * 选中书籍列表.
     */
    private List<BookShelfBookInfoResp> mSelectBookInfoList;

    /**
     * Handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 默认状态, 顶部操作栏退出动画.
     */
    private Animation mTopDefaultOutAnim;

    /**
     * 默认状态, 顶部操作栏进入动画.
     */
    private Animation mTopDefaultInAnim;

    /**
     * 编辑状态, 顶部操作栏退出动画.
     */
    private Animation mTopOutAnim;

    /**
     * 编辑状态, 顶部操作栏进入动画.
     */
    private Animation mTopInAnim;

    /**
     * 编辑状态, 底部操作栏退出动画.
     */
    private Animation mBottomOutAnim;

    /**
     * 编辑状态, 底部操作栏进入动画.
     */
    private Animation mBottomInAnim;

    /**
     * 是否响应长按事件.
     */
    private boolean isRespLongPress;

    //private int mBarLayoutffset;
    private boolean isFirstLoad = true;

    /**
     * 广播接收器.
     */
    private BookShelfReceiver mReceiver;

    private boolean hasDrawed;

    /**
     * 每日推荐书籍评论控件——宽度
     */
    private int tvCommentWidth;

    //AppBarLayout绝对高度的“heightOffset”分之一，用来控制实现渐变效果的高度
    private int heightOffset = 8;
    private HashMap<String, Integer> txtAdMap = new HashMap<>();

    /**
     * 当天签到状态  1 未签到  2 已签到
     */
    private int signState = 1;

    /**
     * 是否显示书架推荐书籍
     */
    boolean isCanRecommend;
    /**
     * 悬浮广告位
     */
    private ImageView imageView;
    private BookSiteBean bookSiteBean;

    private ObjectAnimator animator, objectAnimator;

    private boolean isRead;
    private AdSiteBean flowAdSiteBean;

    public void setIsRead() {
        if (isRead && bookSiteBean != null) {
            isRead = false;
            FuncPageStatsApi.floatAdExpose(bookSiteBean.getSuspensionSite().getType() == 1 ? bookSiteBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.BOOKSHELF, "7 + " + PageNameConstants.BOOKSHELF + " + " + StartGuideMgr.getChooseSex());
        }
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateView(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setContentView(R.layout.fragment_book_shelf);
        //初始化View.
        initView();
        mCanShowBookList = new ArrayList<>();
        //判断网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(getContext())) {
            //显示无网络提示页面.
            dismissLoading();
//            mRecommendBookInfo = DataCacheManager.getInstance().getBookShelfRecoInfoResp();
////            if (mRecommendBookInfo != null) {
////                updateRecommendBook();
////            }
            List<BookShelfAdInfoResp> bookShelfAdInfoResp = DataCacheManager.getInstance().getBookShelfAdInfoResp();
            if (bookShelfAdInfoResp != null && bookShelfAdInfoResp.size() > 0) {
                updateAdData(bookShelfAdInfoResp);
            }

            //设置阅读时长.
            if (mReadingTimeTextView != null) {
                mReadingTimeTextView.setText(String.valueOf(BookShelfPresenter.getWeekReadTime()));
            }

            List<BookShelfBean> bookShelfBookList = BookShelfPresenter.getBookShelfBookList();
            if (bookShelfBookList != null && bookShelfBookList.size() > 0) {
                mBookInfoRespList = new ArrayList<>();
                for (BookShelfBean bookShelfBean : bookShelfBookList) {
                    mBookInfoRespList.add(new BookShelfBookInfoResp(bookShelfBean));
                }
                initCanShowBookList();
                updateBookData(true);
            } else {
                showNetworkError();
            }
        }
        //获取签到状态
        BookShelfPresenter.requestSignState(this);
        //调用加载书架书籍数据接口.
        loadBookData();
        //注册书架书籍记录变化广播.
        BookShelfHelper.getsInstance().addObserver(BookShelfFragment.this);
        //注册阅读历史记录变化广播.
        BookRecordHelper.getsInstance().addObserver(BookShelfFragment.this);
        //注册广播.
        registerReceiver();
        //获取广告位
        initSite();
    }

    /**
     * 初始化View.
     */
    private void initView() {
        xCustomBanner = findView(R.id.banner);

        bsSignLayout = findView(R.id.bs_sign_layout);
        bsSignPoint = findView(R.id.bs_sign_point);
        imageView = findView(R.id.recommend_float_button);
        imageView.setOnClickListener(this);
        //签到
        findView(R.id.bs_sign_layout).setOnClickListener(this);
        //阅读历史入口.
        findView(R.id.bs_read_history_layout).setOnClickListener(this);
        //搜索入口.
        findView(R.id.bs_search_layout).setOnClickListener(this);
        //阅读时长.
        mReadingTimeTextView = findView(R.id.reading_time_textview);
        mReadingTimeTextView.setTypeface(TypefaceHelper.get(getContext(), "fonts/DINCond-Bold.ttf"));
        //标题.
        mTitleTextView = findView(R.id.bs_title_textview);
        //头部默认模式操作栏Layout.
        mTopDefaultBarView = findView(R.id.bs_top_default_layout);
        //头部编辑模式操作栏Layout.
        mTopEditBarView = findView(R.id.bs_top_edit_layout);
        //取消编辑.
        findView(R.id.bs_cancel_textview).setOnClickListener(this);
        //编辑书籍.
        mEditTextView = findView(R.id.bs_edit_textview);
        //底部编辑栏Layout.
        mBottomEditBarView = getActivity().findViewById(R.id.bs_remove_layout);
        //选中所有按钮
        mSelectAllTextView = getActivity().findViewById(R.id.bs_select_all);
        mSelectAllTextView.setOnClickListener(this);
        //置顶按钮
        mToppingTextView = getActivity().findViewById(R.id.bs_topping);
        mToppingTextView.setOnClickListener(this);
        //移除书架书籍
        mRemoveBookTextView = getActivity().findViewById(R.id.bs_remove);
        mRemoveBookTextView.setOnClickListener(this);
        //分享按钮
        mShareTextView = getActivity().findViewById(R.id.bs_share);
        mShareTextView.setOnClickListener(this);
        //广告容器.
        mAdContainerLayout = findView(R.id.bs_ad_container_layout);
        //书架书籍为零
        bsNoDataLayout = findView(R.id.bs_no_data_layout);

//        //每日推荐——布局
        bsTopRecommendLayout = findView(R.id.bs_top_recommend_layout);
//        bsTopRecommendLayout.setOnClickListener(this);
//        //每日推荐——推荐人
//        tvReferrer = findView(R.id.tv_referrer);
//        //每日推荐——书名
//        tvBookName = findView(R.id.tv_book_name);
//        //每日推荐——简介
//        tvBookComment = findView(R.id.tv_book_comment);
//        //每日推荐——封面
//        ivBookCover = findView(R.id.iv_book_cover);

        //推荐广告布局
        recommendLayout = findView(R.id.bs_recommend_layout);
        //展开推荐广告按钮.
        findView(R.id.bs_open_recommend_layout).setOnClickListener(this);
        //收起推荐广告按钮.
        findView(R.id.bs_close_recommend_layout).setOnClickListener(this);
        //去书城按钮.
        findView(R.id.bs_go_book_city_btn).setOnClickListener(this);
        //书籍刷新组件.
        mPullToRefreshLayout = findView(R.id.bs_pull_layout);
        //设置不能下滑手势刷新.
        mPullToRefreshLayout.setCanPullDown(false);
        mPullToRefreshLayout.setCanPullUp(false);
        mPullToRefreshLayout.setOnRefreshListener(this);
        //设置滑动监听.
        mPullToRefreshLayout.setOnScrollListener(new PullToRefreshLayout.OnScrollListener() {
            @Override
            public void onScroll(float distanceX, float distanceY) {
                //发生上下拉倒, 不响应点击事件.
                isRespLongPress = false;
            }
        });
        //书籍ListView.
        mBookRecyclerView = findView(R.id.bs_book_listview);
        GridLayoutManager layoutManage = new GridLayoutManager(getContext(), 3);
        mBookRecyclerView.setLayoutManager(layoutManage);
        //创建Adapter
        mBookAdapter = new BookShelfRecyclerAdapter(getActivity());
        //设置Touch事件.
        mBookAdapter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //初始化响应长按事件标识.
                    isRespLongPress = true;
                }
                return false;
            }
        });
        //设置点击书籍事件.
        mBookAdapter.setClickBookListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击书籍.
                clickBook(v);
            }
        });
        //设置长按事件.
        mBookAdapter.setLongClickBookListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isRespLongPress) {
                    //判断是否为编辑状态.
                    if (isEditMode) {
                        //编辑状态, 不需要处理.
                        return false;
                    }
                    //进入编辑状态.
                    final BookShelfBookInfoResp bookShelfInfoResp = view.getTag() != null && view.getTag() instanceof BookShelfBookInfoResp ? (BookShelfBookInfoResp) view.getTag() : null;
                    toEditMode(bookShelfInfoResp);
                    //长按编辑.
                    FunctionStatsApi.bsLongEditClick();
                    FuncPageStatsApi.bookShelfEdit();
                }
                return isRespLongPress;
            }
        });
        //设置Adapter.
        mBookRecyclerView.setAdapter(mBookAdapter);
        //创建广告推荐Adapter
        mAdAdapter = new AdAdapter(null);
        //广告推荐ListView.
        mAdListView = findView(R.id.bs_recommend_listview);
        mAdListView.setAdapter(mAdAdapter);
        //设置点击事件.
        mAdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View wordTextView = view.findViewById(R.id.bs_word_textview);
                if (wordTextView == null || wordTextView.getTag() == null || !(wordTextView.getTag() instanceof BookShelfAdInfoResp)) {
                    Logger.e(TAG, "onItemClick: 点击广告失败, 无法获取到广告信息:{}", wordTextView != null ? wordTextView.getTag() : "NULL");
                    return;
                }
                //点击推荐广告.
                clickAd((BookShelfAdInfoResp) wordTextView.getTag(), false);
            }
        });
        //广告滚动组件.
        mMarqueeView = findView(R.id.bs_marquee_view);
        //设置点击事件.
        mMarqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, TextView textView) {
                //获取点击的广告信息.
                BookShelfAdInfoResp adInfoResp = mAdInfoRespList != null && mAdInfoRespList.size() > position ? mAdInfoRespList.get(position) : null;
                if (adInfoResp == null) {
                    Logger.e(TAG, "onItemClick: 点击推荐广告失败, 广告对象为空:{}, {}", position, mAdInfoRespList != null ? mAdInfoRespList.size() : "NULL");
                    return;
                }
                //点击推荐广告.
                clickAd(adInfoResp, true);
            }
        });
        //设置滚动监听.
        mMarqueeView.setOnScrollListener(new MarqueeView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(int position, TextView textView) {
                //获取点击的广告信息.
                BookShelfAdInfoResp adInfoResp = mAdInfoRespList != null && mAdInfoRespList.size() > position ? mAdInfoRespList.get(position) : null;
                if (adInfoResp == null) {
                    Logger.e(TAG, "onScrollStateChanged: 推荐广告曝光失败, 广告对象为空:{}, {}", position, mAdInfoRespList != null ? mAdInfoRespList.size() : "NULL");
                    return;
                }
                //推荐广告曝光(H5类型, 默认上报-100).
                if (!txtAdMap.containsKey(adInfoResp.getJumpType() == 1 ? ("" + adInfoResp.getBookId()) : adInfoResp.getLink())) {
                    txtAdMap.put(adInfoResp.getJumpType() == 1 ? ("" + adInfoResp.getBookId()) : adInfoResp.getLink(), 0);
                    FunctionStatsApi.bsTextCarouselExposure(adInfoResp.getJumpType() == 1 ? adInfoResp.getBookId() : -100);
                    FuncPageStatsApi.bookShelfTxtAdShow(adInfoResp.getJumpType() == 1 ? adInfoResp.getBookId() : -100);
                }
            }
        });
        //获取AppBarLayout
        mAppBarLayout = findView(R.id.book_shelf_appbar);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    //verticalOffset  当前偏移量 appBarLayout.getTotalScrollRange() 最大高度 便宜值
                    int Offset = Math.abs(verticalOffset); //目的是将负数转换为绝对正数；
                    // 当前最大高度便宜值除以2 在减去已偏移值 获取浮动 先显示在隐藏
                    if (appBarLayout.getTotalScrollRange() <= 0 || Offset <= 0) {
                        findView(R.id.bs_default_title_layout).setBackgroundColor(changeAlpha(getResources().getColor(R.color.white), 0));
                        mTitleTextView.setText("");
                    } else {
                        //标题栏的渐变
                        findView(R.id.bs_default_title_layout).setBackgroundColor(changeAlpha(getResources().getColor(R.color.white)
                                , Math.abs(verticalOffset * 1.0f) / (appBarLayout.getTotalScrollRange() / heightOffset) < 1 ?
                                        Math.abs(verticalOffset * 1.0f) / (appBarLayout.getTotalScrollRange() / heightOffset) : 1));
                        mTitleTextView.setText(R.string.book_shelf);
                        float floate = (Offset - appBarLayout.getTotalScrollRange() / heightOffset) * 1.0f / (appBarLayout.getTotalScrollRange() / heightOffset);
                        mTitleTextView.setAlpha(floate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mBookRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);

                //判断是否为停止滑动状态
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    //防止重复跳动
                    if (animator != null) {
                        animator.cancel();
                    }
                    objectAnimator = ObjectAnimator.ofFloat(imageView, "translationX", imageView.getTranslationX(), 0f);
                    objectAnimator.setDuration(600);
                    objectAnimator.start();
                } else if (scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //防止重复跳动
                    if (objectAnimator != null) {
                        objectAnimator.cancel();
                    }
                    animator = ObjectAnimator.ofFloat(imageView, "translationX", imageView.getTranslationX(), 400f);
                    animator.setDuration(200);
                    animator.start();
                }
            }
        });
//        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
//            @Override
//            public void onStateChanged(AppBarLayout appBarLayout, State state) {
//                if (state == State.COLLAPSED) {
//                    //折叠状态
//                    mTitleTextView.setText(R.string.book_shelf);
//                    //设置背景.
//                    findView(R.id.bs_default_title_layout).setBackgroundColor(ViewUtils.getColor(R.color.white));
//                } else if (state == State.EXPANDED) {
//                    //展开状态.
//                    mTitleTextView.setText("");
//                    //设置背景透明.
//                    findView(R.id.bs_default_title_layout).setBackgroundColor(ViewUtils.getColor(R.color.transparent));
//                } else {
////                    //中间状态
//                    mTitleTextView.setText("");
////                    //设置背景透明.
////                    findView(R.id.bs_default_title_layout).setBackgroundColor(ViewUtils.getColor(R.color.white_70));
//                }
//            }
//        });
        //注册EventBus.
        EventBus.getDefault().register(this);

        //是否显示书架推荐书籍
        isCanRecommend = SPUtils.INSTANCE.getBoolean(SettingActivity.SETTING_BOOKSHELF_RECOMMEND_KEY, true);
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, 255, 255, 255);
    }

    /**
     * 加载书架书籍数据.
     */
    private void loadBookData() {
        // 判断界面是否已经被销毁
        if (getActivity() == null || !isAdded()) {
            return;
        }
        if (UserManager.getInstance().getUserInfo() == null || !StartGuideMgr.isChooseSex()) {
            //登录成功且选中性别时才调用加载书架书籍信息接口.
            return;
        }
        //判断网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(getActivity())) {
            //网络不可用.
            Logger.e(TAG, "loadBookData: 网络不可用.");
            return;
        }
        //判断可展示的书籍是否都为空.
        if (StringFormat.isEmpty(mCanShowBookList)) {
            //可展示的书籍信息为空, 显示加载中提示页面.
            showLoading();
        }
        //调用加载书架书籍信息接口.
        BookShelfPresenter.getPageBookDataList(new BookShelfPresenter.BookCallback() {
            @Override
            public void onPullBookData(BookShelfListResp bookShelfListResp) {
                if (StringFormat.isEmpty(mCanShowBookList)) {
                    if (bookShelfListResp == null || bookShelfListResp.status != 1) {
                        //请求数据失败, 显示重试页面.
                        showNetworkError();
                        return;
                    }
                }
                //重置曝光BookId.
                BookExposureMgr.refreshBookData(BookExposureMgr.BOOK_SHELF);
                hasDrawed = true;
                //请求到数据, 隐藏加载中提示页面.
                dismissLoading();
                //获取收藏的书籍列表.
                mBookInfoRespList = bookShelfListResp != null ? bookShelfListResp.getStoredBookList() : null;
//                //每日推荐图书
                if (bookShelfListResp != null && bookShelfListResp.getAppRecommendBook() != null && bookShelfListResp.getAppRecommendBook().getRecommendBookList() != null) {
                    infoResps = bookShelfListResp.getAppRecommendBook().getRecommendBookList();
                }
//                infoResps = bookShelfListResp != null ? bookShelfListResp.getAppRecommendBook().getRecommendBookList() : null;
//                //缓存每日推荐的数据
//                DataCacheManager.getInstance().setBookShelfRecoInfoResp(mRecommendBookInfo);
                //推荐书籍列表.
                mRecommendBookList = bookShelfListResp != null ? bookShelfListResp.getRecommendBookList() : null;
                //设置推荐书籍类型.
                if (!StringFormat.isEmpty(mRecommendBookList)) {
                    for (BookShelfBookInfoResp bookInfoResp : mRecommendBookList) {
                        //设置推荐类型数据为2.
                        bookInfoResp.setType(2);
                    }
                }
                //初始化推荐的书籍.
                initCanShowBookList();
                //修改周阅读时长.
                BookShelfPresenter.updateWeekReadTime(false, bookShelfListResp.getWeekTotalReadTime());
                //设置阅读时长.
                if (mReadingTimeTextView != null) {
                    mReadingTimeTextView.setText(String.valueOf(BookShelfPresenter.getWeekReadTime()));
                }
                //更新每日推荐书籍
                updateRecommendBook();
                //更新书架书籍数据.
                updateBookData(true);
                //更新推荐广告数据
                updateAdData(bookShelfListResp.getPromoteSiteList());
                DataCacheManager.getInstance().setBookShelfAdInfoResp(bookShelfListResp.getPromoteSiteList());


            }
        });
    }

    private void initSite() {
        //悬浮广告
        BookShelfPresenter.loadBookSiteList(1, -1, new BookShelfPresenter.loadSiteDataListener() {
            @Override
            public void loadSiteData(BookSiteBean bookBannerAdBean) {
                if (bookBannerAdBean.getSuspensionSite().getType() != 3 && bookBannerAdBean.getSuspensionSite().getIconPath() == null) {
                    imageView.setVisibility(View.GONE);
                } else {
                    bookSiteBean = bookBannerAdBean;
                    // 判断是否悬浮广告，是否可现实
                    if (!TextUtils.isEmpty(bookSiteBean.getSuspensionSite().getAdChannalCode())) {
                        flowAdSiteBean = AdConfigManger.getInstance().showAd(getActivity(),
                                bookSiteBean.getSuspensionSite().getAdChannalCode());
                        if (flowAdSiteBean != null && !TextUtils.isEmpty(flowAdSiteBean.getPicUrl())) {
                            IAdView adView = AdConfigManger.getInstance().getAdView(getActivity(),
                                    flowAdSiteBean.getChannelCode(), flowAdSiteBean);
                            if (adView != null) {
                                imageView.setVisibility(View.VISIBLE);
                                adView.init(null, imageView, 30, null);
                                adView.showAd();
                            } else {
                                imageView.setVisibility(View.GONE);
                            }
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                    } else {
                        flowAdSiteBean = null;
                        imageView.setVisibility(View.VISIBLE);
                        GlideUtils.INSTANCE.loadImageWidthNoCorner(getActivity(), bookBannerAdBean.getSuspensionSite().getIconPath(), imageView);
                    }
                    if (!isRead) {
                        Logger.i("INSTANCE", "loadSiteData: ");
                        FuncPageStatsApi.floatAdExpose(bookSiteBean.getSuspensionSite().getType() == 1 ? bookSiteBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.BOOKSHELF, "7 + " + PageNameConstants.BOOKSHELF + " + " + StartGuideMgr.getChooseSex());
                    }
                }

            }
        });
    }

    /**
     * 初始化可展示的书籍列表.
     *
     * @return
     */
    private void initCanShowBookList() {
        if (mCanShowBookList == null) {
            mCanShowBookList = new ArrayList<>();
        } else {
            mCanShowBookList.clear();
        }
        //添加收藏的书籍.
        if (!StringFormat.isEmpty(mBookInfoRespList)) {
            mCanShowBookList.addAll(mBookInfoRespList);
        }
        //判断推荐的书籍列表是否为空.
        if (!StringFormat.isEmpty(mRecommendBookList) && isCanRecommend) {
            //1.收藏书籍少于或等于5, 则使用推荐书籍填充到8本, 并显示进入书城入口; 2.收藏的书籍数量大于5本, 则显示三本推荐的书籍, 不显示进入书城入口 .
            int suppRecommBookCount = mCanShowBookList.size() <= 5 ? 8 - mCanShowBookList.size() : 3;
            //补充推荐的书籍进行展示.
            mCanShowBookList.addAll(mRecommendBookList.subList(0, mRecommendBookList.size() > suppRecommBookCount ? suppRecommBookCount : mRecommendBookList.size()));
        }
        if (mCanShowBookList.size() <= 0) {
            //无任何书籍. 隐藏书架View.
            if (mPullToRefreshLayout != null && mPullToRefreshLayout.getVisibility() == View.VISIBLE) {
                mPullToRefreshLayout.setVisibility(View.GONE);
                bsNoDataLayout.setVisibility(View.VISIBLE);
            }
        } else if (mPullToRefreshLayout != null && mPullToRefreshLayout.getVisibility() != View.VISIBLE) {
            //显示书架.
            mPullToRefreshLayout.setVisibility(View.VISIBLE);
            bsNoDataLayout.setVisibility(View.GONE);
        }
        //判断是否禁用AppBar滑动
        if (mCanShowBookList.size() < 6) {
            //禁用AppBar滑动
            disableAppBarSliding();
        } else {
            //启动AppBar滑动
            enableAppBarSliding();
        }
    }

    /**
     * 更新每日推荐书籍
     */
    private void updateRecommendBook() {
        if (infoResps == null || infoResps.isEmpty()) return;
        bsTopRecommendLayout.setVisibility(View.VISIBLE);
        xCustomBanner.setBannerData(R.layout.item_day, infoResps);
        mRecommendBookInfo = infoResps.get(0);
        if (mRecommendBookInfo == null) return;
        FuncPageStatsApi.bookShelfRecomDaylyShow(mRecommendBookInfo.getBookId(), "3" + " + " + StartGuideMgr.getChooseSex(), 1);
        xCustomBanner.loadImage(new XCustomBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XCustomBanner banner, Object model, View view, int position) {
                BookShelfRecoInfoResp bookShelfRecoInfoResp = infoResps.get(position);
                if (bookShelfRecoInfoResp == null) return;
                ImageView imageView = view.findViewById(R.id.iv_book_cover);
                imageView.setOnClickListener(BookShelfFragment.this);

                TextView tvReferrer = view.findViewById(R.id.tv_referrer);
                tvReferrer.setOnClickListener(BookShelfFragment.this);

                TextView tvBookName = view.findViewById(R.id.tv_book_name);
                tvBookName.setOnClickListener(BookShelfFragment.this);

                TextView tvBookComment = view.findViewById(R.id.tv_book_comment);
                tvBookComment.setOnClickListener(BookShelfFragment.this);

                TextView tvReadNow = view.findViewById(R.id.tv_read_now);
                tvReadNow.setOnClickListener(BookShelfFragment.this);

                if (!TextUtils.isEmpty(bookShelfRecoInfoResp.getCover()))
                    GlideUtils.INSTANCE.loadImage(BaseContext.getContext(), bookShelfRecoInfoResp.getCover(), imageView, GlideUtils.INSTANCE.getBookRadius(), ViewUtils.dp2px(68), ViewUtils.dp2px(92));
                if (!TextUtils.isEmpty(bookShelfRecoInfoResp.getCommentator()))
                    tvReferrer.setText(bookShelfRecoInfoResp.getCommentator());
                if (!TextUtils.isEmpty(bookShelfRecoInfoResp.getBookName()))
                    tvBookName.setText(bookShelfRecoInfoResp.getBookName());
                if (!TextUtils.isEmpty(bookShelfRecoInfoResp.getBookComment()))
                    tvBookComment.setText(bookShelfRecoInfoResp.getBookComment());
            }
        });
        xCustomBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (infoResps == null) return;
                mRecommendBookInfo = infoResps.get(i);
                if (mRecommendBookInfo == null) return;
                if (sparseArray.get(i) == null && i != 0) {
                    sparseArray.append(i, i);
                    FuncPageStatsApi.bookShelfRecomDaylyShow(mRecommendBookInfo.getBookId(), "3" + " + " + StartGuideMgr.getChooseSex(), i + 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
//        if (mRecommendBookInfo == null) {
//            return;
//        }
//        if (dayRecommendBookId != mRecommendBookInfo.getBookId() || tvCommentWidth == 0) {
//            dayRecommendBookId = mRecommendBookInfo.getBookId();
//
//            bsTopRecommendLayout.setVisibility(View.VISIBLE);
//            GlideUtils.INSTANCE.loadImage(BaseContext.getContext(), mRecommendBookInfo.getCover(), ivBookCover, GlideUtils.INSTANCE.getBookRadius(),ViewUtils.dp2px(68),ViewUtils.dp2px(92));
//            tvReferrer.setText(mRecommendBookInfo.getCommentator());
//            tvBookName.setText(mRecommendBookInfo.getBookName());
////            tvBookComment.setText(mRecommendBookInfo.getBookComment());
//            if (tvCommentWidth == 0) {
//                tvBookComment.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvCommentWidth = tvBookComment.getWidth();
//                        updateTvBookComment(mRecommendBookInfo.getBookComment());
//                    }
//                });
//            } else {
//                updateTvBookComment(mRecommendBookInfo.getBookComment());
//            }
//            //每日推荐书籍曝光 统计
//            FuncPageStatsApi.bookShelfRecomDaylyShow(dayRecommendBookId, PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex());
//        }
    }

    /**
     * 更新每日推荐书籍的书评
     */

//    private void updateTvBookComment(String content) {
//        content = content.replace("\n", "");
//
//        StaticLayout staticLayout = new StaticLayout(content, tvBookComment.getPaint(),
//                tvCommentWidth, Layout.Alignment.ALIGN_NORMAL,
//                1.0f, 0, false);
//        if (staticLayout.getLineCount() >= 3) {
//            tvBookComment.setMaxLines(3);
//            //每行多少字
//            int lineCount = staticLayout.getLineEnd(0);
//            //第三行不能写满，会挡住“立即阅读”
//            if (lineCount * 2.3 > 0 && content.length() > lineCount * 2.3) {
//
//                content = content.subSequence(0, (int) (lineCount * 2.3)) + "...";
//            }
//            tvBookComment.setText(content);
//        } else {
//            tvBookComment.setText(content);
//        }
//    }

    /**
     * 更新书架书籍数据.
     *
     * @param isReset 是否需要重置数据.
     */
    private boolean updateBookData(boolean isReset) {
        if (mBookAdapter == null) {
            return false;
        }
        int books = mCanShowBookList != null ? mCanShowBookList.size() : 0;
        //获取已展示书籍数量.
        int count = isReset ? 0 : mBookAdapter.getBookCount();
        //更新书架Adapter.
        //int endPos = count + BookShelfAdapter.PAGE_COUNT;
        int endPos = count + books;
        mBookAdapter.updateData(isReset, false, books > 0 ? mCanShowBookList.subList(count, books > endPos ? endPos : books) : null);
        return books > count;
    }

    /**
     * 更新推荐广告数据
     *
     * @param bookShelfAdInfoRespList
     */
    private void updateAdData(List<BookShelfAdInfoResp> bookShelfAdInfoRespList) {
        try {
            if (!StringFormat.isEmpty(bookShelfAdInfoRespList)) {
                //显示广告View.
                if (recommendLayout.getVisibility() != View.VISIBLE) {
                    recommendLayout.setVisibility(View.VISIBLE);
                }
            }
            //更新广告Adapter.
            mAdAdapter.setData(bookShelfAdInfoRespList);
            //修改ListView高度.
            updateAdListViewHeight();
            //更新滚动组件数据.
            List<String> wordList = new ArrayList<>();
            if (bookShelfAdInfoRespList != null && !bookShelfAdInfoRespList.isEmpty()) {
                for (BookShelfAdInfoResp adInfo : bookShelfAdInfoRespList) {
                    wordList.add(adInfo.getWord());
                }
            }
            mAdInfoRespList = bookShelfAdInfoRespList;
            mMarqueeView.startWithList(wordList);
        } catch (Throwable throwable) {
            Logger.e(TAG, "updateAdData: {}", throwable);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (mPullToRefreshLayout != null) {
            mPullToRefreshLayout.refreshFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //调用更新数据接口.
                boolean isSucc = updateBookData(false);
                //加载更多数据完成.
                mPullToRefreshLayout.loadMoreFinish(isSucc ? LoadResult.LOAD_MORE_SUCCEED : LoadResult.LOAD_MORE_FAIL_NO_DATA);
            }
        }, 500);
    }

    @Override
    public void onClick(@NotNull View v) {
        super.onClick(v);
        if (Utils.isFastClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.bs_sign_layout:
                //签到
                FuncPageStatsApi.signInClick(PageNameConstants.BOOKSHELF, signState);
                Intent intent = new Intent(getContext(), TaskWebViewActivity.class);
//                intent.putExtra("url", MinePresenter.getUrl(Constants.DOMAIN_TASK_H5 + "/taskCenter"));
                String url2 = "";
                if (TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER))) {
                    url2 = "http://taskcenter.duoyueapp.com/";
                } else {
                    url2 = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER);
                }
                intent.putExtra("url", url2);
                getContext().startActivity(intent);
                break;
            case R.id.bs_read_history_layout:
                //阅读历史.
                com.duoyue.mianfei.xiaoshuo.common.ActivityHelper.INSTANCE.gotoHistory(getActivity());
                //点击阅读历史.
                FunctionStatsApi.bsReadHistoryClick();
                FuncPageStatsApi.bookShelfHistory();
                break;
            case R.id.bs_search_layout:
                //搜索.
                ActivityHelper.INSTANCE.gotoSearch(PageNameConstants.BOOKSHELF);
                //点击搜索.
                FunctionStatsApi.bsSearchClick();
//                FuncPageStatsApi.bookShelfSearch();
                break;
            case R.id.bs_cancel_textview:
                //取消编辑.
                quitEditMode();
                break;
            case R.id.bs_select_all:
                //全选/取消全选.
                isSelectAll = !isSelectAll;
                //更新书籍列表状态.
                mBookAdapter.selectAll(isSelectAll);
                if (mSelectBookInfoList == null) {
                    mSelectBookInfoList = new ArrayList<>();
                } else {
                    mSelectBookInfoList.clear();
                }
                if (isSelectAll) {
                    //全选.
                    if (mCanShowBookList != null) {
                        mSelectBookInfoList.addAll(mCanShowBookList);
                    }
                } else {
                    //取消全选.
                    if (mSelectBookInfoList != null) {
                        mSelectBookInfoList.clear();
                    }
                }
                //更新选择书籍数量.
                updateSelectedCount();
                //设置下方按钮状态
                setbottomBtnStatus();
                break;
            case R.id.bs_topping:

                //置顶/非置顶，只有在选中一本书的情况下才可以有效执行
                if (mSelectBookInfoList.size() != 1) {
                    return;
                }

                showLoading();
                final long bookId = mSelectBookInfoList.get(0).getBookId();
                final int modelId = mSelectBookInfoList.get(0).getToppingTime() == 0 ? 1 : 2;   //1、置顶；2、取消置顶
                Single.fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        if (modelId == 1) {
                            //置顶
                            if (mSelectBookInfoList.get(0).getType() == 2) {
                                mSelectBookInfoList.get(0).setType(1);
                            }
                            return BookShelfPresenter.toppingBook(mSelectBookInfoList.get(0));
                        } else {
                            //取消置顶
                            return BookShelfPresenter.cancelToppingBook(mSelectBookInfoList.get(0));
                        }
                    }
                }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        dismissLoading();
                        if (ReadHistoryMgr.HTTP_OK.equals(s)) {

                            if (mBookInfoRespList == null) {
                                mBookInfoRespList = new ArrayList<>();
                            }
                            if (mRecommendBookList != null) {
                                for (BookShelfBookInfoResp bookInfoResp : mRecommendBookList) {
                                    //如果置顶的是推荐的书籍
                                    if (bookId == bookInfoResp.getBookId()) {
                                        mBookInfoRespList.add(bookInfoResp);
                                        mRecommendBookList.remove(bookInfoResp);
                                        break;
                                    }
                                }
                            }
                            initCanShowBookList();
                            //排序
                            if (mBookInfoRespList != null) {
                                Collections.sort(mBookInfoRespList, bookShelfComparator);
                            }
                            if (mCanShowBookList != null) {
                                Collections.sort(mCanShowBookList, bookShelfComparator);
                            }
                            mBookAdapter.updateData(true, false, mCanShowBookList);
                            quitEditMode();
                            //滑动到顶部.
                            slideTop();
                            //置顶/取消置顶 统计
                            FuncPageStatsApi.bookShelfSetTop(bookId, modelId);
                        } else {
                            //置顶/取消置顶 失败.
                            ToastUtils.showLimited(s);
                        }
                    }
                });

                break;
            case R.id.bs_remove:
                //移除书籍.
                try {
                    String message = ViewUtils.getString(R.string.delete_books_bookshelf);
                    SimpleDialog simpleDialog = new SimpleDialog.Builder(getContext()).setCanceledOnTouchOutside(false).setTitle(message).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭Dialog.
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            removeBookList();
                            FuncPageStatsApi.bookShelfRemoveBook();
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
                    Logger.e(TAG, "removeBook: {}", throwable);
                }
                break;
            case R.id.bs_share:
                //分享
                if (mSelectBookInfoList == null || mSelectBookInfoList.isEmpty()) {
                    return;
                }
                final BookShelfBookInfoResp bookInfoResp = mSelectBookInfoList.get(0);
                String title = "《" + bookInfoResp.getBookName() + "》这本小说很不错，推荐你读。";
                String resume = "";
                if (!TextUtils.isEmpty(bookInfoResp.getResume())) {
                    resume = bookInfoResp.getResume().length() > 50
                            ? bookInfoResp.getResume().substring(0, 50) + "..."
                            : bookInfoResp.getResume();
                }
                String cover = bookInfoResp.getBookCover();
                String url = Constants.DOMAIN_SHARE_H5 + "/book/" + bookInfoResp.getBookId();
                CustomShareManger.getInstance().shareBookWithText(
                        getActivity(), title, resume,
                        R.mipmap.share_big_img, cover, url, new BottomShareDialog.ShareClickListener() {
                            @Override
                            public void onClick(int type) {
                                FuncPageStatsApi.shareClick(bookInfoResp.getBookId(), PageNameConstants.BOOKSHELF, type, "");
                                quitEditMode();
                            }
                        }, new BottomShareDialog.ShareResultListener() {
                            @Override
                            public void onShare(int shareResult) {
                                if (shareResult != 1) {
                                    return;
                                }
                                TaskMgr.show(getContext(), getFragmentManager(), getResources().getString(R.string.finish_share_task), TaskMgr.SHARE_TASK);
                            }
                        }
                );
                //分享 统计
                FuncPageStatsApi.bookShelfShare(bookInfoResp.getBookId());
                break;
            case R.id.bs_open_recommend_layout:
                //展开推荐广告.
                findView(R.id.bs_recommend_single_layout).setVisibility(View.GONE);
                findView(R.id.bs_recommend_list_layout).setVisibility(View.VISIBLE);
                //上报推荐广告曝光.
                if (!StringFormat.isEmpty(mAdInfoRespList)) {
                    for (BookShelfAdInfoResp adInfoResp : mAdInfoRespList) {
                        //调用上报展开曝光接口.
                        FunctionStatsApi.bsTextExpandExposure(adInfoResp.getJumpType() == 1 ? adInfoResp.getBookId() : -100);
                        FuncPageStatsApi.bookShelfTxtAdExpdShow(adInfoResp.getJumpType() == 1 ? adInfoResp.getBookId() : -100);
                    }
                }

                break;
            case R.id.bs_close_recommend_layout:
                //收起推荐广告.
                closeRecommendAd();
                break;
            case R.id.bs_go_book_city_btn:
                //去出城逛逛.
                EventBus.getDefault().post(new TabSwitchEvent(HomeActivity.BOOK_CITY, 5));
                break;
            case R.id.tv_read_now:
                if (mRecommendBookInfo == null) return;
                ActivityHelper.INSTANCE.gotoRead(getActivity(), String.valueOf(mRecommendBookInfo.getBookId()),
                        new BaseData(getPageName()), PageNameConstants.BOOKSHELF, PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex());
                FuncPageStatsApi.bookShelfRecomDaylyClick(mRecommendBookInfo.getBookId(), PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex(), infoResps.indexOf(mRecommendBookInfo));
                break;
            case R.id.iv_book_cover:
            case R.id.tv_referrer:
            case R.id.tv_book_name:
            case R.id.tv_book_comment:
                if (mRecommendBookInfo == null) return;
                if (mRecommendBookInfo.getInnerUrl() == 1) {
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(getActivity(),
                            String.valueOf(mRecommendBookInfo.getBookId()), new BaseData(getActivity().getResources().getString(R.string.book_shelf)), PageNameConstants.BOOKSHELF, 1, PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex());
                } else {
                    ActivityHelper.INSTANCE.gotoRead(getActivity(), String.valueOf(mRecommendBookInfo.getBookId()),
                            new BaseData(getPageName()), PageNameConstants.BOOKSHELF, PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex());
                }
                FuncPageStatsApi.bookShelfRecomDaylyClick(mRecommendBookInfo.getBookId(), PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex(), infoResps.indexOf(mRecommendBookInfo));
                break;
//            case R.id.bs_top_recommend_layout:
            //点击每日推荐书籍
//                if (mRecommendBookInfo != null) {
//                    if (mRecommendBookInfo.getInnerUrl() == 1) {
//                        //1书籍详情页
//                        com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(getActivity(),
//                                String.valueOf(mRecommendBookInfo.getBookId()), new BaseData(getActivity().getResources().getString(R.string.book_shelf)), PageNameConstants.BOOKSHELF, 1, PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex());
//                    } else if (mRecommendBookInfo.getInnerUrl() == 2) {
//                        //2阅读器
//                        ActivityHelper.INSTANCE.gotoRead(getActivity(), String.valueOf(mRecommendBookInfo.getBookId()),
//                                new BaseData(getPageName()), PageNameConstants.BOOKSHELF, PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex());
//                    }
//
//                    //点击每日推荐书籍 统计
//                    FuncPageStatsApi.bookShelfRecomDaylyClick(mRecommendBookInfo.getBookId(), PageNameConstants.SOURCE_DAY_RECOM + " + " + StartGuideMgr.getChooseSex());
//                }
//                break;

            case R.id.recommend_float_button:
                if (bookSiteBean.getSuspensionSite().getType() == 1) { // 详情
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(getActivity(), "" + bookSiteBean.getSuspensionSite().getBookId(), new BaseData(""),
                            PageNameConstants.BOOK_CITY, 17, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.BOOKSHELF + " + " + StartGuideMgr.getChooseSex());
                } else if (bookSiteBean.getSuspensionSite().getType() == 3 && flowAdSiteBean != null) {
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoWeb(getActivity(), flowAdSiteBean.getLinkUrl());
                    AdHttpUtil.click(flowAdSiteBean);
                } else { // H5
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoWeb(getActivity(), bookSiteBean.getSuspensionSite().getLink());
                }
                //书城悬浮按钮.
                FuncPageStatsApi.floatAdClick(bookSiteBean.getSuspensionSite().getType() == 1 ? bookSiteBean.getSuspensionSite().getBookId() : -1, 0, PageNameConstants.BOOKSHELF, PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.BOOKSHELF + " + " + StartGuideMgr.getChooseSex());
                break;
        }
    }

    /**
     * 取消全选.
     */
    private void cancelSelectedAll() {
        isSelectAll = false;
        //取消全选.
        if (mSelectBookInfoList != null) {
            mSelectBookInfoList.clear();
        }
        if (mSelectAllTextView != null) {
            //设置为全选.
            mSelectAllTextView.setText(ViewUtils.getString(R.string.select_all));
        }
    }

    /**
     * 点击推荐广告.
     *
     * @param adInfoResp
     * @param isCarouselClick 是否为轮播状态下的点击
     */
    private void clickAd(BookShelfAdInfoResp adInfoResp, boolean isCarouselClick) {
        //判断打开类型.
        if (adInfoResp.getJumpType() == 1) {
            //进入书籍.
            com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(getActivity(), String.valueOf(adInfoResp.getBookId()),
                    new BaseData(getActivity().getResources().getString(R.string.book_shelf)), PageNameConstants.BOOKSHELF, 2, PageNameConstants.BOOK_SHELF_TEXT_SHOW);
            if (isCarouselClick) {
                //轮播状态下点击书籍.
                FunctionStatsApi.bsTextCarouselClick(adInfoResp.getBookId());
                FuncPageStatsApi.bookShelfTxtAdClick(adInfoResp.getBookId());
            } else {
                //展开状态下点击书籍.
                FunctionStatsApi.bsTextExpandClick(adInfoResp.getBookId());
                FuncPageStatsApi.bookShelfTxtAdExpdClick(adInfoResp.getBookId());
            }
        } else if (adInfoResp.getJumpType() == 2) {
            //使用WebView浏览.
            ActivityHelper.INSTANCE.gotoWeb(getActivity(), adInfoResp.getLink());
            if (isCarouselClick) {
                //轮播状态下点击H5.
                FunctionStatsApi.bsTextCarouselClick(-100);
                FuncPageStatsApi.bookShelfTxtAdClick(-100);
            } else {
                //展开状态下点击H5.
                FunctionStatsApi.bsTextExpandClick(-100);
                FuncPageStatsApi.bookShelfTxtAdExpdClick(-100);
            }
        } else {
            ToastUtils.showLimited(R.string.not_supported);
        }
    }

    /**
     * 点击书籍.
     *
     * @param view
     */
    private void clickBook(View view) {
        //获取书籍信息对象.
        final BookShelfBookInfoResp bookShelfInfoResp = view.getTag() != null && view.getTag() instanceof BookShelfBookInfoResp ? (BookShelfBookInfoResp) view.getTag() : null;
        if (isEditMode) {
            //编辑状态.
            if (bookShelfInfoResp == null) {
                Logger.e(TAG, "clickBook: 选择书籍失败, 书籍信息为空:{}", view.getTag());
                return;
            }
            if (bookShelfInfoResp.getBookId() == BookShelfRecyclerAdapter.ADD_BOOK_BOOKID) {
                //编辑状态, 点击添加书籍按钮无效.
                return;
            }
            //获取选择框.
            CheckBox checkBox = view.findViewById(R.id.bs_book_checkbox);
            Logger.i(TAG, "clickBook: 选中书籍:{}, {}, {}", !checkBox.isChecked(), bookShelfInfoResp.getBookId(), bookShelfInfoResp.getBookName());
            checkBox.setChecked(!checkBox.isChecked());
            //判断选中状态.
            if (checkBox.isChecked()) {
                //选中状态.
                if (mSelectBookInfoList == null) {
                    mSelectBookInfoList = new ArrayList<>();
                }
                //添加选中书籍.
                mSelectBookInfoList.add(bookShelfInfoResp);
            } else {
                //非选中状态, 移除选中书籍.
                if (mSelectBookInfoList != null) {
                    mSelectBookInfoList.remove(bookShelfInfoResp);
                }
            }
            if (mBookAdapter != null) {
                mBookAdapter.onCheckedChange(bookShelfInfoResp.getBookId(), checkBox.isChecked());
            }
            //更新选择书籍数量.
            updateSelectedCount();
            //设置下方按钮状态
            setbottomBtnStatus();
            return;
        }
        if (bookShelfInfoResp == null) {
            Logger.e(TAG, "clickBook: 点击书籍失败, 书籍信息为空:{}", view.getTag());
            return;
        }
        try {
            //判断是否点击添加书籍入口.
            if (bookShelfInfoResp.getBookId() == BookShelfRecyclerAdapter.ADD_BOOK_BOOKID) {
                //点击添加书籍入口.
                EventBus.getDefault().post(new TabSwitchEvent(HomeActivity.BOOK_CITY, 7));
                //添加书籍入口.
                FunctionStatsApi.bsAddClick();
                FuncPageStatsApi.bookShelfAddBtn();
            } else {
                //点击书籍阅读, 判断是否为推荐书籍.
                if (bookShelfInfoResp.getType() == 2) {
                    //点击推荐书籍.
                    FunctionStatsApi.bsRecommBookClick(bookShelfInfoResp.getBookId());
                    FuncPageStatsApi.bookShelfRecmBookClick(bookShelfInfoResp.getBookId());
                    //推荐书籍, 添加到书架.
                    Single.fromCallable(new Callable<String>() {
                        @Override
                        public String call() {
                            return BookShelfPresenter.addBookShelf(bookShelfInfoResp);
                        }
                    }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                        }
                    });
                    ActivityHelper.INSTANCE.gotoRead(getActivity(), String.valueOf(bookShelfInfoResp.getBookId()), new BaseData(getPageName()), PageNameConstants.BOOKSHELF, PageNameConstants.BOOK_SHELF_RECOMMEND_BOOK);
                } else {
                    //更新状态书籍刷新
                    BookShelfPresenter.updatePullChapter(String.valueOf(bookShelfInfoResp.getBookId()), bookShelfInfoResp.getLastChapter());
                    //点击书籍.
                    FunctionStatsApi.bsBookClick(bookShelfInfoResp.getBookId());
                    FuncPageStatsApi.bookShelfBookClick(bookShelfInfoResp.getBookId());
                    ActivityHelper.INSTANCE.gotoRead(getActivity(), String.valueOf(bookShelfInfoResp.getBookId()), new BaseData(getPageName()), PageNameConstants.BOOKSHELF, "");
                }
                //BookShelfHelper.getsInstance().updateBook(bookShelfBean)

            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "clickBook: 点击书籍异常:{}", throwable);
        }
    }

    /**
     * 修改推荐广告ListView高度.
     */
    private void updateAdListViewHeight() {
        try {
            //得到ListView 添加的适配器
            ListAdapter listAdapter = mAdListView != null ? mAdListView.getAdapter() : null;
            if (listAdapter == null) {
                return;
            }
            //获取其中的一项
            View itemView = listAdapter.getView(0, null, mAdListView);
            itemView.measure(0, 0);
            //获取Item高度
            int itemHeight = itemView.getMeasuredHeight();
            //得到总的项数
            int itemCount = listAdapter.getCount();
            //进行布局参数的设置
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * (itemCount > 5 ? 5 : itemCount));
            mAdListView.setLayoutParams(layoutParams);
        } catch (Throwable throwable) {
            Logger.e(TAG, "updateAdListViewHeight: {}", throwable);
        }
    }

    /**
     * AppBar + ListView滑动到顶部.
     */
    private void slideTop() {
        //滑动AppBar.
        if (mAppBarLayout != null) {
            //参数一:是否展开;参数二:是否带动画
            mAppBarLayout.setExpanded(true, true);
        }
        //滑动List.
        if (mBookRecyclerView != null) {
            mBookRecyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * 禁用AppBar滑动
     */
    private void disableAppBarSliding() {
        if (mAppBarLayout != null) {
            try {
                View appBarChildAt = mAppBarLayout.getChildAt(0);
                AppBarLayout.LayoutParams appBarParams = (AppBarLayout.LayoutParams) appBarChildAt.getLayoutParams();
                appBarParams.setScrollFlags(0);
            } catch (Throwable throwable) {
                Logger.e(TAG, "disableAppBarSliding: {}", throwable);
            }
        }
    }

    /**
     * 启用AppBar滑动
     */
    private void enableAppBarSliding() {
        if (mAppBarLayout != null) {
            try {
                View appBarChildAt = mAppBarLayout.getChildAt(0);
                AppBarLayout.LayoutParams appBarParams = (AppBarLayout.LayoutParams) appBarChildAt.getLayoutParams();
                appBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            } catch (Throwable throwable) {
                Logger.e(TAG, "disableAppBarSliding: {}", throwable);
            }
        }
    }

    /**
     * 获取当前Tab名称
     *
     * @return
     */
    @Override
    public String getPageName() {
        return ViewUtils.getString(R.string.tab_bookshelf);
    }

    /**
     * 设置下方按钮的状态
     */
    private void setbottomBtnStatus() {
        //是否只选中了一本书
        boolean isOnlyOne = false;
        if (mSelectBookInfoList != null && mSelectBookInfoList.size() == 1) {
            isOnlyOne = true;
        }
        Resources resources = getResources();
        if (isOnlyOne) {
            //置顶按钮
            mToppingTextView.setClickable(true);
            Drawable topping = resources.getDrawable(R.mipmap.icon_topping_a);
            mToppingTextView.setCompoundDrawablesWithIntrinsicBounds(null, topping, null, null);
            mToppingTextView.setTextColor(resources.getColor(R.color.read_page_tip_05));

            if (mSelectBookInfoList.get(0).getToppingTime() == 0) {
                mToppingTextView.setText("置顶");
            } else {
                mToppingTextView.setText("取消置顶");
            }

            //分享按钮
            mShareTextView.setClickable(true);
            Drawable share = resources.getDrawable(R.mipmap.icon_share_a);
            mShareTextView.setCompoundDrawablesWithIntrinsicBounds(null, share, null, null);
            mShareTextView.setTextColor(resources.getColor(R.color.read_page_tip_05));

            //置顶按钮
        } else {

            //置顶按钮
            mToppingTextView.setText("置顶");
            mToppingTextView.setClickable(false);
            Drawable topping = resources.getDrawable(R.mipmap.icon_topping_b);
            mToppingTextView.setCompoundDrawablesWithIntrinsicBounds(null, topping, null, null);
            mToppingTextView.setTextColor(resources.getColor(R.color.color_ccc));

            //分享按钮
            mShareTextView.setClickable(false);
            Drawable share = resources.getDrawable(R.mipmap.icon_share_b);
            mShareTextView.setCompoundDrawablesWithIntrinsicBounds(null, share, null, null);
            mShareTextView.setTextColor(resources.getColor(R.color.color_ccc));
        }


        //移除按钮
        if (mSelectBookInfoList == null || mSelectBookInfoList.size() == 0) {
            // 一个也未选中
            mRemoveBookTextView.setClickable(false);
            Drawable remove = resources.getDrawable(R.mipmap.icon_remove_b);
            mRemoveBookTextView.setCompoundDrawablesWithIntrinsicBounds(null, remove, null, null);
            mRemoveBookTextView.setTextColor(resources.getColor(R.color.color_ccc));
        } else {
            mRemoveBookTextView.setClickable(true);
            Drawable remove = resources.getDrawable(R.mipmap.icon_remove_a);
            mRemoveBookTextView.setCompoundDrawablesWithIntrinsicBounds(null, remove, null, null);
            mRemoveBookTextView.setTextColor(resources.getColor(R.color.read_page_tip_05));
        }

        //全选按钮
        if (mSelectBookInfoList == null || mSelectBookInfoList.size() < mCanShowBookList.size()) {
            mSelectAllTextView.setText("全选");
            isSelectAll = false;
        } else {
            mSelectAllTextView.setText("取消全选");
            isSelectAll = true;
        }
    }

    /**
     * 是否为编辑模式.
     *
     * @return
     */
    public boolean isEditMode() {
        return isEditMode;
    }

    /**
     * 进入编辑模式.
     */
    private void toEditMode(BookShelfBookInfoResp bookShelfInfoResp) {

        //设置为编辑状态.
        isEditMode = true;
        //清空选中书籍列表.
        if (mSelectBookInfoList == null) {
            mSelectBookInfoList = new ArrayList<>();
        }
        mSelectBookInfoList.clear();
        //刷新书籍Adapter, 显示可选中状态.
        mBookAdapter.updateEditMode(isEditMode);
        if (bookShelfInfoResp != null) {
            mSelectBookInfoList.add(bookShelfInfoResp);
            mBookAdapter.onCheckedChange(bookShelfInfoResp.getBookId(), true);
        }
        //修改标题.
        updateSelectedCount();
        //隐藏每日推荐和滚动广告
        bsTopRecommendLayout.setVisibility(View.GONE);
        recommendLayout.setVisibility(View.GONE);
        //设置下方按钮状态
        setbottomBtnStatus();
        //初始化动画.
        initEditAnim();
        //启动进入编辑动画.
        mTopEditBarView.startAnimation(mTopInAnim);
        mTopDefaultBarView.startAnimation(mTopDefaultOutAnim);
        mBottomEditBarView.startAnimation(mBottomInAnim);
    }

    /**
     * 退出编辑模式
     */
    public void quitEditMode() {
        quitEditMode(false);
    }

    /**
     * 退出编辑模式
     *
     * @param isRemove 是否为删除书籍退出编辑状态
     */
    public void quitEditMode(boolean isRemove) {
        //判断当前是否为编辑状态.
        if (!isEditMode) {
            //非编辑状态.
            return;
        }
        //取消全选.
        cancelSelectedAll();
        //设置为非编辑状态.
        isEditMode = false;
        //删除书籍状态不需要刷下书籍列表.
        if (!isRemove) {
            //清空选中书籍列表.
            if (mSelectBookInfoList != null && !mSelectBookInfoList.isEmpty()) {
                mSelectBookInfoList.clear();
            }
            //刷新书籍Adapter, 显示默认状态.
            mBookAdapter.updateEditMode(isEditMode);
        }
        //启动退出编辑动画.
        if (mTopEditBarView != null && mTopOutAnim != null) {
            mTopEditBarView.startAnimation(mTopOutAnim);
        }
        if (mTopDefaultBarView != null && mTopDefaultInAnim != null) {
            mTopDefaultBarView.startAnimation(mTopDefaultInAnim);
        }
        if (mBottomEditBarView != null && mBottomOutAnim != null) {
            mBottomEditBarView.startAnimation(mBottomOutAnim);
        }
        //显示每日推荐和滚动广告
        if (infoResps != null) {
            bsTopRecommendLayout.setVisibility(View.VISIBLE);
            recommendLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 删除书架书籍信息.
     */
    private void removeBookList() {
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                //移除书籍信息.
                return BookShelfPresenter.removeBookList(mSelectBookInfoList);
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String msg) throws Exception {
                if (!BookShelfMgr.HTTP_OK.equalsIgnoreCase(msg)) {
                    //删除书籍失败.
                    ToastUtils.show(msg);
                    return;
                }
                //删除成功, 退出编辑状态.
                quitEditMode(true);
            }
        });
    }

    /**
     * 初始化动画.
     */
    private void initEditAnim() {
        //================================顶部操作栏==========================
        if (mTopInAnim == null) {
            //获取顶部工具栏高度.
            int topBarHeight = mTopEditBarView.getHeight();
            mTopInAnim = new TranslateAnimation(0, 0, -topBarHeight, 0);
            mTopInAnim.setDuration(200);
            mTopInAnim.setFillAfter(true);
            mTopInAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //显示View.
                    if (mTopEditBarView != null) {
                        mTopEditBarView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mTopEditBarView != null) {
                        mTopEditBarView.clearAnimation();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mTopDefaultInAnim = new AlphaAnimation(0, 1);
            mTopDefaultInAnim.setDuration(200);
            mTopDefaultInAnim.setFillAfter(true);
            mTopOutAnim = new TranslateAnimation(0, 0, 0, -topBarHeight);
            mTopOutAnim.setDuration(200);
            mTopOutAnim.setFillAfter(true);
            mTopOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //隐藏View.
                    if (mTopEditBarView != null) {
                        mTopEditBarView.clearAnimation();
                        mTopEditBarView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        mTopDefaultOutAnim = new AlphaAnimation(1, 0);
        mTopDefaultOutAnim.setDuration(200);
        mTopDefaultOutAnim.setFillAfter(true);
        //==========================底部操作栏=======================
        if (mBottomInAnim == null) {
            //获取顶部工具栏高度.
            int bottomBarHeight = getActivity().findViewById(android.R.id.tabs).getHeight();
            mBottomInAnim = new TranslateAnimation(0, 0, +bottomBarHeight, 0);
            mBottomInAnim.setDuration(200);
            mBottomInAnim.setFillAfter(true);
            mBottomInAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //显示View.
                    if (mBottomEditBarView != null) {
                        mBottomEditBarView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mBottomEditBarView != null) {
                        mBottomEditBarView.clearAnimation();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mBottomOutAnim = new TranslateAnimation(0, 0, 0, +bottomBarHeight);
            mBottomOutAnim.setDuration(200);
            mBottomOutAnim.setFillAfter(true);
            mBottomOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //隐藏View.
                    if (mBottomEditBarView != null) {
                        mBottomEditBarView.clearAnimation();
                        mBottomEditBarView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    /**
     * 更新选中书籍数量.
     */
    private void updateSelectedCount() {
        if (mSelectBookInfoList == null || mSelectBookInfoList.isEmpty()) {
            //显示编辑书架.
            mEditTextView.setText(ViewUtils.getString(R.string.edit_bookshelf));
        } else {
            //显示选中书籍数量.
            mEditTextView.setText(ViewUtils.getString(R.string.select_count, mSelectBookInfoList.size()));
        }
    }

    @Override
    public void onRecordChange(BookRecordBean recordBean) {
        if (StringFormat.isEmpty(mCanShowBookList)) {
            return;
        }
        boolean isUpdateBook = false;
        //更新书籍状态.
        if (recordBean != null && !StringFormat.isEmpty(recordBean.getBookId())) {
            //更新指定书籍信息状态即可.
            for (BookShelfBookInfoResp bookInfoResp : mCanShowBookList) {
                if (Long.parseLong(recordBean.getBookId()) == bookInfoResp.getBookId()) {
                    bookInfoResp.updateBookRecordInfo(recordBean);
                    isUpdateBook = true;
                }
            }
        } else {
            //同步服务器最新阅读历史记录列表
            List<BookRecordBean> bookRecordBeanList = BookRecordHelper.getsInstance().findAllBooks();
            if (bookRecordBeanList == null || bookRecordBeanList.isEmpty()) {
                return;
            }
            Map<String, BookRecordBean> bookRecordMap = new HashMap<>();
            for (BookRecordBean bookRecord : bookRecordBeanList) {
                if (bookRecord == null || StringFormat.isEmpty(bookRecord.getBookId())) {
                    continue;
                }
                bookRecordMap.put(bookRecord.getBookId(), bookRecord);
            }
            //遍历书架书籍列表.
            for (BookShelfBookInfoResp bookInfoResp : mCanShowBookList) {
                //同步最新的阅读历史记录信息.
                if (bookInfoResp.updateBookRecordInfo(bookRecordMap.get(bookInfoResp.getBookId()))) {
                    isUpdateBook = true;
                }
            }
        }
        //重新排序书架书籍顺序.
        if (isUpdateBook) {
            //通过最近阅读时间进行排序.
            if (mBookInfoRespList != null) {
                Collections.sort(mBookInfoRespList, bookShelfComparator);
            }
            if (mCanShowBookList != null) {
                Collections.sort(mCanShowBookList, bookShelfComparator);
            }
            //判断当前Tag是否为书架. 或者发现页。
            if (mBookAdapter != null &&
                    (((TabHost) getActivity().findViewById(android.R.id.tabhost)).getCurrentTab() == HomeActivity.BOOKSHELF
                            || ((TabHost) getActivity().findViewById(android.R.id.tabhost)).getCurrentTab() == HomeActivity.BOOKLIST)) {
                //书架Tab, 刷新书架数据.
                updateBookData(true);
            }
        }
    }

    /**
     * 书架书籍更新
     *
     * @param event
     */
    @Override
    public void onShelfChange(ShelfEvent event) {
        try {
            if (event == null || event.mChangeList == null || event.mChangeList.isEmpty() || mBookAdapter == null) {
                return;
            }
            if (event.mType == ShelfEvent.TYPE_ADD) {
                //添加书籍到书架.
                if (mBookInfoRespList == null) {
                    mBookInfoRespList = new ArrayList<>();
                }
                Map<String, BookShelfBookInfoResp> bookInfoRespMap = null;
                //置顶书籍的后一个坐标
                int index = 0;
                if (!StringFormat.isEmpty(mCanShowBookList)) {
                    bookInfoRespMap = new HashMap<>();
                    for (BookShelfBookInfoResp bookInfoResp : mCanShowBookList) {
                        bookInfoRespMap.put(String.valueOf(bookInfoResp.getBookId()), bookInfoResp);
                        if (bookInfoResp.getToppingTime() != 0) {
                            index++;
                        }
                    }
                }
                boolean isRefresh = false;
                for (BookShelfBean bookShelfBean : event.mChangeList) {
                    if (bookInfoRespMap != null && bookInfoRespMap.containsKey(bookShelfBean.bookId)) {
                        BookShelfBookInfoResp tmpBookInfoResp = bookInfoRespMap.get(bookShelfBean.bookId);
                        //从推荐书籍中移除.
                        if (tmpBookInfoResp.getType() == 2) {
                            if (!StringFormat.isEmpty(mRecommendBookList)) {
                                mRecommendBookList.remove(tmpBookInfoResp);
                            }
                            //当前书架不包含该书籍.
                            mBookInfoRespList.add(index, new BookShelfBookInfoResp(bookShelfBean));
                            isRefresh = true;
                        }
                    } else {
                        //当前书架不包含该书籍.
                        mBookInfoRespList.add(index, new BookShelfBookInfoResp(bookShelfBean));
                        isRefresh = true;
                    }
                }
                if (isRefresh) {
                    //初始化可展示的书籍列表.
                    initCanShowBookList();
                    //获取当前可展示的书籍数量.
                    int endIndex = mBookAdapter.getBookCount() + event.mChangeList.size();
                    //更新书架书籍列表.
                    mBookAdapter.updateData(true, false, mCanShowBookList.subList(0, mCanShowBookList.size() > endIndex ? endIndex : mCanShowBookList.size()));
                }
            } else if (event.mType == ShelfEvent.TYPE_REMOVE) {
                //移除书架书籍.
                if (!StringFormat.isEmpty(mCanShowBookList)) {
                    List<Long> removeBookIdList = new ArrayList<>();
                    for (BookShelfBean bookShelfBean : event.mChangeList) {
                        removeBookIdList.add(Long.valueOf(bookShelfBean.getBookId()));
                    }
                    BookShelfBookInfoResp tmpBookInfoResp;
                    for (int index = mCanShowBookList.size() - 1; index >= 0; index--) {
                        tmpBookInfoResp = mCanShowBookList.get(index);
                        if (removeBookIdList.contains(tmpBookInfoResp.getBookId())) {
                            //移除书架书籍.
                            mCanShowBookList.remove(tmpBookInfoResp);
                            if (mBookInfoRespList != null) {
                                mBookInfoRespList.remove(tmpBookInfoResp);
                            }
                            //移除推荐书籍.
                            if (tmpBookInfoResp.getType() == 2 && mRecommendBookList != null) {
                                mRecommendBookList.remove(tmpBookInfoResp);
                            }
                        }
                    }
                    //初始化可展示的书籍列表.
                    initCanShowBookList();
                    //更新书架书籍列表.
                    mBookAdapter.updateData(true, true, mCanShowBookList.subList(0, mCanShowBookList.size() > mBookAdapter.getBookCount() ? mBookAdapter.getBookCount() : mCanShowBookList.size()));
                }
                //滑动到顶部.
                slideTop();
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "onShelfChange: {}", throwable);
        }
    }

    /**
     * 展示横幅广告.
     */
//    private void showBannerAd() {
        /*if (!AdManager.getInstance().showAd(AdConstants.Position.BOOKSHELF)) {
            return;
        }
        AdManager.getInstance().createAdSource(getActivity()).loadBannerAd(null, mAdContainerLayout, new ADListener() {
            @Override
            public void pull(AdOriginConfigBean originBean) {
                mAdContainerLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void pullFailed(AdOriginConfigBean originBean) {
                //广告拉取失败, 隐藏广告容器.
                if (mAdContainerLayout != null) {
                    mAdContainerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onShow(AdOriginConfigBean originBean) {
            }

            @Override
            public void onClick(AdOriginConfigBean originBean) {
            }

            @Override
            public void onError(AdOriginConfigBean originBean, String msg) {
                //广告展示错误, 隐藏广告容器.
                if (mAdContainerLayout != null) {
                    mAdContainerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDismiss(AdOriginConfigBean originBean) {
                //广告关闭, 隐藏广告容器.
                if (mAdContainerLayout != null) {
                    mAdContainerLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 登录成功.
     */
    public void onLoginSucc() {
        //重新更新数据.
        loadBookData();
        //获取签到状态
        BookShelfPresenter.requestSignState(this);
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
        //阅读品味变化, 更新数据.
        isRead = true;
        loadBookData();
        initSite();
        Logger.e(TAG, "收到事件通知，刷新书架UI");
    }

    /**
     * 签到成功
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskFinishEvent(TaskFinishEvent event) {
        if (event.getTaskId() == TaskMgr.SIGN_TASK) {
            Logger.d(TAG, "taskFinishEvent: " + TaskMgr.SIGN_TASK);
            signState = 2;
            if (bsSignPoint != null) {
                bsSignPoint.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 收起推荐广告.
     */
    private void closeRecommendAd() {
        findView(R.id.bs_recommend_single_layout).setVisibility(View.VISIBLE);
        findView(R.id.bs_recommend_list_layout).setVisibility(View.GONE);
    }

    /**
     * 获取无数据提示类对象.
     *
     * @return
     */
    private PromptLayoutHelper getPromptLayoutHelper() {
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(findView(R.id.load_prompt_layout));
        }
        return mPromptLayoutHelper;
    }

    /**
     * 显示加载中提示页面.
     */
    private void showLoading() {
        getPromptLayoutHelper().showLoading();
    }

    /**
     * 关闭加载页面
     */
    private void dismissLoading() {
        getPromptLayoutHelper().hide();
    }

    /**
     * 展示无数据页面.
     */
    private void showEmpty() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
    }

    /**
     * 展示无网络页面.
     */
    private void showNetworkError() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加载数据数据.
                loadBookData();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;

        }
        updateDataOnResume(1);
    }

//
//    @Override
//    public void onFragmentResume(boolean isFirst, boolean isViewDestroyed) {
//        super.onFragmentResume(isFirst, isViewDestroyed);
//        if (!isFirst) {
//            BookShelfPresenter.getBookShelfRecoInfoResp(new BookShelfPresenter.DayRecommendBookCallback() {
//                @Override
//                public void onDayRecommendBook(List<BookShelfRecoInfoResp> bookShelfRecoInfoResp) {
//                    infoResps = bookShelfRecoInfoResp;
//                    updateRecommendBook();
//                }
//            });
////            BookShelfPresenter.getBookShelfRecoInfoResp(new BookShelfPresenter.DayRecommendBookCallback() {
////                @Override
////                public void onDayRecommendBook(BookShelfRecoInfoResp bookShelfRecoInfoResp) {
////                    mRecommendBookInfo = bookShelfRecoInfoResp;
////                    updateRecommendBook();
////                }
////            });
//        }
//    }

    public void resumeByHomePressed() {
        FuncPageStatsApi.bookShelfShow(2);
        Logger.e("app#", "书架--从桌面启动");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateOnResume(BookShelfUpdateEvent event) {
        updateDataOnResume(event.getType());
    }

    private void updateDataOnResume(int modelId) {
        //展示广告.
//        showBannerAd();
        //设置阅读时长.
        if (mReadingTimeTextView != null) {
            mReadingTimeTextView.setText(String.valueOf(BookShelfPresenter.getWeekReadTime()));
        }
        //收起推荐广告.
        closeRecommendAd();
    }

    /**
     * 注册广播接收器.
     */
    private void registerReceiver() {
        try {
            //创建广播接收器.
            mReceiver = new BookShelfReceiver();
            IntentFilter intentFilter = new IntentFilter();
            //日期变化广播.
            intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
            //Setting
            intentFilter.addAction(SettingActivity.ACTION_SETTING_BOOKSHELF_RECOMMEND_CHANGED);
            //注册广播.
            getContext().registerReceiver(mReceiver, intentFilter);
        } catch (Throwable throwable) {
            Logger.e(TAG, "registerReceiver: {}", throwable);
        }
    }

    /**
     * 注销广播接收器
     */
    private void unRegisterReceiver() {
        try {
            //注销广告事件广播
            if (mReceiver != null) {
                getContext().unregisterReceiver(mReceiver);
                mReceiver = null;
            }
        } catch (Throwable throwable) {
            Logger.e(TAG, "unRegisterReceiver: {}", throwable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销数据库变化监听.
        BookShelfHelper.getsInstance().removeObserver(this);
        BookRecordHelper.getsInstance().removeObserver(this);
        //注销EventBus.
        EventBus.getDefault().unregister(this);
        //注销广播.
        unRegisterReceiver();
    }

    @Override
    public void signSuccess(SignBean mineBean) {

        if (mineBean != null) {
            signState = mineBean.getSignStatus();
            if (signState == 1) {
                //当天未签到
                if (bsSignPoint != null) {
                    bsSignPoint.setVisibility(View.VISIBLE);
                }
            } else {
                if (bsSignPoint != null) {
                    bsSignPoint.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void signEmpty() {
        if (bsSignPoint != null) {
            bsSignPoint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void signError() {
        SignBean signBean = DataCacheManager.getInstance().getSignBean();
        if (signBean != null) {
            signSuccess(signBean);
        } else {
            if (bsSignPoint != null) {
                bsSignPoint.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 广播接收器.
     */
    class BookShelfReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Logger.i(TAG, "BookShelfReceiver: onReceive: {}", intent.getAction());
                if (Intent.ACTION_DATE_CHANGED.equalsIgnoreCase(intent.getAction())) {
                    //日期变化广播, 刷新书架数据, 更新周阅读时长.
                    if (mHandler != null) {
                        //延迟两分钟刷新书架数据(防止服务器周数据更新不及时)
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //调用加载书籍信息接口.
                                loadBookData();
                                BookShelfPresenter.requestSignState(BookShelfFragment.this);
                            }
                        }, 2 * 60 * 1000);
                    }
                } else if (TextUtils.equals(SettingActivity.ACTION_SETTING_BOOKSHELF_RECOMMEND_CHANGED, intent.getAction())) {
                    //书架是否显示推荐书籍
                    isCanRecommend = SPUtils.INSTANCE.getBoolean(SettingActivity.SETTING_BOOKSHELF_RECOMMEND_KEY, true);
                    initCanShowBookList();
                    updateBookData(true);
                }
            } catch (Throwable throwable) {
                Logger.e(TAG, "BookShelfReceiver: onReceive: {}", throwable);
            }

        }
    }

    /**
     * 书架推荐广告Adapter
     */
    class AdAdapter extends BaseAdapter {
        /**
         * 广告信息列表.
         */
        private List<BookShelfAdInfoResp> mDataList;

        /**
         * 构造方法.
         */
        public AdAdapter(List<BookShelfAdInfoResp> dataList) {
            mDataList = dataList;
        }

        /**
         * 设置数据
         *
         * @param dataList
         */
        public void setData(List<BookShelfAdInfoResp> dataList) {
            mDataList = dataList;
            //更新数据.
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataList != null ? mDataList.size() : 0;
        }

        @Override
        public BookShelfAdInfoResp getItem(int position) {
            return mDataList != null && mDataList.size() > position ? mDataList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //获取广告信息.
            BookShelfAdInfoResp bookShelfAdInfoResp = getItem(position);
            AdViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new AdViewHolder();
                convertView = View.inflate(getContext(), R.layout.bs_ad_item_view, null);
                viewHolder.itemView = convertView;
                viewHolder.wordTextView = convertView.findViewById(R.id.bs_word_textview);
                //设置Tag.
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (AdViewHolder) convertView.getTag();
            }
            viewHolder.wordTextView.setText(bookShelfAdInfoResp != null ? bookShelfAdInfoResp.getWord() : "");
            //设置广告信息到Tag.
            viewHolder.wordTextView.setTag(bookShelfAdInfoResp);
            return convertView;
        }
    }

    public Comparator bookShelfComparator = new Comparator<BookShelfBookInfoResp>() {

        @Override
        public int compare(BookShelfBookInfoResp bookInfo1, BookShelfBookInfoResp bookInfo2) {
            //置顶时间排序
            if (bookInfo1.getToppingTime() > bookInfo2.getToppingTime()) {
                return -1;
            } else if (bookInfo1.getToppingTime() < bookInfo2.getToppingTime()) {
                return 1;
            } else {
                //书籍类型  1收藏书籍 2推荐书籍
                if (bookInfo1.getType() == bookInfo2.getType()) {
                    //上次阅读时间排序
                    if (bookInfo1.getLastReadTime() > bookInfo2.getLastReadTime()) {
                        return -1;
                    } else if (bookInfo1.getLastReadTime() < bookInfo2.getLastReadTime()) {
                        return 1;
                    }
                } else if (bookInfo1.getType() < bookInfo2.getType()) {
                    return -1;
                } else if (bookInfo1.getType() > bookInfo2.getType()) {
                    return 1;
                }
            }
            return 0;
        }
    };

    /**
     * 广告ViewHolder.
     */
    class AdViewHolder {
        /**
         * ItemView
         */
        View itemView;

        /**
         * 内容.
         */
        TextView wordTextView;
    }
}
