package com.zydm.base.data.net;

import android.content.Context;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.tools.PhoneStatusManager;
import com.zydm.base.utils.FileUtil;
import com.zydm.base.utils.LogUtils;
import com.zydm.base.utils.MD5Utils;
import com.zydm.base.utils.StorageUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ApiCacheHelper {
    private static final String TAG = "ApiCacheHelper";
    private static final String API_CACHE_DIR_PREFIX = "apiCache_";
    private static final int MAX_CACHE_FILE_LIFE_CYCLE = 10000; //time in milliseconds
    private static String sVersionDir = null;

    private static Context getContext() {
        return BaseApplication.context.globalContext;
    }

    public static ArrayList<File> findTrashes() {
        ArrayList<File> shouldRecycledFiles = new ArrayList<File>();
        File[] allFiles = findAllCacheFiles();
        if (allFiles == null || allFiles.length == 0) {
            return shouldRecycledFiles;
        }
        for (int i = 0; i < allFiles.length; i++) {
            if (shouldBeRecycled(allFiles[i])) {
                shouldRecycledFiles.add(allFiles[i]);
            }
        }
        ArrayList<File> oldCacheDirs = findOldCacheDirs();
        shouldRecycledFiles.addAll(oldCacheDirs);

        return shouldRecycledFiles;
    }

    public static boolean clearCacheData(String url, HashMap<String, String> params) {
        String cacheFile = getCacheFilePath(url, params);
        return FileUtil.delete(cacheFile);
    }

    public static void storeCacheData(String url, HashMap<String, String> params, String content) {
        String cacheFile = getCacheFilePath(url, params);
        ensureDirectory(cacheFile);
        storeIntoDisk(cacheFile, content);
    }

    public static String getCacheData(String url, HashMap<String, String> params) {
        String cacheFile = getCacheFilePath(url, params);
        LogUtils.d(TAG, "getCacheData  params:" + params.toString() + " cacheFile:" + cacheFile);
        String cacheData = loadFromDisk(cacheFile);
//        LogUtils.d(TAG, "cacheData : " + cacheData);
        return cacheData;
    }

    private static void ensureDirectory(String path) {
        File parent = (new File(path)).getParentFile();
        if (!parent.exists()) {
            parent.mkdir();
        }
    }

    private static void storeIntoDisk(String fileName, String content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            byte[] bytes = content.getBytes();
            fos.write(bytes);
        } catch (FileNotFoundException e) {
            LogUtils.d(TAG, "storeIntoDisk() file " + fileName + " not exists");
        } catch (IOException exp) {
            LogUtils.e(TAG, exp.getLocalizedMessage(), exp);
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private static String loadFromDisk(String fileName) {
        String content = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(fileName);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            content = new String(buffer, "UTF-8");
        } catch (FileNotFoundException e) {
            LogUtils.d(TAG, "loadFromDisk() file " + fileName + " not exists");
        } catch (IOException exp) {
            LogUtils.e(TAG, exp.getLocalizedMessage(), exp);
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
            }
        }
        return content;
    }


    private static String getApiCacheDir() {
        Context context = getContext();
        File cacheDir = context.getCacheDir();
        String cacheDirPath = cacheDir.toString();
        String versionDir = getVersionDir();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(cacheDirPath)
                .append("/")
                .append(versionDir);

        return stringBuffer.toString();
    }

    public static float getApiCacheTotalSize() {
        File file = new File(getApiCacheDir());
        return StorageUtils.getFileDirectoryTotalMB(file);
    }

    public static void clearAllApiCache() {
        File file = new File(getApiCacheDir());
        StorageUtils.deleteFileDir(file, false);
    }

    private static String getVersionDir() {
        if (null != sVersionDir) {
            return sVersionDir;
        }
        PhoneStatusManager phone = PhoneStatusManager.getInstance();
        String versionName = phone.getAppVersionName();

        sVersionDir = new String(API_CACHE_DIR_PREFIX + versionName);

        return sVersionDir;
    }

    private static String getCacheFilePath(String url, HashMap<String, String> params) {
        String cacheDir = getApiCacheDir();
        String fileName = getFileName(url, params);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(cacheDir)
                .append("/")
                .append(fileName);

        return stringBuffer.toString();
    }

    private static String getFileName(String url, HashMap<String, String> params) {
        String prefix = MD5Utils.getStringMd5(url);

        if (null == prefix) {
            prefix = url.replace('/', '_');
        }

        StringBuffer stringBuffer = new StringBuffer();
        try {
            ArrayList<String> list = new ArrayList(params.keySet());
            Collections.sort(list);
            for (String key : list) {
                stringBuffer.append(key);
                stringBuffer.append(params.get(key));
            }
        } catch (Exception e) {
        }
        String paramsStr = stringBuffer.toString();
        if (null == paramsStr) {
            paramsStr = "none";
        }

        String suffix = Long.toHexString(MD5Utils.getCRC32(paramsStr));

        return prefix + "_" + suffix;
    }

    private static File[] findAllCacheFiles() {
        String cacheDirPath = getApiCacheDir();
        File cacheDir = new File(cacheDirPath);
        File[] cacheFiles = cacheDir.listFiles();
        return cacheFiles;
    }

    private static ArrayList<File> findOldCacheDirs() {
        Context context = getContext();
        File appCacheDir = context.getCacheDir();

        File[] apiCacheDirs = appCacheDir.listFiles();
        ArrayList<File> oldCacheDirs = new ArrayList<File>();
        for (int i = 0; i < apiCacheDirs.length; i++) {
            String dirName = apiCacheDirs[i].getName();
            if (getVersionDir().equals(dirName)) {
                continue;
            }
            if (!dirName.startsWith(API_CACHE_DIR_PREFIX)) {
                continue;
            }
            oldCacheDirs.add(apiCacheDirs[i]);
        }

        return oldCacheDirs;
    }

    private static boolean shouldBeRecycled(File file) {
        long currTime = System.currentTimeMillis();
        long expireTime = file.lastModified() + MAX_CACHE_FILE_LIFE_CYCLE;

        return currTime >= expireTime;
    }
}
