package com.duoyue.mod.stats.data.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.data.StatsDaoSession;
import com.duoyue.mod.stats.data.entity.FunctionStatsEntity;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

/**
 * 功能统计Dao.
 * @author caoym
 * @data 2019/3/22  11:42
 */
public class FunctionStatsDao extends AbstractDao<FunctionStatsEntity, Long>
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats@FunctionStatsDao";

    /**
     * 表名, 必须定义, 且字段名不能修改, 否则DaoConfig会报异常.
     */
    public static final String TABLENAME = "FUN_STATS";

    /**
     * 实体属性关联表字段.
     */
    public static class Properties
    {
        private static int ordinal = 0;
        //主键ID
        public final static Property ID = new Property(ordinal++, long.class, "_id", true, "_ID");
        //节点名称
        public final static Property NodeName = new Property(ordinal++, String.class, "nodeName", false, "NODE_NAME");
        //书籍Id.
        public final static Property BookId = new Property(ordinal++, long.class, "bookId", false, "BOOK_ID");
        //节点数量.
        public final static Property NodeCount = new Property(ordinal++, String.class, "nodeCount", false, "NODE_COUNT");
        //当前日期(yy-MM-dd)
        public final static Property NodeDate = new Property(ordinal++, String.class, "nodeDate", false, "NODE_DATE");
        //上传批次号(上传数据使用)
        public final static Property BatchNumber = new Property(ordinal++, String.class, "batchNumber", false, "BATCH_NUMBER");
        //扩展参数.
        public final static Property ExtInfo = new Property(ordinal++, String.class, "extInfo", false, "EXT_INFO");
        //保存时间.
        public final static Property SaveTime = new Property(ordinal++, long.class, "saveTime", false, "SAVE_TIME");
    }

    public FunctionStatsDao(DaoConfig config)
    {
        super(config);
    }

    public FunctionStatsDao(DaoConfig config, StatsDaoSession daoSession)
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
            db.execSQL("CREATE TABLE " + constraint + TABLENAME + " (" +
                    "\"_ID\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                    "\"NODE_NAME\" TEXT NOT NULL ," +
                    "\"BOOK_ID\" INTEGER ," +
                    "\"NODE_COUNT\" INTEGER ," +
                    "\"NODE_DATE\" TEXT ," +
                    "\"BATCH_NUMBER\" TEXT ," +
                    "\"EXT_INFO\" TEXT ," +
                    "\"SAVE_TIME\" NUMBER NOT NULL );");
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "createTable: , 异常:{}", throwable);
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
            Logger.e(TAG, "dropTable: , 异常:{}", throwable);
        }
    }

    @Override
    protected FunctionStatsEntity readEntity(Cursor cursor, int offset)
    {
        FunctionStatsEntity entity = new FunctionStatsEntity();
        try
        {
            int columnIndex = offset;
            //主键ID
            entity.set_Id(cursor.getLong(columnIndex));
            //节点名称
            entity.setNodeName(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //书籍Id.
            entity.setBookId(cursor.getLong(++columnIndex));
            //节点数量.
            entity.setNodeCount(cursor.getInt(++columnIndex));
            //节点日期.
            entity.setNodeDate(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //上传批次号(上传数据使用)
            entity.setBatchNumber(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //扩展参数.
            entity.setExtInfo(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //保存时间.
            entity.setSaveTime(cursor.getLong(++columnIndex));
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "readEntity: , 异常:{}", throwable);
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
            Logger.e(TAG, "readKey: , 异常:{}", throwable);
            return 0L;
        }
    }

    @Override
    protected void readEntity(Cursor cursor, FunctionStatsEntity entity, int offset)
    {
        try
        {
            int columnIndex = offset;
            //主键ID
            entity.set_Id(cursor.getLong(columnIndex));
            //节点名称
            entity.setNodeName(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
            //书籍Id.
            entity.setBookId(cursor.getLong(++columnIndex));
            //节点数量.
            entity.setNodeCount(cursor.getInt(++columnIndex));
            //节点日期.
            entity.setNodeDate(cursor.isNull(++columnIndex) ? null : cursor.getString(columnIndex));
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
    protected void bindValues(DatabaseStatement stmt, FunctionStatsEntity entity)
    {
        try
        {
            stmt.clearBindings();
            int index = 0;
            //主键Id, 自动增长.
            ++index;
            if (entity.get_Id() > 0)
            {
                stmt.bindLong(index, entity.get_Id());
            }
            ++index;
            if (entity.getNodeName() != null)
            {
                stmt.bindString(index, entity.getNodeName());
            }
            ++index;
            stmt.bindLong(index, entity.getBookId());
            ++index;
            stmt.bindLong(index, entity.getNodeCount());
            ++index;
            if (entity.getNodeDate() != null)
            {
                stmt.bindString(index, entity.getNodeDate());
            }
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
            Logger.e(TAG, "bindValues:{}, 异常:{}", "DatabaseStatement", throwable);
        }
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, FunctionStatsEntity entity)
    {
        try
        {
            stmt.clearBindings();
            int index = 0;
            //主键Id, 自动增长.
            ++index;
            if (entity.get_Id() > 0)
            {
                stmt.bindLong(1, entity.get_Id());
            }
            ++index;
            if (entity.getNodeName() != null)
            {
                stmt.bindString(index, entity.getNodeName());
            }
            ++index;
            stmt.bindLong(index, entity.getBookId());
            ++index;
            stmt.bindLong(index, entity.getNodeCount());
            ++index;
            if (entity.getNodeDate() != null)
            {
                stmt.bindString(index, entity.getNodeDate());
            }
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
            Logger.e(TAG, "bindValues:{}, 异常:{}", "SQLiteStatement", throwable);
        }
    }

    @Override
    protected Long updateKeyAfterInsert(FunctionStatsEntity entity, long rowId)
    {
        try
        {
            entity.set_Id(rowId);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "updateKeyAfterInsert: , 异常:{}", throwable);
        }
        return rowId;
    }

    @Override
    protected Long getKey(FunctionStatsEntity entity)
    {
        if(entity != null)
        {
            return entity.get_Id();
        } else
        {
            return null;
        }
    }

    @Override
    protected boolean hasKey(FunctionStatsEntity entity)
    {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected boolean isEntityUpdateable()
    {
        return true;
    }
}
