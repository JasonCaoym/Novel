package com.duoyue.mianfei.xiaoshuo.common

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.os.Process
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.duoyue.app.common.mgr.PushMgr
import com.duoyue.app.service.ServiceLauncher
import com.duoyue.lib.base.app.Constants
import com.duoyue.lib.base.devices.PhoneUtil
import com.duoyue.lib.base.log.Logger
import com.duoyue.lib.base.threadpool.ZExecutorService
import com.duoyue.mod.ad.AdConfigManger
import com.duoyue.mod.ad.dao.AdReadConfigHelp
import com.duoyue.mod.ad.utils.AdConstants
import com.duoyue.mod.stats.FuncPageStatsApi
import com.duoyue.mod.stats.FunctionStatsApi
import com.duoyue.mod.stats.ZyStatsApi
import com.huawei.android.hms.agent.HMSAgent
import com.share.platform.ShareConfig
import com.share.platform.ShareManager
///import com.tencent.bugly.crashreport.CrashReport
///import com.tencent.bugly.crashreport.CrashReport
///import com.tencent.bugly.crashreport.CrashReport
import com.vivo.push.PushClient
import com.zydm.base.common.BaseApplication
import com.zydm.base.statistics.umeng.StatisHelper
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.utils.GlideUtils
import com.zydm.base.utils.LogUtils
import com.zydm.base.utils.SPUtils
import com.zydm.base.utils.SPUtils.getBoolean
import com.zydm.base.utils.TimeUtils
import com.zydm.statistics.motong.MtStEventMgr
import com.zydm.statistics.motong.MtStHelper
import java.util.*

class MainApplication : Application(), BaseApplication.Listener {

    companion object {
        /**
         * 是否从后台进入前台
         */
        @Volatile
        open var isBackToForeground = false
        open var stores =  Stack<Activity>()


        private var sInstance: MainApplication? = null

        /**
         * 初始化Application
         */
        fun initApplication()
        {
            if (sInstance != null)
            {
                sInstance?.initCreate()
            }
        }
    }


    /**
     * 是否执行在线时长任务.
     */
    private var isRunOnlineTimeTask: Boolean = false
    /**
     * 当前应用是否在前台运行.
     */
    private var isForeground: Boolean = true

    /**
     *  记录回到后台的时间
     */
    private var back2BgTime = 0L

    private var isFisrtEnter = true

    override fun onCreate() {
        super.onCreate()
        //判断是否调试中.
        if (Debug.isDebuggerConnected())
        {
            //调试中, 直接结束当前应用(isDebuggerConnected函数用于检测此刻是否有调试器挂载到程序上, 如果返回值为true则表示此刻被调试中).
            Process.killProcess(Process.myPid())
            return
        }
        if (PhoneUtil.getCurrentProcessName(applicationContext).equals(applicationContext.packageName)) {
            sInstance = this
            SPUtils.initPres(applicationContext)
            // 初始化假的BaseApplication
            var baseApplication = BaseApplication(this, this)
            baseApplication.onCreate()
            //判断是否已同意用户协议.
            if (getBoolean("userpotodial", false))
            {
                //已同意用户协议.
                initCreate()
            }
        }
    }

    private fun initCreate()
    {
        initApp()
        //异步处理初始化.
        ZExecutorService.getInstance().execute(object : Runnable {
            override fun run() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    try {
                        //延迟500毫秒.
                        Thread.sleep(500)
                    } catch (throwable: Throwable) {
                    }
                }
                MtStHelper.visit()
                //初始化友盟统计.
                StatisHelper.init(applicationContext)
                //初始化掌阅统计SDK.
                ZyStatsApi.init(sInstance)
                //========取消========处理极光推送SDK=================
                JPushInterface.init(applicationContext)
//                    BlockCanary.install(application, AppContext()).start()
            }
        })
    }

    private fun initApp() {
        setActivityCallback()
        // 初始化内存分析工具
        /*if (!LeakCanary.isInAnalyzerProcess(application)) {
            LeakCanary.install(application)
            Logger.w("LeakCanary", "初始化LeakCanary");
        }*/
        ///CrashReport.initCrashReport(this, "fd896bd2d7", true)
        ServiceLauncher.init(this)
        //UiThreadBlockWatcher.install(3000, UiThreadBlockWatcher.TYPE_LOOPER);
        //初始化广告数据.
//        AdManager.getInstance().initPlatform(application)
        AdConfigManger.getInstance().init(this)
        //ShareManger.getsInstance().initEachPlatform(application)
        //初始化share
        val config =
            ShareConfig.instance().qqId(Constants.QQ_APP_ID).wxId(Constants.WX_APP_ID).weiboId(Constants.WEIBO_APP_KEY)
        //下面两个, 如果不需要登录功能, 可不填写
        //.weiboRedirectUrl("http://open.weibo.com/apps/497760849/privilege/oauth")
        //.wxSecret("8dc377e352fcbd385767788172273ebd")
        ShareManager.init(config)
        initPush()
    }

    var isAdActRestart: Boolean = false

    private fun setActivityCallback() {
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                if (activity != null) {
                    stores.add(activity)
                    isAdActRestart = false
                    if (activity != null && activity !is BaseActivity) {
                        SPUtils.putBoolean(SPUtils.REWARD_COMPLETE, false)
                        SPUtils.putBoolean(SPUtils.REWARD_CLICKED, false)
                    }
                }
            }
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
                if (activity != null && activity !is BaseActivity) {
                    if (isAdActRestart && SPUtils.getBoolean(SPUtils.REWARD_COMPLETE, false)
                        && SPUtils.getBoolean(SPUtils.REWARD_CLICKED, false)) {
                        activity.finish()
                    }
                }
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
                if (activity != null) {
                    stores.remove(activity)
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
                if (activity != null && activity !is BaseActivity) {
                    isAdActRestart = true
                }
            }
        })
    }

    private fun initPush() {
        val phoneBrand = PushMgr.getPhoneBrand()
        if (TextUtils.equals(phoneBrand, "HUAWEI")) {
            //===================初始化华为推送SDK=================
            HMSAgent.init(this)
        } else if (TextUtils.equals(phoneBrand, "vivo")) {
            if (PushClient.getInstance(applicationContext).isSupport) {
                //===================初始化vivo推送SDK=================
                PushClient.getInstance(this).initialize()
                PushMgr.openVivoPush()
            }
        } else if (TextUtils.equals(phoneBrand, "OPPO")) {
            Logger.d("MainApplication", "oppo推送注册开始")
            PushMgr.initOppoPush()
        } else if (TextUtils.equals(phoneBrand, "Xiaomi")) {
            PushMgr.registerXimiPush(this)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Logger.e("ad#Extr", "------------检测到内存消耗过大，强制清除图片内存---------------")
        GlideUtils.cleanMemory(applicationContext)
    }

//    override fun onBaseContextAttached(base: Context?) {
//        super.onBaseContextAttached(base)
//        fix()
//    }

    override fun onForegroundChanged(onForeground: Boolean) {
        isForeground = onForeground
        // 统计后台启动次数
        statsBackgroundLaucnher(isForeground)
        if (isForeground) {
            LogUtils.initDebugLogSwitch()
            if (!MtStHelper.visit()) {
                MtStEventMgr.getInstance().upload()
            }
        } else {
            //到后台重新刷新一个push列表
            MtStEventMgr.getInstance().upload()
        }
        isBackToForeground = onForeground
        Logger.e("PageStatsUploadMgr", "目前在当前界面吗？ " + isForeground)
        //执行在线时长统计
        execOnlineTime()
//        NotificationHolderService.mABoolean =
//            SharePreferenceUtils.getBoolean(application, SharePreferenceUtils.IS_FIRST_IN, true)
    }

    /**
     * 统计后台启动次数，间隔次数N分钟，由后台下发至广告配置接口
     */
    private fun statsBackgroundLaucnher(onForeground: Boolean) {
        if (!isFisrtEnter && onForeground) {
            var intervalTime = AdReadConfigHelp.getsInstance().getValueByKey(
                AdConstants.ReadParams.PAGE_STTCNB_TIME,
                5
            ) * TimeUtils.MINUTE_1
            if (System.currentTimeMillis() - back2BgTime >= intervalTime) {
                FuncPageStatsApi.backToForeground()
            }
        }
        isFisrtEnter = false
        back2BgTime = System.currentTimeMillis()
    }

    /**
     * 执行在线时长功能统计.
     */
    private fun execOnlineTime() {
//        var count: Int =
//            if (SharePreferenceUtils.getBoolean(application, SharePreferenceUtils.IS_FIRST_IN, true)) 10 else 120
        //判断是否为前台.
        if (isForeground && !isRunOnlineTimeTask) {
            //前台, 启动异步线程.
            ZExecutorService.getInstance().execute(object : Runnable {
                override fun run() {
                    //设置任务执行中.
                    isRunOnlineTimeTask = true
                    while (isForeground) {
                        //遍历120次, 每次间隔500毫秒.
                        for (index in 0..120) {
                            try {
                                Thread.sleep(500)
                            } catch (throwable: Throwable) {
                            }
                            //判断是否已切换到后台.
                            if (!isForeground) {
                                //设置任务执行结束.
                                isRunOnlineTimeTask = false
                                //直接返回即可.
                                return
                            }
                        }
                        //累计在线时长.
                        FunctionStatsApi.onlineTime()
                        FuncPageStatsApi.onlineTime(BaseApplication.context.currPageId)
                    }
                    //设置任务执行结束.
                    isRunOnlineTimeTask = false
                }
            })
        }
    }

    //参数设置
    /*class AppContext : BlockCanaryContext() {
        private val TAG = "AppContext"

        override
        fun provideQualifier(): String {
            var qualifier = "";
            try {
                var info = BaseApplication.context.globalContext.getPackageManager()
                    .getPackageInfo(BaseApplication.context.globalContext.getPackageName(), 0);
                qualifier += "" + info.versionCode + "_" + info.versionName + "_YYB";
            } catch (e: PackageManager.NameNotFoundException) {
                Logger.e(TAG, "provideQualifier exception", e);
            }
            return qualifier;
        }

        override
        fun provideBlockThreshold(): Int
        {
            return 500;
        }

        override
        fun displayNotification(): Boolean
        {
            return true;
        }

        override
        fun stopWhenDebugging(): Boolean
        {
            return false;
        }
    }*/
}
