package com.duoyue.app.event;

public class TabSwitchEvent
{
    private int tab;

    /**
     * 书城入口：5：书架空状态进入； 7：书架列表添加按钮进入
     */
    private int entrance;

    public TabSwitchEvent(int tab)
    {
        this.tab = tab;
    }

    /**
     * @param entrance - 书城入口：5：书架空状态进入； 7：书架列表添加按钮进入
     */
    public TabSwitchEvent(int tab, int entrance)
    {
        this.tab = tab;
        this.entrance = entrance;
    }

    public int getEntrance() {
        return entrance;
    }

    public int getTab()
    {
        return tab;
    }
}
