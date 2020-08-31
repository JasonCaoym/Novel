package com.duoyue.mod.stats.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.data.dao.AdStatsDao;
import com.duoyue.mod.stats.data.dao.FunctionStatsDao;
import com.duoyue.mod.stats.data.gen.FuncPageStatsEntityDao;
import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

/**
 * 统计DB模块Master
 * @author caoym
 * @data 2019/3/22  11:31
 */
public class StatsDaoMaster extends AbstractDaoMaster
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#StatsDaoMaster";

    public static final int SCHEMA_VERSION = 1;

    /**
     * 创建所有的表
     * @param db 数据库
     * @param ifNotExists 表格是否存在, 不存在才创建, 存在的话就不用创建
     */
    public static void createAllTables(Database db, boolean ifNotExists)
    {
        AdStatsDao.createTable(db, ifNotExists);
        FunctionStatsDao.createTable(db, ifNotExists);
        FuncPageStatsEntityDao.createTable(db, ifNotExists);
    }

    /**
     * 删除所有数据表
     * @param db 数据库
     * @param ifExists
     */
    public static void dropAllTables(Database db, boolean ifExists)
    {
        AdStatsDao.dropTable(db, ifExists);
        FunctionStatsDao.dropTable(db, ifExists);
        FuncPageStatsEntityDao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link StatsDaoMaster.DevOpenHelper}.
     */
    public static StatsDaoSession newDevSession(Context context, String name)
    {
        Database db = new StatsDaoMaster.DevOpenHelper(context, name).getWritableDb();
        StatsDaoMaster daoMaster = new StatsDaoMaster(db);
        return daoMaster.newSession();
    }

    public StatsDaoMaster(SQLiteDatabase db)
    {
        this(new StandardDatabase(db));
    }

    public StatsDaoMaster(Database db)
    {
        super(db, SCHEMA_VERSION);
        registerDaoClass(AdStatsDao.class);
        registerDaoClass(FunctionStatsDao.class);
    }

    public StatsDaoSession newSession()
    {
        return new StatsDaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public StatsDaoSession newSession(IdentityScopeType type)
    {
        return new StatsDaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper
    {
        public OpenHelper(Context context, String name)
        {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory)
        {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db)
        {
            Logger.i(TAG, "OpenHelper.onCreate: {}" + SCHEMA_VERSION);
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends StatsDaoMaster.OpenHelper
    {
        public DevOpenHelper(Context context, String name)
        {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory)
        {
            super(context, name, factory);
        }

        /**
         * 表格的更新, 更新之前先删, 再重新创建
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion)
        {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }
}