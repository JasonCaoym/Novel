package com.zzdm.tinker.service;

import android.content.Context;
import android.content.Intent;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.timer.TimerTask;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.google.gson.Gson;
import com.zydm.base.tools.PhoneStatusManager;
import com.zzdm.tinker.app.BuildInfo;
import com.zzdm.tinker.net.bean.ApiTinkerResult;

public class TinkerRequestTastk extends TimerTask {

    private static final String TAG = "tinker#TinkerRequestService";

    private Context mContext;


    public TinkerRequestTastk(Context context) {
        mContext = context;
    }

    @Override
    public String getAction() {
        return TAG;
    }

    @Override
    public long getPollTime() {
        return 60;
    }

    @Override
    public long timeUp() throws Throwable {
        TinkerRequest request = new TinkerRequest();
        request.hotfixVersionCode = BuildInfo.PATCH_VERSION;
        request.appVersionCode = PhoneStatusManager.getInstance().getAppVersionCode();

        JsonResponse<ApiTinkerResult> jsonResponse = new JsonPost.SyncPost<ApiTinkerResult>()
                .setRequest(request)
                .setResponseType(ApiTinkerResult.class)
                .post();
        if (jsonResponse != null && jsonResponse.status == 1) {
            ApiTinkerResult resultData = jsonResponse.data;
            Logger.i(TAG, "json: "  + new Gson().toJson(resultData));
            if (resultData != null && resultData.getHotfixVersionCode() > Integer.parseInt(BuildInfo.PATCH_VERSION)) {
                Intent intent = new Intent(mContext, TinkerPatchDownloadService.class);
                intent.putExtra("url", resultData.getDownloadUrl());
                mContext.startService(intent);
                Logger.i(TAG, "开始下载补丁");
                FuncPageStatsApi.hotUpdateStartDownload();
            } else {
                Logger.i(TAG, "没有新补丁：remote patch version = " +  resultData.getHotfixVersionCode()
                        + ", current patch version = " + BuildInfo.PATCH_VERSION);
            }
        }
        return 60;
    }
}
