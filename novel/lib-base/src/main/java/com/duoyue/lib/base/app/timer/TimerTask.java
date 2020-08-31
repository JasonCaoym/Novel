package com.duoyue.lib.base.app.timer;

public abstract class TimerTask
{
    public abstract String getAction();

    public abstract long getPollTime();

    public long getErrorTime()
    {
        return 0;
    }

    public boolean requireNetwork()
    {
        return false;
    }

    public abstract long timeUp() throws Throwable;
}

