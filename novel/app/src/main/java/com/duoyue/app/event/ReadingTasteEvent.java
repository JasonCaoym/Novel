package com.duoyue.app.event;

/**
 * 阅读口味设置事件.
 */
public class ReadingTasteEvent
{
    /**
     * 性别(1:男生;2:女生)
     */
    private long mSex;

    public ReadingTasteEvent(long sex) {
        mSex = sex;
    }

    public long getSex() {
        return mSex;
    }
}
