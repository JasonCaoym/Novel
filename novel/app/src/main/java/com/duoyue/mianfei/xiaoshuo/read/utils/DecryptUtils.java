package com.duoyue.mianfei.xiaoshuo.read.utils;

import com.zydm.base.utils.AesUtils;
import com.zydm.base.utils.MD5Utils;
import org.json.JSONObject;

public class DecryptUtils {
    private static final String BASE_KEY = "mtwl.com.motong.cm";
    private static String KEY_SECRET;
    private static String IV_SECRET;

    static {
        String md5 = MD5Utils.getStringMd5(BASE_KEY);
        KEY_SECRET = md5.substring(16, 32);
        IV_SECRET = md5.substring(0, 16);
    }

    private static String[] decryptSecret(String encrypt) throws Exception {
        String[] secret = new String[2];
        String json = AesUtils.decryptString(KEY_SECRET, IV_SECRET, encrypt);
        JSONObject jsonObject = new JSONObject(json);
        secret[0] = jsonObject.optString("key");
        secret[1] = jsonObject.optString("iv");
        return secret;
    }

    public static String decryptUrl(String secretEncrypt, String encryptUrl) throws Exception {
        String[] aes = decryptSecret(secretEncrypt);
        return AesUtils.decryptString(aes[0], aes[1], encryptUrl);
    }
}
