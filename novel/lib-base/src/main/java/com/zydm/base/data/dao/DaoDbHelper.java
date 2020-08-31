package com.zydm.base.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.duoyue.lib.base.log.Logger;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.dao.gen.*;
import org.greenrobot.greendao.database.Database;

import java.io.File;

public class DaoDbHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "Base#DaoDbHelper";

    private static final String DB_NAME = "WeYueReader_DB";

    private static volatile DaoDbHelper sInstance;
    private SQLiteDatabase mDb;
    private DaoMaster mDaoMaster;
    private DaoSession mSession;

    public static class DevOpenHelper extends DaoMaster.DevOpenHelper {
        private static boolean mainTmpDirSet = false;
        private Context mContext;

        public DevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
            mContext = context;
        }

        /**
         * BUG:SQLiteCantOpenDatabaseException: unable to open database file (code 14)
         *
         * @return
         */
        @Override
        public SQLiteDatabase getReadableDatabase() {
            if (!mainTmpDirSet) {
                boolean rs = new File("/data/data/"+ mContext.getPackageName() + "/databases/main").mkdir();
                Logger.e("ad_database", "packageName ; " + mContext.getPackageName() + ", rs : " + rs);
                super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/"+ mContext.getPackageName() + "/databases/main'");
                mainTmpDirSet = true;
                return super.getReadableDatabase();
            }
            return super.getReadableDatabase();
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            }, BookRecordBeanDao.class, BookShelfBeanDao.class, ChapterBeanDao.class);
            Log.e(TAG, "onUpgrade: " + oldVersion + " newVersion = " + newVersion);

        }
    }

    private DaoDbHelper() {
        //封装数据库的创建、更新、删除
        DevOpenHelper openHelper = new DevOpenHelper(BaseApplication.context.globalContext, DB_NAME, null);
        //获取数据库
        mDb = openHelper.getWritableDatabase();
        //封装数据库中表的创建、更新、删除
        mDaoMaster = new DaoMaster(mDb);  //合起来就是对数据库的操作
        //对表操作的对象。
        mSession = mDaoMaster.newSession(); //可以认为是对数据的操作
    }

    public static DaoDbHelper getInstance() {
        if (sInstance == null) {
            synchronized (DaoDbHelper.class) {
                if (sInstance == null) {
                    sInstance = new DaoDbHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 登录成功.
     */
    public void onLoginSucc()
    {
        try
        {
            Logger.i(TAG, "onLoginSucc:");
            //移除数据库重新创建.
            DaoMaster.dropAllTables(mDaoMaster.getDatabase(), true);
            //创建数据库.
            DaoMaster.createAllTables(mDaoMaster.getDatabase(), true);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "onLoginSucc: {}, {}", mDaoMaster, throwable);
        }
    }

    public synchronized DaoSession getSession() {
        return mSession;
    }

    public SQLiteDatabase getDatabase() {
        return mDb;
    }

    public DaoSession getNewSession() {
        return mDaoMaster.newSession();
    }

    public void dropDb() {
        if (mDaoMaster != null) {
            DaoMaster.dropAllTables(mDaoMaster.getDatabase(),true);
        }
    }

}
