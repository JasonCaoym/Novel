package com.duoyue.mianfei.xiaoshuo.read.page;


import com.zydm.base.data.dao.IChapter;

public class TxtChapter implements IChapter {
    String bookId;
    String link;
    String title;
    long start;
    long end;
    public int chapterId;
    public int seqNum;
    boolean isSelect;
    public boolean isRead;
    public boolean isDownload;
    @Override
    public boolean isSelect() {
        return isSelect;
    }

    @Override
    public boolean isRead() {
        return isRead;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
