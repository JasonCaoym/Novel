package com.zydm.base.utils;

import java.io.*;

public class FileUtil {
    @SuppressWarnings("unused")
    private static final String TAG = "FileUtil";
    private static final String OFFLINE_FOLDER = "offline";
    private static final String OFFLINE_TEMP_FOLDER = "temp";
    private static final String OFFLINE_ROOT_FOLDER_PATH = "offline_folder_path";

    public static boolean deleteDir(String dirPath) {
        File file = new File(dirPath);
        return deleteDir(file);
    }

    public static boolean deleteDir(File dir) {
        if (!dir.exists()) {
            return true;
        }
        if (dir.isFile()) {
            return dir.delete();
        }
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            boolean isDeleteSuccess = true;
            for (File file : children) {
                if (StringUtils.equalsIgnoreCase(OFFLINE_TEMP_FOLDER, file.getName())) {
                    continue;
                }
                if (!deleteDir(file)) {
                    isDeleteSuccess = false;
                }
            }
            return isDeleteSuccess && dir.delete();
        }
        return dir.delete();
    }

    private static void copyDir(File srcFile, File desFile) {
        copyDir(srcFile.getAbsolutePath(), desFile.getAbsolutePath());
    }

    public static boolean copyDir(String srcPath, String desPath) {
        File srcFile = new File(srcPath);
        File desFile = new File(desPath);
        if (!desFile.exists()) {
            desFile.mkdirs();
        }
        // 获取源文件夹当前下的文件或目录
        File[] files = srcFile.listFiles();
        File tempFile = null;
        for (int i = 0; i < files.length; i++) {
            if (StringUtils.equalsIgnoreCase(OFFLINE_TEMP_FOLDER,files[i].getName())){
                continue;
            }
            if (desPath.endsWith(File.separator)) {
                tempFile = new File(desPath + files[i].getName());
            } else {
                tempFile = new File(desPath + File.separator + files[i].getName());
            }
            if (files[i].isFile()) {
                if (!copyFile(files[i], tempFile)) {
                    return false;
                }
            }
            if (files[i].isDirectory()) {
                String sourceDir = srcPath + File.separator + files[i].getName();
                String targetDir = desPath + File.separator + files[i].getName();
                if (!copyDir(sourceDir, targetDir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static long getFileSizeB(String path) {
        File file = new File(path);
        return getFileSizeB(file);
    }

    public static long getFileSizeB(File file) {
        if (!file.exists()) {
            return 0;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            int sizeB = 0;
            for (File fileSub : children) {
                sizeB += getFileSizeB(fileSub);
            }
            return sizeB;
        }
        return file.length();
    }

    public static boolean setLastModified(File file, long time) {
        return file.setLastModified(time);
    }

    public static boolean renameTo(File file, File aimFile) {
        return file.renameTo(aimFile);
    }

    public static boolean mkdirs(File file) {
        return file.mkdirs();
    }

    public static boolean createNewFile(File file) throws IOException {
        return file.createNewFile();
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                deleteDir(destFile);
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
                return true;
            } finally {
                try {
                    out.flush();
                } catch (IOException e) {
                }
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean delete(File file) {
        return deleteDir(file);
    }

    public static boolean delete(String filePath) {
        return deleteDir(filePath);
    }

}
