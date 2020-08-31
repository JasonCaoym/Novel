package com.duoyue.mianfei.xiaoshuo.mine.ui


import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import com.duoyue.app.bean.BookSiteBean
import com.duoyue.app.common.mgr.*
import com.duoyue.app.event.GetUpdateFinishEvent
import com.duoyue.app.event.TabSwitchEvent
import com.duoyue.app.event.TaskFinishEvent
import com.duoyue.app.presenter.BookShelfPresenter
import com.duoyue.app.service.CountDownService
import com.duoyue.app.ui.activity.AboutUsActivity
import com.duoyue.app.ui.activity.SettingActivity
import com.duoyue.app.ui.activity.TaskWebViewActivity
import com.duoyue.app.ui.view.BookDetailNestedScrollView
import com.duoyue.app.upgrade.UpgradeMsgUtils
import com.duoyue.lib.base.app.user.LoginPresenter
import com.duoyue.lib.base.app.user.UserManager
import com.duoyue.lib.base.customshare.CustomShareManger
import com.duoyue.lib.base.format.StringFormat
import com.duoyue.lib.base.log.Logger
import com.duoyue.lib.base.time.TimeTool
import com.duoyue.mianfei.xiaoshuo.R
import com.duoyue.mianfei.xiaoshuo.common.ActivityHelper
import com.duoyue.mianfei.xiaoshuo.data.bean.SignBean
import com.duoyue.mianfei.xiaoshuo.presenter.MinePresenter
import com.duoyue.mianfei.xiaoshuo.read.utils.TypefaceHelper
import com.duoyue.mianfei.xiaoshuo.read.utils.Utils
import com.duoyue.mianfei.xiaoshuo.ui.HomeActivity
import com.duoyue.mod.ad.AdConfigManger
import com.duoyue.mod.ad.bean.AdSiteBean
import com.duoyue.mod.ad.dao.AdReadConfigHelp
import com.duoyue.mod.ad.net.AdHttpUtil
import com.duoyue.mod.ad.utils.AdConstants
import com.duoyue.mod.stats.FuncPageStatsApi
import com.duoyue.mod.stats.FunctionStatsApi
import com.duoyue.mod.stats.common.PageNameConstants
import com.share.platform.ShareUtil
import com.zydm.base.common.BaseApplication
import com.zydm.base.data.bean.BookRecordGatherResp
import com.zydm.base.ext.loadAvaterUrl
import com.zydm.base.ext.onClick
import com.zydm.base.ext.setVisible
import com.zydm.base.rx.MtSchedulers
import com.zydm.base.statistics.umeng.StatisHelper
import com.zydm.base.tools.TooFastChecker
import com.zydm.base.ui.fragment.BaseFragment
import com.zydm.base.utils.GlideUtils
import com.zydm.base.utils.SharePreferenceUtils
import com.zydm.base.utils.ToastUtils
import com.zydm.base.utils.ViewUtils
import com.zydm.base.widgets.BottomShareDialog
import com.zzdm.ad.router.BaseData
import io.reactivex.Single
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.mine_item.view.*
import kotlinx.android.synthetic.main.my_read_time_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MineFragment : BaseFragment(), MineView {


    var fastChecker = TooFastChecker(800)

    var minePresenter: Presenter? = null
    var signStatus: Int = 1
    var handler: Handler = Handler()

    var bookCityAdBean: BookSiteBean? = null

    private var animator: ObjectAnimator? = null
    private var objectAnimator: ObjectAnimator? = null

    private var sexNum: Int = 1
    private var flowAdSiteBean: AdSiteBean? = null
    companion object {
        var goTaskCenter = false
    }

    override fun onCreateView(savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_mine)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            var userRootParamst = user_info_layout.layoutParams as RelativeLayout.LayoutParams
            userRootParamst.addRule(RelativeLayout.LEFT_OF, R.id.rl_sign)
            user_info_layout.layoutParams = userRootParamst
        } catch (ex : java.lang.Exception) {

        }

        //登录
        user_head_imgview.onClick(this);
        login_entrance_layout.onClick(this);
        tv_sign_in.onClick(this)
        rl_free_ad.onClick(this)
        ll_my_book_bean.onClick(this)
        recommend_float_button.onClick(this)

        //每日福利任务
        daily_welfare.module_icon.setBackgroundResource(R.mipmap.present_icon)
        daily_welfare.module_name.text = ViewUtils.getString(R.string.daily_welfare)
        daily_welfare.number.text = ViewUtils.getString(R.string.make_book_bean)
        daily_welfare.number.setVisible(true)
        setTaskRedPoint()
        daily_welfare.onClick(this)

        //阅读口味
        read_taste.module_icon.setBackgroundResource(R.mipmap.read_taste_icon)
        read_taste.module_name.text = ViewUtils.getString(R.string.read_taste)
        read_taste.onClick(this)
        read_taste.number.setVisible(true)
        val sex = StartGuideMgr.getChooseSex()
        if (sex == StartGuideMgr.SEX_WOMAN) {
            read_taste.number.text = ViewUtils.getString(R.string.female)
        } else {
            read_taste.number.text = ViewUtils.getString(R.string.male)
        }
        StartGuideMgr.setSexChangeListener { sex ->
            if (sex) {
                sexNum = 1
                read_taste.number.text = ViewUtils.getString(R.string.male)
                minePresenter!!.loadSiteData(1)
            } else {
                sexNum = 2
                read_taste.number.text = ViewUtils.getString(R.string.female)
                minePresenter!!.loadSiteData(2)
            }
        }
        StartGuideMgr.setOnBackListener {
            if (recommend_float_button != null && objectAnimator != null && recommend_float_button.visibility == View.VISIBLE) {
                objectAnimator!!.start()
            }
        }
        //阅读记录
        read_history.module_icon.setBackgroundResource(R.mipmap.read_history_icon)
        read_history.module_name.text = ViewUtils.getString(R.string.read_record)
        read_history.onClick(this)
        //推荐给好友
        recommend_friends.module_icon.setBackgroundResource(R.mipmap.recommend_friends_icon)
        recommend_friends.module_name.text = ViewUtils.getString(R.string.recommend_friends)
        recommend_friends.onClick(this)
        //问题反馈
        problem_feedback.module_icon.setBackgroundResource(R.mipmap.problem_feedback_icon)
        problem_feedback.module_name.text = ViewUtils.getString(R.string.problem_feedback)
        problem_feedback.onClick(this)
        //加入QQ群
        join_qq_group.module_icon.setBackgroundResource(R.mipmap.join_qq_group_icon)
        join_qq_group.module_name.text = ViewUtils.getString(R.string.join_qq_group)
        join_qq_group.number.setVisible(true)
        join_qq_group.number.text = ViewUtils.getString(R.string.join_qq_group_notice)
        val drawable = ViewUtils.getDrawable(R.mipmap.present_icon)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        join_qq_group.number.setCompoundDrawables(drawable, null, null, null)
        join_qq_group.number.compoundDrawablePadding = Utils.dp2px(context,10f)
        join_qq_group.onClick(this)
        // 设置
        mine_setting.module_icon.setBackgroundResource(R.mipmap.icon_setting)
        mine_setting.module_name.text = ViewUtils.getString(R.string.setting)
        if (UpgradeMsgUtils.isHasUpdateInfo(context)) {
            mine_setting.iv_has_point.setVisible(true)
        }
        mine_setting.onClick(this)
        //关于我们
        about_us.module_icon.setBackgroundResource(R.mipmap.about_us_icon)
        about_us.module_name.text = ViewUtils.getString(R.string.about_us)
        about_us.onClick(this)
        //开发者
        developer.setVisible(BaseApplication.context.isTestEnv())
        developer.module_name.text = ViewUtils.getString(R.string.developer)
        developer.onClick(this)
        //AppBarLayout

        mine_appbar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            try {
                //verticalOffset  当前偏移量 appBarLayout.getTotalScrollRange() 最大高度
                var offset = Math.abs(verticalOffset) //目的是将负数转换为绝对正数；
                if (offset < mine_appbar_layout.totalScrollRange / 2) {
                    mine_title_layout.visibility = View.GONE
                } else {
                    mine_title_layout.visibility = View.VISIBLE
                    var floate =
                        (offset - mine_appbar_layout.totalScrollRange / 2) * 1.0f / (mine_appbar_layout.totalScrollRange / 2)
                    mine_title_layout.alpha = floate
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        //初始化当前用户信息.
        updateUserInfo()
        initView()
        initCountDown()

        initListener()
        /**
         * 悬浮广告不需要每次切换请求接口
         * */
        if (minePresenter != null) {
            minePresenter!!.loadSiteData(if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_WOMAN) 2 else 1)
            sexNum = StartGuideMgr.getChooseSex();
        }
        if (goTaskCenter) {
            performClickCenterClick()
        }
    }

    /**
     * 设置每日任务红点
     */
    private fun setTaskRedPoint() {
        val lastTime = SharePreferenceUtils.getLong(context, SharePreferenceUtils.FIRST_CLICK_EVERY_DAY_TASK, 0)
        if (lastTime == 0L || lastTime < TimeTool.getCurrDayBeginTime()) {//今天还未点击
            daily_welfare.iv_has_point.setVisible(true)
        } else {
            daily_welfare.iv_has_point.setVisible(false)
        }
    }

    /**
     * webView 的接口监听
     */
    private fun initListener() {
        TaskWebViewActivity.setJsCallBackListener(object : TaskWebViewActivity.JsCallBackListener {

            override fun bookbeansChanged() {
                //书豆变化
                getReadHistory()
            }

            override fun toBookCity(type: Int) {
                //去书城/书架
                EventBus.getDefault().post(TabSwitchEvent(type, 8))
            }

            override fun signSuccess() {
                initData()
            }
        })
        mine_scrollview.setOnScrollListener(object : BookDetailNestedScrollView.OnScrollListener {
            override fun onStartScroll() {
                //防止重复跳动
                if (objectAnimator != null) {
                    objectAnimator!!.cancel()
                }
                animator = ObjectAnimator.ofFloat(
                    recommend_float_button,
                    "translationX",
                    recommend_float_button.translationX,
                    400f
                )
                animator!!.duration = 200
                animator!!.start()
            }

            override fun onScroll(scrollY: Int) {

            }

            override fun onStopScroll() {
                if (animator != null) {
                    animator!!.cancel()
                }
                objectAnimator = ObjectAnimator.ofFloat(
                    recommend_float_button,
                    "translationX",
                    recommend_float_button.translationX,
                    0f
                )
                objectAnimator!!.duration = 600
                objectAnimator!!.start()

            }
        })

    }

    /**
     * 获取签到信息
     */
    public fun initData() {
        if (minePresenter == null) {
            minePresenter = MinePresenter(this)
        }
        minePresenter!!.loadData()
    }

    /**
     * 设置倒计时是否展示
     */
    private fun setCoundDownVisble(isVisible: Boolean) {
        cdv_count_down?.setVisible(isVisible)
        tv_residue?.setVisible(isVisible)
        tv_to_open?.setVisible(!isVisible)
        CountDownService.setTotalTime(0)
    }

    /**
     * 设置特殊字体
     */
    private fun initView() {
        val fromAsset = TypefaceHelper.get(context, "fonts/DINCond-Bold.ttf")
        tv_read_time.setTypeface(fromAsset)
        tv_book_shelf_num.setTypeface(fromAsset)
        tv_my_book_bean.setTypeface(fromAsset)
    }

    override fun onFragmentResume(isFirst: Boolean, isViewDestroyed: Boolean) {
        super.onFragmentResume(isFirst, isViewDestroyed)

        if (!isFirst && recommend_float_button.visibility == View.VISIBLE) {
            if (animator != null) {
                animator!!.cancel()
            }
            objectAnimator = ObjectAnimator.ofFloat(
                recommend_float_button,
                "translationX",
                recommend_float_button.translationX,
                0f
            )
            objectAnimator!!.duration = 600
            objectAnimator!!.start()
        }
    }

    fun performClickCenterClick() {
        handler.postDelayed(Runnable {
            goTaskCenter()
        }, 300)
    }

    /**
     * 初始化倒计时
     */
    private fun initCountDown() {
        cdv_count_down.setTimeTvSize(Utils.sp2px(context, 5f) * 1.0f)
            .setColonMargin(Utils.dp2px(context, 3.5f), Utils.dp2px(context,3.5f))
            .setTimeTvBackgroundRes(R.drawable.bg_fff7ea)
            .setHourTvPadding(Utils.dp2px(context, 3f), Utils.dp2px(context, 1f), Utils.dp2px(context, 3f), Utils.dp2px(context, 1f))
            .setMinuteTvPadding(Utils.dp2px(context, 3f), Utils.dp2px(context, 1f), Utils.dp2px(context, 3f), Utils.dp2px(context, 1f))
            .setSecondTvPadding(Utils.dp2px(context, 3f), Utils.dp2px(context, 1f), Utils.dp2px(context, 3f), Utils.dp2px(context, 1f))
            .setMinWidth(18f)
        cdv_count_down.setCountDownEndListener { setCoundDownVisble(false) }
    }

    /**
     * 根据百分比改变颜色透明度
     */
//    fun changeAlpha(color: Int, fraction: Float): Int {
//        var alpha: Int = (Color.alpha(color) * fraction).toInt()
//        return Color.argb(alpha, 255, 255, 255)
//    }


    override fun onClick(v: View) {
        if (fastChecker.isTooFast()) {
            return
        }
        super.onClick(v)
        when (v.id) {
            R.id.user_head_imgview -> {
                //游客状态下才能调用手机登录界面.
                if (login_entrance_layout.visibility == View.VISIBLE) {
                    //登录, 显示手机登录框.
                    UserLoginMgr.showLoginPhonePage(activity);
                    //点击登录入口.
                    FunctionStatsApi.mLoginClick();
                    FuncPageStatsApi.mineLoginClick();
                }
            }
            R.id.login_entrance_layout -> {
                //登录, 显示手机登录框.
                UserLoginMgr.showLoginPhonePage(activity);
                //点击登录入口.
                FunctionStatsApi.mLoginClick();
                FuncPageStatsApi.mineLoginClick();
            }
            R.id.read_taste -> {
                //阅读口味.
                StartGuideMgr.showGuidePage(activity, true)
                //点击阅读口味入口.
                FunctionStatsApi.mReadTasteClick();
                FuncPageStatsApi.mineTasteClick();
            }
            R.id.read_history -> {
                //阅读记录.
                ActivityHelper.gotoHistory(activity!!)
                //点击阅读记录入口.
                FunctionStatsApi.mReadHistoryClick();
                FuncPageStatsApi.mineHistoryClick();
            }
            R.id.recommend_friends -> {
                //推荐给好友.
                var content = "来自好友的推荐"
                //var userInfo = UserManager.getInstance().userInfo
                //if (userInfo != null && !TextUtils.isEmpty(userInfo.nickName)) {
                //content = "来自好友\"${userInfo.nickName}\"的推荐"
                //}
                var dialog = CustomShareManger.getInstance().shareWithText(
                    activity, "书荒终结神器，海量正版网络人气小说，你想要的这儿都有。",
                    content, R.mipmap.share_big_img, R.mipmap.share_logo,
                    "http://app.duoyueapp.com/", object : BottomShareDialog.ShareClickListener {
                        override fun onClick(type: Int) {
                            FuncPageStatsApi.shareClick(0, PageNameConstants.MINE, type, "")
                        }
                    }, BottomShareDialog.ShareResultListener { shareResult ->
                        if (shareResult != 1) {
                            return@ShareResultListener
                        }
                        TaskMgr.show(
                            context,
                            fragmentManager,
                            resources.getString(R.string.finish_share_task),
                            TaskMgr.SHARE_TASK
                        )
                    }

                )

                dialog.setOnDismissListener {
                    if (objectAnimator != null && recommend_float_button.visibility == View.VISIBLE) {
                        objectAnimator!!.start()
                    }
                }
            }
            R.id.problem_feedback -> {
                //问题反馈.
                FuncPageStatsApi.userFeedBackClick()
                StatisHelper.onEvent().helpClick()
                ActivityHelper.gotoQuestion(activity!!)
            }
            R.id.join_qq_group -> {
                //判断QQ是否已安装.
                if (!ShareUtil.isQQInstalled(context)) {   //QQ未安装.
                    ToastUtils.show(R.string.no_install_qq)
                    if (recommend_float_button.visibility == View.VISIBLE) {
                        objectAnimator!!.start()
                    }
                    return
                }
                //加入QQ群.
                val isSucc: Boolean = QQMgr.joinQQGroup(activity)
                if (!isSucc) {
                    //加入失败.
                    ToastUtils.show(R.string.join_qq_group_fail)
                }
            }
            R.id.about_us -> {
                //关于我们.
                val intent = Intent(activity!!, AboutUsActivity::class.java)
                activity!!.startActivity(intent)
            }
            R.id.developer -> {
                //开发者.
                val intent = Intent(activity!!, DeveloperActivity::class.java)
                activity!!.startActivity(intent)
            }

            R.id.mine_setting -> {
                //设置
                FuncPageStatsApi.settingClick()
                val intent = Intent(activity!!, SettingActivity::class.java)
                activity!!.startActivity(intent)
            }
            R.id.tv_sign_in -> {
                //签到
                FuncPageStatsApi.signInClick(PageNameConstants.MINE, if (signStatus == 2) signStatus else 1)
                val intent = Intent(activity!!, TaskWebViewActivity::class.java)
                var url = ""
                if (TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER))) {
                    url = "http://taskcenter.duoyueapp.com/"
                } else {
                    url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER)
                }
                intent.putExtra(
                    "url",
                    url
                )
                activity!!.startActivity(intent)
            }
            R.id.daily_welfare -> {
                //每日福利任务
                goTaskCenter()
            }
            R.id.rl_free_ad -> {
                //免广告特权
                FuncPageStatsApi.beanFreeAdClick(2)
                val intent = Intent(activity!!, TaskWebViewActivity::class.java)
                var url = ""
                if (TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOK_BEANS))) {
                    url = "http://taskcenter.duoyueapp.com/bookBean/"
                } else {
                    url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOK_BEANS)
                }
                intent.putExtra(
                    "url",
                    url
                )
                activity!!.startActivity(intent)
            }
            R.id.ll_my_book_bean -> {
                //书豆
                FuncPageStatsApi.beanFreeAdClick(1)
                val intent = Intent(activity!!, TaskWebViewActivity::class.java)
                var url = ""
                if (TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOK_BEANS))) {
                    url = "http://taskcenter.duoyueapp.com/bookBean/"
                } else {
                    url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_BOOK_BEANS)
                }
                intent.putExtra(
                    "url",
                    url
                )
                activity!!.startActivity(intent)
//                val intent = Intent(activity, OppoPushTranslateActivity::class.java)
//
//                intent.setData(Uri.parse("oppopush://com.duoyue.mianfei.xiaoshuo/notification?action={type=1,path=1}"))
//
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//                val intentUrl = intent.toUri(Intent.URI_INTENT_SCHEME)
//                Logger.d("MineFragment--" ,intentUrl)
            }

            R.id.recommend_float_button -> {
                if (bookCityAdBean!!.suspensionSite.type == 1) { // 详情
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.gotoBookDetails(
                        activity!!,
                        "" + bookCityAdBean!!.suspensionSite.bookId,
                        BaseData(""),
                        PageNameConstants.BOOK_CITY,
                        17,
                        PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.MINE + " + " + StartGuideMgr.getChooseSex()
                    )
                } else if (bookCityAdBean!!.getSuspensionSite().getType() == 3 && flowAdSiteBean != null) {
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.gotoWeb(
                        activity!!,
                        flowAdSiteBean!!.getLinkUrl()
                    )
                    AdHttpUtil.click(flowAdSiteBean)
                } else { // H5
                    com.duoyue.mianfei.xiaoshuo.book.common.ActivityHelper.gotoWeb(
                        activity!!,
                        bookCityAdBean!!.suspensionSite.link
                    )
                }
                //书城悬浮按钮.
                FuncPageStatsApi.floatAdClick(
                    if (bookCityAdBean!!.getSuspensionSite().type == 1) bookCityAdBean!!.getSuspensionSite().bookId else -1,
                    0,
                    PageNameConstants.MINE,
                    PageNameConstants.FLOATE_RECOMMEND + " + " + PageNameConstants.MINE + " + " + sexNum
                )
            }
        }
    }

    private fun goTaskCenter() {
        FuncPageStatsApi.taskEveryDayClick()
        val intent = Intent(activity!!, TaskWebViewActivity::class.java)
        var url = ""
        if (TextUtils.isEmpty(AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER))) {
            url = "http://taskcenter.duoyueapp.com/"
        } else {
            url = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.H5_TASKCENTER)
        }
        intent.putExtra(
            "url",
            url
        )
        activity!!.startActivity(intent)

        //设置红点隐藏
        daily_welfare.iv_has_point.setVisible(false)
        SharePreferenceUtils.putLong(
            context, SharePreferenceUtils.FIRST_CLICK_EVERY_DAY_TASK,
            System.currentTimeMillis()
        )
        if (!UpgradeMsgUtils.isHasUpdateInfo(context)) {
            (activity as HomeActivity).setRedPointVisible(false)
        }
    }

    /**
     * 更新用户信息.
     */
    private fun updateUserInfo() {
        try {
            //获取用户登录信息.
            var userInfo = UserManager.getInstance().userInfo;
            Logger.i(TAG, "updateUserInfo: {}", userInfo)
            //判断是否为游客.
            if (userInfo == null || userInfo.type == LoginPresenter.USER_TYPE_TOURIST) {
                //设置默认用户头像.
                user_head_imgview.setImageDrawable(null);
                user_head_imgview.setBackgroundResource(R.mipmap.mine_head_icon);
                //显示登录入口
                login_entrance_layout.visibility = View.VISIBLE;
                //隐藏用户信息.
                user_info_layout.visibility = View.GONE;
            } else {
                //隐藏登录入口
                login_entrance_layout.visibility = View.GONE;
                //显示用户信息.
                user_info_layout.visibility = View.VISIBLE;
                //设置用户头像.
                if (!StringFormat.isEmpty(userInfo.headImg)) {
                    user_head_imgview.loadAvaterUrl(userInfo.headImg)
                }
                //设置账号名称.
                user_name_textview.setText(userInfo.nickName)
                tv_book_shelf_num.setText("0")
                tv_read_time.setText("0")

            }
            initData()
            //获取阅读历史记录.
            getReadHistory()

            //设置阅读口味男女生
            read_taste.number.text =
                if (userInfo.sex == 1) ViewUtils.getString(R.string.male) else ViewUtils.getString(R.string.female)
        } catch (throwable: Throwable) {
            Logger.e(TAG, "updateUserInfo: {}", throwable)
        }
    }

    /**
     * 获取阅读历史记录.
     */
    public fun getReadHistory() {
        Single.fromCallable {
            ReadHistoryMgr.getBookRecordGather()
        }.subscribeOn(MtSchedulers.io()).observeOn(MtSchedulers.mainUi()).subscribe(Consumer<BookRecordGatherResp> {
            if (it != null) {
                //修改总阅读时长.
                BookShelfPresenter.updateTotalReadTime(false, it.totalReadTime);
                //更新页面书籍相关信息.
                updateBookInfo(it.storedBookSize, true);
                //更新书豆和免广告信息
                updateBeanAdInfo(it.bookBeans, it.lastSec)
                CountDownService.setTotalTime(it.lastSec)
                Logger.e("ad#AdConfig", TAG + "免广告剩余时间： ${it.lastSec}")
                SharePreferenceUtils.putObject(context, SharePreferenceUtils.READ_HISTORY_CACHE, it)
            } else {
                //更新页面书籍相关信息.
                updateBookInfo(0, false);
                updateBeanAdInfo(0, 0)
            }
        })
    }

    /**
     * 更新书豆和免广告信息
     * @param bookBeans 书豆
     * @param lastTmsp 兑换卡剩余时长
     */
    private fun updateBeanAdInfo(bookBeans: Int, lastTmsp: Long) {
        tv_my_book_bean.text = "" + bookBeans

        if (lastTmsp == null || lastTmsp <= 0) {//没有免广告特权
            setCoundDownVisble(false)
        } else {
            setCoundDownVisble(true)
            cdv_count_down?.stopCountDown()
            cdv_count_down?.setCountTime(lastTmsp)?.startCountDown()
        }
    }

    /**
     * 更新书籍信息.
     * @param netStoredBookSize 服务器下发的收藏书籍信息
     * @param isServiceBack  是否服务器返回的数据
     */
    private fun updateBookInfo(netStoredBookSize: Int, isServiceBack: Boolean) {
        //收藏.
        if (tv_book_shelf_num != null) {
            //查询书籍书籍数量.
            var bookShelfBookIdList = BookShelfPresenter.getBookShelfBookIdList();
            if (!StringFormat.isEmpty(bookShelfBookIdList) && bookShelfBookIdList.size > netStoredBookSize) {
                tv_book_shelf_num.text = "" + bookShelfBookIdList.size
            } else if (isServiceBack) {
                tv_book_shelf_num.text = "" + netStoredBookSize
            } else {
                val bookRecordGatherResp = SharePreferenceUtils.getObject<BookRecordGatherResp>(
                    context,
                    SharePreferenceUtils.READ_HISTORY_CACHE
                )
                if (bookRecordGatherResp != null && bookRecordGatherResp.storedBookSize > 0) {
                    tv_book_shelf_num.text = "" + bookRecordGatherResp.storedBookSize
                } else {
                    tv_book_shelf_num.text = "" + netStoredBookSize
                }
            }
        }
        //阅读时长.
        if (tv_read_time != null) {
            tv_read_time.setText("" + BookShelfPresenter.getTotalReadTime())
        }
    }

    override fun getPageName(): String {
        return ViewUtils.getString(R.string.tab_mine)
    }

    /**
     * 登录成功.
     */
    fun onLoginSucc() {
        //更新用户信息.
        updateUserInfo()

        var userInfo = UserManager.getInstance().userInfo
        if (userInfo.type == 1) return
//        EventBus.getDefault().post(LoginSuccessEvent(userInfo))
        if (userInfo.bookBeans <= 0) return
        if (Utils.isActivityTop(context, HomeActivity.javaClass)) {
            handler.postDelayed(Runnable {
                TaskMgr.showDialog(
                    userInfo.bookBeans,
                    getString(R.string.finish_Login_task),
                    fragmentManager,
                    TaskMgr.LOGIN_TASK
                )
            }, 1000)
        }

    }

    override fun onResume() {
        super.onResume()
        //刷新阅读总时长和收集书籍.
        updateBookInfo(0, false)
    }

    fun resumeByHomePressed() {
        FuncPageStatsApi.mineShow(2)
        Logger.e("app#", "我的--从桌面启动")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        StartGuideMgr.removeListener()
        //释放
        StartGuideMgr.sexChangeListener = null
        //销毁倒计时
        cdv_count_down?.destoryCountDownView()

        TaskWebViewActivity.mJsCallBackListener = null

        handler.removeCallbacksAndMessages(null)

        if (minePresenter != null) {
            minePresenter!!.destroy()
        }
    }

    override fun showSuccess(signBean: SignBean?) {
        signStatus = signBean!!.signStatus
        if (signBean!!.signStatus == 2) {//当天已签到
            tv_sign_in.text = "已签到"
            tv_sign_in.setTextColor(resources.getColor(R.color.color_FE8B13))
            val left = resources.getDrawable(R.mipmap.sign_icon)
            tv_sign_in.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
            rl_sign.setBackgroundResource(R.drawable.bg_fe8b13_14)
        } else {
            tv_sign_in.text = "签到"
            tv_sign_in.setTextColor(resources.getColor(R.color.white))
            val left = resources.getDrawable(R.mipmap.unsign_icon)
            tv_sign_in.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
            rl_sign.setBackgroundResource(R.drawable.btn_sign_in_14)
        }
        reading_time_textview.text = signBean!!.show
    }

    override fun showEmpty() {
        tv_sign_in.text = "签到"
        tv_sign_in.setTextColor(resources.getColor(R.color.white))
        val left = resources.getDrawable(R.mipmap.unsign_icon)
        tv_sign_in.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
        rl_sign.setBackgroundResource(R.drawable.btn_sign_in_14)
        reading_time_textview.text = "今天还没签到哦"
    }

    override fun showError() {

    }

    override fun showSite(bookSiteBean: BookSiteBean?) {
        if (bookSiteBean!!.suspensionSite.type != 3 && bookSiteBean!!.suspensionSite.iconPath == null) {
            recommend_float_button.setVisible(false)
        } else {
            bookCityAdBean = bookSiteBean

            if (!TextUtils.isEmpty(bookSiteBean.suspensionSite.adChannalCode)) run {
                flowAdSiteBean = AdConfigManger.getInstance().showAd(
                    activity,
                    bookSiteBean.suspensionSite.adChannalCode
                )
                if (flowAdSiteBean != null && !TextUtils.isEmpty(flowAdSiteBean!!.picUrl)) {
                    val adView = AdConfigManger.getInstance().getAdView(
                        activity,
                        flowAdSiteBean!!.getChannelCode(), flowAdSiteBean
                    )
                    if (adView != null) {
                        recommend_float_button.setVisibility(View.VISIBLE)
                        adView.init(null, recommend_float_button, 30, null)
                        adView.showAd()
                    } else {
                        recommend_float_button.setVisibility(View.GONE)
                    }
                } else {
                    recommend_float_button.setVisibility(View.GONE)
                }
            } else {
                flowAdSiteBean = null
                recommend_float_button.setVisible(true)
                GlideUtils.loadImageWidthNoCorner(
                    activity!!,
                    bookSiteBean.suspensionSite.iconPath,
                    recommend_float_button
                )

                objectAnimator = ObjectAnimator.ofFloat(
                    recommend_float_button,
                    "translationX",
                    recommend_float_button.translationX,
                    0f
                )
                objectAnimator!!.duration = 600
                objectAnimator!!.start()
            }
            FuncPageStatsApi.floatAdExpose(
                if (bookCityAdBean!!.getSuspensionSite().type == 1) bookCityAdBean!!.getSuspensionSite().bookId else -1,
                0,
                PageNameConstants.MINE,
                "7 + " + PageNameConstants.MINE + " + " + StartGuideMgr.getChooseSex()
            )
        }

    }

    interface Presenter {
        fun loadData()

        fun loadSiteData(chan: Int)

        fun destroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHandleEvent(event: TaskFinishEvent) {
        getReadHistory()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateHandleSwitchEvent(event: GetUpdateFinishEvent) {
        if (UpgradeMsgUtils.isHasUpdateInfo(context)) {
            mine_setting.iv_has_point.setVisible(true)
        } else {
            mine_setting.iv_has_point.setVisible(false)
        }
    }
}
