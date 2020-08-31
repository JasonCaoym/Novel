package com.duoyue.mod.ad;

import android.app.Activity;
import android.content.Context;

public interface IAdPlatform {

    void init(Context context, String appId);

    IAdSource createSource(Activity context);

}
