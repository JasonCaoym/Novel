package com.duoyue.mod.stats.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class FuncPageStatsEntity implements  Parcelable {

    /**
     * 主键ID
     */
    @Id
    public Long _id;

    /**
     * 节点名称
     */
    public String nodeName;

    /**
     * 书籍Id.
     */
    public long bookId;

    /**
     * 节点数量
     */
    public int nodeCount;

    /**
     * 节点日期(yyyy-MM-dd)
     */
    public String nodeDate;

    /**
     * 上传批次号(上传数据使用)
     */
    public String batchNumber;

    /**
     * 扩展参数.
     */
    public String extInfo;

    /**
     * 保存时间.
     */
    public Long saveTime;

    public String prevPageId;

    public String currPageId;

    public String source;

    public String modelId;

    public FuncPageStatsEntity() {
    }

    protected FuncPageStatsEntity(Parcel in) {
        _id = in.readLong();
        nodeName = in.readString();
        bookId = in.readLong();
        nodeCount = in.readInt();
        nodeDate = in.readString();
        batchNumber = in.readString();
        extInfo = in.readString();
        saveTime = in.readLong();
        prevPageId = in.readString();
        currPageId = in.readString();
        source = in.readString();
        modelId = in.readString();
    }

    @Generated(hash = 267982249)
    public FuncPageStatsEntity(Long _id, String nodeName, long bookId, int nodeCount, String nodeDate,
            String batchNumber, String extInfo, Long saveTime, String prevPageId, String currPageId,
            String source, String modelId) {
        this._id = _id;
        this.nodeName = nodeName;
        this.bookId = bookId;
        this.nodeCount = nodeCount;
        this.nodeDate = nodeDate;
        this.batchNumber = batchNumber;
        this.extInfo = extInfo;
        this.saveTime = saveTime;
        this.prevPageId = prevPageId;
        this.currPageId = currPageId;
        this.source = source;
        this.modelId = modelId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeName);
        dest.writeLong(bookId);
        dest.writeInt(nodeCount);
        dest.writeString(nodeDate);
        dest.writeString(batchNumber);
        dest.writeString(extInfo);
        dest.writeLong(saveTime);
        dest.writeString(prevPageId);
        dest.writeString(currPageId);
        dest.writeString(source);
        dest.writeString(modelId);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getBookId() {
        return this.bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getNodeCount() {
        return this.nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public String getNodeDate() {
        return this.nodeDate;
    }

    public void setNodeDate(String nodeDate) {
        this.nodeDate = nodeDate;
    }

    public String getBatchNumber() {
        return this.batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getExtInfo() {
        return this.extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public Long getSaveTime() {
        return this.saveTime;
    }

    public void setSaveTime(Long saveTime) {
        this.saveTime = saveTime;
    }

    public String getPrevPageId() {
        return this.prevPageId;
    }

    public void setPrevPageId(String prevPageId) {
        this.prevPageId = prevPageId;
    }

    public String getCurrPageId() {
        return this.currPageId;
    }

    public void setCurrPageId(String currPageId) {
        this.currPageId = currPageId;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getModelId() {
        return this.modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public static final Creator<FunctionStatsEntity> CREATOR = new Creator<FunctionStatsEntity>() {
        @Override
        public FunctionStatsEntity createFromParcel(Parcel in)
        {
            return new FunctionStatsEntity(in);
        }

        @Override
        public FunctionStatsEntity[] newArray(int size)
        {
            return new FunctionStatsEntity[size];
        }
    };

}
