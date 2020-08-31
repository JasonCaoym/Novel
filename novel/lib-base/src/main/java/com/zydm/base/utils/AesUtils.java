package com.zydm.base.utils;

import com.zydm.base.common.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {

    private static final String AES = "AES";
    private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
    private static final String HEX = "0123456789ABCDEF";
    public static final String DEFAULT_IV_PARAM = "98AV76130245ZLPM";
    public static final String DEFAULT_KEY = "CM/ido-c/6981799";

    public static String encryptString(String cleartext) {
        try {
            return encryptString(DEFAULT_KEY, DEFAULT_IV_PARAM, cleartext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptString(String key, String iv, String cleartext)
            throws Exception {
        byte[] result = encryptByte(key, iv, cleartext.getBytes(Constants.UTF_8));
        return toHex(result);
    }

    public static String decryptString(String encrypted) {
        try {
            return decryptString(DEFAULT_KEY, DEFAULT_IV_PARAM, encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.EMPTY;
        }
    }

    public static String decryptString(String key, String iv, String encrypted)
            throws Exception {
        byte[] enc = toByte(encrypted);
        byte[] result = decryptByte(key, iv, enc);
        return new String(result, Constants.UTF_8);
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    private static String toHex(byte[] buf) {
        if (buf == null) {
            return Constants.EMPTY;
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static byte[] toByte(String hexString) {

        int len = hexString.length() / 2;
        byte[] result = new byte[len];

        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        }
        return result;

    }

    public static byte[] decryptByte(String key, String iv, byte[] encrypted)
            throws Exception {
        Cipher cipher = initAESCipher(key, iv, Cipher.DECRYPT_MODE);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static byte[] encryptByte(String key, String iv, byte[] clear)
            throws Exception {
        Cipher cipher = initAESCipher(key, iv, Cipher.ENCRYPT_MODE);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static Cipher getDefaultCipher(int cipherMode) {
        try {
            return initAESCipher(DEFAULT_KEY, DEFAULT_IV_PARAM, cipherMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Cipher initAESCipher(String key, String iv, int cipherMode)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(
                iv.getBytes(Constants.UTF_8));
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(Constants.UTF_8), AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
        cipher.init(cipherMode, skeySpec, zeroIv);
        return cipher;
    }
}
