package com.duoyue.mod.ad.bean;

public class AdShowParamsBean {
    private int showNum;
    private long showTime;

    public AdShowParamsBean(int showNum, long showTime) {
        this.showNum = showNum;
        this.showTime = showTime;
    }

    public int getShowNum() {
        return showNum;
    }


    public long getShowTime() {
        return showTime;
    }

}
