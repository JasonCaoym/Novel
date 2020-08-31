package com.duoyue.lib.base.io;

import com.duoyue.lib.base.format.ByteFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by shudo on 2018/4/10.
 */

public class FileAccesser
{
    public static Integer readInt(File file) throws Throwable
    {
        byte[] value = readRandom(file, 4);
        if (value != null)
        {
            return ByteFormat.bytesToInt(value);
        }
        return null;
    }

    public static Long readLong(File file) throws Throwable
    {
        byte[] value = readRandom(file, 8);
        if (value != null)
        {
            return ByteFormat.bytesToLong(value);
        }
        return null;
    }

    public static Float readFloat(File file) throws Throwable
    {
        byte[] value = readRandom(file, 4);
        if (value != null)
        {
            return ByteFormat.bytesToFloat(value);
        }
        return null;
    }

    public static Double readDouble(File file) throws Throwable
    {
        byte[] value = readRandom(file, 8);
        if (value != null)
        {
            return ByteFormat.bytesToDouble(value);
        }
        return null;
    }

    public static Boolean readBoolean(File file) throws Throwable
    {
        byte[] value = readRandom(file, 4);
        if (value != null)
        {
            return ByteFormat.bytesToBoolean(value);
        }
        return null;
    }

    public static void writeInt(File file, Integer value) throws Throwable
    {
        writeRandom(file, ByteFormat.intToBytes(value));
    }

    public static void writeLong(File file, Long value) throws Throwable
    {
        writeRandom(file, ByteFormat.longToBytes(value));
    }

    public static void writeFloat(File file, Float value) throws Throwable
    {
        writeRandom(file, ByteFormat.floatToBytes(value));
    }

    public static void writeDouble(File file, Double value) throws Throwable
    {
        writeRandom(file, ByteFormat.doubleToBytes(value));
    }

    public static void writeBoolean(File file, Boolean value) throws Throwable
    {
        writeRandom(file, ByteFormat.booleanToBytes(value));
    }

    private static byte[] readRandom(File file, int bufferSize) throws Throwable
    {
        if (file.exists())
        {
            FileInputStream inputStream = null;
            try
            {
                inputStream = new FileInputStream(file);
                byte[] buffer = new byte[bufferSize];
                int length = inputStream.read(buffer);
                if (length == bufferSize)
                {
                    return buffer;
                }
            } finally
            {
                IOUtil.close(inputStream);
            }
        }
        return null;
    }

    private static void writeRandom(File file, byte[] value) throws Throwable
    {
        FileOutputStream outputStream = null;
        try
        {
            IOUtil.createFile(file);
            outputStream = new FileOutputStream(file);
            outputStream.write(value);
            outputStream.flush();
        } finally
        {
            IOUtil.close(outputStream);
        }
    }

    public static String readString(File file) throws Throwable
    {
        byte[] value = readBytes(file);
        if (value != null)
        {
            return new String(value);
        }
        return null;
    }

    public static void writeString(File file, String value) throws Throwable
    {
        writeBytes(file, value.getBytes());
    }

    public static byte[] readBytes(File file) throws Throwable
    {
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try
        {
            if (file.exists())
            {
                inputStream = new FileInputStream(file);
                outputStream = new ByteArrayOutputStream();
                IOUtil.syncStream(inputStream, outputStream);
                return outputStream.toByteArray();
            }
        } finally
        {
            IOUtil.close(outputStream, inputStream);
        }
        return null;
    }

    public static void writeBytes(File file, byte[] value) throws Throwable
    {
        FileOutputStream outputStream = null;
        try
        {
            IOUtil.createFile(file);
            outputStream = new FileOutputStream(file);
            outputStream.write(value);
            outputStream.flush();
        } finally
        {
            IOUtil.close(outputStream);
        }
    }

    public static void clear(File file) throws Throwable
    {
        if (file.isFile())
        {
            if (file.delete())
            {
                return;
            }
            FileOutputStream outputStream = null;
            try
            {
                outputStream = new FileOutputStream(file);
            } finally
            {
                IOUtil.close(outputStream);
            }
        }
    }
}
