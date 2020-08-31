package com.duoyue.app.common.data.request.bookcity;

import com.duoyue.app.common.data.response.bookshelf.BookShelfRecoInfoResp;

import java.util.List;

public class DayRecommendBookBean {

    private List<BookShelfRecoInfoResp> recommendBookList;


    public List<BookShelfRecoInfoResp> getRecommendBookList() {
        return recommendBookList;
    }

    public void setRecommendBookList(List<BookShelfRecoInfoResp> recommendBookList) {
        this.recommendBookList = recommendBookList;
    }
}
