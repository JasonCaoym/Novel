package com.zydm.base.injection.component

import android.app.Activity
import android.content.Context
import com.trello.rxlifecycle2.LifecycleProvider
import com.zydm.base.injection.ActivityScope
import com.zydm.base.injection.module.ActivityModule
import com.zydm.base.injection.module.LifecycleProviderModule
import dagger.Component

/*
    Activity级别Component
 */
@ActivityScope
@Component(dependencies = [(AppComponent::class)],modules = [(ActivityModule::class), (LifecycleProviderModule::class)])
interface ActivityComponent {

    fun activity():Activity
    fun context(): Context
    fun lifecycleProvider(): LifecycleProvider<*>
}
