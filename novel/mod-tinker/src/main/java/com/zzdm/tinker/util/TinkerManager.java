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

package com.zzdm.tinker.util;

import android.content.Context;
import android.text.TextUtils;
import com.duoyue.lib.base.log.Logger;
import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.listener.PatchListener;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.zzdm.tinker.app.BuildInfo;
import com.zzdm.tinker.crash.SampleUncaughtExceptionHandler;
import com.zzdm.tinker.reporter.SampleLoadReporter;
import com.zzdm.tinker.reporter.SamplePatchListener;
import com.zzdm.tinker.reporter.SamplePatchReporter;
import com.zzdm.tinker.service.SampleResultService;

import java.io.File;


/**
 * Created by zhangshaowen on 16/7/3.
 */
public class TinkerManager {
    private static final String TAG = "Tinker#TinkerManager";

    private static ApplicationLike                applicationLike;
    private static SampleUncaughtExceptionHandler uncaughtExceptionHandler;
    private static boolean isInstalled = false;

    public static void setTinkerApplicationLike(ApplicationLike appLike) {
        applicationLike = appLike;
    }

    public static ApplicationLike getTinkerApplicationLike() {
        return applicationLike;
    }

    public static void initFastCrashProtect() {
        if (uncaughtExceptionHandler == null) {
            uncaughtExceptionHandler = new SampleUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        }
    }

    public static void setUpgradeRetryEnable(boolean enable) {
        UpgradePatchRetry.getInstance(applicationLike.getApplication()).setRetryEnable(enable);
    }


    /**
     * all use default class, simply Tinker install method
     */
    public static void sampleInstallTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        TinkerInstaller.install(appLike);
        isInstalled = true;

    }

    /**
     * you can specify all class you want.
     * sometimes, you can only install tinker in some process you want!
     *
     * @param appLike
     */
    public static void installTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        //or you can just use DefaultLoadReporter
        LoadReporter loadReporter = new SampleLoadReporter(appLike.getApplication());
        //or you can just use DefaultPatchReporter
        PatchReporter patchReporter = new SamplePatchReporter(appLike.getApplication());
        //or you can just use DefaultPatchListener
        PatchListener patchListener = new SamplePatchListener(appLike.getApplication());
        //you can set your own upgrade patch if you need
        AbstractPatch upgradePatchProcessor = new UpgradePatch();

        TinkerInstaller.install(appLike,
            loadReporter, patchReporter, patchListener,
            SampleResultService.class, upgradePatchProcessor);

        isInstalled = true;
    }

    /**
     * 开始更新补丁包
     */
    public static void updatePatch(Context context, String path) {
        if (context == null || TextUtils.isEmpty(path)) {
            return;
        }
        File patchFile = new File(path);
        if (patchFile.exists()) {
            TinkerInstaller.onReceiveUpgradePatch(context, path);
        }
    }

    /**
     * 清除已经加载过的补丁
     * @param context
     */
    public static void cleanPatch(Context context) {
        if (context != null) {
            Tinker.with(context).cleanPatch();
        }
    }

    /**
     * 加载补丁库
     * @param context
     */
    public static void loadLibPatch(Context context, String libName) {
        // #method 1, hack classloader library path
//        TinkerLoadLibrary.installNavitveLibraryABI(context, "armeabi");
//        System.loadLibrary(libName);

        // #method 2, for lib/armeabi, just use TinkerInstaller.loadLibrary
      TinkerLoadLibrary.loadArmLibrary(context, libName);

        // #method 3, load tinker patch library directly
//      TinkerInstaller.loadLibraryFromTinker(getApplicationContext(), "assets/x86", "stlport_shared");
    }
}
