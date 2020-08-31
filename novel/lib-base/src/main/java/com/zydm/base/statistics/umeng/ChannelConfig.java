package com.zydm.base.statistics.umeng;

import com.zydm.base.data.base.MtMap;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.utils.StringUtils;

/**
 * Created by YinJiaYan on 2017/11/28.
 */

public class ChannelConfig {

    //<channel,resId>
    private static MtMap<String, Integer> IMAGES;

    static {
        IMAGES = new MtMap<>();
//        IMAGES.put("huawei", R.drawable.vmall);
//        IMAGES.put("yingyongbao", R.drawable.tencent);
    }

    public static int getSplashImgRes() {
        String appChannel = PhoneStatusManager.getInstance().getAppChannel();
        if (StringUtils.isBlank(appChannel) || !IMAGES.containsKey(appChannel)) {
            return -1;
        }
        return IMAGES.get(appChannel);
    }
}
