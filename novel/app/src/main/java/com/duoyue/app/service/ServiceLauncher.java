package com.duoyue.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class ServiceLauncher
{
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Class[] services = {ScheduleService.class};
    private static Context mContext;

    public static void init(Context context)
    {
        mContext = context.getApplicationContext();
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                handler.postDelayed(this, 60 * 1000L);

                for (Class service : services)
                {
                    try
                    {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setClass(mContext, service);
                        mContext.startService(intent);
                    } catch (Throwable throwable)
                    {
                        //ignore
                    }
                }
            }
        });
    }
}
