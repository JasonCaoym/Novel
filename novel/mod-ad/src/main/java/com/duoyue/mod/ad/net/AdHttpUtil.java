package com.duoyue.mod.ad.net;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.http.HttpClient;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.mod.ad.bean.AdConfigBean;
import com.duoyue.mod.ad.bean.AdResponseBean;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.google.gson.Gson;
import com.zydm.base.data.tools.JsonUtils;
import com.zydm.base.tools.PhoneStatusManager;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdHttpUtil {

    private static final String TAG = "ad#http";
    private static final String AD_CONFIG = "/getWf/info";
    private static final String UPLOAD_STATISTICS = "/stats/count";
    private static final String UPLOAD_ERROR = "/stats/errorCount";

    /**
     * 拉取广告.
     */
    private static final String PULL = "START";

    /**
     * 拉取广告成功.
     */
    private static final String PULL_SUCC = "PULLED";

    /**
     * 拉取广告失败.
     */
    private static final String PULL_FAIL = "PULLFAIL";

    /**
     * 展示广告成功.
     */
    private static final String SHOW_SUCC = "SHOW";

    /**
     * 展示广告失败.
     */
    private static final String SHOW_FAIL = "SHOWFAIL";

    /**
     * 点击广告.
     */
    private static final String CLICK = "CLICK";

    /**
     * 放弃展示
     */
    private static final String GIVEUP = "SHOWGIVEUP";

    /**
     * 信息流重试节点
     */
    private static final String RETRYFLOW = "RETRYFLOW";

    /**
     * 轮播展示
     */
    private static final String VIEWPAGER = "VIEWPAGER";

    /**
     * 激励视频缓存成功
     */
    public static final String VIDIO_CACHE_SUCCESS = "AD_VIDEO_BUFFER";

    /**
     * 激励视频播放完毕
     */
    public static final String VIDIO_PLAY_SUCCESS = "AD_VIDEO_PALYED";

    /**
     * 激励视频播放错误
     */
    public static final String VIDIO_PLAY_FAIL = "AD_VIDEO_ERROR";

    /**
     * 激励视频奖励调用
     */
    public static final String VIDIO_TASK = "AD_VIDEO_REWARD";

    /**
     * 激励视频app下载
     */
    public static final String VIDIO_APP_DOWNLOAD = "AD_VIDEO_APP_DOWNLOAD";

    /**
     * 激励视频app暂停
     */
    public static final String VIDIO_APP_DOWNLOAD_PAUSE = "AD_VIDEO_APP_DOWNLOAD_PAUSE";

    /**
     * 激励视频APP下载失败
     */
    public static final String VIDIO_APP_DOWNLOAD_FAIL = "AD_VIDEO_APP_DOWNLOAD_FAIL";

    /**
     * 激励视频APP安装完成
     */
    public static final String VIDIO_APP_INSTALL = "AD_VIDEO_APP_INSTALLED";

    /**
     * 阅读器banner关闭按钮点击
     */
    public static final String C_CLOSEAD = "C_CLOSEAD";

    /**
     * 阅读器banner关闭按钮提示曝光
     */
    public static final String C_CLOSEAD_TIP = "C_CLOSEAD_TIP";

    private static OkHttpClient buildHttpClient() {
        return HttpClient.getInstance();
    }

    public static AdConfigBean request(String channalCode) {
        OkHttpClient client = buildHttpClient();
        Request.Builder builder = new Request.Builder();
        Map<String, String> params = new HashMap<>();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.AD_BASE_URL + AD_CONFIG).newBuilder();
        UserManager userManager = UserManager.getInstance();
        if (userManager != null && userManager.getUserInfo() != null) {
            params.put("userId", userManager.getUserInfo().uid);
        }
        params.put("channelCode", channalCode);
        params.put("version", PhoneStatusManager.getInstance().getAppVersionName());
        params.put("device", PhoneStatusManager.getInstance().getAppChannel());
        //协议版本号.
        params.put("protocolCode", String.valueOf(Constants.PROTOCOL_CODE));
        urlBuilder.addQueryParameter("data", new Gson().toJson(params));
        builder.url(urlBuilder.build());

        builder.get();
        Request request = builder.build();
        Logger.e(TAG, "request : " + request.toString());
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            int code = response.code();
            String body = response.body().string();
            Logger.e(TAG, "body : " + body);
            AdResponseBean result = JsonUtils.parseJson(body, AdResponseBean.class);
            if (result != null && result.getStatus().equalsIgnoreCase("ok")) {
                return result.getInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.e(TAG, "新广告配置获取失败 ： " + response);
        }
        return null;
    }

    private static void uploadAdStat(final AdSiteBean adSiteBean, final String operator, final long time) {
        if (adSiteBean == null) {
            Logger.e(TAG, "广告源为null, 广告节点" + operator);
            return;
        }
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = buildHttpClient();
                Request.Builder builder = new Request.Builder();
                Map<String, Object> params = new HashMap<>();
                HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.AD_BASE_URL + UPLOAD_STATISTICS).newBuilder();
                params.put("adId", adSiteBean.getId());
                UserManager userManager = UserManager.getInstance();
                if (userManager != null && userManager.getUserInfo() != null) {
                    params.put("userId", userManager.getUserInfo().uid);
                }
                params.put("channelCode", adSiteBean.getChannelCode());
                params.put("origin", adSiteBean.getOrigin());
                params.put("sdkAdType", adSiteBean.getAdType());
                params.put("sdkAdAppId", adSiteBean.getAdAppId());
                params.put("sdkAdId", adSiteBean.getAdId());
                params.put("operator", operator);
                params.put("version", PhoneStatusManager.getInstance().getAppVersionName());
                params.put("device", PhoneStatusManager.getInstance().getAppChannel());
                params.put("type", adSiteBean.getType());
                params.put("renderType", adSiteBean.getRenderType());
                params.put("num", time);
                try {
                    params.put("imei", PhoneUtil.getIMEI(BaseContext.getContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //协议版本号.
                params.put("protocolCode", String.valueOf(Constants.PROTOCOL_CODE));
                urlBuilder.addQueryParameter("data", new Gson().toJson(params));
                builder.url(urlBuilder.build());

                builder.get();
                Request request = builder.build();
                Logger.e(TAG, "upload : " + request.toString());
                Call call = client.newCall(request);
                Response response = null;
                try {
                    response = call.execute();
                    int code = response.code();
                    if (code == 200) {
                        Logger.e(TAG, "广告节点" + operator + "上传成功： " + adSiteBean.toString());
                    } else {
                        Logger.e(TAG, "广告节点" + operator + "上传失败： "+ adSiteBean.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.e(TAG, "upload 失败 ： " + adSiteBean.toString());
                }
            }
        });
    }

    public static void pull(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, PULL, 0);
    }

    public static void pullFail(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, PULL_FAIL, 0);
    }

    public static void pullSuccess(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, PULL_SUCC, 0);
    }

    public static void showFail(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, SHOW_FAIL, 0);
    }

    public static void showSuccess(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, SHOW_SUCC, 0);
    }

    public static void click(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, CLICK, 0);
    }

    public static void retryInfo(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, RETRYFLOW, 0);
    }

    public static void showGiveUp(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, GIVEUP, 0);
    }

    public static void viewPager(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIEWPAGER, 0);
    }

    public static void uploadErrorMsg(final AdSiteBean adSiteBean, final String codeStr, final String errorMsg) {
        if (adSiteBean == null) {
            return;
        }
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = buildHttpClient();
                Request.Builder builder = new Request.Builder();
                Map<String, Object> params = new HashMap<>();
                HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.AD_BASE_URL + UPLOAD_ERROR).newBuilder();
                params.put("adId", adSiteBean.getId());
                UserManager userManager = UserManager.getInstance();
                if (userManager != null && userManager.getUserInfo() != null) {
                    params.put("userId", userManager.getUserInfo().uid);
                }
                params.put("channelCode", adSiteBean.getChannelCode());
                params.put("origin", adSiteBean.getOrigin());
                params.put("sdkAdType", adSiteBean.getAdType());
                params.put("sdkAdAppId", adSiteBean.getAdAppId());
                params.put("sdkAdId", adSiteBean.getAdId());
                params.put("code", codeStr);
                params.put("msg", errorMsg);
                params.put("device", PhoneStatusManager.getInstance().getAppChannel());
                params.put("version", PhoneStatusManager.getInstance().getAppVersionName());
                params.put("type", adSiteBean.getType());
                params.put("renderType", adSiteBean.getRenderType());
                try {
                    params.put("imei", PhoneUtil.getIMEI(BaseContext.getContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //协议版本号.
                params.put("protocolCode", String.valueOf(Constants.PROTOCOL_CODE));
                urlBuilder.addQueryParameter("data", new Gson().toJson(params));
                builder.url(urlBuilder.build());

                builder.get();
                Request request = builder.build();
                Logger.e(TAG, "upload : " + request.toString());
                Call call = client.newCall(request);
                Response response = null;
                try {
                    response = call.execute();
                    int code = response.code();
                    if (code == 200) {
                        Logger.e(TAG, "广告错误：code = " + codeStr + ",msg = " + errorMsg + ",上传成功： " + adSiteBean.toString());
                    } else {
                        Logger.e(TAG, "广告错误：code = " + codeStr + ",msg = " + errorMsg + ",上传失败： " + adSiteBean.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.e(TAG, "upload 广告错误上报失败 ： " + adSiteBean.toString());
                }
            }
        });
    }


    /**
     * 激励视频缓存成功
     * @param adSiteBean
     * @param time 单位：毫秒
     */
    public static void vidioCacheSuccess(AdSiteBean adSiteBean, long time) {
        uploadAdStat(adSiteBean, VIDIO_CACHE_SUCCESS, time);
    }

    /**
     * 激励视频播放完毕
     */
    public static void vidioPlaySuccess(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIDIO_PLAY_SUCCESS, 0);
    }

    /**
     * 激励视频播放错误
     */
    public static void vidioPlayFail(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIDIO_PLAY_FAIL, 0);
    }

    /**
     * 激励视频奖励调用
     */
    public static void vidioTask(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIDIO_TASK, 0);
    }

    /**
     * 激励视频app下载中
     */
    public static void vidioAPPDownload(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIDIO_APP_DOWNLOAD, 0);
    }

    /**
     * 激励视频app下载暂停
     */
    public static void vidioAPPDownloadPause(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIDIO_APP_DOWNLOAD_PAUSE, 0);
    }

    /**
     * 激励视频app下载失败
     */
    public static void vidioAPPDownloadFail(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIDIO_APP_DOWNLOAD_FAIL, 0);
    }

    /**
     * 激励视频app安装完成
     */
    public static void vidioAPPInstall(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, VIDIO_APP_INSTALL, 0);
    }

    /**
     * 阅读器-banner关闭按钮点击
     */
    public static void clickBannerClose(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, C_CLOSEAD, 0);
    }

    /**
     * 阅读器-banner提示展示
     */
    public static void showBannerTip(AdSiteBean adSiteBean) {
        uploadAdStat(adSiteBean, C_CLOSEAD_TIP, 0);
    }
}
