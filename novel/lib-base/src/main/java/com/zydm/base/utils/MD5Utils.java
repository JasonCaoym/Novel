package com.zydm.base.utils;

import com.zydm.base.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.zip.CRC32;

public class MD5Utils {

    private static final String TAG = "MD5Utils";

    public static final String SHA265 = "SHA-256";
    public static final String SHA1 = "SHA-1";
    public static final String MD5 = "MD5";

    public static String getFileMd5(File file) {
        byte[] digest = null;
        FileInputStream in = null;
        if (file == null) {
            return Constants.EMPTY;
        }
        try {
            MessageDigest digester = MessageDigest.getInstance(MD5);
            byte[] bytes = new byte[8192];
            in = new FileInputStream(file);
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            digest = digester.digest();
        } catch (Exception cause) {
            LogUtils.e(TAG, "Unable to compute MD5 of \"" + file + "\"", cause);
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (Exception e) {
                }

            }
        }
        return (digest == null) ? Constants.EMPTY : byteArrayToString(digest);
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            ret.append(Character.forDigit((aByte >> 4) & 0xf, 16));
            ret.append(Character.forDigit(aByte & 0xf, 16));
        }
        return ret.toString().toUpperCase(Locale.US);
    }

    public static String getStringMd5(String string) {
        try {
            return getMd5(string.getBytes(Constants.UTF_8), MD5);
        } catch (Exception cause) {
            LogUtils.e(TAG, "Unable to compute MD5 of \"" + string + "\"", cause);
            return Constants.EMPTY;
        }
    }

    public static String getMd5(byte[] bytes, String encryptType) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance(encryptType).digest(
                    bytes);
        } catch (Exception cause) {
            return Constants.EMPTY;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toUpperCase(Locale.US);
    }

    public static long getCRC32(String str) {
        CRC32 crc32 = new CRC32();
        crc32.update(str.getBytes());
        return crc32.getValue();
    }
}
