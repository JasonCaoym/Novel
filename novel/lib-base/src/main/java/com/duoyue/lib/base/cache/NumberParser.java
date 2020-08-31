package com.duoyue.lib.base.cache;

import com.duoyue.lib.base.io.FileAccesser;

import java.io.File;

public class NumberParser implements CacheParser<Number>
{
    @Override
    public Number read(File file) throws Throwable
    {
        return FileAccesser.readLong(file);
    }

    @Override
    public void write(File file, Number object) throws Throwable
    {
        if (object != null)
        {
            FileAccesser.writeLong(file, object.longValue());
        } else
        {
            FileAccesser.clear(file);
        }
    }
}
