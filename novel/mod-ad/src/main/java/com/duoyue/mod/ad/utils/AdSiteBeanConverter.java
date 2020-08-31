package com.duoyue.mod.ad.utils;

import android.text.TextUtils;
import com.duoyue.mod.ad.bean.AdSiteBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AdSiteBeanConverter implements PropertyConverter<List<AdSiteBean>, String> {

    private Gson mGson;

    public AdSiteBeanConverter() {
        mGson = new Gson();
    }

    @Override
    public List<AdSiteBean> convertToEntityProperty(String databaseValue) {
        if (TextUtils.isEmpty(databaseValue)) {
            return null;
        }
        Type type = new TypeToken<ArrayList<AdSiteBean>>() {}.getType();
        List<AdSiteBean> list = mGson.fromJson(databaseValue , type);
        return list;
    }

    @Override
    public String convertToDatabaseValue(List<AdSiteBean> entityProperty) {
        return mGson.toJson(entityProperty);
    }

}
