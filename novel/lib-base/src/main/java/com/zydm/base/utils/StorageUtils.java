package com.zydm.base.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.common.Constants;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class StorageUtils {
    private static final String STORAGE_PATH = "storage_path";
    private static final String OLD_SDCARD_1_PATH = "/mnt/sdcard";
    private static final String OLD_SDCARD_2_PATH = "/mnt/sdcard2";
    private static final String NEW_SDCARD_1_PATH = "/storage/sdcard0";
    private static final String NEW_SDCARD_2_PATH = "/storage/sdcard1";
    private static final String OFFLINE_INTERNAL_ROOT_FOLDER_PATH = "offline_internal_folder_path";
    public static final String OFFLINE_EXTERNAL_ROOT_FOLDER_PATH = "offline_external_folder_path";
    private static final String OFFLINE_FOLDER = "offline";
    private static String sRootFolder = "mtwl";
    private static String sSDCardDir;
    private static final String DEFAULT_INTERNAL_RELATIVE_PATH = "/Android/data/motong/files/Pictures";

    static {
        init();
    }

    private static void init() {
        String path = SPUtils.INSTANCE.getString(STORAGE_PATH);
        if (TextUtils.isEmpty(path) || !isSDCard2Exsit()) {
            sSDCardDir = getRootPath();
        } else {
            sSDCardDir = path;
        }
    }

    public static boolean isSDCard2Exsit() {
        if (!StorageUtils.isSDCardMounted()) {
            return false;
        }

        String sdcardPath;
        if (isOldAndroidSystem()) {
            sdcardPath = OLD_SDCARD_2_PATH;
        } else {
            sdcardPath = NEW_SDCARD_2_PATH;
        }

        File sdcard2 = new File(sdcardPath);
        return sdcard2 != null && sdcard2.exists() && (getSDCardBlockCount(sdcardPath) != 0);
    }

    private static boolean isOldAndroidSystem() {
        return OLD_SDCARD_1_PATH.equals(getRootPath());
    }

    @SuppressWarnings("deprecation")
    private static int getSDCardBlockCount(String sdcardDir) {
        try {
            StatFs stat = new StatFs(sdcardDir);
            return stat.getBlockCount();
        } catch (Exception e) {
        }
        return 0;
    }

    public static String getAnotherHomeDir() {
        if (!isSDCard2Exsit()) {
            return null;
        }

        String sdCardDir = sSDCardDir;
        if (isOldAndroidSystem()) {
            sdCardDir = sdCardDir.equals(OLD_SDCARD_1_PATH) ? OLD_SDCARD_2_PATH : OLD_SDCARD_1_PATH;
        } else {
            sdCardDir = sdCardDir.equals(NEW_SDCARD_1_PATH) ? NEW_SDCARD_2_PATH : NEW_SDCARD_1_PATH;
        }

        return sdCardDir + File.separator + sRootFolder;
    }

    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static void onMediaChange() {
        sSDCardDir = getRootPath();
    }

    public static String getSDCardDir() {
        if (isSDCardMounted()) {
            return sSDCardDir;
        }
        return null;
    }

    public static String getRootDir() {
        return sRootFolder;
    }

    public static boolean isSDCardChange() {
        String rootPath = getRootPath();
        return !rootPath.equals(sSDCardDir);
    }

    private static String getRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getHomeDirAbsolute() {
        if (isSDCardMounted()) {
            String retPath = sSDCardDir + File.separator + sRootFolder;
            File file = new File(retPath);
            if (!file.exists()) {
                FileUtil.mkdirs(file);
            }
            return retPath;
        }
        return null;
    }

    public static float getFileDirectoryTotalMB(File fileDirectory) {

        if (null == fileDirectory || !fileDirectory.isDirectory()) {
            return 0f;
        }
        File[] files = fileDirectory.listFiles();
        if (null == files) {
            return 0f;
        }
        float total = 0f;
        for (File file : files) {
            total += file.length();
        }
        return total / Constants.MB;
    }

    public static void deleteFileDir(File fileDir, boolean isNeedDeleteHideFile) {
        if (null == fileDir) {
            return;
        }

        if (!fileDir.exists() || fileDir.isFile()) {
            return;
        }
        recursionDeleteFile(fileDir, isNeedDeleteHideFile);
    }

    private static void recursionDeleteFile(File fileDir, boolean isNeedDeleteHideFile) {
        if (fileDir == null || !fileDir.isDirectory()) {
            return;
        }
        File[] childFiles = fileDir.listFiles();
        if (childFiles == null) {
            return;
        }
        for (File file : childFiles) {
            if (file.isDirectory()) {
                recursionDeleteFile(file, isNeedDeleteHideFile);
            } else {
                if (isNeedDeleteHideFile || !file.isHidden()) {
                    FileUtil.delete(file);
                }
            }
        }
    }

    public static String formatSize(long sizeB) {
        float size = sizeB;
        float value;
        String unit;
        if (size > Constants.GB) {
            value = size / Constants.GB;
            unit = Constants.G;
        } else if (size > Constants.MB) {
            value = size / Constants.MB;
            unit = Constants.M;
        } else if (size > Constants.KB) {
            value = size / Constants.KB;
            unit = Constants.K;
        } else {
            value = size;
            unit = Constants.B;
        }
        if (sizeB == 0) {
            unit = Constants.M;
        }
        BigDecimal decimal = new BigDecimal(value);
        value = decimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return value + unit;
    }

    public static String getExternalSdCardPath() {
        if (!hasExternalSdCard()) {
            return null;
        }
        String path = "";
        path = SPUtils.INSTANCE.getString(OFFLINE_EXTERNAL_ROOT_FOLDER_PATH);
        if (StringUtils.isBlank(path)) {
            path = getStoragePath(BaseApplication.context.globalContext, true);
            if (!StringUtils.isBlank(path)) {
                String internalPath = getInternalStoragePath();
                int subStartIndex = internalPath.indexOf("Android");
                String absPath = internalPath.substring(subStartIndex);
                path = new StringBuilder().append(path).append(File.separator).append(absPath).toString();
                SPUtils.INSTANCE.putString(OFFLINE_EXTERNAL_ROOT_FOLDER_PATH, path);
            }
        }
        return path;
    }

    public static String getInternalStoragePath() {
        String path = "";
        path = SPUtils.INSTANCE.getString(OFFLINE_INTERNAL_ROOT_FOLDER_PATH);
        if (StringUtils.isBlank(path)) {
            try {
                path = BaseApplication.context.globalContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
                if (!StringUtils.isBlank(path)) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(path)
                            .append(File.separator)
                            .append(OFFLINE_FOLDER);
                    path = builder.toString();
                    SPUtils.INSTANCE.putString(OFFLINE_INTERNAL_ROOT_FOLDER_PATH, path);
                }
            } catch (Throwable e) {

            }
        }
        return path;
    }

    public static boolean hasExternalSdCard() {
        String externalPath = getStoragePath(BaseApplication.context.globalContext, true);
        if (StringUtils.isBlank(externalPath)){
            return false;
        }
        if (StringUtils.equalsIgnoreCase(android.os.Environment.getExternalStorageState(), Environment.MEDIA_UNMOUNTED)) {
            return false;
        }
        File externalFile = new File(externalPath);
        if (!externalFile.exists()){
            externalFile.mkdirs();
        }
        long size = externalFile.getTotalSpace();
        LogUtils.d("sdcard","externalPath = "+externalPath+",size = "+size);
        return size != 0;

    }

    private static String getStoragePath(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = null;
            try {
                result = getVolumeList.invoke(mStorageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
