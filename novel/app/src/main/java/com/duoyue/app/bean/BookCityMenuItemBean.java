package com.duoyue.app.bean;

import com.zydm.base.data.bean.SubCategoriesBean;

import java.util.ArrayList;
import java.util.List;

public class BookCityMenuItemBean {

    private int id;
    private int chan;
    private String showName;
    private String pic;
    private int showType;
    private int catId;
    private int sort;
    private String tag;
    private String parentId;

    private ArrayList<SubCategoriesBean> subCategories;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChan() {
        return chan;
    }

    public void setChan(int chan) {
        this.chan = chan;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ArrayList<SubCategoriesBean> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategoriesBean> subCategories) {
        this.subCategories = subCategories;
    }
}
