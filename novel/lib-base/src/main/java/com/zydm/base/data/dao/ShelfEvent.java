package com.zydm.base.data.dao;


import java.util.ArrayList;

public class ShelfEvent {
    public static final int TYPE_REMOVE = 0;
    public static final int TYPE_ADD = 1;
    public static final int TYPE_UPDATE = 3;

    public ArrayList<BookShelfBean> mChangeList = new ArrayList();
    public int mType = TYPE_REMOVE;


}