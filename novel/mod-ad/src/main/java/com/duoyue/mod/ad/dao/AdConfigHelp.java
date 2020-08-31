package com.duoyue.mod.ad.dao;

import com.duoyue.lib.base.log.Logger;
import com.duoyue.mod.ad.bean.AdConfigBean;
import com.duoyue.mod.ad.dao.gen.AdConfigBeanDao;
import com.duoyue.mod.ad.dao.gen.DaoSession;

import java.util.List;

public class AdConfigHelp {
    private static final String TAG = "ad#AdConfigHelp";
    private static volatile AdConfigHelp sInstance;
    private static DaoSession daoSession;
    private static AdConfigBeanDao dao;


    public static AdConfigHelp getsInstance() {
        if (sInstance == null) {
            synchronized (AdConfigHelp.class) {
                if (sInstance == null) {
                    sInstance = new AdConfigHelp();
                    daoSession = AdDaoDbHelper.getInstance().getSession();
                    if (daoSession != null) {
                        dao = daoSession.getAdConfigBeanDao();
                    }
                }
            }
        }
        return sInstance;
    }

    public void saveAdConfig(AdConfigBean originBean) {
        try {
            if (isNull()) {
                return;
            }
            if (dao != null) {
                dao.insertOrReplace(originBean);
            }
        } catch (Exception ex) {

        }
    }

    public void saveAdConfigWithAsync(final AdConfigBean originBean) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                dao.insertOrReplace(originBean);
            }
        });
    }

    public void saveAdConfigWithAsync(final List<AdConfigBean> originBeans) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                dao.insertOrReplaceInTx(originBeans);
            }
        });
    }

    /**
     * @return
     */
    public AdConfigBean findAdConfig(String channalCode) {
        try {
            if (isNull()) {
                return null;
            }
            List<AdConfigBean> list = dao.queryBuilder().where(
                    AdConfigBeanDao.Properties.ChannelCode.eq(channalCode)).list();
            if (list != null && !list.isEmpty()) {
                Logger.e("ad_config", "channalCode:" + channalCode + ",有数据： " + list.size());
                return list.get(0);
            } else {
                Logger.e("ad_config", "channalCode: " + channalCode + ", 有数据0");
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public void deleteByChannalCode(String channalCode) {
        try {
            if (isNull()) {
                return;
            }
            List<AdConfigBean> list = dao.queryBuilder().where(
                    AdConfigBeanDao.Properties.ChannelCode.eq(channalCode)).list();
            if (list != null && !list.isEmpty()) {
                Logger.e("ad_config", "channalCode:" + channalCode + ",有数据： " + list.size());
                for (AdConfigBean configBean : list) {
                    dao.delete(configBean);
                }
            } else {
                Logger.e("ad_config", "channalCode: " + channalCode + ", 有数据0");
            }
        } catch (Exception ex) {

        }
    }

    private boolean isNull() {
        if (dao == null) {
            try {
                daoSession = AdDaoDbHelper.getInstance().getSession();
                if (daoSession != null) {
                    dao = daoSession.getAdConfigBeanDao();
                }
                if (dao == null) {
                    return true;
                }
            } catch (Exception ex) {
            }
        }
        return false;
    }

    public void clearAll() {
        if (dao != null) {
            List<AdConfigBean> list = dao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    dao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
