package com.duoyue.mod.ad.dao;

import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdPositionConfigBean;
import com.duoyue.mod.ad.dao.gen.AdPositionConfigBeanDao;
import com.duoyue.mod.ad.dao.gen.DaoSession;

import java.util.List;

public class AdPositionConfigHelp {
    private static final String TAG = "ad#AdPositionConfigHelp";
    private static volatile AdPositionConfigHelp sInstance;
    private static DaoSession daoSession;
    private static AdPositionConfigBeanDao positionBeanDao;


    public static AdPositionConfigHelp getsInstance() {
        if (sInstance == null) {
            synchronized (AdPositionConfigHelp.class) {
                if (sInstance == null) {
                    sInstance = new AdPositionConfigHelp();
                    daoSession = AdDaoDbHelper.getInstance().getSession();
                    positionBeanDao = daoSession.getAdPositionConfigBeanDao();
                }
            }
        }
        return sInstance;
    }

    public void savePosition(AdPositionConfigBean PositionBean) {
        positionBeanDao.insertOrReplace(PositionBean);
    }

    public void savePositions(List<AdPositionConfigBean> PositionBeans) {
        positionBeanDao.insertOrReplaceInTx(PositionBeans);
    }


    public void savePositionWithAsync(final AdPositionConfigBean PositionBean) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                long id = positionBeanDao.insertOrReplace(PositionBean);
                Logger.d(TAG, "position update: " + (id>0?true:false));
            }
        });
    }

    public void savePositionsWithAsync(final List<AdPositionConfigBean> PositionBeans) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                positionBeanDao.insertOrReplaceInTx(PositionBeans);
            }
        });
    }

    /**
     *
     * @param adSite
     * @return
     */
    public List<AdPositionConfigBean> findAdPositions(int adSite) {
        return positionBeanDao.queryBuilder().where(
                        AdPositionConfigBeanDao.Properties.AdSite.eq(adSite)).list();
    }

    /**
     *
     * @param adType
     * @return
     */
    public List<AdPositionConfigBean> findAdPositionsWithCompate(int adType, String grade) {
        return positionBeanDao.queryBuilder().orderAsc(AdPositionConfigBeanDao.Properties.Grade)
                .where(AdPositionConfigBeanDao.Properties.AdType.eq(adType),
                        AdPositionConfigBeanDao.Properties.Grade.eq(grade)).list();
    }

    public void clearAll() {
        if (positionBeanDao != null) {
            List<AdPositionConfigBean> list = positionBeanDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    positionBeanDao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取有效的广告为：显示次数为0的过滤
     * @return
     */
    public synchronized List<AdPositionConfigBean> findAvailableAdPos() {
        return positionBeanDao
                .queryBuilder()
//                .where(AdPositionConfigBeanDao.Properties.ShowNum.gt(0))
                .orderDesc(AdPositionConfigBeanDao.Properties.AdType)
                .list();
    }

}
