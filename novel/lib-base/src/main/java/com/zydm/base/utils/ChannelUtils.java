package com.zydm.base.utils;

import android.os.Build;
import com.zydm.base.data.base.MtMap;

import java.util.Locale;

/**
 * Created by YinJiaYan on 2017/6/20.
 */

public class ChannelUtils {

    private static final String CHANNEL_DEFAULT = "motong";
    private static final String CHANNEL_TRANSFORM = "xChannel";

    public static final String CHANNEL_HUA_WEI = "huawei";
    public static final String CHANNEL_HUA_WEI_ABROAD = "huaWeiAbroad";
    public static final String CHANNEL_GOOGLE = "google";

//    private static final String[] PHONE_CHANNELS = {
////            "lenovo",
////            "chuizi",
////            "Coolpad",
////            "leshi",
////            "C360",
////            "meizu",
////            "meizuLLQ",
////            "oppo",
////            "samsung",
////            "vivo",
////            "xiaomi",
////            "gionee",
////            "gioneeSuo",
////            CHANNEL_HUA_WEI,
////            CHANNEL_HUA_WEI_ABROAD
////    };

    //<Build.MANUFACTURER,channel>
    private static MtMap<String, String> CONFIGS;
    static {
        CONFIGS = new MtMap<>();
        CONFIGS.put("MEIZU", "meizu");
        CONFIGS.put("GIONEE", "gionee");
        CONFIGS.put("OPPO", "oppo");
        CONFIGS.put("SAMSUNG", "samsung");
        CONFIGS.put("HUAWEI", "huawei");
        CONFIGS.put("XIAOMI", "xiaomi");
        CONFIGS.put("SMARTISAN", "chuizi");
        CONFIGS.put("VIVO", "vivo");

        CONFIGS.put("LENOVO", "lenovo");
        CONFIGS.put("COOLPAD", "Coolpad");
        CONFIGS.put("LESHI", "leshi");
        CONFIGS.put("LE", "leshi");
    }

    public static String transformChannel(String channelConfig) {
        if (StringUtils.isBlank(channelConfig)) {
            return CHANNEL_DEFAULT;
        }

        String channel;
        if (channelConfig.startsWith(CHANNEL_TRANSFORM)) {
            String manufacturer = StringUtils.asString(Build.MANUFACTURER);
            String mobileChannel = CONFIGS.get(manufacturer.toUpperCase(Locale.US));
            if (StringUtils.isBlank(mobileChannel)) {
                if ("xChannelHuaWei".equals(channelConfig)) {
                    return CHANNEL_HUA_WEI;
                } else {
                    return CHANNEL_DEFAULT;
                }
            } else {
                return mobileChannel;
            }
        } else if (channelConfig.startsWith("DY04_XIAOMI")) { // 偶先部分小米机型读取到的渠道号后拼接了账户名
            return "DY04_XIAOMI";
        } else {
            channel = channelConfig;
        }

        return channel;
    }
}
