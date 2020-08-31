package com.duoyue.app.bean;

import java.util.List;

public class CategoryBookListBean {

    private int nextPage;
    private List<CategoryBookBean> list;

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public List<CategoryBookBean> getList() {
        return list;
    }

    public void setList(List<CategoryBookBean> list) {
        this.list = list;
    }
}
