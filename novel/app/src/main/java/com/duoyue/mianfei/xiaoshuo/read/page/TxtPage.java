package com.duoyue.mianfei.xiaoshuo.read.page;

import java.util.List;

public class TxtPage {
    int position;
    String title;
    int titleLines;
    List<String> lines;
    public boolean isExtraAfterBook;
    public boolean isExtraAfterChapter;
    public boolean isExtraChapterEnd;
    public int offsetY;


    private List<String> bottomLines;

    public List<String> getBottomLines() {
        return bottomLines;
    }

    public void setBottomLines(List<String> bottomLines) {
        this.bottomLines = bottomLines;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleLines() {
        return titleLines;
    }

    public void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public boolean isExtraAfterBook() {
        return isExtraAfterBook;
    }

    public void setExtraAfterBook(boolean extraAfterBook) {
        isExtraAfterBook = extraAfterBook;
    }

    public boolean isExtraAfterChapter() {
        return isExtraAfterChapter;
    }

    public void setExtraAfterChapter(boolean extraAfterChapter) {
        isExtraAfterChapter = extraAfterChapter;
    }
}
