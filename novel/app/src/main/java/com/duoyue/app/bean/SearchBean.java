package com.duoyue.app.bean;

import java.util.List;

public class SearchBean {


    public static final int TYME_ONE = 1;
    public static final int TYME_TWO = 2;
    public static final int TYME_THREE = 3;


    private int type;
    private SearchV2ListBean searchV2ListBean;
    private SearchV2MoreListBean searchV2MoreListBean;
    private List<String> stringList;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SearchV2ListBean getSearchV2ListBean() {
        return searchV2ListBean;
    }

    public void setSearchV2ListBean(SearchV2ListBean searchV2ListBean) {
        this.searchV2ListBean = searchV2ListBean;
    }

    public SearchV2MoreListBean getSearchV2MoreListBean() {
        return searchV2MoreListBean;
    }

    public void setSearchV2MoreListBean(SearchV2MoreListBean searchV2MoreListBean) {
        this.searchV2MoreListBean = searchV2MoreListBean;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
