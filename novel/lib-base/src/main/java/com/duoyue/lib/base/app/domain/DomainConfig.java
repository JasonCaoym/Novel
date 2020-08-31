package com.duoyue.lib.base.app.domain;

import com.google.gson.annotations.SerializedName;

public class DomainConfig
{
    @SerializedName("upgradedHosts")
    public String upgradedHosts;

    @SerializedName("appHosts")
    public String appHosts;
}
