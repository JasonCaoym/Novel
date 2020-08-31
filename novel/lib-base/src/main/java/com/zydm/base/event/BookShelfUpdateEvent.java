package com.zydm.base.event;

public class BookShelfUpdateEvent {

    private int type;

    public BookShelfUpdateEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
