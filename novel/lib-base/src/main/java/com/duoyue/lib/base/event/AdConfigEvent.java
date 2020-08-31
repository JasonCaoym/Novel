package com.duoyue.lib.base.event;

public class AdConfigEvent {
    private boolean isDateUpdate;

    public AdConfigEvent(boolean isDateUpdate) {
        this.isDateUpdate = isDateUpdate;
    }

    public boolean isDateUpdate() {
        return isDateUpdate;
    }
}
