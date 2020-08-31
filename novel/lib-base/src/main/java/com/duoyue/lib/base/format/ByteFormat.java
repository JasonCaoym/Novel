package com.duoyue.lib.base.format;

public class ByteFormat
{
    public static byte[] intToBytes(int value)
    {
        int length = 4;
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++)
        {
            data[i] = (byte) ((value >>> (3 - i) * 8) & 0xFF);
        }
        return data;
    }

    public static int bytesToInt(byte[] data)
    {
        int value = 0;
        int length = 4;
        for (int i = 0; i < length; i++)
        {
            value += (data[i] & 0xFF) << (3 - i) * 8;
        }
        return value;
    }

    public static byte[] longToBytes(long value)
    {
        int length = 8;
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++)
        {
            data[i] = (byte) ((value >>> (7 - i) * 8) & 0xFF);
        }
        return data;
    }

    public static long bytesToLong(byte[] data)
    {
        long value = 0;
        int length = 8;
        for (int i = 0; i < length; i++)
        {
            value += ((long) data[i] & 0xFF) << (7 - i) * 8;
        }
        return value;
    }

    public static byte[] floatToBytes(float value)
    {
        return intToBytes(Float.floatToIntBits(value));
    }

    public static float bytesToFloat(byte[] data)
    {
        return Float.intBitsToFloat(bytesToInt(data));
    }

    public static byte[] doubleToBytes(double value)
    {
        return longToBytes(Double.doubleToLongBits(value));
    }

    public static double bytesToDouble(byte[] data)
    {
        return Double.longBitsToDouble(bytesToLong(data));
    }

    public static byte[] booleanToBytes(boolean value)
    {
        return intToBytes(value ? 1 : 0);
    }

    public static boolean bytesToBoolean(byte[] data)
    {
        return bytesToInt(data) == 1;
    }
}
