package com.duoyue.lib.base.app.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RequestParser
{
    static RequestParser parse(JsonRequest request) throws Throwable
    {
        RequestParser parser = new RequestParser();
        parser.parseUrl(request);
        parser.parseHeaderAndParams(request);
        return parser;
    }

    private String url;
    private Map<String, String> header;
    private JsonObject request;

    private RequestParser()
    {
        header = new HashMap<>();
    }

    String getUrl()
    {
        return url;
    }

    Map<String, String> getHeader()
    {
        return header;
    }

    public JsonObject getRequest()
    {
        return request;
    }

    private void parseUrl(JsonRequest request) throws Throwable
    {
        Class clazz = request.getClass();
        AutoPost autoPost = (AutoPost) clazz.getAnnotation(AutoPost.class);
        if (autoPost == null)
        {
            throw new Exception("Declare request without AutoPost!");
        }
        url = FillTool.getUrl(autoPost.domain(), autoPost.action());
    }

    private void parseHeaderAndParams(JsonRequest jsonRequest)
    {
        Class clazz = jsonRequest.getClass();
        List<Field> list = new ArrayList<>();
        addAllField(clazz, list);
        for (Field field : list)
        {
            parseHeader(field.getAnnotation(AutoHeader.class), field, jsonRequest);
            parseParam(field.getAnnotation(AutoParams.class), field, jsonRequest);
        }
        request = new Gson().toJsonTree(jsonRequest).getAsJsonObject();
    }

    private void parseHeader(AutoHeader autoHeader, Field field, JsonRequest jsonRequest)
    {
        if (autoHeader != null)
        {
            switch (autoHeader.value())
            {
                case CUSTOM:
                    FillTool.addHeader(header, field, jsonRequest);
                    break;
                case USER_AGENT:
                    FillTool.addUserAgent(header);
                    break;
                case TOKEN:
                    FillTool.addToken(header);
                    break;
            }
        }
    }

    private void parseParam(AutoParams autoParams, Field field, JsonRequest jsonRequest)
    {
        if (autoParams != null)
        {
            switch (autoParams.value())
            {
                case MID:
                    FillTool.setMid(field, jsonRequest);
                    break;
                case UID:
                    FillTool.setUid(field, jsonRequest);
                    break;
                case APP_ID:
                    FillTool.setAppId(field, jsonRequest);
                    break;
                case CHANNEL_CODE:
                    FillTool.setChannelCode(field, jsonRequest);
                    break;
                case VERSION:
                    FillTool.setVersion(field, jsonRequest);
                    break;
                case TIMESTAMP:
                    FillTool.setTimestamp(field, jsonRequest);
                    break;
                case IMEI:
                    FillTool.setImei(field, jsonRequest);
                    break;
                case IMSI:
                    FillTool.setImsi(field, jsonRequest);
                    break;
                case MEID:
                    FillTool.setMeid(field, jsonRequest);
                    break;
                case ANDROID_ID:
                    FillTool.setAndroidId(field, jsonRequest);
                    break;
                case PROVINCE:
                    //省份
                    FillTool.setProvince(field, jsonRequest);
                    break;
                case CITY:
                    //城市
                    FillTool.setCity(field, jsonRequest);
                    break;
                case WIFIS:
                    //WIFI列表.
                    FillTool.setWiFis(field, jsonRequest);
                    break;
                case NETWORK:
                    //网络情况
                    FillTool.setNetwork(field, jsonRequest);
                    break;
                case MOBILE:
                    //运营商
                    FillTool.setOperator(field, jsonRequest);
                    break;

                case LATITUDE:
                    //维度
                    FillTool.setLatitude(field, jsonRequest);
                    break;
                case LONGITUDE:
                    //精度
                    FillTool.setLongitude(field, jsonRequest);
                    break;
                case PROTOCOL_CODE:
                    //协议版本号
                    FillTool.setProtocolCode(field, jsonRequest);
                    break;
            }
        }
    }

    private void addAllField(Class clazz, List<Field> list)
    {
        Class superClazz = clazz.getSuperclass();
        if (superClazz != null)
        {
            addAllField(superClazz, list);
        }
        Collections.addAll(list, clazz.getDeclaredFields());
    }
}
