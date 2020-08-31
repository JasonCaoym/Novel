package com.duoyue.mod.stats.data;

import android.database.sqlite.SQLiteDatabase;
import com.duoyue.lib.base.log.Logger;
import com.zydm.base.common.BaseApplication;

public class StatsDaoDBHelper
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#StatsDaoDBHelper";

    /**
     * 数据库文件名称
     */
    private static final String DB_NAME = "stats";

    /**
     * 当前类单例对象
     */
    private static volatile StatsDaoDBHelper sInstance = new StatsDaoDBHelper();

    /**
     * SQLiteDatabase对象.
     */
    private SQLiteDatabase mSqlDB;

    /**
     * StatsDaoMaster对象
     */
    private StatsDaoMaster mDaoMaster;

    /**
     * StatsDaoSession对象
     */
    private StatsDaoSession mDaoSession;

    private StatsDaoDBHelper()
    {
        try {
            //封装数据库的创建、更新、删除
            StatsDaoMaster.DevOpenHelper openHelper = new StatsDaoMaster.DevOpenHelper(BaseApplication.context.globalContext, DB_NAME, null);
            //获取数据库
            mSqlDB = openHelper.getWritableDatabase();
            //封装数据库中表的创建、更新、删除
            mDaoMaster = new StatsDaoMaster(mSqlDB);
            //对表操作的对象。
            mDaoSession = mDaoMaster.newSession();
        } catch (Exception ex) {

        }
    }

    /**
     * 获取当前类单例对象.
     * @return
     */
    public static StatsDaoDBHelper getInstance()
    {
        return sInstance;
    }

    /**
     * 登录成功.
     */
    public void onLoginSucc()
    {
        try
        {
            //移除数据库重新创建.
            StatsDaoMaster.dropAllTables(mDaoMaster.getDatabase(), true);
            //创建数据库.
            StatsDaoMaster.createAllTables(mDaoMaster.getDatabase(), true);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "onLoginSucc: {}，{}", mDaoMaster, throwable);
        }
    }

    public StatsDaoSession getSession()
    {
        return mDaoSession;
    }

    public SQLiteDatabase getDatabase()
    {
        return mSqlDB;
    }

    public StatsDaoSession getNewSession()
    {
        return mDaoMaster.newSession();
    }
}
