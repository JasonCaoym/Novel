package com.duoyue.app.bean;

public class FeedConfigItemBean {

    /**
     * 类型Id
     */
    private int id;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 排序
     */
    private int sort;

    /**
     * 是否选中
     * 非服务端接口字段
     */
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
