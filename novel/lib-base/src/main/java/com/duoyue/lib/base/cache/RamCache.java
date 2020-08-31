package com.duoyue.lib.base.cache;

import java.io.File;

public class RamCache<T> extends Cache<T>
{
    private T cacheObject;

    public RamCache(File file, CacheParser<T> parser)
    {
        super(file, parser);
    }

    @Override
    public synchronized T get(T defaultValue)
    {
        if (cacheObject == null)
        {
            cacheObject = super.get(defaultValue);
        }
        return cacheObject;
    }

    @Override
    public synchronized void set(T object)
    {
        cacheObject = object;
        super.set(object);
    }
}
