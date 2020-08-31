package com.duoyue.lib.base.app.timer;

import com.google.gson.annotations.SerializedName;

public class TimerRecord
{
    @SerializedName("timestamp")
    public long timestamp;

    @SerializedName("interval")
    public long interval;
}
