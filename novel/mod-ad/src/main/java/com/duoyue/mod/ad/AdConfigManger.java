package com.duoyue.mod.ad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.PermissionUtil;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.event.AdConfigEvent;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.random.RandomUtil;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.ad.bean.AdConfigBean;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.duoyue.mod.ad.dao.AdConfigHelp;
import com.duoyue.mod.ad.listener.AdCallbackListener;
import com.duoyue.mod.ad.net.AdHttpUtil;
import com.duoyue.mod.ad.platform.IAdView;
import com.duoyue.mod.ad.platform.csj.*;
import com.duoyue.mod.ad.platform.gdt.GDTBanner2;
import com.duoyue.mod.ad.platform.gdt.GDTInfoFlowModel;
import com.duoyue.mod.ad.platform.gdt.GDTLauncher;
import com.duoyue.mod.ad.platform.gdt.GDTRewardVideo;
import com.duoyue.mod.ad.platform.url.UrlBannerAdView;
import com.duoyue.mod.ad.platform.url.UrlFlowBallAdView;
import com.duoyue.mod.ad.platform.url.UrlInfoFlowAdView;
import com.duoyue.mod.ad.utils.AdConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.bean.BookRecordGatherResp;
import com.zydm.base.utils.SharePreferenceUtils;
import com.zydm.base.utils.TimeUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdConfigManger {

    private static final String TAG = "ad#AdConfigManger";

    private Context mContext;
    private HashMap<String, AdConfigBean> adConfigCacheMap = new HashMap<>();

    private SharedPreferences preferences;
    private Gson mGson;
    private String todayTime;


    private static class Inner {
        static final AdConfigManger INSTANCE = new AdConfigManger();
    }

    private AdConfigManger() {
        EventBus.getDefault().register(this);
    }

    public void init(Context context) {
        mContext = context;
        preferences = context.getSharedPreferences(AdConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
        todayTime = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02);
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                checkAdShowParams(true);
            }
        });
    }

    public static AdConfigManger getInstance() {
        return Inner.INSTANCE;
    }

    private synchronized void checkAdShowParams(boolean init) {
        todayTime = TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_02);

        if (!TextUtils.isEmpty(preferences.getString(todayTime, ""))) { // 读取出老数据
            try {
                Type type = new TypeToken<HashMap<String, AdConfigBean>>() {
                }.getType();
                adConfigCacheMap = mGson.fromJson(preferences.getString(todayTime, ""), type);
                Logger.e(TAG, todayTime + "老数据==所有广告位已显示次数:" + mGson.toJson(adConfigCacheMap));
            } catch (Throwable throwable) {
                Logger.e(TAG, "checkAdShowParams: {}", throwable);
            }
        } else {
            Logger.e(TAG, todayTime + "没有数据或者跨天了，清空");
            adConfigCacheMap.clear();
        }

        AdConfigBean adConfigBean = null;
        AdConfigBean cacheBean = null;
        AdConfigHelp configHelp = AdConfigHelp.getsInstance();
        if (configHelp != null) {
            for (String channalCode : Constants.channalCodes) {
                adConfigBean = configHelp.findAdConfig(channalCode);
                if (adConfigBean != null) {
                    adConfigBean.setShowedCnt(0);
                    adConfigBean.setLastShowTime(0);
                    if (adConfigCacheMap.containsKey(channalCode)) { // 保存老数据
                        cacheBean = adConfigCacheMap.get(channalCode);
                        adConfigBean.setShowedCnt(cacheBean.getShowedCnt());
                        adConfigBean.setLastShowTime(cacheBean.getLastShowTime());
                        // 遍历每个广告
                        boolean hasHistory = false;
                        for (AdSiteBean newAdSiteBean : adConfigBean.getAdSiteBeans()) {
                            for (AdSiteBean cachAdSiteBean : cacheBean.getAdSiteBeans()) {
                                if (cachAdSiteBean.getId() == newAdSiteBean.getId()) {
                                    newAdSiteBean.setLastShowTime(cachAdSiteBean.getLastShowTime());
                                    newAdSiteBean.setLastClickTime(cachAdSiteBean.getLastClickTime());
                                    hasHistory = true;
                                    Logger.e(TAG, newAdSiteBean.getAdId() + "老数据的展示时间是："
                                            + TimeTool.timeToData(newAdSiteBean.getLastShowTime(), TimeTool.DATE_FORMAT_FULL_01)
                                            + "，点击时间是："
                                            + TimeTool.timeToData(newAdSiteBean.getLastClickTime(), TimeTool.DATE_FORMAT_FULL_01)
                                    );
                                    break;
                                }
                            }
                            if (!hasHistory) {
                                newAdSiteBean.setLastShowTime(0);
                                newAdSiteBean.setLastClickTime(0);
                            }
                            hasHistory = false;
                        }
                        Logger.e(TAG, todayTime + "有数据老数据，合并");
                    }
                    adConfigCacheMap.put(channalCode, adConfigBean);
                } else if (adConfigCacheMap.containsKey(channalCode)) {
                    adConfigCacheMap.remove(channalCode);
                    Logger.e(TAG, channalCode + "====================￥￥￥渠道没有流量了￥￥￥====================");
                }
            }
        }
        preferences.edit().putString(todayTime, mGson.toJson(adConfigCacheMap)).apply();
        Logger.d(TAG, "广告配置更新后： " + mGson.toJson(adConfigCacheMap));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAdConfig(AdConfigEvent eventMessage) {
        Logger.d(TAG, "updateAdConfig: 接收到更新广告配置消息");
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                checkAdShowParams(false);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Object o) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * @param channalCode
     * @param adType      0.不过滤;1.开屏;2.横幅;3.插屏;4.信息流;5.视频
     * @return
     */
    public List<AdSiteBean> getAdChannalBeanByType(String channalCode, int adType) {
        if (!adConfigCacheMap.containsKey(channalCode) || isAdFreeTime(channalCode)) {
            Logger.e(TAG, channalCode + "没有对应的广告渠道");
            return null;
        }
        AdConfigBean adConfigBean = getAvailableAdConfig(channalCode);
        if (adConfigBean != null) {
            List<AdSiteBean> adSiteBeanList = filterEachAdTime(adConfigBean.getChannelCode(), adConfigBean.getAdSiteBeans());
            if (adSiteBeanList == null || adSiteBeanList.isEmpty()) {
                return null;
            } else {
                List<AdSiteBean> availableList = new ArrayList<>();
                for (AdSiteBean adSiteBean : adSiteBeanList) {
                    if (adSiteBean != null) {
                        if (adType == 0) {
                            availableList.add(adSiteBean);
                        } else if (adSiteBean.getAdType() == adType) {
                            availableList.add(adSiteBean);
                        }
                    }
                }
                return availableList;
            }
        } else {
            return null;
        }
    }

    public AdSiteBean getAvailiableAdSite(Activity activity, String channalCode, int adId) {
        AdConfigBean adConfigBean = getAvailableAdConfig(channalCode);
        if (adConfigBean == null) {
            Logger.e(TAG, channalCode+ "getAvailiableAdSite:没有对应的广告渠道");
            return null;
        } else {
            return getAvailiableAdSite(activity, adConfigBean, adId);
        }
    }

    public AdSiteBean getAvailiableAdSite(Activity activity, AdConfigBean adConfigBean, int id) {
        if (adConfigBean != null && adConfigBean.getAdSiteBeans().size() > 0) {
            List<AdSiteBean> availiableSiteBeans = new ArrayList<>();
            for (AdSiteBean adSiteBean : adConfigBean.getAdSiteBeans()) {
                if (adSiteBean.getId() != id) {
                    availiableSiteBeans.add(adSiteBean);
                }
            }
            if (!availiableSiteBeans.isEmpty()) {
                Logger.e(TAG, adConfigBean.getChannelCode() + ", 可用size : " + availiableSiteBeans.size());
                return filterHiehgtestPriorityAd(activity, filterEachAdTime(adConfigBean.getChannelCode(), availiableSiteBeans));
            } else {
                Logger.e(TAG, adConfigBean.getChannelCode() + ", 没有可用的广告来源");
            }
        }
        return null;
    }

    /**
     * 判断所有条件是否可以展示广告，展示则返回对应的广告源
     * @param channalCode
     * @return
     *
     */
    public AdSiteBean showAd(Activity activity, String channalCode) {
        if (TextUtils.isEmpty(channalCode) || !adConfigCacheMap.containsKey(channalCode)) {
            Logger.e(TAG, channalCode+ "不显示广告，没有对应的广告渠道");
            return null;
        }

        if (isAdFreeTime(channalCode)) {
            return null;
        }

        AdConfigBean adConfigBean = getAvailableAdConfig(channalCode);
        if (adConfigBean == null) {
            return null;
        }
        // 随机获取优先级高的广告资源
        AdSiteBean adSiteBean = filterHiehgtestPriorityAd(activity, filterEachAdTime(channalCode,
                adConfigBean.getAdSiteBeans()));
        if (adSiteBean == null ) {
            Logger.e(TAG, "不显示广告" + channalCode + "，没有获取到可用的广告");
            return null;
        }

        return adSiteBean;
    }

    public AdConfigBean getAvailableAdConfig(String channalCode) {
        AdConfigBean adConfigBean = adConfigCacheMap.get(channalCode);
        if (adConfigBean == null) {
            Logger.e(TAG, channalCode+ "不显示广告，没有对应的广告渠道");
            return null;
        }
        // 是否在展示时间段如: 1,2|9,20
        if (!TextUtils.isEmpty(adConfigBean.getShowtimeRange())) {
            if (!inShowtimeRange(adConfigBean.getChannelCode(), adConfigBean.getShowtimeRange())) {
                return null;
            }
        }
        // 展示次数, 日限为0可以无限展示
        if (adConfigBean.getDayShowLimit() != 0 && adConfigBean.getShowedCnt() >= adConfigBean.getDayShowLimit()) {
            Logger.e(TAG, "不显示广告" + channalCode + "广告日限达到最大了： " + adConfigBean.getDayShowLimit());
            return null;
        } else {
            Logger.e(TAG, channalCode + " -- 已展示次数" + adConfigBean.getShowedCnt()
                    + ", 广告日限： " + adConfigBean.getDayShowLimit());
        }
        Logger.e(TAG, channalCode + "展示间隔： " + adConfigBean.getDayShowInteval() + "秒"
                + ", 当前时间： " + TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_FULL_01) + ", 上次显示时间： "
                + TimeTool.timeToData(adConfigBean.getLastShowTime(),TimeTool.DATE_FORMAT_FULL_01));
        // 展示间隔
        if (System.currentTimeMillis() - adConfigBean.getLastShowTime() < adConfigBean.getDayShowInteval() * 1000) {
            Logger.e(TAG, "不显示广告" + channalCode + "小于展示间隔： " + adConfigBean.getDayShowInteval() + "秒");
            return null;
        }
        return adConfigBean;
    }

    /**
     * <判断该广告位是否正在免广告期间
     *  所有任务不受免广告特权和免广告时长影响
     * @param channalCode
     * @return
     */
    public boolean isAdFreeTime(String channalCode) {
        BookRecordGatherResp bookRecordGatherResp = SharePreferenceUtils.getObject(BaseApplication.context.globalContext, SharePreferenceUtils.READ_HISTORY_CACHE);
        if (bookRecordGatherResp != null && bookRecordGatherResp.getLastSec() > 0
                && !channalCode.equals(Constants.channalCodes[5]) && !channalCode.equals(Constants.channalCodes[6])
                && !channalCode.equals(Constants.channalCodes[8]) && !channalCode.equals(Constants.channalCodes[9])
                && !channalCode.equals(Constants.channalCodes[10])) {
            Logger.e(TAG, channalCode+ "免广告特权使用中，兑换卡剩余时间： " + bookRecordGatherResp.getLastSec());
            Logger.e(TAG, TAG + "特权免广告剩余时间： " + bookRecordGatherResp.getLastSec());
            return true;
        }

        // 判断是否免广告
        long freeDuration = preferences.getLong(AdConstants.CURR_FREE_TIME, 15) * TimeUtils.MINUTE_1;
        long freeStartTime = preferences.getLong(AdConstants.KEY_FREE_START_TIME, 0);
        if (System.currentTimeMillis() - freeStartTime <= freeDuration
                && !channalCode.equals(Constants.channalCodes[5]) && !channalCode.equals(Constants.channalCodes[6])
                && !channalCode.equals(Constants.channalCodes[8]) && !channalCode.equals(Constants.channalCodes[9])
                && !channalCode.equals(Constants.channalCodes[10])) {
            Logger.e(TAG, channalCode+ "免广告时间--免广告时长：" + preferences.getLong(AdConstants.CURR_FREE_TIME, 15) + "分钟");
            return true;
        }

        return false;
    }

    private boolean inShowtimeRange(String chanalCode, String showTimeRange) {
        String[] ranges = null;
        int hour = new Date().getHours();
        if (TextUtils.isEmpty(showTimeRange)) {
            return true;
        }
        if (showTimeRange.contains("|")) {
            ranges = showTimeRange.split("\\|");
            String[] items = null;
            boolean inRange = false;
            for (String item : ranges) {
                items = item.split(",");
                if (items == null || items.length != 2) {
                    continue;
                }
//                Logger.e(TAG, chanalCode + "当前时间段： " + hour + ",展示区间： " + items[0] + " : " + items[1]);
                if (hour >= StringFormat.parseInt(items[0], 0) && hour <= StringFormat.parseInt(items[1], 0)) {
                    inRange = true;
                    break;
                }
            }
            if (!inRange) {
                Logger.e(TAG, "不显示广告,不在时间段内");
            }
            return inRange;
        } else {
            ranges = showTimeRange.split(",");
//            Logger.e(TAG, showTimeRange+ "当前时间段： " + hour + ",展示区间： " + ranges[0] + " : " + ranges[1]);
            if (hour < StringFormat.parseInt(ranges[0], 0) || hour > StringFormat.parseInt(ranges[1], 0)) {
                Logger.e(TAG, chanalCode + "不显示广告" +  ",不在时间段内： " + ranges[0] + "-" + ranges[1]);
                return false;
            } else {
                return true;
            }
        }
    }

    private List<AdSiteBean> filterEachAdTime(String channalCode, List<AdSiteBean> adSiteBeanList) {
        if (adSiteBeanList == null || adSiteBeanList.isEmpty()) {
            return null;
        }
        List<AdSiteBean> availiableList = new ArrayList<>();
        long currTime = System.currentTimeMillis(); // 单位：秒
        for (AdSiteBean adSiteBean : adSiteBeanList) {
            int isAvailable = 0;
            if (currTime - adSiteBean.getLastShowTime() > adSiteBean.getShowInteval() * 1000) {
                ++isAvailable;
            } else {
                Logger.e(TAG, adSiteBean.getAdId()  + "的展示间隔：" + adSiteBean.getShowInteval()
                        + "秒，广告展示时间不符： 上次展示时间是 "
                        + TimeTool.timeToData(adSiteBean.getLastShowTime(),TimeTool.DATE_FORMAT_FULL_01)
                        + "当前时间是：" + TimeTool.timeToData(currTime,TimeTool.DATE_FORMAT_FULL_01));
            }
            if (currTime - adSiteBean.getLastClickTime() > adSiteBean.getClickInteval() * 1000) {
                ++isAvailable;
            } else {
                Logger.e(TAG, adSiteBean.getAdId() + "的点击间隔：" + adSiteBean.getClickInteval()
                        + "秒，广告点击时间不符： 上次点击时间是 "
                        + TimeTool.timeToData(adSiteBean.getLastClickTime(),TimeTool.DATE_FORMAT_FULL_01)
                        + "当前时间是：" + TimeTool.timeToData(currTime,TimeTool.DATE_FORMAT_FULL_01));
            }
            if (inShowtimeRange(channalCode, adSiteBean.getShowtimeRange())) {
                ++isAvailable;
            } else {
                Logger.e(TAG, adSiteBean.getAdId() + "，广告展示时间段不符： 当前时间 = " + new Date().getHours()
                        + ", 展示时间段： " + adSiteBean.getShowtimeRange());
            }
            if (isAvailable == 3) {
                availiableList.add(adSiteBean);
            }
        }
        Logger.e(TAG, channalCode + "，获取到有效广告个数：" + availiableList.size());
        return availiableList;
    }

    /**
     *
     * @param activity
     * @param adSites
     * @return
     */
    private AdSiteBean filterHiehgtestPriorityAd(Activity activity, List<AdSiteBean> adSites) {
        if (adSites != null && !adSites.isEmpty()) {
            // 广点通没有权限时，不使用广点通广告源
            List<AdSiteBean> availiableList = new ArrayList<>();
            boolean hasPermission = PermissionUtil.requestPermissions(activity);
            for (AdSiteBean adSiteBean : adSites) {
                if (adSiteBean.getOrigin() != AdConstants.Source.GDT || hasPermission ) {
                    availiableList.add(adSiteBean);
                }
            }
            if (availiableList.size() == 1) {
                return availiableList.get(0);
            } else {
                return RandomUtil.getRandomGroupAD(availiableList);
            }
        } else {
            return null;
        }
    }

    public IAdView getAdView(Activity activity, String channalCode, AdSiteBean adSiteBean) {
        if (adSiteBean == null) {
            return null;
        }
        if (adSiteBean.getLinkType() == AdConstants.LinkType.SDK) {
            switch (adSiteBean.getOrigin()) {
                case AdConstants.Source.GDT:
                    switch (adSiteBean.getAdType()) {
                        case AdConstants.Type.LAUNCHING:
                            return new GDTLauncher(activity, adSiteBean, statisticListener);
                        case AdConstants.Type.BANNER:
                            return new GDTBanner2(activity, adSiteBean, statisticListener);
                        case AdConstants.Type.INFORMATION_FLOW:
                            return new GDTInfoFlowModel(activity, adSiteBean, statisticListener);
                        case AdConstants.Type.VIDEO:
                            return new GDTRewardVideo(activity, adSiteBean, statisticListener);
                    }
                    break;
                case AdConstants.Source.CSJ:
                    switch (adSiteBean.getAdType()) {
                        case AdConstants.Type.LAUNCHING:
                            return new CSJLauncher(activity, adSiteBean, statisticListener);
                        case AdConstants.Type.BANNER:
                            return new CSJBannerModel(activity, adSiteBean, statisticListener);
                        case AdConstants.Type.INFORMATION_FLOW:
                            //判断渲染方式.
                            if (adSiteBean.getRenderType() == AdConstants.RenderType.TEMPLATE)
                            {
                                //穿山甲信息流模版广告.
                                return new CSJExpressNative(activity, adSiteBean, statisticListener);
                            } else
                            {
                                //穿山甲信息流自渲染广告.
                                return new CSJInfoFlowNative(activity, adSiteBean, statisticListener);
                            }
                        case AdConstants.Type.VIDEO:
                            return new CSJRewardVideo(activity, adSiteBean, statisticListener);
                    }
                    break;
                case AdConstants.Source.BD:
                    break;
            }
        } else if (!TextUtils.isEmpty(channalCode) && adSiteBean.getLinkType() == AdConstants.LinkType.URL) {
            if (channalCode.equals(Constants.channalCodes[2])) { // 信息流
                return new UrlInfoFlowAdView(activity, adSiteBean, statisticListener);
            } else if (channalCode.equals(Constants.channalCodes[1]) || channalCode.equals(Constants.channalCodes[12])) { // 横幅
                return new UrlBannerAdView(activity, adSiteBean, statisticListener);
            } else if (channalCode.equals(Constants.channalCodes[7])) {
                return  new UrlFlowBallAdView(activity, adSiteBean, statisticListener);
            }
        }

        return null;
    }

    private void updateClickTime(AdSiteBean adSiteBean) {
        AdConfigBean adConfigBean = adConfigCacheMap.get(adSiteBean.getChannelCode());
        if (adConfigBean != null) {
            for (AdSiteBean updadteBean : adConfigBean.getAdSiteBeans()) {
                if (updadteBean.getId() == adSiteBean.getId()) {
                    updadteBean.setLastClickTime(System.currentTimeMillis());
                    Logger.e(TAG, adConfigBean.getChannelCode() + "====== 广告：" + updadteBean.getAdId() + "的点击时间是 "
                            + TimeTool.timeToData(updadteBean.getLastClickTime(),TimeTool.DATE_FORMAT_FULL_01));
                    break;
                }
            }
            adConfigCacheMap.put(adSiteBean.getChannelCode(), adConfigBean);
            preferences.edit().putString(todayTime, mGson.toJson(adConfigCacheMap)).apply();
            Logger.e(TAG, adSiteBean.getChannelCode() + "========广告位点击时间更新： "
                    + ", 已显示次数 ： " + adConfigBean.getShowedCnt());
        }
    }

    public void updateShowNum(AdSiteBean adSiteBean) {
        AdConfigBean adConfigBean = adConfigCacheMap.get(adSiteBean.getChannelCode());
        if (adConfigBean != null) {
            adConfigBean.setShowedCnt(adConfigBean.getShowedCnt() + 1);
            long currTime = System.currentTimeMillis();
            adConfigBean.setLastShowTime(currTime);
            for (AdSiteBean updadteBean : adConfigBean.getAdSiteBeans()) {
                if (updadteBean.getId() == adSiteBean.getId()) {
                    updadteBean.setLastShowTime(currTime);
                    Logger.e(TAG, adConfigBean.getChannelCode()
                            + "=========广告：" + updadteBean.getAdId() + "的展示时间是 "
                            + TimeTool.timeToData(updadteBean.getLastShowTime(),TimeTool.DATE_FORMAT_FULL_01));
                    break;
                }
            }
            adConfigCacheMap.put(adSiteBean.getChannelCode(), adConfigBean);
            preferences.edit().putString(todayTime, mGson.toJson(adConfigCacheMap)).apply();
            Logger.e(TAG, adSiteBean.getChannelCode() + "=====更新次数和展示时间, 已显示次数 ： " + adConfigBean.getShowedCnt());
        }
    }

    private AdCallbackListener statisticListener = new AdCallbackListener() {
        @Override
        public void pull(AdSiteBean adSiteBean) {
            if (PhoneUtil.isNetworkAvailable(mContext)) {
                AdHttpUtil.pull(adSiteBean);
            } else {
                Logger.e("ad#http", "没有网络，取消PULL上传: " + adSiteBean.toString());
            }
        }

        @Override
        public void pullFailed(AdSiteBean adSiteBean, String code, String errorMsg) {
            if (PhoneUtil.isNetworkAvailable(mContext)) {
                AdHttpUtil.pullFail(adSiteBean);
                AdHttpUtil.uploadErrorMsg(adSiteBean, code, errorMsg);
            } else {
                Logger.e("ad#http", "没有网络，取消pullFailed上传: " + adSiteBean.toString());
            }
        }

        @Override
        public void onShow(AdSiteBean adSiteBean) {
            if (PhoneUtil.isNetworkAvailable(mContext)) {
                AdHttpUtil.pullSuccess(adSiteBean);
                AdHttpUtil.showSuccess(adSiteBean);
            } else {
                Logger.e("ad#http", "没有网络，取消onShow上传: " + adSiteBean.toString());
            }
            if (adSiteBean != null) {
            // 更新广告位次数和显示时间
                updateShowNum(adSiteBean);
            }
        }

        @Override
        public void onClick(AdSiteBean adSiteBean) {
            if (PhoneUtil.isNetworkAvailable(mContext)) {
                AdHttpUtil.click(adSiteBean);
            } else {
                Logger.e("ad#http", "没有网络，取消onClick上传: " + adSiteBean.toString());
            }
            updateClickTime(adSiteBean);
        }

        @Override
        public void onError(AdSiteBean adSiteBean, String code, String errorMsg) {
            if (PhoneUtil.isNetworkAvailable(mContext)) {
                AdHttpUtil.showFail(adSiteBean);
                AdHttpUtil.uploadErrorMsg(adSiteBean, code, errorMsg);
            } else {
                Logger.e("ad#http", "没有网络，取消onError上传: " + adSiteBean.toString());
            }
        }

        @Override
        public void onDismiss(AdSiteBean adSiteBean) {
        }

        @Override
        public void onAdTick(long time) {
        }
    };

    public void destroy() {
        adConfigCacheMap.clear();
        EventBus.getDefault().unregister(this);
    }
}
