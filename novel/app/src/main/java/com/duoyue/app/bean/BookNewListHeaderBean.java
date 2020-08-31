package com.duoyue.app.bean;

import java.util.List;

public class BookNewListHeaderBean {


    private List<BookNewHeaderBean> list;

    private int nextPage;


    public List<BookNewHeaderBean> getList() {
        return list;
    }

    public void setList(List<BookNewHeaderBean> list) {
        this.list = list;
    }


    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }
}
