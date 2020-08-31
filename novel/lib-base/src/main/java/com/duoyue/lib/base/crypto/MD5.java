package com.duoyue.lib.base.crypto;

import com.duoyue.lib.base.io.IOUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public class MD5
{
    public static String getMD5(byte[] data) throws Throwable
    {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(data);
        return toHex(digest.digest());
    }

    public static String getMD5(File file) throws Throwable
    {
        FileInputStream in = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 8];
            int length;
            while ((length = in.read(buffer)) != -1)
            {
                digest.update(buffer, 0, length);
            }
            return toHex(digest.digest());
        } finally
        {
            IOUtil.close(in);
        }
    }

    public static String getCRC32(String data)
    {
        CRC32 crc32 = new CRC32();
        crc32.update(data.getBytes());
        return Long.toHexString(crc32.getValue()).toLowerCase();
    }

    public static String getCRC32(File file) throws Throwable
    {
        BufferedInputStream inputStream = null;
        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            CRC32 crc32 = new CRC32();
            byte[] buffer = new byte[1024 * 8];
            int length;
            while ((length = inputStream.read(buffer)) != -1)
            {
                crc32.update(buffer, 0, length);
            }
            return Long.toHexString(crc32.getValue()).toLowerCase();
        } finally
        {
            IOUtil.close(inputStream);
        }
    }

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String toHex(byte[] data)
    {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++)
        {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }
}
