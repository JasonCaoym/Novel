package com.duoyue.lib.base.app.http;

import android.util.Log;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.domain.DomainManager;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.location.BDLocationMgr;
import com.duoyue.lib.base.location.LocationModel;
import com.zydm.base.tools.PhoneStatusManager;

import java.lang.reflect.Field;
import java.util.Map;

class FillTool
{
    private static final String TAG = "Base#FillTool";

    static String getUrl(DomainType type, String action)
    {
        switch (type)
        {
            case UPGRADE:
                return DomainManager.getInstance().getUpgradeDomain() + action;
            case BUSINESS:
                return DomainManager.getInstance().getBusinessDomain() + action;
            case ERROR:
                //错误日志接口.
                return Constants.DOMAINM_ERROR + action;
        }
        return null;
    }

    static void addHeader(Map<String, String> header, Field field, Object obj)
    {
        try
        {
            field.setAccessible(true);
            String value = (String) field.get(obj);
            if (value != null)
            {
                int index = value.indexOf(":");
                header.put(value.substring(0, index).trim(), value.substring(index + 1).trim());
            }
        } catch (Throwable throwable)
        {
            Log.e(TAG, "addHeader: ", throwable);
        }
    }

    static void addUserAgent(Map<String, String> header)
    {
        header.put("User-Agent", PhoneUtil.getUserAgent());
    }

    static void addToken(Map<String, String> header)
    {
        UserInfo info = UserManager.getInstance().getUserInfo();
        if (info != null)
        {
            header.put("token", info.token);
        }
    }

    static void setMid(Field field, Object obj)
    {
        try
        {
            setField(field, obj, UserManager.getInstance().getMid());
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setMid: failed!", throwable);
        }
    }

    static void setUid(Field field, Object obj)
    {
        try
        {
            UserInfo info = UserManager.getInstance().getUserInfo();
            if (info != null)
            {
                setField(field, obj, info.uid);
            }
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setUid: failed!", throwable);
        }
    }

    static void setAppId(Field field, Object obj)
    {
        try
        {
            setField(field, obj, Constants.APP_ID);
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setAppId: failed!", throwable);
        }
    }

    static void setChannelCode(Field field, Object obj)
    {
        try
        {
            setField(field, obj, PhoneStatusManager.getInstance().getAppChannel());
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setChannelCode: failed!", throwable);
        }
    }

    static void setVersion(Field field, Object obj)
    {
        try
        {
            setField(field, obj, PhoneStatusManager.getInstance().getAppVersionName());
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setVersion: failed!", throwable);
        }
    }

    static void setTimestamp(Field field, Object obj)
    {
        try
        {
            setField(field, obj, System.currentTimeMillis());
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setTimestamp: failed!", throwable);
        }
    }

    static void setImei(Field field, Object obj)
    {
        try
        {
            setField(field, obj, PhoneUtil.getIMEI(BaseContext.getContext()));
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setImei: failed!", throwable);
        }
    }

    static void setImsi(Field field, Object obj)
    {
        try
        {
            setField(field, obj, PhoneUtil.getIMSI(BaseContext.getContext()));
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setImsi: failed!", throwable);
        }
    }

    static void setMeid(Field field, Object obj)
    {
        try
        {
            setField(field, obj, PhoneUtil.getMEID(BaseContext.getContext()));
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setMeid: failed!", throwable);
        }
    }

    static void setAndroidId(Field field, Object obj)
    {
        try
        {
            setField(field, obj, PhoneUtil.getAndroidID(BaseContext.getContext()));
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setAndroidId: failed!", throwable);
        }
    }

    /**
     * 设置省份.
     * @param field
     * @param obj
     */
    static void setProvince(Field field, Object obj)
    {
        try
        {
            //获取位置信息.
            LocationModel locationModel = BDLocationMgr.getLocation();
            setField(field, obj, locationModel != null ? locationModel.getProvince() : "");
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setProvince: failed!", throwable);
        }
    }

    /**
     * 设置城市.
     * @param field
     * @param obj
     */
    static void setCity(Field field, Object obj)
    {
        try
        {
            //获取位置信息.
            LocationModel locationModel = BDLocationMgr.getLocation();
            setField(field, obj, locationModel != null ? locationModel.getCity() : "");
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setCity: failed!", throwable);
        }
    }

    /**
     * 设置纬度.
     * @param field
     * @param obj
     */
    static void setLatitude(Field field, Object obj)
    {
        try
        {
            //获取位置信息.
            LocationModel locationModel = BDLocationMgr.getLocation();
            setField(field, obj, locationModel != null ? locationModel.getmLatitude() : "");
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setCity: failed!", throwable);
        }
    }

    /**
     * 设置经度.
     * @param field
     * @param obj
     */
    static void setLongitude(Field field, Object obj)
    {
        try
        {
            //获取位置信息.
            LocationModel locationModel = BDLocationMgr.getLocation();
            setField(field, obj, locationModel != null ? locationModel.getmLongitude() : 0);
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setCity: failed!", throwable);
        }
    }

    /**
     * 设置城市.
     * @param field
     * @param obj
     */
    static void setWiFis(Field field, Object obj)
    {
        try
        {
            setField(field, obj, BDLocationMgr.getWiFis());
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setWiFis: failed!", throwable);
        }
    }

    /**
     * 设置网络类型
     * @param field
     * @param obj
     */
    public static void setNetwork(Field field, Object obj) {
        try
        {
            setField(field, obj, PhoneUtil.getNetwork(BaseContext.getContext()));
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setNetwork: failed!", throwable);
        }
    }

    /**
     * 设置运营商
     * @param field
     * @param obj
     */
    public static void setOperator(Field field, Object obj) {
        try
        {
            setField(field, obj, PhoneUtil.getProvider(BaseContext.getContext()));
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setOperator: failed!", throwable);
        }
    }

    /**
     * 设置经度.
     * @param field
     * @param obj
     */
    static void setProtocolCode(Field field, Object obj)
    {
        try
        {
            setField(field, obj, Constants.PROTOCOL_CODE);
        } catch (Throwable throwable)
        {
            Log.e(TAG, "setProtocolCode: failed!", throwable);
        }
    }

    private static void setField(Field field, Object obj, Object value) throws Throwable
    {
        if (value != null)
        {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }

}
