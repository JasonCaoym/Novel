package com.zydm.base.injection.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/*
    Application级别Module
 */
@Module
class AppModule(private val context: Application) {

    @Singleton
    @Provides
    fun provideContext():Context{
        return this.context
    }
}
