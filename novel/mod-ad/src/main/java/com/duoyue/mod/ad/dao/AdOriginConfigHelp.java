package com.duoyue.mod.ad.dao;

import com.duoyue.mod.ad.bean.AdOriginConfigBean;
import com.duoyue.mod.ad.dao.gen.AdOriginConfigBeanDao;
import com.duoyue.mod.ad.dao.gen.DaoSession;

import java.util.List;

public class AdOriginConfigHelp {
    private static final String TAG = "ad#AdOriginConfigHelp";
    private static volatile AdOriginConfigHelp sInstance;
    private static DaoSession daoSession;
    private static AdOriginConfigBeanDao originConfigBeanDao;


    public static AdOriginConfigHelp getsInstance() {
        if (sInstance == null) {
            synchronized (AdOriginConfigHelp.class) {
                if (sInstance == null) {
                    sInstance = new AdOriginConfigHelp();
                    daoSession = AdDaoDbHelper.getInstance().getSession();
                    originConfigBeanDao = daoSession.getAdOriginConfigBeanDao();
                }
            }
        }
        return sInstance;
    }

    public void saveOrigin(AdOriginConfigBean originBean) {
        originConfigBeanDao.insertOrReplace(originBean);
    }

    public void saveOrigins(List<AdOriginConfigBean> originBeans) {
        originConfigBeanDao.insertOrReplaceInTx(originBeans);
    }


    public void saveOriginWithAsync(final AdOriginConfigBean originBean) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                originConfigBeanDao.insertOrReplace(originBean);
            }
        });
    }

    public void saveOriginsWithAsync(final List<AdOriginConfigBean> originBeans) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                originConfigBeanDao.insertOrReplaceInTx(originBeans);
            }
        });
    }

    /**
     * 查找指定类型的广告源，不向下兼容
     * @param adType
     * @return
     */
    public List<AdOriginConfigBean> findAdOrigins(int adType, String grade) {
        return originConfigBeanDao.queryBuilder().where(
                        AdOriginConfigBeanDao.Properties.AdType.eq(adType),
                        AdOriginConfigBeanDao.Properties.Grade.eq(grade)).list();
    }

    /**
     * 查找指定类型的广告源，并向下兼容查询，排序 orderAsc
     * @param adType
     * @return
     */
    public List<AdOriginConfigBean> findAdOriginsWithCompate(int adType) {
        return originConfigBeanDao.queryBuilder().orderAsc(AdOriginConfigBeanDao.Properties.Grade)
                .where(AdOriginConfigBeanDao.Properties.AdType.eq(adType)).list();
    }

    public void clearAll() {
        if (originConfigBeanDao != null) {
            List<AdOriginConfigBean> list = originConfigBeanDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    originConfigBeanDao.deleteAll();
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        }
    }

    /**
     * 已根据等级进行排序
     * @return
     */
    public synchronized List<AdOriginConfigBean> findAllAdOrigins() {
        return originConfigBeanDao
                .queryBuilder()
                .orderAsc(AdOriginConfigBeanDao.Properties.AdType) // 升序
                .list();
    }

}
