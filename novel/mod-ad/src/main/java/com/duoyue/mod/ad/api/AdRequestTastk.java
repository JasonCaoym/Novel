package com.duoyue.mod.ad.api;

import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.timer.TimerTask;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.AdEventMessage;
import com.duoyue.mod.ad.bean.AdBean;
import com.duoyue.mod.ad.bean.AdReadConfigBean;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.google.gson.Gson;
import com.zydm.base.utils.SPUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class AdRequestTastk extends TimerTask {

    @Override
    public String getAction() {
        return "ad#AdRequestTastk";
    }

    @Override
    public long getPollTime() {
        return 60;
    }

    @Override
    public long getErrorTime() {
        return 1;
    }

    @Override
    public boolean requireNetwork() {
        return true;
    }

    @Override
    public long timeUp() throws Throwable {
        AdConfigRequest request = new AdConfigRequest();
        JsonResponse<AdBean> response = new JsonPost.SyncPost<AdBean>()
                .setRequest(request)
                .setResponseType(AdBean.class)
                .post();
        long interval = 60;
        if (response.status == 1) {
            interval = response.interval;
            if (response.data != null) {
                /*if (response.data.getAdConfigList() != null && response.data.getAdConfigList().size() > 0) {
                    AdPositionConfigHelp.getsInstance().clearAll();
                    AdPositionConfigHelp.getsInstance().savePositions(response.data.getAdConfigList());
                }
                if (response.data.getAdOriginConfigList() != null && response.data.getAdOriginConfigList().size() > 0) {
                    AdOriginConfigHelp.getsInstance().clearAll();
                    AdOriginConfigHelp.getsInstance().saveOrigins(response.data.getAdOriginConfigList());
                }*/
                if (response.data.getParamConfigList() != null && response.data.getParamConfigList().size() > 0) {
                    AdReadConfigHelp.getsInstance().clearAll();
                    AdReadConfigHelp.getsInstance().saveReads(response.data.getParamConfigList());
                    List<AdReadConfigBean> configParamsList = response.data.getParamConfigList();
                    if (configParamsList != null && !configParamsList.isEmpty()) {
                        for (AdReadConfigBean configBean : configParamsList) {
                            if (configBean.getParamName().equals(AdConstants.ReadParams.FLOW_FREE_DURATION)) {
                                SPUtils.INSTANCE.putInt(AdConstants.ReadParams.FLOW_FREE_DURATION, StringFormat.parseInt(configBean.getParamValue(), 15));
                            } else  if (configBean.getParamName().equals(AdConstants.ReadParams.RD_BANNER_FREE)) {
                                SPUtils.INSTANCE.putInt(AdConstants.ReadParams.RD_BANNER_FREE, StringFormat.parseInt(configBean.getParamValue(), 15));
                            } else if (configBean.getParamName().equals(AdConstants.ReadParams.RD_TIRED_FREE_TIME)) {
                                SPUtils.INSTANCE.putInt(AdConstants.ReadParams.RD_TIRED_FREE_TIME, StringFormat.parseInt(configBean.getParamValue(), 15));
                            } else if (configBean.getParamName().equals(AdConstants.ReadParams.RD_TIRED_TIME)) {
                                SPUtils.INSTANCE.putInt(AdConstants.ReadParams.RD_TIRED_TIME, StringFormat.parseInt(configBean.getParamValue(), 15));
                            } else if (configBean.getParamName().equals(AdConstants.ReadParams.AD_COMM_CLICK_INTERVAL)) {
                                SPUtils.INSTANCE.putLong(AdConstants.ReadParams.AD_COMM_CLICK_INTERVAL, StringFormat.parseInt(configBean.getParamValue(), 15));
                            } else if (configBean.getParamName().equals(AdConstants.ReadParams.BANNER_INTERVAL)) {
                                SPUtils.INSTANCE.putInt(AdConstants.ReadParams.BANNER_INTERVAL, StringFormat.parseInt(configBean.getParamValue(), 5));
                            } else if (configBean.getParamName().equals(AdConstants.ReadParams.RD_BANNER_CLEAR)) {
                                SPUtils.INSTANCE.putInt(AdConstants.ReadParams.RD_BANNER_CLEAR, StringFormat.parseInt(configBean.getParamValue(), 4));
                            }
                        }
                    }
                }
                EventBus.getDefault().post(new AdEventMessage());
                Logger.i("ad#AdRequestTastk", "onNext: " + new Gson().toJson(response));
            }
        }
        return interval;
    }

}
