package com.zydm.base.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.duoyue.lib.base.format.StringFormat
import com.duoyue.lib.base.log.Logger
import com.zydm.base.common.BaseApplication
import com.zydm.base.common.Constants
import com.zydm.base.statistics.umeng.StatisHelper
import com.zydm.base.ui.activity.AppConfig
import com.zydm.base.ui.activity.BaseActivity
import com.zydm.base.utils.EventUtils
import com.zydm.base.utils.LogUtils
import com.zydm.base.utils.StringUtils

abstract class BaseFragment : Fragment(), OnClickListener {


    val TAG = "Base:BaseFragment"

    private var mIsVisibleToUser = false

    private var mRecycleViewable = false
    var mRootView: View? = null

    private var mInflater: LayoutInflater? = null

    private var mIsOnCreateView = false
    private var mWaitSetVisibleToUser: Boolean? = null
    private var mIsSetUserVisibleHint = false
    private var mIsByStop = false
    private var mIsPostSetVisibleTask = false
    private val mSetVisibleTask = Runnable {
        mIsPostSetVisibleTask = false
        setVisibleToUser(true)
    }

    private val fragmentConfig = FragmentConfig()

    //==============友盟统计专用(Begin)==============
    private var isLastVisible = false;
    private var hidden = false;
    private var isFirst = true;
    private var isResuming = false;
    private var isViewDestroyed = false;
    //==============友盟统计专用(End)==============
    val baseActivity: BaseActivity
        get() = activity as BaseActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragmentConfig(fragmentConfig)
        if (fragmentConfig.isApplyEventBus) {
            EventUtils.register(this)
        }
    }

    open protected fun initFragmentConfig(fragmentConfig: FragmentConfig) {

    }

    final override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.mInflater = inflater
        if (null == mRootView) {
            onCreateView(savedInstanceState)
            mIsOnCreateView = true
            if (mWaitSetVisibleToUser != null) {
                var isVisibleToUser = mWaitSetVisibleToUser!!
                BaseApplication.handler.postDelayed(
                        { setVisibleToUser(isVisibleToUser) },
                        Constants.MILLIS_100.toLong()
                )
            }
        }
        if (null != mRootView && mRootView!!.parent != null) {
            val parent = mRootView?.parent as ViewGroup
            parent.removeView(mRootView)
        }
        return mRootView
    }

    abstract fun onCreateView(savedInstanceState: Bundle?)

    /**
     * 只能在onCreateView(Bundle savedInstanceState) 方法中调用
     *
     * @param layoutResID
     */
    protected fun setContentView(layoutResID: Int): View {
        val rootView = mInflater!!.inflate(layoutResID, null)
        setContentView(rootView)
        return rootView
    }

    /**
     * 只能在onCreateView(Bundle savedInstanceState) 方法中调用
     *
     * @param view
     */
    protected fun setContentView(view: View) {
        if (null != mRootView) {
            return
        }
        mRootView = view
    }

    override fun onResume() {
        super.onResume()
        // LogUtils.d(TAG, "onResume() ");
        if (!mIsSetUserVisibleHint) {
            setVisibleToUser(true)
        } else if (mIsByStop) {
            BaseApplication.handler.postDelayed(mSetVisibleTask, Constants.MILLIS_100.toLong())
            mIsPostSetVisibleTask = true
        }
        mIsByStop = false
        //==========友盟统计专用(Begin)============
        isResuming = true;
        tryToChangeVisibility(true);
        //==========友盟统计专用(End)============
    }

    open fun getPageName(): String {
        return ""
    }

    override fun onPause() {
        super.onPause()
        if (mIsVisibleToUser) {
            mIsByStop = true
        }
        setVisibleToUser(false)
        //==========友盟统计专用(Begin)============
        isResuming = false;
        tryToChangeVisibility(false);
        //==========友盟统计专用(End)============
    }

    fun setRecycleViewable(recycleViewable: Boolean) {
        this.mRecycleViewable = recycleViewable
    }

    fun recycleViewable(): Boolean {
        return mRecycleViewable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.d(TAG, "onDestroyView() this:" + this + "   mRecycleViewable:" + mRecycleViewable)
        if (mRecycleViewable) {
            mIsOnCreateView = false
        }
        onDestroyView(mRecycleViewable)

        if (fragmentConfig.isApplyEventBus) {
            EventUtils.unregister(this)
        }
        //==========友盟统计专用(Begin)============
        isViewDestroyed = true;
        //==========友盟统计专用(End)============
    }

    protected fun onDestroyView(recycleViewable: Boolean) {
        if (recycleViewable) {
            mRootView = null
            mInflater = null
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        removeSetVisibleTask()
        mIsSetUserVisibleHint = true
        setVisibleToUser(isVisibleToUser)
        //==========友盟统计专用(Begin)============
        setUserVisibleHintClient(isVisibleToUser)
        //==========友盟统计专用(End)============
    }

    private fun setVisibleToUser(isVisibleToUser: Boolean) {
        LogUtils.d(TAG, "##setVisibleToUser() isVisibleToUser = "
                + isVisibleToUser);
        if (!mIsOnCreateView) {
            mWaitSetVisibleToUser = isVisibleToUser
            return
        }
        if (isVisibleToUser == mIsVisibleToUser) {
            return
        }
        mIsVisibleToUser = isVisibleToUser
        onVisibleToUserChanged(mIsVisibleToUser)
    }

    fun isVisibleToUser(): Boolean {
        return mIsVisibleToUser
    }

    private fun removeSetVisibleTask() {
        if (mIsPostSetVisibleTask) {
            BaseApplication.handler.removeCallbacks(mSetVisibleTask)
            mIsPostSetVisibleTask = false
        }
    }

    open protected fun onVisibleToUserChanged(isVisibleToUser: Boolean) {
        LogUtils.d(
                TAG, "##onVisibleToUserChanged() isVisibleToUser = "
                + isVisibleToUser + "  " + this
        )
    }

    fun <V : View> findView(id: Int): V? {
        return if (null == mRootView) {
            null
        } else mRootView!!.findViewById<View>(id) as V?
    }

    fun <V : View> findViewSetOnClick(id: Int): V? {
        val view = findView<V>(id)
        view?.setOnClickListener(this)
        return view
    }

    fun <V : View> findView(itemView: View?, id: Int): V? {
        return if (null == itemView) {
            null
        } else itemView.findViewById<View>(id) as V?
    }

    fun <V : View> findViewSetOnClick(itemView: View, id: Int): V? {
        val view = findView<V>(itemView, id)
        view?.setOnClickListener(this)
        return view

    }

    override fun onClick(v: View) {

    }

    protected class FragmentConfig : AppConfig()

    fun finishFragment() {
        //        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onHiddenChanged(hidden: Boolean)
    {
        super.onHiddenChanged(hidden)
        onHiddenChangedClient(hidden)
    }

    //=========================友盟统计Fragment页面跳转专用(Begin)=================================
    private fun setUserVisibleHintClient(isVisibleToUser: Boolean)
    {
        try
        {
            tryToChangeVisibility(isVisibleToUser);
            if (isAdded) {
                // 当Fragment不可见时, 其子Fragment也是不可见的.因此要通知子Fragment当前可见状态改变了.
                var fragments = getChildFragment()
                if (fragments != null)
                {
                    for (fragment in fragments)
                    {
                        if (fragment is BaseFragment)
                        {
                            fragment.run { setUserVisibleHintClient(isVisibleToUser) }
                        }
                    }
                }
            }
        } catch (throwable: Throwable)
        {
            Logger.e(TAG, "setUserVisibleHintClient: {}, {}", isVisibleToUser, throwable)
        }
    }

    private fun onHiddenChangedClient(hidden: Boolean)
    {
        try
        {
            this.hidden = hidden
            tryToChangeVisibility(!hidden)
            if (isAdded)
            {
                //隐藏子Fragment.
                var fragments = getChildFragment()
                if (fragments != null)
                {
                    for (fragment in fragments)
                    {
                        if (fragment is BaseFragment)
                        {
                            fragment.run { onHiddenChangedClient(hidden) }
                        }
                    }
                }
            }
        } catch (throwable: Throwable)
        {
            Logger.e(TAG, "onHiddenChangedClient: {}, {}", hidden, throwable)
        }
    }

    /**
     * 获取子Fragment列表
     */
    open fun getChildFragment(): List<Fragment>?
    {
        return null
    }

    private fun tryToChangeVisibility(tryToShow: Boolean)
    {
        try
        {
            // 上次可见
            if (isLastVisible)
            {
                if (tryToShow)
                {
                    return
                }
                if (!isFragmentVisible())
                {
                    onFragmentPause()
                    isLastVisible = false
                }
                // 上次不可见
            } else
            {
                var tryToHide = !tryToShow
                if (tryToHide)
                {
                    return
                }
                if (isFragmentVisible())
                {
                    onFragmentResume(isFirst, isViewDestroyed);
                    isLastVisible = true
                    isFirst = false
                }
            }
        } catch (throwable: Throwable)
        {
            Logger.e(TAG, "tryToChangeVisibility: {}, {}", tryToShow, throwable)
        }
    }

    /**
     * Fragment是否可见
     * @return
     */
    private fun isFragmentVisible(): Boolean
    {
        if (isResuming() && userVisibleHint && !hidden)
        {
            return true
        }
        return false
    }

    /**
     * Fragment 是否在前台。
     * @return
     */
    private fun isResuming(): Boolean
    {
        return isResuming
    }

    /**
     * Fragment 可见时回调
     * @param isFirst 是否是第一次显示
     * @param isViewDestroyed Fragment中的View是否被回收过。
     * 存在这种情况:Fragment的View 被回收, 但是Fragment实例仍在.
     */
    open fun onFragmentResume(isFirst: Boolean, isViewDestroyed: Boolean)
    {
        try
        {
            //判断是否包含子Fragment.
            if (StringFormat.isEmpty(getChildFragment()))
            {
                //无子Fragment, 添加友盟统计.
                if (fragmentConfig.isStPage) {
                    StatisHelper.onPageStart(getPageName(), this)
                }
            }
        } catch (throwable: Throwable)
        {
            Logger.e(TAG, "onFragmentResume: {}, {}, {}", isFirst, isViewDestroyed, throwable)
        }
    }

    /**
     * Fragment 不可见时回调
     */
    open fun onFragmentPause()
    {
        try
        {
            //判断是否包含子Fragment.
            if (StringFormat.isEmpty(getChildFragment()))
            {
                //无子Fragment, 添加友盟统计.
                if (fragmentConfig.isStPage) {
                    StatisHelper.onPageEnd(getPageName(), this);
                }
            }
        } catch (throwable: Throwable)
        {
            Logger.e(TAG, "onFragmentPause: {}", throwable)
        }

    }
    //=========================友盟统计Fragment页面跳转专用(End)=================================
}

