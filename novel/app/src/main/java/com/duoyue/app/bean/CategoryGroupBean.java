package com.duoyue.app.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zydm.base.data.bean.CategoryBean;

import java.util.List;

/**
 * 分类组信息对象
 *
 * @author caoym
 * @data 2019/4/18  19:46
 */
public class CategoryGroupBean {
    /**
     * 分类组Id(1:男生;2:女生)
     */
    @Expose(deserialize = false, serialize = false)
    public int groupId;

    /**
     * 分类组名称.
     */
    @Expose(deserialize = false, serialize = false)
    public String groupName;

    /**
     * 分类列表
     */
    @SerializedName("list")
    public List<CategoryBean> categoryList;

    /**
     * 当前分类组是否为选中状态.
     */
    @Expose(deserialize = false, serialize = false)
    public boolean isSelected;
}
