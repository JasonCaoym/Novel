package com.duoyue.app.service;

import android.app.Service;
import android.content.Intent;

public abstract class MiniService
{
    public MiniService(Service service)
    {
    }

    public abstract void onStartCommand(Intent intent);

    public abstract void onDestroy();
}
