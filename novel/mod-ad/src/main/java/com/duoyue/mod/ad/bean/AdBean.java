package com.duoyue.mod.ad.bean;

import java.util.List;

public class AdBean {

    private List<AdPositionConfigBean> adConfigList;
    private List<AdOriginConfigBean> adOriginConfigList;
    private List<AdReadConfigBean> paramConfigList;

    public List<AdPositionConfigBean> getAdConfigList() {
        return adConfigList;
    }

    public void setAdConfigList(List<AdPositionConfigBean> adConfigList) {
        this.adConfigList = adConfigList;
    }

    public List<AdOriginConfigBean> getAdOriginConfigList() {
        return adOriginConfigList;
    }

    public void setAdOriginConfigList(List<AdOriginConfigBean> adOriginConfigList) {
        this.adOriginConfigList = adOriginConfigList;
    }

    public List<AdReadConfigBean> getParamConfigList() {
        return paramConfigList;
    }

    public void setParamConfigList(List<AdReadConfigBean> paramConfigList) {
        this.paramConfigList = paramConfigList;
    }
}
