package com.duoyue.mod.stats.data.helper;

import android.os.Looper;
import android.support.annotation.NonNull;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.data.StatsDaoDBHelper;
import com.duoyue.mod.stats.data.StatsDaoSession;
import com.duoyue.mod.stats.data.dao.FunctionStatsDao;
import com.duoyue.mod.stats.data.entity.FunctionStatsEntity;
import com.zydm.base.common.BaseApplication;
import org.greenrobot.greendao.query.QueryBuilder;

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
public class FunctionStatsHelper
{
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats@FunctionStatsHelper";

    /**
     * 当前类单例对象
     */
    private static volatile FunctionStatsHelper sInstance;

    /**
     * StatsDaoSession对象
     */
    private StatsDaoSession mDaoSession;

    /**
     * FunctionStatsDao对象
     */
    private FunctionStatsDao mFunctionStatsDao;

    private Semaphore semaphore = new Semaphore(1, true);

    /**
     * 监听器列表.
     */
    private ArrayList<FunctionStatsDaoObserver> mObservers = new ArrayList<>();

    private FunctionStatsHelper()
    {
        mDaoSession = StatsDaoDBHelper.getInstance().getSession();
        mFunctionStatsDao = mDaoSession.getFuncStatsDao();
    }

    /**
     * 获取当前类单例对象.
     * @return
     */
    public static FunctionStatsHelper getInstance()
    {
        if (sInstance == null)
        {
            synchronized (FunctionStatsHelper.class)
            {
                if (sInstance == null)
                {
                    sInstance = new FunctionStatsHelper();
                }
            }
        }
        return sInstance;
    }

    public synchronized void saveFuncStatsInfo(final FunctionStatsEntity statsEntity, final boolean notify)
    {
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    //查询数据.
                    FunctionStatsEntity funcStatsEntity;
                    if (StringFormat.isEmpty(statsEntity.extInfo)) {
                        funcStatsEntity = sInstance.mFunctionStatsDao.queryBuilder().where(FunctionStatsDao.Properties.NodeName.eq(statsEntity.getNodeName()), FunctionStatsDao.Properties.BookId.eq(statsEntity.getBookId()),
                                FunctionStatsDao.Properties.ExtInfo.isNull(), FunctionStatsDao.Properties.NodeDate.eq(statsEntity.getNodeDate()), FunctionStatsDao.Properties.BatchNumber.isNull()).unique();
                    } else {
                        funcStatsEntity = sInstance.mFunctionStatsDao.queryBuilder().where(FunctionStatsDao.Properties.NodeName.eq(statsEntity.getNodeName()), FunctionStatsDao.Properties.BookId.eq(statsEntity.getBookId()),
                                FunctionStatsDao.Properties.ExtInfo.isNotNull(), FunctionStatsDao.Properties.ExtInfo.eq(statsEntity.getExtInfo()), FunctionStatsDao.Properties.NodeDate.eq(statsEntity.getNodeDate()), FunctionStatsDao.Properties.BatchNumber.isNull()).unique();
                    }
                    if (funcStatsEntity != null) {
                        //递增节点数量.
                        funcStatsEntity.setNodeCount(funcStatsEntity.getNodeCount() >= 0 ? funcStatsEntity.getNodeCount() + 1 : 1);
                        //修改数据.
                        sInstance.mFunctionStatsDao.update(funcStatsEntity);
                    } else {
                        //设置节点数为1.
                        statsEntity.setNodeCount(1);
                        sInstance.mFunctionStatsDao.insert(statsEntity);
                    }
                    if (notify) {
                        sInstance.notifyObserver(statsEntity);
                    }
                } catch (Throwable throwable) {
                    Logger.e(TAG, "saveFuncStatsInfo: {}", throwable);
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    public synchronized void updateFuncStats(FunctionStatsEntity statsEntity, boolean notify)
    {
        try
        {
            sInstance.mFunctionStatsDao.update(statsEntity);
            if (notify)
            {
                sInstance.notifyObserver(statsEntity);
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "updateFuncStats: {}", throwable);
        }
    }

    public static StatsDaoSession getDaoSession()
    {
        return sInstance != null ? sInstance.mDaoSession : null;
    }

    public synchronized void removeFuncStats(String nodeName)
    {
        try
        {
            sInstance.mFunctionStatsDao.queryBuilder().where(FunctionStatsDao.Properties.NodeName.eq(nodeName)).buildDelete() .executeDeleteWithoutDetachingEntities();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeFuncStats: {}", throwable);
        }
    }

    /**
     * 根据批次号进行删除.
     * @param batchNumber 批次号
     */
    public synchronized static void removeFuncStatsForBatchNumber(String batchNumber)
    {
        try
        {
            sInstance.mFunctionStatsDao.queryBuilder().where(FunctionStatsDao.Properties.BatchNumber.eq(batchNumber)).buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "removeFuncStatsForBatchNumber: {}", throwable);
        }
    }

    public synchronized FunctionStatsEntity findFuncStatsByNodeName(String nodeName)
    {
        try
        {
            FunctionStatsEntity statsEntity = sInstance.mFunctionStatsDao.queryBuilder().where(FunctionStatsDao.Properties.NodeName.eq(nodeName)).unique();
            return statsEntity;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "findFuncStatsByNodeName: {}", throwable);
            return null;
        }
    }

    /**
     * 查询上报的统计信息集合
     * @return
     */
    public synchronized static Map<String, List<FunctionStatsEntity>> findUploadDataMap()
    {
        try {
            //启动事务进行处理.
            return getInstance().mDaoSession.callInTx(new Callable<Map<String, List<FunctionStatsEntity>>>()
            {
                @Override
                public Map<String, List<FunctionStatsEntity>> call()
                {
                    //查询出批次号不为空的节点数据(之前上传失败的)
                    List<FunctionStatsEntity> funcStatsList = sInstance.mFunctionStatsDao.queryBuilder().where(FunctionStatsDao.Properties.BatchNumber.isNotNull()).list();
                    //根据批次号进行分组.
                    Map<String, List<FunctionStatsEntity>> funcStatsMap = new HashMap<>();
                    List<FunctionStatsEntity> tmpFuncStatsList;
                    for (FunctionStatsEntity statsEntity : funcStatsList)
                    {
                        if (statsEntity == null)
                        {
                            continue;
                        }
                        tmpFuncStatsList = funcStatsMap.get(statsEntity.batchNumber);
                        if (tmpFuncStatsList == null)
                        {
                            tmpFuncStatsList = new ArrayList<>();
                        }
                        tmpFuncStatsList.add(statsEntity);
                        funcStatsMap.put(statsEntity.batchNumber, tmpFuncStatsList);
                    }
                    List<FunctionStatsEntity> dbStatsList = null;
                    do {
                        //获取新的批次号.
                        String batchNumber = String.valueOf(TimeTool.currentTimeMillis());
                        //查询按保存时间进行升序排列, 前一百条数据.
                        dbStatsList = sInstance.mFunctionStatsDao.queryBuilder().where(FunctionStatsDao.Properties.BatchNumber.isNull()).orderAsc(FunctionStatsDao.Properties.SaveTime).offset(0).limit(30).list();
                        if (dbStatsList != null && !dbStatsList.isEmpty())
                        {
                            for (FunctionStatsEntity stats : dbStatsList)
                            {
                                //设置批次号.
                                stats.setBatchNumber(batchNumber);
                                //修改批次号.
                                sInstance.mFunctionStatsDao.update(stats);
                            }
                            //记录该批次数据.
                            funcStatsMap.put(batchNumber, dbStatsList);
                        }
                    } while (dbStatsList != null && !dbStatsList.isEmpty() && dbStatsList.size() >= 30);
                    return funcStatsMap;
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "<统计>findAllFuncStats: , 异常:{}", throwable);
            return null;
        }
    }

    public synchronized  List<FunctionStatsEntity> findAllFuncStats(int offset, int pageCount)
    {
        try
        {
            return sInstance.mFunctionStatsDao.queryBuilder().orderDesc(FunctionStatsDao.Properties.SaveTime).offset(offset * pageCount).limit(pageCount).list();
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "findAllFuncStats: {}", throwable);
            return null;
        }
    }


    /**
     * 查询当日未上报的阅读时长.
     * @return
     */
    public synchronized  int findCurrDayReadingTime()
    {
        try {
            //查询当日阅读时长数据列表.
            QueryBuilder queryBuilder = sInstance.mFunctionStatsDao.queryBuilder();
            List list = queryBuilder.where(FunctionStatsDao.Properties.NodeName.eq(FunctionStatsApi.READING_TIME), queryBuilder.and(FunctionStatsDao.Properties.SaveTime.ge(TimeTool.getCurrDayBeginTime()), FunctionStatsDao.Properties.SaveTime.le(TimeTool.currentTimeMillis()))).list();
            return list != null ? list.size() : 0;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "findCurrDayReadingTime: {}", throwable);
            return 0;
        }
    }

    /**
     * 查询所有未上报的阅读时长.
     * @return
     */
    public synchronized int findTotalReadingTime()
    {
        try {
            //查询当日阅读时长数据列表.
            QueryBuilder queryBuilder = sInstance.mFunctionStatsDao.queryBuilder();
            List list = queryBuilder.where(FunctionStatsDao.Properties.NodeName.eq(FunctionStatsApi.READING_TIME)).list();
            return list != null ? list.size() : 0;
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "findTotalReadingTime: {}", throwable);
            return 0;
        }
    }

    private void notifyObserver(final FunctionStatsEntity statsEntity)
    {
        try {
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

    private void excute(FunctionStatsEntity statsEntity)
    {
        try {
            for (FunctionStatsDaoObserver observer : mObservers)
            {
                observer.onDataChange(statsEntity);
            }
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "excute: {}", throwable);
        }
    }

    public void clearAll() {
        if (mFunctionStatsDao != null) {
            List<FunctionStatsEntity> list = mFunctionStatsDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    mFunctionStatsDao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 监听DB数据变化监听器
     */
    public interface FunctionStatsDaoObserver {
        void onDataChange(@NonNull FunctionStatsEntity statsEntity);
    }
}
