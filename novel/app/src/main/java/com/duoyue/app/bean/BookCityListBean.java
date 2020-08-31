package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;
import kotlinx.android.parcel.Parcelize;

import java.util.List;

@Parcelize
public class BookCityListBean {

    @SerializedName("columns")
    private List<BookCityModuleBean> list;

    @SerializedName("searchTitle")
    private String searchTitle;
//    @SerializedName("rankingColumn")
//    private BookRankingColumnBean rankingColumn;

    public List<BookCityModuleBean> getList() {
        return list;
    }

    public void setList(List<BookCityModuleBean> list) {
        this.list = list;
    }

//    public static class BookOne2FourBean {
//        public BookCityModuleBean moduleBean;
//    }
//
//    public static class BookThreeBean {
//        public BookCityModuleBean moduleBean;
//    }
//
//    public static class BookOne2DoubleBean {
//        public BookCityModuleBean moduleBean;
//    }


    public String getSearchTitle() {
        return searchTitle;
    }

    public void setSearchTitle(String searchTitle) {
        this.searchTitle = searchTitle;
    }

//    public BookRankingColumnBean getRankingColumn() {
//        return rankingColumn;
//    }
//
//    public void setRankingColumn(BookRankingColumnBean rankingColumn) {
//        this.rankingColumn = rankingColumn;
//    }
}
