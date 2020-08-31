package com.duoyue.mianfei.xiaoshuo.read.ui.bookshelf

import android.view.View
import com.duoyue.mianfei.xiaoshuo.R
import com.zydm.base.data.dao.ShelfBookListBean
import com.zydm.base.data.tools.DataUtils
import com.zydm.base.ext.loadUrl
import com.zydm.base.ui.item.AbsItemView
import com.zydm.base.utils.ViewUtils
import kotlinx.android.synthetic.main.shelf_book_item.view.*

open class EditShelfBooksView : AbsItemView<ShelfBookListBean>() {

    private val ids = arrayOf(
            R.id.book_1,
            R.id.book_2,
            R.id.book_3)

    override fun onCreate() {
        setContentView(R.layout.three_books_horizontal_layout)
        ids.forEachIndexed { index, id ->
            val view = mItemView.findViewById<View>(id)
            val width = (ViewUtils.getPhonePixels()[0] - ViewUtils.dp2px(17.0f) * 6) / 3
            val height = width * 4 / 3
            ViewUtils.setViewSize(view.book_cover, width, height)
            view.book_name.maxWidth = width
            view.select_icon.visibility = View.VISIBLE
            view.setOnClickListener(this)
            view.tag = index
        }
    }

    override fun onSetData(isFirstSetData: Boolean, isPosChanged: Boolean, isDataChanged: Boolean) {
        ids.forEachIndexed { index, id ->
            val view = mItemView.findViewById<View>(id)
            val data = DataUtils.getItem(mItemData.list, index)
            if (data != null) {
                view.visibility = View.VISIBLE
                view.book_cover.loadUrl(data.bookCover)
                view.book_name.text = data.bookName
                view.select_icon.setImageResource(if (data.mIsSelect) R.mipmap.icon_class_boy else R.mipmap.icon_class_gril)
                view.isEnabled = true
            } else {
                view.visibility = View.INVISIBLE
                view.isEnabled = false
            }
        }
    }
}
