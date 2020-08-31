package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;

public class BookSiteBean {

    @SerializedName("suspensionSite")
    private BookCityAdBean suspensionSite;

    public BookCityAdBean getSuspensionSite() {
        return suspensionSite;
    }

    public void setSuspensionSite(BookCityAdBean suspensionSite) {
        this.suspensionSite = suspensionSite;
    }
}
