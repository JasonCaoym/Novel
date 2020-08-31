package com.duoyue.mianfei.xiaoshuo.read.ui.read

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Handler
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.duoyue.app.common.mgr.TaskMgr
import com.duoyue.app.ui.activity.TaskWebViewActivity
import com.duoyue.app.ui.fragment.SimpleDialogFragment
import com.duoyue.app.ui.view.VideoEnterDialog
import com.duoyue.lib.base.app.Constants
import com.duoyue.lib.base.devices.PhoneUtil
import com.duoyue.lib.base.log.Logger
import com.duoyue.lib.base.time.TimeTool
import com.duoyue.lib.base.widget.AdFrameLayout
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity
import com.duoyue.mod.ad.AdConfigManger
import com.duoyue.mod.ad.bean.AdSiteBean
import com.duoyue.mod.ad.dao.AdReadConfigHelp
import com.duoyue.mod.ad.listener.AdCallbackListener
import com.duoyue.mod.ad.net.AdHttpUtil
import com.duoyue.mod.ad.platform.IAdView
import com.duoyue.mod.ad.platform.csj.CSJBannerModel
import com.duoyue.mod.ad.platform.csj.CSJInfoFlowNative
import com.duoyue.mod.ad.platform.gdt.GDTInfoFlowModel
import com.duoyue.mod.ad.platform.url.BaseUrlAdView
import com.duoyue.mod.ad.utils.AdConstants
import com.duoyue.mod.stats.common.FunPageStatsConstants
import com.zydm.base.common.BaseApplication
import com.zydm.base.data.bean.BookRecordGatherResp
import com.zydm.base.ext.setVisible
import com.zydm.base.utils.SPUtils
import com.zydm.base.utils.SharePreferenceUtils
import com.zydm.base.utils.ToastUtils
import com.zydm.base.utils.ViewUtils
import com.zydm.base.widgets.MTDialog
import org.greenrobot.eventbus.EventBus

@SuppressLint("StaticFieldLeak")
class ExtraPageMgr {
    private val TAG = "ad#ExtraPageMgr"
    companion object {
        const val REQUEST_TIME = 20_000L
    }
    private var activity : FragmentActivity? = null
    private var index : Int = 0
    open var hasShowVideo = false
    private var resource: Resources? = null
    private var adPadding = ViewUtils.dp2px(10f)
    private var hasRetried = false

    open var bannerView: IAdView? = null
    private var infoFlowView: IAdView? = null
    open var freeDuration: Int? = null
    private var tvTitle: TextView? = null
    private var mIsNightMode: Boolean = false
    private var adClickInterval = 30_000L
    open var clickedPagePosList = HashMap<String, Long>()
    private var statSource = ""
    private var readAdContainer: AdFrameLayout? = null
    private lateinit var bannerRootView: AdFrameLayout
    private lateinit var mCloseView: View
    open var hasShowBanner = false
    private var hasChapterEndRetry = false
    open var chapterEndView: IAdView? = null
    open var chapterEndLoadSucc = false
    open var flowAdLoadSucc = false
    private var clearBannerCacheNum = 4


    fun init(activity: FragmentActivity, source : String) {
        adClickInterval = SPUtils.getLong(AdConstants.ReadParams.AD_COMM_CLICK_INTERVAL, 30) * 1000
        resource = activity.resources
        statSource = source
        initChapterEndPage(activity)
        clearBannerCacheNum = SPUtils.getInt(AdConstants.ReadParams.RD_BANNER_CLEAR, 4)
    }

    private fun initChapterEndPage(activity: FragmentActivity) {
        this.activity = activity
    }

    fun setClearBannerCacheNum(count: Int) {
        clearBannerCacheNum = count
    }

    fun setNightMode(videoView: ViewGroup, nightMode: Boolean) {
        setColor(videoView, nightMode)
    }

    open fun clearClickedData() {
        clickedPagePosList.clear()
    }

    open fun disableAdClick(key: String) : Boolean {
        adClickInterval = SPUtils.getLong(AdConstants.ReadParams.AD_COMM_CLICK_INTERVAL, 30) * 1000
        return if (clickedPagePosList.containsKey(key)) {
            var lastTime = clickedPagePosList[key]!!
            var disabled = System.currentTimeMillis() - lastTime <= adClickInterval
            Logger.e(TAG, "广告禁止点击：$disabled")
            disabled
        } else if (AdConfigManger.getInstance().showAd(activity, Constants.channalCodes[2]) == null) {
            false
        } else {
            Logger.e(TAG, "广告是可以点击")
            false
        }
    }

    private fun setColor(videoView: ViewGroup, isNightMode: Boolean) {
        try {
            mIsNightMode = isNightMode
            if (isNightMode) {
                // 激励视频入口
                videoView?.findViewById<TextView>(R.id.ad_native_play).setBackgroundResource(R.drawable.video_btn_night_bg)
                videoView?.findViewById<TextView>(R.id.ad_native_play).setTextColor(activity!!.resources!!.getColor(R.color.color_A4A3A8))
                var drawable = activity!!.resources.getDrawable(R.mipmap.icon_play_night)
                drawable.setBounds(ViewUtils.dp2px(47f), 0, ViewUtils.dp2px(63f), ViewUtils.dp2px(16f))    //需要设置图片的大小才能显示
                videoView?.findViewById<TextView>(R.id.ad_native_play).setCompoundDrawables(
                    drawable, null, null, null)
                videoView?.findViewById<TextView>(R.id.ad_native_rule).setTextColor(activity!!.resources!!.getColor(R.color.color_A4A3A8))
            } else {
                // 激励视频入口
                videoView?.findViewById<TextView>(R.id.ad_native_play).setBackgroundResource(R.drawable.dialog_btn_bg_positive)
                videoView?.findViewById<TextView>(R.id.ad_native_play).setTextColor(activity!!.resources!!.getColor(R.color.white))
                var drawable = activity!!.resources.getDrawable(R.mipmap.ad_play)
                drawable.setBounds(ViewUtils.dp2px(47f), 0, ViewUtils.dp2px(63f), ViewUtils.dp2px(16f))    //需要设置图片的大小才能显示
                videoView?.findViewById<TextView>(R.id.ad_native_play).setCompoundDrawables(
                    drawable, null, null, null)
                videoView?.findViewById<TextView>(R.id.ad_native_rule).setTextColor(activity!!.resources!!.getColor(R.color.read_chapter_content))
            }
            if (infoFlowView != null && infoFlowView is GDTInfoFlowModel/*origin == AdConstants.Source.GDT*/) {
                tvTitle = null
            } else if (infoFlowView != null && infoFlowView is BaseUrlAdView) {
                (infoFlowView as BaseUrlAdView).updateDayModel(isNightMode)
                tvTitle = null
            } else if (infoFlowView != null && infoFlowView is CSJInfoFlowNative) {
                (infoFlowView as CSJInfoFlowNative).updateDayModel(isNightMode)
            } else if (chapterEndView != null && chapterEndView is CSJInfoFlowNative) {
                (chapterEndView as CSJInfoFlowNative).updateDayModel(isNightMode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            Logger.e(TAG, "刷新报错：" + e)
        }
    }

    private fun showDialog(activity: Activity, msg : String) {
        val dialog = MTDialog(activity)
        dialog.setBackgroundImg(R.mipmap.ad_i_know)
        dialog.setMessage(msg)
        dialog.setPositiveButton(R.string.i_know, DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            if (isFreeTime()) {
                EventBus.getDefault().post(ReadUpdateEvent())
            }
            if(activity is ReadActivity) {
                activity.hideSystemBar()
            }
        })
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    var dialog : Dialog? = null
    var rewardDialog : VideoEnterDialog? = null

    private fun showLoadingDialog(show : Boolean) {
        if (dialog == null) {
            dialog = Dialog(activity!!, R.style.CustomDialog)
            var view = LayoutInflater.from(activity!!).inflate(R.layout.dialog_loading, null)
            dialog!!.setContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
        }
        if (show) {
            dialog!!.show()
        } else {
            dialog!!.hide()
            if(activity is ReadActivity) {
                (activity as ReadActivity).hideSystemBar()
            }
        }
    }

    private fun showTimeoutTip() {
        showLoadingDialog(false)
        showDialog(activity!!, activity!!.getString(R.string.ad_video_load_error))

    }

    fun isFreeTime() : Boolean {
        // 判断免广告
        return AdConfigManger.getInstance().isAdFreeTime(Constants.channalCodes[1])
    }

    // 停留在当前页不动，需定时刷新
    var clickRunnable = Runnable {
        readAdContainer?.setDisabled(false)
    }

    fun initVideoView(videoView: ViewGroup, handler: Handler, containerView: ViewGroup) {
        freeDuration = AdReadConfigHelp.getsInstance()?.getValueByKey(AdConstants.ReadParams.FLOW_FREE_DURATION, 30)
//        freeDuration = SPUtils.getInt(AdConstants.ReadParams.FLOW_FREE_DURATION, 15)
        var freeTime = freeDuration
//        videoView.findViewById<TextView>(R.id.ad_native_play).text = (String.format(activity.getString(com.zzdm.ad.R.string.ad_free_time), "" + freeDuration))
        videoView.findViewById<TextView>(R.id.ad_native_play).text = activity!!.getString(com.zzdm.ad.R.string.close_ad_now)
        videoView.findViewById<TextView>(R.id.ad_native_play).setOnClickListener {
            showRewardVideo(handler, containerView, freeTime!!, Constants.channalCodes[3], FunPageStatsConstants.READ_WORD_AD, "1", statSource)
        }
        videoView.findViewById<TextView>(R.id.ad_native_rule).setOnClickListener {
            showRuleDialog(freeTime!!.toInt())
        }
    }

    /**
     * 阅读器信息流广告
     */
    fun showInfoFlowAd(activity: Activity, handler: Handler, containerView : AdFrameLayout, tvTip : TextView, videoView: View,
                       channelCode: String,adSiteBean: AdSiteBean) {
        if (handler == null) {
            return
        }
        readAdContainer = containerView
        containerView.removeAllViews()
        containerView.setPadding(0, 0, 0, 0)
        // 阅读器激励视频入口
        videoView.setVisible(false)
        flowAdLoadSucc = false
        freeDuration = AdReadConfigHelp.getsInstance()?.getValueByKey(AdConstants.ReadParams.FLOW_FREE_DURATION, 15)
//        freeDuration = SPUtils.getInt(AdConstants.ReadParams.FLOW_FREE_DURATION, 15)
        var freeTime = freeDuration
//        videoView.findViewById<TextView>(R.id.ad_native_play).text = (String.format(activity.getString(com.zzdm.ad.R.string.ad_free_time), "" + freeDuration))
        videoView.findViewById<TextView>(R.id.ad_native_play).text = activity.getString(com.zzdm.ad.R.string.close_ad_now)
        videoView.findViewById<TextView>(R.id.ad_native_play).setOnClickListener {
            showRewardVideo(handler, containerView, freeTime!!, Constants.channalCodes[3], FunPageStatsConstants.READ_WORD_AD, "1", statSource)
        }
        videoView.findViewById<TextView>(R.id.ad_native_rule).setOnClickListener {
            showRuleDialog(freeTime!!.toInt())
        }
        //---------穿山甲模版信息流广告-----------
        /*adSiteBean.adAppId = "5017318"
        adSiteBean.adId = "917318680"
        adSiteBean.adType = AdConstants.Type.INFORMATION_FLOW
        adSiteBean.origin = AdConstants.Source.CSJ*/
        //---------广点通信息流大图广告-----------
        /*adSiteBean.adAppId = "1109680909"
        adSiteBean.adId = "9000199276001365"
        adSiteBean.aspectRatio = 2
        adSiteBean.adType = AdConstants.Type.INFORMATION_FLOW
        adSiteBean.origin = AdConstants.Source.GDT*/
        //---------广点通信息流(测试)广告-----------
        /*adSiteBean.adAppId = "1101152570"
        adSiteBean.adId = "7030020348049331"
        adSiteBean.aspectRatio = 2
        adSiteBean.adType = AdConstants.Type.INFORMATION_FLOW
        adSiteBean.origin = AdConstants.Source.GDT*/

        if (adSiteBean != null) {
            if (adSiteBean.origin == AdConstants.Source.GDT) {
                containerView.setPadding(adPadding, adPadding, adPadding, adPadding)
            }
        }
        // 先销毁
        infoFlowView?.destroy()
        infoFlowView = AdConfigManger.getInstance().getAdView(activity, channelCode, adSiteBean)
        infoFlowView?.init(containerView, null, 30, object : AdCallbackListener {
            override fun onAdTick(time: Long) {}

            override fun pull(adSiteBean : AdSiteBean) {}

            override fun pullFailed(adSiteBean : AdSiteBean, code: String, msg: String?) {
                flowAdLoadSucc = false
                handler?.post {
                    infoFlowView?.destroy()
                    infoFlowView = null
                    videoView.setVisible(false)
                    if (!hasRetried) {
                        hasRetried = true
//                        var nextAdSiteBean = getNextAdSiteBean(Constants.channalCodes[2], adSiteBean.id)
                        var nextAdSiteBean = AdConfigManger.getInstance().showAd(activity, Constants.channalCodes[2])
                        if (nextAdSiteBean != null) {
                            Logger.e(TAG, "-------阅读器信息流广告重试------")
                            AdHttpUtil.retryInfo(adSiteBean)
                            showInfoFlowAd(activity, handler, containerView, tvTip, videoView, channelCode, nextAdSiteBean)
                        } else {
                            Logger.e(TAG, "-------阅读器信息流广告不重试，找不到有效的广告------")
                            if (activity is ReadActivity && !activity.isCurChapterEndAd) {
                                tvTip.setVisible(true)
                                chapterEndLoadSucc = false
                                containerView.setVisible(false)
                                containerView.setPadding(0, 0, 0, 0)
                            }
                        }
                    } else {
                        hasRetried = false
                        Logger.e(TAG, "----------阅读器信息流广告重试----还是没有广告")
                        if (activity is ReadActivity && !activity.isCurChapterEndAd) {
                            tvTip.setVisible(true)
                            chapterEndLoadSucc = false
                            containerView.setVisible(false)
                            containerView.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                Logger.e("show_flow", "信息流显示拉领失败了")
            }

            override fun onShow(adSiteBean : AdSiteBean?) {
                flowAdLoadSucc = true
                chapterEndLoadSucc = false
                hasRetried = true
                handler?.post {
                    clickedPagePosList.clear()
                    containerView.setDisabled(false)
                    tvTip.setVisible(false)
                    containerView.setVisible(true)
                    containerView.scaleX = 1f
                    containerView.scaleY = 1f
                    containerView.setPadding(adPadding, adPadding, adPadding, adPadding)
                    // 避免广告重试导致广告白天和夜间模式失效
                    setNightMode(videoView as ViewGroup, mIsNightMode)
                    handler?.postDelayed(Runnable {
                        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        containerView.measure(width, height)
                        Logger.e(
                            "ad#Ext",
                            "width = " + containerView.getMeasuredWidth() + ", height = " + containerView.getMeasuredHeight()
                        )
                        val adParams = containerView.getLayoutParams() as LinearLayout.LayoutParams
                        var viewHeight = containerView.getMeasuredHeight()
                        var chapterEndAdHeight = (activity as ReadActivity).pageEndBlankHeight
                        if (activity.isCurChapterEndAd && chapterEndAdHeight < viewHeight) {
                            adParams.height = viewHeight
                            val scaleY = chapterEndAdHeight / viewHeight
                            containerView.setScaleX(scaleY)
                            containerView.setScaleY(scaleY)
                            containerView.setPivotX((PhoneUtil.getScreenSize(activity)[0] / 2).toFloat())
                            containerView.setPivotY(0f)
                            containerView.invalidate()
                            Logger.e("ad#ReadActivity", "Extr 显示章节末广告，信息流广告加载成功,缩放比： " + scaleY + ", viewHeight : " + viewHeight + ", chapterEndAdHeight : " + chapterEndAdHeight)
                        } else if (viewHeight * 100f / PhoneUtil.getScreenSize(activity)[1] > 60) {
                            if (viewHeight * 100f / PhoneUtil.getScreenSize(activity)[1] > 70) {
                                adParams.height = (PhoneUtil.getScreenSize(activity)[1] * 0.7f).toInt()
                            } else {
                                adParams.height = viewHeight
                            }
                        } else {
                            adParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                        }
                        if (!activity.isCurChapterEndAd) {
                            videoView.setVisible(AdConfigManger.getInstance().showAd(activity,
                                Constants.channalCodes[3]) != null)
                            Logger.e("show_flow", "信息流：videoView是否可以显示：${AdConfigManger.getInstance().showAd(activity,
                                Constants.channalCodes[3]) != null}")
                        } else {
                            videoView.setVisible(false)
                        }
                        containerView.setLayoutParams(adParams)
                    }, 300)
                }
                Logger.e("show_flow", "信息流显示成功了")
            }

            override fun onClick(adSiteBean : AdSiteBean?) {
                Logger.e(TAG, "阅读器信息流被点击了")
                handler?.removeCallbacks(clickRunnable)
                handler?.post {
                    if (containerView.tag != null) {
                        var pos = containerView.tag as Int
                        Logger.e(TAG, "点击时获取到的位置是： $pos")
                        clickedPagePosList.put("$pos", System.currentTimeMillis())
                        containerView.setDisabled(true)
                    }
                }

                handler?.postDelayed(clickRunnable, adClickInterval)
            }

            override fun onError(adSiteBean : AdSiteBean, code: String, msg: String?) {
                flowAdLoadSucc = false
                handler?.post {
                    infoFlowView?.destroy()
                    infoFlowView = null
                    videoView.setVisible(false)
                    if (!hasRetried) {
                        hasRetried = true
//                        var nextAdSiteBean = getNextAdSiteBean(Constants.channalCodes[2], adSiteBean.id)
                        var nextAdSiteBean = AdConfigManger.getInstance().showAd(activity, Constants.channalCodes[2])
                        if (nextAdSiteBean != null) {
                            Logger.e(TAG, "-------阅读器信息流广告重试------")
                            AdHttpUtil.retryInfo(adSiteBean)
                            showInfoFlowAd(activity, handler, containerView, tvTip, videoView, channelCode, nextAdSiteBean)
                        } else {
                            Logger.e(TAG, "-------阅读器信息流广告不重试，找不到有效的广告------")
                            if (activity is ReadActivity && !activity.isCurChapterEndAd) {
                                tvTip.setVisible(true)
                                chapterEndLoadSucc = false
                                containerView.setVisible(false)
                                containerView.setPadding(0, 0, 0, 0)
                            }
                        }
                    } else {
                        hasRetried = false
                        Logger.e(TAG, "-----------阅读器信息流广告重试----还是没有广告")
                        if (activity is ReadActivity && !activity.isCurChapterEndAd) {
                            tvTip.setVisible(true)
                            chapterEndLoadSucc = false
                            containerView.setVisible(false)
                            containerView.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                Logger.e("show_flow", "信息流显示出错，失败了")
            }

            override fun onDismiss(adSiteBean : AdSiteBean?) {
                handler?.post {
                    tvTip.setVisible(false)
                }
            }
        })
        infoFlowView?.showAd()
    }

    fun showRuleDialog(freeDuration : Int) {
        showDialog(activity!!, String.format(activity!!.getString(R.string.ad_free_time_tip), freeDuration))
    }

    fun showRewardVideo(handler: Handler, containerView : ViewGroup?, freeDuration : Int, channalCode: String, prePageId: String,
                        modelId: String, source: String) {
        Logger.e(TAG, "传给激励视频弹框的时间是：$freeDuration")
        hasShowVideo = false
        showLoadingDialog(true)
        // 超时判断
        var adSiteBean = AdConfigManger.getInstance().showAd(activity, channalCode)
        if (adSiteBean != null) {
            var iAdView = AdConfigManger.getInstance().getAdView(activity, channalCode, adSiteBean)
            iAdView.setStatParams(prePageId, modelId, source)
            iAdView?.init(null, null, 30, object : AdCallbackListener {
                override fun onAdTick(time: Long) {}

                override fun pull(adSiteBean : AdSiteBean) {}


                override fun pullFailed(adSiteBean : AdSiteBean, code: String, msg: String?) {
                    hasShowVideo = false
                    handler?.post{
                        showTimeoutTip()
                    }
                }

                override fun onShow(adSiteBean : AdSiteBean) {
                    showLoadingDialog(false)
                    hasShowVideo = true
                    if (activity is ReadActivity && !channalCode.equals(Constants.channalCodes[10])) {
                        ToastUtils.showRewardVideoToast("看完视频即可无限免广告")
                    }
                }

                override fun onClick(adSiteBean : AdSiteBean) {
                    /*if (iAdView != null && iAdView is AbstractAdView) {
                        if (iAdView.isVideoComplete) {
                            MainApplication.stores.peek().finish()
                        }
                    }*/
                }

                override fun onError(adSiteBean : AdSiteBean, code: String, msg: String?) {
                    hasShowVideo = false
                    handler?.post {
                        showTimeoutTip()
                    }
                }

                override fun onDismiss(adSiteBean : AdSiteBean) {
                    if (hasShowVideo) {
                        hasShowVideo = false
                        // 任务中心的激励任务不需要弹窗
                        if (activity is TaskWebViewActivity) {
                            TaskMgr.show(
                                activity, activity!!.getSupportFragmentManager(),
                                activity!!.getString(R.string.finish_reward_video_task), TaskWebViewActivity.mTaskId
                            )
                        } else if (activity is HomeActivity) {
                            TaskMgr.show(
                                activity, activity!!.getSupportFragmentManager(),
                                activity!!.getString(R.string.finish_reward_video_task), TaskMgr.REWARD_VIDEO_EXIT_TASK
                            )
                        } else if (channalCode.equals(Constants.channalCodes[10])) {
                            TaskMgr.show(
                                activity, activity!!.getSupportFragmentManager(),
                                activity!!.getString(R.string.finish_reward_video_task), ReadActivity.gRewarTastId
                            )
                        } else {
                            showFreeTimeDialog(handler, containerView, freeDuration)
                        }
                    } else {
                        hasShowVideo = false
                        showLoadingDialog(false)
                        ToastUtils.show(R.string.ad_video_load_error)
                    }

                }
            })
            iAdView.showAd()
        }
    }

    /**
     * 展示横幅激励视频广告入口对话框
     */
    fun showRewardVideoEnterDialog(handler: Handler, containerView : ViewGroup) {
        if (rewardDialog == null) {
            freeDuration = SPUtils.getInt(AdConstants.ReadParams.RD_BANNER_FREE, 30)
            if (freeDuration == null) {
                freeDuration = 30
            }
            var freeTime = freeDuration
            rewardDialog = VideoEnterDialog(activity, freeDuration!!, View.OnClickListener {
                rewardDialog?.cancel()
                showRewardVideo(handler, containerView, freeTime!!, Constants.channalCodes[4], FunPageStatsConstants.CLOSE_ADPOP, "2", statSource)
            }, View.OnClickListener {
                showRuleDialog(freeTime!!)
            })
            rewardDialog?.setCanceledOnTouchOutside(false)
        }
        rewardDialog?.show()
    }

    fun showFreeTimeDialog(handler: Handler, containerView : ViewGroup?, durationTime: Int) {
        handler?.post {
            var tip = ""
            // 是否处在免广告时间内
            containerView?.setVisible(false)
            showLoadingDialog(false)
            var start = 0
            if (!AdConfigManger.getInstance().isAdFreeTime(Constants.channalCodes[1])) {
                activity!!.getSharedPreferences(AdConstants.PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
                    .putLong(AdConstants.KEY_FREE_START_TIME, System.currentTimeMillis())
                    .putLong(AdConstants.CURR_FREE_TIME, durationTime.toLong())
                    .apply()
                tip = String.format("已成功免除%s分钟广告啦", durationTime)
                start = 5
            } else { // 正在免广告期间
                var preference = activity!!.getSharedPreferences(AdConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
                var editor = preference.edit()
                var preFreeTime = preference.getLong(AdConstants.CURR_FREE_TIME, 15)
                // 计算免广告时长
                val bookRecordGatherResp = SharePreferenceUtils.getObject<BookRecordGatherResp>(
                    BaseApplication.context.globalContext, SharePreferenceUtils.READ_HISTORY_CACHE)
                // 特权卡
                if (bookRecordGatherResp != null && bookRecordGatherResp.lastSec > 0) {
                    // 当前时间加上剩余秒
                    var freeTimeStart = System.currentTimeMillis() + bookRecordGatherResp.lastSec * 1000
                    editor.putLong(AdConstants.KEY_FREE_START_TIME, freeTimeStart)
                    editor.putLong(AdConstants.CURR_FREE_TIME, durationTime.toLong())
                    Logger.e(TAG, "正在使用免广告特权卡： 还剩${bookRecordGatherResp.lastSec} 秒，" +
                            "使用免广告时长起始时间是： ${TimeTool.timeToData(freeTimeStart, TimeTool.DATE_FORMAT_FULL_01)}， " +
                            "免广告${(durationTime.toLong())}分钟")
                } else {
                    editor.putLong(AdConstants.CURR_FREE_TIME, (durationTime.toLong() + preFreeTime))
                    Logger.e(TAG, "正在免广告，累计总免广告时长： ${(durationTime.toLong() + preFreeTime)}分钟")
                }
                editor.apply()
                tip = "您的免广告特权延长了${durationTime.toLong()}分钟"
                start = 10
                // 给书豆
                /*TaskMgr.show(
                    activity, activity!!.getSupportFragmentManager(),
                    activity!!.getString(R.string.finish_reward_video_task), TaskMgr.TIRED_REWARD_VIDEO_TASK
                )*/
            }
            if (activity != null && !activity!!.isFinishing) {
                showRewardSuccussDialog(R.string.congratulations, tip, start, durationTime.toString().length)
            }
        }
    }

    private fun showRewardSuccussDialog(title: Int, msg: String, start: Int, size: Int) {
        val dialogFragment = SimpleDialogFragment()
            .title(title)
            .messageWithColor(msg, resource!!.getColor(R.color.standard_red_main_color_c1), start, start + size)
            .confirm(R.string.know)
        dialogFragment.setOnExitAppListener(object : SimpleDialogFragment.OnExitAppListener {
            override fun onCancel(dialog: DialogInterface) {
                dialog.dismiss()
            }

            override fun onConfirm(dialog: DialogInterface) {
                dialog.dismiss()
                if (isFreeTime()) {
                    EventBus.getDefault().post(ReadUpdateEvent())
                }
                if(activity is ReadActivity) {
                    (activity as ReadActivity).hideSystemBar()
                }
            }

            override fun onDismiss(dialog: DialogInterface) {
            }
        })
        activity!!.supportFragmentManager.beginTransaction().add(dialogFragment, "reward_dialog").commitAllowingStateLoss()
    }

    var bannerClickRunnable = Runnable {
        bannerRootView?.setDisabled(false)
    }

    /**
     * 阅读器底部banner广告
     */
    fun showBottomBannerAd(bottomWhiteView: View, bannerContainer: AdFrameLayout, closeView: View, handler: Handler, channelCode: String, adSiteBean : AdSiteBean?) {
        if (!PhoneUtil.isNetworkAvailable(activity)) {
            Logger.e(TAG, "showBottomBannerAd -- 没有网络了")
            return
        }
        bannerRootView = bannerContainer
        mCloseView = closeView
        bannerContainer.setVisible(true)
        var canRequestNextAd = false
        if (adSiteBean != null){
            if (!hasShowBanner) {
                bannerContainer.removeAllViews()
                canRequestNextAd = true
            }
            bannerView?.destroy()
            hasShowBanner = false
            bannerView = AdConfigManger.getInstance().getAdView(activity, channelCode, adSiteBean)
            bannerView?.init(bannerContainer, null, 0, object : AdCallbackListener {
                override fun onAdTick(time: Long) {}

                override fun pull(adSiteBean: AdSiteBean) {}

                override fun pullFailed(adSiteBean: AdSiteBean, code: String, msg: String) {
                    // 成功后可能也回调失败
                    if (hasShowBanner) {
                        return
                    }
                    if (adSiteBean != null && adSiteBean.origin == 1) {
                        bannerError(bottomWhiteView, bannerContainer, closeView, handler, canRequestNextAd, msg, adSiteBean)
                    } else {
                        handler?.post {
                            Runnable {
                                bannerError(bottomWhiteView, bannerContainer, closeView, handler, canRequestNextAd, msg, adSiteBean)
                            }
                        }
                    }
                }

                override fun onShow(adSiteBean: AdSiteBean) {
                    Logger.e(TAG, "banner展示了")
                    hasShowBanner = true
                    (activity as ReadActivity).bannerLoadSuccess()
                    handler?.post {
                        closeView.setVisible(AdConfigManger.getInstance().showAd(activity, Constants.channalCodes[4]) != null)
                        bannerContainer.setVisible(true)
                    }
//                  AdHttpUtil.viewPager(adSiteBean)
                }

                override fun onClick(adSiteBean: AdSiteBean) {
                    Logger.e(TAG, "banner被点击了，点击间隔：$adClickInterval 秒")
                    handler?.post {
                        bannerContainer.setDisabled(true)
                    }

                    handler?.postDelayed(bannerClickRunnable, adClickInterval)
                }

                override fun onError(adSiteBean: AdSiteBean, code: String, msg: String?) {
                    // 成功后可能也回调失败
                    if (hasShowBanner) {
                        return
                    }
                    if (adSiteBean != null && adSiteBean.origin == 1) {
                        bannerError(bottomWhiteView, bannerContainer, closeView, handler, canRequestNextAd, msg, adSiteBean)
                    } else {
                        handler?.post {
                            Runnable {
                                bannerError(bottomWhiteView, bannerContainer, closeView, handler, canRequestNextAd, msg, adSiteBean)
                            }
                        }
                    }
                }

                override fun onDismiss(adSiteBean: AdSiteBean) {}
            })
            bannerView?.showAd()
        } else {
            Logger.e(TAG, "没有可用广告源")
        }
    }

    private fun bannerError(bottomWhiteView: View, bannerContainer: AdFrameLayout, closeView: View, handler: Handler,
                            canRequestNextAd: Boolean, msg: String?, adSiteBean: AdSiteBean) {
        bannerView?.destroy()
        Logger.e(TAG, "banner请求出错，错误信息： $msg")
        var retyrAdBean =
            AdConfigManger.getInstance().showAd(activity, Constants.channalCodes[12])
        if (canRequestNextAd && adSiteBean != null && Constants.channalCodes[12] != adSiteBean.channelCode && retyrAdBean != null) {
            Logger.e(TAG, "banner请求出错，立即补充一个")
            showBottomBannerAd(bottomWhiteView, bannerContainer, closeView, handler, retyrAdBean.channelCode, retyrAdBean)
        }
    }

    fun loadChapterEndAd(handler: Handler, adContainer: AdFrameLayout, videoView: ViewGroup, tvTip: View?, adSiteBean: AdSiteBean) {
        if (activity !is ReadActivity ||  !(activity as ReadActivity).isCurrPageAd) {
            adContainer.setVisible(false)
        }
        tvTip?.setVisible(false)
        flowAdLoadSucc = false
        if (adSiteBean == null) {
            chapterEndLoadSucc = false
            return
        }
        chapterEndView?.destroy()
        chapterEndView = AdConfigManger.getInstance().getAdView(activity, Constants.channalCodes[11], adSiteBean)
        chapterEndView?.init(adContainer, null, 30, object : AdCallbackListener {
            override fun pull(adSiteBean: AdSiteBean?) {
            }

            override fun pullFailed(adSiteBean: AdSiteBean, code: String, errorMsg: String?) {
                chapterEndView?.destroy()
                chapterEndAdError(handler, adContainer, videoView, tvTip, adSiteBean)
            }

            override fun onShow(adSiteBean: AdSiteBean?) {
                handler?.post(Runnable {
                    hasChapterEndRetry = false
                    flowAdLoadSucc = false
                    chapterEndLoadSucc = true
//                infoFlowView?.destroy()
//                infoFlowView = null
                    adContainer.scaleX = 1f
                    adContainer.scaleY = 1f
                    adContainer.setDisabled(false)
                    adContainer.setPadding(adPadding, adPadding, adPadding, adPadding)
//                if (activity != null && activity is ReadActivity) {
//                    (activity as ReadActivity).setChapterEndLoaded(true)
//                }
                    Logger.e("show_flow", "章节末显示成功了")
                    // 检测是否停留在章节末页
                    if (activity is ReadActivity) {
                        tvTip?.setVisible(false)
                        adContainer.setVisible(true)
                        if ((activity as ReadActivity).isCurChapterEndAd) {
                            videoView.setVisible(false)
//                        adContainer.setVisible(true)
                        } else if ((activity as ReadActivity).isCurrPageAd) {
                            handler?.postDelayed(Runnable {
                                val adParams = adContainer.getLayoutParams() as LinearLayout.LayoutParams
                                adParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                                adContainer.setLayoutParams(adParams)
                                videoView.setVisible(
                                    AdConfigManger.getInstance().showAd(
                                        activity,
                                        Constants.channalCodes[3]
                                    ) != null
                                )
                                Logger.e(
                                    "show_flow", "章节末，videoView是否可以显示：${AdConfigManger.getInstance().showAd(
                                        activity,
                                        Constants.channalCodes[3]
                                    ) != null}"
                                )
                            }, 300)
                        }
                    }

                    // 避免广告重试导致广告白天和夜间模式失效
                    setNightMode(videoView, mIsNightMode)
                })
            }

            override fun onClick(adSiteBean: AdSiteBean?) {
                handler?.removeCallbacks(clickRunnable)
                handler?.post {
                    if (adContainer.tag != null) {
                        var pos = adContainer.tag as Int
                        Logger.e(TAG, "点击时获取到的位置是： $pos")
                        clickedPagePosList.put("$pos", System.currentTimeMillis())
                        adContainer.setDisabled(true)
                    }
                }

                handler?.postDelayed(clickRunnable, adClickInterval)
            }

            override fun onError(adSiteBean: AdSiteBean, code: String?, errorMsg: String?) {
                chapterEndView?.destroy()
                chapterEndAdError(handler, adContainer, videoView, tvTip, adSiteBean)
            }

            override fun onDismiss(adSiteBean: AdSiteBean?) {

            }

            override fun onAdTick(time: Long) {
            }

        })
        chapterEndView?.showAd()
    }

    private fun chapterEndAdError(handler: Handler, adContainer: AdFrameLayout, videoView: ViewGroup, tvTip: View?, adSiteBean: AdSiteBean) {
        chapterEndLoadSucc = false
        if (activity != null && activity is ReadActivity) {
            (activity as ReadActivity).setChapterEndLoaded(false)
        }
        handler?.post {
            if (!hasChapterEndRetry) {
                hasChapterEndRetry = true
//                var nextAdSiteBean = getNextAdSiteBean(Constants.channalCodes[11], adSiteBean.id)
                var nextAdSiteBean = AdConfigManger.getInstance().showAd(activity, Constants.channalCodes[11])
                if (nextAdSiteBean != null) {
                    Logger.e(TAG, "-------阅读器章节末广告重试------")
                    AdHttpUtil.retryInfo(adSiteBean)
                    loadChapterEndAd(handler, adContainer, videoView, tvTip, nextAdSiteBean)
                } else {
                    Logger.e(TAG, "-------阅读器章节末广告不重试，找不到有效的广告------")
                    hasChapterEndRetry = false
                    // 检测是否停留在插页广告
                    if (activity !is ReadActivity ||  !(activity as ReadActivity).isCurrPageAd) {
                        adContainer.setVisible(false)
                    }
                    flowAdLoadSucc = false
                    chapterEndView?.destroy()
                    chapterEndView = null
                }
            } else {
                Logger.e(TAG, "-----------阅读器章节末广告重试----还是没有广告")
                hasChapterEndRetry = false
                flowAdLoadSucc = false
                // 检测是否停留在插页广告
                if (activity !is ReadActivity ||  !(activity as ReadActivity).isCurrPageAd) {
                    adContainer.setVisible(false)
                }
                chapterEndView?.destroy()
                chapterEndView = null
            }
        }
    }

    fun destroy(bottomAdView : ViewGroup) {
        infoFlowView?.destroy()
        bannerView?.destroy()
        chapterEndView?.destroy()
        bottomAdView.removeAllViews()
        infoFlowView = null
        bannerView = null
        chapterEndView = null
    }

}