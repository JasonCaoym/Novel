/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zzdm.tinker.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;
import com.duoyue.lib.base.app.AppFrontBackHelper;
import com.tencent.tinker.entry.ApplicationLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.zzdm.tinker.Log.MyLogImp;
import com.zzdm.tinker.service.SampleResultService;
import com.zzdm.tinker.util.SampleApplicationContext;
import com.zzdm.tinker.util.TinkerManager;

/**
 * because you can not use any other class in your application, we need to
 * move your implement of Application to {@link ApplicationLifeCycle}
 * As Application, all its direct reference class should be in the main dex.
 *
 * We use tinker-android-anno to make sure all your classes can be patched.
 *
 * application: if it is start with '.', we will add SampleApplicationLifeCycle's package name
 *
 * flags:
 * TINKER_ENABLE_ALL: support dex, lib and resource
 * TINKER_DEX_MASK: just support dex
 * TINKER_NATIVE_LIBRARY_MASK: just support lib
 * TINKER_RESOURCE_MASK: just support resource
 *
 * loaderClass: define the tinker loader class, we can just use the default TinkerLoader
 *
 * loadVerifyFlag: whether check files' md5 on the load time, defualt it is false.
 *
 * Created by zhangshaowen on 16/3/17.
 */
//@DefaultLifeCycle(application = "com.victor.tinkerdemo.app.SampleApplication",
//                  flags = ShareConstants.TINKER_ENABLE_ALL,
//                  loadVerifyFlag = false)
public abstract class SampleApplicationLike extends DefaultApplicationLike {
    private static final String TAG = "SampleApplicationLike";

    private boolean hasInit;

    public SampleApplicationLike(Application application, Integer tinkerFlags, Boolean tinkerLoadVerifyFlag,
                                 Long applicationStartElapsedTime, Long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
        application.getSharedPreferences(SampleResultService.PREFERENCE_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(SampleResultService.KEY_NEED_UPDATE, false).apply();

        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(application, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理

            }

            @Override
            public void onBack() {
                //应用切到后台处理
                if (getApplication().getSharedPreferences(SampleResultService.PREFERENCE_NAME, Context.MODE_PRIVATE)
                        .getBoolean(SampleResultService.KEY_NEED_UPDATE, false)) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });
    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        if (hasInit) {
            return;
        }
        hasInit = true;
        Log.e(TAG, "application onBaseContextAttacked");
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        SampleApplicationContext.application = getApplication();
        SampleApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker.with(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

}
