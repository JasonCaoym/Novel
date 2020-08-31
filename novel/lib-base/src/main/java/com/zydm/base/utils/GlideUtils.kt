package com.zydm.base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.duoyue.lib.base.app.user.UserManager
import com.duoyue.lib.base.log.Logger
import com.duoyue.lib.base.transform.GlideCircleTransform
import com.zydm.base.R
import com.zydm.base.utils.transformation.CropTransformation
import com.zydm.base.utils.transformation.RatioBlurTransformation

/*
    Glide工具类
 */
object GlideUtils {
    /**
     * 日志Tag
     */
    private val TAG = "Base#GlideUtils"

    /**
     *   后续如果在添加新的加载图片的方法  URL都通过此方法加入UID  注意注意注意注意
     *
     *   后续如果在添加新的加载图片的方法  URL都通过此方法加入UID  注意注意注意注意
     *
     *   后续如果在添加新的加载图片的方法  URL都通过此方法加入UID  注意注意注意注意
     * */

    fun initUrl(url: String?): String {
//        return url + "?uid=" + UserManager.getInstance().userInfo.uid // 没有用到了
        return "$url"
    }

    fun loadImage(context: Context, url: String?, imageView: ImageView, width: Int, height: Int) {
        val myOptions = RequestOptions()
            .placeholder(R.mipmap.a) // 设置了占位图
            .transforms(CenterCrop())
            .skipMemoryCache(false).override(width, height)
//            .override()
//        var requestListener = object : RequestListener<Drawable> {
//            override fun onLoadFailed(
//                e: GlideException?,
//                model: Any,
//                target: Target<Drawable>,
//                isFirstResource: Boolean
//            ): Boolean {
//                return false
//            }
//
//            override fun onResourceReady(
//                resource: Drawable,
//                model: Any,
//                target: Target<Drawable>,
//                dataSource: DataSource,
//                isFirstResource: Boolean
//            ): Boolean {
//                Glide.get(context).clearMemory()
//                return false
//            }
//        }
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(myOptions).into(imageView)
        } else {
            Glide.with(context)
                .load(initUrl(url))
                .apply(myOptions).into(imageView)
        }
    }

    fun loadImage(context: Context, url: String?, imageView: ImageView, radius: Int, width: Int, height: Int) {
        val myOptions = RequestOptions()
            .placeholder(R.mipmap.a) // 设置了占位图
            .transforms(CenterCrop(), RoundedCorners(radius))
            .skipMemoryCache(false).override(width, height)
//            .override()
//        var requestListener = object : RequestListener<Drawable> {
//            override fun onLoadFailed(
//                e: GlideException?,
//                model: Any,
//                target: Target<Drawable>,
//                isFirstResource: Boolean
//            ): Boolean {
//                return false
//            }
//
//            override fun onResourceReady(
//                resource: Drawable,
//                model: Any,
//                target: Target<Drawable>,
//                dataSource: DataSource,
//                isFirstResource: Boolean
//            ): Boolean {
//                Glide.get(context).clearMemory()
//                return false
//            }
//        }
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(myOptions).into(imageView)
        } else {
            Glide.with(context)
                .load(initUrl(url))
                .apply(myOptions).into(imageView)
        }
    }

    fun loadImage(context: Context, url: String?, imageView: ImageView, radius: Int) {
        val myOptions = RequestOptions()
            .placeholder(R.mipmap.a) // 设置了占位图
            .transforms(CenterCrop(), RoundedCorners(radius))
            .skipMemoryCache(false)
//            .override()
//        var requestListener = object : RequestListener<Drawable> {
//            override fun onLoadFailed(
//                e: GlideException?,
//                model: Any,
//                target: Target<Drawable>,
//                isFirstResource: Boolean
//            ): Boolean {
//                return false
//            }
//
//            override fun onResourceReady(
//                resource: Drawable,
//                model: Any,
//                target: Target<Drawable>,
//                dataSource: DataSource,
//                isFirstResource: Boolean
//            ): Boolean {
//                Glide.get(context).clearMemory()
//                return false
//            }
//        }
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(myOptions).into(imageView)
        } else {
            Glide.with(context)
                .load(initUrl(url))
                .apply(myOptions).into(imageView)
        }
    }

    fun loadImage(context: Context, url: String?, imageView: ImageView) {
        try {
            Log.i("loadImage", initUrl(url))
            val myOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.a) // 设置了占位图
                .skipMemoryCache(false) //设置内存缓存
                .dontAnimate()//取消加载变换动画
                .transforms(CenterCrop())
            if (TextUtils.isEmpty(url)) {
                Glide.with(context)
                    .load("http://null")
                    .apply(myOptions).into(imageView)
            } else {
                Glide.with(context).load(initUrl(url)).apply(myOptions).into(imageView)
            }
        } catch (throwable: Throwable) {
            Logger.e(TAG, "loadImage: {}, {}", url, imageView)
        }
    }

    fun loadAvatarImage(context: Context, url: String?, imageView: ImageView) {
        try {
            Log.i("loadImage", initUrl(url))
            val myOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.mine_head_icon) // 设置了占位图
                .skipMemoryCache(false) //设置内存缓存
                .dontAnimate()//取消加载变换动画
                .transforms(CenterCrop())
            if (TextUtils.isEmpty(url)) {
                Glide.with(context)
                    .load("http://null")
                    .apply(myOptions).into(imageView)
            } else {
                Glide.with(context).load(initUrl(url)).apply(myOptions).into(imageView)
            }
        } catch (throwable: Throwable) {
            Logger.e(TAG, "loadImage: {}, {}", url, imageView)
        }
    }

    fun loadGiftImage(context: Context, url: String?, imageView: ImageView) {
        try {
            val myOptions = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.bg_f5f5f5) // 设置了占位图
                .skipMemoryCache(false) //设置内存缓存
                .dontAnimate()//取消加载变换动画
                .transforms(CenterCrop())
            if (TextUtils.isEmpty(url)) {
                Glide.with(context)
                    .load("http://null")
                    .apply(myOptions).into(imageView)
            } else {
                Glide.with(context).load(initUrl(url)).apply(myOptions).into(imageView)
            }
        } catch (throwable: Throwable) {
            Logger.e(TAG, "loadImage: {}, {}", url, imageView)
        }
    }

    fun loadDetailImage(context: Context, url: String?, imageView: ImageView, radius: Int) {
        val myOptions = RequestOptions()
            .placeholder(R.mipmap.b) // 设置了占位图
            .transforms(CenterCrop(), RoundedCorners(radius))
            .skipMemoryCache(false)
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(myOptions).into(imageView)
        } else {
            Glide.with(context).load(url).apply(myOptions).into(imageView)
        }
    }

    /**
     * 加载图片设置为背景图.
     */
    fun loadBackground(context: Context, url: String?, view: View, blur: Boolean) {
        var requestOptions = RequestOptions().dontAnimate()
            .centerCrop()
            .placeholder(R.drawable.bg_f5f5f5) // 设置了占位图
        if (blur) {
            requestOptions = requestOptions.transform(RatioBlurTransformation(80, 20, CropTransformation.CropType.TOP))
        }
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(requestOptions).into(object : ViewTarget<View, Drawable>(view) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        view.background = resource
                    }
                })
        } else {
            Glide.with(context)
                .load(initUrl(url))
                .apply(requestOptions).into(object : ViewTarget<View, Drawable>(view) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        view.background = resource
                    }
                })
        }
    }

    fun loadImage(context: Context, bitmap: Bitmap, imageView: ImageView) {
        val myOptions = RequestOptions()
//            .placeholder(R.mipmap.a) // 设置了占位图
            .transforms(CenterCrop())
            .skipMemoryCache(false)

        Glide.with(context).load(bitmap).apply(myOptions).into(imageView)
    }

    /**
     * 取消加载
     */
    fun clear(context: Context, view: View) {
        Glide.with(context).clear(view)
    }

    /*
    * 图书详情页需要单独设置默认
    * */
    fun loadCircleImage(context: Context, url: String?, imageView: ImageView, rid: Int) {
        val myOptions = RequestOptions()
            .centerCrop()
            .placeholder(rid)
            .transform(GlideCircleTransform(context))
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(myOptions).into(imageView)
        } else {
            Glide.with(context)
                .load(initUrl(url))
                .apply(myOptions).into(imageView)
        }
    }

    fun loadImageWidthNoCorner(context: Context, url: String?, imageView: ImageView) {
        val myOptions = RequestOptions()
            .centerCrop()
            .placeholder(imageView.getDrawable()) // 设置了占位图
            .skipMemoryCache(true) //设置内存缓存
            .dontAnimate()//取消加载变换动画
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(myOptions).into(imageView)
        } else {
            Glide.with(context)
                .load(initUrl(url))
                .apply(myOptions).into(imageView)
        }
    }

    fun loadBlurBackground(context: Context, url: String, view: View, Radius: Int, sampling: Int) {
        var requestOptions = RequestOptions().dontAnimate()
        requestOptions = requestOptions.transform(RatioBlurTransformation(20, 5, CropTransformation.CropType.TOP))
        Glide.with(context)
            .load(initUrl(url))
            .apply(requestOptions).into(object : ViewTarget<View, Drawable>(view) {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    view.background = resource
                }
            })
    }

    fun loadFixImage(context: Context, url: String?, imageView: ImageView, radius: Int) {
        val myOptions = RequestOptions()
            .placeholder(imageView.getDrawable()) // 设置了占位图
            .skipMemoryCache(false) //设置内存缓存
            .dontAnimate()//取消加载变换动画
            .transforms(CenterCrop(), RoundedCorners(radius)).override(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewUtils.dp2px(125f))
        if (TextUtils.isEmpty(url)) {
            Glide.with(context)
                .load("http://null")
                .apply(myOptions).into(imageView)
        } else {
            Glide.with(context)
                .load(initUrl(url))
                .apply(myOptions).into(imageView)
        }
    }

    fun loadImage(context: Context, url: Int, imageView: ImageView, radius: Int) {
        val myOptions = RequestOptions()
            .transforms(CenterCrop(), RoundedCorners(radius))
            .skipMemoryCache(false)


        Glide.with(context).load(url).addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }).apply(myOptions).into(imageView)
    }

    /**
     * 获取书籍图片圆角弧度.
     */
    fun getBookRadius(): Int {
        return ViewUtils.dp2px(2.5f)
    }


    fun cleanMemory(context: Context) {
        Glide.get(context).clearMemory()
    }


    fun initGlide(context: Context) {
        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize

        val glideBuilder = GlideBuilder()
        glideBuilder.setMemoryCache(LruResourceCache(defaultMemoryCacheSize.toLong()))
        glideBuilder.setBitmapPool(LruBitmapPool(defaultBitmapPoolSize.toLong()))
    }
}
