package com.duoyue.lib.base.cache;

import com.duoyue.lib.base.log.Logger;

import java.io.File;

public class Cache<T>
{
    private static final String TAG = "Base#Cache";

    private File cacheFile;
    private CacheParser<T> cacheParser;

    public Cache(File file, CacheParser<T> parser)
    {
        cacheFile = file;
        cacheParser = parser;
    }

    public T get()
    {
        return get(null);
    }

    public synchronized T get(T defaultValue)
    {
        try
        {
            T cacheObject = cacheParser.read(cacheFile);
            if (cacheObject != null)
            {
                return cacheObject;
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "get: failed!", throwable);
        }
        return defaultValue;
    }

    public synchronized void set(T object)
    {
        try
        {
            cacheParser.write(cacheFile, object);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "set: failed!", throwable);
        }
    }
}