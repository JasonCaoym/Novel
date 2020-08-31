package com.duoyue.mod.stats.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.data.StatsDaoSession;
import com.duoyue.mod.stats.data.entity.AdStatsEntity;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * 广告统计数据Dao
 * @author caoym
 * @data 2019/3/22  11:41
 */
public class AdStatsDao extends AbstractDao<AdStatsEntity, Long>
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#AdStatsDao";

    /**
     * 表名, 必须定义, 且字段名不能修改, 否则DaoConfig会报异常.
     */
    public static final String TABLENAME = "A_STATS";

    /**
     * 实体属性关联表字段.
     */
    public static class Properties
    {
        private static int ordinal = 0;
        //主键ID
        public final static Property ID = new Property(ordinal++, long.class, "_id", true, "_ID");
        //广告位Id
        public final static Property AdSoltId = new Property(ordinal++, String.class, "adSoltId", false, "AD_SOLT_ID");
        //广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
        public final static Property AdSite = new Property(ordinal++, int.class, "adSite", false, "AD_SITE");
        //广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
        public final static Property AdType = new Property(ordinal++, int.class, "adType", false, "AD_TYPE");
        //广告源(1:广点通2:穿山甲 3:百度)
        public final static Property Origin = new Property(ordinal++, int.class, "origin", false, "ORIGIN");
        //节点类型(开始请求:"START"、拉取成功:"PULLED"、拉取失败:"PULLFAIL"、展示成功:"SHOWED",展示失败:"SHOWFAIL",点击广告:"CLICK)
        public final static Property NodeName = new Property(ordinal++, String.class, "nodeName", false, "NODE_NAME");
        //节点数量
        public final static Property NodeCount = new Property(ordinal++, int.class, "nodeCount", false, "NODE_COUNT");
        //上传批次号(上传数据使用)
        public final static Property BatchNumber = new Property(ordinal++, String.class, "batchNumber", false, "BATCH_NUMBER");
        //扩展参数.
        public final static Property ExtInfo = new Property(ordinal++, String.class, "extInfo", false, "EXT_INFO");
        //保存时间.
        public final static Property SaveTime = new Property(ordinal++, long.class, "saveTime", false, "SAVE_TIME");
    }

    public AdStatsDao(DaoConfig config)
    {
        super(config);
    }

    public AdStatsDao(DaoConfig config, StatsDaoSession daoSession)
    {
        super(config, daoSession);
    }

    /**
     * 创建表.
     * @param db
     * @param ifNotExists
     */
    public static void createTable(Database db, boolean ifNotExists)
    {
        try
        {
            String constraint = ifNotExists? "IF NOT EXISTS ": "";
            db.execSQL("CREATE TABLE " + constraint + TABLENAME + " (" + //
                    "\"_ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                    "\"AD_SOLT_ID\" TEXT ," +
                    "\"AD_SITE\" NUMBER ," +
                    "\"AD_TYPE\" NUMBER ," +
                    "\"ORIGIN\" NUMBER ," +
                    "\"NODE_NAME\" TEXT NOT NULL ," +
                    "\"NODE_COUNT\" NUMBER ," +
                    "\"BATCH_NUMBER\" TEXT ," +
                    "\"EXT_INFO\" TEXT ," +
                    "\"SAVE_TIME\" NUMBER);");
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "createTable: 异常:{}", throwable);
        }
    }

    /**
     * 删除表
     * @param db
     * @param ifExists
     */
    public static void dropTable(Database db, boolean ifExists)
    {
        try
        {
            String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + TABLENAME;
            db.execSQL(sql);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "dropTable: 异常:{}", throwable);
        }
    }

    @Override
    protected AdStatsEntity readEntity(Cursor cursor, int offset)
    {
        AdStatsEntity entity = new AdStatsEntity();
        try
        {
            int columnIndex = offset;
            //主键ID
            entity.set_id(cursor.getLong(columnIndex));
            //广告位Id
            entity.setAdSoltId(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
            entity.setAdSite(cursor.getInt(++columnIndex));
            //广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
            entity.setAdType(cursor.getInt(++columnIndex));
            //广告源(1:广点通2:穿山甲 3:百度)
            entity.setOrigin(cursor.getInt(++columnIndex));
            //节点名称
            entity.setNodeName(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //次数
            entity.setNodeCount(cursor.getInt(++columnIndex));
            //上传批次号(上传数据使用)
            entity.setBatchNumber(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //扩展参数.
            entity.setExtInfo(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //保存时间.
            entity.setSaveTime(cursor.getLong(++columnIndex));
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "readEntity: {}, 异常:{}", offset, throwable);
        }
        return entity;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset)
    {
        try
        {
            return cursor.getLong(offset + 1);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "readKey:{}, 异常: {}", offset, throwable);
            return 0L;
        }
    }

    @Override
    protected void readEntity(Cursor cursor, AdStatsEntity entity, int offset)
    {
        try
        {
            int columnIndex = offset;
            //主键ID
            entity.set_id(cursor.getLong(columnIndex));
            //广告位Id
            entity.setAdSoltId(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //广告位置(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
            entity.setAdSite(cursor.getInt(++columnIndex));
            //广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
            entity.setAdType(cursor.getInt(++columnIndex));
            //广告源(1:广点通2:穿山甲 3:百度)
            entity.setOrigin(cursor.getInt(++columnIndex));
            //节点名称
            entity.setNodeName(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //次数
            entity.setNodeCount(cursor.getInt(++columnIndex));
            //上传批次号(上传数据使用)
            entity.setBatchNumber(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //扩展参数.
            entity.setExtInfo(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //保存时间.
            entity.setSaveTime(cursor.getLong(++columnIndex));
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "readEntity: {}, 异常:{}", offset, throwable);
        }
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, AdStatsEntity entity) {
        try
        {
            stmt.clearBindings();
            int index = 0;
            //主键Id, 自动增长.
            ++index;
            if (entity.get_id() > 0)
            {
                stmt.bindLong(index, entity.get_id());
            }
            ++index;
            if (entity.getAdSoltId() != null)
            {
                stmt.bindString(index, entity.getAdSoltId());
            }
            ++index;
            stmt.bindLong(index, entity.getAdSite());
            ++index;
            stmt.bindLong(index, entity.getAdType());
            ++index;
            stmt.bindLong(index, entity.getOrigin());
            ++index;
            if (entity.getNodeName() != null)
            {
                stmt.bindString(index, entity.getNodeName());
            }
            ++index;
            stmt.bindLong(index, entity.getNodeCount());
            ++index;
            if (entity.getBatchNumber() != null)
            {
                stmt.bindString(index, entity.getBatchNumber());
            }
            ++index;
            if (entity.getExtInfo() != null)
            {
                stmt.bindString(index, entity.getExtInfo());
            }
            ++index;
            stmt.bindLong(index, entity.getSaveTime());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "bindValues:{}, 异常: {}", "DatabaseStatement", throwable);
        }
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, AdStatsEntity entity) {
        try
        {
            stmt.clearBindings();
            int index = 0;
            //主键Id, 自动增长.
            ++index;
            if (entity.get_id() > 0)
            {
                stmt.bindLong(1, entity.get_id());
            }
            ++index;
            if (entity.getAdSoltId() != null)
            {
                stmt.bindString(index, entity.getAdSoltId());
            }
            ++index;
            stmt.bindLong(index, entity.getAdSite());
            ++index;
            stmt.bindLong(index, entity.getAdType());
            ++index;
            stmt.bindLong(index, entity.getOrigin());
            ++index;
            if (entity.getNodeName() != null)
            {
                stmt.bindString(index, entity.getNodeName());
            }
            ++index;
            stmt.bindLong(index, entity.getNodeCount());
            ++index;
            if (entity.getBatchNumber() != null)
            {
                stmt.bindString(index, entity.getBatchNumber());
            }
            ++index;
            if (entity.getExtInfo() != null)
            {
                stmt.bindString(index, entity.getExtInfo());
            }
            ++index;
            stmt.bindLong(index, entity.getSaveTime());
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "bindValues: {}, 异常:{}", "SQLiteStatement", throwable);
        }
    }

    @Override
    protected Long updateKeyAfterInsert(AdStatsEntity entity, long rowId)
    {
        try
        {
            entity.set_id(rowId);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "updateKeyAfterInsert: {}, 异常:{}", rowId, throwable);
        }
        return rowId;
    }

    @Override
    protected Long getKey(AdStatsEntity entity)
    {
        if(entity != null)
        {
            return entity.get_id();
        } else
        {
            return null;
        }
    }

    @Override
    protected boolean hasKey(AdStatsEntity entity)
    {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected boolean isEntityUpdateable()
    {
        return true;
    }
}
