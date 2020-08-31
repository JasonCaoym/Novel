package com.duoyue.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import com.duoyue.lib.base.app.domain.DomainManager;
import com.duoyue.lib.base.app.timer.TimerExecutor;
import com.duoyue.lib.base.app.timer.TimerTask;
import com.duoyue.mod.ad.api.AdRequestTastk;
import com.duoyue.mod.ad.net.AdConfigTastk;
import com.duoyue.mod.stats.common.upload.FuncPageStatsRequestTask;
import java.util.ArrayList;
import java.util.List;

public class TimerService extends MiniService
{
    private List<TimerTask> taskList;
    private TimerExecutor executor;
    private Handler handler;

    public TimerService(Service service)
    {
        super(service);
        taskList = new ArrayList<>();
        taskList.add(DomainManager.getInstance().getFetchConfigTask());
        taskList.add(DomainManager.getInstance().getPingConfigTask());
        taskList.add(new AdRequestTastk());
        taskList.add(new AdConfigTastk());

		//统计模块--上报功能统计.
//        taskList.add(new FuncStatsRequestTask());
        //统计模块--上报广告统计.
//        taskList.add(new AdStatsRequestTask());
        // 1.1.8 场景统计节点上报
        taskList.add(new FuncPageStatsRequestTask());
        executor = new TimerExecutor();
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Task());
    }

    @Override
    public void onStartCommand(Intent intent)
    {

    }

    @Override
    public void onDestroy()
    {

    }

    private class Task implements Runnable
    {
        @Override
        public void run()
        {
            handler.postDelayed(this, 60 * 1000L);
            for (TimerTask task : taskList)
            {
                executor.exec(task);
            }
        }
    }
}
