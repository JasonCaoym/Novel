package com.duoyue.lib.base.app.http;

import com.google.gson.annotations.SerializedName;

public class JsonResponse<T>
{
    @SerializedName("status")
    public int status;

    @SerializedName("code")
    public String code;

    @SerializedName("interval")
    public long interval;

    @SerializedName("msg")
    public String msg;

    @SerializedName("data")
    public T data;
}
