package com.zydm.base.ui.item


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zydm.base.data.tools.DataUtils
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by YinJiaYan on 2017/6/7.
 */

class ListAdapter internal constructor(
        private val mActivity: Activity,
        private val mItemViewHelper: ItemViewHelper,
        private val mDataContainer: List<Any> = ArrayList()) : BaseAdapter(), IAdapter {

    companion object
    {
        /**
         * 扩展参数Key-榜单频道(1:男生;2:女生)
         */
        const val EXT_KEY_RANK_FREQUENCY = "rankFrequency"

        /**
         * 扩展参数Key-榜单Id
         */
        const val EXT_KEY_RANK_ID = "rankId"

        /**
         * 扩展参数Key-模块Id
         */
        const val EXT_KEY_MODULE_ID = "moduleId"
        /**
         * 扩展参数Key-上个页面Id
         */
        const val EXT_KEY_PARENT_ID = "parentId"

        const val EXT_KEY_MODEL_ID = "model_id"

        const val EXT_KEY_CURRENT_PAGE_ID = "curr_page_id"
    }

    /**
     * 拓展参数.
     */
    private var mExtParamMap: MutableMap<String, String>? = null

    fun setData(dataList: List<*>) {
        if (DataUtils.setAll(this.mDataContainer, dataList)) {
            this.notifyDataSetChanged()
        }
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
            return null;
        }
        return mExtParamMap?.get(key)
    }

    override fun getDataList(): List<*> {
        return this.mDataContainer
    }

    override fun getCount(): Int {
        return this.mDataContainer.size
    }

    override fun getItemCount(): Int {
        return count
    }

    override fun getItem(position: Int): Any? {
        return DataUtils.getItem(this.mDataContainer, position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getViewTypeCount(): Int {
        return mItemViewHelper.mViewTypeCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (mItemViewHelper.mIsSingleTypeItem) {
            super.getItemViewType(position)
        } else mItemViewHelper.getItemViewType(getItem(position)!!.javaClass)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemData = getItem(position)!!
        val itemView: AbsItemView<*>
        var view = convertView
        if (view == null) {
            val itemDataClass = itemData.javaClass
            itemView = mItemViewHelper.createItemView(itemDataClass)
            itemView.mAdapter = this
            view = itemView.createView(mActivity, parent)
            view.tag = itemView
        } else {
            itemView = view.tag as AbsItemView<*>
        }

        itemView.setItemData(position, itemData)
        return view
    }


}
