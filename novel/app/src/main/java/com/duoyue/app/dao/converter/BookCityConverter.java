package com.duoyue.app.dao.converter;

import com.duoyue.app.bean.BookCityItemBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookCityConverter implements PropertyConverter<List<BookCityItemBean>, String> {

    private final Gson mGson;

    public BookCityConverter() {
        mGson = new Gson();
    }

    @Override
    public List<BookCityItemBean> convertToEntityProperty(String databaseValue) {
        Type type = new TypeToken<ArrayList<BookCityConverter>>() {
        }.getType();
        return mGson.fromJson(databaseValue, type);
    }

    @Override
    public String convertToDatabaseValue(List<BookCityItemBean> entityProperty) {
        return mGson.toJson(entityProperty);
    }

}
