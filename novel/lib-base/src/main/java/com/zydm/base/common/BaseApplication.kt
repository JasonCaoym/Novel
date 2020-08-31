package com.zydm.base.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import com.alibaba.android.arouter.launcher.ARouter
import com.duoyue.lib.base.log.Logger
import com.zydm.base.tools.PhoneStatusManager
import com.zydm.base.ui.ZydmReceiver
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.utils.LogUtils
import io.reactivex.plugins.RxJavaPlugins

/*
    Application 假基类
 */
open class BaseApplication(val application: Application, val listener : Listener){

    open interface Listener {
        fun onForegroundChanged(value: Boolean)
    }

    lateinit var globalContext: Application
    ///lateinit var appComponent: AppComponent
    @Volatile open var currPageId: String = ""
    open var isOnForeground = false
        set(value) {
            if (field != value) {
                field = value
                onForegroundChanged(value)
            }
        }

    var topActivity: BaseActivity? = null

    fun onCreate() {
        globalContext = application
        context = this
        handler = Handler()
        initRxJava()
        //注册广播接收器.
        registerReceiver()
    }

    private fun initRxJava() {
        if (Logger.isDebug())
        {
            //输出ARouter日志.
            ARouter.openLog()
            ARouter.openDebug()
        }
        //阿里-集中式的URL管理框架ARouter初始化.
        ARouter.init(application)
        RxJavaPlugins.setErrorHandler { throwable -> LogUtils.d(BaseApplication.TAG, "RxJavaPlugins:ErrorHandler:", throwable) }
    }

    private fun registerReceiver() {
        val cmReceiver = ZydmReceiver(object : ZydmReceiver.Listener {
            override fun homeKeyPressed() {
                BaseApplication.context.isOnForeground = false
            }
        })
        val homeFilter = IntentFilter(
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS
        )
        application.registerReceiver(cmReceiver, homeFilter)
    }

    /**
     * Application Component初始化
     */
    ///private fun initAppInjection() {
        ///appComponent = DaggerAppComponent.builder().appModule(AppModule(application)).build()
    ///}

    fun isTestEnv(): Boolean {
        return PhoneStatusManager.getInstance().getAppVersionName().endsWith("debug")
    }

    open protected fun onForegroundChanged(value: Boolean) {
        listener.onForegroundChanged(value)
    }

    open fun exit() {
        isOnForeground = false
        topActivity = null
    }

    open fun quitActivity(baseActivity: BaseActivity) {

    }

    /*
        全局伴生对象
     */
    companion object {
        const val TAG = "BaseApplication"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: BaseApplication
        lateinit var handler: Handler
    }
}
