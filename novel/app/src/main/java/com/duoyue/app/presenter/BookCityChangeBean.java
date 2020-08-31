package com.duoyue.app.presenter;

import android.util.SparseArray;

import java.util.List;

/*
 * 上报所有id
 * */
public class BookCityChangeBean {

    private SparseArray<SparseArray<List<Long>>> sparseArray = new SparseArray<>();

    private static BookCityChangeBean bookCityChangeBean;

    public static BookCityChangeBean getInstance() {
        if (bookCityChangeBean == null) {
            synchronized (BookCityChangeBean.class) {
                if (bookCityChangeBean == null) {
                    bookCityChangeBean = new BookCityChangeBean();
                }
            }
        }
        return bookCityChangeBean;
    }

    public SparseArray<SparseArray<List<Long>>> getSparseArray() {
        return sparseArray;
    }

    public void onDestroy() {
        if (sparseArray != null) {
            sparseArray.clear();
            sparseArray = null;
        }
        if (bookCityChangeBean != null) {
            bookCityChangeBean = null;
        }
    }

}
