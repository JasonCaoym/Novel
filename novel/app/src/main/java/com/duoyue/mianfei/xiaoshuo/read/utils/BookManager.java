package com.duoyue.mianfei.xiaoshuo.read.utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class BookManager {
    private String chapterName;
    private String bookId;
    private long chapterLen;
    private long position;
    private Map<String, Cache> cacheMap = new HashMap<>();
    private static volatile BookManager sInstance;

    public static BookManager getInstance() {
        if (sInstance == null) {
            synchronized (BookManager.class) {
                if (sInstance == null) {
                    sInstance = new BookManager();
                }
            }
        }
        return sInstance;
    }

    public boolean openChapter(String bookId, String chapterName) {
        return openChapter(bookId, chapterName, 0);
    }

    public boolean openChapter(String bookId, String chapterName, long position) {
        File file = new File(ReadConstant.BOOK_CACHE_PATH + bookId
                + File.separator + chapterName + FileUtils.SUFFIX_FILE);
        if (!file.exists()) {
            return false;
        }
        this.bookId = bookId;
        this.chapterName = chapterName;
        this.position = position;
        createCache();
        return true;
    }

    private void createCache() {
        if (!cacheMap.containsKey(chapterName)) {
            Cache cache = new Cache();
            File file = getBookFile(bookId, chapterName);
            char[] array = FileUtils.getFileContent(file).toCharArray();
            WeakReference<char[]> charReference = new WeakReference<char[]>(array);
            cache.size = array.length;
            cache.data = charReference;
            cacheMap.put(chapterName, cache);
            chapterLen = cache.size;
        } else {
            chapterLen = cacheMap.get(chapterName).getSize();
        }
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getPosition() {
        return position;
    }

    public String getPrevPara() {
        if (position < 0) {
            return null;
        }

        int end = (int) position;
        int begin = end;
        char[] array = getContent();

        while (begin >= 0) {
            char character = array[begin];
            if ((character + "").equals("\n") && begin != end) {
                position = begin;
                begin++;
                break;
            }
            begin--;
        }
        if (begin < 0) {
            begin = 0;
            position = -1;
        }
        int size = end + 1 - begin;
        return new String(array, begin, size);
    }

    public String getNextPara() {
        if (position >= chapterLen) {
            return null;
        }

        int begin = (int) position;
        int end = begin;
        char[] array = getContent();

        while (end < chapterLen) {
            char character = array[end];
            if ((character + "").equals("\n") && begin != end) {
                ++end;
                position = end;
                break;
            }
            end++;
        }
        int size = end - begin;
        return new String(array, begin, size);
    }

    public char[] getContent() {
        if (cacheMap.size() == 0) {
            return new char[1];
        }
        char[] block = cacheMap.get(chapterName).getData().get();
        if (block == null) {
            File file = getBookFile(bookId, chapterName);
            block = FileUtils.getFileContent(file).toCharArray();
            Cache cache = cacheMap.get(chapterName);
            cache.data = new WeakReference<char[]>(block);
        }
        return block;
    }

    public long getChapterLen() {
        return chapterLen;
    }

    public void clear() {
        cacheMap.clear();
        position = 0;
        chapterLen = 0;
    }

    public static File getBookFile(String folderName, String fileName) {
        return FileUtils.getFile(ReadConstant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_FILE);
    }

    public static long getBookSize(String folderName) {
        return FileUtils.getDirSize(FileUtils
                .getFolder(ReadConstant.BOOK_CACHE_PATH + folderName));
    }

    public static boolean isChapterCached(String folderName, String fileName) {
        File file = new File(ReadConstant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_FILE);
        return file.exists();
    }

    public class Cache {
        private long size;
        private WeakReference<char[]> data;

        public WeakReference<char[]> getData() {
            return data;
        }

        public void setData(WeakReference<char[]> data) {
            this.data = data;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }
}
