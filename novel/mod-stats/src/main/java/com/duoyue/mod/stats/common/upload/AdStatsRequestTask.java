package com.duoyue.mod.stats.common.upload;

import com.duoyue.lib.base.app.timer.TimerTask;

/**
 * 广告统计数据请求任务
 */
public class AdStatsRequestTask extends TimerTask {

    @Override
    public String getAction() {
        return "Stats#AdStatsRequestTask";
    }

    @Override
    public long getPollTime() {
        return 1;
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
        //调用上报广告统计信息接口.
//        long interval = UploadStatsMgr.getInstance().uploadAdStats();
//        return interval > 0 ? interval : 1;
        return 1000000;
    }
}
