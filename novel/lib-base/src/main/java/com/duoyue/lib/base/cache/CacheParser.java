package com.duoyue.lib.base.cache;

import java.io.File;

public interface CacheParser<T>
{
    abstract T read(File file) throws Throwable;

    abstract void write(File file, T object) throws Throwable;
}
