package com.duoyue.lib.base.cache;

import com.duoyue.lib.base.io.FileAccesser;

import java.io.File;

public class StringParser implements CacheParser<String>
{
    @Override
    public String read(File file) throws Throwable
    {
        return FileAccesser.readString(file);
    }

    @Override
    public void write(File file, String object) throws Throwable
    {
        if (object != null)
        {
            FileAccesser.writeString(file, object);
        } else
        {
            FileAccesser.clear(file);
        }
    }
}
