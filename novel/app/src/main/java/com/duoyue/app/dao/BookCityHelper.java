package com.duoyue.app.dao;

import com.duoyue.app.dao.gen.DaoSession;

public class BookCityHelper {

    private static final String TAG = "app#BookCityHelper";
    private static volatile BookCityHelper sInstance;
    private static DaoSession daoSession;


    public static BookCityHelper getsInstance() {
        if (sInstance == null) {
            synchronized (BookCityHelper.class) {
                if (sInstance == null) {
                    sInstance = new BookCityHelper();
                    daoSession = AppDaoDbHelper.getInstance().getSession();
                }
            }
        }
        return sInstance;
    }

}
