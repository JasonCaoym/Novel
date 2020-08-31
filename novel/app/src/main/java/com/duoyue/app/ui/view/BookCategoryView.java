package com.duoyue.app.ui.view;

import com.duoyue.app.bean.BookCategoryListBean;

public interface BookCategoryView {

    void showError();
    void updateCategory(BookCategoryListBean maleList, BookCategoryListBean femaleList);

}
