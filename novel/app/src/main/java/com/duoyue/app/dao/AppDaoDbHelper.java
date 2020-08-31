package com.duoyue.app.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.duoyue.app.dao.gen.DaoMaster;
import com.duoyue.app.dao.gen.DaoSession;
import com.duoyue.lib.base.log.Logger;
import com.zydm.base.common.BaseApplication;

import java.io.File;

public class AppDaoDbHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#AppDaoDbHelper";

    private static final String DB_NAME = "duoyue_app.db";

    private static volatile AppDaoDbHelper sInstance;
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

    private AppDaoDbHelper() {
        //封装数据库的创建、更新、删除
        DevOpenHelper openHelper = new DevOpenHelper(BaseApplication.context.globalContext, DB_NAME, null);
        //获取数据库
        mDb = openHelper.getWritableDatabase();
        //封装数据库中表的创建、更新、删除
        mDaoMaster = new DaoMaster(mDb);  //合起来就是对数据库的操作
        //对表操作的对象。
        mSession = mDaoMaster.newSession(); //可以认为是对数据的操作
    }

    public static AppDaoDbHelper getInstance() {
        if (sInstance == null) {
            synchronized (AppDaoDbHelper.class) {
                if (sInstance == null) {
                    sInstance = new AppDaoDbHelper();
                }
            }
        }
        return sInstance;
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
}
