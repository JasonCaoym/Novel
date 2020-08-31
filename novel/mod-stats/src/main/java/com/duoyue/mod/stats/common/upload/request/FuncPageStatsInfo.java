package com.duoyue.mod.stats.common.upload.request;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;


public class FuncPageStatsInfo {

    @SerializedName("targetId")
    private long bookId;
    @SerializedName("pageId")
    private String prevPageId;
    @SerializedName("modelId")
    private String modelId;
    @SerializedName("operator")
    private String operator;
    @SerializedName("source")
    private String source;
    /**
     * 叠加次数
     */
    @SerializedName("num")
    private int num;
    @SerializedName("nowPage")
    private String currPageId;
    @SerializedName("field1")
    private String field1;

    public boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuncPageStatsInfo that = (FuncPageStatsInfo) o;
        return bookId == that.bookId &&
                modelId == that.modelId &&
                num == that.num &&
                equals(prevPageId, that.prevPageId) &&
                equals(operator, that.operator) &&
                equals(source, that.source) &&
                equals(currPageId, that.currPageId)&&
                equals(field1, that.field1);
    }

    @Override
    public int hashCode() {
        return hash(bookId, prevPageId, modelId, operator, source, num, currPageId);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPrevPageId() {
        return prevPageId;
    }

    public void setPrevPageId(String prevPageId) {
        this.prevPageId = prevPageId;
    }

    public String getCurrPageId() {
        return currPageId;
    }

    public void setCurrPageId(String currPageId) {
        this.currPageId = currPageId;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }
}
