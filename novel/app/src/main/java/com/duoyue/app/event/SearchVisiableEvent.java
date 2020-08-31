package com.duoyue.app.event;

public class SearchVisiableEvent {

    private boolean visiable;
    private int offset;

    public SearchVisiableEvent(boolean visiable, int offset) {
        this.visiable = visiable;
        this.offset = offset;
    }

    public boolean isVisiable() {
        return visiable;
    }

    public int getOffset() {
        return offset;
    }
}
