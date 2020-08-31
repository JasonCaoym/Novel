package com.duoyue.mod.stats.common.upload;

import com.duoyue.lib.base.app.timer.TimerTask;

/**
 * 功能统计数据请求任务
 */
public class FuncStatsRequestTask extends TimerTask {

    @Override
    public String getAction() {
        return "Stats#FuncStatsRequestTask";
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
        //调用上报功能统计信息接口.
//        long interval = UploadStatsMgr.getInstance().uploadFuncStats();
//        return interval > 0 ? interval : 1;
        return 10000;
    }
}
