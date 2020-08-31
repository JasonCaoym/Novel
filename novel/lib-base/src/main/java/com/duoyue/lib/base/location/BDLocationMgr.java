package com.duoyue.lib.base.location;

import android.Manifest;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.cache.Cache;
import com.duoyue.lib.base.cache.StringParser;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import org.json.JSONObject;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * 百度定位管理类
 * @author caoym
 * @data 2019/5/6  15:55
 */
public class BDLocationMgr extends BDAbstractLocationListener {
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#BDLocationMgr";

    /**
     * 当前类对象.
     */
    private static BDLocationMgr sInstance;

    /**
     * LocationClient
     */
    private LocationClient mLocationClient;

    /**
     * 阅读品味信息.
     */
    private Cache<String> mLocationCache;

    /**
     * 位置信息对象.
     */
    private LocationModel mLocationModel;

    /**
     * WiFi名称列表.
     */
    private String mWiFis;

    private BDLocationMgr()
    {
        try {
            //阅读品味信息.
            mLocationCache = new Cache(new File(BaseContext.getContext().getFilesDir(), "novel/app/location.dat"), new StringParser());
            String location = mLocationCache.get("");
            //解析出位置信息.
            mLocationModel = LocationModel.parseJSONObject(StringFormat.isEmpty(location) ? null : new JSONObject(location));
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "BDLocationMgr: {}", throwable);
        }
        //更新WiFi列表.
        updateWiFis();
    }

    /**
     * 创建当前类单例对象.
     */
    private synchronized static void createInstance()
    {
        if (sInstance == null)
        {
            synchronized (BDLocationMgr.class)
            {
                if (sInstance == null)
                {
                    sInstance = new BDLocationMgr();
                }
            }
        }
    }

    /**
     * 获取位置信息.
     * @return
     */
    public static LocationModel getLocation()
    {
        //创建当前类对象.
        createInstance();
        return sInstance.mLocationModel;
    }

    /**
     * 获取WiFi列表.
     * @return
     */
    public static String getWiFis()
    {
        //创建当前类对象.
        createInstance();
        if (StringFormat.isEmpty(sInstance.mWiFis))
        {
            //调用更新WiFi接口.
            sInstance.updateWiFis();
        }
        return sInstance.mWiFis;
    }

    /**
     * 启动百度定位.
     */
    public synchronized static void startLocation()
    {
        //判断是否有定位权限.
        if (!PhoneUtil.checkPermission(BaseContext.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            //未授权定位权限, 不需要开启定位功能.
            return;
        }
        try {
            //创建当前类对象.
            createInstance();
            if (sInstance.mLocationClient == null)
            {
                //声明LocationClient类
                sInstance.mLocationClient = new LocationClient(BaseContext.getContext());
                //注册监听函数
                sInstance.mLocationClient.registerLocationListener(sInstance);
                //配置定位SDK参数
                LocationClientOption option = new LocationClientOption();
                //可选, 设置定位模式, 默认高精度(Hight_Accuracy:高精度;Battery_Saving:低功耗;Device_Sensors:仅使用设备)
                option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                //可选, 设置返回经纬度坐标类型, 默认GCJ02(GCJ02:国测局坐标;BD09ll:百度经纬度坐标;BD09:百度墨卡托坐标;海外地区定位, 无需设置坐标类型, 统一返回WGS84类型坐标)
                option.setCoorType("BD09ll");
                //可选, 设置发起定位请求的间隔, int类型, 单位ms(如果设置为0, 则代表单次定位, 即仅定位一次, 默认为0;如果设置非0, 需设置1000ms以上才有效)
                option.setScanSpan(30  * 60 * 1000);
                //可选, 设置是否使用GPS, 默认false(使用高精度和仅用设备两种定位模式的, 参数必须设置为true)
                option.setOpenGps(true);
                //可选, 设置是否当GPS有效时按照1S/1次频率输出GPS结果, 默认false
                option.setLocationNotify(true);
                //可选, 定位SDK内部是一个service, 并放到了独立进程.(设置是否在stop的时候杀死这个进程, 默认(建议)不杀死, 即setIgnoreKillProcess(true))
                option.setIgnoreKillProcess(true);
                //可选, 设置是否收集Crash信息，默认收集，即参数为false
                option.SetIgnoreCacheException(false);
                //可选, V7.2 版本新增能力(如果设置了该接口, 首次启动定位时, 会先判断当前Wi-Fi是否超出有效期, 若超出有效期, 会先重新扫描Wi-Fi, 然后定位)
                //option.setWifiCacheTimeOut(5 * 60 * 1000);
                //可选, 设置是否需要过滤GPS仿真结果, 默认需要, 即参数为false
                option.setEnableSimulateGps(false);
                //可选, 是否需要地址信息, 默认为不需要, 即参数为false
                option.setIsNeedAddress(true);
                //设置定位SDK参数.
                sInstance.mLocationClient.setLocOption(option);
            }
            //发起定位请求.
            sInstance.mLocationClient.start();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "startLocation: {}", throwable);
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation)
    {
        try
        {
            //获取纬度信息
        double latitude = bdLocation.getLatitude();
//        //获取经度信息
        double longitude = bdLocation.getLongitude();
//        //获取定位精度, 默认值为0.0f
//        float radius = bdLocation.getRadius();
//        //获取经纬度坐标类型, 以LocationClientOption中设置过的坐标类型为准.
//        String coorType = bdLocation.getCoorType();
//        //获取定位类型、定位错误返回码, 具体信息可参照类参考中BDLocation类中的说明.
//        int errorCode = bdLocation.getLocType();
//        //获取详细地址信息
//        String addr = bdLocation.getAddrStr();
//        //获取国家
//        String country = bdLocation.getCountry();
//        //获取省份
//        String province = bdLocation.getProvince();
//        //获取城市
//        String city = bdLocation.getCity();
//        //获取区县
//        String district = bdLocation.getDistrict();
//        //获取街道信息
//        String street = bdLocation.getStreet();
            if (mLocationModel == null) {
                mLocationModel = new LocationModel();
            }
            //设置省份
            mLocationModel.setProvince(bdLocation.getProvince());
            mLocationModel.setmLatitude(latitude);
            mLocationModel.setmLongitude(longitude);
            //设置城市
            mLocationModel.setCity(bdLocation.getCity());
            //生成JSON保存.
            mLocationCache.set(StringFormat.toString(mLocationModel.toJSONObject()));
            Logger.i(TAG, "onReceiveLocation: {}, {}", mLocationModel.getProvince(), mLocationModel.getCity());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "onReceiveLocation: {}, {}", bdLocation, throwable);
        }
        //更新WiFi热点列表.
        updateWiFis();
    }

    /**
     * 更新WiFi列表.
     */
    private synchronized void updateWiFis()
    {
        try
        {
            StringBuffer wifiBuffer = new StringBuffer();
            //获取WiFi列表.
            List<String> wifiList = PhoneUtil.getWifiListInfo(BaseContext.getContext());
            if (!StringFormat.isEmpty(wifiList))
            {
                for (String wifi : wifiList)
                {
                    if (wifiBuffer.length() > 0)
                    {
                        wifiBuffer.append(",");
                    }
                    wifiBuffer.append(wifi);
                }
            }
            mWiFis = wifiBuffer.toString();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "updateWiFis: {}", throwable);
        }
    }
}
