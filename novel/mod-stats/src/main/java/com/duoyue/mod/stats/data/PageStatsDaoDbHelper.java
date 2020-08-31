package com.duoyue.mod.stats.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.stats.data.gen.DaoMaster;
import com.duoyue.mod.stats.data.gen.DaoSession;
import com.zydm.base.common.BaseApplication;

import java.io.File;

public class PageStatsDaoDbHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#AdDaoDbHelper";

    private static final String DB_NAME = "page_stats.db";

    private static volatile PageStatsDaoDbHelper sInstance = new PageStatsDaoDbHelper();
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
    }

    private PageStatsDaoDbHelper() {
        try {
            //封装数据库的创建、更新、删除
            DevOpenHelper openHelper = new DevOpenHelper(BaseApplication.context.globalContext, DB_NAME, null);
            //获取数据库
            mDb = openHelper.getWritableDatabase();
            //封装数据库中表的创建、更新、删除
            mDaoMaster = new DaoMaster(mDb);  //合起来就是对数据库的操作
            //对表操作的对象。
            mSession = mDaoMaster.newSession(); //可以认为是对数据的操作
        } catch (Exception ex) {

        }
    }


    public static PageStatsDaoDbHelper getInstance() {
        return sInstance;
    }

    public synchronized DaoSession getSession() {
        return mSession;
    }

    public SQLiteDatabase getDatabase() {
        return mDb;
    }

    public void dropDb() {
        if (mDaoMaster != null) {
            DaoMaster.dropAllTables(mDaoMaster.getDatabase(),true);
        }
    }

}
