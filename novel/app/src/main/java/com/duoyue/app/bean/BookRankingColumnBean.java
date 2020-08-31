package com.duoyue.app.bean;

import kotlinx.android.parcel.Parcelize;

import java.util.List;
@Parcelize
public class BookRankingColumnBean {



    private BookCityModuleBean bookCityModuleBean;


    public BookCityModuleBean getBookCityModuleBean() {
        return bookCityModuleBean;
    }

    public void setBookCityModuleBean(BookCityModuleBean bookCityModuleBean) {
        this.bookCityModuleBean = bookCityModuleBean;
    }
//    private List<BookRankingBooksListBean> rankingBooksList;
//
//    public List<BookRankingBooksListBean> getRankingBooksList() {
//        return rankingBooksList;
//    }
//
//    public void setRankingBooksList(List<BookRankingBooksListBean> rankingBooksList) {
//        this.rankingBooksList = rankingBooksList;
//    }

}
