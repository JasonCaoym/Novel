package com.duoyue.mod.ad;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import com.duoyue.mod.ad.bean.AdOriginConfigBean;
import com.duoyue.mod.ad.listener.ADListener;
import com.duoyue.mod.ad.listener.AdCallback;
import com.duoyue.mod.ad.listener.SplashAdListener;
import com.duoyue.mod.ad.utils.AdConstants;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;

import java.util.*;

public class AdSourceProxy implements IAdSource {

    public static final long REWORD_VIDEO_TIMEOUT = 15000;

    private Activity mActivity;
    private HashSet<ADListener> mListenerSet = new HashSet();
    private HashMap<Integer, IAdSource> sourceMap = new HashMap();
    private AdOriginConfigBean originBean;

    protected AdSourceProxy(Activity activity, AdOriginConfigBean originBean) {
        mActivity = activity;
        this.originBean = originBean;
    }

    private IAdSource getSource(int origin) {
        if (sourceMap.get(origin) != null) {
            return sourceMap.get(origin);
        }
        IAdSource source = null;
        switch (origin) {
            case AdConstants.Source.GDT:
                source = AdManager.getInstance().getGdtAdPlatform().createSource(mActivity);
                break;
            case AdConstants.Source.CSJ:
                source = AdManager.getInstance().getCsjAdPlatform().createSource(mActivity);
                break;
            case AdConstants.Source.BD:
                source = AdManager.getInstance().getBdAdPlatform().createSource(mActivity);
                break;
        }
        Iterator<ADListener> iterator = mListenerSet.iterator();
        while (iterator.hasNext()) {
            source.addListener(iterator.next());
        }
        sourceMap.put(origin, source);
        return source;
    }

    @Override
    public void addListener(ADListener adListener) {
        mListenerSet.add(adListener);
        Collection<IAdSource> values = sourceMap.values();
        for (IAdSource source : values) {
            source.addListener(adListener);
        }
    }

    @Override
    public void loadBannerAd(AdOriginConfigBean adParam, ViewGroup containerView, ADListener loadListener) {
        if (originBean != null) {
            getSource(originBean.getOrigin()).loadBannerAd(originBean, containerView, loadListener);
        }
    }

    @Override
    public void loadSplashAd(AdOriginConfigBean adParam, ViewGroup containerView, View skipView, SplashAdListener bannerListener) {
        if (originBean != null) {
            getSource(originBean.getOrigin()).loadSplashAd(originBean, containerView, skipView, bannerListener);
        }
    }

    @Override
    public void loadInteractionAd(AdOriginConfigBean adParam, ADListener loadListener) {
        if (originBean != null) {
            getSource(originBean.getOrigin()).loadInteractionAd(originBean, loadListener);
        }
    }

    @Override
    public void loadRewardVideoAD(AdOriginConfigBean adParam, ADListener adListener) {
        if (originBean != null) {
            getSource(originBean.getOrigin()).loadRewardVideoAD(originBean, adListener);
        }
    }

    @Override
    public void loadCommonAd(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height, AdCallback callback) {
        if (originBean != null) {
            getSource(originBean.getOrigin()).loadCommonAd(originBean, containerView, width, height, callback);
        }
    }

    @Override
    public void loadCommonAdWithVideo(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height,
                                      boolean showBigImg, View.OnClickListener clickListener, ADListener adListener) {
        if (originBean != null) {
            getSource(originBean.getOrigin()).loadCommonAdWithVideo(originBean, containerView, width, height,
                    showBigImg, clickListener, adListener);
        }
    }

    @Override
    public void loadReadNativeAd(AdOriginConfigBean adParam, ViewGroup containerView, int width, int height, ADListener adListener) {
        if (originBean != null) {
            getSource(originBean.getOrigin()).loadReadNativeAd(originBean, containerView, width, height, adListener);
        }
    }

    @Override
    public Single<ArrayList<?>> loadListAd(AdOriginConfigBean adParam, int width, int height) {
        if (originBean != null) {
            return getSource(originBean.getOrigin()).loadListAd(originBean, width, height);
        } else {
            return Single.fromObservable(new ObservableSource<ArrayList<?>>() {
                @Override
                public void subscribe(final Observer<? super ArrayList<?>> observer) {
                    observer.onNext(new ArrayList<Object>());
                    observer.onComplete();
                }
            });
        }
    }
}
