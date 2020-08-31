package com.duoyue.app.bean;

import com.stx.xhb.xbanner.entity.SimpleBannerInfo;

public class BannerBean extends SimpleBannerInfo {

    private BookBannerItemBean bannerBean;

    public BannerBean(BookBannerItemBean bannerBean) {
        this.bannerBean = bannerBean;
    }

    public BookBannerItemBean getBanner() {
        return bannerBean;
    }

    @Override
    public Object getXBannerUrl() {
        if (bannerBean != null) {
            return bannerBean.getCover();
        } else {
            return "";
        }
    }

}
