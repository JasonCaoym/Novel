package com.duoyue.lib.base.crypto;

public class NES
{
    static
    {
        System.loadLibrary("cus_crypto");
    }

    public static native byte[] encode(byte[] data);

    public static native byte[] decode(byte[] data);
}
