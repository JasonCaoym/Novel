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

package com.zzdm.tinker.service;

import android.content.Context;
import com.duoyue.lib.base.log.Logger;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.tencent.tinker.lib.util.TinkerServiceInternals;
import com.zydm.base.common.Constants;
import com.zzdm.tinker.util.Utils;

import java.io.File;


/**
 * optional, you can just use DefaultTinkerResultService
 * we can restart process when we are at background or screen off
 * Created by zhangshaowen on 16/4/13.
 */
public class SampleResultService extends DefaultTinkerResultService {
    private static final String TAG = "tinker";
    public static final String KEY_NEED_UPDATE = "nedd_update_patch";
    public static final String PREFERENCE_NAME = "update_tinker";

    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            Logger.e(TAG, "SampleResultService received null result!!!!");
            return;
        }
        Logger.i(TAG, "SampleResultService receive result: %s", result.toString());

        //first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());

//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (result.isSuccess) {
//                    Toast.makeText(getApplicationContext(), "patch success, please restart process", Toast.LENGTH_LONG).show();
//                    ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
//                    Process.killProcess(Process.myPid());
//                } else {
//                    Toast.makeText(getApplicationContext(), "patch fail, please check reason", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
        // is success and newPatch, it is nice to delete the raw file, and restart at once
        // for old patch, you can't delete the patch file
        Logger.e(TAG, result.toString());
        if (result.isSuccess) {
            TinkerLoadResult tinkerLoadResult = Tinker.with(this).getTinkerLoadResultIfPresent();

            Logger.e(TAG, "合并成功, current version : " + tinkerLoadResult.currentVersion + ", result version : " + result.patchVersion);
            Logger.e(TAG, "补丁包路径 : " + result.rawPatchFilePath);
            deleteRawPatchFile(new File(result.rawPatchFilePath));

            //not like TinkerResultService, I want to restart just when I am at background!
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            if (checkIfNeedKill(result)) {
//                if (Utils.isBackground()) {
//                    Logger.i(TAG, "it is in background, just restart process");
//                    restartProcess();
//                } else {
                    //we can wait process at background, such as onAppBackground
                    //or we can restart when the screen off
                    getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_NEED_UPDATE, true).apply();
                    Logger.i(TAG, "监听锁屏后更新补丁");
                    new Utils.ScreenState(getApplicationContext(), new Utils.ScreenState.IOnScreenOff() {
                        @Override
                        public void onScreenOff() {
                            Logger.e(TAG, "开始更新补丁");
                            restartProcess();
                        }
                    });
//                }
            } else {
                Logger.i(TAG, "I have already install the newly patch version!");
                getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_NEED_UPDATE, false).apply();
            }
        }
    }

    /**
     * you can restart your process through service or broadcast
     */
    private void restartProcess() {
        Logger.i(TAG, "app is background now, i can kill quietly");
        //you can send service or broadcast intent to restart your process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
