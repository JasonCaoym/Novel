package com.duoyue.mod.stats.data.helper;

import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZExecutorService;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.stats.common.FunPageStatsConstants;
import com.duoyue.mod.stats.data.PageStatsDaoDbHelper;
import com.duoyue.mod.stats.data.entity.FuncPageStatsEntity;
import com.duoyue.mod.stats.data.gen.DaoSession;
import com.duoyue.mod.stats.data.gen.FuncPageStatsEntityDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

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
public class FuncPagetatsHelper {
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats@FunctionStatsHelper";

    /**
     * 当前类单例对象
     */
    private static volatile FuncPagetatsHelper sInstance;

    /**
     * StatsDaoSession对象
     */
    private DaoSession mDaoSession;

    /**
     * FuncPageStatsEntityDao对象
     */
    private FuncPageStatsEntityDao mFuncPageStatsEntityDao;

    private Semaphore semaphore = new Semaphore(1, true);

    private FuncPagetatsHelper() {
        initDao();
    }

    private void initDao() {
        mDaoSession = PageStatsDaoDbHelper.getInstance().getSession();
        if (mDaoSession != null) {
            mFuncPageStatsEntityDao = mDaoSession.getFuncPageStatsEntityDao();
        }
    }

    /**
     * 获取当前类单例对象.
     *
     * @return
     */
    public synchronized static FuncPagetatsHelper getInstance() {
        if (sInstance == null) {
            synchronized (FuncPagetatsHelper.class) {
                if (sInstance == null) {
                    sInstance = new FuncPagetatsHelper();
                }
            }
        }
        return sInstance;
    }

    public static WhereCondition eq(Property property, Object value) {
        if (value == null) {
            value = "";
        }
        return property.eq(value);
    }

    /**
     * 保存或更新节点
     *
     * @param statsEntity
     */
    public void saveStatsInfo(final FuncPageStatsEntity statsEntity) {
        if (mFuncPageStatsEntityDao == null) {
            initDao();
        }
        if (mFuncPageStatsEntityDao == null) {
            initDao();
            return;
        }
        ZExecutorService.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                //查询数据.
                try {
                    semaphore.acquire();
                    FuncPageStatsEntity funcStatsEntity;
                    if (StringFormat.isEmpty(statsEntity.extInfo)) {
                        funcStatsEntity = sInstance.mFuncPageStatsEntityDao.queryBuilder().where(
                                eq(FuncPageStatsEntityDao.Properties.NodeName, statsEntity.getNodeName()),
                                eq(FuncPageStatsEntityDao.Properties.PrevPageId, statsEntity.getPrevPageId()),
                                eq(FuncPageStatsEntityDao.Properties.CurrPageId, statsEntity.getCurrPageId()),
                                eq(FuncPageStatsEntityDao.Properties.BookId, statsEntity.getBookId()),
                                eq(FuncPageStatsEntityDao.Properties.ModelId, statsEntity.getModelId()),
                                eq(FuncPageStatsEntityDao.Properties.Source, statsEntity.getSource()),
                                eq(FuncPageStatsEntityDao.Properties.NodeDate, statsEntity.getNodeDate()),
                                FuncPageStatsEntityDao.Properties.ExtInfo.isNull(),
                                FuncPageStatsEntityDao.Properties.BatchNumber.isNull()).unique();
                    } else {
                        funcStatsEntity = sInstance.mFuncPageStatsEntityDao.queryBuilder().where(
                                eq(FuncPageStatsEntityDao.Properties.NodeName, statsEntity.getNodeName()),
                                eq(FuncPageStatsEntityDao.Properties.PrevPageId, statsEntity.getPrevPageId()),
                                eq(FuncPageStatsEntityDao.Properties.CurrPageId, statsEntity.getCurrPageId()),
                                eq(FuncPageStatsEntityDao.Properties.BookId, statsEntity.getBookId()),
                                eq(FuncPageStatsEntityDao.Properties.ModelId, statsEntity.getModelId()),
                                eq(FuncPageStatsEntityDao.Properties.Source, statsEntity.getSource()),
                                eq(FuncPageStatsEntityDao.Properties.NodeDate, statsEntity.getNodeDate()),
                                eq(FuncPageStatsEntityDao.Properties.ExtInfo, statsEntity.getExtInfo()),
                                FuncPageStatsEntityDao.Properties.ExtInfo.isNotNull(),
                                FuncPageStatsEntityDao.Properties.BatchNumber.isNull()).unique();
                    }
                    if (funcStatsEntity != null) {
                        //递增节点数量.
                        funcStatsEntity.setNodeCount(funcStatsEntity.getNodeCount() >= 0 ? funcStatsEntity.getNodeCount() + 1 : 1);
                        //修改数据.
                        sInstance.mFuncPageStatsEntityDao.update(funcStatsEntity);
                    } else {
                        //设置节点数为1.
                        statsEntity.setNodeCount(1);
                        sInstance.mFuncPageStatsEntityDao.insert(statsEntity);
                    }
                } catch (Throwable throwable) {
                    Logger.e(TAG, "saveFuncStatsInfo: {}", throwable);
                } finally {
                    semaphore.release();
                }
            }
        });
    }

    /**
     * 根据批次号进行删除.
     *
     * @param batchNumber 批次号
     */
    public synchronized void removeStatsByBatchNumber(String batchNumber) {
        if (mFuncPageStatsEntityDao == null) {
            initDao();
        }
        if (mFuncPageStatsEntityDao == null) {
            initDao();
            return;
        }
        try {
            sInstance.mFuncPageStatsEntityDao.queryBuilder().where(FuncPageStatsEntityDao.Properties.BatchNumber.eq(batchNumber)).buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Throwable throwable) {
            Logger.e(TAG, "removeFuncStatsForBatchNumber: {}", throwable);
        }
    }

    public synchronized FuncPageStatsEntity findFuncStatsByNodeName(String nodeName) {
        try {
            List<FuncPageStatsEntity> list = mFuncPageStatsEntityDao.queryBuilder().where(FuncPageStatsEntityDao.Properties.NodeName.eq(nodeName)).list();
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
            return null;
        } catch (Throwable throwable) {
            Logger.e(TAG, "findFuncStatsByNodeName: {}", throwable);
            return null;
        }
    }

    /**
     * 查询上报的统计信息集合
     *
     * @return
     */
    public synchronized Map<String, List<FuncPageStatsEntity>> findUploadDataMap() {
        if (mFuncPageStatsEntityDao == null || mDaoSession == null) {
            initDao();
        }
        if (mFuncPageStatsEntityDao == null) {
            initDao();
            return new HashMap<>();
        }
        try {
            //启动事务进行处理.
            return sInstance.mDaoSession.callInTx(new Callable<Map<String, List<FuncPageStatsEntity>>>() {
                @Override
                public Map<String, List<FuncPageStatsEntity>> call() {
                    //查询出批次号不为空的节点数据(之前上传失败的)
                    List<FuncPageStatsEntity> funcStatsList = sInstance.mFuncPageStatsEntityDao.queryBuilder().where(FuncPageStatsEntityDao.Properties.BatchNumber.isNotNull()).list();
                    //根据批次号进行分组.
                    Map<String, List<FuncPageStatsEntity>> funcStatsMap = new HashMap<>();
                    List<FuncPageStatsEntity> tmpFuncStatsList;
                    for (FuncPageStatsEntity statsEntity : funcStatsList) {
                        if (statsEntity == null) {
                            continue;
                        }
                        tmpFuncStatsList = funcStatsMap.get(statsEntity.batchNumber);
                        if (tmpFuncStatsList == null) {
                            tmpFuncStatsList = new ArrayList<>();
                        }
                        tmpFuncStatsList.add(statsEntity);
                        funcStatsMap.put(statsEntity.batchNumber, tmpFuncStatsList);
                    }
                    List<FuncPageStatsEntity> dbStatsList = null;

                    do {
                        //获取新的批次号.
                        String batchNumber = String.valueOf(TimeTool.currentTimeMillis());
                        //查询按保存时间进行升序排列, 前一百条数据.
                        dbStatsList = sInstance.mFuncPageStatsEntityDao.queryBuilder().where(FuncPageStatsEntityDao.Properties.BatchNumber.isNull())
                                .orderAsc(FuncPageStatsEntityDao.Properties.SaveTime).offset(0).limit(30).list();
                        if (dbStatsList != null && !dbStatsList.isEmpty()) {
                            for (FuncPageStatsEntity stats : dbStatsList) {
                                //设置批次号.
                                stats.setBatchNumber(batchNumber);
                                //修改批次号.
                                sInstance.mFuncPageStatsEntityDao.update(stats);
                            }
                            //记录该批次数据.
                            funcStatsMap.put(batchNumber, dbStatsList);
                        }
                    } while (dbStatsList != null && !dbStatsList.isEmpty() && dbStatsList.size() >= 30);
                    return funcStatsMap;
                }
            });
        } catch (Throwable throwable) {
            Logger.e(TAG, "<统计>findAllFuncStats: , 异常:{}", throwable);
            return null;
        }
    }

    /**
     * 查询当日未上报的阅读时长.
     *
     * @return
     */
    public synchronized int findCurrDayReadingTime() {
        if (mFuncPageStatsEntityDao == null || mDaoSession == null) {
            initDao();
        }
        if (mFuncPageStatsEntityDao == null) {
            initDao();
            return 0;
        }
        try {
            //查询当日阅读时长数据列表.
            QueryBuilder queryBuilder = sInstance.mFuncPageStatsEntityDao.queryBuilder();
            List list = queryBuilder.where(FuncPageStatsEntityDao.Properties.NodeName.eq(FunPageStatsConstants.READ_DURATION),
                    queryBuilder.and(FuncPageStatsEntityDao.Properties.SaveTime.ge(TimeTool.getCurrDayBeginTime()),
                            FuncPageStatsEntityDao.Properties.SaveTime.le(TimeTool.currentTimeMillis()))).list();
            return list != null ? list.size() : 0;
        } catch (Throwable throwable) {
            Logger.e(TAG, "findCurrDayReadingTime: {}", throwable);
            return 0;
        }
    }

    /**
     * 查询所有未上报的阅读时长.
     *
     * @return
     */
    public synchronized static int findTotalReadingTime() {
        getInstance();
        if (sInstance.mFuncPageStatsEntityDao == null || sInstance.mDaoSession == null) {
            sInstance.initDao();
        }
        if (sInstance.mFuncPageStatsEntityDao == null) {
            sInstance.initDao();
            return 0;
        }
        try {
            //查询当日阅读时长数据列表.
            QueryBuilder queryBuilder = sInstance.mFuncPageStatsEntityDao.queryBuilder();
            List list = queryBuilder.where(FuncPageStatsEntityDao.Properties.NodeName.eq(FunPageStatsConstants.READ_DURATION)).list();
            return list != null ? list.size() : 0;
        } catch (Throwable throwable) {
            Logger.e(TAG, "findTotalReadingTime: {}", throwable);
            return 0;
        }
    }

    public void clearAll() {
        if (sInstance.mFuncPageStatsEntityDao == null || sInstance.mDaoSession == null) {
            sInstance.initDao();
        }
        if (mFuncPageStatsEntityDao != null) {
            List<FuncPageStatsEntity> list = mFuncPageStatsEntityDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    mFuncPageStatsEntityDao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
