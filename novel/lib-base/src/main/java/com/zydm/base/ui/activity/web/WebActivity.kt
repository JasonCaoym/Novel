package com.zydm.base.ui.activity.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.zydm.base.R
import com.zydm.base.common.Constants
import com.zydm.base.tools.DelayTask
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.utils.LogUtils
import com.zydm.base.utils.NetWorkUtils
import com.zydm.base.utils.StringUtils
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.web_activity.*

class WebActivity : BaseActivity() {

    //    private val mToolbarBack: View? = null
//    private val mToolbarTitle: TextView? = null
//    private var mWebView: WebView? = null
//    private var mUrl: String? = null
    private var mTitle: String? = ""
    //    private var mIsNeedToHome: Boolean = false
    private val mRootLayout: RelativeLayout? = null
    private val mShareTask = DelayTask()
    private val mOnClickListener = View.OnClickListener { this@WebActivity.quitWeb() }
    private val mToolbarRight: ImageView? = null
    //    private var mCloseTv: TextView? = null
    private var mWebViewHelper: WebViewHelper? = null

    private lateinit var mData: Data

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)
        initData()
        initView()
        initWebView()
//        initPrompt()
        setData()
    }

    override fun initActivityConfig(activityConfig: BaseActivity.ActivityConfig) {
        super.initActivityConfig(activityConfig)
        activityConfig.isHandlerBug5497 = true
    }

    private fun initData() {
        mData = intent.getParcelableExtra(BaseActivity.DATA_KEY)
    }

    private fun initView() {
        //        mToolbarBack = findView(R.id.toolbar_back);
        //        mToolbarTitle = findView(R.id.toolbar_title);
        //        mToolbarRight = findViewSetOnClick(R.id.toolbar_right_img);
        //        mToolbarBack.setOnClickListener(mOnClickListener);
        //        addCloseBtn();
        //        mToolbarRight.setImageResource(R.drawable.icon_share_gray);
        //        mToolbarRight.setVisibility(View.VISIBLE);
        //
        //        mRootLayout = findView(R.id.web_root_layout);
//        mWebView = findView(R.id.banner_sec_web)
    }

//    private fun addCloseBtn() {
//        mCloseTv = TextView(this)
//        mCloseTv!!.id = R.id.toolbar_left_tv
//        mCloseTv!!.setPadding(ViewUtils.dp2px(4f), 0, 0, 0)
//        mCloseTv!!.setText(R.string.barrage_close)
//        mCloseTv!!.setTextColor(ViewUtils.getColor(R.color.standard_text_color_light_gray))
//        mCloseTv!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
//        mCloseTv!!.setOnClickListener(this)
//        mCloseTv!!.gravity = Gravity.CENTER_VERTICAL
//        mCloseTv!!.visibility = View.GONE
//        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                ViewUtils.getDimenPx(R.dimen.default_title_h))
//        params.addRule(RelativeLayout.RIGHT_OF, R.id.toolbar_back)
//        (mToolbarBack!!.parent as RelativeLayout).addView(mCloseTv, params)
//    }

//    private fun initPrompt() {
//        val promptView = findView(R.id.layout_prompt)
//        mPromptLayoutHelper = PromptLayoutHelper(promptView)
//    }


    override fun getCurrPageId(): String {
        return "GEN_H5"
    }

    @SuppressLint("JavascriptInterface")
    private fun initWebView() {

        mWebViewHelper = object : WebViewHelper(web_view, this) {
            override fun onTitle(title: String?) {
                super.onTitle(title)
                if (!StringUtils.isBlank(title)) {
                    mTitle = title
                }
                setTitle(mTitle)
            }

            override fun onMoTongScheme(lastPathSegment: String) {
                super.onMoTongScheme(lastPathSegment)
                if (H5_CHAPTER_READ == lastPathSegment) {
//                    CustomEventMgr.getInstance().put(StatisConst.KEY_VIEWNAME, StatisConst.H5_PAGE + mTitle!!)
                } else if (WebViewHelper.H5_SUBJECT_LIST == lastPathSegment) {
                    super@WebActivity.onBackPressed()
                }
            }

            override fun onShowPromptProblem() {
                super.onShowPromptProblem()
                showPromptProblem()
            }

            override fun onCanGoBack(webCanGoBack: Boolean) {
                super.onCanGoBack(webCanGoBack)
//                ViewUtils.setViewVisible(mCloseTv, webCanGoBack)
            }

            override fun onJsShareResult(result: String) {
                super.onJsShareResult(result)
                handleJsShareResult(result)
            }
        }
    }

    private fun setData() {
//        setTitle(mTitle)
        if (NetWorkUtils.isNetWorkAvailable(this)) {
//            mPromptLayoutHelper.hide()
            webLoad(mData.url)
        } else {
            showPromptProblem()
        }
    }

    private fun setTitle(title: String?) {
        val realTitle = if (StringUtils.isBlank(title)) Constants.EMPTY else title
        toolbar_title.text = realTitle
        toolbar_title.text = ""
    }

    private fun showPromptProblem() {
//        if (null != mPromptLayoutHelper) {
//            mPromptLayoutHelper.showPrompt(PromptLayoutHelper.TYPE_NO_NET, View.OnClickListener {
//                if (Utils.hasNetwork()) {
//                    mPromptLayoutHelper.hide()
//                    webLoad(mUrl)
//                }
//            })
//        }
    }

    private fun webLoad(url: String?) {
//        LogUtils.d(TAG, "WebView.loadUrl : " + url!!)
        web_view.loadUrl(url)
//        if (null != mWebView) {
//            mWebView!!.loadUrl(url)
//        }
    }

    override fun onClick(v: View) {

        super.onClick(v)
        when (v.id) {
//            R.id.toolbar_right_img -> {
//                webLoad("javascript:getShareInfo()")
//                mShareTask.doDelay(mShareRunnable, 100)
//            }
//            R.id.toolbar_left_tv -> handleWebNotCanBack()
//            else -> {
//            }
        }
    }

    override fun onBackPressed() {
        quitWeb()
    }

    private fun quitWeb() {
//        LogUtils.d(TAG, mWebView!!.canGoBack().toString() + "mWebView.canGoBack")
//        LogUtils.d(TAG, "mIsSplash : $mIsNeedToHome")
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            handleWebNotCanBack()
        }
    }

    private fun handleWebNotCanBack() {
//        if (mIsNeedToHome) {
//            ActivityHelper.goToHomeActivity(this)
//            super.onBackPressed()
//        } else {
        super.onBackPressed()
//        }
    }

//    override fun getPageName(): String {
//        val pageName: String
//        if (H5UrlHelper.getBecomeAuthorUrl().equals(mUrl)) {
//            pageName = StatisConst.PAGE_BECOME_AUTHOR
//        } else if (H5UrlHelper.getGainMdouUrl().equals(mUrl)) {
//            pageName = StatisConst.PAGE_OPERATION_MANUAL
//        } else if (H5UrlHelper.getUserAgreementUrl().equals(mUrl)) {
//            pageName = StatisConst.PAGE_USER_AGREEMENT
//        } else {
//            pageName = mTitle!! + mUrl!!
//        }
//        return pageName
//    }

    override fun onDestroy() {
        super.onDestroy()
        //webview调用destory时,webview仍绑定在Activity上.
        // 这是由于自定义webview构建时传入了该Activity的context对象,因此需要先从父容器中移除webview,然后再销毁webview:
        web_view.destroy()
//        if (mRootLayout != null) {
//            if (mWebView != null) {
//                //                if (CMApp.getInstance().isTestEnv()) {
//                //                    clearCache();
//                //                }
//                mRootLayout.removeView(mWebView)
//                mWebView!!.destroy()
//            }
//        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.ZERO_NUM -> if (Activity.RESULT_OK == resultCode) {
                h5TokenCallBack(mWebViewHelper!!.functionName)
            }
        }
    }

    private fun h5TokenCallBack(funtionName: String) {
//        webLoad("javascript:" + funtionName + "(" + mWebViewHelper!!.invokeid + Constants.COMMA_SYMBOL +
//                Constants.ZERO + Constants.COMMA_SYMBOL + Constants.QUOTATION_MARKS_SYMBOL + appTokenData
//                + Constants.QUOTATION_MARKS_SYMBOL + ")")
    }

//    @Subscribe
//    fun handleLoginMessage(loginRefreshData: LoginRefreshData) {
//        if (mWebView != null) {
//            mWebView!!.reload()
//        }
//    }

    private fun handleJsShareResult(result: String?) {
        LogUtils.d(TAG, "onShareResult result : " + (result ?: "result is null"))

//        if (StringUtils.isBlank(result)) {
//            mShareBean = null
//        } else {
//            mShareBean = JsonUtils.parseJson(result, WebShareBean::class.java)
//            if (null != mShareBean) {
//                this@WebActivity.runOnUiThread { ViewUtils.setViewVisible(mToolbarRight, !mShareBean.isHideShareBtn()) }
//                mShareTask.executeImmediately()
//            }
//        }
    }

    companion object {

        val TAG = "webtest"

        val WEB_URL = "banner_url"
        val WEB_TITLE = "banner_title"
        private val H5_USER_ID = "userId"
        private val H5_CHAPTER_READ = "chapterRead"
    }

    @Parcelize
    data class Data(var url: String, val title: String) : Parcelable
}
