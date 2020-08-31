package com.zydm.base.widgets

import android.app.Activity
import android.util.SparseArray
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import com.duoyue.lib.base.log.Logger
import com.zydm.base.R
import com.zydm.base.common.BaseApplication
import com.zydm.base.ext.setPaddingTop
import com.zydm.base.ext.setVisible
import com.zydm.base.utils.LogUtils
import com.zydm.base.utils.NetWorkUtils
import com.zydm.base.utils.StringUtils
import com.zydm.base.utils.ViewUtils
import kotlinx.android.synthetic.main.load_prompt_layout.view.*

class PromptLayoutHelper {

    var mPrompt: ViewGroup

    private val mShowLoadingRunnable = Runnable { showLoading() }

    val isVisibility: Boolean
        get() = mPrompt.visibility == View.VISIBLE

    constructor(activity: Activity) {
        initConfig()
        mPrompt = ViewUtils.inflateView(activity, R.layout.load_prompt_layout) as ViewGroup
        initView()
    }

    constructor(promptLayout: View) {
        initConfig()
        this.mPrompt = promptLayout as ViewGroup
        initView()
    }

    private fun initView() {
        this.mPrompt.visibility = View.GONE
    }

    fun showStrMsgPrompt(strMsgId: Int) {
        val info = PromptInfo(R.mipmap.prompt_content_empty, strMsgId, -1)
        showPrompt(info, null)
    }

    fun showPrompt(promptType: Int, btnClickListener: OnClickListener? = null) {
        mPrompt.removeCallbacks(mShowLoadingRunnable)
        val info = getByType(promptType)
        showPrompt(info, btnClickListener)
    }

    private fun showPrompt(info: PromptInfo, btnClickListener: OnClickListener?) {
        LogUtils.d(TAG, "info " + info.toString() + " onclicklistener=" + btnClickListener)
        mPrompt.setVisible(true)
        mPrompt.prompt_info_layout.setVisible(true)
        mPrompt.prompt_info_layout.setOnClickListener(btnClickListener)
        if (info.mIconId > 0) {
            mPrompt.prompt_icon.setVisible(true)
            try
            {
                (mPrompt.prompt_icon as ImageView).setImageResource(info.mIconId)
            } catch (throwable: Throwable)
            {
                Logger.e(TAG, "showPrompt: {}", throwable)
            }
        } else {
            mPrompt.prompt_icon.setVisible(false)
        }

        val mPromptMsg = mPrompt.prompt_text
        if (!StringUtils.isBlank(info.mMsg)) {
            mPromptMsg.setVisibility(View.VISIBLE)
            mPromptMsg.setText(info.mMsg)
        } else {
            mPromptMsg.setVisibility(View.GONE)
        }

        val mPromptBtn = mPrompt.prompt_opt_text
        if (!StringUtils.isBlank(info.mBtnStr)) {
            mPromptBtn.setVisibility(View.VISIBLE)
            mPromptBtn.setText(info.mBtnStr)
        } else {
            mPromptBtn.setVisibility(View.GONE)
        }
        setProgressBarVisibility(false)
    }

    fun showLoading() {
        if (!NetWorkUtils.isNetWorkAvailable(BaseApplication.context.globalContext)) {
            return
        }
        mPrompt.removeCallbacks(mShowLoadingRunnable)
        mPrompt.prompt_info_layout.setVisible(false)
        setProgressBarVisibility(true)
        mPrompt.setVisible(true)
    }

    private fun setProgressBarVisibility(isVisible: Boolean) {
        mPrompt.prompt_progress_layout.setVisible(isVisible)
        if (isVisible) {
            mPrompt.prompt_progress_bar.playAnimation()
        } else {
            mPrompt.prompt_progress_bar.pauseAnimation()
        }
    }

    fun hideLoading() {
        if (mPrompt.prompt_progress_layout.getVisibility() == View.VISIBLE) {
            setProgressBarVisibility(false)
            hide()
        }
    }

    fun hide() {
        mPrompt.removeCallbacks(mShowLoadingRunnable)
        mPrompt.prompt_info_layout.setOnClickListener(null)
        this.mPrompt.setVisible(false)
        setProgressBarVisibility(false)
    }

    private fun getByType(promptType: Int): PromptInfo {
        return sPromptConfig!!.get(promptType)
    }

    fun setTopPadding(topPadding: Int) {
        LogUtils.d(TAG, "setTopPadding ${topPadding}")
        if (topPadding < 0) {
            return
        }
        mPrompt.load_prompt_layout.setPaddingTop(topPadding)
    }

    fun getPromptLayout(): ViewGroup {
        return mPrompt
    }

    private data class PromptInfo(val mIconId: Int = -1, var mMsg: String = "", var mBtnStr: String = "") {

        constructor(iconId: Int, strMsgId: Int, btnTextId: Int): this(iconId) {
            if (strMsgId > 0) {
                this.mMsg = ViewUtils.getString(strMsgId)
            }
            if (btnTextId > 0) {
                this.mBtnStr = ViewUtils.getString(btnTextId)
            }
        }
    }

    companion object {

        private val TAG = "PromptLayoutHelper"

        private const val TYPE_NONE = 0
        const val TYPE_NO_NET = 10
        const val TYPE_DEFAULT_EMPTY = 11
        const val TYPE_NO_PERMISSION = 12


        private var sPromptConfig: SparseArray<PromptInfo>? = null

        private fun initConfig() {
            if (sPromptConfig != null) {
                return
            }
            sPromptConfig = SparseArray()
            sPromptConfig!!.put(TYPE_NO_NET, PromptInfo(R.mipmap.prompt_no_net, R.string.prompt_no_net,
                    R.string.prompt_no_net_refresh))
            sPromptConfig!!.put(TYPE_DEFAULT_EMPTY, PromptInfo(R.mipmap.prompt_content_empty, R.string.prompt_no_data,
                    -1))
            sPromptConfig!!.put(TYPE_NO_PERMISSION, PromptInfo(R.mipmap.prompt_content_empty, R.string.prompt_no_permission,
                -1))
        }
    }
}
