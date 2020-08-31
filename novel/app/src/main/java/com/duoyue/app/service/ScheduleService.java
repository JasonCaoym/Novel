package com.duoyue.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class ScheduleService extends Service
{
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private List<MiniService> serviceList;

    @Override
    public void onCreate()
    {
        super.onCreate();
        serviceList = new ArrayList<>();
        serviceList.add(new TimerService(this));
        serviceList.add(new UserService(this));
        serviceList.add(new CountDownService(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent != null && !Intent.ACTION_CALL.equals(intent.getAction()))
        {
            for (MiniService service : serviceList)
            {
                service.onStartCommand(intent);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        for (MiniService service : serviceList)
        {
            service.onDestroy();
        }
        super.onDestroy();
    }
}
