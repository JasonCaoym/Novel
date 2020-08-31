package com.duoyue.mianfei.xiaoshuo.read.utils;

import android.os.Parcel;
import com.duoyue.app.bean.AllChapterDownloadBean;
import com.duoyue.app.bean.BookDetailBean;
import com.duoyue.app.common.data.response.bookdownload.AllChapterDownloadResp;
import com.duoyue.lib.base.log.Logger;

import java.io.*;
import java.util.List;

public class BookSaveUtils {

    public static final String BOOK_DETAIL_BEAN = "BookDetailBean";
    public static final String ALL_CHAPTER = "allChapter";

    public static void saveChapterInfo(String folderName, String fileName, String content) {
        File file = BookManager.getBookFile(folderName, fileName);
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
            Logger.w("App#ReadActivity", "书籍"+ fileName +"保存成功");
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

    /**
     * 保存书籍详情对象到本地文件，用于离线阅读
     *
     * @param bookId
     * @param bookDetailBean
     */
    public static void saveBookDetailBean(String bookId, BookDetailBean bookDetailBean) {

        if (bookDetailBean == null) {
            return;
        }
        byte[] bytes = ParcelableUtil.marshall(bookDetailBean);
        File file = BookManager.getBookFile(bookId, BOOK_DETAIL_BEAN);

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存书籍所有目录到本地文件，用于离线阅读
     *
     * @param bookId
     * @param allChapterDownloadResp
     */
    public static void saveAllChapter(String bookId, AllChapterDownloadResp allChapterDownloadResp) {

        if (allChapterDownloadResp == null) {
            return;
        }
        byte[] bytes = ParcelableUtil.marshall(allChapterDownloadResp);
        File file = BookManager.getBookFile(bookId, ALL_CHAPTER);

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取书籍详情的缓存
     *
     * @return
     */
    public static BookDetailBean getCacheBookDetailBean(String bookId) {
        File file = BookManager.getBookFile(bookId, BOOK_DETAIL_BEAN);
        if (file.exists()) {

            InputStream inputStream = null;

            try {
                inputStream = new FileInputStream(file);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                Parcel parcel = ParcelableUtil.unmarshall(bytes);
                BookDetailBean bookDetailBean = BookDetailBean.CREATOR.createFromParcel(parcel);
                return bookDetailBean;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取书籍全部章节的缓存
     *
     * @return
     */
    public static AllChapterDownloadResp getCacheAllChapter(String bookId) {
        File file = BookManager.getBookFile(bookId, ALL_CHAPTER);
        if (file.exists()) {

            InputStream inputStream = null;

            try {
                inputStream = new FileInputStream(file);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                Parcel parcel = ParcelableUtil.unmarshall(bytes);
                AllChapterDownloadResp allChapterDownloadResp = AllChapterDownloadResp.CREATOR.createFromParcel(parcel);
                return allChapterDownloadResp;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static boolean isCached(String folderName, String fileName) {
        File file = new File(ReadConstant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_FILE);
        return file.exists();
    }
}
