package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookBannerAdBean {

//    @SerializedName("susperSite")
//    private List<BookCityAdBean> susperSite;

    @SerializedName("bannerSite")
    private List<BookBannerItemBean> bannerSite;
    @SerializedName("iconList")
    private List<BookCityMenuItemBean> iconList;
    @SerializedName("newUserBagStatuses")
    private List<BookNewUserBagStatusesBean> newUserBagStatuses;

//    public List<BookCityAdBean> getSusperSite() {
//        return susperSite;
//    }
//
//    public void setSusperSite(List<BookCityAdBean> susperSite) {
//        this.susperSite = susperSite;
//    }

    public List<BookBannerItemBean> getBannerSite() {
        return bannerSite;
    }

    public void setBannerSite(List<BookBannerItemBean> bannerSite) {
        this.bannerSite = bannerSite;
    }

    public List<BookCityMenuItemBean> getIconList() {
        return iconList;
    }

    public void setIconList(List<BookCityMenuItemBean> iconList) {
        this.iconList = iconList;
    }

    public List<BookNewUserBagStatusesBean> getNewUserBagStatuses() {
        return newUserBagStatuses;
    }

    public void setNewUserBagStatuses(List<BookNewUserBagStatusesBean> newUserBagStatuses) {
        this.newUserBagStatuses = newUserBagStatuses;
    }
}
