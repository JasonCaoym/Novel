package com.zydm.base.data.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CategoryBean : Serializable {
    companion object {
        /**
         * 分类View类型-0:分类组
         */
        const val VIEW_TYPE_GROUP: Int = 0

        /**
         * 分类View类型-1:分类
         */
        const val VIEW_TYPE_CATEGORY: Int = 1

        /**
         *  留白
         */
        const val VIEW_TYPE_NULL: Int = 2
    }

    /**
     * 分类Id
     */
    var id: String = ""

    /**
     * 分类名称
     */
    var name: String = ""

    /**
     * 分类图片.
     */
    @SerializedName("cover")
    var image: String = ""

    /**
     * 所属性别(0:男生;1:女生).
     */
    var sex: Int = 0;

    /**
     * View类型(0:分类组;1:分类)
     */
    var viewType: Int = 0

    /**
     * 位置
     */
    var position: Int = -1

    var sort: Int = 1
    /**
     * 留白间距
     */
    var nullHeight: Int = 0

    /**
     * 是否选中该类型 0未选  1已选
     */
    var isMine: Int = 0

    /**
     * 标签：列如：爱情,古代言情,宫斗,阴谋,女性,事业,现言,言情,豪门
     */
    var tags: String = ""

    /**
     * 二级分类
     */
    var subCategories: ArrayList<SubCategoriesBean>? = null

    /**
     * 人工自定义标签
     */
    var mtSubCateName: String = ""

    var isSelected: Boolean = false;
}

/**
 * 二级分类
 */
class SubCategoriesBean : Serializable {
    var id: Int = 0
    var name: String = ""
    var sort: Int = 0
}

class CategorySubBean : Serializable {
    var subId: String = "0"
    var name: String = "全部"
}
