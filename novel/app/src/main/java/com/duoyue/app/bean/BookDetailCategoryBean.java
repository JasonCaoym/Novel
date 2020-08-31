package com.duoyue.app.bean;

import com.zydm.base.data.bean.SubCategoriesBean;

import java.io.Serializable;
import java.util.ArrayList;

public class BookDetailCategoryBean implements Serializable {

    private int id;
    private int parentId;
    private String name;
    private String tags;

    private ArrayList<SubCategoriesBean> subCategorys;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ArrayList<SubCategoriesBean> getSubCategorys() {
        return subCategorys;
    }

    public void setSubCategorys(ArrayList<SubCategoriesBean> subCategorys) {
        this.subCategorys = subCategorys;
    }
}
