package com.duoyue.mod.stats.data;

import com.duoyue.mod.stats.data.dao.AdStatsDao;
import com.duoyue.mod.stats.data.dao.FunctionStatsDao;
import com.duoyue.mod.stats.data.entity.AdStatsEntity;
import com.duoyue.mod.stats.data.entity.FunctionStatsEntity;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

/**
 * 统计Dso Session.
 */
public class StatsDaoSession extends AbstractDaoSession
{
    /**
     * 广告统计Dao配置
     */
    private final DaoConfig mAdStatsDaoConfig;

    /**
     * 功能统计Dao配置
     */
    private final DaoConfig mFunStatsDaoConfig;

    /**
     * 广告统计Dao
     */
    private final AdStatsDao mAdStatsDao;

    /**
     * 功能统计Dao
     */
    private final FunctionStatsDao mFunStatsDao;

    public StatsDaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap)
    {
        super(db);
        //获取广告统计Dao配置对象.
        mAdStatsDaoConfig = daoConfigMap.get(AdStatsDao.class).clone();
        mAdStatsDaoConfig.initIdentityScope(type);
        //获取功能统计Dao配置对象.
        mFunStatsDaoConfig = daoConfigMap.get(FunctionStatsDao.class).clone();
        mFunStatsDaoConfig.initIdentityScope(type);

        //创建广告统计Dao对象.
        mAdStatsDao = new AdStatsDao(mAdStatsDaoConfig, this);
        //创建功能统计Dao对象.
        mFunStatsDao = new FunctionStatsDao(mFunStatsDaoConfig, this);

        //注册Dao.
        registerDao(AdStatsEntity.class, mAdStatsDao);
        registerDao(FunctionStatsEntity.class, mFunStatsDao);
    }
    
    public void clear()
    {
        if (mAdStatsDaoConfig != null)
        {
            mAdStatsDaoConfig.clearIdentityScope();
        }
        if (mFunStatsDaoConfig != null)
        {
            mFunStatsDaoConfig.clearIdentityScope();
        }
    }

    public AdStatsDao getAdStatsDao()
    {
        return mAdStatsDao;
    }

    public FunctionStatsDao getFuncStatsDao()
    {
        return mFunStatsDao;
    }
}
