package com.zydm.base.ui.item

import android.app.Activity
import android.support.v4.util.SparseArrayCompat
import com.zydm.base.utils.ClassUtils
import io.reactivex.annotations.NonNull
import java.util.*

class AdapterBuilder {
    private val mItemInfoMap = SparseArrayCompat<ItemInfo>()
    private var mDataContainer: List<Any> = ArrayList()

    @JvmOverloads
    fun <V : AbsItemView<*>> putItemClass(
            itemViewClass: Class<V>,
            itemListener: ItemListener<V>? = null): AdapterBuilder {

        val itemDataClass = ClassUtils.getGenericsClass(itemViewClass, AbsItemView::class.java, 0)
        val itemType = itemDataClass.hashCode()
        mItemInfoMap.put(itemType, ItemInfo(itemType, itemViewClass, itemListener))
        return this
    }

    fun setDataContainer(dataContainer: MutableList<Any> = ArrayList()): AdapterBuilder {
        dataContainer.clear()
        mDataContainer = dataContainer
        return this
    }

    fun builderListAdapter(activity: Activity): ListAdapter {
        val itemViewHelper = ItemViewHelper(activity.javaClass.simpleName, mItemInfoMap)
        return ListAdapter(activity, itemViewHelper, mDataContainer)
    }

    fun builderRecyclerAdapter(activity: Activity): RecyclerAdapter {
        val itemViewHelper = ItemViewHelper(activity.javaClass.simpleName, mItemInfoMap)
        return RecyclerAdapter(activity, itemViewHelper, mDataContainer)
    }
}

internal class ItemViewHelper(
        private val mActivitySimpleName: String,
        private val mItemInfoMap: SparseArrayCompat<ItemInfo>) {

    val mViewTypeCount = mItemInfoMap.size()

    val mIsSingleTypeItem = mViewTypeCount <= 1

    fun getItemViewType(itemDataClass: Class<*>): Int {
        val itemViewType = mItemInfoMap.indexOfKey(itemDataClass.hashCode())
        if (itemViewType < 0) {
            throw RuntimeException(
                "in page:$mActivitySimpleName itemData:${itemDataClass.simpleName} not find itemViewClass!"
            )
        }
        return itemViewType
    }

    fun createItemView(@NonNull itemDataClass: Class<*>): AbsItemView<*> {
        return createItemView(getItemViewType(itemDataClass))
    }

    fun createItemView(viewType: Int): AbsItemView<*> {
        try {
            val info = mItemInfoMap.valueAt(viewType)
            val itemView = info.itemClass.newInstance()
            itemView.setDataClassHash(info.itemType)
            itemView.mItemListener = info.listener as ItemListener<Any>?
            return itemView
        } catch (e: Exception) {
            throw RuntimeException(
                "in page:$mActivitySimpleName viewType:$viewType createItemView fail!$e${e.message}", e
            )
        }
    }
}

internal data class ItemInfo(val itemType: Int, val itemClass: Class<out AbsItemView<*>>, val listener: ItemListener<*>?)