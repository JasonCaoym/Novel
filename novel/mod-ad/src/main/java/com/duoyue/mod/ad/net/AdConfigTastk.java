package com.duoyue.mod.ad.net;

import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.timer.TimerTask;
import com.duoyue.lib.base.event.AdConfigEvent;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdConfigBean;
import com.duoyue.mod.ad.dao.AdConfigHelp;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;

public class AdConfigTastk extends TimerTask {

    private boolean isFirstEntry = true;

    @Override
    public String getAction() {
        return "ad#AdConfigTastk";
    }

    @Override
    public long getPollTime() {
        return 60;
    }

    @Override
    public long getErrorTime() {
        return 2;
    }

    @Override
    public boolean requireNetwork() {
        return true;
    }

    @Override
    public long timeUp() throws Throwable {
        if (isFirstEntry) {
            isFirstEntry = false;
            Thread.sleep(3000);
        }
        for (String channalCode : Constants.channalCodes) {
            AdConfigBean adConfigBean = AdHttpUtil.request(channalCode);
            AdConfigHelp.getsInstance().deleteByChannalCode(channalCode);
            if (adConfigBean != null) {
                AdConfigHelp.getsInstance().saveAdConfig(adConfigBean);
                if (adConfigBean != null) {
                    Logger.i("ad#AdConfigTastk", "获取到数据 : " + new Gson().toJson(adConfigBean));
                } else {
                    Logger.i("ad#AdConfigTastk", "获取到数据 : 没有获取到数据");
                }
            }
        }
        EventBus.getDefault().post(new AdConfigEvent(false));
        return 60;
    }

}
