package com.duoyue.lib.base.location;

import com.duoyue.lib.base.log.Logger;
import org.json.JSONObject;

/**
 * 位置信息对象
 * @author caoym
 * @data 2019/5/6  18:00
 */
public class LocationModel
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#LocationModel";

    /**
     * 省份
     */
    private String mProvince;

    /**
     * 城市
     */
    private String mCity;

    private double mLatitude;
    private double mLongitude;

    public LocationModel()
    {
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        this.mProvince = province;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        this.mCity = city;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    /**
     * 生成JSON对象.
     * @return
     */
    public JSONObject toJSONObject()
    {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            //省份
            jsonObject.put("province", getProvince());
            //城市
            jsonObject.put("city", getCity());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "toJSONObject: {}", throwable);
        }
        return jsonObject;
    }

    /**
     * 解析位置信息.
     * @param jsonObject
     * @return
     */
    public static LocationModel parseJSONObject(JSONObject jsonObject)
    {
        LocationModel locationModel = null;
        if (jsonObject != null)
        {
            locationModel = new LocationModel();
            //省份
            locationModel.setProvince(jsonObject.optString("province", null));
            //城市
            locationModel.setCity(jsonObject.optString("city", null));
        }
        return locationModel;
    }
}
