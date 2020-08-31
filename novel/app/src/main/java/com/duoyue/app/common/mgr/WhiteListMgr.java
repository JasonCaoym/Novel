package com.duoyue.app.common.mgr;

import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;

import java.util.Arrays;
import java.util.List;

public class WhiteListMgr {


    public static String getOppoWhiteListUrl() {
        return AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_URL_OPPO);
    }

    public static String getVivoWhiteListUrl() {
        return AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_URL_VIVO);
    }

    public static String getXiaomiWhiteListUrl() {
        return AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_URL_XIAOMI);
    }

    public static String getHuaweiWhiteListUrl() {
        return AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_URL_HUAWEI);
    }

    public static String getJinliWhiteListUrl() {
        return AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_URL_GIONEE);
    }

    public static String getMeizuWhiteListUrl() {
        return AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.PAGE_URL_MEIZU);
    }

    /**
     * 获取白名单列表
     * @return
     */
    public static List<String> getSupportWhiteList() {
        return Arrays.asList("oppo", "vivo", "huawei", "xiaomi", "meizu", "gionee");
    }

}
