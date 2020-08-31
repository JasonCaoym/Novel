package com.zydm.base.ui.activity

//import com.umeng.socialize.UMShareAPI
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.duoyue.lib.base.event.AdConfigEvent
import com.duoyue.lib.base.log.Logger
import com.duoyue.lib.base.time.TimeTool
import com.zydm.base.R
import com.zydm.base.common.BaseApplication
import com.zydm.base.common.Constants
import com.zydm.base.data.tools.DataUtils
import com.zydm.base.statistics.umeng.StatisConst
import com.zydm.base.statistics.umeng.StatisHelper
import com.zydm.base.tools.TooFastChecker
import com.zydm.base.utils.StatusBarUtils
import com.zydm.base.utils.ViewUtils
import org.greenrobot.eventbus.EventBus
import java.util.*

abstract class BaseActivity : AppCompatActivity(), OnClickListener {

    companion object {
        const val DATA_KEY = "data_key"
        const val ID_KEY = "id_key"
        var currData: String? = null
    }

    val TAG = this.javaClass.simpleName

    var isOnForeground = false
        private set
    protected var mTooFastChecker = TooFastChecker()
    private var mOnActivityResultListeners: HashSet<OnActivityResultListener>? = null
    private var mHandleBackPresseds: HashSet<IHandleBackPressed>? = null

    protected val activityConfig = ActivityConfig()

    fun getPageName() = StatisConst.PAGE_READ

    val contentView: View
        get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)

    val activity: AppCompatActivity
        get() = this

    var hasShowed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        initActivityConfig(activityConfig)
        super.onCreate(savedInstanceState)

        if (activityConfig.isScreenPortrait) {
            try {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } catch (e: Throwable) {

            }

        }

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setStatusBarFlag()
        setTopActivity()
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        Log.e("App#", "进入分屏()")
    }

    private fun setStatusBarFlag() {

        val window = window
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        //5.0以下不支持透明,依系统颜色
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        //6.0以下不支持改变状态栏文字颜色，系统一般为白色字体，因此使用半透明黑色背景
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = resources.getColor(R.color.half_black_translucent)
            return
        }

        //6.0以上透明背景，字体颜色根据ActivityConfig配置而定
        window.statusBarColor = Color.TRANSPARENT
        if (activityConfig.isStatusBarTextLightColor) {
            ViewUtils.setAndroidMWindowsBarTextWhite(this)
        } else {
            ViewUtils.setAndroidMWindowsBarTextDark(this)
        }
    }

//    open abstract fun pressdHomeKey()

    open fun isTopActivity(): Boolean {
        var isTop = false
        BaseApplication.context.topActivity?.let {
            var cn = BaseApplication.context.topActivity!!.javaClass.simpleName
            Logger.d("App#", "isTopActivity = $cn")
            if (cn.contains(TAG)) {
                isTop = true
                Logger.d("App#", "$TAG 当前界面是顶层ACTIVITY ")
            }
        }
        return isTop
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        initStateBar(findView(R.id.toolbar_layout))
    }

    open fun initStateBar(layoutTitle: View?) {
        if (!activityConfig.isStatusBarTransparent) return
        if (layoutTitle == null) return
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val statusBarHeight = StatusBarUtils.getStatusBarHeight(activity)
                if (activityConfig.isHandlerBug5497) {
                    AndroidBug5497Workaround.assistActivity(this)
                }
                ViewUtils.setPaddingTop(layoutTitle, statusBarHeight)
                onTitleUpdateHeight(statusBarHeight)
            } else
            {
                layoutTitle.fitsSystemWindows = true
            }
        } catch (throwable: Throwable)
        {
            Logger.e(TAG, "initStateBar: {}, {}", layoutTitle, throwable)
        }
    }

    open protected fun onTitleUpdateHeight(statusBarHeight: Int) {

    }

    open protected fun initActivityConfig(activityConfig: ActivityConfig) {

    }

    abstract fun getCurrPageId(): String

    protected fun hideStatusBar() {
        val win = window
        //设置 flag，隐藏状态栏
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    protected fun full(enable: Boolean) {
        if (enable) {
            val lp = window.attributes
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = lp
        } else {
            val attr = window.attributes
            attr.flags = attr.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = attr
        }
    }

    override fun onStart() {
        super.onStart()
        setTopActivity()
        BaseApplication.context.isOnForeground = true
    }


    private fun updateAdConfigByDate() {
        if (currData == null) {
            currData = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02)
        } else if (!currData.equals(TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02))) {
            currData = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02)
            EventBus.getDefault().post(AdConfigEvent(true))
            Logger.e("date_changed", "日期变更，开始更新数据，当前日期: $currData")
        }
    }

    override fun onResume() {
        super.onResume()
        updateAdConfigByDate()
        BaseApplication.context.currPageId = getCurrPageId()
        Logger.e("PageStatsUploadMgr", "当前界面是： " + BaseApplication.context.currPageId)
//        if (activityConfig.isStPage) {
//            StatisHelper.onPageStart(getPageName());
//        }
        StatisHelper.onResume(this);
        setTopActivity()
    }

    override fun onPause() {
        super.onPause()
//        if (activityConfig.isStPage) {
//            StatisHelper.onPageEnd(getPageName());
//        }
        StatisHelper.onPause(this);
    }

    override fun onStop() {
        super.onStop()
        isOnForeground = false
    }

    private fun setTopActivity() {
        BaseApplication.context.topActivity = this
        isOnForeground = true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.toolbar_back -> onTitleBack(v)

            else -> {
            }
        }
    }

    fun changeScreenBrightness(screenBrightness: Float) {
        val window = window
        val lp = window.attributes
        lp.screenBrightness = screenBrightness
        window.attributes = lp
    }

    fun onTitleBack(v: View) {
        onBackPressed()
    }

    fun <V : View> findViewSetOnClick(id: Int): V? {
        val view = findViewById<V?>(id)
        view?.setOnClickListener(this)
        return view
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected fun setStatusBarTransparent() {
        //7.0以上去除黑色透明层
        /*        if(getWindow().getStatusBarColor() != Color.TRANSPARENT) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val decorViewClazz = Class.forName("com.android.internal.policy.DecorView")
                val field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor")
                field.isAccessible = true
                field.setInt(window.decorView, Color.TRANSPARENT)  //改为透明
            } catch (e: Throwable) {
            }

        }

        StatusBarUtils.compat(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //状态栏颜色在透明时设置为深色
        if (!activityConfig.isStatusBarTextLightColor) {
            StatusBarUtils.setStatusBarDarkModeOfVersionM(this)
        }
        StatusBarUtils.MiuiSetStatusBarDarkMode(true, this)
        StatusBarUtils.FlymeSetStatusBarLightMode(window, true)
    }

    fun handleUnloginStatus() {
        finish()
    }

    /**
     * 初始化标题栏，只有包含 toolbar_layout的布局使用。
     *
     * @param title
     */
    protected fun setToolBarLayout(title: String?) {
        findViewSetOnClick<View>(R.id.toolbar_back)
        val toolBarTitle = findView<TextView>(R.id.toolbar_title)
        toolBarTitle!!.text = title ?: Constants.EMPTY
    }

    protected fun <T : View> findView(id: Int): T? {
        return findViewById<T?>(id)
    }

    protected fun setToolBarLayout(title: String, titleColor: Int, titleDpSize: Int) {
        setToolBarLayout(title)
        val toolBarTitle = findView<TextView>(R.id.toolbar_title)
        toolBarTitle!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleDpSize.toFloat())
        toolBarTitle.setTextColor(titleColor)
    }

    protected fun setToolBarLayout(title: String?, @ColorRes color: Int) {
        findViewSetOnClick<View>(R.id.toolbar_back)
        findView<View>(R.id.toolbar_layout)!!.setBackgroundColor(ViewUtils.getColor(color))
        val toolBarTitle = findView<TextView>(R.id.toolbar_title)
        toolBarTitle!!.text = title ?: Constants.EMPTY
    }

    protected fun setToolBarLayout(@StringRes stringId: Int) {
        findViewSetOnClick<View>(R.id.toolbar_back)
        val toolBarTitle = findView<TextView>(R.id.toolbar_title)
        toolBarTitle!!.text =
                if (ViewUtils.getString(stringId) == null) Constants.EMPTY else ViewUtils.getString(stringId)
    }

    protected fun setToolBarLayout(@StringRes stringId: Int, @ColorRes color: Int) {
        findViewSetOnClick<View>(R.id.toolbar_back)
        findView<View>(R.id.toolbar_layout)!!.setBackgroundColor(ViewUtils.getColor(color))
        val toolBarTitle = findView<TextView>(R.id.toolbar_title)
        toolBarTitle!!.text =
                if (ViewUtils.getString(stringId) == null) Constants.EMPTY else ViewUtils.getString(stringId)
    }

    /**
     * Activity配置类
     */
    class ActivityConfig : AppConfig() {

        var isStatusBarTransparent = true
        var isHandlerBug5497 = false
        var isStatusBarTextLightColor = false
        var isScreenPortrait = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!DataUtils.isEmptyList(mOnActivityResultListeners)) {
            val list = ArrayList(mOnActivityResultListeners!!)
            for (onActivityResultListener in list) {
                onActivityResultListener.onActivityResult(requestCode, resultCode, data)
            }
        }
        // 新浪微博需要
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    fun addOnActivityResultListener(onActivityResultListener: OnActivityResultListener?) {
        if (onActivityResultListener == null) {
            return
        }
        if (mOnActivityResultListeners == null) {
            mOnActivityResultListeners = HashSet()
        }
        mOnActivityResultListeners!!.add(onActivityResultListener)
    }

    fun removeOnActivityResultListener(onActivityResultListener: OnActivityResultListener?) {
        if (onActivityResultListener == null || mOnActivityResultListeners == null) {
            return
        }
        mOnActivityResultListeners!!.remove(onActivityResultListener)
    }

    fun addHandleBackPressed(handleBackPressed: IHandleBackPressed?) {
        if (handleBackPressed == null) {
            return
        }
        if (mHandleBackPresseds == null) {
            mHandleBackPresseds = HashSet()
        }
        mHandleBackPresseds!!.add(handleBackPressed)
    }

    fun removeHandleBackPressed(handleBackPressed: IHandleBackPressed?) {
        if (handleBackPressed == null || mHandleBackPresseds == null) {
            return
        }
        mHandleBackPresseds!!.remove(handleBackPressed)
    }

    override fun onBackPressed() {
        if (!DataUtils.isEmptyList(mHandleBackPresseds)) {
            val handleBackPresseds = ArrayList(mHandleBackPresseds!!)
            for (handleBackPressed in handleBackPresseds) {
                if (handleBackPressed.handleBackPressed()) {
                    return
                }
            }
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.context.quitActivity(this)
    }
}