package com.duoyue.app.bean;

import java.util.List;

public class BookBannerListBean {

    /**
     * 0：精选
     * 1：男生
     * 2：女生
     */
    private int type;

    private List<BookBannerItemBean> list;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<BookBannerItemBean> getList() {
        return list;
    }

    public void setList(List<BookBannerItemBean> list) {
        this.list = list;
    }
}
