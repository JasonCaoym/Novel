package com.duoyue.mianfei.xiaoshuo.read.ui.read;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.bean.BookDownloadDBBean;
import com.duoyue.app.bean.BookDownloadTask;
import com.duoyue.app.common.data.response.ReadTaskResp;
import com.duoyue.app.common.data.response.bookdownload.ChapterDownloadOptionResp;
import com.duoyue.app.common.data.response.bookshelf.TaskFinishResp;
import com.duoyue.app.common.mgr.PermissionMgr;
import com.duoyue.app.common.mgr.PushMgr;
import com.duoyue.app.common.mgr.TaskMgr;
import com.duoyue.app.event.BookDownloadEvent;
import com.duoyue.app.event.TaskFinishEvent;
import com.duoyue.app.notification.NotificationsUtils;
import com.duoyue.app.presenter.BookShelfPresenter;
import com.duoyue.app.ui.activity.BookDetailActivity;
import com.duoyue.app.ui.activity.BookDownloadActivity;
import com.duoyue.app.ui.activity.LoginActivity;
import com.duoyue.app.ui.activity.ReadRecommendActivity;
import com.duoyue.app.ui.dialog.DownloadBottomDialog;
import com.duoyue.app.ui.fragment.ReadSleepFragment;
import com.duoyue.app.ui.view.BookDoTaskFragment;
import com.duoyue.app.upgrade.ReadModeUtil;
import com.duoyue.app.upgrade.UpgradeManager;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.user.MobileInfoPresenter;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.customshare.CustomShareManger;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.devices.SystemUtil;
import com.duoyue.lib.base.event.AdConfigEvent;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.io.IOUtil;
import com.duoyue.lib.base.location.BDLocationMgr;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ScheduledService;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.lib.base.widget.AdFrameLayout;
import com.duoyue.lib.base.widget.SimpleDialog;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper;
import com.duoyue.mianfei.xiaoshuo.read.page.*;
import com.duoyue.mianfei.xiaoshuo.read.presenter.ReadPresenter;
import com.duoyue.mianfei.xiaoshuo.read.presenter.view.IReadPage;
import com.duoyue.mianfei.xiaoshuo.read.setting.BrightnessMgr;
import com.duoyue.mianfei.xiaoshuo.read.setting.ReadSettingManager;
import com.duoyue.mianfei.xiaoshuo.read.utils.*;
import com.duoyue.mianfei.xiaoshuo.read.utils.StatusBarUtils;
import com.duoyue.mianfei.xiaoshuo.read.utils.StringUtils;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.common.PageNameConstants;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.ChapterBean;
import com.zydm.base.data.dao.ChapterListBean;
import com.zydm.base.rx.MtSchedulers;
import com.zydm.base.statistics.umeng.StatisHelper;
import com.zydm.base.tools.TooFastChecker;
import com.zydm.base.ui.activity.BaseActivity;
import com.zydm.base.ui.item.AdapterBuilder;
import com.zydm.base.ui.item.ItemListenerAdapter;
import com.zydm.base.ui.item.ListAdapter;
import com.zydm.base.utils.*;
import com.zydm.base.widgets.BottomShareDialog;
import com.zydm.statistics.motong.MtStHelper;
import com.zzdm.ad.router.BaseData;
import com.zzdm.ad.router.RouterPath;
import io.reactivex.Observable;
import io.reactivex.*;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@Route(path = RouterPath.Read.PATH_READ)
public class ReadActivity extends BaseActivity implements IReadPage, View.OnClickListener {

    /**
     * 日志Tag
     */
    private static final String TAG = "ad#ReadActivity";
    public static final int REQUEST_CODE_DOWNLOAD = 1010;
    public static final int REQUEST_CODE_BANNER = 0x1234;

    public static final String KEY_LEVEL = "level";
    public static long gBookId = 0;
    public static int gRewarTastId;

    public static float csjInfoAdHeight = ViewUtils.dp2px(290);
    View mReadTopMenu;
    PageView mPvReadPage;
    View errorTipLayout;
    View readLoadingLayout;
    TextView mReadTvCategory;
    TextView mReadTvNightMode;
    TextView mReadTvSetting;
    LinearLayout mReadBottomMenuRoot;
    TwoDirectionPullListView mRvReadCategory;
    DrawerLayout mDrawerLayout;
    private TextView mReadTitleTv;
    private TextView mOrderTv;

    private ReadSettingDialog mSettingDialog;
    private PageLoader mPageLoader;
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private ObjectAnimator mBottomInAnim;
    private ObjectAnimator mBottomOutAnim;
    private BookRecordBean mBook;
    private boolean isNightMode = false;
    private boolean isFullScreen = false;
    private String mBookId = "0";
    ListAdapter mReadCategoryAdapter;
    List<List<TxtChapter>> mTxtChapters;
    private ReadPresenter mReadPresenter;
    List<List<ChapterBean>> bookChapterList;
    private String prevPageId;
    private String sourceStats;
    private ImageView ivMoreMenu;
    private ImageView ivDownload;
    private ImageView ivBack;
    private AdSiteBean flowAdSiteBean;

    /**
     * 下方正在下载控件
     */
    private LinearLayout layoutDownloadBottom;
    private TextView tvDownloadBottom;
    private ImageView ivDownloadClose;

    private ViewGroup bottomAdRootView;
    private AdFrameLayout bottomAdView;
    private TextView tvBottomAdTip;
    private View adClear;
    /**
     * 是否本书正在下载
     */
    private boolean isDownloading;

    /**
     * 是否隐藏下载进度控件
     */
    private boolean isHideDownloadLayout;

    /**
     * 书籍来源(1:运营上传;2:掌阅接口;3:掌阅爬虫接口)
     */
    private int mBookFrom;

    private List<TxtChapter> prevChapters = new ArrayList<>();
    private PopupWindow windowPopup;
    private long offScreenTime;

    private Button btnErrorBack;
    private Button btnErrorRetry;
    // 总章节数
    private int totalChapter;
    /**
     * 免广告时长：分钟
     */
    private int tiredFreeTime;
    /**
     * 疲劳弹框时长
     */
    private long showTiredDialogTime;
    /**
     * 阅读累计时长，回到后台的时间不算
     */
//    private long tiredTotalTime = 0;
    /**
     * 分段阅读起始时长
     */
//    private long rstRdTime;

    private ReadSleepFragment dialogFragment;
    private boolean isTiredDialogShow;
    private AdSiteBean tiredAdSiteBean;
    private boolean isTaskDialogShow;
    private boolean hasShowDoubleDialog;
    private boolean hasInitBannerTask;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(KEY_LEVEL, 0);
                mPageLoader.updateBattery(level);
            } else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                mPageLoader.updateTime();
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (PhoneUtil.isNetworkAvailable(ReadActivity.this)) {

                    if (mPageLoader.getPageStatus() == AbsPageLoader.STATUS_ERROR) {
                        mPageLoader.setPageStatus(AbsPageLoader.STATUS_LOADING);
                        errorTipLayout.setVisibility(GONE);
                        // 是否已经成功加载过
                        if (prevChapters != null && prevChapters.size() > 0) {
                            Logger.e(TAG, "之前载过");
                            mReadPresenter.loadContent(mBookId, prevChapters, sourceStats, "onReceive");
                        } else {
                            Logger.e(TAG, "没有加载过");
                            initData();
                        }
                    }

                    //重启下载任务
                    if (TextUtils.equals(prevPageId, PageNameConstants.SPLASH)) {
                        BookDownloadManager.getsInstance().checkDownloadTask();
                    }
                }
            }
        }
    };
    private int mTargetSeqNum;
    private int lastReadedChapterGroup = -1;
    private int lastReadedChapterPos = -1;
    private View mReadBottomMenu;
    private ArrayList<TxtChapter> mChaptersForCatalogue;
    private boolean mIsNormalOrderType = true;
    private View mTiTleLayout;
    private TextView mCatalogue;
    private String mFrom;
    private int mType = ReadPresenter.LOAD_CATALOGUE_TYPE_NORMAL;
    private View mGuideView;
    private View mGuideBtn;
    private ExtraPageMgr mExtraPageMgr;
    private Handler mHandler;
    private View flowAdFrameLayout;
    private LinearLayout adRootView;
    private AdFrameLayout adContainer;
    private FrameLayout.LayoutParams adParams;
    private TextView tvAdTip;
    private ViewGroup videoRootView;
    private TooFastChecker tooFastChecker = new TooFastChecker(400);

    /**
     * 当前章节
     */
    private int mCurSeqNum;

    /**
     * 当前页码.
     */
    private int mCurPageNumber;

    /**
     * 当前页停留时长(毫秒).
     */
    private long mCurPageReadingTime;
    private boolean isShowToBack;
    private TextView mBookName;

    /**
     * 进入阅读器时间.
     */
    private long mStartReadTime;

    /**
     * 内容渲染状态(0:加载中;1:渲染成功;2:渲染失败).
     */
    private int mRenderingState = 0;

    private long enterTime;
    //今天阅读时长
    private long mTodayReadTime;
    //阅读任务完成状态  1：可完成；3：已完成
    private int mStatus;
    private List<ReadTaskResp.ReadStageBean> mReadStage;
    private int currStage;
    private boolean bookReopend;
    private boolean isBackground;
    private boolean isRestart;
    PowerManager pm;
    //    PowerManager.WakeLock wakeLock;
    private View adBottomWhiteBg;
    private AdSiteBean bannerAdSiteBean;
    private View bannerCloseTipView;

    private BannerRequestRunnable bannerTimerTask = new BannerRequestRunnable("banner_rquest");
    private BannerTipRunnable bannerCloseTipTask = new BannerTipRunnable("banner_tip");

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReadPresenter = new ReadPresenter(getApplicationContext(), this);
        setContentView(R.layout.activity_read);
        enterTime = System.currentTimeMillis();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
        EventBus.getDefault().register(this);
        Logger.e(TAG, "穿山甲信息流高度： " + csjInfoAdHeight);
        mHandler = new Handler(Looper.getMainLooper());
        findView();
        // 如果上次异常退出则检查版本更新
        if (!TextUtils.isEmpty(SPUtils.INSTANCE.getString(ReadModeUtil.TAG))) {
            UpgradeManager.getInstance(getApplicationContext()).setCheckOnHome(false);
            UpgradeManager.getInstance(getApplicationContext()).startBackgroundCheck(this,PageNameConstants.READER);
        }
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        offScreenTime = ReadSettingManager.getInstance().offScreenArray[ReadSettingManager.getInstance().getScreenOffMode()];
        //记录启动时间.
        mStartReadTime = TimeTool.currentTimeMillis();
        initView();
        if (bookReopend) {
            bookReopendFinish();
            return;
        }

        gBookId = StringFormat.parseLong(mBookId, 0);
        getTodayReadTime();
        //TODO 判断用户是否已登录, 如果已登录, 则添加活跃用户统计.
        FunctionStatsApi.activeUser();
        //进入阅读器阅读.
        FunctionStatsApi.enterRead(mBookId);
        FuncPageStatsApi.readShow(StringFormat.parseLong(mBookId, 0), prevPageId, sourceStats);

        SPUtils.INSTANCE.putString(TAG, mBookId);


        mExtraPageMgr.init(this, sourceStats);
        uploadYMEvent(true);
        SPUtils.INSTANCE.putString(ReadModeUtil.TAG, mBookId);

        int requestInterval = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.BANNER_INTERVAL, 30);
        ScheduledService.getInstance().sheduler(bannerTimerTask, 0, requestInterval, TimeUnit.SECONDS);
    }

    private void uploadYMEvent(boolean isEntry) {
        //获取用户信息.
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        Map<String, String> paramMap = new HashMap<>();
        //MID
        paramMap.put("MID", UserManager.getInstance().getMid());
        //UID
        paramMap.put("UID", userInfo != null ? userInfo.uid : "NULL");
        if (isEntry) {
            StatisHelper.onEvent(getApplicationContext(), "ENTER_READ", paramMap);
        } else {
            StatisHelper.onEvent(getApplicationContext(), "OUT_READ", paramMap);
        }
    }

    /**
     * 获取今天已经阅读的时间以及任务完成状态
     */
    private void getTodayReadTime() {
        mReadPresenter.loadTodayReadTime();
    }

    @SuppressLint("InvalidWakeLockTag")
    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            prevPageId = intent.getStringExtra(RouterPath.KEY_PARENT_ID);
            sourceStats = intent.getStringExtra(RouterPath.KEY_SOURCE);
        }

        // 信息流章节广告
        flowAdFrameLayout = LayoutInflater.from(this).inflate(R.layout.read_ad_view, null);
        adRootView = flowAdFrameLayout.findViewById(R.id.read_ad_root_layout);
        adContainer = flowAdFrameLayout.findViewById(R.id.read_container);

        videoRootView = flowAdFrameLayout.findViewById(R.id.ad_native_video);
        tvAdTip = flowAdFrameLayout.findViewById(R.id.read_tip);

        // 底部广告位
        bottomAdRootView = findViewById(R.id.read_bottom_ad_root);
        if (AdConfigManger.getInstance().showAd(ReadActivity.this, Constants.channalCodes[1]) != null) {
            bottomAdRootView.setVisibility(View.VISIBLE);
        } else {
            bottomAdRootView.setVisibility(View.GONE);
        }
        bottomAdView = findViewById(R.id.read_bottom_container);
        tvBottomAdTip = findViewById(R.id.read_bottom_ad_tip);
        adClear = findViewById(R.id.read_ad_clear);
        adClear.setOnClickListener(this);
        adBottomWhiteBg = findViewById(R.id.read_bottom_white_bg);
        bannerCloseTipView = findViewById(R.id.read_banner_close_tip);

        Uri uri = getIntent().getData();
        if (uri != null) {
            mBookId = uri.getQueryParameter(RouterPath.KEY_BOOK_ID);
            mFrom = "h5";
            mTargetSeqNum = Integer.valueOf(uri.getQueryParameter(RouterPath.TARGET_SEQ_NUM));
        } else {
            mBookId = getIntent().getStringExtra(RouterPath.KEY_BOOK_ID);
            if (gBookId != 0 && StringFormat.parseLong(mBookId, 0) == gBookId) {
                bookReopend = true;
            }
            try {
                mFrom = ((BaseData) getIntent().getParcelableExtra(BaseActivity.DATA_KEY)).getFrom();
            } catch (Throwable throwable) {
                mFrom = "";
                Logger.e(TAG, "initView: {}", throwable);
            }
            mTargetSeqNum = getIntent().getIntExtra(RouterPath.TARGET_SEQ_NUM, 0);
        }

        isFullScreen = ReadSettingManager.getInstance().isFullScreen();
        mPageLoader = mPvReadPage.getPageLoader(this, prevPageId, sourceStats,mBookId);
        mExtraPageMgr = new ExtraPageMgr();
        mPageLoader.supportPageAfterLastChapter(true);
        mPageLoader.supportPageBetweenChapters(AdConfigManger.getInstance().showAd(this, Constants.channalCodes[2]) != null);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ivBack = findViewById(R.id.toolbar_back);

        mSettingDialog = new ReadSettingDialog(this, mPageLoader, prevPageId, sourceStats);
        mSettingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                toggleMenu(false);
            }
        });
        setCatalogue();

        toggleNightMode();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, intentFilter);

        boolean mIsSystemLight = ReadSettingManager.getInstance().isBrightnessAuto();
        float brightness = mIsSystemLight ? BrightnessMgr.SYSTEM_LIGHT : ReadSettingManager.getInstance().getBrightness() / 100f;
        changeScreenBrightness(brightness);

        //隐藏StatusBar
        mPvReadPage.post(new Runnable() {
            @Override
            public void run() {
                hideSystemBar();
            }
        });

        mPageLoader.setOnPageChangeListener(new AbsPageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int curGroupPos, int pos, boolean isSlide) {
                pos = Math.max(pos, 0);
                setCategorySelect(curGroupPos, pos);
                if (curGroupPos < 0 || curGroupPos >= mBook.bookChapterList.size()) {
                    return;
                }
                List<ChapterBean> chapterBeans = mBook.bookChapterList.get(curGroupPos);
                if (chapterBeans == null || chapterBeans.size() == 0 || pos >= chapterBeans.size()) {
                    return;
                }
                mExtraPageMgr.clearClickedData();
                ChapterBean chapterBean = chapterBeans.get(pos);
                //判断书籍来源是否为掌阅.
                if (mBookFrom == 2) {
                    //掌阅书籍, 添加掌阅统计.
                    MtStHelper.INSTANCE.readBookChaper(mBook.bookId, chapterBean.chapterId + "", chapterBean.seqNum);
                }
                //判断是否通过滑动方式切换章节.
                if (isSlide) {
                    FunctionStatsApi.rChapterReading(mBookId);
                    FuncPageStatsApi.readChapterNum(StringFormat.parseLong(mBookId, 0), prevPageId, sourceStats);
                }

                if(mPageLoader.getPageStatus() != AbsPageLoader.STATUS_ERROR){
                    errorTipLayout.setVisibility(GONE);
                } else if(mPageLoader.getPageStatus() != AbsPageLoader.STATUS_LOADING){
                    dismissLoading();
                }

                chapterChangeShowError(chapterBean.seqNum, chapterBean.bookId, chapterBean.chapterId);
            }

            @Override
            public void loadChapterContents(List<TxtChapter> chapters, int curGroupPos, int pos) {
                Logger.e(TAG, "开始请求下载书籍章节内容");
                mReadPresenter.loadContent(mBookId, chapters, sourceStats, "loadChapterContents");
                prevChapters.clear();
                prevChapters.addAll(chapters);
            }

            @Override
            public void preLoadChaptersGroup(int groupPos) {
                if (PhoneUtil.isNetworkAvailable(getApplication())) {
                    mReadPresenter.preLoadChaptersGroup(mBookId, groupPos);
                } else {
                    ToastUtils.showLimited(R.string.toast_no_net);
                }
            }

            @Override
            public void onChaptersConverted(List<List<TxtChapter>> chapters, int curGroupPos, int groupPos) {
                mRvReadCategory.setIsLoad(false);
                mTxtChapters = chapters;
                if (mType == ReadPresenter.LOAD_CATALOGUE_TYPE_PRE) {
                    int normalLastSeqNum = mChaptersForCatalogue.get(mChaptersForCatalogue.size() - 1).seqNum;
                    int lastGroup = normalLastSeqNum % 50 == 0 ? normalLastSeqNum / 50 - 1 : normalLastSeqNum / 50;
                    int firstSeqNum = mChaptersForCatalogue.get(0).seqNum;
                    int firstGroup = firstSeqNum % 50 == 0 ? firstSeqNum / 50 - 1 : firstSeqNum / 50;
                    if (lastGroup > firstGroup) {
                        if (groupPos == lastGroup + 1) {
                            mType = ReadPresenter.LOAD_CATALOGUE_TYPE_BOTTOM_MORE;
                        } else if (groupPos == firstGroup - 1) {
                            mType = ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE;
                        }
                    } else {
                        if (groupPos == lastGroup - 1) {
                            mType = ReadPresenter.LOAD_CATALOGUE_TYPE_BOTTOM_MORE;
                        } else if (groupPos == firstGroup + 1) {
                            mType = ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE;
                        }
                    }
                }
                refreshCatalogue();
            }

            @Override
            public void preLoadAdFlow() {
                loadInfoFlowAd();
            }

            @Override
            public void onPageChange(final int pos, TxtPage txtPage, boolean isCur) {
                //判断是否发生切页.
                if (mCurPageNumber != pos) {
                    //记录当前页码.
                    mCurPageNumber = pos;
                    //重新开始计算阅读时长.
                    mCurPageReadingTime = 0;
                }
            }

            @Override
            public void onDrawPageAfterLastChapter(Canvas canvas, int textColor, boolean isNightMode, FrameLayout rootLayoutForExtra) {
//                mExtraPageMgr.onDrawPageAfterLastChapter(canvas, textColor, isNightMode, rootLayoutForExtra);
                if (tooFastChecker.isTooFast()/* || !mPvReadPage.isCanStart()*/) {
                    return;
                }
                Intent intent = new Intent(ReadActivity.this, ReadRecommendActivity.class);
                intent.putExtra(RouterPath.INSTANCE.KEY_BOOK_ID, StringFormat.parseLong(mBookId, 0));
                intent.putExtra("is_finished", mBook.isFinish);
                intent.putExtra(RouterPath.INSTANCE.KEY_SOURCE, sourceStats);
                startActivity(intent);
            }

            @Override
            public void onDrawPageAfterChapter(boolean isVisiable, int maxLine, int lineNumber, FrameLayout rootLayoutForExtra) {
//                mExtraPageMgr.onDrawPageAfterChapter(isVisiable, maxLine, lineNumber, adContainer);
            }

            @Override
            public void onDrawInfoFlowAd(FrameLayout rootLayoutForExtra, int lastPos, int pagePos) {
                if (flowAdFrameLayout.getParent() == null) {
                    rootLayoutForExtra.addView(flowAdFrameLayout);
                }

                FrameLayout.LayoutParams rootParams = (FrameLayout.LayoutParams) adRootView.getLayoutParams();
                rootParams.gravity = Gravity.CENTER_VERTICAL;
                adRootView.setLayoutParams(rootParams);

                LinearLayout.LayoutParams adParams = (LinearLayout.LayoutParams) adContainer.getLayoutParams();
                adParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                adContainer.setLayoutParams(adParams);
                adContainer.setTag(pagePos);
                adContainer.setScaleX(1f);
                adContainer.setScaleY(1f);
                // 判断广告是否可点击
                adContainer.setDisabled(mExtraPageMgr.disableAdClick("" + pagePos));
                mExtraPageMgr.initVideoView(videoRootView, mHandler, adContainer);
                mExtraPageMgr.setNightMode(videoRootView, isNightMode);
                if (PhoneUtil.isNetworkAvailable(ReadActivity.this)/* && mExtraPageMgr.getFlowAdLoadSucc()*/) {
                    if (mExtraPageMgr.getFlowAdLoadSucc() || mExtraPageMgr.getChapterEndLoadSucc()) {
                        adContainer.setVisibility(View.VISIBLE);
                        if (mExtraPageMgr.getFlowAdLoadSucc()) {
                            videoRootView.setVisibility(AdConfigManger.getInstance().showAd(ReadActivity.this,
                                    Constants.channalCodes[3]) != null ? View.VISIBLE : View.GONE);
                        }
                        tvAdTip.setVisibility(View.GONE);
                        Logger.e("show_flow", "信息流或章节末有一个现实成功了");
                    } else {
                        adContainer.setVisibility(View.GONE);
                        videoRootView.setVisibility(View.GONE);
                        tvAdTip.setVisibility(View.VISIBLE);
                    }
                } else {
                    adContainer.setVisibility(View.GONE);
                    videoRootView.setVisibility(View.GONE);
                    tvAdTip.setVisibility(View.VISIBLE);
                }

                Logger.e(TAG, "显示信息流广告");
                tvAdTip.setTextColor(mPageLoader.getTextContentColor());
                tvAdTip.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPageLoader.getTextContentSize());
            }

            @Override
            public void onNextPage(int pageNumber) {
                if (Constants.IS_AIGAO) {
                    //阅读30页检查一次授权.
                    if (pageNumber % 30 == 0) {
                        if (!SPUtils.INSTANCE.getBoolean(PermissionMgr.PERMISSION_REQUEST_TODAY, false)) {
                            //有效阅读, 申请权限.
                            checkPermission();
                        }
                    }
                } else {
                    //阅读10页检查一次授权.
                    if (pageNumber % 10 == 0) {
                        //有效阅读, 申请权限.
                        checkPermission();
                    }
                }
            }

            @Override
            public void showRetryView() {
                if (errorTipLayout.getVisibility() != View.VISIBLE) {
                    mPvReadPage.invalidate();
                    errorTipLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mPageLoader.getPageStatus() == AbsPageLoader.STATUS_ERROR){
                                errorTipLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 200);
                    Logger.e(TAG, "内容显示失败，显示错误界面: " + errorTipLayout.getVisibility());
                    FuncPageStatsApi.readLoadFail(Long.parseLong(mBookId),sourceStats);
                }
            }

            @Override
            public boolean hideBottomBanner() {
                adContainer.removeAllViews();
                if (bottomAdRootView.getVisibility() == View.VISIBLE) {
                    bottomAdRootView.setVisibility(View.GONE);
                    if (mExtraPageMgr.getBannerView() != null) {
                        mExtraPageMgr.getBannerView().destroy();
                    }
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void showChapterEndAd(FrameLayout rootLayoutForExtra, int pagePos, int containerHeight) {
                if (flowAdFrameLayout.getParent() == null) {
                    rootLayoutForExtra.addView(flowAdFrameLayout);
                }
                adContainer.setTag(pagePos);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.TOP;
                adRootView.setLayoutParams(params);
                adContainer.setScaleX(1f);
                adContainer.setScaleY(1f);
                LinearLayout.LayoutParams adParams = (LinearLayout.LayoutParams) adContainer.getLayoutParams();
                adParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                adContainer.setLayoutParams(adParams);
                adContainer.invalidate();
                if (mExtraPageMgr.getChapterEndView() != null && mExtraPageMgr.getChapterEndLoadSucc()) {
                    adContainer.setVisibility(View.VISIBLE);
                    mExtraPageMgr.setNightMode(videoRootView, isNightMode);
                    Logger.e(TAG, "显示章节末广告，章节末广告加载成功");
                } else if (mExtraPageMgr.getFlowAdLoadSucc()) {
                    //此处如果没有章节末尾广告的情况下, 不使用信息流广告进行填充.
                    // adContainer进行缩放
                    adContainer.setVisibility(View.VISIBLE);
                    float blankHeight = getPageEndBlankHeight();
                    Logger.e(TAG, "显示章节末广告，信息流广告加载成功, 穿山甲原来高度: " + csjInfoAdHeight + ", 剩余空间: " + blankHeight);
                    if (blankHeight < csjInfoAdHeight) {
                        float scaleY = blankHeight / csjInfoAdHeight;
                        adContainer.setScaleX(scaleY);
                        adContainer.setScaleY(scaleY);
                        adContainer.setPivotX(PhoneUtil.getScreenSize(ReadActivity.this)[0] / 2);
                        adContainer.setPivotY(0);
                        adContainer.invalidate();
                        Logger.e(TAG, "显示章节末广告，信息流过大，进行缩放比： " + scaleY);
                    }
                } else {
                    adContainer.setVisibility(View.GONE);
                    Logger.e(TAG, "章节末还没展示， view = " + mExtraPageMgr.getChapterEndView()
                            + ", 拉取成功: " + mExtraPageMgr.getChapterEndLoadSucc());
                }
                videoRootView.setVisibility(View.GONE);
                tvAdTip.setVisibility(View.GONE);
            }

            @Override
            public void preloadChapterEndAd() {
                AdSiteBean adSiteBean = AdConfigManger.getInstance().showAd(ReadActivity.this, Constants.channalCodes[11]);
                if (adSiteBean != null && PhoneUtil.isNetworkAvailable(ReadActivity.this)) {
                    setChapterEndLoaded(true);
                    mExtraPageMgr.loadChapterEndAd(mHandler, adContainer, videoRootView, tvAdTip, adSiteBean);
                } else {
                    setChapterEndLoaded(false);
                }
            }
        });

        mPvReadPage.setTouchListener(new PageView.TouchListener() {
            @Override
            public void center() {
                acquireLock();
                toggleMenu(true);
            }

            @Override
            public boolean onTouch() {
                acquireLock();
                mDrawerLayout.closeDrawer(Gravity.START);
                return !hideReadMenu();
            }

            @Override
            public boolean prePage() {
                return true;
            }

            @Override
            public boolean nextPage() {
                return true;
            }

            @Override
            public void cancel() {
            }

            @Override
            public void showRuleDialog() {
                int freeDuration = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.FLOW_FREE_DURATION, 15);
                mExtraPageMgr.showRuleDialog(freeDuration);
            }

            @Override
            public void showVideoDialog() {
                int freeDuration = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.FLOW_FREE_DURATION, 15);
                mExtraPageMgr.showRewardVideo(mHandler, adContainer, freeDuration, Constants.channalCodes[3],
                        FunPageStatsConstants.READ_WORD_AD, "1", sourceStats);
            }
        });

        initData();
//        setOrderTv();
    }

    public float getPageEndBlankHeight() {
        return mPageLoader.getLastPageBlankHeight();
    }

    public String getCurrPageId() {
        return PageNameConstants.READER;
    }

    private void checkPermission() {
        if (PermissionMgr.requestPermissions(ReadActivity.this)) {
            //权限申请完成,检查通知权限
            NotificationsUtils.isNotifyEnable(ReadActivity.this, getSupportFragmentManager(), PageNameConstants.READER);
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

    public void setChapterEndLoaded(boolean success) {
        mPageLoader.chapterEndAdLoaded(success);
    }

    /**
     * 提前预加载信息流广告
     */
    private void loadInfoFlowAd() {
        if (mHandler != null) {
            mHandler.post(adRunnable);
        }
    }

    private Runnable adRunnable = new Runnable() {
        @Override
        public void run() {
            flowAdSiteBean = AdConfigManger.getInstance().showAd(ReadActivity.this, Constants.channalCodes[2]);
            if (flowAdSiteBean != null) {
                mPageLoader.supportPageBetweenChapters(true);
                LinearLayout.LayoutParams adParams = (LinearLayout.LayoutParams) adContainer.getLayoutParams();
                adParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                adContainer.setLayoutParams(adParams);
                mExtraPageMgr.showInfoFlowAd(ReadActivity.this, mHandler, adContainer, tvAdTip, videoRootView,
                        Constants.channalCodes[2], flowAdSiteBean);
                mPageLoader.hasAdPreload();
            } else {
                mPageLoader.supportPageBetweenChapters(false);
                mPageLoader.clearInfoFlowData();
                Logger.e("ad#http", "没有找到广告源，不显示广告，调用clearInfoFlowData()");
            }
        }
    };

    /**
     * 检查是否需要疲劳弹框
     */
    private void checkReadTiredTime(boolean needShowDialog) {
        //判断是否在免广告周期内.
        BookRecordGatherResp bookRecordGatherResp = SharePreferenceUtils.getObject(BaseApplication.context.globalContext, SharePreferenceUtils.READ_HISTORY_CACHE);
        if (bookRecordGatherResp != null && bookRecordGatherResp.getLastSec() > 0)
        {
            Logger.e(TAG, "免广告特权使用中，不触发疲劳弹框, 特权免广告剩余时间:{}", bookRecordGatherResp.getLastSec());
            return;
        }
        // 是否需要弹窗
        long tiredTotalTime = SPUtils.INSTANCE.getLong(SPUtils.INSTANCE.getREAD_TIME_TOTAL(), 0);
        if (needShowDialog && tiredTotalTime >= showTiredDialogTime) {
            Logger.e(TAG, "需要弹疲劳提示窗");
            if (dialogFragment == null) {
                dialogFragment = new ReadSleepFragment();
                dialogFragment.setOnExitAppListener(new ReadSleepFragment.OnExitAppListener() {
                    @Override
                    public void onLeft() {
                        dismissDialogFragment();
                        FuncPageStatsApi.tiredDialogClick(prevPageId, "2");
                    }

                    @Override
                    public void onRight() {
                        dismissDialogFragment();
                        if (tiredAdSiteBean != null && PhoneUtil.isNetworkAvailable(ReadActivity.this)) {
                            mExtraPageMgr.showRewardVideo(mHandler, null, tiredFreeTime, tiredAdSiteBean.getChannelCode(),
                                    FunPageStatsConstants.TIRED_POP, "7", sourceStats);
                            FuncPageStatsApi.tiredDialogClick(prevPageId, "1");
                        } else {
                            FuncPageStatsApi.tiredDialogClick(prevPageId, "2");
                        }
                    }

                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
//                        tiredTotalTime = 0;
//                        rstRdTime = System.currentTimeMillis();
                        isTiredDialogShow = false;
                        SPUtils.INSTANCE.putLong(SPUtils.INSTANCE.getREAD_TIME_TOTAL(), 0);
                        if (mHandler != null) {
                            mHandler.removeCallbacks(closeTiredDialog);
                        }
                    }
                });
            }
            // 是否已经显示对话框
            if (dialogFragment.isVisible() || isTiredDialogShow) {
                Logger.e(TAG, "疲劳弹框正在显示");
                return;
            }
            isTiredDialogShow = true;
            dialogFragment.showNow(getSupportFragmentManager(), "tired_dialog");
            FuncPageStatsApi.showTiredDialog(prevPageId);
            tiredAdSiteBean = AdConfigManger.getInstance().showAd(this, Constants.channalCodes[9]);
            if (tiredAdSiteBean == null && mHandler != null) {
                mHandler.postDelayed(closeTiredDialog, 3000);
            }
            dialogFragment.updateNightMode(isNightMode);
        }
    }

    private Runnable closeTiredDialog = new Runnable() {
        @Override
        public void run() {
            dismissDialogFragment();
            isTiredDialogShow = false;
        }
    };

    /**
     * 关闭DialogFragment
     */
    private void dismissDialogFragment()
    {
        if (dialogFragment != null)
        {
            try {
                dialogFragment.dismiss();
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "dismissDialogFragment Error:" + throwable);
            }
        }
    }

    private void setOrderTv() {
        mOrderTv.setText(ViewUtils.getString(mIsNormalOrderType ? R.string.order_normal : R.string.order_reverse));

        mOrderTv.setTextColor(isNightMode ? ViewUtils.getColor(R.color.color_a4a3a8) : ViewUtils.getColor(R.color.color_1b1b1b));
        Drawable drawable;
        if (mIsNormalOrderType) {
            drawable = ViewUtils.getDrawable(isNightMode ? R.mipmap.icon_read_botton_list_along_night : R.mipmap.icon_read_botton_list_along);
        } else {
            drawable = ViewUtils.getDrawable(isNightMode ? R.mipmap.icon_read_botton_list_inverted_night : R.mipmap.icon_read_botton_list_inverted);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mOrderTv.setCompoundDrawables(drawable, null, null, null);
    }

    private void findView() {
        mReadTopMenu = findViewById(R.id.toolbar_layout);
        mReadTitleTv = findViewById(R.id.toolbar_title);
        mPvReadPage = findViewById(R.id.pv_read_page);
        errorTipLayout = findViewById(R.id.read_error_layout);
        readLoadingLayout = findViewById(R.id.read_loading_layout);
        btnErrorRetry = findViewById(R.id.read_btn_retry);
        btnErrorRetry.setOnClickListener(this);
        btnErrorBack = findViewById(R.id.read_btn_back);
        btnErrorBack.setOnClickListener(this);
        mReadTvCategory = findViewById(R.id.read_tv_category);
        mReadTvCategory.setOnClickListener(this);
        mReadTvNightMode = findViewById(R.id.read_tv_night_mode);
        mReadTvNightMode.setOnClickListener(this);
        mReadTvSetting = findViewById(R.id.read_tv_setting);
        mReadTvSetting.setOnClickListener(this);
        mReadBottomMenuRoot = findViewById(R.id.read_ll_bottom_menu_root);
        mReadBottomMenu = findViewById(R.id.read_ll_bottom_menu);
        mRvReadCategory = findViewById(R.id.rv_read_category);
        mDrawerLayout = findViewById(R.id.read_dl_slide);
//        mCatalogueBtn = findViewById(R.id.catalogue_btn);
//        mCatalogueBtn.setOnClickListener(this);
        mOrderTv = findViewById(R.id.order_type);
        mOrderTv.setOnClickListener(this);
        mTiTleLayout = findViewById(R.id.title_layout);
        mCatalogue = findViewById(R.id.catalogue);
        mGuideView = findViewById(R.id.guide_view);
        mGuideBtn = findViewById(R.id.guide_btn);
        mGuideBtn.setOnClickListener(this);

        mBookName = findViewById(R.id.tv_book_name);
        ivMoreMenu = findViewSetOnClick(R.id.toolbar_right_img);
        ivMoreMenu.setVisibility(View.VISIBLE);
        ivMoreMenu.setImageResource(R.mipmap.more);

        ivDownload = findViewById(R.id.toolbar_right_img_2);
        ivDownload.setVisibility(GONE);
        ivDownload.setImageResource(R.mipmap.icon_download);

        layoutDownloadBottom = findViewById(R.id.layout_download_bottom);
        tvDownloadBottom = findViewById(R.id.tv_download_bottom);
        ivDownloadClose = findViewById(R.id.iv_download_close);
        ivDownloadClose.setOnClickListener(this);
    }

    @SuppressLint("CheckResult")
    private void initData() {
        if (!bookReopend) {
            mReadPresenter.loadRecordedChaptersGroup(mBookId, mTargetSeqNum, sourceStats);
        }
        showTiredDialogTime = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.RD_TIRED_TIME, 10000)
                * TimeUtils.MINUTE_1;
        tiredFreeTime = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.RD_TIRED_FREE_TIME, 15);
        Logger.e(TAG, "初始化--疲劳免广告时长： " + tiredFreeTime);
    }

    private void checkBannerTipStatus() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    int hour = new Date().getHours();
                    if (bottomAdRootView.getVisibility() == View.VISIBLE && (hour >= 23 || hour < 6)) {
                        bannerCloseTipView.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bannerCloseTipView.setVisibility(View.GONE);
                            }
                        }, 10_000);
                        AdHttpUtil.showBannerTip(AdConfigManger.getInstance().showAd(ReadActivity.this, Constants.channalCodes[4]));
                    } else {
                        bannerCloseTipView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void setCatalogue() {
        if (mReadCategoryAdapter == null) {
            mReadCategoryAdapter = new AdapterBuilder().putItemClass(ReadChapterListHolder.class, getItemListener()).builderListAdapter(this);
            mRvReadCategory.setAdapter(mReadCategoryAdapter);
        }
        mRvReadCategory.setOnRefreshingListener(new TwoDirectionPullListView.OnRefreshingListener() {
            @Override
            public void onLoadMoreBottom() {
                int normalLastSeqNum = mChaptersForCatalogue.get(mChaptersForCatalogue.size() - 1).seqNum;
                int group = normalLastSeqNum % 50 == 0 ? normalLastSeqNum / 50 - 1 : normalLastSeqNum / 50;
                int nextGroup = mIsNormalOrderType ? group + 1 : group - 1;
                if (nextGroup >= 0 && nextGroup < mTxtChapters.size() && mTxtChapters.get(nextGroup).size() != 0) {
                    mType = ReadPresenter.LOAD_CATALOGUE_TYPE_BOTTOM_MORE;
                    mRvReadCategory.onLoadMoreBottomComplete();
                    refreshCatalogue();
                } else {
                    if (PhoneUtil.isNetworkAvailable(getApplication())) {
                        mReadPresenter.loadCatalogue(mBook.bookId, mBook.chapterCount, nextGroup, ReadPresenter.LOAD_CATALOGUE_TYPE_BOTTOM_MORE);
                    } else {
                        ToastUtils.showLimited(R.string.toast_no_net);
                        mRvReadCategory.onLoadMoreBottomComplete();
                    }
                }
            }

            @Override
            public void onLoadMoreTop() {
                int firstSeqNum = mChaptersForCatalogue.get(0).seqNum;
                int group = firstSeqNum % 50 == 0 ? firstSeqNum / 50 - 1 : firstSeqNum / 50;
                int preGroup = mIsNormalOrderType ? group - 1 : group + 1;
                if (preGroup < 0) {
                    preGroup = 0;
                }
                if (mTxtChapters == null || mTxtChapters.size() == 0) {
                    mRvReadCategory.onLoadMoreTopComplete();
                    return;
                }
                if (preGroup >= mTxtChapters.size()) {
                    preGroup = mTxtChapters.size() - 1;
                }
                if (mTxtChapters.get(preGroup).size() != 0) {
                    mType = ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE;
                    mRvReadCategory.onLoadMoreTopComplete();
                    refreshCatalogue();
                } else {
                    if (PhoneUtil.isNetworkAvailable(getApplication())) {
                        mReadPresenter.loadCatalogue(mBook.bookId, mBook.chapterCount, preGroup, ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE);
                    } else {
                        ToastUtils.showLimited(R.string.toast_no_net);
                        mRvReadCategory.onLoadMoreTopComplete();
                    }
                }
            }

            @Override
            public boolean hasTopMore() {
                if (mChaptersForCatalogue.size() == 0) {
                    return false;
                }
                return mIsNormalOrderType ? mChaptersForCatalogue.get(0).seqNum != 1 : mChaptersForCatalogue.get(0).seqNum != mBook.chapterCount;
            }

            @Override
            public boolean hasBottomMore() {
                if (mChaptersForCatalogue.size() == 0) {
                    return false;
                }
                return mIsNormalOrderType ? mChaptersForCatalogue.get(mChaptersForCatalogue.size() - 1).seqNum != mBook.chapterCount : mChaptersForCatalogue.get(mChaptersForCatalogue.size() - 1).seqNum != 1;
            }

            @Override
            public void noBottomMore() {
                ToastUtils.showLimited(mIsNormalOrderType ? R.string.already_last_chapter : R.string.already_first_chapter);
            }

            @Override
            public void noTopMore() {
                if (!mIsNormalOrderType) {
                    ToastUtils.showLimited(R.string.already_last_chapter);
                }
            }
        });
    }

    @NonNull
    private ItemListenerAdapter<ReadChapterListHolder> getItemListener() {
        return new ItemListenerAdapter<ReadChapterListHolder>() {
            @Override
            public void onClick(ReadChapterListHolder readBgHolder, View v) {
                mDrawerLayout.closeDrawer(Gravity.START);
                TxtChapter chapter = readBgHolder.getMItemData();
                if(chapter != null && !chapter.isDownload){
                    if(!PhoneUtil.isNetworkAvailable(ReadActivity.this)){
                        ToastUtils.show("加载失败，请检查网络后重试!");
                        return;
                    }
                }
                int seqNum = readBgHolder.getMItemData().seqNum;
                int groupIndex = seqNum % 50 == 0 ? seqNum / 50 - 1 : seqNum / 50;
                int selectPos = seqNum % 50 == 0 ? 50 - 1 : seqNum % 50 - 1;
                Logger.e(TAG, "目录点击的位置：第" + groupIndex + "组， 第" + seqNum + "章， 第" + selectPos + "页");
                mPageLoader.skipToChapter(groupIndex, selectPos);
                StatisHelper.onEvent().bookReading(mBook.bookName, "左侧目录");
                //点击目录列表章节切换.
                FunctionStatsApi.rCatalogChapterClick(mBookId);
                FuncPageStatsApi.readCatalogueClick(StringFormat.parseLong(mBookId, 0), prevPageId, sourceStats);

                chapterChangeShowError(readBgHolder.getMItemData().seqNum, readBgHolder.getMItemData().getBookId(), readBgHolder.getMItemData().chapterId);
            }

            @Override
            public void onSetDate(ReadChapterListHolder itemView) {
                super.onSetDate(itemView);
                TxtChapter chapter = itemView.getMItemData();
                if (isNightMode) {
                    if (chapter.isSelect()) {
                        itemView.setChapterTitleColor(ViewUtils.getColor(R.color.standard_red_main_color_c1));
//                    } else if (chapter.isRead()) {
//                        itemView.setChapterTitleColor(ViewUtils.getColor(R.color.color_6E6D70));
                    } else {
                        itemView.setChapterTitleColor(ViewUtils.getColor(R.color.color_a4a3a8));
                    }
                    itemView.setDividerColor(ViewUtils.getColor(R.color.color_323133));
                    itemView.setChapterTitleBackgruond(ViewUtils.getColor(R.color.color_202020));
                    itemView.setDownloadTextColor(ViewUtils.getColor(R.color.color_6E6D70));
                    itemView.setDownloadBackgruond(ViewUtils.getColor(R.color.color_202020));
                } else {
                    if (chapter.isSelect()) {
                        itemView.setChapterTitleColor(ViewUtils.getColor(R.color.standard_red_main_color_c1));
//                    } else if (chapter.isRead()) {
//                        itemView.setChapterTitleColor(ViewUtils.getColor(R.color.color_b2b2b2));
                    } else {
                        itemView.setChapterTitleColor(ViewUtils.getColor(R.color.color_666666));
                    }
                    itemView.setDividerColor(ViewUtils.getColor(R.color.color_f5f5f5));
                    itemView.setChapterTitleBackgruond(ViewUtils.getColor(R.color.white));
                    itemView.setDownloadTextColor(ViewUtils.getColor(R.color.color_b2b2b2));
                    itemView.setDownloadBackgruond(ViewUtils.getColor(R.color.white));
                }

                if (chapter.isDownload) {
                    itemView.setDownloadTextVisible(true);
                } else {
                    itemView.setDownloadTextVisible(false);
                }

            }
        };
    }

    private void setCategorySelect(int currentGroup, int selectPos) {
        if (mTxtChapters == null || mTxtChapters.size() == 0 || mBook.bookChapterList == null || mBook.bookChapterList.isEmpty()) {
            return;
        }
        if (lastReadedChapterGroup != -1 && lastReadedChapterPos != -1) {
            if (mTxtChapters.size() > lastReadedChapterGroup && mTxtChapters.get(lastReadedChapterGroup) != null
                    && mTxtChapters.get(lastReadedChapterGroup).size() > lastReadedChapterPos) {
                mTxtChapters.get(lastReadedChapterGroup).get(lastReadedChapterPos).isRead = true;
                ChapterBean chapterBean = mBook.bookChapterList.get(lastReadedChapterGroup).get(lastReadedChapterPos);
                chapterBean.isRead = true;
                BookChapterHelper.getsInstance().updateBookChaptersAsync(chapterBean);
            }
        }
        if (mChaptersForCatalogue == null) {
            mChaptersForCatalogue = new ArrayList<>();
        }
        refreshCatalogueSelect(currentGroup, selectPos);
        lastReadedChapterGroup = currentGroup;
        lastReadedChapterPos = selectPos;
    }

    private void refreshCatalogueSelect(int currentGroup, int selectPos) {
        if (mTxtChapters == null || mTxtChapters.size() == 0) {
            return;
        }
        mChaptersForCatalogue.clear();
        // 判断是否已经选择过
        for (int i = 0; i < mTxtChapters.size(); i++) {
            List<TxtChapter> list = mTxtChapters.get(i);
            for (int j = 0; j < list.size(); j++) {
                TxtChapter chapter = list.get(j);
                chapter.setSelect(i == currentGroup && j == selectPos);
            }
        }
        ArrayList<TxtChapter> temp = new ArrayList<>();
        // 获取当前组前面正序需要显示的目录
        for (int i = currentGroup - 1; i > -1; i--) {
            List<TxtChapter> list = mTxtChapters.get(i);
            if (list.size() == 0) {
                break;
            }
            temp.addAll(list);
            temp.addAll(mChaptersForCatalogue);
            mChaptersForCatalogue.clear();
            mChaptersForCatalogue.addAll(temp);
            temp.clear();
        }

        int select = 0;
        // 添加当前组的目录,并判断应该选中哪个章节目录
        List<TxtChapter> curTxtChapter = mTxtChapters.get(currentGroup);
        for (int j = 0; j < curTxtChapter.size(); j++) {
            TxtChapter chapter = curTxtChapter.get(j);
            mChaptersForCatalogue.add(chapter);
            if (chapter.isSelect()) {
                select = mChaptersForCatalogue.size() - 1;
            }
        }
        // 添加当前组后面的章节目录
        for (int i = currentGroup + 1; i < mTxtChapters.size(); i++) {
            List<TxtChapter> list = mTxtChapters.get(i);
            if (list.size() == 0) {
                break;
            }
            mChaptersForCatalogue.addAll(list);
        }

        // 倒序
        if (!mIsNormalOrderType) {
            Collections.reverse(mChaptersForCatalogue);
            select = mChaptersForCatalogue.size() - 1 - select;
        }
        mReadCategoryAdapter.setData(mChaptersForCatalogue);
        if (select >= 6) {
            select = select - 6;
        }
        mRvReadCategory.setSelection(select);
    }

    private void refreshCatalogue() {
        if (mChaptersForCatalogue == null) {
            mChaptersForCatalogue = new ArrayList<>();
        }
        if (mType == ReadPresenter.LOAD_CATALOGUE_TYPE_POSITIVE) {
            mChaptersForCatalogue.clear();
            for (int i = 0; i < mTxtChapters.size(); i++) {
                List<TxtChapter> list = mTxtChapters.get(i);
                if (list.size() != 0) {
                    mChaptersForCatalogue.addAll(list);
                } else {
                    break;
                }
            }
        } else if (mType == ReadPresenter.LOAD_CATALOGUE_TYPE_REVERSE) {
            mChaptersForCatalogue.clear();
            ArrayList<TxtChapter> temp = new ArrayList<>();
            for (int i = mTxtChapters.size() - 1; i > -1; i--) {
                List<TxtChapter> list = mTxtChapters.get(i);
                if (list.size() != 0) {
                    temp.addAll(list);
                    temp.addAll(mChaptersForCatalogue);
                    mChaptersForCatalogue.clear();
                    mChaptersForCatalogue.addAll(temp);
                    temp.clear();
                } else {
                    break;
                }
            }
            Collections.reverse(mChaptersForCatalogue);
        } else if (mType == ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE) {
            int firstSeqNum = mChaptersForCatalogue.get(0).seqNum;
            int curGroup = firstSeqNum % 50 == 0 ? firstSeqNum / 50 - 1 : firstSeqNum / 50;
            int preGroup = mIsNormalOrderType ? curGroup - 1 : curGroup + 1;
            if (preGroup >= mTxtChapters.size()) {
                preGroup = mTxtChapters.size() - 1;
            }
            if (preGroup < 0) {
                preGroup = 0;
            }
            List<TxtChapter> chapters = mTxtChapters.get(preGroup);
            ArrayList<TxtChapter> temps = new ArrayList<>(chapters);
            if (!mIsNormalOrderType) {
                Collections.reverse(temps);
            }
            temps.addAll(mChaptersForCatalogue);
            mChaptersForCatalogue.clear();
            mChaptersForCatalogue.addAll(temps);
        } else if (mType == ReadPresenter.LOAD_CATALOGUE_TYPE_BOTTOM_MORE) {
            int lastSeqNum = mChaptersForCatalogue.get(mChaptersForCatalogue.size() - 1).seqNum;
            int curGroup = lastSeqNum % 50 == 0 ? lastSeqNum / 50 - 1 : lastSeqNum / 50;
            int nextGroup = mIsNormalOrderType ? curGroup + 1 : curGroup - 1;
            if (nextGroup >= mTxtChapters.size()) {
                nextGroup = mTxtChapters.size() - 1;
            }
            if (nextGroup < 0) {
                nextGroup = 0;
            }
            List<TxtChapter> chapters = mTxtChapters.get(nextGroup);
            ArrayList<TxtChapter> temps = new ArrayList<>(chapters);
            if (!mIsNormalOrderType) {
                Collections.reverse(temps);
            }
            mChaptersForCatalogue.addAll(temps);
        }
        mReadCategoryAdapter.setData(mChaptersForCatalogue);
        if (mType == ReadPresenter.LOAD_CATALOGUE_TYPE_POSITIVE || mType == ReadPresenter.LOAD_CATALOGUE_TYPE_REVERSE) {
            mRvReadCategory.setSelection(0);
        }
    }

    private void setBottomAdBg() {
        int index = ReadSettingManager.getInstance().getReadBgTheme();
        if (isNightMode) {
            bottomAdRootView.setBackgroundColor(ReadSettingManager.getInstance()
                    .bottomAdBgColor[ReadSettingManager.getInstance().bottomAdBgColor.length - 1]);
            tvBottomAdTip.setTextColor(ReadSettingManager.getInstance().bottomAdTxtColor[ReadSettingManager.getInstance().bottomAdTxtColor.length - 1]);
        } else {
            bottomAdRootView.setBackgroundColor(ReadSettingManager.getInstance().bottomAdBgColor[index]);
            tvBottomAdTip.setTextColor(ReadSettingManager.getInstance().bottomAdTxtColor[index]);
        }
    }

    private void toggleNightMode() {
        if (isNightMode) {
            ivBack.setImageResource(R.mipmap.icon_back_gray);
            nightMode();
        } else {
            ivBack.setImageResource(R.mipmap.icon_back);
            dayMode();
        }
        setBottomAdBg();
    }

    private void dayMode() {
        btnErrorBack.setBackgroundResource(R.drawable.shape_btn_read_setting_select);
        btnErrorBack.setTextColor(getResources().getColor(R.color.standard_red_main_color_c1));
        btnErrorRetry.setBackgroundResource(R.drawable.extra_page_retry);
        btnErrorRetry.setTextColor(getResources().getColor(R.color.white));

        mCatalogue.setTextColor(ViewUtils.getColor(R.color.color_b2b2b2));
        mBookName.setTextColor(ViewUtils.getColor(R.color.color_1b1b1b));
        setOrderTv();
        mRvReadCategory.setBackgroundColor(ViewUtils.getColor(R.color.standard_black_fourth_level_color_c6));
        mReadCategoryAdapter.notifyDataSetChanged();
        mTiTleLayout.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_day));
        mReadTopMenu.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_day));
        mReadTitleTv.setTextColor(ViewUtils.getColor(R.color.standard_black_second_level_color_c4));
        mReadBottomMenu.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_day));
        mReadTvNightMode.setText(StringUtils.getString(R.string.night));
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.icon_read_botton_night_g);
        mReadTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        ViewUtils.setAndroidMWindowsBarTextDark(this);
        mExtraPageMgr.setNightMode(videoRootView, false);
        //设置NavigationBar背景色为白色.
        SystemUtil.setNavigationBarColor(this, R.color.white);
    }

    private void nightMode() {
        btnErrorBack.setBackgroundResource(R.drawable.shape_btn_read_setting_select_night);
        btnErrorBack.setTextColor(getResources().getColor(R.color.standard_red_main_light));
        btnErrorRetry.setBackgroundResource(R.drawable.extra_page_retry_night);
        btnErrorRetry.setTextColor(getResources().getColor(R.color.color_D8D8D8));

        mCatalogue.setTextColor(ViewUtils.getColor(R.color.color_a4a3a8));
        mBookName.setTextColor(ViewUtils.getColor(R.color.color_a4a3a8));
        setOrderTv();
        mRvReadCategory.setBackgroundColor(ViewUtils.getColor(R.color.black));
        mReadCategoryAdapter.notifyDataSetChanged();
        mTiTleLayout.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_night));
        mReadTopMenu.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_night));
        mReadTitleTv.setTextColor(ViewUtils.getColor(R.color.standard_black_second_level_color_c4));
        mReadBottomMenu.setBackgroundColor(ViewUtils.getColor(R.color.read_menu_bg_night));
        mReadTvNightMode.setText(StringUtils.getString(R.string.morning));
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.icon_read_botton_day_g);
        mReadTvNightMode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        ViewUtils.setAndroidMWindowsBarTextWhite(this);
        mExtraPageMgr.setNightMode(videoRootView, true);
        //设置NavigationBar背景色为黑色.
        SystemUtil.setNavigationBarColor(this, R.color.black);
    }

    public void showSystemBar() {
        if (mHandler == null) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!FringeUtils.hasNotchScreen(ReadActivity.this)) {
                    StatusBarUtils.showUnStableStatusBar(ReadActivity.this);
                }
                if (isFullScreen) {
                    StatusBarUtils.showUnStableNavBar(ReadActivity.this);
                }
            }
        }, 50);
    }

    public void hideSystemBar() {
        if (mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                StatusBarUtils.hideStableStatusBar(ReadActivity.this);
                if (isFullScreen) {
                    StatusBarUtils.hideStableNavBar(ReadActivity.this);
                }
            }
        });
    }

    private boolean hideReadMenu() {
        hideSystemBar();
        if (mReadTopMenu.getVisibility() == VISIBLE) {
            toggleMenu(false);
            return true;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }

    public void toggleMenu(boolean showStatusBar) {
        initMenuAnim();

        if (mReadTopMenu.getVisibility() == View.VISIBLE) {
            mReadTopMenu.startAnimation(mTopOutAnim);
            mBottomOutAnim.start();
            mReadTopMenu.setVisibility(GONE);
            hideSystemBar();
        } else {
            mReadTopMenu.setVisibility(View.VISIBLE);
            mReadTopMenu.startAnimation(mTopInAnim);
            mBottomInAnim.start();
            if (showStatusBar) {
                showSystemBar();
            } else {
                hideSystemBar();
            }
        }
    }

    private void initMenuAnim() {
        if (mTopInAnim != null) {
            mTopInAnim.cancel();
            mTopOutAnim.cancel();
            mBottomInAnim.cancel();
            mBottomOutAnim.cancel();
            return;
        }

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mReadBottomMenuRoot, "translationY", ViewUtils.dp2px(56), 0);
        animator1.setDuration(400);
        mBottomInAnim = animator1;
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mReadBottomMenuRoot, "translationY", 0, ViewUtils.dp2px(56));
        animator2.setDuration(400);
        mBottomOutAnim = animator2;
        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }

    public boolean isCurrPageAd() {
        if (mPageLoader != null) {
            return mPageLoader.isCurrPageAd();
        } else {
            return false;
        }
    }

    public boolean isCurChapterEndAd() {
        if (mPageLoader != null) {
            return mPageLoader.isCurrChapterEndAd();
        } else {
            return false;
        }
    }

    public void toDayMode() {
        isNightMode = false;
        mPageLoader.setNightMode(false);
        toggleNightMode();
        setBottomAdBg();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAdConfig(AdConfigEvent eventMessage) {
        Logger.d("ad#http", "接收到更新广告配置消息");
        // 跨天更新数据
        if (eventMessage.isDateUpdate()) {
//            mPageLoader.clearPreloadAd();
        }
        int freeTime = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.RD_TIRED_FREE_TIME, 0);
        if (freeTime > 0) {
            tiredFreeTime = freeTime;
            Logger.e(TAG, "更新广告配置--疲劳免广告时长： " + tiredFreeTime);
        }

        mExtraPageMgr.setClearBannerCacheNum(SPUtils.INSTANCE.getInt(AdConstants.ReadParams.RD_BANNER_CLEAR, 4));
//        loadInfoFlowAd();
    }

    @Override
    public void onClick(@NonNull View v) {
        if (tooFastChecker.isTooFast(400)) {
            return;
        }
        int id = v.getId();
        if (id == R.id.read_tv_category) {
            if (mTxtChapters != null && mTxtChapters.size() != 0) {
                setCategorySelect(mPageLoader.getCurrGroupPos(), mPageLoader.getCurrChapterPos());
            }
            toggleMenu(false);
            mDrawerLayout.openDrawer(Gravity.START);
            StatisHelper.onEvent().catalogClick(mBook == null ? "" : mBook.bookName);
            //目录点击.
            FunctionStatsApi.rCatalogClick(mBook == null ? "" : mBook.bookId);
            FuncPageStatsApi.readCatalogueMenu(StringFormat.parseLong(mBookId, 0), prevPageId, sourceStats);
        } else if (id == R.id.read_tv_night_mode) {
            isNightMode = !isNightMode;
            mPageLoader.setNightMode(isNightMode);
            toggleNightMode();
            if (isNightMode) {
                //夜间.
                FunctionStatsApi.rNightModeClick(mBook == null ? "" : mBook.bookId);
                FuncPageStatsApi.readNightMode(prevPageId, sourceStats);
            } else {
                //日间.
                FunctionStatsApi.rDayModeClick(mBook == null ? "" : mBook.bookId);
                FuncPageStatsApi.readDayMode(prevPageId, sourceStats);
            }
            setBottomAdBg();
        } else if (id == R.id.read_tv_setting) {
//            toggleMenu(true);
            mSettingDialog.show();
            StatisHelper.onEvent().readSetClick(mBook == null ? "" : mBook.bookName);
            //设置
            FunctionStatsApi.rMenuClick(mBook == null ? "" : mBook.bookId);
            FuncPageStatsApi.readSettingClick(prevPageId, sourceStats);
        } else if (id == R.id.order_type) {
            changeOrder();
        } /* else if (id == R.id.catalogue_btn) {
            mDrawerLayout.closeDrawer(Gravity.START);
            setCategorySelect(mPageLoader.getCurrGroupPos(), mPageLoader.getCurrChapterPos());
        }*/ else if (id == R.id.toolbar_back) {
            finish();
        } else if (id == R.id.guide_btn) {
            mGuideView.setVisibility(GONE);
        } else if (id == R.id.toolbar_right_img) {
            FuncPageStatsApi.readMoreMenu(sourceStats == null ? "" : sourceStats);
            showMoreMenuDialog();
        } else if (id == R.id.toolbar_right_img_2) {
            if (isDownloading) {
                ToastUtils.show("本书正在下载中...");
            } else if (UserManager.getInstance().getUserInfo() != null &&
                    UserManager.getInstance().getUserInfo().type != 1) {
                //已登录
                ivDownload.setClickable(false);
                mReadPresenter.getDownloadOption(Long.valueOf(mBookId));
//                gotoBookDownloadActivity();
            } else {
                //未登录
                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivityForResult(intent2, REQUEST_CODE_DOWNLOAD);
            }
        } else if (id == R.id.dialog_read_more_book) {
            windowPopup.dismiss();
            FuncPageStatsApi.readMoreDetal(1, sourceStats == null ? "" : sourceStats);
            if (mBook == null) {
                ToastUtils.showLimited("书籍信息获取失败，请稍后重试");
                return;
            }
            ActivityHelper.INSTANCE.gotoBookDetails(ReadActivity.this, mBookId, new BaseData("阅读器"),
                    PageNameConstants.READER, 20, sourceStats == null ? "" : sourceStats);
        } else if (id == R.id.dialog_read_more_share) {
            windowPopup.dismiss();
            FuncPageStatsApi.readMoreDetal(2, sourceStats == null ? "" : sourceStats);
            if (mBook == null) {
                ToastUtils.showLimited("书籍信息获取失败，请稍后重试");
                return;
            }
            String title = "《" + mBook.getBookName() + "》这本小说很不错，推荐你读。";
            String content = (StringFormat.isEmpty(mBook.getResume()) ? "" : (mBook.getResume().length() > 50
                    ? mBook.getResume().substring(0, 50) + "..."
                    : mBook.getResume()));
            String cover = mBook.getBookCover();
            String url = Constants.DOMAIN_SHARE_H5 + "/book/" + mBook.getBookId();
            CustomShareManger.getInstance().shareBookWithText(
                    getActivity(), title, content,
                    R.mipmap.share_big_img, cover, url, new BottomShareDialog.ShareClickListener() {
                        @Override
                        public void onClick(int type) {
                            FuncPageStatsApi.shareClick(StringFormat.parseLong(mBookId, 0), PageNameConstants.READER, type, sourceStats);
                        }
                    }, new BottomShareDialog.ShareResultListener() {
                        @Override
                        public void onShare(int shareResult) {
                            if (shareResult != 1) {
                                return;
                            }
                            TaskMgr.show(ReadActivity.this, getSupportFragmentManager(), getResources().getString(R.string.finish_share_task), TaskMgr.SHARE_TASK);
                        }
                    }
            );
        } else if (id == R.id.dialog_read_more_error) {
            windowPopup.dismiss();
            FuncPageStatsApi.readMoreDetal(3, sourceStats == null ? "" : sourceStats);
            if (mBook == null || mPageLoader.getCurrChapter() == null) {
                ToastUtils.showLimited("书籍信息获取失败，请稍后重试");
                return;
            }
            TxtChapter currChapter = mPageLoader.getCurrChapter();
            SimpleDialog.Builder builder = new SimpleDialog.Builder(this);
            builder.setTitle("报告错误");
            builder.setMessage("《" + mBook.getBookName() + "》 " + currChapter.getTitle() + "，确认报告本章存在章节错乱、内容缺失、排版混乱问题?");
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FuncPageStatsApi.readChapterError(1, sourceStats);
                    TxtChapter chapter = mPageLoader.getCurrChapter();
                    if (chapter != null) {
                        mReadPresenter.uploadChapterError(mBook.bookName, chapter);
                    }
                    dialog.cancel();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FuncPageStatsApi.readChapterError(2, sourceStats);
                    dialog.cancel();
                }
            });
            builder.create().show();
        } else if (v.getId() == R.id.read_btn_back) {
            gBookId = 0;
            onBackPressed();
            FuncPageStatsApi.loadFailBack(Long.parseLong(mBookId), sourceStats);
        } else if (v.getId() == R.id.read_btn_retry) {
            showLoading();
            initData();
            FuncPageStatsApi.loadFailRetry(Long.parseLong(mBookId), sourceStats);
        } else if (v.getId() == R.id.iv_download_close) {
            isHideDownloadLayout = true;
            layoutDownloadBottom.setVisibility(GONE);
        } else if (v.getId() == R.id.read_ad_clear) {
//            mExtraPageMgr.showRewardVideoEnterDialog(mHandler, adContainer);
            int freeDuration = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.RD_BANNER_FREE, 15);
            mExtraPageMgr.showRewardVideo(mHandler, adContainer, freeDuration, Constants.channalCodes[4],
                    FunPageStatsConstants.CLOSE_ADPOP, "2", sourceStats);
            AdHttpUtil.clickBannerClose(AdConfigManger.getInstance().showAd(ReadActivity.this, Constants.channalCodes[4]));
            mPageLoader.saveRecord(false);
        } else {
            gBookId = 0;
            super.onClick(v);
        }
    }

    public void gotoBookDownloadActivity() {
        if (BookDownloadManager.getsInstance().isDownloadingOrPending(Long.valueOf(mBookId))) {
            ToastUtils.show("该书籍正在下载队列中...");
        } else {
            Intent intent2 = new Intent(this, BookDownloadActivity.class);
            intent2.putExtra(BookDownloadActivity.BOOK_ID, Long.valueOf(mBookId));
            intent2.putExtra(BookDownloadActivity.BOOK_NAME, mBook == null ? "" : mBook.getBookName());
            intent2.putExtra(RouterPath.KEY_PARENT_ID, PageNameConstants.READER);
            intent2.putExtra(RouterPath.KEY_SOURCE, sourceStats);
            startActivity(intent2);
            FuncPageStatsApi.bookDetailReadDownload(PageNameConstants.READER, Long.parseLong(mBookId), sourceStats);
        }
    }

    private void showMoreMenuDialog() {
        if (windowPopup != null) {
            windowPopup.showAsDropDown(ivMoreMenu, 0, 0);
            return;
        }
        // 用于PopupWindow的View
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_read_more_menu, null, false);
        contentView.findViewById(R.id.dialog_read_more_book).setTag(1);
        contentView.findViewById(R.id.dialog_read_more_book).setOnClickListener(this);
        contentView.findViewById(R.id.dialog_read_more_share).setTag(2);
        contentView.findViewById(R.id.dialog_read_more_share).setOnClickListener(this);
        contentView.findViewById(R.id.dialog_read_more_error).setTag(3);
        contentView.findViewById(R.id.dialog_read_more_error).setOnClickListener(this);
        // 创建PopupWindow对象，其中：
        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
        windowPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置PopupWindow的背景
        windowPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        windowPopup.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        windowPopup.setTouchable(true);
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
        windowPopup.showAsDropDown(ivMoreMenu, 0, 0);
    }

    private void changeOrder() {
        if (mBook == null || mTxtChapters == null) {
            return;
        }
        if (mRvReadCategory.isLoading()) {
            ToastUtils.showLimited(R.string.is_loading);
            return;
        }
        if (!PhoneUtil.isNetworkAvailable(getApplication())) {
            ToastUtils.showLimited(R.string.toast_no_net);
            return;
        }
        mRvReadCategory.setIsLoad(true);
        mIsNormalOrderType = !mIsNormalOrderType;
        setOrderTv();
        int group = mIsNormalOrderType ? 0 : mTxtChapters.size() - 1;
        int type = mIsNormalOrderType ? ReadPresenter.LOAD_CATALOGUE_TYPE_POSITIVE : ReadPresenter.LOAD_CATALOGUE_TYPE_REVERSE;
        if (mTxtChapters.get(group).size() != 0) {
            mType = type;
            refreshCatalogue();
            mRvReadCategory.setIsLoad(false);
            return;
        }
        Logger.e(TAG, "chapterCount = " + mBook.chapterCount + ", group = " + group);
        mReadPresenter.loadCatalogue(mBook.bookId, mBook.chapterCount, group, type);
    }

    @Override
    public void loadBookChaptersSuccess(List<ChapterListBean> chapterListBeans) {
        ChapterListBean listBean = chapterListBeans.get(0);
        mBook = listBean.mOwnBook;
        totalChapter = listBean.totalChapter;
        mPageLoader.setTotalChapter(totalChapter);
        mBookName.setText(mBook.bookName);
        mCatalogue.setText(mBook.isFinish ? "已完结   " +
                ViewUtils.getString(R.string.chapter_count, totalChapter) : ViewUtils.getString(R.string.chapter_count, totalChapter));
        //设置书籍来源.
        mBookFrom = listBean.from;
        mType = ReadPresenter.LOAD_CATALOGUE_TYPE_NORMAL;
        StatisHelper.onEvent().bookReading(mBook.bookName, mFrom);

        int chapterCount = mBook.getChapterCount();
        int groupCount = chapterCount % 50 == 0 ? chapterCount / 50 : chapterCount / 50 + 1;
        //有些书籍有番外篇什么的，后台并未统计这些章节，导致数组越界
        if (groupCount < chapterListBeans.size()) {
            groupCount = chapterListBeans.size();
        }
        if (bookChapterList == null) {
            bookChapterList = new ArrayList<>(groupCount);
            for (int i = 0; i < groupCount; i++) {
                bookChapterList.add(new ArrayList<ChapterBean>());
            }
        }
        for (ChapterListBean chapterListBean : chapterListBeans) {
            bookChapterList.set(chapterListBean.mGroupIndex, chapterListBean.getList());
        }
        mBook.bookChapterList = bookChapterList;
        setToolBarLayout(mBook.getBookName());
        mPageLoader.openBook(mBook);
        //不显示引导.
        //showGuide();

        if (!BookShelfPresenter.isAdded(mBook.getBookId())) {
            //加入书架倒计时
            int duration = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.AUTOADD_TIME, 20);
            if (mHandler != null) {
                mHandler.postDelayed(addBookshelfRunnable, duration * 60 * 1000);
            }
        }
        dismissLoading();
    }

    private void showGuide() {
        boolean hasShowed = SPUtils.INSTANCE.getBoolean("read_guide", false);
        if (!hasShowed) {
            mGuideView.setVisibility(VISIBLE);
            SPUtils.INSTANCE.putBoolean("read_guide", true);
        }
    }

    /**
     * 刷新目录页的下载状态
     */
    private void updateChapterList() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                for (int i = 0; i < bookChapterList.size(); i++) {
                    List<ChapterBean> beanList = bookChapterList.get(i);
                    if (beanList != null && !beanList.isEmpty()) {

                        boolean isUpdate = false;

                        List<BookDownloadDBBean> dbBeanList = BookDownloadHelper.getsInstance().queryDownloadCompleteTaskByGroup(mBookId, beanList.get(0).getSeqNum());
                        if (dbBeanList != null && !dbBeanList.isEmpty()) {
                            for (ChapterBean chapterBean : beanList) {
                                if (chapterBean.isDownload) {
                                    //如果已经是已下载状态，就不再对比状态
                                    continue;
                                }
                                for (BookDownloadDBBean dbBean : dbBeanList) {
                                    if (chapterBean.getChapterId() == dbBean.getChapterId()) {
                                        chapterBean.isDownload = true;
                                        isUpdate = true;
                                    }
                                }
                            }
                        }
                        if (isUpdate) {
                            emitter.onNext(i);
                        }
                    }
                }
                emitter.onComplete();
            }
        }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer i) {
                        mPageLoader.onChaptersGroupUpdate(i);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        setCategorySelect(mPageLoader.getCurrGroupPos(), mPageLoader.getCurrChapterPos());
                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadBookEvent(BookDownloadEvent event) {
        if (Long.parseLong(mBookId) == event.getBookId()) {
            if (event.getDownloadStates() == BookDownloadEvent.DOWNLOADING) {
                isDownloading = true;
                tvDownloadBottom.setText("正在下载（" + event.getProgress() + " /" + event.getTotal() + "）：" +
                        event.getBookDownloadDBBean().getTitle());
                if (isDownloading && !isHideDownloadLayout) {
                    layoutDownloadBottom.setVisibility(VISIBLE);
                }
            } else if (event.getDownloadStates() == BookDownloadEvent.DOWNLOAD_COMPLETE) {
                isDownloading = false;
                layoutDownloadBottom.setVisibility(GONE);
                updateChapterList();
            } else if (event.getDownloadStates() == BookDownloadEvent.DOWNLOAD_ERROR) {
                isDownloading = false;
                layoutDownloadBottom.setVisibility(GONE);
                updateChapterList();
                showRetryDialog(event.getBookDownloadTask());
            }
        }
    }

    private void showRetryDialog(final BookDownloadTask task) {
        try {
            String message = "当前网络不稳\n是否重试下载?";
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

    @Override
    public void preLoadBookChaptersSuccess(ChapterListBean chapterListBean, int groupPos) {
        bookChapterList.set(groupPos, chapterListBean.getList());
        mType = ReadPresenter.LOAD_CATALOGUE_TYPE_PRE;
        mPageLoader.onChaptersGroupUpdate(groupPos);
    }

    @Override
    public void loadCatalogueSuccess(ChapterListBean chapterListBean, int groupPos, int type) {
        mType = type;
        totalChapter = chapterListBean.totalChapter;
        mPageLoader.setTotalChapter(totalChapter);
        bookChapterList.set(groupPos, chapterListBean.getList());
        if (type == ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE) {
            mRvReadCategory.onLoadMoreTopComplete();
        } else if (type == ReadPresenter.LOAD_CATALOGUE_TYPE_BOTTOM_MORE) {
            mRvReadCategory.onLoadMoreBottomComplete();
        }
        mPageLoader.onChaptersGroupUpdate(groupPos);
        if (type == ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE) {
            mRvReadCategory.setSelection(50);
        }
    }

    @Override
    public void loadCatalogueFailed(int groupPos, int type) {
        if (type == ReadPresenter.LOAD_CATALOGUE_TYPE_TOP_MORE) {
            mRvReadCategory.onLoadMoreTopComplete();
        } else if (type == ReadPresenter.LOAD_CATALOGUE_TYPE_BOTTOM_MORE) {
            mRvReadCategory.onLoadMoreBottomComplete();
        }
        mRvReadCategory.setIsLoad(false);
        ToastUtils.showLimited(R.string.load_failed);
    }

    @Override
    public void loadChapterContentsSuccess() {
//        errorTipLayout.setVisibility(GONE);
        Logger.e(TAG, "是否可以显示： " + (mPageLoader.getPageStatus() == AbsPageLoader.STATUS_LOADING)
                + ", 当前状态： " + mPageLoader.getPageStatus());
        if (mPageLoader.getPageStatus() == AbsPageLoader.STATUS_LOADING && mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //设置为渲染成功.
                    mRenderingState = 1;
                    Logger.e(TAG, "开始渲染");
                    mPageLoader.showChapterContent();
                }
            });
            dismissLoading();
        }
    }

    @Override
    public void loadChapterContentsFailed() {
        if (mPageLoader.getPageStatus() == AbsPageLoader.STATUS_LOADING || mPageLoader.getPageStatus() == AbsPageLoader.STATUS_ERROR) {
            //未渲染成功的情况下, 设置为渲染失败.
            if (mRenderingState != 1) {
                mRenderingState = 2;
            }
            showError();
        } else {
            Logger.e(TAG, "内容显示失败，不显示错误界面");
        }
    }

    @Override
    public void loadTodayReadTimeSuccess(ReadTaskResp readTaskResp) {
        mTodayReadTime = readTaskResp.getReadTime();
        mStatus = readTaskResp.getStatus();
        mReadStage = readTaskResp.getReadStage();
        currStage = readTaskResp.getStage();
        Logger.e(TAG, "阅读任务：" + readTaskResp.toString());
    }

    @Override
    public void getDownloadOptionSuccess(final ChapterDownloadOptionResp resp) {

        Observable.just(mBook.getBookId()).map(new Function<String, BookDetailBean>() {
            @Override
            public BookDetailBean apply(String s) throws Exception {
                BookDetailBean bookDetailBean = mReadPresenter.getBookDetailBean(mBook.getBookId());
                return bookDetailBean;
            }
        }).subscribeOn(MtSchedulers.io())
                .observeOn(MtSchedulers.mainUi())
                .subscribe(new Observer<BookDetailBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BookDetailBean bookDetailBean) {
                        ivDownload.setClickable(true);
                        DownloadBottomDialog downloadBottomDialog = new DownloadBottomDialog(ReadActivity.this, resp, bookDetailBean, PageNameConstants.READER, sourceStats);
                        downloadBottomDialog.show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ivDownload.setClickable(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getDownloadOptionError() {
        ivDownload.setClickable(true);
        ToastUtils.show("下载配置信息获取失败!");
    }

    public void bannerLoadSuccess() {
        if (!hasInitBannerTask) {
            ScheduledService.getInstance().sheduler(bannerCloseTipTask, 0, 30, TimeUnit.MINUTES);
        }
        hasInitBannerTask = true;
    }

    @Override
    public void onBackPressed() {
        if (mReadTopMenu.getVisibility() == View.VISIBLE) {
            if (!ReadSettingManager.getInstance().isFullScreen()) {
                toggleMenu(false);
                return;
            }
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return;
        } else if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }
        gBookId = 0;

        super.onBackPressed();
    }

    @Override
    public void finish() {
        gBookId = 0;
        uploadYMEvent(false);

        if (mBook == null || bookReopend) {
            SPUtils.INSTANCE.remove(ReadModeUtil.TAG);
            super.finish();
            return;
        }

        SPUtils.INSTANCE.remove(ReadModeUtil.TAG);
        //判断该书籍是否为书架应用.
        if (mBook.mIsInShelf || BookShelfPresenter.isAdded(mBook.getBookId())) {
            //通过返回键直接退出阅读器.
            FunctionStatsApi.readQuit(mRenderingState, mBookId, TimeTool.currentTimeMillis() - mStartReadTime);
            super.finish();
        } else {
            showShelfDialog();
        }
    }

    private void showShelfDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && this.isDestroyed()) {
            return;
        }
        if (this.isFinishing()) {
            return;
        }
        SimpleDialog.Builder builder = new SimpleDialog.Builder(this);
        builder.setTitle(R.string.add_shelf);
        builder.setMessage(R.string.add_shelf_msg);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                StatisHelper.onEvent().subscription(mBook.bookName, "退出阅读页提示加入书架");
                mBook.setLastRead(TimeTool.currentTimeMillis());
                mReadPresenter.addBookToShelf(mBook, getString(R.string.add_shelf_success));
                ReadActivity.super.finish();
                //添加退出阅读器节点.
                FunctionStatsApi.readQuit(mRenderingState, mBookId, TimeTool.currentTimeMillis() - mStartReadTime);
                // bug ： 上报时长(分钟数)
                FuncPageStatsApi.readExit((System.currentTimeMillis() - enterTime) / 60_000, prevPageId, 1, sourceStats);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ReadActivity.super.finish();
                //添加退出阅读器节点.
                FunctionStatsApi.readQuit(mRenderingState, mBookId, TimeTool.currentTimeMillis() - mStartReadTime);
                // bug ： 上报时长(分钟数)
                FuncPageStatsApi.readExit((System.currentTimeMillis() - enterTime) / 60_000, prevPageId, 2, sourceStats);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 稍微延时，避免在跨天清除数据前显示
//        requestBannerView();
        if (bookReopend) {
            return;
        }
        acquireLock();
        //开始阅读, 执行阅读时长统计.
        execReadingTime(true);
        // 处理广点通激励视频不回调关闭动作问题
        Logger.e(TAG, "免广告时长：" + mExtraPageMgr.getFreeDuration());
        if (isBackground && mExtraPageMgr.getHasShowVideo()) {
            if (!hasShowDoubleDialog) {
                mExtraPageMgr.showFreeTimeDialog(mHandler, adContainer,
                        mExtraPageMgr.getFreeDuration() == null ? 30 : mExtraPageMgr.getFreeDuration());
            } else {
                hasShowDoubleDialog = false;
                TaskMgr.show(this, getSupportFragmentManager(), getString(R.string.finish_reward_video_task), gRewarTastId);
            }
        }
        mExtraPageMgr.setHasShowVideo(false);
        isBackground = false;

        if (mReadTopMenu.getVisibility() != View.VISIBLE) {
            hideSystemBar();
        }
//        toggleMenu(false);
//        if (mSettingDialog.isShowing()) {
//            mSettingDialog.dismiss();
//        }
        if (mSettingDialog != null && isShowToBack) {
            isShowToBack = false;
            toggleMenu(true);
            mSettingDialog.show();
        }
        gBookId = StringFormat.parseLong(mBookId, 0);

//        rstRdTime = System.currentTimeMillis();
        checkReadTiredTime(true);
    }

    public void requestBannerView() {
        if (mHandler != null && !isBackground) {
            mHandler.removeCallbacks(bannerRunnable);
            mHandler.removeMessages(REQUEST_CODE_BANNER);
            Logger.e("ad#Extr", "开始请求banner");
            mHandler.postDelayed(bannerRunnable, 500);
        }
    }

    private Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            bannerAdSiteBean = null;
            if (!mExtraPageMgr.getHasShowBanner() && bannerAdSiteBean != null) {
                bannerAdSiteBean = AdConfigManger.getInstance().getAvailiableAdSite(ReadActivity.this,
                        Constants.channalCodes[1], bannerAdSiteBean.getId());
            }
            if (bannerAdSiteBean == null) {
                bannerAdSiteBean = AdConfigManger.getInstance().showAd(ReadActivity.this, Constants.channalCodes[1]);
            }
            if (bannerAdSiteBean == null) {
                Logger.e("ad#Extr", "没有可用广告");
                return;
            }

            if (!PhoneUtil.isNetworkAvailable(ReadActivity.this)) {
                Logger.e("ad#Extr", "没有网络了");
                return;
            }
            if (bottomAdRootView.getVisibility() != View.VISIBLE && bannerAdSiteBean != null) {
                bottomAdRootView.setVisibility(View.VISIBLE);
                saveBookRecord();
            }
            mExtraPageMgr.showBottomBannerAd(adBottomWhiteBg, bottomAdView, adClear, mHandler,
                    Constants.channalCodes[1], bannerAdSiteBean);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePage(ReadUpdateEvent event) {
        mPageLoader.supportPageBetweenChapters(false);
        mPageLoader.saveRecord(false);
        mPageLoader.gotoNextPage();
        long freeDuration = getSharedPreferences(AdConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getLong(AdConstants.CURR_FREE_TIME, 30);
        if (mHandler != null) {
            mHandler.postDelayed(adSupportRunnable, freeDuration * TimeUtils.MINUTE_1);
        }
        mExtraPageMgr.destroy(bottomAdView);
        adClear.setVisibility(View.GONE);
        adContainer.setVisibility(View.GONE);
    }

    private Runnable adSupportRunnable = new Runnable() {

        @Override
        public void run() {
            if (mHandler == null) {
                return;
            }
            // 判断是否还在免广告时间
            SharedPreferences preference = getSharedPreferences(AdConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
            long freeDuration = preference.getLong(AdConstants.CURR_FREE_TIME, 30) * TimeUtils.MINUTE_1;
            long freeStartTime = preference.getLong(AdConstants.KEY_FREE_START_TIME, 0);
            BookRecordGatherResp bookRecordGatherResp = SharePreferenceUtils.getObject(BaseApplication.context.globalContext, SharePreferenceUtils.READ_HISTORY_CACHE);
            if (bookRecordGatherResp != null && bookRecordGatherResp.getLastSec() > 0) {
                mHandler.postDelayed(adSupportRunnable, freeDuration + bookRecordGatherResp.getLastSec() * 1000);
                return;
            } else if (System.currentTimeMillis() - freeStartTime  + 100 < freeDuration) {
                mHandler.postDelayed(adSupportRunnable, freeDuration + freeStartTime - System.currentTimeMillis());
                return;
            }

            if (!isFinishing() && !isDestroyed() && mPageLoader != null) {
                Logger.e(TAG, "阅读器重新显示广告");
                saveBookRecord();
                mExtraPageMgr.showBottomBannerAd(adBottomWhiteBg, bottomAdView, adClear, mHandler, Constants.channalCodes[1],
                        AdConfigManger.getInstance().showAd(ReadActivity.this, Constants.channalCodes[1]));
            } else {
                Logger.e(TAG, "阅读器已经退出");
            }
        }
    };

    public void saveBookRecord() {
        Logger.e(TAG, "底部广告重新出现，保存阅读记录");
        mPageLoader.saveRecord(false);
        mPageLoader.supportPageBetweenChapters(true);
        bottomAdRootView.setVisibility(VISIBLE);
        mPageLoader.clearInfoFlowData();
        loadInfoFlowAd();
    }

    /**
     * 一次性阅读时长满足，自动加入书架
     */
    private Runnable addBookshelfRunnable = new Runnable() {
        @Override
        public void run() {
            mBook.setLastRead(TimeTool.currentTimeMillis());
            mReadPresenter.addBookToShelf(mBook, "");
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 解决滑出通知栏导致标题栏重叠问题
        if (hasFocus && (mReadTopMenu.getVisibility() != View.VISIBLE)) {
            hideSystemBar();
        }
    }

    public void offScreenTimeChanged(int index) {
        if (index >= ReadSettingManager.getInstance().offScreenArray.length) {
            index = ReadSettingManager.getInstance().offScreenArray.length - 1;
        }
        offScreenTime = ReadSettingManager.getInstance().offScreenArray[index];
        acquireLock();
    }

    private void acquireLock() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        if (mHandler != null && getSysScreenLockTime() < offScreenTime) {
//            mHandler.removeCallbacks(wakeLockRunnable);
//            mHandler.postDelayed(wakeLockRunnable, offScreenTime - getSysScreenLockTime());
//        }
    }

    private Runnable wakeLockRunnable = new Runnable() {
        @Override
        public void run() {
            clearWakeLock();
        }
    };

    private void clearWakeLock() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 获取系统休眠时间
     */
    public long getSysScreenLockTime() {
        long result = 0;
        try {
            result = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPause() {
        if (mSettingDialog.isShowing()) {
            isShowToBack = true;
            mSettingDialog.dismiss();
        }
        super.onPause();
        Logger.i(TAG, "onPause: ");
        clearWakeLock();
        mPageLoader.saveRecord(false);
        //结束阅读, 执行阅读时长统计.
        execReadingTime(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBackground = true;
        checkReadTiredTime(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //上报补充IMEI、IMSI等设备信息.
        MobileInfoPresenter.uploadSupplyMobileInfo();
        //授权完成, 启动定位.
        BDLocationMgr.startLocation();
        boolean isSucc = PermissionMgr.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        if (isSucc) {
            //调用授权成功统计.
            FunctionStatsApi.authSucc();
            PushMgr.registerXimiPush(ReadActivity.this);
            NotificationsUtils.isNotifyEnable(ReadActivity.this, getSupportFragmentManager(), PageNameConstants.READER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DOWNLOAD && resultCode == 1008) {
            ivDownload.setClickable(false);
            mReadPresenter.getDownloadOption(Long.valueOf(mBookId));
//            gotoBookDownloadActivity();
        } else {
            PermissionMgr.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerTimerTask.terminal();
        bannerCloseTipTask.terminal();
        mExtraPageMgr.destroy(bottomAdView);
        EventBus.getDefault().unregister(this);
        IOUtil.clearList(mTxtChapters);
        IOUtil.clearList(bookChapterList);
        IOUtil.clearList(prevChapters);
        unregisterReceiver(mReceiver);
        mPageLoader.closeBook();
        if (mHandler != null) {
            mHandler.removeMessages(REQUEST_CODE_BANNER);
            mHandler.removeCallbacks(bannerRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        mReadPresenter.destroy();
        mPageLoader.onDestroy();
    }

    /**
     * 是否阅读中标识
     */
    private boolean isReading = false;

    /**
     * 阅读检测任务是否执行中.
     */
    private boolean isRunReadingTimeTask = false;

    /**
     * 执行阅读时长统计
     *
     * @param reading 是否阅读中
     */
    private void execReadingTime(boolean reading) {
        isReading = reading;
        //判断是否为前台.
        if (isReading && !isRunReadingTimeTask) {
            //阅读中, 启动异步线程.
            ZExecutorService.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    //设置任务执行中.
                    isRunReadingTimeTask = true;
                    while (isReading) {
                        //遍历120次, 每次间隔500毫秒.
                        for (int index = 0; index < 120; index++) {
                            try {
                                Thread.sleep(500);
                            } catch (Throwable throwable) {
                            }
                            //阅读时长累加500毫秒.
                            mCurPageReadingTime += 500;
                            //判断是否已切换到后台.
                            if (!isReading) {
                                //设置任务执行结束.
                                isRunReadingTimeTask = false;
                                //直接返回即可.
                                return;
                            }
                            //判断当前页面是否已超过45秒.
                            if (mCurPageReadingTime >= 45_000) {
                                //重新开始计时(既:同一个页停留超过45秒, 则不再累加阅读时长)
                                index = 0;
                            } else if (mCurPageReadingTime == 5_000) {
                                //判断是否上报有效翻页(大于或等于5秒, 则认为是有效翻页).
                                FunctionStatsApi.rValidNextPage(mBookId);
                            }
                        }
                        if (mHandler != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    long tiredTime = TimeUtils.MINUTE_1 + SPUtils.INSTANCE.getLong(SPUtils.INSTANCE.getREAD_TIME_TOTAL(), 0);
                                    Logger.e(TAG, "累计疲劳阅读时长: " + tiredTime / 1000 + " 秒");
                                    SPUtils.INSTANCE.putLong(SPUtils.INSTANCE.getREAD_TIME_TOTAL(), tiredTime);
                                    checkReadTiredTime(isReading);
                                }
                            });
                        }

                        //累计在线时长.
                        FunctionStatsApi.readingTime(mBookId);
                        FuncPageStatsApi.readDuration(StringFormat.parseLong(mBookId, 0), prevPageId, sourceStats);
                        //递增总阅读时长.
                        BookShelfPresenter.updateWeekReadTime(true, 0);
                        //递增周阅读时长.
                        BookShelfPresenter.updateTotalReadTime(true, 0);

                        //请求服务器确定是否完成阅读任务
                        if (mStatus != 3) {
                            mTodayReadTime = mTodayReadTime + 1;
                            finishTask();
                        }
                    }
                    //设置任务执行结束.
                    isRunReadingTimeTask = false;
                }
            });
        }
    }

    //请求服务器确定是否完成阅读任务
    private void finishTask() {
        if (mReadStage == null || mReadStage.size() == 0) {
            return;
        }
        boolean isReadOk = false;
        for (ReadTaskResp.ReadStageBean readStageBean : mReadStage) {
            if (readStageBean == null) continue;

            if (mTodayReadTime > ((readStageBean.getTime() - 4) > 0 ? (readStageBean.getTime() - 4) : 0) && mTodayReadTime < (readStageBean.getTime() + 4)) {
                isReadOk = true;
                break;
            }
        }

        if (isReadOk) {
            Single.fromCallable(new Callable<TaskFinishResp>() {
                @Override
                public TaskFinishResp call() throws Exception {
                    TaskFinishResp taskFinishResp = TaskMgr.taskFinish(ReadActivity.this, TaskMgr.READ_TASK);
                    return taskFinishResp;
                }
            }).subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(new Consumer<TaskFinishResp>() {
                @Override
                public void accept(TaskFinishResp taskFinishResp) throws Exception {
                    if (taskFinishResp != null) {
                        mStatus = taskFinishResp.getStatus();
                        mTodayReadTime = taskFinishResp.getReadTime();

                        if (taskFinishResp.getBookBean() != 0) {//完成任务,弹框
                            switch (currStage) {
                                case 1:
                                    gRewarTastId = TaskMgr.READ_FIRST_TASK;
                                    break;
                                case 2:
                                    gRewarTastId = TaskMgr.READ_SECOND_TASK;
                                    break;
                                case 3:
                                    gRewarTastId = TaskMgr.READ_THIRD_TASK;
                                    break;
                                default:
                                    gRewarTastId = 0;
                            }
                            if (isTaskDialogShow) {
                                return;
                            }
                            BookDoTaskFragment bookDoTaskFragment = new BookDoTaskFragment();
                            bookDoTaskFragment.setClickListener(new ReadSleepFragment.OnExitAppListener() {
                                @Override
                                public void onLeft() {
                                    FuncPageStatsApi.clickRewardDialog("2");
                                }

                                @Override
                                public void onRight() {
                                    hasShowDoubleDialog = true;
                                    FuncPageStatsApi.clickRewardDialog("1");
                                    mExtraPageMgr.showRewardVideo(mHandler, adContainer, 0, Constants.channalCodes[10],
                                            FunPageStatsConstants.REWARD, "6", sourceStats);
                                }

                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    isTaskDialogShow = false;
                                }
                            });
                            isTaskDialogShow = true;
                            bookDoTaskFragment.setValue(getResources().getString(R.string.finish_read_task),
                                    taskFinishResp.getBookBean(), AdConfigManger.getInstance()
                                            .showAd(ReadActivity.this, Constants.channalCodes[10]) != null, gRewarTastId);
                            bookDoTaskFragment.showNow(getSupportFragmentManager(), "taskFinish");
                            bookDoTaskFragment.updateNightMode(isNightMode);
                            EventBus.getDefault().post(new TaskFinishEvent(TaskMgr.READ_TASK));
                            hideSystemBar();
                            FuncPageStatsApi.showRewardDialog();

                            ++currStage;
                        }
                    }
                }
            });
        }
    }

    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            return true;
        }
        lastClickTime = curClickTime;

        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Logger.d(TAG, "KEYCODE_VOLUME_DOWN");
                mPvReadPage.turnNextPage();
                //声音减, 向下翻页.
                FunctionStatsApi.readVolumeDown(mBookId);
                FuncPageStatsApi.readVolNextPage(prevPageId, sourceStats);
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                Logger.d(TAG, "KEYCODE_VOLUME_UP");
                mPvReadPage.turnPrePage();
                //声音加, 向上翻页.
                FunctionStatsApi.readVolumeUp(mBookId);
                FuncPageStatsApi.readVolPrevPage(prevPageId, sourceStats);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 断网情况下，即使预加载成功的章节页不能阅读
     */
    private void chapterChangeShowError(final int seqNum, final String bookId, final int ChapterId) {

        Logger.e(TAG, "chapterChangeShowError");

        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {

                Logger.e(TAG, "chapterBean.getSeqNum():  " + seqNum + "   mCurSeqNum:  " + mCurSeqNum);

                //判断是否往下一页翻
                if (seqNum > mCurSeqNum) {
                    //没网络
                    if (!PhoneUtil.isNetworkAvailable(ReadActivity.this)) {
                        //判断是否下载过
                        BookDownloadDBBean bookDownloadDBBean = BookDownloadHelper.getsInstance().queryChapterByChapterId(bookId, ChapterId);
                        if(bookDownloadDBBean == null){
                            emitter.onNext("");
                        }
                    } else {
                        mCurSeqNum = seqNum;
                    }
                }
            }
        }).subscribeOn(MtSchedulers.io())
                .observeOn(MtSchedulers.mainUi())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        showError();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Logger.e(TAG, "onComplete");
                    }
                });
    }

    private void showLoading(){
        if(errorTipLayout.getVisibility() == VISIBLE){
            errorTipLayout.setVisibility(GONE);
        }

        mPageLoader.showLoading();
        readLoadingLayout.setVisibility(VISIBLE);
    }

    private void dismissLoading(){
        if(readLoadingLayout.getVisibility() == VISIBLE){
            readLoadingLayout.setVisibility(GONE);
        }
    }

    private void showError(){
        dismissLoading();
        mPageLoader.showError();
    }


    private class BannerRequestRunnable extends ScheduledService.JobRunnable {

        public BannerRequestRunnable(@NonNull String jobId) {
            super(jobId);
        }

        @Override
        public void run() {
            requestBannerView();
        }
    }

    private class BannerTipRunnable extends ScheduledService.JobRunnable {

        public BannerTipRunnable(@NonNull String jobId) {
            super(jobId);
        }

        @Override
        public void run() {
            checkBannerTipStatus();
        }
    }

}
