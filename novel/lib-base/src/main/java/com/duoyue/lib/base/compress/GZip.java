package com.duoyue.lib.base.compress;

import com.duoyue.lib.base.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZip
{
    public static byte[] zip(byte[] data) throws Throwable
    {
        ByteArrayOutputStream out = null;
        GZIPOutputStream gzip = null;
        try
        {
            out = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(out);
            gzip.write(data);
            gzip.finish();
            return out.toByteArray();
        } finally
        {
            IOUtil.close(gzip, out);
        }
    }

    public static byte[] unzip(byte[] data) throws Throwable
    {
        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        GZIPInputStream gzip = null;
        try
        {
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(data);
            gzip = new GZIPInputStream(in);
            IOUtil.syncStream(gzip, out);
            return out.toByteArray();
        } finally
        {
            IOUtil.close(gzip, in, out);
        }
    }
}
