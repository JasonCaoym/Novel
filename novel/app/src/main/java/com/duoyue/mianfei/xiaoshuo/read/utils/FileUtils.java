package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.os.Environment;
import com.zydm.base.common.BaseApplication;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static final String SUFFIX_FILE = ".zydm";

    public static File getFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static synchronized File getFile(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                getFolder(file.getParent());
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getCachePath() {
        if (isSdCardExist() && BaseApplication.context.globalContext.getExternalCacheDir() != null) {
            return BaseApplication.context.globalContext.getExternalCacheDir().getAbsolutePath();
        } else {
            return BaseApplication.context.globalContext.getCacheDir().getAbsolutePath();
        }
    }

    public static long getDirSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                return file.length();
            }
        } else {
            return 0;
        }
    }

    public static String getFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "kb", "M", "G", "T"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getFileContent(File file) {
        Reader reader = null;
        String str = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (!str.equals("")) {
                    sb.append("    " + str + "\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return sb.toString();
    }

    public static boolean isSdCardExist() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static synchronized void deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return;

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                String path = subFile.getPath();
                deleteFile(path);
            }
        }
        file.delete();
    }

    public static List<File> getTxtFiles(String filePath) {
        final List txtFiles = new ArrayList();
        File file = new File(filePath);
        File[] dirs = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory() && !pathname.getName().startsWith(".")) {
                    return true;
                }
                else if (pathname.getName().endsWith(".txt")) {
                    txtFiles.add(pathname);
                    return false;
                } else {
                    return false;
                }
            }
        });
        for (File dir : dirs) {
            txtFiles.addAll(getTxtFiles(dir.getPath()));
        }
        return txtFiles;
    }

    public static Single<List<File>> getSDTxtFile() {
        final String rootPath = Environment.getExternalStorageDirectory().getPath();
        return Single.create(new SingleOnSubscribe<List<File>>() {
            @Override
            public void subscribe(SingleEmitter<List<File>> e) throws Exception {
                List<File> files = getTxtFiles(rootPath);
                e.onSuccess(files);
            }
        });
    }

    public static Charset getCharset(String fileName) {
        BufferedInputStream bis = null;
        Charset charset = Charset.GBK;
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(fileName));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = Charset.UTF8;
                checked = true;
            }
            bis.mark(0);
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = Charset.UTF8;
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(bis);
        }
        return charset;
    }
}
