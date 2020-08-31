package com.duoyue.app.ui.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.BookDetailCacheBean;
import com.duoyue.app.bean.BookDetailCategoryBean;
import com.duoyue.app.bean.BookDownloadTask;
import com.duoyue.app.bean.CommentItemBean;
import com.duoyue.app.bean.CommentListBean;
import com.duoyue.app.bean.RecommendBean;
import com.duoyue.app.bean.RecommendItemBean;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.duoyue.app.common.mgr.BookExposureMgr;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.app.common.mgr.TaskMgr;
import com.duoyue.app.event.BookDownloadEvent;
import com.duoyue.app.event.DayEvent;
import com.duoyue.app.presenter.BookCommentPresenter;
import com.duoyue.app.presenter.BookDetailsPresenter;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.app.presenter.ReadHistoryPresenter;
import com.duoyue.app.ui.BookDetailCommentAdapter;
import com.duoyue.app.ui.adapter.BookDetailHotAdapter;
import com.duoyue.app.ui.adapter.BookDetailOtherReadAdapter;
import com.duoyue.app.ui.dialog.DownloadBottomDialog;
import com.duoyue.app.ui.view.BookDetailNestedScrollView;
import com.duoyue.app.ui.view.BookDetailsView;
import com.duoyue.lib.base.FlowXLayout;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.customshare.CustomShareManger;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.image.ImageTool;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.lib.base.widget.XLinearLayout;
import com.duoyue.lib.base.widget.XRelativeLayout;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDownloadHelper;
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDownloadManager;
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils;
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.BookShelfBean;
import com.zydm.base.data.dao.BookShelfHelper;
import com.zydm.base.data.dao.ShelfEvent;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.tools.TooFastChecker;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.utils.GlideUtils;
import com.zydm.base.utils.SharePreferenceUtils;
import com.zydm.base.utils.StringUtils;
import com.zydm.base.utils.TimeUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import com.zydm.base.widgets.BottomShareDialog;
import com.zydm.base.widgets.PromptLayoutHelper;
import com.zydm.statistics.motong.MtStHelper;
import com.zzdm.ad.router.BaseData;
import com.zzdm.ad.router.RouterPath;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

@Route(path = RouterPath.Read.PATH_DETAIL)
public class BookDetailActivity extends BaseActivity implements View.OnClickListener, BookDetailsView, BookShelfHelper.ShelfDaoObserver {

    private static final String TAG = "App#BookDetailActivity";
    public static final int REQUEST_CODE_READ = 999;
    public static final int REQUEST_CODE_DOWNLOAD = 1010;
    public static long currBookId;
    private PromptLayoutHelper mPromptLayoutHelper;
    private BookDetailActivity.Presenter presenter;
    private BookDetailActivity.CommentPresenter commentPresenter;
    private RecyclerView recyclerView;
    private long bookId;
    private BookDetailBean mBookDetailBean;

    private TooFastChecker tooFastChecker;

    private ImageView ivCover;
    private TextView tvBookName;
    private TextView tvAuthor;
    private TextView tvStatus;
    private TextView tvGrade;
    private TextView tvWordCnt;
    private TextView tvPopularity;
    private TextView tvFans;
    private TextView tvRecommenWords;
    private TextView tvResume;
    private ViewGroup adView;
    private RecyclerView commentContainer;
    private TextView tvCahpter;
    private TextView tvUpdateTime;
    private View dotView;
    private TextView tvSource;
    private TextView tvPublishTime;
    private TextView tvAddToBookShelf;
    private TextView readBtn;
    private BookDetailNestedScrollView scrollView;
    private ImageView moreLayout;
    private ImageView ivTopBg;
    private TextView tvPopularityUnit;

    private View view;

    private TextView mTv_cat_name;
    private TextView mTv_wan;
    private TextView mTv_wan_x;

    private FlowXLayout flowLayout;

    private boolean mIsSend = false;

    private TextView mTv_comment;

    private BookDetailHotAdapter bookDetailHotAdapter;
    private List<RecommendItemBean> recommendItemBeans;
    private List<RecommendItemBean> recommendOtherItemBeans;

    private BookDetailCommentAdapter bookDetailCommentAdapter;
    private List<CommentItemBean> commentItemBeans;

    private TextView mTv_switch, mTv_title_name;
    private TextView mTv_comment_more;

    private View mView_null_comment;

    private ImageView mIv_share, mIv_back;

    private View mView_bg;

    private XRelativeLayout xRelativeLayout;


    private int mTitleHeight = 0;

    private UserManager userManager;

    private View mView_first;

    private TextView mTv_title;
    private TextView mTv_chapter;

    private TextView mTv_next;

    private int textCount = -1;

    private XLinearLayout mRl_go;

    private ImageView mIv_go;

    private RecyclerView mRv_other;

    private BookDetailOtherReadAdapter bookDetailOtherReadAdapter;

    private TextView mTv_other;
    private View bg_w;
    /**
     * 上级页面
     */
    private String prevPageId;
    /**
     * 小说口令等来源
     */
    private String source;
    /**
     * 精确定位从哪里跳转过来的
     */
    private int modelId;

    private boolean isDeepLink;

    /**
     * 同类热门  大家都在读
     */
    private XRelativeLayout mRl_all_read;
    private XRelativeLayout mRl_hot_book;
    private View mView_all;
    private View mView_hot;

    private TextView mTv_go_comment;

    private TextView mTv_download;
    private XRelativeLayout mRlProgress;
    private ProgressBar mPbDownload;
    private TextView mTvProgress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        initViews();
        initData();

        EventBus.getDefault().register(this);
        //进入详情设置为true
        SharePreferenceUtils.putBoolean(this, SharePreferenceUtils.IS_IN_DETAIL, true);
    }

    private void initViews() {
        mView_null_comment = findViewById(R.id.view_null_comment);
        mTv_download = findViewById(R.id.btn_download);
        mTv_download.setOnClickListener(this);
        mTv_go_comment = findViewById(R.id.tv_go_comment);
        mTv_go_comment.setOnClickListener(this);
        mView_all = findViewById(R.id.view_all);
        mView_hot = findViewById(R.id.view_hot);
        mRl_all_read = findViewById(R.id.xrl_all_read);
        mRl_hot_book = findViewById(R.id.xrl_hot_book);
        bg_w = findViewById(R.id.view_bg_w);
        mTv_other = findViewById(R.id.tv_switch_other);
        mTv_other.setOnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRv_other = findViewById(R.id.rv_other);
        mRv_other.setLayoutManager(gridLayoutManager);
        mIv_go = findViewById(R.id.tv_go);
        mRl_go = findViewById(R.id.xrl_go);
        mTv_next = findViewById(R.id.tv_next);
        mView_first = findViewById(R.id.view_first_data);
        mTv_chapter = findViewById(R.id.tv_title);
        xRelativeLayout = findViewById(R.id.xll_title);
        mTv_title_name = findViewById(R.id.tv_title_name);
        mTv_title_name.setOnClickListener(this);
        mView_bg = findViewById(R.id.view_bg);
        mIv_back = findViewById(R.id.iv_back);
        mIv_back.setOnClickListener(this);
        mRl_go.setOnClickListener(this);
        flowLayout = findViewById(R.id.fl_list);
        mTv_comment = findViewById(R.id.tv_comment);
        mIv_share = findViewById(R.id.iv_share);
        mTv_comment.setOnClickListener(this);
        mIv_share.setOnClickListener(this);
        xRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTitleHeight = xRelativeLayout.getHeight();
                xRelativeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mView_bg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                scrollView.setOnScrollListener(new BookDetailNestedScrollView.OnScrollListener() {
                    @Override
                    public void onScroll(int scrollY) {
                        if (scrollY <= 0) {
                            mTv_title_name.setText("");
                            xRelativeLayout.setBackgroundColor(Color.argb(0, 255, 255, 255));
                            mIv_share.setImageResource(R.mipmap.icon_share_w);
                            mIv_back.setImageResource(R.mipmap.icon_back_w);
                        } else if (scrollY < (mView_bg.getHeight() - mTitleHeight)) {
                            //由于title和上层布局重叠需要减去title的高度，算出去除重叠部分的高度,最后算出透明度
                            if (TextUtils.isEmpty(mTv_title_name.getText().toString().trim())) {
                                mTv_title_name.setText(tvBookName.getText().toString().trim());
                            }
                            float alpha = (255 * ((float) scrollY / (mView_bg.getHeight() - mTitleHeight)));
                            mTv_title_name.setTextColor(Color.argb((int) alpha, 0, 0, 0));
                            xRelativeLayout.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                        } else {
                            mIv_share.setImageResource(R.mipmap.icon_share);
                            mIv_back.setImageResource(R.mipmap.icon_back);
                            mTv_title_name.setTextColor(Color.argb(255, 0, 0, 0));
                            xRelativeLayout.setBackgroundColor(Color.argb(255, 255, 255, 255));
                        }
                        scrollView.requestLayout();
                    }

                    @Override
                    public void onStopScroll() {

                    }

                    @Override
                    public void onStartScroll() {

                    }
                });

                mView_bg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mTv_switch = findViewById(R.id.tv_switch);
        mTv_switch.setOnClickListener(this);

        mTv_wan = findViewById(R.id.tv_wan);
        mTv_wan_x = findViewById(R.id.tv_wan_x);
        mTv_comment_more = findViewById(R.id.book_review_title);
        mTv_comment_more.setOnClickListener(this);

        mPromptLayoutHelper = getPromptLayoutHelper();
        scrollView = findViewById(R.id.scroll_view);
        ivTopBg = findViewById(R.id.book_detail_top_bg);
        ivCover = findViewById(R.id.book_cover);
        tvBookName = findViewById(R.id.book_name);
        tvAuthor = findViewById(R.id.book_author);
        tvStatus = findViewById(R.id.book_status);
        tvGrade = findViewById(R.id.book_grade);

        mTv_cat_name = findViewById(R.id.tv_cat_name);
        mTv_cat_name.setOnClickListener(this);

        tvWordCnt = findViewById(R.id.book_detail_wordcount);

        tvPopularity = findViewById(R.id.book_detail_popularity);

        tvFans = findViewById(R.id.book_detail_fan);

        tvRecommenWords = findViewById(R.id.book_detail_recomword);
        tvResume = findViewById(R.id.book_discription);
        tvResume.setOnClickListener(this);
        dotView = findViewById(R.id.book_detail_dot);

        adView = findViewById(R.id.ad_layout);

        recyclerView = findViewById(R.id.rv_hot);
        GridLayoutManager otherGridLayoutManager = new GridLayoutManager(BookDetailActivity.this, 2);
        recyclerView.setLayoutManager(otherGridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        mRv_other.setNestedScrollingEnabled(false);

        commentContainer = findViewById(R.id.book_comment_container);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentContainer.setLayoutManager(linearLayoutManager);
        commentContainer.setNestedScrollingEnabled(false);

        tvCahpter = findViewById(R.id.latest_chapter);
        tvUpdateTime = findViewById(R.id.update_time);
        findViewById(R.id.book_detail_catalogue_layout).setOnClickListener(this);

        tvSource = findViewById(R.id.book_detail_source);
        tvPublishTime = findViewById(R.id.book_detail_time);
        tvAddToBookShelf = findViewById(R.id.btn_add_shelf);
        findViewById(R.id.btn_add_shelf).setOnClickListener(this);
        readBtn = findViewById(R.id.btn_read);
        readBtn.setOnClickListener(this);

        moreLayout = findViewById(R.id.book_detail_open);
        moreLayout.setVisibility(View.GONE);
        moreLayout.setOnClickListener(this);

        tvPopularityUnit = findViewById(R.id.book_detail_popularity_unit);

        view = findViewById(R.id.divider_1px);
        mTv_title = findViewById(R.id.tv_first);

        mRlProgress = findViewById(R.id.rl_progress);
        mPbDownload = findViewById(R.id.pb_download);
        mTvProgress = findViewById(R.id.tv_progress);
//        mRlProgress.setOnClickListener(this);
//        setDownloaProgress(0);
    }

    /**
     * 设置下载进度
     */
    public void setDownloaProgress(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (progress == 0 || progress == 100) {
//                    mRlProgress.setVisibility(View.GONE);
//                    mTv_download.setVisibility(View.VISIBLE);
//                } else {
//                    mRlProgress.setVisibility(View.VISIBLE);
//                    mTv_download.setVisibility(View.GONE);
//                    mPbDownload.setProgress(progress);
//                    mTvProgress.setText(progress + "%");
//                }
            }
        });
    }


    @Override
    public String getCurrPageId() {
        return PageNameConstants.BOOK_DETAIL;
    }

    private void initData() {
        bookId = getIntent().getLongExtra(RouterPath.INSTANCE.KEY_BOOK_ID, 0);
        prevPageId = getIntent().getStringExtra(RouterPath.INSTANCE.KEY_PARENT_ID);
        source = getIntent().getStringExtra(RouterPath.INSTANCE.KEY_SOURCE) == null ? ""
                : getIntent().getStringExtra(RouterPath.INSTANCE.KEY_SOURCE);
        modelId = getIntent().getIntExtra(RouterPath.INSTANCE.KEY_MODEL_ID, 0);
        BaseData pageFrom = getIntent().getParcelableExtra(BaseActivity.DATA_KEY);

        // 判断是否外部唤起
        Uri data = getIntent().getData();
        if (data != null) {
            isDeepLink = true;
            String id = data.getQueryParameter("id");
            Logger.i(TAG, "id: " + id);//'10086'
            bookId = StringFormat.parseLong(id, 0);
            modelId = 21;
            FuncPageStatsApi.deepLink(bookId, PageNameConstants.BOOK_DETAIL);
        }
        //注册书架数据库变化监听.
        BookShelfHelper.getsInstance().addObserver(this);
        // 判断书籍是否重复显示
        if (currBookId == bookId && BaseApplication.context.getCurrPageId().equalsIgnoreCase(PageNameConstants.BOOK_DETAIL)) {
            finish();
            return;
        } else {
            currBookId = bookId;
        }


//        if (pageFrom != null && pageFrom.getFrom().equals("Notification")) {
//            modelId = 18;
//            prevPageId = PageNameConstants.NOTIFICATION;
//            FuncPageStatsApi.notifyBookClick(bookId);
////            Intent notifyIntent = new Intent(this, NotificationHolderService.class);
////            notifyIntent.putExtra("book_id", bookId);
////            startService(notifyIntent);
//        }
        // 处理通知栏的书籍点击事件
        if (pageFrom != null && pageFrom.getFrom().equals("PUSH")) {
            FuncPageStatsApi.notifyBookClick(bookId, modelId);
            // 每日推荐  进入书籍详情页  点击图片时需要刷新通知栏
            if (modelId == 3) {
                EventBus.getDefault().post(new DayEvent());
            }

        }
        userManager = UserManager.getInstance();
        // 判断书籍是否已经加入书架
        if (BookShelfPresenter.isAdded("" + bookId)) {
            disableAddButton();
        }
        // 加载评论 都在读 分类
        presenter = new BookDetailsPresenter(this);
        presenter.loadData(bookId);

        //进入详情页需要下载第一章内容
        //当用户处于A模块
        if (userManager.getUserInfo() != null && userManager.getUserInfo().model == 0) {
            mView_first.setVisibility(View.GONE);
        }
        presenter.preLoadNextChapter(bookId);
        //加载头部
        commentPresenter = new BookCommentPresenter(this);
        commentPresenter.loadData(bookId, 1);

        tooFastChecker = new TooFastChecker();

        recommendItemBeans = new ArrayList<>();
        bookDetailHotAdapter = new BookDetailHotAdapter(this, BookExposureMgr.PAGE_ID_DETAIL_HOT, String.valueOf(bookId), recommendItemBeans, this);
        recyclerView.setAdapter(bookDetailHotAdapter);
        commentItemBeans = new ArrayList<>();
        bookDetailCommentAdapter = new BookDetailCommentAdapter(this, commentItemBeans);
        commentContainer.setAdapter(bookDetailCommentAdapter);
        recommendOtherItemBeans = new ArrayList<>();
        bookDetailOtherReadAdapter = new BookDetailOtherReadAdapter(this, BookExposureMgr.PAGE_ID_DETAIL_READERS_LOOKING, String.valueOf(bookId), recommendOtherItemBeans, this);
        mRv_other.setAdapter(bookDetailOtherReadAdapter);


        // 传递参数
        BookExposureMgr.getInstance().setStatiscParams(prevPageId, source);
        //进入详情页.
        FunctionStatsApi.enterDetailsPage(bookId);
    }

    protected PromptLayoutHelper getPromptLayoutHelper() {
        View promptView = findViewById(R.id.load_prompt_layout);
        if (mPromptLayoutHelper == null) {
            mPromptLayoutHelper = new PromptLayoutHelper(promptView);
        }
        return mPromptLayoutHelper;
    }

    private void disableAddButton() {
        tvAddToBookShelf.setCompoundDrawablesWithIntrinsicBounds(ImageTool.getDrawable(this, R.mipmap.book_shelf_ok), null, null, null);
        tvAddToBookShelf.setText(R.string.already_in_shelf);
        tvAddToBookShelf.setTextColor(getResources().getColor(R.color.color_898989));
        tvAddToBookShelf.setEnabled(false);
    }

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
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_DEFAULT_EMPTY, null);
    }

    @Override
    public void showNetworkError() {
        getPromptLayoutHelper().showPrompt(PromptLayoutHelper.TYPE_NO_NET, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showLoading();
                presenter.loadData(bookId);
                commentPresenter.loadData(bookId, 1);
                if (userManager.getUserInfo() != null && userManager.getUserInfo().model == 0) {
                    mView_first.setVisibility(View.GONE);
                } else {
                    presenter.preLoadNextChapter(bookId);
                }
            }
        });
    }

    @Override
    public void showPage(BookDetailBean bookDetailBean) {
        this.mBookDetailBean = bookDetailBean;
        updateView(bookDetailBean);
        sendStatis();
        FuncPageStatsApi.bookDetailShow(bookId, prevPageId, modelId, source);
    }

    /**
     * 更新下载状态
     */
    private void updateDownloadStatus() {
        if (mBookDetailBean != null) {
            long count = BookDownloadHelper.getsInstance().queryDownloadCompleteTaskCount(mBookDetailBean.getBookId());
            if (mBookDetailBean.getLastChapter() <= count) {
                //本书所有章节已全部下载
                mTv_download.setText("已下载");
                mTv_download.setTextColor(getResources().getColor(R.color.color_898989));
                Drawable top = getResources().getDrawable(R.mipmap.bg_download_ok);
                mTv_download.setCompoundDrawablesWithIntrinsicBounds(null, top, null, null);
            }
        }
    }

    private void sendStatis() {
        if (mIsSend) {
            return;
        }
        mIsSend = true;

        //判断是否为掌阅书籍.
        if (mBookDetailBean != null && mBookDetailBean.getFrom() == 2) {
            StatisHelper.onEvent().bookDetail(mBookDetailBean.getBookName(), "0");
            MtStHelper.INSTANCE.bookDetails(mBookDetailBean.getBookId(), "0");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        currBookId = 0;
    }

    private void updateView(BookDetailBean bookDetailBean) {
        if (bookDetailBean.getParentId() != null && bookDetailBean.getParentId().equals("3")) {
            mRl_all_read.setVisibility(View.GONE);
            mRl_hot_book.setVisibility(View.GONE);
            mView_all.setVisibility(View.GONE);
            mView_hot.setVisibility(View.GONE);
        } else {
            mRl_all_read.setVisibility(View.VISIBLE);
            mRl_hot_book.setVisibility(View.VISIBLE);
            mView_all.setVisibility(View.VISIBLE);
            mView_hot.setVisibility(View.VISIBLE);
        }
//        if (AdManager.getInstance().showAd(AdConstants.Position.BOOK_DETAIL)) {
//            adView.setVisibility(View.VISIBLE);
//            AdManager.getInstance().createAdSource(this).loadCommonAd(null, adView, 800, 400, null);
//        } else {
        adView.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
//        }
        GlideUtils.INSTANCE.loadBlurBackground(this, bookDetailBean.getCover(), ivTopBg, 30, 2);
        GlideUtils.INSTANCE.loadImage(this, bookDetailBean.getCover(), ivCover);
        if (!TextUtils.isEmpty(bookDetailBean.getBookName())) {
            tvBookName.setText(bookDetailBean.getBookName());
        }
        if (!TextUtils.isEmpty(bookDetailBean.getAuthorName())) {
            tvAuthor.setText(bookDetailBean.getAuthorName());
        }
        if (bookDetailBean.getCategory() == null) {
            mTv_cat_name.setVisibility(View.INVISIBLE);
        } else {
            mTv_cat_name.setText(bookDetailBean.getCategory().getName());
            mTv_cat_name.setTag(bookDetailBean.getCategory());
        }

        if (bookDetailBean.getState() == 1) {
            tvStatus.setText(R.string.updating);
        } else if (bookDetailBean.getState() == 2) {
            tvStatus.setText(R.string.finished);
        } else {
            tvStatus.setText(R.string.stop_update);
        }

        tvGrade.setText("" + bookDetailBean.getStar());
        tvWordCnt.setText((int) (bookDetailBean.getWordCount() * 1f / 10000) + "万字");
        // 判断人气值和粉丝数是否超过1万
        if (bookDetailBean.getPopularityNum() >= 100000000) {
            tvPopularity.setText(String.format("%.1f", bookDetailBean.getPopularityNum() * 1f / 100000000));
            mTv_wan.setText("亿");
            tvPopularityUnit.setText("人气");
        } else if (bookDetailBean.getPopularityNum() >= 10000) {
            tvPopularity.setText(String.valueOf((int) (bookDetailBean.getPopularityNum() * 1f / 10000f)));
            tvPopularityUnit.setText("人气");
            mTv_wan.setText("万");
        } else {
            tvPopularity.setText("" + bookDetailBean.getPopularityNum());
            tvPopularityUnit.setText("人气");
            mTv_wan.setText("");
        }
        if (bookDetailBean.getFansNum() >= 10000) {
            tvFans.setText(String.format("%.1f", bookDetailBean.getFansNum() * 1f / 10000f));
            mTv_wan_x.setText("万");
        } else {
            tvFans.setText("" + bookDetailBean.getFansNum());

            mTv_wan_x.setText("");
        }


        if (!TextUtils.isEmpty(bookDetailBean.getRecWords())) {
            tvRecommenWords.setVisibility(View.VISIBLE);
            tvRecommenWords.setText(bookDetailBean.getRecWords());
            dotView.setVisibility(View.VISIBLE);
        } else {
            tvRecommenWords.setVisibility(View.GONE);
            dotView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bookDetailBean.getResume())) {
            tvResume.setText(bookDetailBean.getResume());

            tvResume.setVisibility(View.VISIBLE);
            Logger.e(TAG, "行数:" + tvResume.getLineCount());
            if (tvResume.getLineCount() > 3) {
                tvResume.setMaxLines(3);
                moreLayout.setVisibility(View.VISIBLE);

                int lineEndIndex = tvResume.getLayout().getLineEnd(2);
                String text = tvResume.getText().subSequence(0, lineEndIndex - 5) + "...";
                tvResume.setText(text);
                Logger.d(TAG, "NewText:" + text);
            } else {
                tvResume.setMaxLines(tvResume.getLineCount());
                moreLayout.setVisibility(View.GONE);
            }
        } else {
            moreLayout.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(bookDetailBean.getFromSource())) {
            //设置显示来源.
            findViewById(R.id.book_detail_source_layout).setVisibility(View.VISIBLE);
            tvSource.setText(bookDetailBean.getFromSource());
        }
        if (!TextUtils.isEmpty(bookDetailBean.getIssueTime())) {
            tvPublishTime.setText(bookDetailBean.getIssueTime());
        }

//        updateDownloadStatus();

        setCatalogue(bookDetailBean);

    }

    private void setCatalogue(final BookDetailBean bookDetailBean) {
        if (bookDetailBean.getState() == 2) {
            tvCahpter.setText(ViewUtils.getString(R.string.chapter_count, bookDetailBean.getLastChapter()));
            tvUpdateTime.setVisibility(View.VISIBLE);
            tvUpdateTime.setText("已完结");
        } else {
            tvCahpter.setText(ViewUtils.getString(R.string.latest_chapter, bookDetailBean.getLastChapter()));
            if (!TextUtils.isEmpty(bookDetailBean.getLastChapIssueTime())) {
                tvUpdateTime.setVisibility(View.VISIBLE);
                String lastDate = TimeUtils.YMD_FORMAT.format(Long.parseLong(bookDetailBean.getLastChapIssueTime()) * 1000);
                String currentDate = TimeUtils.YMD_FORMAT.format(TimeUtils.getCurTime());
                long day = TimeUtils.getDaySub(lastDate, currentDate);
                if (day <= 1) {
                    tvUpdateTime.setText(((day == 1) ? "昨天" : "今天") + TimeUtils.MD_FORMAT_X.format(Long.parseLong(bookDetailBean.getLastChapIssueTime()) * 1000) + "更新");
                } else if (TimeUtils.getCurTime() - TimeUtils.parseTime(bookDetailBean.getLastChapIssueTime()) * 1000 < 48 * 60 * 60 * 1000) {//前天,但是更新时间小于48小时
                    tvUpdateTime.setText("前天" + TimeUtils.MD_FORMAT_X.format(Long.parseLong(bookDetailBean.getLastChapIssueTime()) * 1000) + "更新");
                } else {
                    //是否今天进入过
                    boolean hasIn = false;
                    //该书籍对应的缓存对象
                    BookDetailCacheBean bookDetailCacheBean = null;
                    //获取缓存的对象列表
                    List<BookDetailCacheBean> bookDetailBeanLast = SharePreferenceUtils.getList(BookDetailActivity.this, SharePreferenceUtils.BOOK_DETAIL_CACHE);

                    if (bookDetailBeanLast != null && bookDetailBeanLast.size() > 0) {

                        //缓存列表包含相同bookId的对象
                        if (bookDetailBeanLast.contains(new BookDetailCacheBean(bookDetailBean.getBookId(), 0))) {
                            int i = bookDetailBeanLast.indexOf(new BookDetailCacheBean(bookDetailBean.getBookId(), 0));
                            BookDetailCacheBean bookDetailCacheBean1 = bookDetailBeanLast.get(i);
                            long randomTime = bookDetailCacheBean1.getRandomTime();
                            if (TimeUtils.getTomorowMilinSeconds(randomTime) > TimeUtils.getCurTime()) {//是今天
                                hasIn = true;
                                bookDetailCacheBean = bookDetailCacheBean1;
                            }
                        }
                    }

                    if (hasIn) {
                        String time = TimeUtils.MD_FORMAT_X.format(bookDetailCacheBean.getRandomTime());
                        tvUpdateTime.setText("更新于今天" + time);
                    } else {
                        //随机生成"更新于今天HH:MM",HH:MM为当前时间往前推15-60分钟的随机时间
                        long updateTime = TimeUtils.getCurTime() - (new Random().nextInt(46) + 15) * 60 * 1000;
                        String time = TimeUtils.MD_FORMAT_X.format(updateTime);
                        tvUpdateTime.setText("更新于今天" + time);
                        //为空时先创建
                        if (bookDetailBeanLast == null) bookDetailBeanLast = new ArrayList<>();
                        //集合存在,但不是今天进入的,先移除再添加
                        if (bookDetailCacheBean != null)
                            bookDetailBeanLast.remove(bookDetailCacheBean);
                        bookDetailBeanLast.add(new BookDetailCacheBean(bookDetailBean.getBookId(), updateTime));

                        //缓存
                        SharePreferenceUtils.putList(BookDetailActivity.this, SharePreferenceUtils.BOOK_DETAIL_CACHE, bookDetailBeanLast);
                    }
                }
            } else {
                tvUpdateTime.setVisibility(View.GONE);
            }
        }

        if (bookDetailBean.getTagsInfo() == null) {
            flowLayout.setVisibility(View.GONE);
        } else {
            String[] tags = bookDetailBean.getTagsInfo().split(",");
            flowLayout.setAdapter(Arrays.asList(tags), R.layout.item_detail_tag, new FlowXLayout.ItemView<String>() {
                @Override
                public void getCover(String item, FlowXLayout.ViewHolder holder, View inflate, int position) {
                    holder.setText(R.id.tv_text, item);
                    TextView textView = inflate.findViewById(R.id.tv_text);
                    textView.setTag(item);
                    textView.setOnClickListener(BookDetailActivity.this);
                }
            });
        }

    }

    @Override
    public void showAdPage(Object adObject) {

    }

    @Override
    public void showRecommend(RecommendBean recommendBean) {
        //重置曝光BookId.
        BookExposureMgr.refreshBookData(BookExposureMgr.PAGE_ID_DETAIL_HOT);
        recommendItemBeans.clear();
        recommendItemBeans.addAll(recommendBean.getBookList());
        bookDetailHotAdapter.notifyItemRangeChanged(0, recommendItemBeans.size());
    }

    @Override
    public void showComment(CommentListBean commentList) {
        if (commentList != null && commentList.getCommentList() != null && commentList.getCommentList().size() > 0) {
            if (mView_null_comment.getVisibility() == View.VISIBLE) {
                mView_null_comment.setVisibility(View.GONE);
                mTv_comment_more.setVisibility(View.VISIBLE);
                commentContainer.setVisibility(View.VISIBLE);
            }
            commentItemBeans.clear();
            commentItemBeans.addAll(commentList.getCommentList());
            bookDetailCommentAdapter.notifyItemRangeChanged(0, commentItemBeans.size());
            mTv_comment_more.setVisibility(commentList.getCommentList().size() <= 3 ? View.GONE : View.VISIBLE);
        } else {
            mView_null_comment.setVisibility(View.VISIBLE);
            mTv_comment_more.setVisibility(View.GONE);
            commentContainer.setVisibility(View.GONE);
        }

    }

    @Override
    public void loadFirstChapterData(final String data, final String title) {
        if (userManager.getUserInfo() != null && userManager.getUserInfo().model == 1) {
            mTv_title.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTv_chapter.setText(title);
                    mTv_title.setText(data);
                    XRelativeLayout.LayoutParams layoutParams = (XRelativeLayout.LayoutParams) bg_w.getLayoutParams();
                    layoutParams.height = mTv_title.getLineHeight() + (int) (mTv_title.getLineSpacingExtra() + mTv_title.getLineSpacingExtra() / 2);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        XLinearLayout.LayoutParams params = (XLinearLayout.LayoutParams) mRl_go.getLayoutParams();
                        params.topMargin = ViewUtils.dp2px(16);
                        mRl_go.setLayoutParams(params);
                    }
                    bg_w.setLayoutParams(layoutParams);
                    textCount = mTv_title.getLineCount();
                    if (textCount > 16) {
                        mTv_title.setMaxLines(16);
                    } else {
                        mTv_title.setMaxLines(textCount);
                    }
                }
            }, 100);
        }

    }

    @Override
    public void loadOtherReadData(RecommendBean recommendBean) {
        //重置曝光BookId.
        BookExposureMgr.refreshBookData(BookExposureMgr.PAGE_ID_DETAIL_READERS_LOOKING);
        recommendOtherItemBeans.clear();
        recommendOtherItemBeans.addAll(recommendBean.getBookList());
        bookDetailOtherReadAdapter.notifyItemRangeChanged(0, recommendOtherItemBeans.size());
    }

    @Override
    public void loadSaveComment() {

    }


    @Override
    public void onClick(@NotNull View v) {
        if (tooFastChecker.isTooFast()) {
            return;
        }
        super.onClick(v);
        if (mBookDetailBean == null) {
            ToastUtils.showLimited("书籍数据获取失败");
            return;
        }
        switch (v.getId()) {
            case R.id.book_detail_catalogue_layout:
                ActivityHelper.INSTANCE.gotoCatalogue(this, mBookDetailBean);
                //点击目录.
                FunctionStatsApi.bdDirPageBookClick(bookId);
                FuncPageStatsApi.bookDetailCatalogue(bookId, prevPageId, source);
                break;
            case R.id.book_review_title:
                Intent intent = new Intent(BookDetailActivity.this, BookCommentActivity.class);
                intent.putExtra(RouterPath.INSTANCE.KEY_BOOK_ID, bookId);
                intent.putExtra(RouterPath.INSTANCE.KEY_SOURCE, source);
                startActivityForResult(intent, 1002);
                //点击更多书评.
                FunctionStatsApi.bdMoreBookReviewBookClick(bookId);
                FuncPageStatsApi.bookDetailMoreComment(bookId, prevPageId, source);
                break;
            case R.id.tv_title_name:
                scrollView.fullScroll(ScrollView.FOCUS_UP);
                break;
            case R.id.tv_switch_other: // 这本书的读者都在看
                presenter.loadOtherReadBooks(bookId);
                FuncPageStatsApi.bookDetailSimilarSwitch(prevPageId, source, bookId);
                break;
            case R.id.xrl_go:
                if (mTv_title.getTag() == null) {
//                    if (textCount > mTv_title.getMaxLines()) {
//                        if (mTv_title.getMaxLines() + 32 < textCount) {
//                            mTv_title.setMaxLines(mTv_title.getMaxLines() + 32);
//                        } else {
//                            mTv_title.setMaxLines(textCount);
//                        }
//                    } else {
//                        mTv_title.setMaxLines(textCount);
//                    }
                    mTv_title.setMaxLines(textCount);
                    mTv_title.setTag(false);
                    mTv_next.setText("继续阅读下一章");
                    mIv_go.setImageResource(R.mipmap.icon_my_list_back_right_w);
                    bg_w.setVisibility(View.GONE);
//                    if (mTv_title.getMaxLines() == textCount) {
//
//                    }
                    //B方案阅读第一章节.
                    FunctionStatsApi.bdReadFirstChapterB(bookId);
                    FuncPageStatsApi.bookDetailFirstChapter(bookId, prevPageId, source);
                } else {
                    ActivityHelper.INSTANCE.gotoRead(this, String.valueOf(bookId), 2, new BaseData("目录"), PageNameConstants.BOOK_DETAIL, source);
                    //B方案继续阅读下一次章节.
                    FunctionStatsApi.bdContinueRead2ChapterB(bookId);
                    FuncPageStatsApi.bookDetailSecondChapter(bookId, prevPageId, source);
                }
                break;
            case R.id.iv_share:
                String title = "《" + mBookDetailBean.getBookName() + "》这本小说很不错，推荐你读。";
                String content = mBookDetailBean.getResume().length() > 50
                        ? mBookDetailBean.getResume().substring(0, 50) + "..."
                        : mBookDetailBean.getResume();
                String cover = mBookDetailBean.getCover();
                String url = Constants.DOMAIN_SHARE_H5 + "/book/" + mBookDetailBean.getBookId();
                CustomShareManger.getInstance().shareBookWithText(
                        getActivity(), title, content,
                        R.mipmap.share_big_img, cover, url, new BottomShareDialog.ShareClickListener() {
                            @Override
                            public void onClick(int type) {
                                FuncPageStatsApi.shareClick(bookId, PageNameConstants.READER, type, source);
                            }
                        }, new BottomShareDialog.ShareResultListener() {
                            @Override
                            public void onShare(int shareResult) {
                                if (shareResult != 1) {
                                    return;
                                }
                                TaskMgr.show(BookDetailActivity.this, getSupportFragmentManager(), getResources().getString(R.string.finish_share_task), TaskMgr.SHARE_TASK);
                            }
                        }
                );
                break;
            case R.id.btn_add_shelf:
                addToBookshelf();
                //点击添加书籍按钮.
                if (userManager.getUserInfo() != null) {
                    FunctionStatsApi.bdAddBookShelf(userManager.getUserInfo().model == 0 ? "1" : "2", bookId);
                    FuncPageStatsApi.bookDetailAddBook(bookId, prevPageId, userManager.getUserInfo().model == 0 ? 1 : 2, source);
                } else {
                    FunctionStatsApi.bdAddBookShelf("1", bookId);
                    FuncPageStatsApi.bookDetailAddBook(bookId, prevPageId, 1, source);
                }
                break;
            case R.id.btn_read:
                ActivityHelper.INSTANCE.gotoReadForResult(this, mBookDetailBean.getBookId(), new BaseData("详情"), PageNameConstants.BOOK_DETAIL, source, REQUEST_CODE_READ);
                //点击继续阅读.
                if (userManager.getUserInfo() != null) {
                    FunctionStatsApi.bdContinueReadClick(userManager.getUserInfo().model == 0 ? "1" : "2", String.valueOf(bookId));
                    FuncPageStatsApi.bookDetailReadClick(bookId, prevPageId, userManager.getUserInfo().model == 0 ? 1 : 2, source);
                } else {
                    FunctionStatsApi.bdContinueReadClick("1", String.valueOf(bookId));
                    FuncPageStatsApi.bookDetailReadClick(bookId, prevPageId, 1, source);
                }
                break;
            case R.id.book_detail_open:
            case R.id.book_discription:
                // 控制书籍介绍展开和收起
                if (tvResume.getLineCount() > 3) {
                    tvResume.setMaxLines(3);
                    int lineEndIndex = tvResume.getLayout().getLineEnd(2);
                    String text = tvResume.getText().subSequence(0, lineEndIndex - 4) + "...";
                    tvResume.setText(text);
                } else {
                    tvResume.setMaxLines(Integer.MAX_VALUE / 2);
                    tvResume.setText(mBookDetailBean.getResume());
                }

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(moreLayout, "rotation", tvResume.getLineCount() > 3 ? 180f : 0f);
                objectAnimator.setDuration(300);
                objectAnimator.start();
                break;
            case R.id.tv_comment:
            case R.id.tv_go_comment:
                //游客登录
                if (userManager.getUserInfo().type == 1) {

                    Intent intent1 = new Intent(BookDetailActivity.this, LoginActivity.class);
                    startActivityForResult(intent1, 1002);
                } else {
                    Intent intent1 = new Intent(BookDetailActivity.this, WriteBookCommentActivity.class);
                    intent1.putExtra(WriteBookCommentActivity.BOOK_ID, bookId);
                    intent1.putExtra(RouterPath.INSTANCE.KEY_PARENT_ID, PageNameConstants.BOOK_DETAIL);
                    intent1.putExtra(RouterPath.INSTANCE.KEY_SOURCE, source);

                    startActivityForResult(intent1, 1002);
                    //进入评论页面.
                    FunctionStatsApi.bdWriteBookReview("1", bookId);
                    FuncPageStatsApi.bookDetailEditComment(prevPageId, PageNameConstants.BOOK_DETAIL, 1, source);
                }

                break;

            case R.id.tv_switch:
                presenter.updateRecoomendBooks(bookId, false);
                FuncPageStatsApi.bookDetailHotSwitch(prevPageId, source, bookId);
                break;
            case R.id.iv_back:
                finish();
                currBookId = 0;
                break;
            case R.id.xrl_hot:
                //同类热门书籍.
                com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(this,
                        String.valueOf(v.getTag()), new BaseData(""),
                        PageNameConstants.BOOK_DETAIL, 14, source);
                FunctionStatsApi.bdSimilarPopularClick(String.valueOf(v.getTag()));
                FuncPageStatsApi.bookDetailHotClick(String.valueOf(v.getTag()), prevPageId, source, bookId);
                break;
            case R.id.xrl_other:
                //这本书的读者都在看.
                com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoBookDetails(this,
                        String.valueOf(v.getTag()), new BaseData(""),
                        PageNameConstants.BOOK_DETAIL, 14, source);
                FunctionStatsApi.bdReadersLookingClick(String.valueOf(v.getTag()));
                FuncPageStatsApi.bookDetailSimilarClick(String.valueOf(v.getTag()), prevPageId, source, bookId);
                break;
            case R.id.tv_text:
                Intent intent1 = new Intent(BookDetailActivity.this, BookDetailCategoryActivity.class);
                intent1.putExtra("title", (String) v.getTag());
                startActivityForResult(intent1, BookDetailActivity.REQUEST_CODE_READ);
                FuncPageStatsApi.bookDetailCltagClick((String) v.getTag(), bookId);
                break;

            case R.id.tv_cat_name:
                BookDetailCategoryBean bookDetailCategoryBean = (BookDetailCategoryBean) v.getTag();
                CategoryBean categoryBean = new CategoryBean();
                if (bookDetailCategoryBean.getTags() != null) {
                    categoryBean.setTags(bookDetailCategoryBean.getTags());
                }
                categoryBean.setId(String.valueOf(bookDetailCategoryBean.getId()));
                categoryBean.setName(bookDetailCategoryBean.getName());
                categoryBean.setSex(bookDetailCategoryBean.getParentId());
                categoryBean.setSubCategories(bookDetailCategoryBean.getSubCategorys());
                com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.INSTANCE.gotoCategoryBookList(BookDetailActivity.this, categoryBean);
                FuncPageStatsApi.bookDetailClsClick(bookDetailCategoryBean.getId(), bookId);
                break;
            case R.id.btn_download:
                //已下载图标为btn_download_ok
                if (UserManager.getInstance().getUserInfo() != null &&
                        UserManager.getInstance().getUserInfo().type != 1) {
                    //已登录
//                    gotoBookDownloadActivity();
                    presenter.getDownloadOption(bookId);
                } else {
                    //未登录
                    Intent intent2 = new Intent(BookDetailActivity.this, LoginActivity.class);
                    startActivityForResult(intent2, REQUEST_CODE_DOWNLOAD);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1002 && resultCode == 1003) {
            //刷新评论内容
            commentPresenter.loadData(bookId, 1);
        }
        if (requestCode == 1002 && resultCode == 1008) {
            //登录成功后刷新用户信息
            userManager = UserManager.getInstance();
            if (userManager.getUserInfo() != null && userManager.getUserInfo().model == 1) {
                presenter.preLoadNextChapter(bookId);
                mView_first.setVisibility(View.VISIBLE);
            } else {
                mView_first.setVisibility(View.GONE);
            }
        }
        if (requestCode == REQUEST_CODE_DOWNLOAD && resultCode == 1008) {
            presenter.getDownloadOption(bookId);
        }
        if (requestCode == REQUEST_CODE_READ && data != null) {
            if (data.getBooleanExtra("exit", false)) {
                bookReopendFinish();
                finish();
            }
        }
    }


    public void gotoBookDownloadActivity() {
        if (BookDownloadManager.getsInstance().isDownloadingOrPending(bookId)) {
            ToastUtils.show("该书籍正在下载队列中...");
        } else {
            Intent intent2 = new Intent(BookDetailActivity.this, BookDownloadActivity.class);
            intent2.putExtra(BookDownloadActivity.BOOK_ID, bookId);
            intent2.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.BOOK_DETAIL);
            intent2.putExtra(BookDownloadActivity.BOOK_NAME, mBookDetailBean.getBookName());
            intent2.putExtra(RouterPath.KEY_PARENT_ID, prevPageId);
            intent2.putExtra(RouterPath.KEY_SOURCE, source);
            startActivity(intent2);
            FuncPageStatsApi.bookDetailReadDownload(PageNameConstants.BOOK_DETAIL, bookId, source);
        }
    }

    /**
     * 如果书籍重复打开则直接退出，并通知详情页退出
     */
    private void bookReopendFinish() {
        Intent intent = new Intent();
        intent.putExtra("exit", true);
        setResult(BookDetailActivity.REQUEST_CODE_READ, intent);
        finish();
        return;
    }

    private void addToBookshelf() {
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return BookShelfPresenter.addBookShelf(mBookDetailBean);
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (ReadHistoryMgr.HTTP_OK.equals(s)) {
                    //添加书架成功.
                    StatisHelper.onEvent().subscription(mBookDetailBean.getBookName(), "详情页加入书架");
                    ToastUtils.showLimited(R.string.add_shelf_success);
                    disableAddButton();
                    setResult(10003);
                } else {
                    //添加书架失败.
                    ToastUtils.showLimited(s);
                }
            }
        });
    }

    @Override
    public void onShelfChange(@NonNull ShelfEvent event) {
        if (event != null && event.mType == ShelfEvent.TYPE_ADD) {
            //获取书架书籍信息.
            BookShelfBean bookShelfBean = !StringFormat.isEmpty(event.mChangeList) ? event.mChangeList.get(0) : null;
            if (bookShelfBean != null && !StringUtils.isEmpty(bookShelfBean.getBookId()) && bookShelfBean.getBookId().equals(String.valueOf(bookId))) {
                //修改当前按钮为已添加书架状态.
                disableAddButton();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bookId > 0 && readBtn != null) {
            //查询阅读历史记录.
            BookRecordBean bookRecordBean = ReadHistoryPresenter.findBookRecordBean(String.valueOf(bookId));
            if (bookRecordBean != null) {
                //readBtn.setText(ViewUtils.getString(R.string.continue_read_chapter, bookRecordBean.seqNum));
                readBtn.setText(R.string.continue_read);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        // 上一页不能是阅读器页
        if (TextUtils.isEmpty(prevPageId) || (prevPageId.equalsIgnoreCase(PageNameConstants.READER) && !Utils.isExsitMianActivity(this, HomeActivity.class)) || isDeepLink) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.distory();
        }
        //注销书架数据库变化监听.
        BookShelfHelper.getsInstance().removeObserver(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadBookEvent(BookDownloadEvent event) {

        if (bookId == event.getBookId()) {
            if (event.getDownloadStates() == BookDownloadEvent.DOWNLOADING) {
                int progress = (int) ((new BigDecimal((float) event.getProgress() / event.getTotal()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) * 100);
                Logger.i(TAG, "progress :   " + progress);
                setDownloaProgress(progress);
            } else if (event.getDownloadStates() == BookDownloadEvent.DOWNLOAD_COMPLETE) {
                updateDownloadStatus();
                setDownloaProgress(100);
            } else {
                setDownloaProgress(0);
                showRetryDialog(event.getBookDownloadTask());
            }
        }
    }

    private void showRetryDialog(final BookDownloadTask task) {
        try {
            String message = "当前网络不稳定\n是否重试下载?";
            SimpleDialog simpleDialog = new SimpleDialog.Builder(this)
                    .setCanceledOnTouchOutside(false)
                    .setTitle(message)
                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭Dialog.
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            BookDownloadManager.getsInstance().retryDownload(task);
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭Dialog.
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            BookDownloadManager.getsInstance().removeTask(task);
                        }
                    }).create();
            //显示Dialog.
            simpleDialog.show();
        } catch (Throwable throwable) {
            Logger.e(TAG, "removeBook: {}", throwable);
        }
    }

    /**
     * 弹出下载弹窗
     */
    @Override
    public void showDownloadDialog(ChapterDownloadOptionResp resp) {
        DownloadBottomDialog downloadBottomDialog = new DownloadBottomDialog(this, resp, mBookDetailBean, PageNameConstants.BOOK_DETAIL, source);
        downloadBottomDialog.show();
    }

    public interface Presenter {

        void loadData(long bookId);

        void updateRecoomendBooks(long bookId, boolean bool);

        void preLoadNextChapter(long bookId);

        void distory();

        void loadOtherReadBooks(long bookId);

        void getDownloadOption(long bookId);
    }

    public interface CommentPresenter {
        void loadData(long bookId, int pageIndex);

        void distory();
    }
}
