package com.duoyue.lib.base.io;

import java.io.*;
import java.util.List;

public class IOUtil
{
    public static void deleteFile(File file)
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {
                deleteFiles(file.listFiles());
            }
            delete(file);
        }
    }

    public static void deleteFiles(File[] files)
    {
        if (files != null)
        {
            for (File file : files)
            {
                deleteFile(file);
            }
        }
    }

    private static void delete(File file)
    {
        if (!file.delete())
        {
            file.deleteOnExit();
        }
    }

    public static void createFile(File file) throws Throwable
    {
        createFolder(file.getParentFile());
    }

    public static void createFolder(File folder) throws Throwable
    {
        if (folder.isDirectory() || folder.mkdirs())
        {
            return;
        }
        throw new FileNotFoundException("Create folder failed: " + folder.getAbsolutePath());
    }

    public static void copyToFolder(File src, File dest) throws Throwable
    {
        if (src.exists())
        {
            if (src.isDirectory())
            {
                copyToFolder(src.listFiles(), new File(dest, src.getName()));
            } else
            {
                copyToFile(src, new File(dest, src.getName()));
            }
        }
    }

    public static void copyToFolder(File[] src, File dest) throws Throwable
    {
        if (src != null)
        {
            for (File file : src)
            {
                copyToFolder(file, dest);
            }
        }
    }

    public static void copyToFile(File src, File dest) throws Throwable
    {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try
        {
            createFile(dest);
            inputStream = new FileInputStream(src);
            outputStream = new FileOutputStream(dest);
            syncStream(inputStream, outputStream);
        } finally
        {
            close(outputStream, inputStream);
        }
    }

    public static void syncStream(InputStream inputStream, OutputStream outputStream) throws Throwable
    {
        syncStream(inputStream, outputStream, 8);
    }

    public static void syncStream(InputStream inputStream, OutputStream outputStream, int bufferSize) throws Throwable
    {
        byte[] buffer = new byte[1024 * bufferSize];
        int length;
        while ((length = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
    }

    public static void close(Closeable... closeables)
    {
        if (closeables != null)
        {
            for (Closeable closeable : closeables)
            {
                if (closeable != null)
                {
                    try
                    {
                        closeable.close();
                    } catch (Throwable throwable)
                    {
                        //ignore
                    }
                }
            }
        }
    }

    public static void clearList(List list) {
        if (list != null) {
            list.clear();
            list = null;
        }
    }
}
