package com.duoyue.mod.stats.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.zydm.base.data.base.IIdGetter;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 功能统计实体类
 * @author caoym
 * @data 2019/3/22  13:42
 */
@Entity
public class FunctionStatsEntity implements IIdGetter, Parcelable
{
    /**
     * 主键ID
     */
    @Id
    public long _id;

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

    public FunctionStatsEntity()
    {
    }

    protected FunctionStatsEntity(Parcel in)
    {
        _id = in.readLong();
        nodeName = in.readString();
        bookId = in.readLong();
        nodeCount = in.readInt();
        nodeDate = in.readString();
        batchNumber = in.readString();
        extInfo = in.readString();
        saveTime = in.readLong();
    }

    @Generated(hash = 1260146806)
    public FunctionStatsEntity(long _id, String nodeName, long bookId, int nodeCount, String nodeDate,
            String batchNumber, String extInfo, Long saveTime) {
        this._id = _id;
        this.nodeName = nodeName;
        this.bookId = bookId;
        this.nodeCount = nodeCount;
        this.nodeDate = nodeDate;
        this.batchNumber = batchNumber;
        this.extInfo = extInfo;
        this.saveTime = saveTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(nodeName);
        dest.writeLong(bookId);
        dest.writeInt(nodeCount);
        dest.writeString(nodeDate);
        dest.writeString(batchNumber);
        dest.writeString(extInfo);
        dest.writeLong(saveTime);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<FunctionStatsEntity> CREATOR = new Creator<FunctionStatsEntity>()
    {
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

    @Override
    public String getId() {
        return null;
    }

    public long get_Id() {
        return _id;
    }

    public void set_Id(long id) {
        this._id = id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public String getNodeDate() {
        return nodeDate;
    }

    public void setNodeDate(String nodeDate) {
        this.nodeDate = nodeDate;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }

    public Long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(Long saveTime) {
        this.saveTime = saveTime;
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
