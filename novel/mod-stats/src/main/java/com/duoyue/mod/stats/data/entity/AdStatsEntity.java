package com.duoyue.mod.stats.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.zydm.base.data.base.IIdGetter;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 广告统计实体类
 * @author caoym
 * @data 2019/3/22  13:42
 */
@Entity
public class AdStatsEntity implements IIdGetter, Parcelable
{
    /**
     * 主键ID
     */
    @Id
    public long _id;

    /**
     * 广告位id
     */
    public String adSoltId;

    /**
     * 广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
     */
    public int adSite;

    /**
     * 广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
     */
    public int adType;

    /**
     * 广告源(1:广点通2:穿山甲 3:百度)
     */
    public int origin;

    /**
     * 节点类型(开始请求:"START"、拉取成功:"PULLED"、拉取失败:"PULLFAIL"、展示成功:"SHOWED",展示失败:"SHOWFAIL",点击广告:"CLICK)
     */
    public String nodeName;

    /**
     * 次数
     */
    private int nodeCount;

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

    public AdStatsEntity()
    {
    }

    protected AdStatsEntity(Parcel in)
    {
        _id = in.readLong();
        adSoltId = in.readString();
        adSite = in.readInt();
        adType = in.readInt();
        origin = in.readInt();
        nodeName = in.readString();
        nodeCount = in.readInt();
        batchNumber = in.readString();
        extInfo = in.readString();
        saveTime = in.readLong();
    }

    @Generated(hash = 1824412842)
    public AdStatsEntity(long _id, String adSoltId, int adSite, int adType, int origin, String nodeName,
            int nodeCount, String batchNumber, String extInfo, Long saveTime) {
        this._id = _id;
        this.adSoltId = adSoltId;
        this.adSite = adSite;
        this.adType = adType;
        this.origin = origin;
        this.nodeName = nodeName;
        this.nodeCount = nodeCount;
        this.batchNumber = batchNumber;
        this.extInfo = extInfo;
        this.saveTime = saveTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(adSoltId);
        dest.writeInt(adSite);
        dest.writeInt(adType);
        dest.writeInt(origin);
        dest.writeString(nodeName);
        dest.writeInt(nodeCount);
        dest.writeString(batchNumber);
        dest.writeString(extInfo);
        dest.writeLong(saveTime);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public String getId() {
        return null;
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

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getAdSoltId() {
        return adSoltId;
    }

    public void setAdSoltId(String adSoltId) {
        this.adSoltId = adSoltId;
    }

    public int getAdSite() {
        return adSite;
    }

    public void setAdSite(int adSite) {
        this.adSite = adSite;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
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
}
