package com.duoyue.app.upgrade;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.duoyue.lib.base.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author fanwentao
 * @Description
 * @date 2018/7/6
 */
public class Tools {

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo;
        try {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return networkInfo != null && networkInfo.isAvailable()
                && networkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    /**
     * app version code应用版本号
     *
     * @return Version Code
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * get file md5
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String md5 = bigInt.toString(16);
        Logger.e("App#Tools", " file md5 = " + md5);
        return md5;
    }

    public static int parseString2Int(String source, int df) {
        int result = df;
        try {
            result = Integer.parseInt(source);
        } catch (Exception ex) {
            result = df;
        }
        return result;
    }

    public static long parseString2Long(String source, long df) {
        long result = df;
        try {
            result = Long.parseLong(source);
        } catch (Exception ex) {
            result = df;
        }
        return result;
    }

}
