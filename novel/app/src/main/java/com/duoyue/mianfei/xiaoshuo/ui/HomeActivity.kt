package com.duoyue.mianfei.xiaoshuo.ui

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.widget.TabHost
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.duoyue.app.bean.BookDownloadTask
import com.duoyue.app.bean.LauncherDialogBean
import com.duoyue.app.common.mgr.*
import com.duoyue.app.event.*
import com.duoyue.app.notification.NotificationHolderService
import com.duoyue.app.notification.NotificationHolderV2Service
import com.duoyue.app.receiver.PushMessageReceiver
import com.duoyue.app.ui.fragment.*
import com.duoyue.app.ui.view.HomeView
import com.duoyue.app.ui.view.RecommandDialog
import com.duoyue.app.upgrade.FirstModeUtil
import com.duoyue.app.upgrade.UpgradeApkInstallUtil
import com.duoyue.app.upgrade.UpgradeManager
import com.duoyue.app.upgrade.UpgradeMsgUtils
import com.duoyue.lib.base.BaseContext
import com.duoyue.lib.base.app.Constants
import com.duoyue.lib.base.app.user.MobileInfoPresenter
import com.duoyue.lib.base.app.user.UserManager
import com.duoyue.lib.base.devices.PhoneUtil
import com.duoyue.lib.base.devices.UtilSharedPreferences
import com.duoyue.lib.base.format.StringFormat
import com.duoyue.lib.base.location.BDLocationMgr
import com.duoyue.lib.base.log.Logger
import com.duoyue.lib.base.time.TimeTool
import com.duoyue.lib.base.widget.SimpleDialog
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mianfei.xiaoshuo.common.MainApplication
import com.duoyue.mianfei.xiaoshuo.data.bean.RecommandBean
import com.duoyue.mianfei.xiaoshuo.mine.ui.MineFragment
import com.duoyue.mianfei.xiaoshuo.presenter.HomePresenter
import com.duoyue.mianfei.xiaoshuo.read.common.ActivityHelper
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ExtraPageMgr
import com.duoyue.mianfei.xiaoshuo.read.ui.read.ReadActivity
import com.duoyue.mianfei.xiaoshuo.read.utils.BookDownloadManager
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils
import com.duoyue.mod.stats.ErrorStatsApi
import com.duoyue.mod.stats.FuncPageStatsApi
import com.duoyue.mod.stats.FunctionStatsApi
import com.duoyue.mod.stats.common.FunPageStatsConstants
import com.duoyue.mod.stats.common.PageNameConstants
import com.zydm.base.common.BaseApplication
import com.zydm.base.common.ParamKey
import com.zydm.base.data.tools.JsonUtils
import com.zydm.base.ext.setVisible
import com.zydm.base.statistics.umeng.StatisHelper
import com.zydm.base.tools.PhoneStatusManager
import com.zydm.base.ui.ZydmReceiver
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.ui.fragment.BaseFragment
import com.zydm.base.utils.SPUtils
import com.zydm.base.utils.SharePreferenceUtils
import com.zydm.base.utils.ToastUtils
import com.zydm.base.utils.ViewUtils
import com.zydm.base.widgets.MyFragmentTabHost
import com.zzdm.ad.router.BaseData
import com.zzdm.ad.router.RouterPath
import com.zzdm.tinker.app.BuildInfo
import com.zzdm.tinker.util.SampleApplicationContext
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.home_tab_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterPath.App.PATH_HOME)
class HomeActivity : BaseActivity(), HomeView {

    private var mCurTab: Int = BOOK_CITY
    private var mlastTab: Int = BOOKSHELF
    private var homeKeyPressd: Boolean = false

    companion object {
        private const val TAG = "App#HomeActivity"
        const val BOOK_CITY = 0
        const val BOOKSHELF = 1
        const val BOOKLIST = 2
//      const val CATEGORY = 2
        const val MINE = 3
    }

    private var isFirst: Boolean = false
    private var isResume: Boolean = false
    private var mHomeKeyReceiver: ZydmReceiver? = null
    private var isFromBookCityIcon = false
    private var bookCityFrom = 1
    //Tab动画
    private var mTabAnimation: Animation? = null
    private var mHandler = Handler()

    private var homePresenter: Presenter? = null

    private var editFragment: ExitAppFragment? = null
    private var mExtraPageMgr: ExtraPageMgr? = null
    private var isBackground: Boolean = false
    private var goTaskCenter = false

    private val mOnTabChangedListener = TabHost.OnTabChangeListener { tabId ->
        mCurTab = tabId.toInt()
//        startTabAnim(mCurTab,mlastTab)

        if (mCurTab != BOOKSHELF) {
            val fragment = supportFragmentManager.findFragmentByTag(BOOKSHELF.toString())
            if (fragment != null && fragment is BookShelfFragment) {
                fragment.quitEditMode()
            }
        }

        when (mCurTab) {
            BOOK_CITY -> {
                //设置为进入书城可以进行加载数据(V1.2.7 20191031修改, 启动进入时选中发现Tab)
                //FirstModeUtil.getInstance(applicationContext).mode = null
                StatisHelper.onEvent().bookstore(getPageName())
                //切换到书城.
                FunctionStatsApi.bcTabClick()
                bookCityFrom = 1
                if (isFirst) {
                    setCommand()
                }

                if (!SPUtils.getBoolean(SPUtils.SHARED_IS_FIRST_USE, true)) {
                    FuncPageStatsApi.bookCityShow(bookCityFrom)

                }
                BaseApplication.context.currPageId = PageNameConstants.BOOK_CITY
                val fragmentBookCity = supportFragmentManager.findFragmentByTag(BOOK_CITY.toString())
                if (fragmentBookCity != null && fragmentBookCity is BookCityFragment) {
                    fragmentBookCity.setIsRead()
                }
            }
//            CATEGORY -> {
//                StatisHelper.onEvent().classify()
//                //切换到分类.
//                FunctionStatsApi.cTabClick()
//                if (isFromBookCityIcon) {
//                    FuncPageStatsApi.categoryShow(3)
//                } else {
//                    FuncPageStatsApi.categoryShow(1)
//                }
//                isFromBookCityIcon = false
//                BaseApplication.context.currPageId = PageNameConstants.CATEGORY
//                val fragmentClass = supportFragmentManager.findFragmentByTag(CATEGORY.toString())
//                if (fragmentClass != null && fragmentClass is NewCategoryActivity) {
//                    fragmentClass.setIsRead()
//                }
//            }
            BOOKSHELF -> {
                StatisHelper.onEvent().bookshelf()
                //切换到书架.
                FunctionStatsApi.bsTabClick()
                FuncPageStatsApi.bookShelfShow(1)
                BaseApplication.context.currPageId = PageNameConstants.BOOKSHELF

                val fragmentBookShelf = supportFragmentManager.findFragmentByTag(BOOKSHELF.toString())
                if (fragmentBookShelf != null && fragmentBookShelf is BookShelfFragment) {
                    fragmentBookShelf.setIsRead()
                }
            }
            MINE -> {
                StatisHelper.onEvent().home()
                //切换到我的.
                FunctionStatsApi.mTabClick()
                FuncPageStatsApi.mineShow(1)

                //获取我的Fragment对象.
                val fragmentMine = supportFragmentManager.findFragmentByTag(MINE.toString())
                if (fragmentMine != null && fragmentMine is MineFragment) {
                    //调用登录成功接口.
                    fragmentMine.initData()
                    fragmentMine.getReadHistory()
                    if (goTaskCenter) {
                        goTaskCenter = false
                        fragmentMine.performClickCenterClick()
                    }
                } else {
                    MineFragment.goTaskCenter = goTaskCenter
                }
                BaseApplication.context.currPageId = PageNameConstants.MINE
            }

            BOOKLIST -> {
                //切换到发现.
                BaseApplication.context.currPageId = PageNameConstants.FIND
                FuncPageStatsApi.intoDiscover(1)
                val fragmentBookList = supportFragmentManager.findFragmentByTag(BOOKLIST.toString())
                if (fragmentBookList != null && fragmentBookList is BookFindFragment) {
                    fragmentBookList.setIsRead()
                }
            }
        }

        mlastTab = mCurTab
        Glide.get(this).clearMemory()
    }

    private val mTabInfos = arrayOf(
        TabInfo(BookCityFragment::class.java, R.drawable.tab_book_city_selector, R.string.tab_book_city),
        TabInfo(BookShelfFragment::class.java, R.drawable.tab_bookshelf_selector, R.string.tab_bookshelf),
        TabInfo(BookFindFragment::class.java, R.drawable.tab_new_book_list_selector, R.string.find),
//        TabInfo(NewCategoryActivity::class.java, R.drawable.tab_category_selector, R.string.tab_category),
        TabInfo(MineFragment::class.java, R.drawable.tab_mine_selector, R.string.tab_mine)
    )

    override fun initActivityConfig(activityConfig: ActivityConfig) {
        super.initActivityConfig(activityConfig)
        //上报主页初始化.
        ErrorStatsApi.addError(ErrorStatsApi.HOME_INIT)
        activityConfig.isStPage = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        homePresenter = HomePresenter(this)
        UpgradeApkInstallUtil.getInstance(this).getApkStorePath(this);
        SPUtils.putBoolean(RecommandDialog.KEY_SHOW_DIALOG, true)

        //每次启动app设置为false
        SharePreferenceUtils.putBoolean(applicationContext, SharePreferenceUtils.IS_IN_DETAIL, false)

        //检查是否更新成功
        checkUpdateSuccess()

        //上报进入主页成功.
        ErrorStatsApi.addError(ErrorStatsApi.HOME_SUCC)
        UtilSharedPreferences.saveBooleanData(application, UtilSharedPreferences.KEY_IS_DOWNLOADING, false)
        //注册广播接收器.
        registerReceiver();
        //检查用户登录.
        //UserLoginMgr.checkLogin(this);
        StatisHelper.onEvent().bookstore(intent.getParcelableExtra<BaseData>(DATA_KEY)?.from ?: "启动页")
        initTab()
        PhoneStatusManager.getInstance().resetMtDeviceId()

        mExtraPageMgr = ExtraPageMgr()
        mExtraPageMgr!!.init(this, "")

        EventBus.getDefault().register(this)
        //调用展示启动引导页接口.
        if (!StartGuideMgr.showGuidePage(this, false)) {
            FirstModeUtil.getInstance(this).mode = null
            isFirst = true
            SharePreferenceUtils.putLong(application, SharePreferenceUtils.PREV_APPID, Constants.APP_ID)
            //如果版本更新信息中版本号和本地版本号相同,清除更新信息
            val upgradeMsgBean = UpgradeMsgUtils.getUpgradeMsg(application)
            if (upgradeMsgBean != null && upgradeMsgBean.getAppVersionCode() <= PhoneStatusManager.getInstance().getAppVersionCode()) {
                UpgradeMsgUtils.clearUpdateMsg(application)
            }

            if (UpgradeManager.getInstance(applicationContext).isCheckOnHome) {
                var currPageId = ""
                when (mCurTab) {
                    BOOKSHELF -> currPageId = PageNameConstants.BOOKSHELF
                    BOOK_CITY -> currPageId = PageNameConstants.BOOK_CITY
//                    CATEGORY -> currPageId = PageNameConstants.CATEGORY
                    MINE -> currPageId = PageNameConstants.MINE
                }
                UpgradeManager.getInstance(application).startBackgroundCheck(this@HomeActivity, currPageId)
            }
            // oppo渠道部分机型极端情况下会报错：https://bbs.coloros.com/thread-174655-3-1.html
            try {
                startService(Intent(this@HomeActivity, NotificationHolderV2Service::class.java))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            Logger.d(TAG, "第二次进来了")
            //判断是否已授权成功.
            if (checkPermission(false)) {
                //授权成功, 调用激活接口.
                FunctionStatsApi.authSucc();
            }
            StartGuideMgr.checkTaotiaoUpload(activity)
        } else {
            FirstModeUtil.getInstance(this).mode = "isFirst"
            isFirst = false
            //如果显示阅读品味选择页面, 则检查权限.
            if (!Constants.IS_AIGAO) {
                checkPermission(true)
            }
        }
        //处理Push消息.
        handlePushMessage(intent);
        if (SharePreferenceUtils.getBoolean(application, SharePreferenceUtils.IS_FIRST_IN, true)) {
            SharePreferenceUtils.putBoolean(application, SharePreferenceUtils.IS_FIRST_IN, false)
        }

        StartGuideMgr.setSexChangeListener { sex: Boolean ->
            isFirst = true

            //新用户选择性别时上报

            var isFirstUse = SPUtils.getBoolean(SPUtils.SHARED_IS_FIRST_USE, true)
            if (isFirstUse) {
                //bookCityFrom = 3
                //tabhost.currentTab = BOOK_CITY
                // 对接今日头条广告回调
                setCommand()
                //拉取任务列表信息
                TaskMgr.taskList(application)
                TaskMgr.getReadHistory(application)
            }
            SharePreferenceUtils.putLong(application, SharePreferenceUtils.PREV_APPID, Constants.APP_ID)
        }
        if (UserManager.getInstance().userInfo != null) {
            //拉取任务列表信息
            TaskMgr.taskList(application)
            TaskMgr.getReadHistory(application)
        }

        isShowRedPoint()

        //服务要求上报用户的经纬度  每三十分钟  和主页的任何逻辑都不冲突
        mHandler.post(runnable)
        PushMgr.connectHW(this)
    }

    //退出弹框回调
    var exitListener = object : ExitAppFragment.OnExitAppListener {
        override fun onLeft() {
            BaseApplication.context.exit()
            var type = getViewStatus()
            FuncPageStatsApi.exitApp(type, 1)
            FuncPageStatsApi.showExitClick(getCurrPageId(), "1")
            StatisHelper.onKillProcess(this@HomeActivity)
            finish()
        }

        override fun onRight() {
            if (editFragment?.adSiteBean != null) {
                FuncPageStatsApi.showExitClick(getCurrPageId(), "2")
                mExtraPageMgr?.showRewardVideo(mHandler, null, 0, editFragment!!.adSiteBean.channelCode,
                    FunPageStatsConstants.EXITPOP, "3", "")
            }
        }

        override fun onDismiss(dialogInterface: DialogInterface?) {
            FuncPageStatsApi.showExitClick(getCurrPageId(), "3")
        }
    }

    override fun getCurrPageId(): String {
        return when (mCurTab) {
            BOOK_CITY -> {
                PageNameConstants.BOOK_CITY
            }
//            CATEGORY -> {
//                PageNameConstants.CATEGORY
//            }
            BOOKSHELF -> {
                PageNameConstants.BOOKSHELF
            }
            MINE -> {
                PageNameConstants.MINE
            }
            BOOKLIST -> {
                PageNameConstants.FIND
            }
            else -> {
                "HOME"
            }
        }
    }

    /**
     * 更新成功,上报节点
     */
    private fun checkUpdateSuccess() {
        //覆盖更新第一次启动上报
        val lastVersion = SharePreferenceUtils.getInt(applicationContext, SharePreferenceUtils.UPDATE_FIRST_START, -1)
        if (lastVersion > 0 && lastVersion < PhoneStatusManager.getInstance().getAppVersionCode()) {
            SharePreferenceUtils.putInt(
                applicationContext,
                SharePreferenceUtils.UPDATE_FIRST_START,
                PhoneStatusManager.getInstance().getAppVersionCode()
            )
            FuncPageStatsApi.updateFirstStart()
        } else if (lastVersion == -1) {
            SharePreferenceUtils.putInt(
                applicationContext,
                SharePreferenceUtils.UPDATE_FIRST_START,
                PhoneStatusManager.getInstance().getAppVersionCode()
            )
        }

        //热更新生效
        val lastHotVersion =
            SharePreferenceUtils.getString(applicationContext, SharePreferenceUtils.HOT_UPDATE_FIRST_START, "")
        Logger.d(
            TAG,
            "开始判断 lastHotVersion-->" + lastHotVersion + "<-->nowVersion " + BuildInfo.PATCH_VERSION + "<-->比较" + lastHotVersion.compareTo(
                BuildInfo.PATCH_VERSION
            )
        )
        if (!TextUtils.isEmpty(lastHotVersion) && lastHotVersion.compareTo(BuildInfo.PATCH_VERSION) < 0) {
            Logger.d(TAG, "更新成功")
            SharePreferenceUtils.putString(
                applicationContext,
                SharePreferenceUtils.HOT_UPDATE_FIRST_START,
                BuildInfo.PATCH_VERSION
            )
            FuncPageStatsApi.hotUpdateSuccess()
        } else if (TextUtils.isEmpty(lastHotVersion)) {
            Logger.d(TAG, "版本为空")

            SharePreferenceUtils.putString(
                applicationContext,
                SharePreferenceUtils.HOT_UPDATE_FIRST_START,
                BuildInfo.PATCH_VERSION
            )
        }
    }

    /**
     * 判断是否展示红点
     */
    public fun isShowRedPoint() {
        val lastTime = SharePreferenceUtils.getLong(
            SampleApplicationContext.application,
            SharePreferenceUtils.FIRST_CLICK_EVERY_DAY_TASK, 0
        )
        if (UpgradeMsgUtils.isHasUpdateInfo(application) || (lastTime == 0L || lastTime < TimeTool.getCurrDayBeginTime())) {
            setRedPointVisible(true)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            return
        }
        val from: String? = intent.getParcelableExtra<BaseData>(DATA_KEY)?.from;
        if (!TextUtils.equals(from, "PUSH")) {
            StatisHelper.onEvent().bookstore(from ?: "启动页")
            //如果从阅读历史页面过来的, 直接进入书城.
//        tabhost.currentTab = if (from == ReadHistoryActivity.getCurrPageName()) BOOK_CITY else BOOKSHELF
            tabhost.currentTab = BOOK_CITY
        }
        mCurTab = tabhost.currentTab
        BaseApplication.context.currPageId = PageNameConstants.BOOK_CITY
        //处理Push消息.
        handlePushMessage(intent)
    }

    /**
     * 处理Push推送.
     */
    private fun handlePushMessage(intent: Intent?) {
        try {
            //判断是否为Push推送.
            var pushData = intent?.getStringExtra(ParamKey.PUSH_DATA_KEY)
            if (!StringFormat.isEmpty(pushData)) {
                //调用打开推送消息接口.
                PushMessageReceiver.openPushMessage(this, pushData)
            }
        } catch (throwable: Throwable) {
            Logger.e(TAG, "handlePushMessage:")
        }
    }

    private fun initTab() {
        val mTabHost = findViewById<MyFragmentTabHost>(android.R.id.tabhost)
        mTabHost.setup(this, supportFragmentManager, android.R.id.tabcontent)
//        mTabAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_main_tab)
        for (i in mTabInfos.indices) {
            val tabInfo = mTabInfos[i]
            val tabSpec = mTabHost.newTabSpec("$i")
                .setIndicator(createTabItemView(tabInfo))
            mTabHost.addTab(tabSpec, tabInfo.mFragmentClass, null)
        }
        mTabHost.setOnTabChangedListener(mOnTabChangedListener)


        var baseData = intent?.getParcelableExtra<BaseData>(BaseActivity.DATA_KEY)

        var isFirstUse = SPUtils.getBoolean(SPUtils.SHARED_IS_FIRST_USE, true)

        if (baseData != null && baseData.from.equals("Notification")) {
            bookCityFrom = 7
            mTabHost.currentTab = BOOK_CITY
        } else if (isFirstUse) {
            bookCityFrom = 3
            mTabHost.currentTab = BOOK_CITY
        } else {
            //mTabHost.currentTab = BOOKSHELF
            //FuncPageStatsApi.bookShelfShow(3)
            mTabHost.currentTab = BOOK_CITY
        }
        //切换到书架.
        FunctionStatsApi.bsTabClick();
        mCurTab = tabhost.currentTab
    }

    private fun createTabItemView(tabInfo: TabInfo): View {
        val tabItemView = ViewUtils.inflateView(this, R.layout.home_tab_item)
        tabItemView.tab_name.setText(tabInfo.textRes)
        tabItemView.tab_icon.setImageResource(tabInfo.iconRes)
        return tabItemView
    }

    /**
     * 设置我的页面红点显示
     */
    fun setRedPointVisible(isVisible: Boolean) {
        findViewById<MyFragmentTabHost>(android.R.id.tabhost).tabWidget.getChildTabViewAt(MINE)
            .iv_red_point.setVisible(isVisible)
    }

    /**
     * 点击Tab动画效果
     *
     * @param curPos
     * @param prePos
     */
    fun startTabAnim(curPos: Int, prePos: Int) {
        findViewById<MyFragmentTabHost>(android.R.id.tabhost).tabWidget.getChildTabViewAt(prePos)
            .tab_icon.clearAnimation()
        findViewById<MyFragmentTabHost>(android.R.id.tabhost).tabWidget.getChildTabViewAt(curPos)
            .tab_icon.startAnimation(mTabAnimation)

    }

    private var exitTime: Long = 0
    override fun onBackPressed() {
        //调用关闭阅读口味接口.
        if (StartGuideMgr.onBackPressed()) {
            //响应阅读回味页面返回操作(我的Tab).
            return;
        }

        if (mCurTab == BOOKSHELF) {
            val fragment = supportFragmentManager.findFragmentByTag(BOOKSHELF.toString())
            if (fragment != null) {
                val bookShelfFragment = fragment as BookShelfFragment
                if (bookShelfFragment.isEditMode) {
                    bookShelfFragment.quitEditMode()
                    return
                }
            }
        }
        //退出弹框
        if (editFragment == null) {
            editFragment = ExitAppFragment()
            editFragment!!.setOnExitAppListener(exitListener)
        }
        if (editFragment != null && !editFragment!!.isVisible && !this.isFinishing) {
            editFragment!!.showNow(supportFragmentManager, "ExitApp")
            FuncPageStatsApi.showExitDialog(getCurrPageId())
            if (!editFragment!!.showAd()) {
                editFragment!!.setBottomBtnText(resources.getString(R.string.confirm), resources.getString(R.string.cancel))
                editFragment!!.setMiddleIsDisplay(View.GONE)
                editFragment!!.setBottomBtnTextColor(resources.getColor(R.color.color_1b1b1b),
                    resources.getColor(R.color.standard_red_main_color_c1))
            } else {
//                editFragment!!.setShudouSize()
                editFragment!!.setBottomBtnText(resources.getString(R.string.exit_ignore), resources.getString(R.string.get_shudou))
                editFragment!!.setBottomBtnTextColor(resources.getColor(R.color.color_1b1b1b),
                    resources.getColor(R.color.standard_red_main_color_c1))
            }
        }
//        editFragment!!.setMiddleIsDisplay(View.GONE)
//        if (System.currentTimeMillis() - exitTime > 3000) {
////            ToastUtils.showLimited(getString(R.string.back_quit))
//            editFragment!!.showNow(supportFragmentManager, "ExitApp")
//            editFragment!!.setMiddleIsDisplay(View.GONE)
//            exitTime = System.currentTimeMillis()
//        } else {
//            super.onBackPressed()
//            BaseApplication.context.exit()
//            var type = getViewStatus()
//            FuncPageStatsApi.exitApp(type, 1)
//            StatisHelper.onKillProcess(this);
//        }
    }

    override fun onPause() {
        super.onPause()
        isResume = false
    }

    override fun onResume() {
        super.onResume()
        ReadActivity.gBookId = 0
        isResume = true
        if (isFirst && mCurTab == BOOK_CITY) {
            setCommand()
        }
        if (homeKeyPressd) {
            when (mCurTab) {
                BOOKSHELF -> {
                    val bookShelfFragment = supportFragmentManager.findFragmentByTag(BOOKSHELF.toString())
                    if (bookShelfFragment is BookShelfFragment) {
                        bookShelfFragment?.resumeByHomePressed()
                    }
                }
                BOOK_CITY -> {
                    val fragmentBookCity = supportFragmentManager.findFragmentByTag(BOOK_CITY.toString())
                    if (fragmentBookCity is BookCityFragment) {
                        fragmentBookCity?.resumeByHomePressed()
                    }
                }
//                CATEGORY -> {
//                    val fragmentCategory = supportFragmentManager.findFragmentByTag(CATEGORY.toString())
//                    if (fragmentCategory is NewCategoryActivity) {
//                        fragmentCategory?.resumeByHomePressed()
//                    }
//                }

                BOOKLIST -> {
                    val fragmentCategory = supportFragmentManager.findFragmentByTag(BOOKLIST.toString())
                    if (fragmentCategory is BookFindFragment) {
                        fragmentCategory?.resumeByHomePressed()
                    }
                }

                MINE -> {
                    val fragmentMine = supportFragmentManager.findFragmentByTag(MINE.toString())
                    var guidePgae = findViewById<View>(R.id.guide_page_id)
                    if (fragmentMine is MineFragment && guidePgae.visibility != View.VISIBLE) {
                        fragmentMine?.resumeByHomePressed()
                    }
                }
            }
            homeKeyPressd = false
        }
        registerHomeKeyReceiver()
        // 处理广点通激励视频不回调关闭动作问题
        if (isBackground && mExtraPageMgr!!.hasShowVideo) {
            TaskMgr.show(this, supportFragmentManager,
                getString(R.string.finish_reward_video_task), TaskMgr.REWARD_VIDEO_EXIT_TASK)
        }
        mExtraPageMgr?.hasShowVideo = false
        isBackground = false
    }

    /**
     * 获取剪贴板数据
     */
    private fun setCommand() {
        if (MainApplication.isBackToForeground) {
            if (UpgradeManager.upgradeRequestFinished(this@HomeActivity)) {
                val clipText = Utils.getClipText()
                if (TextUtils.isEmpty(clipText)) return
                Logger.d(TAG, clipText)
//                var homePresenter: Presenter? = null
//                if (homePresenter == null) {
//                    homePresenter = HomePresenter(this)
//                }
                Logger.e("App#HomeActivity", "开始请求口令书籍")
                homePresenter?.loadData(clipText)
            } else {
                Logger.e("App#HomeActivity", "有版本更新请求，不请求口令")
                mHandler.postDelayed(object : Runnable {
                    override
                    fun run() {
                        val clipText = Utils.getClipText()
                        if (TextUtils.isEmpty(clipText)) return
                        Logger.d(TAG, clipText)
//                        var homePresenter1: Presenter? = null
//                        if (homePresenter1 == null) {
//                            homePresenter1 = HomePresenter(this@HomeActivity)
//                        }
                        Logger.e("App#HomeActivity", "延迟后又开始请求口令书籍")
                        homePresenter?.loadData(clipText)
                    }
                }, 5000)
            }

        }
    }

    private fun getViewStatus(): Long {
        var type = 2L
        when (mCurTab) {
            BOOKSHELF -> {
                val bookShelfFragment = supportFragmentManager.findFragmentByTag(BOOKSHELF.toString())
                if (bookShelfFragment is BookShelfFragment) {
                    if (bookShelfFragment.hasDrawed()) {
                        type = 1L
                    }
                }
            }
            BOOK_CITY -> {
                val fragmentBookCity = supportFragmentManager.findFragmentByTag(BOOK_CITY.toString())
                if (fragmentBookCity is BookCityFragment) {
                    if (fragmentBookCity.hasDrawed()) {
                        type = 1L
                    }
                }
            }
//            CATEGORY -> {
//                val fragmentCategory = supportFragmentManager.findFragmentByTag(CATEGORY.toString())
//                if (fragmentCategory is NewCategoryActivity) {
//                    if (fragmentCategory.hasDrawed()) {
//                        type = 1L
//                    }
//                }
//            }
            MINE -> {
                type = 1L
            }
        }
        return type;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun goTaskCenterEvent(event: GoTaskCenterEvent) {
        goTaskCenter = true
        val fragmentMine = supportFragmentManager.findFragmentByTag(MINE.toString())
        if (fragmentMine != null && fragmentMine is MineFragment) {
            goTaskCenter = false
            fragmentMine.performClickCenterClick()
        } else {
            val mTabHost = findViewById<MyFragmentTabHost>(android.R.id.tabhost)
            mTabHost.currentTab = MINE
        }
    }

    private val homePressdListener = ZydmReceiver.Listener {
        if (isTopActivity()) {
            homeKeyPressd = true
            var type = getViewStatus()
            FuncPageStatsApi.exitApp(type, 2)
        }
    }

    private fun registerHomeKeyReceiver() {
        mHomeKeyReceiver = ZydmReceiver(homePressdListener)
        val homeFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)

        registerReceiver(mHomeKeyReceiver, homeFilter)
    }

    private fun unregisterHomeKeyReceiver() {
        mHomeKeyReceiver?.let {
            unregisterReceiver(mHomeKeyReceiver)
            mHomeKeyReceiver = null
        }
    }

    public override fun onStop() {
        super.onStop()
        isBackground = true
        unregisterHomeKeyReceiver()
    }

    override fun showDialog(data: RecommandBean) {
        val recommandDialog = RecommandDialog()
        // 有启动弹窗时，跳过，等待下次触发
        if (recommandDialog.canShowDialog()) {
            SPUtils.putBoolean(RecommandDialog.KEY_HAS_COMMAND_DIALOG, false)
            recommandDialog.setData(data)
            recommandDialog.setCancelListener {
                if (SPUtils.getBoolean(RecommandDialog.KEY_HAS_LAUNCHER_DIALOG, false)) {
                    var dataJson = SPUtils.getString(RecommandDialog.KEY_DATA_JSON)
                    if (!StringFormat.isEmpty(dataJson)) {
                        var launcherBean: LauncherDialogBean =
                            JsonUtils.parseJson(dataJson, LauncherDialogBean::class.java)
                        if (launcherBean != null) {
                            LauncherDialogMgr.showDialog(this@HomeActivity, launcherBean, PageNameConstants.BOOK_CITY)
                        }
                    }
                    SPUtils.putString(RecommandDialog.KEY_DATA_JSON, "")
                }
            }
            recommandDialog.show(supportFragmentManager, "recommand")
            SPUtils.putBoolean(RecommandDialog.KEY_SHOW_DIALOG, true)
        } else {
            SPUtils.putBoolean(RecommandDialog.KEY_HAS_COMMAND_DIALOG, true)
            SPUtils.putString(RecommandDialog.KEY_DATA_JSON, JsonUtils.toJson(data))
            Logger.e("App#HomeActivity", "有启动弹窗了，等待下次触发")
        }
        Utils.cleanClipText()
    }

    /**
     * 口令目前只支持跳转阅读器
     */
    private fun goRead(recommandBean: RecommandBean) {
        when (recommandBean.getJumpType()) {
            3 -> {
                //阅读器
                ActivityHelper.gotoRead(
                    this, recommandBean.getBookId().toString(),
                    recommandBean.getLastReadChapter(), BaseData("小说口令"), "", PageNameConstants.SOURCE_COMMAND
                )
            }
            2 -> {
                //H5
                //                ActivityHelper.INSTANCE.gotoWeb(getActivity(), mRecommandBean.getLink());
            }
        }
    }

    override fun showEmpty() {
    }

    override fun showError() {
    }

    override fun onDestroy() {
        super.onDestroy()
        //注销广播接收器.
        unRegisterReceiver();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        PermissionMgr.onDestroy();
        // 阅读口味oom  退出置空
        StartGuideMgr.onXDestroy()

        mHandler.removeCallbacksAndMessages(null)
        BaseApplication.handler.removeCallbacksAndMessages(null)
        if (homePresenter != null) {
            homePresenter!!.destroy()
        }
    }

    /**
     * 注册广播接收器.
     */
    private fun registerReceiver() {
        try {
            //注册登陆广播接收器.
            var intentFilter = IntentFilter();
            intentFilter.addAction(com.duoyue.lib.base.app.Constants.LOGIN_SUCC_ACTION)
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            //注册广播.
            BaseContext.getContext().registerReceiver(mReceiver, intentFilter);
        } catch (throwable: Throwable) {
            Logger.e(TAG, "registerReceiver: {}", throwable);
        }
    }

    /**
     * 注销广播接收器
     */
    private fun unRegisterReceiver() {
        //============注销Dsp广告事件广播================
        try {
            BaseContext.getContext().unregisterReceiver(mReceiver);
        } catch (throwable: Throwable) {
            //Logger.e(TAG, "unRegisterReceiver: {}", throwable)
        }
    }

    /**
     * 广播接收器.
     */
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Logger.i(TAG, "onReceive: {}", intent.action)
                if (Constants.LOGIN_SUCC_ACTION.equals(intent.action)) {
                    //登录成功.
                    onLoginSucc();
                    var userInfo = UserManager.getInstance().userInfo
                    if (userInfo === null) return
                    if (userInfo.type == 1) return
                    EventBus.getDefault().post(LoginSuccessEvent(userInfo))
                } else if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                    if (PhoneUtil.isNetworkAvailable(this@HomeActivity)) {
                        //重启下载任务
                        BookDownloadManager.getsInstance().checkDownloadTask()

                        //同步添加书架
                        homePresenter?.uploadAddShelf()
                    } else {
                        ToastUtils.showLimited(getString(R.string.net_connect_Fail))
                    }
                }
            } catch (throwable: Throwable) {
                Logger.e(TAG, "onReceive: {}", throwable)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleSwitchEvent(event: TabSwitchEvent) {
        when (event.tab) {
            BOOK_CITY -> {
                bookCityFrom = event.entrance
                tabhost.currentTab = BOOK_CITY
            }
//            CATEGORY -> {
//                isFromBookCityIcon = true
//                tabhost.currentTab = CATEGORY
//            }

            BOOKSHELF -> {
                tabhost.currentTab = BOOKSHELF
            }
            BOOKLIST -> {
                tabhost.currentTab = BOOKLIST
            }
        }
        mCurTab = tabhost.currentTab

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateHandleSwitchEvent(event: GetUpdateFinishEvent) {
        isShowRedPoint()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun downloadBookEvent(event: BookDownloadEvent) {
        if (isResume) {
            if (event.downloadStates == BookDownloadEvent.DOWNLOAD_ERROR) {
                showRetryDialog(event.bookDownloadTask)
            }
        }
    }

    private fun showRetryDialog(task: BookDownloadTask) {
        try {
            val message = "当前网络不稳定\n是否重试下载?"
            val simpleDialog = SimpleDialog.Builder(this)
                .setCanceledOnTouchOutside(false)
                .setTitle(message)
                .setPositiveButton("重试") { dialog, which ->
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                    BookDownloadManager.getsInstance().retryDownload(task)
                }.setNegativeButton(R.string.cancel) { dialog, which ->
                    //关闭Dialog.
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                    BookDownloadManager.getsInstance().removeTask(task)
                }.create()
            //显示Dialog.
            simpleDialog.show()
        } catch (throwable: Throwable) {
            Logger.e(TAG, "removeBook: {}", throwable)
        }
    }

    /**
     * 登录成功.
     */
    private fun onLoginSucc() {
        Logger.i(TAG, "onLoginSucc: {}", supportFragmentManager);
        if (supportFragmentManager == null) {
            return
        }
        //获取书架Fragment对象.
        val fragmentBookShelf = supportFragmentManager.findFragmentByTag(BOOKSHELF.toString())
        if (fragmentBookShelf != null && fragmentBookShelf is BookShelfFragment) {
            //调用登录成功接口.
            fragmentBookShelf.onLoginSucc()
        }

        //获取书城Fragment对象.
        val fragmentBookCity = supportFragmentManager.findFragmentByTag(BOOK_CITY.toString())
        if (fragmentBookCity != null && fragmentBookCity is BookCityFragment) {
            //调用登录成功接口.
            fragmentBookCity.onLoginSucc()
        }

//        //获取分类Fragment对象.
//        val fragmentCategory = supportFragmentManager.findFragmentByTag(CATEGORY.toString())
//        if (fragmentCategory != null && fragmentCategory is NewCategoryActivity) {
//            //调用登录成功接口.
//            fragmentCategory.onLoginSucc()
//        }


        //获取发现Fragment对象.
        val fragmentBookListFragment = supportFragmentManager.findFragmentByTag(BOOKLIST.toString())
        if (fragmentBookListFragment != null && fragmentBookListFragment is BookFindFragment) {
            //调用登录成功接口.
            fragmentBookListFragment.onLoginSucc()
        }

        //获取我的Fragment对象.
        val fragmentMine = supportFragmentManager.findFragmentByTag(MINE.toString())
        if (fragmentMine != null && fragmentMine is MineFragment) {
            //调用登录成功接口.
            fragmentMine.onLoginSucc()
        }
    }


    /**
     * 检查权限.
     * @param isReqPermissions 是否请求权限.
     */
    private fun checkPermission(isReqPermissions: Boolean): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //Android6.0以下系统版本, 不需要授权.
            return true;
        }
        var permissionList = ArrayList<String>()
        //判断存储空间权限.
        if (!PhoneUtil.checkPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //未授权.
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        //判断手机信息权限.
        if (!PhoneUtil.checkPermission(applicationContext, Manifest.permission.READ_PHONE_STATE)) {
            //未授权.
            permissionList.add(Manifest.permission.READ_PHONE_STATE)
        }
        //判断定位权限.
        if (!PhoneUtil.checkPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //未授权.
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionList.size > 0) {
            //判断是否请求权限.
            if (isReqPermissions) {
                //发送授权请求(必须从后往前).
                ActivityCompat.requestPermissions(activity, StringFormat.listConvertStringArray(permissionList), 100)
            }
            return false;
        }
        return true;
    }

    /**
     * 授权回调.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 100) {
            return;
        }
        //授权完成, 调用上报IMEI接口.
        MobileInfoPresenter.uploadSupplyMobileInfo()
        //授权完成, 启动定位.
        BDLocationMgr.startLocation()
        try {
            var isOK: Boolean = true;
            if (grantResults.isNotEmpty()) {
                // 遍历数组元素
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        //存在未授权的权限.
                        isOK = false;
                        break
                    }
                }
                //判断是否授权通过.
                if (isOK) {
                    //调用授权成功统计.
                    FunctionStatsApi.authSucc()
                    PushMgr.registerXimiPush(application)
                }
            }
        } catch (throwable: Throwable) {
            Logger.e(TAG, "onRequestPermissionsResult:{}, {}, {}", permissions, grantResults, throwable)
        }
    }

    var runnable: Runnable = object : Runnable {
        override fun run() {
            // TODO Auto-generated method stub
            //要做的事情
            homePresenter!!.LocationModel()
            mHandler.postDelayed(this, 30 * 60 * 1000)
        }
    }

    data class TabInfo(val mFragmentClass: Class<out BaseFragment>, val iconRes: Int, val textRes: Int)

    interface Presenter {
        fun loadData(clipText: String)

        fun uploadAddShelf()

        fun LocationModel()

        fun destroy()
    }
}
