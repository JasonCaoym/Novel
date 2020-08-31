package com.duoyue.mianfei.xiaoshuo.read.page;

import android.graphics.*;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.FrameLayout;
import com.duoyue.app.common.mgr.ReadHistoryMgr;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.io.IOUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mianfei.xiaoshuo.R;
import com.duoyue.mianfei.xiaoshuo.read.page.animation.Layer;
import com.duoyue.mianfei.xiaoshuo.read.setting.ReadSettingManager;
import com.duoyue.mianfei.xiaoshuo.read.utils.*;
import com.duoyue.mod.ad.AdConfigManger;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.ChapterBean;
import com.zydm.base.rx.RxUtils;
import com.zydm.base.utils.SPUtils;
import com.zydm.base.utils.TimeUtils;
import com.zydm.base.utils.ToastUtils;
import com.zydm.base.utils.ViewUtils;
import io.reactivex.*;
import io.reactivex.disposables.Disposable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.zydm.base.common.BaseApplication.context;


public abstract class AbsPageLoader {
    private static final String TAG = "ad#AbsPageLoader";
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_FINISH = 2;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_EMPTY = 4;
    public static final int STATUS_PARSE = 5;
    public static final int STATUS_PARSE_ERROR = 6;
    public static final int STATUS_NO_AD = 7;

    private static final int DEFAULT_MARGIN_HEIGHT = 28;
    private static final int DEFAULT_MARGIN_WIDTH = 12;

    private static final int DEFAULT_TIP_SIZE = 12;
    private static final int EXTRA_TITLE_SIZE = 4;
    private static final int TIP_COLOR_NIGHT = Color.parseColor("#88BFBFBF");
    private final String mBookId;

    protected List<List<TxtChapter>> mChapterList;
    protected BookRecordBean mRecordBook;
    protected OnPageChangeListener mPageChangeListener;
    protected FragmentActivity mActivity;
    protected PageView mPageView;
    private TxtPage mCurPage;
    private WeakReference<List<TxtPage>> mWeakPrePageList;
    private WeakReference<List<Integer>> mWeakPreAdPosList;
    private List<TxtPage> mCurPageList;
    private List<TxtPage> mNextPageList;

    private Paint mBatteryPaint;
    private Paint mTipPaint;
    private Paint mTitlePaint;
    private Paint mBgPaint;
    private TextPaint mTextPaint;
    private Paint mButtonPaint;
    private TextPaint mAdTextPaint;
    private TextPaint mRulePaint;
    private TextPaint imgPaint;
    private ReadSettingManager mSettingManager;
    private TxtPage mLastPage;
    protected int mStatus = STATUS_LOADING;
    protected int mCurPos = 0;
    protected boolean isBookOpen = false;
    private Disposable mPreLoadDisposable;
    private int mLastPos = 0;
    private int mVisibleWidth;
    private int mVisibleHeight;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private int mMarginWidth;
    private int mMarginHeight;
    private int mTextColor;
    private int mTitleSize;
    private int mTextSize;
    private int mTextInterval;
    private int mTitleInterval;
    private int mTextPara;
    private int mTitlePara;
    private int mBatteryLevel;
    private int mBgTheme;
    private int mPageBg;
    private boolean isNightMode;
    protected boolean mSupportPageBetweenChapters;
    protected boolean mSupportPageAfterLastChapter;
    protected int mCurGroupPos;
    protected int mLastGroupPos;
    /**
     * 阅读maxPageIntervalAd页后显示插屏广告
     */
    private int maxPageIntervalAd;
    /**
     * 记录阅读的页数
     */
    private int readPageSize;
    /**
     * 记录翻页取消的之前的动作
     */
    private boolean prevIsNext;
    private Random mRandom = new Random();

    private int firstShowAdMin;
    private int firstShowAdMax;
    private int showAdMin;
    private int showAdMax;

    private Bitmap playBitmap;
    /**
     * 记录章节页码，从0开始的
     */
    private int recordPageIndex = -1;

    private int totalPageSize;

    private int lineSpacePage;

    /**
     * 翻页次数(向下翻页)
     */
    private int nextPageNumber = 0;

    private Bitmap bgBitmap;
    private Bitmap scrollTopBitmap;
    private Bitmap scrollBitmap;

    private String ruleStr = "规则说明";

    private RectF mVideoRectF = null;
    private float videoTxtSize = 15;
    private Rect mRuleRect = null;
    private Rect mTextRect = null;
    private String freeStr;
    private boolean hasTooBigFirstAdPos;
    private float videoBtnHeight = 44; // dp
    private float videoBtnPaddingTop = 40; // dp
    private float playImgPaddingRight = 6; // dp
    private float rulePaddingTop = 16; // dp
    private float videoLayoutPaddingLeft = 60; // dp
    private float playPaddingRight = 5; // dp

    private String prevPageId;
    private String sourceStats;
    private boolean isFirstEnter = true;
    /**
     * 保存进入阅读器的日期，用于判断用户是否跨天阅读
     */
    private String currData;
    private int firstAdPageMax;
    /**
     * 距离顶部的高度
     */
    private int tipMarginHeight;

    protected PageMode mPageMode;
    protected long prevPageStayTime;
    protected int textSpaceFactor = 1;
    protected boolean pageModeChanged;

    int mNextPageListSize = 0;
    private int totalChapter;
    private int mScreenHeight;
    private int mAdHeight;
    private long freeTime;
    private boolean showAdWithBanner;
    private boolean hasChapterEndLoaded;
    private List<Integer> curAdPosList = new ArrayList<>();
    private List<Integer> nextAdPosList = new ArrayList<>();
    /**
     * 章节末前一页是插页广告
     */
    private boolean isInfoFlowAdPre = false;

    /**
     * @param
     */
    public AbsPageLoader(FragmentActivity activity, PageView pageView, String prevPageId, String sourceStats,String bookId) {
        this.mActivity = activity;
        mPageView = pageView;
        this.prevPageId = prevPageId;
        this.sourceStats = sourceStats;
        mBookId = bookId;
        firstShowAdMin = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_MIN_FIRST, 8);
        firstShowAdMax = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_MAX_FIRST, 12);
        showAdMin = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_MIN_NOR, 4);
        showAdMax = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_MAX_NOR, 6);
        Logger.e(TAG, "AbsPageLoader: firstShowAdMin = " + firstShowAdMin + ", firstShowAdMax = " + firstShowAdMax
                + ", showAdMin = " + showAdMin + ", showAdMax = " + showAdMax);
        try {
            freeTime = SPUtils.INSTANCE.getInt(AdConstants.ReadParams.FLOW_FREE_DURATION, 15);
        } catch (Exception ex) {
            freeTime = SPUtils.INSTANCE.getLong(AdConstants.ReadParams.FLOW_FREE_DURATION, 15);
        }
        currData = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_01);
        firstAdPageMax = genPageIntervalFirst();
        mScreenHeight = PhoneUtil.getScreenSize(context.globalContext)[1];
        mAdHeight = mScreenHeight / 2 + Utils.dp2px(20);
        initData();
        initPaint();
        initPageView();
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();
        mTextSize = mSettingManager.getTextSize();
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE);
        isNightMode = mSettingManager.isNightMode();
        if (isNightMode) {
            getAndRecycleVideoImg(R.mipmap.icon_play_night);
        } else {
            getAndRecycleVideoImg(R.mipmap.ad_play);
        }
        mBgTheme = mSettingManager.getReadBgTheme();

        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH);
        mMarginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT);

        textSpaceFactor = ReadSettingManager.getInstance().getLineSpace();
        mTextInterval = (int) (mTextSize * ReadSettingManager.getInstance().textSpaceArray[textSpaceFactor]);

        mTitleInterval = (int) (mTitleSize * 0.681818f + 1.454545f);

        mTextPara = (int) (mTextSize * 1.272727f + 2.181818f);
        mTitlePara = (int) (mTitleSize * 1.434343f + 10f);

        maxPageIntervalAd = genPageIntervalCnt();

        videoBtnHeight = ViewUtils.dp2px(videoBtnHeight);
        videoBtnPaddingTop = ViewUtils.dp2px(videoBtnPaddingTop);
        playImgPaddingRight = ViewUtils.dp2px(playImgPaddingRight);
        rulePaddingTop = ViewUtils.dp2px(rulePaddingTop);
        playPaddingRight = ViewUtils.dp2px(playPaddingRight);

        int[] screenSize = PhoneUtil.getScreenSize(context.globalContext);
        if (screenSize[1] * 1.0f / screenSize[0] >= 1.9f) {
            tipMarginHeight = ScreenUtils.dpToPx(13);
        } else {
            tipMarginHeight = ScreenUtils.dpToPx(3);
        }
        setPageMode(mSettingManager.getPageMode());
        if (isNightMode) {
            setBgColor(ReadSettingManager.NIGHT_MODE);
        } else {
            setBgColor(mBgTheme);
        }
    }

    private void initPaint() {
        mTipPaint = new Paint();
        mTipPaint.setColor(TIP_COLOR_NIGHT);
        mTipPaint.setTextAlign(Paint.Align.LEFT);
        mTipPaint.setTextSize(ScreenUtils.spToPx(DEFAULT_TIP_SIZE));
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setColor(mPageBg);

        mBatteryPaint = new Paint();
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setColor(TIP_COLOR_NIGHT);
        setTipColor(mBgTheme);

        mButtonPaint = new Paint();
        mButtonPaint.setStrokeWidth(2);
        mButtonPaint.setAntiAlias(true);
        mButtonPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        mAdTextPaint = new TextPaint();

        mAdTextPaint.setTextSize(ViewUtils.dp2px(videoTxtSize));
        mAdTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mAdTextPaint.setTypeface(Typeface.DEFAULT);
        mAdTextPaint.setAntiAlias(true);

        mRulePaint = new TextPaint();
        mRulePaint.setTextSize(ViewUtils.dp2px(videoTxtSize - 1));
        mRulePaint.setAntiAlias(true);

        imgPaint = new TextPaint();
        imgPaint.setAntiAlias(true);

        if (isNightMode) {
            mButtonPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
            mAdTextPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
            mRulePaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
        } else {
            mButtonPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.standard_red_main_color_c1));
            mAdTextPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.white));
            mRulePaint.setColor(mTextColor);
        }
    }

    private void initPageView() {
        mPageView.initSwitchAnim();
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mPageBg);
    }

    public void skipToChapter(int groupPos, int pos) {
        mStatus = STATUS_LOADING;
        // 校正下标
        mCurPos = Math.max(pos, 0);
        mCurGroupPos = groupPos;
        if (mWeakPrePageList != null && mWeakPrePageList.get() != null) {
            clearEachTxtPage(mWeakPrePageList.get(), true);
        }
        if (mWeakPreAdPosList != null && mWeakPreAdPosList.get() != null) {
            mWeakPreAdPosList.get().clear();
        }
        mWeakPrePageList = null;
        mWeakPreAdPosList = null;
        mLastPage = null;
        if (mPreLoadDisposable != null) {
            mPreLoadDisposable.dispose();
        }
        clearEachTxtPage(mNextPageList, true);
        nextAdPosList.clear();
        mNextPageList = null;
        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurGroupPos, mCurPos, false);
            preLoadCatalogue();
        }
        if (mCurPage != null) {
            mCurPage.position = 0;
            mCurPage.offsetY = 0;
            mCurPage.isExtraAfterChapter = false;
            mCurPage.isExtraChapterEnd = false;
        }
        // 判断是否已经预加载
        if (hasPreloadFlowAd) {
            hasTooBigFirstAdPos = true;
            totalPageSize = Math.max(genPageIntervalCnt() - readPageSize, 1);
        } else {
            hasTooBigFirstAdPos = false;
            totalPageSize = readPageSize;
        }
        mPageView.refreshPage();
        Logger.e(TAG, "readPageSize：" + readPageSize);
//        clearInfoFlowData();
    }

    public void setTotalChapter(int totalChapter) {
        this.totalChapter = totalChapter;
    }

    public void skipToPage(int pos) {
        mCurPage = getCurPage(pos);
        mPageView.refreshPage();
    }

    public void updateTime() {
        if (mPageView.isPrepare() && mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * 上报真实翻页节点
     */
    private void uploadFlipPageReal() {
        if (mRecordBook != null) {
            FuncPageStatsApi.readFlipPageReal(StringFormat.parseLong(mRecordBook.bookId, 0), prevPageId, sourceStats);
        }
    }

    /**
     * 免广告后自动跳转下一页
     */
    public void gotoNextPage() {
        // 需要重新计算当前页数
        if (mCurPage != null && mCurPageList.size() > 0) {
            Logger.e(TAG, "有广告前的章节页数:" + mCurPageList.size() + ", 停留在第" + (1 + mCurPage.position) + "页");
            List<TxtPage> oldData = new ArrayList<>(mCurPageList);
            mCurPageList.clear();
            curAdPosList.clear();
            mSupportPageBetweenChapters = false;
            int adPageSize = 0;
            // 移除广告页面
            for (int i = 0; i < oldData.size(); i++) {
                TxtPage item = oldData.get(i);
                if (item.lines != null) {
                    item.position = mCurPageList.size();
                    item.isExtraChapterEnd = false;
                    item.isExtraAfterChapter = false;
                    item.offsetY = 0;
                    mCurPageList.add(item);
                } else if (i < mCurPage.position) {
                    ++adPageSize;
                }
            }
            if (mNextPageList != null) {
                List<TxtPage> nextOldPageList = new ArrayList<>(mNextPageList);
                mNextPageList.clear();
                nextAdPosList.clear();
                for (TxtPage item : nextOldPageList) {
                    if (item.lines != null) {
                        item.position = mNextPageList.size();
                        item.isExtraChapterEnd = false;
                        item.isExtraAfterChapter = false;
                        item.offsetY = 0;
                        mNextPageList.add(item);
                    }
                }
            }

            int bannerPageSize = 0;
            // 减去横幅高度导致的页数变动
            if (/*isFreeTime() && */mDisplayHeight < mScreenHeight) {
                bannerPageSize = (int) ((mCurPage.position - adPageSize) * (mScreenHeight - mDisplayHeight) * 1f / mScreenHeight + 0.90f);
                Logger.e(TAG, "正在免广告, 横幅导致页面变化：" + (bannerPageSize));
            }

            mCurPage.position = mCurPage.position - adPageSize - bannerPageSize;
            mCurPage.isExtraChapterEnd = false;
            mCurPage.isExtraAfterChapter = false;
            Logger.e(TAG, "移除广告后的章节页数:" + mCurPageList.size() + ",当前页前面有"
                    + adPageSize + "个广告" + ", 真实位置是" + mCurPage.position);
            // 隐藏底部banner
            if (mPageChangeListener != null && !mPageChangeListener.hideBottomBanner()) {
                skipToPage(mCurPage.position);
                Logger.e(TAG, "直接跳转页面");
            }
        }
    }

    public void updateBattery(int level) {
        mBatteryLevel = level;
        if (mPageView.isPrepare() && mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    public void setTextSize(int textSize) {
        if (!isBookOpen || mCurPageList == null || mCurPage == null) {
            return;
        }
        mTextSize = textSize;
        mTextInterval = (int) (mTextSize * ReadSettingManager.getInstance().textSpaceArray[textSpaceFactor]);
        mTextPara = (int) (mTextSize * 1.272727f + 2.181818f);
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE);
        mTitleInterval = (int) (mTitleSize * 0.681818f + 1.454545f);
        mTitlePara = (int) (mTitleSize * 1.434343f + 10f);

        mTextPaint.setTextSize(mTextSize);
        mTitlePaint.setTextSize(mTitleSize);
        mSettingManager.setTextSize(mTextSize);

        if (mWeakPrePageList != null && mWeakPrePageList.get() != null) {
            clearEachTxtPage(mWeakPrePageList.get(), false);
        }
        if (mWeakPreAdPosList != null && mWeakPreAdPosList.get() != null) {
            mWeakPreAdPosList.get().clear();
        }
        clearEachTxtPage(mNextPageList, false);
        nextAdPosList.clear();
        mWeakPrePageList = null;
        mWeakPreAdPosList = null;
        mNextPageList = null;
        if (mStatus == STATUS_FINISH) {
            Logger.e(TAG, "字体大小调整，重新加载当前页");
            mPageView.getBgBitmap().isExtraAfterChapter = false;
            mPageView.getBgBitmap().isExtraChapterEnd = false;
            mPageView.getNextPage().isExtraAfterChapter = false;
            mPageView.getNextPage().isExtraChapterEnd = false;
            curAdPosList.clear();
            showAdWithBanner = true;
            mCurPageList = loadPageList(mCurGroupPos, mCurPos);
            showAdWithBanner = false;
            if (mCurPageList != null && mCurPage != null && mCurPage.position >= mCurPageList.size()) {
                mCurPage.position = mCurPageList.size() - 1;
            }
        }
        mCurPage = getCurPage(mCurPage.position);
        mPageView.refreshPage();
    }

    public int getTextContentSize() {
        return mTextSize;
    }

    public int getTextContentColor() {
        return mTextColor;
    }

    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
        if (isNightMode) {
            getAndRecycleVideoImg(R.mipmap.icon_play_night);
            mButtonPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
            mAdTextPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
            mRulePaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
            setTipColor(ReadSettingManager.NIGHT_MODE);
            setBgColor(ReadSettingManager.NIGHT_MODE);
        } else {
            getAndRecycleVideoImg(R.mipmap.ad_play);
            mButtonPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.standard_red_main_color_c1));
            mAdTextPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.white));
            mRulePaint.setColor(mTextColor);
            setTipColor(ReadSettingManager.getInstance().getReadBgTheme());
            setBgColor(ReadSettingManager.getInstance().getReadBgTheme());
        }
        mSettingManager.setNightMode(nightMode);

    }

    private void getAndRecycleVideoImg(int drawableId) {
        if (playBitmap != null && !playBitmap.isRecycled()) {
            playBitmap.recycle();
        }
        playBitmap = BitmapFactory.decodeResource(context.globalContext.getResources(), drawableId);
    }

    /**
     * 翻页动画
     *
     * @param pageMode:翻页模式
     * @see PageMode
     */
    public void setPageMode(PageMode pageMode) {
        /*if (mPageView != null) {
            pageModeChanged = true;
            mPageMode = pageMode;
            mPageView.setPageMode(mPageMode);
            mSettingManager.setPageMode(mPageMode);
            mVideoRectF = null;
            mRuleRect = null;
            // 重新绘制当前页
            if (mPageView != null) {
                if (mPageView.getNextPage() != null && mPageView.getNextPage().rootLayoutForExtra != null) {
                    mPageView.getNextPage().rootLayoutForExtra.setTranslationY(0);
                    mPageView.getNextPage().rootLayoutForExtra.setTranslationX(0);
                }
                if (mPageView.getBgBitmap() != null && mPageView.getBgBitmap().rootLayoutForExtra != null) {
                    mPageView.getBgBitmap().rootLayoutForExtra.setTranslationY(0);
                    mPageView.getBgBitmap().rootLayoutForExtra.setTranslationX(0);
                }
            }
            if (pageMode == PageMode.SCROLL && mPageView.getBgBitmap() != null && mPageView.getBgBitmap().rootLayoutForExtra != null) {
                mPageView.getNextPage().rootLayoutForExtra.setTranslationY(0);
                mPageView.getBgBitmap().rootLayoutForExtra.setTranslationY(0);
                if (mCurPage != null && mCurPage.isExtraAfterChapter) {
                    skipToPage(mCurPage.position - 1);
                } else {
                    mPageView.drawCurPage(false);
                }
            } else {
                mPageView.drawCurPage(false);
            }
        }*/
    }

    public void setTextSpaceMode(int position) {
        if (mPageView != null && textSpaceFactor != position && position < ReadSettingManager.getInstance().textSpaceArray.length) {
            // 判断页码的切换，行距会导致页面改变
            mTextInterval = (int) (mTextSize * ReadSettingManager.getInstance().textSpaceArray[position]);
            float eachTextSize = mTextSize * Math.abs(ReadSettingManager.getInstance().textSpaceArray[textSpaceFactor] - (ReadSettingManager.getInstance().textSpaceArray[position]));
            Logger.e(TAG, "字体行间变化值: " + eachTextSize);
            if (mCurPage != null && mCurPage.position != 0 && mCurPageList != null) {
                float eachPageHeihgt = 0;
                eachPageHeihgt = mCurPageList.get(0).lines.size() * eachTextSize;
                Logger.e(TAG, "每页行距变化值: " + eachPageHeihgt);
                int totalHight = (int) (mCurPage.position * eachPageHeihgt);
                if (textSpaceFactor - position > 0) {
                    lineSpacePage = totalHight / (mVisibleHeight - mTitleSize - mTitleInterval) + 1;
                } else {
                    lineSpacePage = -(totalHight / (mVisibleHeight - mTitleSize - mTitleInterval)) - 1;
                }
            } else {
                lineSpacePage = 0;
            }
            Logger.e(TAG, "总页数变化值: " + lineSpacePage);
            textSpaceFactor = position;
            saveRecord(false);
            isBookOpen = false;
            // 重新绘制所有页面
            showChapterContent();
        }
    }

    private void setTipColor(int theme) {
        if (mTipPaint == null || mBatteryPaint == null) {
            return;
        }
        int[] tipColors = ReadSettingManager.getInstance().colorTip;
        if (isNightMode) {
            mTipPaint.setColor(TIP_COLOR_NIGHT);
            mBatteryPaint.setColor(TIP_COLOR_NIGHT);
        }
//        else if (isNightMode) {
//            mTipPaint.setColor(TIP_COLOR_NIGHT);
//            mBatteryPaint.setColor(TIP_COLOR_NIGHT);
//        }

        else {
            if (theme >= tipColors.length) {
                theme = tipColors.length - 1;
            }

            if (tipColors[theme] == -1 || tipColors[theme] == -2) {
                mTipPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.read_page_tip_06));
                mBatteryPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.read_page_tip_06));
            } else {
                mTipPaint.setColor(ContextCompat.getColor(context.globalContext, tipColors[theme]));
                mBatteryPaint.setColor(ContextCompat.getColor(context.globalContext, tipColors[theme]));
            }
        }
    }

    public void setBgColor(int theme) {
        setTipColor(theme);
        if (theme == ReadSettingManager.NIGHT_MODE) {
            mTextColor = ContextCompat.getColor(context.globalContext, R.color.standard_black_second_level_color_c4);
        } else {
            mTextColor = ContextCompat.getColor(context.globalContext, R.color.read_chapter_content);
        }
        if (mTitlePaint != null) {
            mTitlePaint.setColor(mTextColor);
        }

        if (mTextPaint != null) {
            mTextPaint.setColor(mTextColor);
        }

        int[] colors = ReadSettingManager.getInstance().colorBg;
        if (isNightMode) {
            mPageBg = ContextCompat.getColor(context.globalContext, R.color.black);
        }
//        else if (!isNightMode && theme == ReadSettingManager.NIGHT_DAY) {
//            mBgTheme = mSettingManager.getReadBgTheme();
////            mSettingManager.setReadBackground(theme);
//            mPageBg = ContextCompat.getColor(context.globalContext, colors[mSettingManager.getReadBgTheme()]);
//        }
        else {
            if (theme >= colors.length) {
                theme = colors.length - 1;
            }
            mSettingManager.setReadBackground(theme);
            if (colors[theme] == -1 || colors[theme] == -2) {
                mPageBg = colors[theme];
            } else {
                mPageBg = ContextCompat.getColor(context.globalContext, colors[theme]);
            }
        }

        mPageView.setBgColor(mPageBg);
//        if (mPageMode != null && mPageMode == PageMode.SCROLL && mCurPage != null && ((mPageView.getBgBitmap() != null
//                &&  mPageView.getBgBitmap().isExtraAfterChapter) || (mPageView.getNextPage() != null
//                &&  mPageView.getNextPage().isExtraAfterChapter))) {
//            if (mPageView.getNextPage() != null &&  mPageView.getNextPage().isExtraAfterChapter) {
//                mPageView.getNextPage().isExtraAfterChapter = false;
//                mPageView.getBgBitmap().isExtraAfterChapter = true;
//            } else {
//                mPageView.getNextPage().isExtraAfterChapter = true;
//                mPageView.getBgBitmap().isExtraAfterChapter = false ;
//            }
//            mPageView.refreshPage();
//        } else {
            mPageView.refreshPage();
//        }

    }

    private Bitmap getBgBitmap() {
        if (mPageMode == PageMode.SCROLL || mPageBg == -2) {
            return getScrollBitmap();
        }
        if (bgBitmap == null || bgBitmap.isRecycled()) {
            bgBitmap = BitmapFactory.decodeResource(mPageView.getResources(), R.mipmap.bg_kraft_paper).copy(Bitmap.Config.RGB_565, true);
        }
        return bgBitmap;
    }

    private Bitmap getScrollBitmap() {
        if (scrollBitmap == null || scrollBitmap.isRecycled()) {
            scrollBitmap = BitmapFactory.decodeResource(mPageView.getResources(), R.mipmap.read_yellow_bg).copy(Bitmap.Config.RGB_565, true);
        }
        return scrollBitmap;
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;
        // 进入阅读后就开始预加载
        BaseApplication.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPageChangeListener != null) {
                    mPageChangeListener.preLoadAdFlow();
                }
            }
        }, 500);

    }

    public int getPageStatus() {
        return mStatus;
    }

    public void setPageStatus(int status) {
        mStatus = status;
    }

    public int getPagePos() {
        return mCurPage.position;
    }

    public float getLastPageBlankHeight() {
        if (mCurPageList != null && mCurPageList.size() > 2 && mCurPageList.get(mCurPageList.size() - 1).isExtraChapterEnd) {
            float availableHeight = mVisibleHeight - mCurPageList.get(mCurPageList.size() - 1).offsetY ;
            Logger.e("ad#ReadActivity", "容器高度：  " + mVisibleHeight
                    + ", offsetY ： " + mCurPageList.get(mCurPageList.size() - 1).offsetY + ", availableHeight : " + availableHeight);
            return availableHeight;
        } else {
            return 0;
        }
    }

    public int getCurrGroupPos() {
        return mCurGroupPos;
    }

    public int getCurrChapterPos() {
        return mCurPos;
    }

    /**
     * 保存阅读历史记录.
     *
     * @param isNewChapter 是否为新章节
     */
    public void saveRecord(boolean isNewChapter) {
        if (!isBookOpen || mRecordBook == null || mRecordBook.bookChapterList == null || mRecordBook.bookChapterList.isEmpty()) {
            return;
        }
        List<ChapterBean> curGroup = mRecordBook.bookChapterList.get(mCurGroupPos);
        if (curGroup == null || curGroup.isEmpty()) {
            return;
        }
        if (mCurPos < 0) {
            mCurPos = 0;
        }
        if (mCurPos >= curGroup.size()) {
            mCurPos = curGroup.size() - 1;
        }
        mRecordBook.setSeqNum(mCurPos + curGroup.get(0).seqNum);
        mRecordBook.setChapterTitle(curGroup.get(mCurPos).chapterTitle);
        int adCount = 0;
        if (isNewChapter) {
            mRecordBook.setPagePos(0);
        } else {
            int start = mCurPage.position;
            if (start < 0) {
                mCurPage.position = 0;
                start = 0;
            }
            if (mCurPageList != null && mCurPage.position >= mCurPageList.size()) {
                mCurPage.position = mCurPageList.size() - 1;
                start = mCurPageList.size() - 1;
            }
            for (; start >= 0 && mCurPageList != null; start--) {
                if (mCurPageList.get(start).isExtraAfterChapter) {
                    ++adCount; //
                }
            }
            int bannerPageSize = 0;
            // 减去横幅高度导致的页数变动
            if (/*isFreeTime() && */mDisplayHeight < mScreenHeight) {
                bannerPageSize = (mCurPage.position - adCount) * (mScreenHeight - mDisplayHeight) / mScreenHeight;
                Logger.e(TAG, "正在免广告, 横幅导致页面变化：" + (bannerPageSize));
            }
            // 减去广告页面导致的页面变动
            mRecordBook.setPagePos(mCurPage.position - adCount - bannerPageSize);
        }
        Logger.e(TAG,  "章节名称: " + mCurPage.title + ", 记录的阅读历史位置： " + mCurPage.position + ",真实位置是" + mRecordBook.getPagePos() + ", 前面有" + (adCount)+ "个广告");
        //mRecordBook.setLastRead(StringUtils.dateConvert(System.currentTimeMillis(), ReadConstant.FORMAT_BOOK_DATE));
        mRecordBook.setLastRead(TimeTool.currentTimeMillis());
        if (totalChapter != 0) {
            mRecordBook.setChapterCount(totalChapter);
        }
        //添加阅读历史记录.
        BookRecordHelper.getsInstance().saveRecordBook(mRecordBook, true);
        //将新的阅读记录同步到服务器.
        ReadHistoryMgr.addRecordBook(mRecordBook);
    }

    public boolean isFreeTime() {
        // 判断免广告
        return AdConfigManger.getInstance().isAdFreeTime(Constants.channalCodes[1]);
    }

    public void openBook(BookRecordBean recordBean) {
        if (recordBean == null || recordBean.bookChapterList == null || recordBean.bookChapterList.isEmpty()) {
            return;
        }
        List<List<ChapterBean>> bookChapterList = recordBean.bookChapterList;
        mRecordBook = recordBean;
        int recordSeqNum = mRecordBook.getSeqNum();
        mCurGroupPos = recordSeqNum % 50 == 0 ? recordSeqNum / 50 - 1 : recordSeqNum / 50;
        mLastGroupPos = mCurGroupPos;
        List<ChapterBean> group = bookChapterList.get(mCurGroupPos);
        int firstSeqNum = group.get(0).seqNum;
        mCurPos = recordSeqNum - firstSeqNum;
        if (mCurPos < 0) {
            mCurPos = 0;
        }
        mLastPos = mCurPos;
        preLoadCatalogue();
    }

    public void showChapterContent() {
        mCurPos = Math.max(mCurPos, 0);
        curAdPosList.clear();
        showAdWithBanner = true;
        mCurPageList = loadPageList(mCurGroupPos, mCurPos);
        showAdWithBanner = false;
        if (mCurPageList == null) {
            Logger.e("ReadActivity", "开始渲染,没有章节数据: mCurGroupPos = " + mCurGroupPos + ", mCurPos = " + mCurPos);
            showError();

            FuncPageStatsApi.readLoadFail(Long.parseLong(mBookId),sourceStats);
            ToastUtils.showLimited(R.string.load_failed);
            return;
        }
        Logger.e("ReadActivity", "开始渲染,有章节数据");
        preLoadNextChapter();
        mStatus = STATUS_FINISH;
        if (!isBookOpen) {
            isBookOpen = true;
            //定位到该章上一次打开的页面
            int position = mRecordBook.getPagePos() + lineSpacePage;
            if (position >= mCurPageList.size()) {
                position = mCurPageList.size() - 1;
            } else if (position < 0) {
                position = 0;
            }
            // 清零行距调整导致的界面位置变化
            lineSpacePage = 0;
            Logger.e("ReadActivity", "---------------获取到的的阅读历史位置------------------： " + position);

            mCurPage = getCurPage(position);
            mLastPage = mCurPage;
            if (mPageChangeListener != null) {
                mPageChangeListener.onChapterChange(mCurGroupPos, mCurPos, false);
            }
        } else {
            mCurPage = getCurPage(0);
        }
        if (mPageView != null) {
            mPageView.drawCurPage(false);
        }
        //添加阅读记录.
        saveRecord(false);
    }

    public void showError() {
        mStatus = STATUS_ERROR;
        if (mPageView != null) {
            mPageView.drawCurPage(false);
        }
    }

    public void showLoading(){
        mStatus = STATUS_LOADING;
        if (mPageView != null) {
            mPageView.drawCurPage(false);
        }
    }

    public void closeBook() {
        isBookOpen = false;
        if (mPreLoadDisposable != null) {
            mPreLoadDisposable.dispose();
        }
    }

    @Nullable
    protected abstract List<TxtPage> loadPageList(int groupPos, int chapterPos);

    // 有章节重复加载导致广告位置不准的情况
    List<TxtPage> loadPages(TxtChapter chapter, BufferedReader br, int groupPos, int chapterPos) {
        // 如果发生了跳转或回退则清空之前的累计页数
//        if (chapterPos - 1 != recordPageIndex) {
//            totalPageSize = 0;
//        }
        recordPageIndex = chapterPos;

        List<TxtPage> pages = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        int rHeight = mVisibleHeight;
        int titleLinesCount = 0;
        boolean isTitle = true;
        String paragraph = chapter.getTitle();
        try {
            while (isTitle || (paragraph = br.readLine()) != null) {
                if (!isTitle) {
                    paragraph = paragraph.replaceAll("\\s", "");
                    if (paragraph.equals("")) continue;
                    paragraph = StringUtils.halfToFull("  " + paragraph + "\n");
                } else {
                    rHeight -= mTitlePara;
                }

                int wordCount;
                String subStr;
                while (paragraph.length() > 0) {
                    if (isTitle) {
                        rHeight -= mTitlePaint.getTextSize();
                    } else {
                        rHeight -= mTextPaint.getTextSize();
                    }
                    if (rHeight < 0) {
                        TxtPage page = new TxtPage();
                        page.position = pages.size();
                        page.title = chapter.getTitle();
                        page.lines = new ArrayList<>(lines);
                        page.titleLines = titleLinesCount;
                        pages.add(page);
                        lines.clear();
                        rHeight = mVisibleHeight;
                        titleLinesCount = 0;
                        continue;
                    }

                    if (isTitle) {
                        wordCount = mTitlePaint.breakText(paragraph, true, mVisibleWidth, null);
                    } else {
                        wordCount = mTextPaint.breakText(paragraph, true, mVisibleWidth, null);
                    }

                    subStr = paragraph.substring(0, wordCount);
                    if (!subStr.equals("\n")) {
                        lines.add(subStr);
                        if (isTitle) {
                            titleLinesCount += 1;
                            rHeight -= mTitleInterval;
                        } else {
                            rHeight -= mTextInterval;
                        }
                    }
                    paragraph = paragraph.substring(wordCount);
                }

                if (!isTitle && lines.size() != 0) {
                    rHeight = rHeight - mTextPara + mTextInterval;
                }

                if (isTitle) {
                    rHeight = rHeight - mTitlePara + mTitleInterval;
                    isTitle = false;
                }
            }

            if (lines.size() != 0) {
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = chapter.getTitle();
                page.lines = new ArrayList<>(lines);
                page.titleLines = titleLinesCount;
                Logger.e(TAG, "标题：" + page.title + " -- 章节末广告区域占比： " + (rHeight * 1f / mVisibleHeight));
                pages.add(page);
                lines.clear();

                Logger.e(TAG, "================屏幕高度：" + mScreenHeight + ", 阅读器可用高度：" + mDisplayHeight
                        + ", 文字绘制可用高度：" + mVisibleHeight + ", 最后一页的高度： " + (mVisibleHeight - rHeight));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (pages.size() == 0) {
            TxtPage page = new TxtPage();
            page.lines = new ArrayList<>(1);
            pages.add(page);

            mStatus = STATUS_EMPTY;
        }

        // 如果不显示广告，则主动请求是否可以显示广告
        if (!mSupportPageBetweenChapters) {
            mSupportPageBetweenChapters = AdConfigManger.getInstance().showAd(mActivity, Constants.channalCodes[2]) != null;
            Logger.e("ad#http", "主动检测广告----没有显示广告时-----广告是否可以显示： " + mSupportPageBetweenChapters);
            hasTooBigFirstAdPos = true;
            totalPageSize = 2;
        } else if (!hasPreloadFlowAd && mSupportPageBetweenChapters) { // 翻页次数没达到，即没有预加载时，但是需要加载章节的时候
            mSupportPageBetweenChapters = AdConfigManger.getInstance().showAd(mActivity, Constants.channalCodes[2]) != null;
            Logger.e(TAG, "主动检测广告---没有预加载时----广告是否可以显示： " + mSupportPageBetweenChapters);
        }
        Logger.e(TAG, "加载的章节标题是：" + chapter.getTitle() + ",本章是否可以显示广告： " + mSupportPageBetweenChapters);
//        boolean canShowAd = AdConfigManger.getInstance().showAd(mActivity, Constants.channalCodes[2]) != null;
        if (mSupportPageBetweenChapters/* || canShowAd*/) {
//            if (!mSupportPageBetweenChapters) {
//                hasTooBigFirstAdPos = true;
//                totalPageSize = 1;
//                Logger.e(TAG, "广告从不显示到可以显示，强制第二页显示广告");
//            }
            boolean isPositive = true;
            if (mCurPage != null) {
                List<TxtChapter> list = mChapterList.get(mCurGroupPos);
                // 向后翻页了
                if (list != null && list.size() != 0 && list.get(mCurPos).seqNum > chapter.seqNum) {
                    Logger.e(TAG, "当前章节：" + list.get(mCurPos).seqNum + ", 生成的章节：" + chapter.seqNum);
                    isPositive = false;
                }
            }
            if (isPositive) {
                insertNextChapterAd(pages);
            } else {
                insertPrevChapterAd(pages);
            }

            if (pages.size() > 1) {
                if (rHeight >= mVisibleHeight * 0.35f) {
                    TxtPage page = pages.get(pages.size() - 1);
                    page.isExtraChapterEnd = true;
                    page.offsetY = mVisibleHeight - rHeight + ViewUtils.dp2px(20);
                    Logger.e("ad#ReadActivity", chapter.getTitle() + ", 最后一章节剩余高度 ： " + rHeight);
                    pages.set(page.position, page);
                }
                Logger.e(TAG, "================屏幕高度：" + mScreenHeight + ", 阅读器可用高度：" + mDisplayHeight
                        + ", 文字绘制可用高度：" + mVisibleHeight + ", 最后一页的高度： " + (mVisibleHeight - rHeight));
            }
        }

//        mSupportPageBetweenChapters = canShowAd;
        boolean isLastChapter = isLastChapter(groupPos, chapterPos);
        if (mSupportPageAfterLastChapter && isLastChapter) {
            if (pages.get(pages.size() - 1).isExtraAfterChapter) {
                pages.remove(pages.size() - 1);
            }
//            TxtPage page = new TxtPage();
//            page.isExtraAfterBook = true;
//            page.title = pages.get(pages.size() - 1).title;
//            page.position = pages.size();
//            pages.add(page);
            pages.get(pages.size() - 1).isExtraAfterBook = true;
        }
        // 章节末添加广告位置
        if (pages.size() > 1 && pages.get(pages.size() - 1).isExtraChapterEnd) {
            if (!isBookOpen || showAdWithBanner) {
                curAdPosList.add(pages.size() - 1);
                Logger.e(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$$ 章节末广告添加到当前页list $$$$$$$$$$$$$$$$$$$$$$$$$$$");
            } else {
                nextAdPosList.add(pages.size() - 1);
                Logger.e(TAG, "$$$$$$$$$$$$$$$$$$$$$$$$$$$ 章节末广告添加到下一页list $$$$$$$$$$$$$$$$$$$$$$$$$$$");
            }
        }

        return pages;
    }

    /**
     * 获取历史位置前的广告位置，倒序的！！！
     * @param historyPos 真实页面位置
     * @return
     */
    private List<Integer> getHistoryPreAdPos(int historyPos) {
        int adPosGen = genPageIntervalCnt();
        List<Integer> posList = new ArrayList<>();

        while (historyPos > 0 && historyPos > adPosGen) {
            historyPos -= adPosGen;
            posList.add(historyPos);
            Logger.e(TAG, "广告历史位置前的位置: " + historyPos);
            adPosGen = genPageIntervalCnt();
        }

        return posList;
    }

    /**
     * 获取一下广告位置
     * @return
     */
    private boolean addNextAdPos(int pageSize, int lastAdPos, int history) {
        int adIntervel = 0;
        if (!isBookOpen && lastAdPos == 0) {
            adIntervel = firstAdPageMax;
        } else {
            adIntervel = maxPageIntervalAd;
        }
        // 优先处理之前留下的广告剩余页
        if (hasTooBigFirstAdPos) {
            if (pageSize == totalPageSize) {
                totalPageSize = 0;
                hasTooBigFirstAdPos = false;
                maxPageIntervalAd = genPageIntervalCnt();
                Logger.e(TAG, "------------------------广告位置： " + pageSize + ", 有大数据");
                return true;
            } else {
                return false;
            }
        } else {
            if (isBookOpen) {
                adIntervel = Math.max(adIntervel - totalPageSize, 1);
            }
            if (lastAdPos == 0) {
                lastAdPos = history;
            }
            if (pageSize == lastAdPos + adIntervel) {
                totalPageSize = 0;
                maxPageIntervalAd = genPageIntervalCnt();
                Logger.e(TAG, "------------------------广告位置： " + pageSize + ", 没有大数据");
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     *
     * @param isFirstLoad
     * @param pageSize
     * @param lastAdPos
     * @param historyPos
     */
    private void processPageEndAd(boolean isFirstLoad, int pageSize, int lastAdPos, int historyPos) {
        if (isFirstLoad) {
            if (lastAdPos <= 0) { // 第一次进入没有广告位置，间隔过大
                hasTooBigFirstAdPos = true;
                totalPageSize = firstAdPageMax - (pageSize - historyPos);
            } else {
                hasTooBigFirstAdPos = false;
                totalPageSize = pageSize - lastAdPos;
            }
        } else { // 不是第一次加载
            if (hasTooBigFirstAdPos) {
                if (lastAdPos <= 0) {
                    totalPageSize = totalPageSize - (pageSize - historyPos);
                } else {
                    hasTooBigFirstAdPos = false;
                    totalPageSize = pageSize - lastAdPos;
                }
            } else {
                if (lastAdPos <= 0) { // 没有大数据，却没有广告，广告间隔过大
                    totalPageSize = maxPageIntervalAd - (pageSize - historyPos);
                } else {
                    totalPageSize = pageSize - lastAdPos;
                }
            }
        }
        totalPageSize = Math.max(totalPageSize, 1);
        Logger.e(TAG, "广告剩余页数: " + totalPageSize);
    }

    private boolean addAdPosition(List<TxtPage> pages, int historyPos) {
        if (!mSupportPageBetweenChapters) {
            return false;
        }

        // 判断是否是第一次进入阅读器
        int firstAdGen = 0;
        if (firstAdPageMax != 0) { // 书籍第一次进入
            firstAdGen = firstAdPageMax;
            Logger.e(TAG, "书籍历史位置是: " + (historyPos + 1));
        }

        // 判断历史位置
        if (historyPos > 0) {
            if (firstAdGen <= 0) {
                if (historyPos >= pages.size()) { // 历史位置前面的广告已处理，开始处理历史位置后的广告

                } else { // 处理历史位置前的广告

                }

                if (historyPos < maxPageIntervalAd) {
                    totalPageSize = historyPos + genPageIntervalCnt();
                    return false;
                } else { // 可以插入广告
                    if (historyPos == pages.size()) {
                        totalPageSize = pages.size() + genPageIntervalCnt();
                        return false;
                    } else if (historyPos < pages.size()) {
                        if (totalPageSize == pages.size()) {
                            totalPageSize = pages.size() + genPageIntervalCnt();
                            hasTooBigFirstAdPos = false;
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        totalPageSize = 0;
                        if ((historyPos - pages.size()) % maxPageIntervalAd == 0) {
                            hasTooBigFirstAdPos = false;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                // 先处理历史位置之前的广告位
                if (historyPos == pages.size()) {
                    totalPageSize = pages.size() + firstAdGen;
                    return true;
                } else if (historyPos < pages.size()) {
                    if (totalPageSize == pages.size()) {
                        totalPageSize = pages.size() + genPageIntervalCnt();
                        hasTooBigFirstAdPos = false;
                        firstAdPageMax = 0;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if ((historyPos - pages.size()) % maxPageIntervalAd == 0) {
                        hasTooBigFirstAdPos = false;
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            // 不是第一次加载章节
            if (firstAdGen <= 0) {
                if (totalPageSize == pages.size()) {
                    totalPageSize = genPageIntervalCnt() + pages.size();
                    hasTooBigFirstAdPos = false;
                    return true;
                } else {
                    return false;
                }
            } else { // 第一次加载，且历史位置是0
                if (firstAdGen == pages.size()) {
                    totalPageSize = genPageIntervalCnt() + pages.size();
                    hasTooBigFirstAdPos = false;
                    firstAdPageMax = 0;
                    return true;
                } else {
                    totalPageSize = firstAdGen;
                    return false;
                }
            }
        }
    }

    public TxtChapter getCurrChapter() {
        if (mChapterList != null && !mChapterList.isEmpty() && mCurPageList != null && !mCurPageList.isEmpty()) {
            if (mCurGroupPos >= mChapterList.size()) {
                mCurGroupPos = mChapterList.size() - 1;
            }
            List<TxtChapter> groupList = mChapterList.get(mCurGroupPos);
            if (mCurPos >= groupList.size()) {
                mCurPos = groupList.size() - 1;
            }

            return groupList.get(mCurPos);
        }
        return null;
    }

    protected boolean isLastChapter(int groupPos, int chapterPos) {
        List<List<ChapterBean>> groups = mRecordBook.bookChapterList;
        if (groups == null || groups.isEmpty()) {
            return false;
        }
        List<ChapterBean> chapters = groups.get(groupPos);
        if (groupPos == groups.size() - 1 && chapterPos == chapters.size() - 1) {
            return true;
        }
        return false;
    }

    private int getHistoryPosition() {
        int position = mRecordBook.getPagePos();
        if (mCurPageList != null && position >= mCurPageList.size()) {
            position = mCurPageList.size() - 1;
        }
        return position;
    }

    private void insertNextChapterAd(List<TxtPage> pages) {
        ArrayList<TxtPage> txtPages = new ArrayList<>();
        List<Integer> preMaxPageIntervals = new ArrayList<>();

        // 判断是否是第一次进入阅读器
        int firstAdGen = 0;
        int historyPos = 0;
        if (!isBookOpen) { // 书籍第一次进入
            historyPos = getHistoryPosition() + 1;
            float bannerCount = (mScreenHeight - mDisplayHeight) * (historyPos - 1) * 1f / mDisplayHeight;
            historyPos += (int) (bannerCount + 0.10f);

            firstAdGen = firstAdPageMax;
            Logger.e(TAG, "---------------------内容解析-----首次进入的位置--------------：" + (historyPos));
        } else if (showAdWithBanner && mCurPage != null) {
            historyPos = mCurPage.position + genPageIntervalCnt();
        } else {
            Logger.e(TAG, "--------------------下一章节内容解析--------------");
        }
        boolean canAddLastOne = true;
        int betweenPos = historyPos + firstAdGen;
        maxPageIntervalAd = genPageIntervalCnt();
        if (betweenPos > 0) {
            int tmp = betweenPos;
            // firstAdGen很小置，测试通过
            if (tmp < pages.size() - 1) {
                Logger.e(TAG, "历史位置不是1， 第一个出现的广告位置： " + tmp);

                preMaxPageIntervals.add(tmp);
                // 处理历史位置之前的广告显示
                int prevHistory = historyPos;
                while (prevHistory > maxPageIntervalAd) {
                    prevHistory -= maxPageIntervalAd;
                    preMaxPageIntervals.add(prevHistory);
                    Logger.e(TAG, "当前位置前面的--广告位置： " + prevHistory);
                    maxPageIntervalAd = genPageIntervalCnt();
                }

                hasTooBigFirstAdPos = false;
                // 倒叙
                if (preMaxPageIntervals.size() > 0) {
                    Collections.reverse(preMaxPageIntervals);
                }

                // 处理第一个广告位置后面的广告
                while (pages.size() - betweenPos > maxPageIntervalAd) {
                    betweenPos += maxPageIntervalAd;
                    preMaxPageIntervals.add(betweenPos);
                    maxPageIntervalAd = genPageIntervalCnt();
                    Logger.e(TAG, "当前位置后面的--广告位置： " + betweenPos);
                }
                // 保留没有处理到到书籍页数
                totalPageSize = pages.size() - betweenPos;
//                --totalPageSize;
            } else { // firstAdGen很大，或者historyPos很大时
                totalPageSize = firstAdGen - (pages.size() - historyPos);
//                --totalPageSize;
                hasTooBigFirstAdPos = true; // firstAdGen过大，当前页不显示广告
                Logger.e(TAG, "本章不显示广告，historyPos + firstAdGen = " + betweenPos + " 过大， pages.size() = " + pages.size());

                // 处理历史位置之前的广告显示
                int prevHistory = historyPos;
                while (prevHistory > maxPageIntervalAd) {
                    prevHistory -= maxPageIntervalAd;
                    preMaxPageIntervals.add(prevHistory);
                    Logger.e(TAG, "位置过大，当前位置前的--广告位置： " + prevHistory);
                    maxPageIntervalAd = genPageIntervalCnt();
                }

                // 倒叙
                if (preMaxPageIntervals.size() > 0) {
                    Collections.reverse(preMaxPageIntervals);
                }
            }
        } else {
            if (hasTooBigFirstAdPos) {
                if (totalPageSize >= pages.size() - 1){
                    Logger.e(TAG, "本章不显示广告，上次遗留下来到广告页数还是过大 ： " + totalPageSize + "， pages.size() = " + pages.size());
                    totalPageSize -= pages.size();
                } else {
                    if (totalPageSize <= 0) {
                        Logger.e(TAG, "上次应在章节末尾显示的，放到下个章节第二页来显示");
                        totalPageSize = 1;
                        canAddLastOne = false;
                    }
                    int tmp = totalPageSize;
                    preMaxPageIntervals.add(tmp);
                    Logger.e(TAG, "有过大的数据，直接加入的--广告位置： " + tmp);
//                    int adSize = 1;
                    while (tmp + maxPageIntervalAd < pages.size() - 1/* + adSize*/) {
                        tmp += maxPageIntervalAd;
                        preMaxPageIntervals.add(tmp);
                        maxPageIntervalAd = genPageIntervalCnt();
                        Logger.e(TAG, "有过大的数据，加入后的--广告位置： " + tmp);
//                        ++adSize;
                    }
                    totalPageSize = pages.size() - tmp;
                    hasTooBigFirstAdPos = false;
                }
            } else {
                if (totalPageSize < 0) {
                    totalPageSize = 1;
                    canAddLastOne = false;
                }
                int tmp = totalPageSize - 1;

                tmp = Math.abs(maxPageIntervalAd - tmp) <= 1 ? 1 : Math.abs(maxPageIntervalAd - tmp);
                if (tmp >= pages.size() - 1) { // 上次遗留下来的广告页数大于了当前章节的页数,当前章节数比较小会出现
                    totalPageSize = tmp - pages.size();
                    hasTooBigFirstAdPos = true;
                } else {
                    preMaxPageIntervals.add(tmp);
                    Logger.e(TAG, "没有大数据--广告位置： " + tmp);
//                    int adSize = 1;
                    while (tmp + maxPageIntervalAd < pages.size() - 1/* + adSize*/) {
                        tmp += maxPageIntervalAd;
                        preMaxPageIntervals.add(tmp);
                        maxPageIntervalAd = genPageIntervalCnt();
                        Logger.e(TAG, "没有大数据,后面的广告--广告位置： " + tmp);
//                        ++adSize;
                    }
                    totalPageSize = pages.size() - tmp;
                    hasTooBigFirstAdPos = false;
                }
            }
        }

        int pageIndex = 0;
        int adIndex = 0;
        // 插入到历史位置前面到广告页数
        int historyPrevAdSize = 0;
        List<Integer> adPosList = null;
        // 首次加载和横幅等导致的阅读器高度变化
        if (!isBookOpen || showAdWithBanner) {
            curAdPosList.clear();
            adPosList = curAdPosList;
            Logger.e(TAG, "广告位置插入当前广告list");
        } else {
            nextAdPosList.clear();
            adPosList = nextAdPosList;
            Logger.e(TAG, "广告位置插入下一个广告list");
        }
        for (TxtPage page : pages) {
            if (adIndex < preMaxPageIntervals.size() && pageIndex == preMaxPageIntervals.get(adIndex)) {
                TxtPage page1 = new TxtPage();
                page1.isExtraAfterChapter = true;
                page1.title = page.title;
                page1.position = txtPages.size();
                txtPages.add(page1);

                if (historyPos > pageIndex) {
                    ++historyPrevAdSize;
                } else {
                    ++adIndex;
                }
                Logger.e(TAG, "$$$$$$$$$$&$$$$$$$$$ 添加的广告位置 ： " + page1.position + " &&&&&&&&&$$$$$$$$$$$&&&");

                // 记录真实广告的出现位置
                if (adPosList != null) {
                    adPosList.add(page1.position);
                }
            }
            page.position = txtPages.size();
            txtPages.add(page);
            ++pageIndex;
        }
        pages.clear();
        pages.addAll(txtPages);
        txtPages.clear();
        txtPages = null;
        preMaxPageIntervals.clear();
        preMaxPageIntervals = null;

        if (canAddLastOne && adIndex > 0) {
            ++totalPageSize;
            Logger.e(TAG, "-----------------广告剩余页数加一了----------" );
        }
        if (totalPageSize < 1) {
            totalPageSize = 1;
        }
        Logger.e(TAG, "本次广告剩余页数： " + totalPageSize);

        // 处理变动过的阅读位置
        if (!isBookOpen || showAdWithBanner) {
            // 计算横幅高度导致的页面变动
            int bannerPageSize = 0;
            if (historyPos > 1 && mSupportPageBetweenChapters && mDisplayHeight < mScreenHeight) {
                // (mCurPage.position - adCount) * (mScreenHeight - mDisplayHeight) / mScreenHeight;
                float bannerCount = (mScreenHeight - mDisplayHeight) * (historyPos - 1 - historyPrevAdSize) * 1f / mDisplayHeight;
                bannerPageSize = (int) (bannerCount + 0.10f);
                Logger.e(TAG, "---------------bannerCount = " + bannerCount);
            }
            Logger.e(TAG, "历史定位是: " + historyPos + ", 前面有广告: " + historyPrevAdSize + ", 横幅导致页数变动：" + bannerPageSize);
            mRecordBook.setPagePos(mRecordBook.getPagePos() + historyPrevAdSize + bannerPageSize);
            if (showAdWithBanner && mCurPage != null) {
                mCurPage.position += historyPrevAdSize + bannerPageSize;
            }
        }
    }

    private void insertPrevChapterAd(List<TxtPage> pages) {
        ArrayList<TxtPage> txtPages = new ArrayList<>();
        List<Integer> preMaxPageIntervals = new ArrayList<>();

        // 当前章节的翻页次数
        int genInterval = genPageIntervalCnt();
        int savedAdPageSize = genInterval + readPageSize;
        Logger.e(TAG, "滑动手势页数：" + readPageSize);
        if (savedAdPageSize <= 0) {
            savedAdPageSize = 1;
            Logger.e(TAG, "次数过大，校正后：" + savedAdPageSize);
        } else {
            if (savedAdPageSize == genInterval - 1) {
                savedAdPageSize += 1;
                Logger.e(TAG, "-------------比随机数小2了----------- ：" + savedAdPageSize);
            } else {
                savedAdPageSize += 1;
            }
            Logger.e(TAG, "次数过小，校正后：" + savedAdPageSize);
        }
        int lastAdPagePos = savedAdPageSize;
        while (true) {
            int genValue = genPageIntervalCnt();
            if (lastAdPagePos + genValue > pages.size()) {
                break;
            }
            Logger.e(TAG, "倒着翻页产生的随机数：" + genValue);
            lastAdPagePos += genValue;
            preMaxPageIntervals.add(genValue);
        }
        preMaxPageIntervals.add(savedAdPageSize);
        Logger.e(TAG, "倒着翻页产生的随机数：" + savedAdPageSize);
        int totalPageSize = pages.size();
//        Logger.e(TAG, " 最后一个广告位置是： " + lastAdPagePos + "，广告总页数 : " + totalPageSize + ", 章节内容页数：" + pages.size());
        // 首次加载和横幅等导致的阅读器高度变化
        List<Integer> adPosList = new ArrayList<>();
        if (curAdPosList.isEmpty() && (!isBookOpen || showAdWithBanner)) {
            adPosList = curAdPosList;
            Logger.e(TAG, "广告位置插入当前广告list");
        } else {
            adPosList = nextAdPosList;
            Logger.e(TAG, "广告位置插入下一个广告list");
        }
        int adIndex = 0;
        int index = 0;
        for (TxtPage page : pages) {
            // 向后翻页, 倒数出现的位置关联readPageSize值
//            if (lastAdPagePos != 0 && totalPageSize + adIndex - lastAdPagePos == txtPages.size()) {
            if (index == totalPageSize - lastAdPagePos) {
                // 广告不出现在第一页
                boolean isFirstIndex = false;
                if (txtPages.size() == 0) {
                    page.position = txtPages.size();
                    txtPages.add(page);
                    isFirstIndex = true;
                }
                TxtPage page1 = new TxtPage();
                page1.isExtraAfterChapter = true;
                page1.title = page.title;
                page1.position = txtPages.size();
                txtPages.add(page1);
                adPosList.add(page1.position);
                Logger.e(TAG, "-----产生的广告位置：" + page1.position);
                if (adIndex < preMaxPageIntervals.size()) {
                    lastAdPagePos -= preMaxPageIntervals.get(adIndex++);
                }
                if (isFirstIndex) {
                    continue;
                }
            }
            page.position = txtPages.size();
            txtPages.add(page);
            ++index;
        }
        pages.clear();
        pages.addAll(txtPages);

        txtPages.clear();
        txtPages = null;
        preMaxPageIntervals.clear();
        preMaxPageIntervals = null;
    }

    private int genPageIntervalFirst() {
        int genValue = 8;
        try {
            int min = firstShowAdMin;
            int max = firstShowAdMax;
            if (max - min != 0) {
                genValue = mRandom.nextInt(max - min) + min;
            } else {
                genValue = min;
            }
        } catch (Exception ex) {
            Logger.i(TAG, "genPageIntervalFirst--随机数产生错误：" + ex.getMessage());
        }
        Logger.i(TAG, "产生的第一个随机数是：" + genValue);
        return genValue;
    }

    //区分 颜色和图片   颜色和图片函数分开，预留后期不同处理
    void onDraw(Layer layer, boolean isUpdate, boolean isBitmap) {
        if (isBitmap) {
            drawBitmapBackground(mPageView.getBgBitmap(), isUpdate);
        } else {
            drawBackground(mPageView.getBgBitmap(), isUpdate);
        }
        if (!isUpdate || pageModeChanged) {
            drawContent(layer);
        }
        pageModeChanged = false;
        mPageView.invalidate();
    }


    //记载图片  可能有代码冗余 后期重构
    private void drawBitmapBackground(Layer layer, boolean isUpdate) {
        Canvas canvas = new Canvas(layer.bitmap);
        if (!isUpdate || pageModeChanged) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            RectF rectF = new RectF(0, 0, mDisplayWidth, mDisplayHeight);
            canvas.drawBitmap(getBgBitmap(), null, rectF, null);
            float tipTop = tipMarginHeight - mTipPaint.getFontMetrics().top;
            int visibleRight = mDisplayWidth - mMarginWidth;
            if (mStatus != STATUS_FINISH) {
                if (mChapterList != null && mChapterList.size() != 0 && mCurPos < mChapterList.size()) {
                    List<TxtChapter> list = mChapterList.get(mCurGroupPos);
                    if (list != null && list.size() != 0) {
                        String title = list.get(mCurPos).getTitle();
                        if (!TextUtils.isEmpty(title)) {
                            if (title.length() > 15) {
                                title = title.substring(0, 13) + "...";
                            }
                            float x = visibleRight - mTipPaint.measureText(title);
                            canvas.drawText(title, x, tipTop, mTipPaint);
                        }
                    }
                }
            } else if (mCurPage != null) {
                String title = mCurPage.title;
                if (!TextUtils.isEmpty(title)) {
                    if (title.length() > 15) {
                        title = title.substring(0, 13) + "...";
                    }
                    float x = visibleRight - mTipPaint.measureText(title);
                    canvas.drawText(title, x, tipTop, mTipPaint);
                }
            }
            if (mRecordBook != null) {
                String bookName = mRecordBook.bookName;
                if (bookName.length() > 14) {
                    bookName = bookName.substring(0, 13) + "...";
                }
                canvas.drawText(bookName, mMarginWidth, tipTop, mTipPaint);
            }
            float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
            if (mStatus == STATUS_FINISH && mCurPage != null && mCurPageList != null) {
//                String percent = (mCurPage.position + 1) + "/" + mCurPageList.size();
//                canvas.drawText(percent, mMarginWidth, y, mTipPaint);
                String percent = "";
//                if (mLastPage == null || mPageView.isCurPosition()) {
                    percent = (mCurPage.position + 1) + "/" + mCurPageList.size();
//                } else {
//                    if (mLastPage.getTitle().equalsIgnoreCase(mCurPage.getTitle())) {
//                        percent = (mLastPage.position + 1) + "/" + mCurPageList.size();
//                    } else {
//                        if (mNextPageList == null) {
//                            percent = (mCurPageList.size()) + "/" + mCurPageList.size();
//                            Logger.e(TAG, "还没有拿到下一章节数据");
//                        } else {
//                            percent = (mLastPage.position + 1) + "/" + mNextPageListSize;
//                        }
//                    }
//                }
                canvas.drawText(percent, mMarginWidth, y, mTipPaint);
            }
            if (mCurPage != null && mCurPage.isExtraAfterChapter) {
                drawAdTip(canvas, tipMarginHeight);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.saveLayer(mDisplayWidth - Utils.dp2px(90), mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight, mBgPaint);
                RectF rectF = new RectF(0, 0, mDisplayWidth, mDisplayHeight);
                canvas.drawBitmap(getBgBitmap(), null, rectF, null);
                canvas.restore();
            } else {
                canvas.saveLayer(mDisplayWidth  - Utils.dp2px(90), mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight, mBgPaint, Canvas.ALL_SAVE_FLAG);
                RectF rectF = new RectF(0, 0, mDisplayWidth, mDisplayHeight);
                canvas.drawBitmap(getBgBitmap(), null, rectF, null);
                canvas.restore();
            }
        }
        int outFrameLeft = drawBattery(canvas, tipMarginHeight);
        drawTime(canvas, tipMarginHeight, outFrameLeft);
    }

    //加载颜色 可能有代码冗余 后期重构
    private void drawBackground(Layer layer, boolean isUpdate) {
        Canvas canvas = new Canvas(layer.bitmap);
        if (!isUpdate || pageModeChanged) {
            canvas.drawColor(mPageBg);
            float tipTop = tipMarginHeight - mTipPaint.getFontMetrics().top;

            int visibleRight = mDisplayWidth - mMarginWidth;
            if (mStatus != STATUS_FINISH) {
                if (mChapterList != null && mChapterList.size() != 0 && mCurPos < mChapterList.size()) {
                    List<TxtChapter> list = mChapterList.get(mCurGroupPos);
                    if (list != null && list.size() != 0) {
                        String title = list.get(mCurPos).getTitle();
                        if (!TextUtils.isEmpty(title)) {
                            if (title.length() > 15) {
                                title = title.substring(0, 13) + "...";
                            }
                            float x = visibleRight - mTipPaint.measureText(title);
                            canvas.drawText(title, x, tipTop, mTipPaint);
                        }
                    }
                }
            } else if (mCurPage != null) {
                String title = mCurPage.title;
                if (!TextUtils.isEmpty(title)) {
                    if (title.length() > 15) {
                        title = title.substring(0, 13) + "...";
                    }
                    float x = visibleRight - mTipPaint.measureText(title);
                    canvas.drawText(title, x, tipTop, mTipPaint);
                }
            }
            if (mRecordBook != null) {
                String bookName = mRecordBook.bookName;
                if (!TextUtils.isEmpty(bookName)) {
                    if (bookName.length() > 14) {
                        bookName = bookName.substring(0, 13) + "...";
                    }
                    canvas.drawText(bookName, mMarginWidth, tipTop, mTipPaint);
                }

            }
            float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
            if (mCurPage != null) {
                Logger.e(TAG, "mCurPage.position = " + (mCurPage.position + 1));
            }
            if (mStatus == STATUS_FINISH && mCurPage != null && mCurPageList != null) {
                String percent = "";
//                if (mLastPage == null || mPageView.isCurPosition()) {
                    percent = (mCurPage.position + 1) + "/" + mCurPageList.size();
//                } else {
//                    if (mLastPage.getTitle().equalsIgnoreCase(mCurPage.getTitle())) {
//                        percent = (mLastPage.position + 1) + "/" + mCurPageList.size();
//                    } else {
//                        percent = (mLastPage.position + 1) + "/" + mNextPageListSize;
//                    }
//                }
                canvas.drawText(percent, mMarginWidth, y, mTipPaint);
            }

            if (mCurPage != null && mCurPage.isExtraAfterChapter) {
                drawAdTip(canvas, tipMarginHeight);
            }
        } else {
            mBgPaint.setColor(mPageBg);
            canvas.drawRect(mDisplayWidth - Utils.dp2px(90), mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight, mBgPaint);
        }

        int outFrameLeft = drawBattery(canvas, tipMarginHeight);
        drawTime(canvas, tipMarginHeight, outFrameLeft);
    }

    private void drawAdTip(Canvas canvas, int tipMarginHeight) {
        String adTip = context.globalContext.getResources().getString(R.string.read_ad_tip);
        float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
        canvas.drawText(adTip, (mDisplayWidth - mTipPaint.measureText(adTip)) / 2, y, mTipPaint);
    }

    private void drawTime(Canvas canvas, int tipMarginHeight, int outFrameLeft) {
        float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
        String time = StringUtils.dateConvert(System.currentTimeMillis(), ReadConstant.FORMAT_TIME);
        float x = outFrameLeft - mTipPaint.measureText(time) - ScreenUtils.dpToPx(4);
        canvas.drawText(time, x, y, mTipPaint);
    }

    private int drawBattery(Canvas canvas, int tipMarginHeight) {
        int visibleRight = mDisplayWidth - mMarginWidth;
        int visibleBottom = mDisplayHeight - tipMarginHeight;
        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();
        int polarHeight = ScreenUtils.dpToPx(6);
        int polarWidth = ScreenUtils.dpToPx(2);
        int border = 1;
        int innerMargin = 1;
        int polarLeft = visibleRight - polarWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ScreenUtils.dpToPx(2));
        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(polar, mBatteryPaint);
        int outFrameLeft = polarLeft - outFrameWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);
        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(border);
        canvas.drawRect(outFrame, mBatteryPaint);
        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);
        mBatteryPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(innerFrame, mBatteryPaint);
        return outFrameLeft;
    }

    private void drawContent(Layer layer) {
        Canvas canvas = new Canvas(layer.bitmap);
        /*if (mPageMode == PageMode.SCROLL) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (mPageBg == -2) {
                RectF rectF = new RectF(0, 0, mDisplayWidth, mDisplayHeight);
                canvas.drawBitmap(getScrollBitmap(), null, rectF, null);
            } else {
                canvas.drawColor(mPageBg);
            }
        }*/
        if (mCurPage != null && mCurPage.isExtraAfterBook) {
//            layer.isExtraAfterBook = false;
//            layer.isExtraAfterChapter = false;
//            mPageChangeListener.onDrawPageAfterLastChapter(canvas, mTextColor, isNightMode, layer.rootLayoutForExtra);
//            return;
        }
        if (mCurPage != null && mCurPage.isExtraChapterEnd) {
            hasChapterEndLoaded = false;
            layer.isExtraAfterBook = false;
            layer.isExtraChapterEnd = true;
            layer.isExtraAfterChapter = false;
//            if (hasPreloadFlowAd) {
//                clearPreloadAd();
//                readPageSize = showAdMin;
//            }
//            layer.offsetY = mCurPage.offsetY;
            clearPreloadAd();
        } else if (mCurPage != null && mCurPage.isExtraAfterChapter && mPageChangeListener != null) {
            Logger.e(TAG, "当前页是广告");
            layer.isExtraAfterBook = false;
            layer.isExtraAfterChapter = true;
            layer.isExtraChapterEnd = false;
            layer.offsetY = 0;
            mPageChangeListener.onDrawInfoFlowAd(layer.rootLayoutForExtra,
                    (mCurPageList != null ? (mCurPageList.size() - 1) : 0), mCurPage.position);
            clearInfoFlowData();
            clearPreloadAd();
            mVideoRectF = null;
            mRuleRect = null;
            hasChapterEndLoaded = false;
            return;
        } else {
            layer.isExtraAfterBook = false;
            layer.isExtraAfterChapter = false;
            layer.isExtraChapterEnd = false;
            layer.offsetY = 0;
        }

        if (mStatus != STATUS_FINISH) {
            layer.isExtraAfterChapter = false;
            layer.isExtraAfterBook = false;
            layer.isExtraChapterEnd = false;
            layer.offsetY = 0;
            drawTips(canvas);
        } else {
            float top;
            top = mMarginHeight - mTextPaint.getFontMetrics().top;
            int interval = mTextInterval + (int) mTextPaint.getTextSize();
            int para = mTextPara + (int) mTextPaint.getTextSize();
            int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
            int titlePara = mTitlePara + (int) mTextPaint.getTextSize();
            String str = "";

            for (int i = 0; mCurPage.lines != null && i < mCurPage.titleLines; ++i) {
                str = mCurPage.lines.get(i);
                if (i == 0) {
                    top += mTitlePara;
                }
                int start = mMarginWidth;
                canvas.drawText(str, start, top, mTitlePaint);

                if (i == mCurPage.titleLines - 1) {
                    top += titlePara;
                } else {
                    top += titleInterval;
                }
            }
            if (mCurPage.lines == null) {
                Logger.e(TAG,  "书籍内容是空");
                layer.offsetY = 0;
                return;
            }

            for (int i = mCurPage.titleLines; mCurPage.lines != null && i < mCurPage.lines.size(); ++i) {
                str = mCurPage.lines.get(i);
                canvas.drawText(str, mMarginWidth, top, mTextPaint);
                if (str.endsWith("\n")) {
                    top += para;
                } else {
                    top += interval;
                }
            }
            if (layer.isExtraChapterEnd) {
                if (str.endsWith("\n")) {
                    layer.offsetY = (int) (top - para + ViewUtils.dp2px(20));
                } else {
                    layer.offsetY = (int) (top - interval + ViewUtils.dp2px(20));
                }
//                canvas.drawLine(0, layer.offsetY, mVisibleWidth, layer.offsetY, mTextPaint);
                mPageChangeListener.showChapterEndAd(layer.rootLayoutForExtra, mCurPage.position, layer.offsetY);
                Logger.e("ad#ReadActivity", "章节末可以显示广告,偏移量： " + layer.offsetY);
            } else {
                layer.offsetY = 0;
            }
//            drawVideoAd(canvas, top);
//            if (showVideo) {
//                mPageChangeListener.onDrawPageAfterChapter(true, maxLine, mCurPage.lines.size(), layer.rootLayoutForExtra);
//            }
        }
    }

    private void drawTips(Canvas canvas) {
        String tip = "";
        switch (mStatus) {
            case STATUS_LOADING:

                tip = ViewUtils.getString(R.string.loading);
                break;
            case STATUS_ERROR:
                tip = "章节内容获取失败";
                if (mPageChangeListener != null) {
                    mPageChangeListener.showRetryView();
                }
                break;
            case STATUS_EMPTY:
                tip = ViewUtils.getString(R.string.no_content);
                break;
            case STATUS_PARSE:
                tip = ViewUtils.getString(R.string.parsing);
                break;
            case STATUS_PARSE_ERROR:
                tip = ViewUtils.getString(R.string.parsing_failed);
                break;
            default:
                tip = "";
                break;
        }

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textHeight = fontMetrics.top - fontMetrics.bottom;
        float textWidth = mTextPaint.measureText(tip);
        float pivotX = (mDisplayWidth - textWidth) / 2;
        float pivotY = (mDisplayHeight - textHeight) / 2;
        /*if (mPageMode == PageMode.SCROLL) {
            pivotY -= mMarginHeight;
        }*/

        canvas.drawText(tip, pivotX, pivotY, mTextPaint);
    }

    void setDisplaySize(int w, int h) {
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;
        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

        // 重置 PageMode
        setPageMode(mPageMode);

        if (isBookOpen && mStatus == STATUS_FINISH) {
            clearEachTxtPage(mCurPageList, true);
            clearEachTxtPage(mNextPageList, true);
            curAdPosList.clear();
            nextAdPosList.clear();
            mNextPageList = null;
            showAdWithBanner = true;
            readPageSize = 0;
            hasTooBigFirstAdPos = true;
            totalPageSize = 1;
            mCurPageList = loadPageList(mCurGroupPos, mCurPos);
            mCurPage = getCurPage(mCurPage.position);
            showAdWithBanner = false;
        }
        if (mPageView != null) {
            mPageView.drawCurPage(false);
        }
    }

    private void drawVideoAd( Canvas canvas, float top) {
        mVideoRectF = new RectF(videoLayoutPaddingLeft, top + videoBtnPaddingTop,
                mDisplayWidth - videoLayoutPaddingLeft, top + videoBtnPaddingTop + videoBtnHeight);
        Paint.FontMetricsInt fontMetrics = mAdTextPaint.getFontMetricsInt();
        int baseline = (int) (mVideoRectF.top + (mVideoRectF.bottom - mVideoRectF.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top);
        mTextRect = new Rect();
        mRulePaint.getTextBounds(ruleStr, 0, ruleStr.length(), mTextRect);
        int ruleHeight = (int) (top + videoBtnPaddingTop + videoBtnHeight + rulePaddingTop + mTextRect.height() + 10);
        mRuleRect = new Rect(150, (int) (top + videoBtnPaddingTop + videoBtnHeight + rulePaddingTop), mDisplayWidth - 150, ruleHeight + 30);
        if (ruleHeight < mVisibleHeight/* && AdManager.getInstance().showAd(AdConstants.Position.REWARD_VIDEO)*/) {
            if (isNightMode()) {
                mButtonPaint.setStyle(Paint.Style.STROKE);
                mButtonPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
                mAdTextPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.color_A4A3A8));
            } else {
                mButtonPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mButtonPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.standard_red_main_color_c1));
                mAdTextPaint.setColor(ContextCompat.getColor(context.globalContext, R.color.white));
            }
            canvas.save();


            canvas.drawRoundRect(mVideoRectF, videoBtnHeight / 2, videoBtnHeight / 2, mButtonPaint);
            canvas.restore();
            freeStr = String.format(context.globalContext.getResources().getString(R.string.ad_free_time), "" + freeTime);
            mAdTextPaint.getTextBounds(freeStr, 0, freeStr.length(), mTextRect);
            canvas.drawText(freeStr, (mDisplayWidth - mTextRect.width()) / 2 + 2 * playPaddingRight, baseline, mAdTextPaint);

            canvas.drawBitmap(playBitmap, null, new Rect((int) ((mDisplayWidth - mTextRect.width()) / 2 - mAdTextPaint.getTextSize() / 2 - playPaddingRight),
                    (int) (mVideoRectF.top + (videoBtnHeight - mAdTextPaint.getTextSize()) / 2),
                    (int) ((mDisplayWidth - mTextRect.width()) / 2 + mAdTextPaint.getTextSize() / 2 - playPaddingRight), (int) ((int)
                    (mVideoRectF.top + (videoBtnHeight - mAdTextPaint.getTextSize()) / 2) + mAdTextPaint.getTextSize())), imgPaint);

            mRulePaint.getTextBounds(ruleStr, 0, ruleStr.length(), mTextRect);
            canvas.drawText(ruleStr, (mDisplayWidth - mTextRect.width()) / 2, ruleHeight, mRulePaint);
            Logger.e("ad#point", "rule : " + mRuleRect.toString());
        } else {
            mVideoRectF = null;
            mRuleRect = null;
        }
    }

    public RectF getVideoRectF() {
        return mVideoRectF;
    }

    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    public int getMarginHeight() {
        return mMarginHeight;
    }

    public Rect getRuleRect() {
        return mRuleRect;
    }

    boolean pre() {
        if (checkStatusPre()/* || (mPageMode == PageMode.SCROLL && checkStatus())*/) {
            return false;
        }
        TxtPage prevPage = null;
        prevPage = getPrevPage();
        if (prevPage == null) {
            if (!preChapter()) {
                return false;
            } else {
                mLastPage = mCurPage;
                mCurPage = getPrevLastPage();
                mPageView.drawNextPage();
                FuncPageStatsApi.readPrevPage(mRecordBook != null ? StringFormat.parseLong(mRecordBook.getBookId(), 0) : 0,
                        prevPageId, sourceStats);
                return true;
            }
        }
        mLastPage = mCurPage;
        mCurPage = prevPage;
        mPageView.drawNextPage();
        FuncPageStatsApi.readPrevPage(mRecordBook != null ? StringFormat.parseLong(mRecordBook.getBookId(), 0) : 0,
                prevPageId, sourceStats);
        return true;
    }

    boolean preChapter() {
        if (mCurPos == 0 && mCurGroupPos == 0) {
            return false;
        }
        int preGroupPos = mCurGroupPos;
        int prevChapter = mCurPos - 1;
        if (prevChapter < 0) {
            preGroupPos = mCurGroupPos - 1;
            if (mRecordBook.bookChapterList.get(preGroupPos).size() == 0) {
                return false;
            }
            prevChapter = mRecordBook.bookChapterList.get(preGroupPos).size() - 1;
        }
        mNextPageList = mCurPageList;
        nextAdPosList = new ArrayList<>(curAdPosList);
        if (mWeakPrePageList != null && mWeakPrePageList.get() != null) {
            mCurPageList = mWeakPrePageList.get();
            mWeakPrePageList = null;
            if (mWeakPreAdPosList != null && mWeakPreAdPosList.get() != null) {
                curAdPosList = new ArrayList<>(mWeakPreAdPosList.get());
            }
            mWeakPreAdPosList = null;
        } else {
            curAdPosList.clear();
            showAdWithBanner = true;
            mCurPageList = loadPageList(preGroupPos, prevChapter);
            showAdWithBanner = false;
        }
        mCurPos = Math.max(prevChapter, 0);
        mLastPos = mCurPos;

        if (preGroupPos != mCurGroupPos) {
            mLastGroupPos = mCurGroupPos;
            mCurGroupPos = preGroupPos;
        }
        if (mCurPageList != null) {
            mStatus = STATUS_FINISH;
        } else {
            mStatus = STATUS_LOADING;
            mCurPage.position = 0;
            mPageView.drawNextPage();
            Logger.e(TAG, "preChapter -- 没有加载到内容!");
        }

        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurGroupPos, mCurPos, false);
            preLoadCatalogue();
        }
        return true;
    }

    private void preLoadCatalogue() {
        if (mPageChangeListener != null) {
            if (mCurPos % 50 > 42 && mCurGroupPos != mRecordBook.bookChapterList.size() - 1 && mRecordBook.bookChapterList.get(mCurGroupPos + 1).size() == 0) {
                mPageChangeListener.preLoadChaptersGroup(mCurGroupPos + 1);
            } else if (mCurPos % 50 < 8 && mCurGroupPos != 0 && mRecordBook.bookChapterList.get(mCurGroupPos - 1).size() == 0) {
                mPageChangeListener.preLoadChaptersGroup(mCurGroupPos - 1);
            }
        }
    }

    public void hasAdPreload() {
        hasPreloadFlowAd = true;
        hasChapterEndLoaded = false;
    }

    public void chapterEndAdLoaded(boolean success) {
        Logger.e("ad#http", "chapterEndAdLoaded: hasChapterEndLoaded = " + success);
//        hasChapterEndLoaded = success;
        if (success) {
            hasPreloadFlowAd = false;
        }
    }

    /**
     * 移动后清除计数，并重新获取广告显示位置
     */
    public void clearInfoFlowData() {
        readPageSize = 0;
    }

    public boolean isCurrPageAd() {
        if (mCurPage != null && mCurPage.isExtraAfterChapter) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCurrChapterEndAd() {
        if (mCurPage != null && mCurPage.isExtraChapterEnd) {
            return true;
        } else {
            return false;
        }
    }

    public void clearPreloadAd() {
        hasPreloadFlowAd = false;
    }

    /**
     * 随机生成多少页后显示信息广告
     *
     * @return
     */
    private int genPageIntervalCnt() {
        int min = showAdMin;
        int max = showAdMax;
        int genValue;
        if (max - min > 0) {
            genValue = mRandom.nextInt(max - min) + min;
        } else {
            genValue = Math.abs(min);
        }
        Logger.i(TAG, "产生的随机数是：" + genValue);
        return genValue;
    }

    boolean next() {
        if (checkStatus()) {
            return false;
        }
        // 判断是否阅读跨天
        if (!currData.equals(TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_01))) {
            currData = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_01);
            // 清除翻页次数累计，重新上报一次深度阅读节点
            nextPageNumber = 0;
            FunctionStatsApi.enterRead(mRecordBook != null ? mRecordBook.getBookId() : "0");
            FuncPageStatsApi.readShow(mRecordBook != null ? StringFormat.parseLong(mRecordBook.getBookId(), 0) : 0, prevPageId, sourceStats);
            Logger.e(TAG, "已跨天，重新上报阅读器日活和深度用户节点");
        }
        //递增翻页次数.
        ++nextPageNumber;
        if (nextPageNumber == 10) {
            //记录深度阅读统计(翻页10+)
            FunctionStatsApi.enterRealRead(mRecordBook != null ? mRecordBook.getBookId() : "-1");
            FuncPageStatsApi.readDeep(mRecordBook != null ? StringFormat.parseLong(mRecordBook.getBookId(), 0) : 0, prevPageId, sourceStats);
        }
        //回调下一页接口.
        if (mPageChangeListener != null) {
            mPageChangeListener.onNextPage(nextPageNumber);
        }
        //翻页统计.
        FunctionStatsApi.rNextPage(mRecordBook != null ? mRecordBook.getBookId() : "-1");
        FuncPageStatsApi.readNextPage(mRecordBook != null ? StringFormat.parseLong(mRecordBook.getBookId(), 0) : 0, prevPageId, sourceStats);
        if (isFirstEnter) {
            FuncPageStatsApi.readNextPageOnce(mRecordBook != null ? StringFormat.parseLong(mRecordBook.getBookId(), 0) : 0, prevPageId, sourceStats);
            isFirstEnter = false;
        }
        TxtPage nextPage = null;
        nextPage = getNextPage();
        if (nextPage == null) {
            if (!nextChapter()) {
                return false;
            } else {
                mLastPage = mCurPage;
                mCurPage = getCurPage(0);
                mPageView.drawNextPage();
                return true;

            }
        }
        mLastPage = mCurPage;
        mCurPage = nextPage;
        mPageView.drawNextPage();
        return true;
    }

    boolean nextChapter() {
        if (mRecordBook.bookChapterList == null || mRecordBook.bookChapterList.isEmpty()) {
            return false;
        }
        if (mCurGroupPos >= mRecordBook.bookChapterList.size()) {
            mCurGroupPos = mRecordBook.bookChapterList.size() - 1;
        }
        mCurGroupPos = Math.max(0, mCurGroupPos);
        List<ChapterBean> currGroup = mRecordBook.bookChapterList.get(mCurGroupPos);
        if (mCurPos == currGroup.size() - 1 && mCurGroupPos == mRecordBook.bookChapterList.size() - 1) {
            return false;
        }

        int nextGroupPos = mCurGroupPos;
        int nextChapter = mCurPos + 1;
        if (nextChapter >= currGroup.size()) {
            nextGroupPos = mCurGroupPos + 1;
            if (mRecordBook.bookChapterList.get(nextGroupPos).size() == 0) {
                return false;
            }
            nextChapter = 0;
        }

        if (mCurPageList != null) {
            mWeakPrePageList = new WeakReference<List<TxtPage>>(new ArrayList<>(mCurPageList));
            mWeakPreAdPosList = new WeakReference<List<Integer>>(new ArrayList<>(curAdPosList));
        }
        if (mNextPageList != null) {
            mNextPageListSize = mNextPageList.size();
            clearEachTxtPage(mCurPageList, false);
            curAdPosList.clear();
            curAdPosList = new ArrayList<>(nextAdPosList);
            mCurPageList = mNextPageList;
            nextAdPosList.clear();
            mNextPageList = null;
//            List<TxtPage> tmp = new ArrayList<>();
//            tmp.addAll(mCurPageList);
//            mCurPageList.clear();
//            mCurPageList.addAll(mNextPageList);
//            mNextPageList.clear();
//            mNextPageList.addAll(tmp);
//            tmp.clear();
        } else {
            curAdPosList.clear();
            showAdWithBanner = true;
            mCurPageList = loadPageList(nextGroupPos, nextChapter);
            showAdWithBanner = false;
        }

        mLastPos = mCurPos;
        mCurPos = Math.max(nextChapter, 0);
        if (nextGroupPos != mCurGroupPos) {
            mLastGroupPos = mCurGroupPos;
            mCurGroupPos = nextGroupPos;
        }
        if (mCurPageList != null) {
            mStatus = STATUS_FINISH;
            preLoadNextChapter();
        } else {
            mStatus = STATUS_LOADING;
            mCurPage.position = 0;
            mPageView.drawNextPage();
        }

        if (mPageChangeListener != null) {
            //滑动切换章节.
            mPageChangeListener.onChapterChange(mCurGroupPos, mCurPos, true);
            preLoadCatalogue();
        }
        //切换章节, 记录阅读记录.
        saveRecord(true);
        return true;
    }

    private void preLoadNextChapter() {
        int groupPos = mCurGroupPos;
        int chapterPos = mCurPos;
        if (chapterPos + 1 >= mRecordBook.bookChapterList.get(mCurGroupPos).size()) {
            groupPos = mCurGroupPos + 1;
            if (groupPos >= mRecordBook.bookChapterList.size()) {
                return;
            }
            chapterPos = 0;
        } else {
            chapterPos = mCurPos + 1;
        }
        if (mPreLoadDisposable != null) {
            mPreLoadDisposable.dispose();
        }
        final int nextGroup = groupPos;
        final int nextChapter = chapterPos;
        Observable.create(new ObservableOnSubscribe<List<TxtPage>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TxtPage>> e) throws Exception {
                e.onNext(loadPageList(nextGroup, nextChapter));
            }
        }).compose(new ObservableTransformer<List<TxtPage>, List<TxtPage>>() {
            @Override
            public ObservableSource<List<TxtPage>> apply(Observable<List<TxtPage>> upstream) {
                return RxUtils.toSimpleSingle(upstream);
            }
        }).subscribe(new Observer<List<TxtPage>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mPreLoadDisposable = d;
            }

            @Override
            public void onNext(List<TxtPage> txtPages) {
                mNextPageList = txtPages;
                mNextPageListSize = mNextPageList.size();
                Logger.e(TAG, "已经获取到下一章节内容");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private boolean hasPreloadFlowAd;

    /**
     * 停止移动翻页
     */
    void moveActionUp() {
        int minSize = Integer.MAX_VALUE;
//        if (minSize < 1) {
//            minSize = 1;
//        }
        if (mCurPage != null && !curAdPosList.isEmpty() && !hasPreloadFlowAd) {
            if (curAdPosList.contains(mCurPage.position + 1) && (mCurPageList.size() > mCurPage.position + 1)
                    && mCurPageList.get(mCurPage.position + 1).isExtraAfterChapter) {
                Logger.e("ad#http", "开始预加载--信息流广告");
                if (mPageChangeListener != null) {
                    mPageChangeListener.preLoadAdFlow();
                }
            }
        }

        /*if (!hasPreloadFlowAd && Math.abs(readPageSize) > minSize
                && !isFirstChapterHead(showAdMin) && !isLastChapterEnd(showAdMin)) { // 滑动到第一章头或者最后一章末了
            Logger.e("ad#http", "开始预加载--信息流广告");
            if (mPageChangeListener != null) {
                mPageChangeListener.preLoadAdFlow();
            }
        }*/
        // 判断章节末广告是否需要加载
        /*if (!hasChapterEndLoaded && mCurPage != null && mCurPageList != null && mCurPageList.size() > 1) {
            if ((mCurPage.position + 1 == mCurPageList.size() - 1) &&  mCurPageList.get(mCurPageList.size() - 1).isExtraChapterEnd) {
                Logger.e("ad#http", "开始预加载==章节末广告 ");
                mPageChangeListener.preloadChapterEndAd();
            }
        }*/
        /*Logger.e("ad#http", "mCurPage != null ？ " + (mCurPage != null)
                + "\n, curAdPosList.isEmpty() = " + (curAdPosList.isEmpty())
                + "\n, hasChapterEndLoaded = " + hasChapterEndLoaded
        );*/
        if (mCurPage != null && !curAdPosList.isEmpty() && !hasChapterEndLoaded) {
            /*Logger.e("ad#http", "\n, curAdPosList.contains(mCurPage.position + 1) = " + (curAdPosList.contains(mCurPage.position + 1))
                    + "\n, mCurPageList.size() > mCurPage.position + 1 = " + (mCurPageList.size() > mCurPage.position + 1)
            );
            if (mCurPageList.size() > mCurPage.position + 1)
            Logger.e("ad#http", " mCurPageList.get(mCurPage.position + 1).isExtraChapterEnd = "
                    + (mCurPageList.get(mCurPage.position + 1).isExtraChapterEnd)
            );*/
            if (curAdPosList.contains(mCurPage.position + 1) && (mCurPageList.size() > mCurPage.position + 1)
                    && mCurPageList.get(mCurPage.position + 1).isExtraChapterEnd) {
                if (!mCurPage.isExtraAfterChapter) {
                    Logger.e("ad#http", "开始预加载==章节末广告 ");
                    mPageChangeListener.preloadChapterEndAd();
                } else {
                    isInfoFlowAdPre = true;
                    Logger.e("ad#http", " $$$$ 章节末广告 -- 需要延迟一页加载");
                }
            } else if (curAdPosList.contains(mCurPage.position) && mCurPage.isExtraChapterEnd && isInfoFlowAdPre) { // 再次判断章节末广告加载情况
                Logger.e("ad#http", "开始预加载 ￥￥￥ 延迟的 ￥￥￥￥￥章节末广告 ");
                mPageChangeListener.preloadChapterEndAd();
                isInfoFlowAdPre = false;
            }
        }


        Logger.e(TAG, "hasPreloadFlowAd = " + hasPreloadFlowAd +", readPageSize = " + readPageSize + ", 是否支持显示广告：" + mSupportPageBetweenChapters);
        if (System.currentTimeMillis() - prevPageStayTime >= 5 * TimeUtils.SECOND_1) {
            uploadFlipPageReal();
        }
        prevPageStayTime = System.currentTimeMillis();
    }

    /**
     * 判断是否触及第一章的章节头几页
     *
     * @param preLoadCunt
     * @return
     */
    private boolean isFirstChapterHead(int preLoadCunt) {
        if (mChapterList != null && mChapterList.size() > 0 && mCurPageList != null && mCurPageList.size() > 0) {
            // 第一组第一章的前几页
            /*if (mCurGroupPos == 0 && mCurPos == 0 && mCurPage.position > preLoadCunt) {
                int start = mCurPage.position;
                if (mCurPage.position >= mCurPageList.size()) {
                    start = mCurPageList.size() - 1;
                }
                // 判断第一章的前几页是否还有广告
                while (start >= 0) {
                    if (mCurPageList.get(start).isExtraAfterChapter) {
                        return false;
                    }
                    --start;
                }
                Logger.e(TAG, "已经触及第一章节起点，不再预加载");
                return true;
            }*/
        }
        return false;
    }

    /**
     * 是否触及最后一章节的末尾几页
     *
     * @param preLoadCunt
     * @return
     */
    private boolean isLastChapterEnd(int preLoadCunt) {
        if (mChapterList != null && mChapterList.size() > 0 && mCurPageList != null && mCurPageList.size() > 0) {
            // 最后一组最后一章的后几页
            if (mCurGroupPos == mChapterList.size() - 1) {
                List lastGroupChapter = mChapterList.get(mCurGroupPos);
                if (lastGroupChapter != null && mCurPos == lastGroupChapter.size() - 1 && mCurPageList.size() - mCurPage.position <= preLoadCunt) {
                    int start = mCurPage.position;
                    if (mCurPage.position >= mCurPageList.size()) {
                        start = mCurPageList.size() - 1;
                    }
                    while (start < mCurPageList.size()) {
                        if (mCurPageList.get(start).isExtraAfterChapter) {
                            return false;
                        }
                        ++start;
                    }
                    Logger.e(TAG, "已经触及最后一章节末尾，不再预加载");
                    return true;
                }
            }
        }
        return false;
    }

    void onMoving() {
    }

    void pageCancel() {
        if (mCurPage.position == 0 && (mCurGroupPos > mLastGroupPos || mCurPos > mLastPos)) {
            preChapter();
        } else if (mCurPageList == null ||
                (mCurPage.position == mCurPageList.size() - 1 && (mCurGroupPos < mLastGroupPos || mCurPos < mLastPos))) {
            nextChapter();
        }
        if (prevIsNext) {
            --readPageSize;
            Logger.e(TAG, "pageCancel()：--readPageSize：" + readPageSize);
        } else {
            ++readPageSize;
            Logger.e(TAG, "pageCancel()：++readPageSize：" + readPageSize);
        }

        mCurPage = mLastPage;
    }

    private TxtPage getCurPage(int pos) {
        if (mCurPageList == null || mCurPageList.isEmpty()) {
            TxtPage txtPage = new TxtPage();
            txtPage.title = "";
            return txtPage;
        }
        if (pos > mCurPageList.size() - 1) {
            pos = mCurPageList.size() - 1;
        }
        if (pos < 0) {
            pos = 0;
        }
        TxtPage txtPage = mCurPageList.get(pos);
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos, txtPage, false);
        }
        return txtPage;
    }

    private TxtPage getPrevPage() {
        int pos = mCurPage.position - 1;
        --readPageSize;
        prevIsNext = false;
        Logger.e(TAG, "getPrevPage:--readPageSize：" + readPageSize);
        if (pos < 0 || mCurPageList == null || mCurPageList.isEmpty()) {
            return null;
        }
        if (pos >= mCurPageList.size()) {
            pos = mCurPageList.size() - 1;
        }
        TxtPage txtPage = mCurPageList.get(pos);
        if (mPageChangeListener != null) {
            boolean preLoadExtra = false;
            preLoadExtra = pos - 1 > -1;
            mPageChangeListener.onPageChange(pos, txtPage, preLoadExtra || pos == 0);
        }
        // 取到了广告
        /*if (txtPage.isExtraAfterChapter && !AdManager.getInstance().showAd(AdConstants.Position.CHAPTER_INTERACTION)) {
            Logger.e(TAG, "getPrevPage");
            // 不是第一页
            if (pos > 0) {
                txtPage = mCurPageList.get(pos - 1);
            } else {
                return null;
            }
        } // 广告已经不在第一页和最后一页显示了*/
        return txtPage;
    }

    private TxtPage getNextPage() {
        if (null != mCurPage) {
            ++readPageSize;
            prevIsNext = true;
            Logger.e(TAG, "getNextPage: readPageSize = " + readPageSize);
            int pos = mCurPage.position + 1;
            if (mCurPageList == null) {
                return null;
            }

            if (pos >= mCurPageList.size()) {
                TxtPage txtPage = mCurPageList.get(mCurPageList.size() - 1);
                if (txtPage != null && txtPage.isExtraAfterBook && mPageChangeListener != null) {
                    mPageChangeListener.onDrawPageAfterLastChapter(null, mTextColor, isNightMode, null);
                }
                return null;
            }

            TxtPage txtPage = mCurPageList.get(pos);
            if (mPageChangeListener != null) {
                mPageChangeListener.onPageChange(pos, txtPage, false);
            }

            return txtPage;
        }
        return new TxtPage();
    }

    private TxtPage getPrevLastPage() {
        int pos = mCurPageList.size() - 1;
        TxtPage txtPage = mCurPageList.get(pos);
        if(txtPage != null){
            Logger.e(TAG, "最后一页的位置： " + txtPage.position + ", 幅值后的位置： " + pos + ", title : " + txtPage.title);
            txtPage.position = pos;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos, txtPage, false);
        }
        return txtPage;
    }

    private boolean checkStatus() {
        if (mStatus == STATUS_LOADING) {
            ToastUtils.showLimited(ViewUtils.getString(R.string.please_wait));
            return true;
        } else if (mStatus == STATUS_ERROR) {
//            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return true;
        }
        return false;
    }

    /**
     * 滑到异常页面可以向左滑动返回原来的页面
     * @return
     */
    private boolean checkStatusPre() {
        if (mStatus == STATUS_LOADING) {
            ToastUtils.showLimited(ViewUtils.getString(R.string.please_wait));
            return true;
        } else if (mStatus == STATUS_ERROR) {
//            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            //只能在第一页回退，防止在异常页一直左滑回退
            if(mCurPage != null && mCurPage.position == 0 && mLastPage != null && mLastPage.position != 1
                    && mCurPageList != null && mCurPageList.size() > 2){
                return false;
            }
            Logger.e(TAG, "checkStatusPre -- 进入异常页面");
            return true;
        }
        return false;
    }

    public abstract void onChaptersGroupUpdate(int groupPos);

    public void supportPageBetweenChapters(boolean support) {
        if (support && !mSupportPageBetweenChapters) {
            hasTooBigFirstAdPos = true;
            totalPageSize = 1;
            Logger.e("ad#http", "广告从不显示到可以显示，强制第二页显示广告");
        }
        Logger.e("ad#http", "设置广告是否显示： " + support);
        mSupportPageBetweenChapters = support;
    }

    public void supportPageAfterLastChapter(boolean support) {
        mSupportPageAfterLastChapter = support;
    }

    public interface OnPageChangeListener {
        /**
         * @param curGroupPos
         * @param pos
         * @param isSlide     是否为滑动切换章节.
         */
        void onChapterChange(int curGroupPos, int pos, boolean isSlide);

        void loadChapterContents(List<TxtChapter> chapters, int curGroupPos, int pos);

        void preLoadChaptersGroup(int groupPos);

        void onChaptersConverted(List<List<TxtChapter>> chapters, int curGroupPos, int groupPos);

        void preLoadAdFlow();

        void onPageChange(int pos, TxtPage txtPage, boolean isCur);

        void onDrawPageAfterLastChapter(Canvas canvas, int textColor, boolean isNightMode, FrameLayout rootLayoutForExtra);

        void onDrawPageAfterChapter(boolean isVisiable, int maxLine, int lineNumber, FrameLayout rootLayoutForExtra);

        void onDrawInfoFlowAd(FrameLayout rootLayoutForExtra, int lastPos, int pagePos);

        /**
         * 翻下一页.
         *
         * @param pageNumber 当前页码
         */
        void onNextPage(int pageNumber);

        void showRetryView();

        boolean hideBottomBanner();

        void showChapterEndAd(FrameLayout rootLayoutForExtra, int pagePos, int containerHeight);

        void preloadChapterEndAd();
    }

    public static void  clearEachTxtPage(List<TxtPage> pageList, boolean clearAll) {
        if (pageList != null && !pageList.isEmpty()) {
            if (clearAll) {
                for (TxtPage txtPage : pageList) {
                    if (txtPage != null && txtPage.lines != null) {
                        txtPage.lines.clear();
                        txtPage.lines = null;
                    }
                }
            }
            pageList.clear();
        }
    }

    public void onDestroy() {
        mPageChangeListener = null;
        // 清除章节的每页数据
        clearEachTxtPage(mCurPageList, true);
        clearEachTxtPage(mNextPageList, true);
        curAdPosList.clear();
        nextAdPosList.clear();
        // 清除所有章节数据
        if (mChapterList != null && !mChapterList.isEmpty()) {
            for(List<TxtChapter> eachChapter : mChapterList) {
                if (eachChapter != null && !eachChapter.isEmpty()) {
                    eachChapter.clear();
                    eachChapter = null;
                }
            }
        }
        IOUtil.clearList(mChapterList);
        if (bgBitmap != null) {
            if (!bgBitmap.isRecycled()) {
                bgBitmap.recycle();
            }
            bgBitmap = null;
        }
        if (scrollBitmap != null) {
            if (!scrollBitmap.isRecycled()) {
                scrollBitmap.recycle();
            }
            scrollBitmap = null;
        }
    }
}
