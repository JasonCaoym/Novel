package com.duoyue.app.bean;

import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.bean.CategoryBean;

import java.util.List;

public class CategoryAllGroupBean {

    @SerializedName("manList")
    private List<CategoryBean> maleBean;
    @SerializedName("womenList")
    private List<CategoryBean>  femaleBean;
    @SerializedName("pushList")
    private List<CategoryBean>  pushBean;

    public List<CategoryBean> getMaleBean() {
        return maleBean;
    }

    public void setMaleBean(List<CategoryBean> maleBean) {
        this.maleBean = maleBean;
    }

    public List<CategoryBean> getFemaleBean() {
        return femaleBean;
    }

    public void setFemaleBean(List<CategoryBean> femaleBean) {
        this.femaleBean = femaleBean;
    }


    public List<CategoryBean> getPushBean() {
        return pushBean;
    }

    public void setPushBean(List<CategoryBean> pushBean) {
        this.pushBean = pushBean;
    }
}
