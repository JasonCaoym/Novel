package com.duoyue.lib.base;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;

public class BaseContext
{
    private static Context mContext;

    public static Context getContext()
    {
        if (mContext == null)
        {
            mContext = getApplicationContext();
        }
        return mContext;
    }

    private static Context getApplicationContext()
    {
        try
        {
            Class clsActivityThread = Class.forName("android.app.ActivityThread");
            Method medCurrentApplication = clsActivityThread.getDeclaredMethod("currentApplication");
            medCurrentApplication.setAccessible(true);
            Application application = (Application) medCurrentApplication.invoke(clsActivityThread);
            return application.getApplicationContext();
        } catch (Throwable throwable)
        {
            //ignore
        }
        return null;
    }
}
