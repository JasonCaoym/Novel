package com.duoyue.lib.base.app.user;

import android.Manifest;
import android.text.format.DateUtils;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.cache.Cache;
import com.duoyue.lib.base.cache.NumberParser;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import java.io.File;

/**
 * 手机设备信息
 * @author caoym
 * @data 2019/4/28  20:17
 */
public class MobileInfoPresenter
{
    /**
     * 日志Tag
     */
    private static final String TAG = "App#MobileInfoPresenter";

    /**
     * 当前类对象.
     */
    private static MobileInfoPresenter sInstance;

    /**
     * 补充手机信息接口调用时间
     */
    private RamCache<Long> mSupplyMobileTimeCache;

    private MobileInfoPresenter()
    {
        try
        {
            //调用补充手机信息接口时间.
            mSupplyMobileTimeCache = new RamCache(new File(BaseContext.getContext().getFilesDir(), "novel/app/upl_supply_mobile_info_time.dat"), new NumberParser());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "MobileInfoPresenter: {}", throwable);
        }
    }

    /**
     * 创建当前类单例对象.
     */
    private static synchronized void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (MobileInfoPresenter.class)
            {
                if (sInstance == null)
                {
                    sInstance = new MobileInfoPresenter();
                }
            }
        }
    }

    /**
     * 上报手机设备信息.
     */
    public static void uploadMobileInfo()
    {
        try
        {
            //判断网络是否可用.
            if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
            {
                //网络不可用.
                return;
            }
            //判断是否已上报过手机信息.
            final Cache<Long> uploadMobileInfoTimeCache = getCache();
            if (uploadMobileInfoTimeCache != null && uploadMobileInfoTimeCache.get(0L) > 0)
            {
                //已上报过, 不需要再上报.
                return;
            }
            new JsonPost.AsyncPost<Object>().setRequest(new MobileInfoRequest()).setResponseType(Object.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<Object>>(){
                @Override
                protected void onStart()
                {
                    super.onStart();
                }

                @Override
                public void onComplete()
                {
                    Logger.i(TAG, "uploadMobileInfo: onComplete: ");
                }

                @Override
                public void onNext(JsonResponse<Object> response)
                {
                    Logger.i(TAG, "uploadMobileInfo: onNext: {}", response);
                    if (response.status == 1)
                    {
                        //上报成功.
                        if (uploadMobileInfoTimeCache != null)
                        {
                            uploadMobileInfoTimeCache.set(TimeTool.currentTimeMillis());
                        } else
                        {
                            Cache<Long> cache = getCache();
                            if (cache != null)
                            {
                                cache.set(TimeTool.currentTimeMillis());
                            }
                        }
                    }
                }

                @Override
                public void onError(Throwable throwable)
                {
                    Logger.e(TAG, "uploadMobileInfo: onError: {}", throwable);
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "uploadMobileInfo: {}", throwable);
        }
    }

    /**
     * 上报补充设备信息(补充IMSI、IMEI信息接口[授权通过后调用]).
     */
    public static void uploadSupplyMobileInfo()
    {
        try
        {
            //判断用户是否已登录.
            if (UserManager.getInstance().getUserInfo() == null)
            {
                //用户未登录.
                return;
            }
            //判断网络是否可用.
            if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext()))
            {
                //网络不可用.
                return;
            }
            //判断是否已获取到手机权限.
            if (!PhoneUtil.checkPermission(BaseContext.getContext(), Manifest.permission.READ_PHONE_STATE))
            {
                //未授权读取设备信息权限, 不进行上报.
                return;
            }
            //创建当前类对象.
            createInstance();
            //判断是否已上报.
            if (sInstance.mSupplyMobileTimeCache != null && sInstance.mSupplyMobileTimeCache.get(0L) > 0)
            {
                //已上报过, 不需要再次上报.
                return;
            }
            new JsonPost.AsyncPost<Object>().setRequest(new SupplyDeviceRequest()).setResponseType(Object.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<Object>>(){
                @Override
                protected void onStart()
                {
                    super.onStart();
                }

                @Override
                public void onComplete()
                {
                    Logger.i(TAG, "uploadSupplyMobileInfo: onComplete: ");
                }

                @Override
                public void onNext(JsonResponse<Object> response)
                {
                    Logger.i(TAG, "uploadSupplyMobileInfo: onNext: {}", response);
                    try
                    {
                        if (response != null && response.status == 1)
                        {
                            //上报成功.
                            if (sInstance != null && sInstance.mSupplyMobileTimeCache != null)
                            {
                                //记录上报时间.
                                sInstance.mSupplyMobileTimeCache.set(TimeTool.currentTimeMillis());
                            }
                        }
                    } catch (Throwable throwable)
                    {
                        Logger.e(TAG, "uploadSupplyMobileInfo: onNext: {}, {}", response, throwable);
                    }
                }

                @Override
                public void onError(Throwable throwable)
                {
                    Logger.e(TAG, "uploadSupplyMobileInfo: onError: {}", throwable);
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "uploadSupplyMobileInfo: {}", throwable);
        }
    }

    /**
     * 获取缓存对象.
     * @return
     */
    private static Cache<Long> getCache()
    {
        try
        {
            return new Cache(new File(BaseContext.getContext().getFilesDir(), "novel/app/upl_mobile_info_time.dat"), new NumberParser());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "getCache: {}", throwable);
            return null;
        }
    }
}
