package com.duoyue.mod.ad.dao;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.dao.gen.DaoMaster;
import com.duoyue.mod.ad.dao.gen.DaoSession;
import com.zydm.base.common.BaseApplication;

import java.io.File;

public class AdDaoDbHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "Ad#AdDaoDbHelper";

    private static final String DB_NAME = "Ad_config.db";

    private static volatile AdDaoDbHelper sInstance = new AdDaoDbHelper();
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

    private AdDaoDbHelper() {
        try {
            //封装数据库的创建、更新、删除
            DevOpenHelper openHelper = new DevOpenHelper(BaseApplication.context.globalContext, DB_NAME, null);
            //获取数据库
            mDb = openHelper.getWritableDatabase();
            //封装数据库中表的创建、更新、删除
            mDaoMaster = new DaoMaster(mDb);  //合起来就是对数据库的操作
            //对表操作的对象。
            mSession = mDaoMaster.newSession(); //可以认为是对数据的操作

            //注册登陆成功广播.
            BaseApplication.context.globalContext.registerReceiver(new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    try
                    {
                        Logger.i(TAG, "onReceive: {}, {}", context, intent);
                        //登陆成功.
                        if (Constants.LOGIN_SUCC_ACTION.equals(intent.getAction()))
                        {
                            //切换用户, 广告模块不需要移除相关信息.
                            //移除数据库重新创建.
                            ///DaoMaster.dropAllTables(mDaoMaster.getDatabase(), true);
                            //创建数据库.
                            ///DaoMaster.createAllTables(mDaoMaster.getDatabase(), true);
                        }
                    } catch (Throwable throwable)
                    {
                        Logger.e(TAG, "onReceive: {}, {}, {}", context, intent, throwable);
                    }
                }
            }, new IntentFilter(Constants.LOGIN_SUCC_ACTION));
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "AdDaoDbHelper: {}", throwable);
        }
    }


    public static AdDaoDbHelper getInstance() {
        if (sInstance == null) {
            synchronized (AdDaoDbHelper.class) {
                if (sInstance == null) {
                    sInstance = new AdDaoDbHelper();
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

    public void dropDb() {
        if (mDaoMaster != null) {
            DaoMaster.dropAllTables(mDaoMaster.getDatabase(),true);
        }
    }
}
