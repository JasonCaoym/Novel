package com.zydm.base.ui.item

import android.app.Activity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.zydm.base.data.tools.DataUtils
import io.reactivex.annotations.NonNull
import java.util.*

/**
 * Created by YinJiaYan on 2017/6/26.
 */

class RecyclerAdapter internal constructor(
        private val mActivity: Activity,
        private val mItemViewHelper: ItemViewHelper,
        private val mDataContainer: List<Any> = ArrayList()) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(), IAdapter {

    /**
     * 拓展参数.
     */
    private var mExtParamMap: MutableMap<String, String>? = null

    override fun getDataList(): List<*> {
        return this.mDataContainer
    }

    /**
     * 添加扩展参数.
     * @param key
     * @param value
     */
    @Synchronized
    fun addExtParam(key: String, value: String)
    {
        if (mExtParamMap == null)
        {
            mExtParamMap = HashMap()
        }
        mExtParamMap!![key] = value
    }

    /**
     * 根据Key获取扩展参数.
     */
    override fun getExtParam(key: String): String?
    {
        if (mExtParamMap == null)
        {
            return null
        }
        return mExtParamMap?.get(key)
    }

    fun setData(dataList: List<*>) {
        if (DataUtils.setAll(this.mDataContainer, dataList)) {
            this.notifyDataSetChanged()
        }
    }

    fun setData(dataList: List<*>, @NonNull diffCallback: DiffCallback?) {
        if (diffCallback == null) {
            setData(dataList)
            return
        }
        diffCallback.setData(ArrayList(this.mDataContainer), dataList)
        if (!DataUtils.setAll(this.mDataContainer, dataList)) {
            return
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
    }

    fun notifyItemChangedSafe(newData: List<*>, position: Int) {
        if (!DataUtils.setAll(this.mDataContainer, newData)) {
            return
        }
        if (position < 0 || position >= this.mDataContainer.size) {
            notifyDataSetChanged()
            return
        }
        notifyItemChanged(position)
    }

    fun getItem(position: Int): Any? {
        return DataUtils.getItem(this.mDataContainer, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //        LogUtils.d(TAG, "onCreateViewHolder " + parent.getChildCount());
        val itemView = mItemViewHelper.createItemView(viewType)
        itemView.mAdapter = this
        return itemView.createRecyclerViewHolder(mActivity, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //        LogUtils.d(TAG, "position:" + position + "  isRecyclable:" + holder.isRecyclable());
        holder.mItemView.setItemData(position, getItem(position)!!)
    }

    override fun getItemViewType(position: Int): Int {
        return if (mItemViewHelper.mIsSingleTypeItem) {
            super.getItemViewType(position)
        } else mItemViewHelper.getItemViewType(getItem(position)!!.javaClass)
    }

    override fun getItemCount(): Int {
        return this.mDataContainer.size
    }

    /**
     * Created by YinJiaYan on 2017/6/27.
     */
    class ViewHolder(mView: View,
                     internal val mItemView: AbsItemView<*>) : RecyclerView.ViewHolder(mView)

    /**
     * Created by YinJiaYan on 2017/6/29
     */
    class DiffCallback : DiffUtil.Callback() {

        private var mOldData: List<*>? = null
        private var mNewData: List<*>? = null

        fun setData(@NonNull oldData: List<*>?, @NonNull newData: List<*>?) {
            mOldData = oldData ?: ArrayList<Any>()
            mNewData = newData ?: ArrayList<Any>()
        }

        override fun getOldListSize(): Int {
            return mOldData!!.size
        }

        override fun getNewListSize(): Int {
            return mNewData!!.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldData!![oldItemPosition] === mNewData!![newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldData!![oldItemPosition] == mNewData!![newItemPosition]
        }
    }

    companion object {

        private val TAG = "RecyclerAdapter"
    }
}
