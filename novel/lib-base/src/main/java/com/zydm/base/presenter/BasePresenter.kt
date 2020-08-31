package com.zydm.base.presenter

import android.content.Context
import com.trello.rxlifecycle2.LifecycleProvider
import com.zydm.base.presenter.view.BaseView
import com.zydm.base.utils.NetWorkUtils
import io.reactivex.Single
import javax.inject.Inject

/*
    MVP中P层 基类
 */
open class BasePresenter<T: BaseView>{

    lateinit var mView:T

    //Dagger注入，Rx生命周期管理
    @Inject
    lateinit var lifecycleProvider: LifecycleProvider<*>


    @Inject
    lateinit var context:Context

    /*
        检查网络是否可用
     */
    fun checkNetWork():Boolean{
        if(NetWorkUtils.isNetWorkAvailable(context)){
            return true
        }
        Single.just("")
            .compose(lifecycleProvider.bindToLifecycle())

        mView.onError("网络不可用")
        return false
    }
}
