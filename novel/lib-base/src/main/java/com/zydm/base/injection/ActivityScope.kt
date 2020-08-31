package com.zydm.base.injection

import javax.inject.Scope

/*
    Activity级别 作用域
 */
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
