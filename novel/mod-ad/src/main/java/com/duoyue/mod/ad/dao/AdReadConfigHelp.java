package com.duoyue.mod.ad.dao;

import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.mod.ad.bean.AdReadConfigBean;
import com.duoyue.mod.ad.dao.gen.AdReadConfigBeanDao;
import com.duoyue.mod.ad.dao.gen.DaoSession;

import java.util.List;

public class AdReadConfigHelp {

    private static volatile AdReadConfigHelp sInstance;
    private static DaoSession daoSession;
    private static AdReadConfigBeanDao readBeanDao;


    public static AdReadConfigHelp getsInstance() {
        if (sInstance == null) {
            synchronized (AdReadConfigHelp.class) {
                if (sInstance == null) {
                    sInstance = new AdReadConfigHelp();
                    daoSession = AdDaoDbHelper.getInstance().getSession();
                    if (daoSession != null) {
                        readBeanDao = daoSession.getAdReadConfigBeanDao();
                    }
                }
            }
        }
        return sInstance;
    }

    public void saveRead(AdReadConfigBean ReadBean) {
        readBeanDao.insertOrReplace(ReadBean);
    }

    public void saveReads(List<AdReadConfigBean> ReadBeans) {
        if (readBeanDao != null) {
            readBeanDao.insertOrReplaceInTx(ReadBeans);
        }
    }

    public void saveReadWithAsync(final AdReadConfigBean ReadBean) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                readBeanDao.insertOrReplace(ReadBean);
            }
        });
    }

    public void saveReadsWithAsync(final List<AdReadConfigBean> ReadBeans) {
        daoSession.startAsyncSession().runInTx(new Runnable() {
            @Override
            public void run() {
                readBeanDao.insertOrReplaceInTx(ReadBeans);
            }
        });
    }

    public synchronized int getValueByKey(String key, int defaultValue) {
        try {
            if (readBeanDao == null) {
                return defaultValue;
            }
            List<AdReadConfigBean> list = readBeanDao.queryBuilder().where(
                    AdReadConfigBeanDao.Properties.ParamName.eq(key)).list();
            if (list != null && !list.isEmpty()) {
                AdReadConfigBean readBean = list.get(0);
                return StringFormat.parseInt(readBean.getParamValue(), defaultValue);
            }
        } catch (Exception ex) {

        }
        return defaultValue;
    }

    public synchronized String getValueByKey(String key) {
        try {
            if (readBeanDao == null) {
                return "";
            }
            List<AdReadConfigBean> list = readBeanDao.queryBuilder().where(
                    AdReadConfigBeanDao.Properties.ParamName.eq(key)).list();
            if (list != null && !list.isEmpty()) {
                AdReadConfigBean readBean = list.get(0);
                return readBean.getParamValue();
            }
        } catch (Exception ex) {

        }
        return "";
    }

    public void clearAll() {
        if (readBeanDao != null) {
            List<AdReadConfigBean> list = readBeanDao.loadAll();
            if (list != null && !list.isEmpty()) {
                try {
                    readBeanDao.deleteAll();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
