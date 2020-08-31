package com.duoyue.mod.stats.common.upload.request;

import com.google.gson.annotations.SerializedName;

/**
 * 功能统计数据.
 * @author caoym
 * @data 2019/4/9  0:29
 */
public class FuncStatsInfoReq
{
    /**
     * 时长/分钟
     */
    @SerializedName("num")
    private String mNum;

    /**
     *  操作, 详见备注
     */
    @SerializedName("operator")
    private String mOperator;

    /**
     *  书籍Id
     */
    @SerializedName("bookId")
    private long mBookId;

    /**
     * 数据产生的目标来源, 如上报书籍曝光信息时, 上报分栏ID
     */
    @SerializedName("target")
    private String mTarget;

    public FuncStatsInfoReq()
    {
    }

    public void setNum(String num) {
        this.mNum = num;
    }

    public void setOperator(String operator) {
        this.mOperator = operator;
    }

    public void setBookId(long bookId) {
        this.mBookId = bookId;
    }

    public void setTarget(String target) {
        this.mTarget = target;
    }
}
