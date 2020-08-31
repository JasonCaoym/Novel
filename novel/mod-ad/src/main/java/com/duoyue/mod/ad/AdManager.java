package com.duoyue.mod.ad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdOriginConfigBean;
import com.duoyue.mod.ad.bean.AdPositionConfigBean;
import com.duoyue.mod.ad.bean.AdShowParamsBean;
import com.duoyue.mod.ad.listener.ADListener;
import com.duoyue.mod.ad.platform.baidu.BaiDuAdPlatform;
import com.duoyue.mod.ad.platform.csj.CSJAdPlatform;
import com.duoyue.mod.ad.platform.gdt.GDTAdPlatform;
import com.duoyue.mod.ad.utils.AdConstants;
import com.google.gson.Gson;
import com.zydm.base.utils.TimeUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdManager {
    private static final String TAG = "ad#AdManager";
    private static final String[] AD_GRADE = {"A", "B", "C", "D", "E", "F"};
    private Context mContext;
    private GDTAdPlatform gdtAdPlatform;
    private CSJAdPlatform csjAdPlatform;
    private BaiDuAdPlatform bdAdPlatform;
    private HashMap<Integer, AdPositionConfigBean> posMap = new HashMap();
    private HashMap<Integer, AdShowParamsBean> showNumMap = new HashMap<>();
    private CopyOnWriteArrayList<AdOriginConfigBean> originList = new CopyOnWriteArrayList<>();
    private SharedPreferences preferences;
    private long freeDuration;
    private Random random = new Random();
    private AdOriginConfigBean currOriginBean;
    private Gson mGson = new Gson();

    private AdManager() {
        EventBus.getDefault().register(this);
    }

    private static class Inner {
        static final AdManager INSTANCE = new AdManager();
    }

    public static AdManager getInstance() {
        return Inner.INSTANCE;
    }


    public synchronized GDTAdPlatform getGdtAdPlatform() {
        if (gdtAdPlatform == null) {
            gdtAdPlatform = new GDTAdPlatform();
            //调用初始化方法.
            gdtAdPlatform.init(mContext, getAdAppId(AdConstants.Source.GDT));
        }
        return gdtAdPlatform;
    }

    public synchronized CSJAdPlatform getCsjAdPlatform() {
        if (csjAdPlatform == null) {
            csjAdPlatform = new CSJAdPlatform();
            //调用初始化方法.
            csjAdPlatform.init(mContext, getAdAppId(AdConstants.Source.CSJ));
        }
        return csjAdPlatform;
    }

    public synchronized BaiDuAdPlatform getBdAdPlatform() {
        if (bdAdPlatform == null) {
            bdAdPlatform = new BaiDuAdPlatform();
            //调用初始化方法.
            bdAdPlatform.init(mContext, getAdAppId(AdConstants.Source.BD));
        }
        return bdAdPlatform;
    }

    public void initPlatform(Context context) {
        mContext = context;
        preferences = context.getSharedPreferences(AdConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        // 获取免广告时长
        freeDuration = 15 * TimeUtils.MINUTE_1;
    }

    /**
     * 根据SDK类型, 获取广告AppId.
     *
     * @param sdkSource SDK来源(1:GDT;2:穿山甲;3:百度)
     * @return
     */
    private String getAdAppId(int sdkSource) {
        if (StringFormat.isEmpty(originList)) {
            return "";
        }
        for (AdOriginConfigBean origin : originList) {
            if (sdkSource == origin.getOrigin()) {
                return origin.getAdAppId();
            }
        }
        return "";
    }

    /**
     * 必须先调用showAd方法，否则无法显示
     *
     * @param activity
     * @return
     */
    public IAdSource createAdSource(Activity activity) {
        // currOriginBean在showAd方法中产生
        IAdSource adSource = new AdSourceProxy(activity, currOriginBean);
        adSource.addListener(statisticListener);
        return adSource;
    }

    private void checkAdShowParams(Context context, List<AdPositionConfigBean> positionBeans) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAdConfig(AdEventMessage eventMessage) {
    }


    private ADListener statisticListener = new ADListener() {
        @Override
        public void pull(AdOriginConfigBean originBean) {
        }

        @Override
        public void pullFailed(AdOriginConfigBean originBean) {
        }

        @Override
        public void onShow(AdOriginConfigBean originBean) {
        }

        @Override
        public void onClick(AdOriginConfigBean originBean) {
        }

        @Override
        public void onError(AdOriginConfigBean originBean, String msg) {
        }

        @Override
        public void onDismiss(AdOriginConfigBean originBean) {
        }
    };

    private void updateShowNum(int adSite, int showNum) {

    }

    /**
     * 广告位是否显示广告
     *
     * @param adSite : 广告位 {@link com.duoyue.mod.ad.utils.AdConstants.Position}
     * @return
     */
    public boolean showAd(int adSite) {
        return false;
    }

    private boolean checkAndGetOrigin(AdPositionConfigBean positionBean) {
        return false;
    }

    public AdOriginConfigBean getCurrOriginBean() {
        return currOriginBean;
    }

    /**
     * 根据广告源个数产生一个随机数下标
     *
     * @param size
     * @return
     */
    private int genRandmIndex(int size) {
        int value = random.nextInt(size);
        Logger.d(TAG, "size: " + size + ", genRandmIndex: " + value);
        return value;
    }

    public void destroy() {
        posMap.clear();
        originList.clear();
        EventBus.getDefault().unregister(this);
    }

}
