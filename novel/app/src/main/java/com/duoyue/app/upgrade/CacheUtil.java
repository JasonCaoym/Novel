package com.duoyue.app.upgrade;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CacheUtil {

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;

    private static volatile CacheUtil cacheUtil;

    public static CacheUtil getInstance(Context context) {
        if (cacheUtil == null) {
            synchronized (CacheUtil.class) {
                if (cacheUtil == null) {
                    cacheUtil = new CacheUtil();
                    sharedPreferences = context.getSharedPreferences("Localsearchresults", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                }
            }
        }
        return cacheUtil;
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public void setDataList(String tag, List<String> datalist) {
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();

    }

    /**
     * 获取List
     *
     * @param
     * @return
     */
    public List<String> getDataList(String tag) {
        Gson gson = new Gson();
        String strJson = sharedPreferences.getString(tag, null);
        List<String> datalist = gson.fromJson(strJson, new TypeToken<List<String>>() {
        }.getType());
        if (datalist == null) {
            datalist = new ArrayList<>();
        }
        return datalist;

    }

    public void onDestroy() {
        editor = null;
        sharedPreferences = null;
        cacheUtil = null;
    }
}
