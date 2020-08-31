package com.duoyue.mod.stats.data.helper;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.stats.data.StatsDaoDBHelper;
import com.duoyue.mod.stats.data.StatsDaoSession;
import com.duoyue.mod.stats.data.dao.AdStatsDao;
import com.duoyue.mod.stats.data.entity.AdStatsEntity;
import com.zydm.base.common.BaseApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

/**
 * @author caoym
 * @data 2019/3/23  11:06
 */
public class AdStatsHelper
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#AdStatsHelper";

    /**
     * 当前类单例对象
     */
    private static volatile AdStatsHelper sInstance;

    /**
     * StatsDaoSession对象
     */
    private StatsDaoSession mDaoSession;

    /**
     * FunctionStatsDao对象
     */
    private volatile AdStatsDao mAdStatsDao;

    /**
     * 监听器列表.
     */
    private ArrayList<AdStatsDaoObserver> mObservers = new ArrayList<>();
    private Semaphore semaphore = new Semaphore(1, true);

    private AdStatsHelper()
    {
        mDaoSession = StatsDaoDBHelper.getInstance().getSession();
        mAdStatsDao = mDaoSession.getAdStatsDao();
    }

    /**
     * 获取当前类单例对象.
     * @return
     */
    public static AdStatsHelper getInstance() {
        if (sInstance == null)
        {
            synchronized (AdStatsHelper.class)
            {
                if (sInstance == null)
                {
                    sInstance = new AdStatsHelper();
                }
            }
        }
        return sInstance;
    }

    public void saveAdStatsInfo(final AdStatsEntity statsEntity, final boolean notify)
    {
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    sInstance.mAdStatsDao.insertOrReplace(statsEntity);
                    if (notify) {
                        sInstance.notifyObserver(statsEntity);
                    }
                } catch (Throwable throwable) {
                    Logger.e(TAG, "saveAdStatsInfo: {}", throwable);
                } finally {
                    semaphore.release();
                }
            }
        });

    }

    public synchronized static void updateAdStats(AdStatsEntity statsEntity, boolean notify)
    {
        try
        {
            sInstance.mAdStatsDao.update(statsEntity);
            if (notify)
            {
                sInstance.notifyObserver(statsEntity);
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "updateAdStats: {}", throwable);
        }
    }

    public static StatsDaoSession getDaoSession()
    {
        return sInstance != null ? sInstance.mDaoSession : null;
    }

    public synchronized static void removeAdStats(String nodeName)
    {
        try
        {
            sInstance.mAdStatsDao.queryBuilder().where(AdStatsDao.Properties.NodeName.eq(nodeName)).buildDelete() .executeDeleteWithoutDetachingEntities();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeAdStats: {}", throwable);
        }
    }

    /**
     * 根据批次号进行删除.
     * @param batchNumber 批次号
     */
    public synchronized static void removeAdStatsForBatchNumber(String batchNumber)
    {
        try
        {
            sInstance.mAdStatsDao.queryBuilder().where(AdStatsDao.Properties.BatchNumber.eq(batchNumber)).buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeAdStatsForBatchNumber: {}", throwable);
        }
    }

    public synchronized static AdStatsEntity findAdtatsByNodeName(String nodeName)
    {
        try
        {
            AdStatsEntity statsEntity = sInstance.mAdStatsDao.queryBuilder().where(AdStatsDao.Properties.NodeName.eq(nodeName)).unique();
            return statsEntity;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "findAdtatsByNodeName: {}", throwable);
            return null;
        }
    }

    /**
     * 查询上报的统计信息集合
     * @return
     */
    public synchronized Map<String, List<AdStatsEntity>> findUploadDataMap()
    {
        try {
            //启动事务进行处理.
            return sInstance.mDaoSession.callInTx(new Callable<Map<String, List<AdStatsEntity>>>()
            {
                @Override
                public Map<String, List<AdStatsEntity>> call()
                {
                    //查询出批次号不为空的节点数据(之前上传失败的)
                    List<AdStatsEntity> adStatsList = sInstance.mAdStatsDao.queryBuilder().where(AdStatsDao.Properties.BatchNumber.isNotNull()).list();
                    //根据批次号进行分组.
                    Map<String, List<AdStatsEntity>> adStatsMap = new HashMap<>();
                    List<AdStatsEntity> tmpAdStatsList;
                    for (AdStatsEntity statsEntity : adStatsList)
                    {
                        if (statsEntity == null)
                        {
                            continue;
                        }
                        tmpAdStatsList = adStatsMap.get(statsEntity.batchNumber);
                        if (tmpAdStatsList == null)
                        {
                            tmpAdStatsList = new ArrayList<>();
                        }
                        tmpAdStatsList.add(statsEntity);
                        adStatsMap.put(statsEntity.batchNumber, tmpAdStatsList);
                    }
                    List<AdStatsEntity> dbStatsList = null;
                    do {
                        //获取新的批次号.
                        String batchNumber = String.valueOf(TimeTool.currentTimeMillis());
                        //查询按保存时间进行升序排列, 前一百条数据.
                        dbStatsList = sInstance.mAdStatsDao.queryBuilder().where(AdStatsDao.Properties.BatchNumber.isNull()).orderAsc(AdStatsDao.Properties.SaveTime).offset(0).limit(30).list();
                        if (dbStatsList != null && !dbStatsList.isEmpty())
                        {
                            for (AdStatsEntity stats : dbStatsList)
                            {
                                //设置批次号.
                                stats.setBatchNumber(batchNumber);
                                //修改批次号.
                                sInstance.mAdStatsDao.update(stats);
                            }
                            //记录该批次数据.
                            adStatsMap.put(batchNumber, dbStatsList);
                        }
                    } while (dbStatsList != null && !dbStatsList.isEmpty() && dbStatsList.size() >= 30);
                    return adStatsMap;
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "findAllAdStats: , 异常:{}", throwable);
            return null;
        }
    }

    public synchronized static List<AdStatsEntity> findAllAdStats(int offset, int pageCount)
    {
        try
        {
            return sInstance.mAdStatsDao.queryBuilder().orderDesc(AdStatsDao.Properties.SaveTime).offset(offset * pageCount).limit(pageCount).list();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "findAllAdStats: {}", throwable);
            return null;
        }
    }

    private void addObserver(AdStatsDaoObserver observer)
    {
        try
        {
            if (!mObservers.contains(observer))
            {
                mObservers.add(observer);
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "addObserver: {}", throwable);
        }
    }

    private void removeObserver(AdStatsDaoObserver observer)
    {
        try
        {
            mObservers.remove(observer);
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeObserver: {}", throwable);
        }
    }

    private void notifyObserver(final AdStatsEntity statsEntity)
    {
        try
        {
            if (Looper.myLooper() == Looper.getMainLooper())
            {
                excute(statsEntity);
            } else {
                BaseApplication.handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        excute(statsEntity);
                    }
                });
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "notifyObserver: {}", throwable);
        }
    }

    private void excute(AdStatsEntity statsEntity)
    {
        try
        {
            for (AdStatsDaoObserver observer : mObservers)
            {
                observer.onDataChange(statsEntity);
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "excute: {}", throwable);
        }
    }

    public void clearAll() {
        if (mAdStatsDao != null) {
            List<AdStatsEntity> list = mAdStatsDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    mAdStatsDao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 监听DB数据变化监听器
     */
    public interface AdStatsDaoObserver {
        void onDataChange(@NonNull AdStatsEntity statsEntity);
    }
}
