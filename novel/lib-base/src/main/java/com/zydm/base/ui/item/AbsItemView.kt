package com.zydm.base.ui.item

import android.app.Activity
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import com.zydm.base.utils.ViewUtils

/**
 * Created by YinJiaYan on 2017/6/26.
 */

abstract class AbsItemView<D : Any> : View.OnClickListener {

    val MIN_CLICK_DELAY_TIME = 300

    private var lastClickTime: Long = 0

    protected var TAG = javaClass.simpleName

    private var mDataClassHash = 0
    internal var mItemListener: ItemListener<Any>? = null
    protected lateinit var mItemView: View
    protected lateinit var mActivity: Activity
    private var mParent: ViewGroup? = null

    var mPosition: Int = 0
        private set

    private var mOldItemData: D? = null
    open lateinit var mItemData: D
        protected set

    var mAdapter: IAdapter? = null
    private var mDataTimestamp: Long = 0

    internal fun createRecyclerViewHolder(activity: Activity, parent: ViewGroup): RecyclerAdapter.ViewHolder {
        val rootView = createView(activity, parent)
        return RecyclerAdapter.ViewHolder(rootView, this)
    }

    fun createView(activity: Activity, parent: ViewGroup? = null): View {
        mActivity = activity
        mParent = parent
        onCreate()
        mItemListener?.onCreate(this)
        return mItemView
    }

    abstract fun onCreate()

    fun setContentView(layoutResID: Int): View {
        val rootView = ViewUtils.inflateView(mActivity, layoutResID, mParent)
        setContentView(rootView)
        return rootView
    }

    open fun setContentView(view: View) {
        mItemView = view
    }

    fun setItemData(positon: Int, itemData: Any) {
        val isDataChanged = itemData !== this.mOldItemData
        val isFirstSetData = mDataTimestamp == 0L
        val isPosChanged = positon != mPosition || isFirstSetData
        if (isDataChanged || isPosChanged || isFirstSetData) {
            mPosition = positon
            this.mItemData = itemData as D
            this.mOldItemData = this.mItemData
            mDataTimestamp = SystemClock.elapsedRealtime()
        }
        onSetData(isFirstSetData, isPosChanged, isDataChanged)
        mItemListener?.onSetDate(this)
    }

    abstract fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean)

    fun isMatch(itemDataClass: Class<*>): Boolean {
        return itemDataClass.hashCode() == mDataClassHash
    }

    internal fun setDataClassHash(dataClassHash: Int) {
        mDataClassHash = dataClassHash
    }

    override fun onClick(view: View) {
        mItemListener?.onClick(this, view)
    }

    fun <V : View> findView(id: Int): V {
        return mItemView.findViewById(id)
    }

    fun noDoubleListener(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            return true
        } else {
            return false
        }
    }
}

interface ItemListener<V> {

    fun onCreate(itemView: V) {
    }

    fun onSetDate(itemView: V) {
    }

    fun onClick(itemView: V, view: View) {
    }
}

open class ItemListenerAdapter<V> : ItemListener<V> {

    override fun onCreate(itemView: V) {
    }

    override fun onSetDate(itemView: V) {
    }

    override fun onClick(itemView: V, view: View) {
    }

}


