package com.zydm.base.ext

import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zydm.base.rx.Packing
import com.zydm.base.rx.RxUtils
import com.zydm.base.utils.GlideUtils
import com.zydm.base.utils.ViewUtils
import io.reactivex.Single

//Kotlin通用扩展

/*
    扩展点击事件
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

/*
    扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}


/*
    ImageView加载网络图片
 */
fun ImageView.loadUrl(url: String) {
    GlideUtils.loadImage(this.context, url, this)
}

/**
 * 加载头像
 */
fun ImageView.loadAvaterUrl(url: String) {
    GlideUtils.loadAvatarImage(this.context, url, this)
}

/*
    扩展视图可见性
 */
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setBackgroundColorRes(res: Int) {
    this.setBackgroundColor(ViewUtils.getColor(res))
}

fun View.loadBackground(url: String, blur: Boolean) {
    GlideUtils.loadBackground(this.context, url, this, blur)
}

fun View.setPaddingLeft(padding: Int) {
    setPadding(padding, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingRight(padding: Int) {
    setPadding(paddingLeft, paddingTop, padding, paddingBottom)
}

fun View.setPaddingTop(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, paddingBottom)
}

fun View.setPaddingBottom(padding: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, padding)
}

fun View.setPaddingHorizontal(paddingLeft: Int, paddingRight: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingVertical(paddingTop: Int, paddingBottom: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun TextView.setHtmlText(text: String) {
    if (!text.contains('<')) {
        setText(text)
        return
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY))
    } else {
        setText(Html.fromHtml(text))
    }
}

fun <T>Single<T>.mapPacking(): Single<Packing<T>> {
    return RxUtils.mapPacking(this)
}
