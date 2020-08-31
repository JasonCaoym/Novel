package com.duoyue.lib.base.cache;

import com.duoyue.lib.base.crypto.NES;
import com.duoyue.lib.base.io.FileAccesser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class GsonParser<T> implements CacheParser<T>
{
    private Class<T> mType;

    public GsonParser(Class<T> type)
    {
        mType = type;
    }

    private byte[] encode(String data)
    {
        return NES.encode(data.getBytes());
    }

    private String decode(byte[] data)
    {
        return new String(NES.decode(data));
    }

    @Override
    public T read(File file) throws Throwable
    {
        byte[] data = FileAccesser.readBytes(file);
        if (data != null && data.length > 0)
        {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            return gson.fromJson(decode(data), mType);
        }
        return null;
    }

    @Override
    public void write(File file, T object) throws Throwable
    {
        if (object != null)
        {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            FileAccesser.writeBytes(file, encode(gson.toJson(object)));
        } else
        {
            FileAccesser.clear(file);
        }
    }
}
