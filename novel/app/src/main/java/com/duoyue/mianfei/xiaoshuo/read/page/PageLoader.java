package com.duoyue.mianfei.xiaoshuo.read.page;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.duoyue.lib.base.io.IOUtil;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mianfei.xiaoshuo.read.utils.FileUtils;
import com.duoyue.mianfei.xiaoshuo.read.utils.ReadConstant;
import com.duoyue.mod.ad.dao.AdReadConfigHelp;
import com.duoyue.mod.ad.utils.AdConstants;
import com.duoyue.mod.stats.ErrorStatsApi;
import com.zydm.base.data.dao.BookRecordBean;
import com.zydm.base.data.dao.ChapterBean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PageLoader extends AbsPageLoader {

    private static final String TAG = "App#PageLoader";

    /**
     * 向上预加载章节数
     */
    private int mPreCount = 1;
    /**
     * 向下预加载章节数
     */
    private int mNextCount = 20;

    PageLoader(FragmentActivity activity, PageView pageView, String prevPageId, String sourceStats, String bookId) {
        super(activity, pageView, prevPageId, sourceStats,bookId);
        mPreCount = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.RD_PRELOAD_BACK, 1);
        mNextCount = AdReadConfigHelp.getsInstance().getValueByKey(AdConstants.ReadParams.RD_PRELOAD, 20);
    }

    @Override
    public void openBook(BookRecordBean recordBean) {
        super.openBook(recordBean);
        isBookOpen = false;
        if (recordBean.bookChapterList == null) {
            Logger.e("App#ReadActivity", "书籍章节信息为空： bookChapterList == null");
            ErrorStatsApi.addError(ErrorStatsApi.LOAD_RD_FAIL, "PageLoader.openBook(bookChapterList is null, " + recordBean.getBookId() + ")");
            return;
        }
        mChapterList = convertTxtChapterGroup(recordBean.bookChapterList);
        Logger.e("App#ReadActivity", "openBook -- recordBean.bookChapterList.size = "
                + (recordBean.bookChapterList == null? "null" : recordBean.bookChapterList.size())
                + ", mChapterList = " + (mChapterList==null?"null": mChapterList.size()));
        if (mPageChangeListener != null) {
            mPageChangeListener.onChaptersConverted(mChapterList, mCurGroupPos, mCurGroupPos);
        }
        loadCurrentChapter();
    }

    @Override
    public void onChaptersGroupUpdate(int groupPos) {
        List<ChapterBean> chapterBeans = mRecordBook.bookChapterList.get(groupPos);
        List<TxtChapter> txtChapters = convertTxtChapters(chapterBeans);
        mChapterList.set(groupPos, txtChapters);

        if (mPageChangeListener != null) {
            mPageChangeListener.onChaptersConverted(mChapterList, mCurGroupPos, groupPos);
        }
    }

    private List<List<TxtChapter>> convertTxtChapterGroup(List<List<ChapterBean>> bookChapters) {
        List<List<TxtChapter>> txtChapters = new ArrayList<>(bookChapters.size());
        for (List<ChapterBean> group : bookChapters) {
            List<TxtChapter> sub = convertTxtChapters(group);
            txtChapters.add(sub);
        }
        return txtChapters;
    }

    @NonNull
    private List<TxtChapter> convertTxtChapters(List<ChapterBean> group) {
        List<TxtChapter> sub = new ArrayList<>();
        for (ChapterBean bean : group) {
            TxtChapter chapter = new TxtChapter();
            chapter.bookId = bean.getBookId();
            chapter.title = bean.getChapterTitle();
            chapter.seqNum = bean.getSeqNum();
            chapter.chapterId = bean.getChapterId();
            chapter.isRead = bean.isRead;
            chapter.isDownload = bean.isDownload;
            sub.add(chapter);
        }
        return sub;
    }

    @Nullable
    @Override
    protected List<TxtPage> loadPageList(int groupPos, int chapterPos) {
        if (mChapterList == null || mChapterList.isEmpty()) {
            Logger.e("ReadActivity", "loadPageList 没有章节数据: mCurGroupPos = " + mCurGroupPos + ", mCurPos = " + mCurPos);
            return null;
        }

        List<TxtChapter> chapterList = mChapterList.get(groupPos);
        if (chapterPos >= chapterList.size()) {
            chapterPos = chapterList.size() - 1;
            mCurPos = chapterPos;
        }
        TxtChapter txtChapter = chapterList.get(chapterPos);
        File file = new File(ReadConstant.BOOK_CACHE_PATH + mRecordBook.getBookId()
                + File.separator + txtChapter.chapterId + FileUtils.SUFFIX_FILE);
        if (!file.exists()) return null;

        Reader reader = null;
        BufferedReader br = null;
        try {
            reader = new FileReader(file);
            br = new BufferedReader(reader);
            return loadPages(txtChapter, br, groupPos, chapterPos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(reader);
            IOUtil.close(br);
        }
        return null;
    }

    @Override
    boolean preChapter() {
        boolean hasPrev = super.preChapter();
        if (!hasPrev) return false;

        if (mStatus == STATUS_FINISH) {
            loadPreChapter();
            return true;
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
            return false;
        }
        return false;
    }

    @Override
    boolean nextChapter() {
        boolean hasNext = super.nextChapter();
        if (!hasNext) return false;

        if (mStatus == STATUS_FINISH) {
            loadNextChapter();
            return true;
        } else if (mStatus == STATUS_LOADING) {
            loadCurrentChapter();
            return false;
        }
        return false;
    }

    @Override
    public void skipToChapter(int groupPos, int chapterPos) {
        super.skipToChapter(groupPos, chapterPos);
        // 解决上下滚动广告不消失问题
        if (mPageView.getNextPage() != null) {
            mPageView.getNextPage().isExtraAfterBook = false;
            mPageView.getNextPage().isExtraAfterChapter = false;
            mPageView.getNextPage().isExtraChapterEnd = false;
        }
        if (mPageView.getBgBitmap() != null) {
            mPageView.getBgBitmap().isExtraAfterBook = false;
            mPageView.getBgBitmap().isExtraAfterChapter = false;
            mPageView.getBgBitmap().isExtraChapterEnd = false;
        }
        loadCurrentChapter();
    }

    private void loadCurrentChapter() {
        List<TxtChapter> bookChapters = new ArrayList<>(mPreCount + mNextCount + 1);
        int currentPos = mCurPos;
        int currentGroupPos = mCurGroupPos;
        List<TxtChapter> currentGroup = mChapterList.get(currentGroupPos);
        List<TxtChapter> preGroup = currentGroupPos == 0 ? null : mChapterList.get(currentGroupPos - 1);
        List<TxtChapter> nextGroup = currentGroupPos == mChapterList.size() - 1 ? null : mChapterList.get(currentGroupPos + 1);
        if (currentPos >= currentGroup.size()) {
            Logger.e("Read#", "章节跳转数组越界 : currentPos = " + currentPos + ", 章节组长度： " + currentGroup.size());
            currentPos = currentGroup.size() - 1;
        }
        bookChapters.add(currentGroup.get(currentPos));

        //往下加载章节
        if (currentPos < currentGroup.size() - mNextCount) {
            //当前Group中包含了所有需预加载的章节
            int begin = currentPos + 1;
            int next = begin + mNextCount;
            if (next > currentGroup.size()) {
                next = currentGroup.size();
            }
            bookChapters.addAll(currentGroup.subList(begin, next));

        } else if (currentPos == currentGroup.size() - 1) {
            //当前章节已是当前Group的最后一个章节，所有需预加载的章节全部在下一个Group中
            if (nextGroup != null && nextGroup.size() != 0) {
                int begin = 0;
                int next = begin + mNextCount;
                if (next > nextGroup.size()) {
                    next = nextGroup.size();
                }
                bookChapters.addAll(nextGroup.subList(begin, next));
            }
        } else {
            //当前Group中包含了一部分需预加载的章节，另一部分在下一个Group中
            bookChapters.addAll(currentGroup.subList(currentPos + 1, currentGroup.size()));
            //当前Group所有剩余章节数量
            int currentCount = currentGroup.size() - 1 - currentPos;
            if (nextGroup != null && nextGroup.size() != 0) {
                int nextCount = mNextCount - currentCount;
                int begin = 0;
                int next = begin + nextCount;
                if (next > nextGroup.size()) {
                    next = nextGroup.size();
                }
                bookChapters.addAll(nextGroup.subList(begin, next));
            }
        }

        //往上加载章节
        if (currentPos >= mPreCount) {
            //当前Group中包含了所有需预加载的章节
            int prev = currentPos - mPreCount;
            if (prev < 0) {
                prev = 0;
            }
            bookChapters.addAll(currentGroup.subList(prev, currentPos));
        } else if (currentPos == 0) {
            //当前章节是当前Group的第一个章节，所有需预加载的章节全部在上一个Group中
            if (preGroup != null && preGroup.size() != 0) {
                int prev = preGroup.size() - mPreCount;
                if (prev < 0) {
                    prev = 0;
                }
                bookChapters.addAll(preGroup.subList(prev, preGroup.size()));
            }
        } else {
            //当前Group中包含了一部分需预加载的章节，另一部分在上一个Group中
            bookChapters.addAll(currentGroup.subList(0, currentPos));
            int currentCount = currentPos;
            if (preGroup != null && preGroup.size() != 0) {
                int prev = preGroup.size() - currentCount;
                if (prev < 0) {
                    prev = 0;
                }
                bookChapters.addAll(preGroup.subList(prev, preGroup.size()));
            }
        }

        Logger.e(TAG, "loadCurrentChapter -- bookChapters.size():   " + bookChapters.size());
        if (bookChapters.isEmpty())
        {
            //获取章节列表失败.
            ErrorStatsApi.addError(ErrorStatsApi.LOAD_CHAPTER_FAIL, "PageLoader.loadCurrentChapter(BookId:" + (mRecordBook != null ? mRecordBook.getBookId() : "NULL") + ", PreCount:" + mPreCount + ", NextCount:" + mNextCount
                + ", CurPos:" + mCurPos + ", CurGroupPos:" + mCurGroupPos + ", ChapterList:" + mChapterList.size() + ", CurrentGroup:" + (currentGroup != null ? currentGroup.size() : "NULL") + ", PreGroup:"
                + (preGroup != null ? preGroup.size() : "NULL") + ", NextGroup:" + (nextGroup != null ? nextGroup.size() : "NULL") + ")");
        }
        mPageChangeListener.loadChapterContents(bookChapters, mCurGroupPos, mCurPos);
    }

    private void loadPreChapter() {
        List<TxtChapter> bookChapters = new ArrayList<>(mPreCount);
        int currentPos = mCurPos;
        int currentGroupPos = mCurGroupPos;
        List<TxtChapter> currentGroup = mChapterList.get(currentGroupPos);
        List<TxtChapter> preGroup = currentGroupPos == 0 ? null : mChapterList.get(currentGroupPos - 1);

        //往上加载章节
        if (currentPos >= mPreCount) {
            //当前Group中包含了所有需预加载的章节
            int prev = currentPos - mPreCount;
            if (prev < 0) {
                prev = 0;
            }
            bookChapters.addAll(currentGroup.subList(prev, currentPos));
        } else if (currentPos == 0) {
            //当前章节是当前Group的第一个章节，所有需预加载的章节全部在上一个Group中
            if (preGroup != null && preGroup.size() != 0) {
                int prev = preGroup.size() - mPreCount;
                if (prev < 0) {
                    prev = 0;
                }
                bookChapters.addAll(preGroup.subList(prev, preGroup.size()));
            }
        } else {
            //当前Group中包含了一部分需预加载的章节，另一部分在上一个Group中
            bookChapters.addAll(currentGroup.subList(0, currentPos));
            int currentCount = currentPos;
            if (preGroup != null && preGroup.size() != 0) {
                int prev = preGroup.size() - currentCount;
                if (prev < 0) {
                    prev = 0;
                }
                bookChapters.addAll(preGroup.subList(prev, preGroup.size()));
            }
        }

        Logger.e(TAG, "loadPreChapter -- bookChapters.size():   " + bookChapters.size());
        if (bookChapters.isEmpty())
        {
            ErrorStatsApi.addError(ErrorStatsApi.LOAD_CHAPTER_FAIL, "PageLoader.loadPreChapter(BookId:" + (mRecordBook != null ? mRecordBook.getBookId() : "NULL") + ", PreCount:" + mPreCount + ", CurPos:"
                + mCurPos + ", CurGroupPos:" + mCurGroupPos + ", ChapterList:" + mChapterList.size() + ")");
        }
        mPageChangeListener.loadChapterContents(bookChapters, currentGroupPos, currentPos);
    }

    private void loadNextChapter() {
        List<TxtChapter> bookChapters = new ArrayList<>(mNextCount);
        int currentPos = mCurPos;
        int currentGroupPos = mCurGroupPos;
        List<TxtChapter> currentGroup = mChapterList.get(currentGroupPos);
        List<TxtChapter> nextGroup = currentGroupPos == mChapterList.size() - 1 ? null : mChapterList.get(currentGroupPos + 1);

        //往下加载章节
        if (currentPos < currentGroup.size() - mNextCount) {
            //当前Group中包含了所有需预加载的章节
            int begin = currentPos + 1;
            int next = begin + mNextCount;
            if (next > currentGroup.size()) {
                next = currentGroup.size();
            }
            bookChapters.addAll(currentGroup.subList(begin, next));
        } else if (currentPos == currentGroup.size() - 1) {
            //当前章节已是当前Group的最后一个章节，所有需预加载的章节全部在下一个Group中
            if (nextGroup != null && nextGroup.size() != 0) {
                int begin = 0;
                int next = begin + mNextCount;
                if (next > nextGroup.size()) {
                    next = nextGroup.size();
                }
                bookChapters.addAll(nextGroup.subList(begin, next));
            }
        } else {
            //当前Group中包含了一部分需预加载的章节，另一部分在下一个Group中
            bookChapters.addAll(currentGroup.subList(currentPos + 1, currentGroup.size()));
            //当前Group所有剩余章节数量
            int currentCount = currentGroup.size() - 1 - currentPos;
            if (nextGroup != null && nextGroup.size() != 0) {
                int nextCount = mNextCount - currentCount;
                int begin = 0;
                int next = begin + nextCount;
                if (next > nextGroup.size()) {
                    next = nextGroup.size();
                }
                bookChapters.addAll(nextGroup.subList(begin, next));
            }
        }
        Logger.e(TAG, "loadNextChapter -- bookChapters.size():   " + bookChapters.size());
        if (bookChapters.isEmpty())
        {
            ErrorStatsApi.addError(ErrorStatsApi.LOAD_CHAPTER_FAIL, "PageLoader.loadNextChapter(BookId:" + (mRecordBook != null ? mRecordBook.getBookId() : "NULL") + ", NextCount:" + mNextCount + ", CurPos:"
                + mCurPos + ", CurGroupPos:" + mCurGroupPos + ", ChapterList:" + mChapterList.size() + ")");
        }
        mPageChangeListener.loadChapterContents(bookChapters, currentGroupPos, currentPos);
    }
}

