package com.duoyue.lib.base.app.user;

import android.os.Environment;
import android.text.TextUtils;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.cache.Cache;
import com.duoyue.lib.base.cache.GsonParser;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.crypto.MD5;

import java.io.File;
import java.util.UUID;

public class UserManager
{
    private static class Holder
    {
        private static final UserManager INSTANCE = new UserManager();
    }

    public static UserManager getInstance()
    {
        return Holder.INSTANCE;
    }

    private final String PATH_M_USER_ID = "novel/user/id";
    private final String PATH_S_USER_ID_1 = ".system.dy";
    private final String PATH_S_USER_ID_2 = ".config/data.dy";

    private final String PATH_USER_INFO = "novel/user/info";

    private Cache<MidInfo> mIdCache;
    private Cache<MidInfo> sIdCache1;
    private Cache<MidInfo> sIdCache2;

    private RamCache<UserInfo> infoCache;

    private UserManager()
    {
        File mIdFile = new File(BaseContext.getContext().getFilesDir(), PATH_M_USER_ID);
        mIdCache = new Cache<>(mIdFile, new GsonParser<>(MidInfo.class));

        File sIdFile1 = new File(Environment.getExternalStorageDirectory(), PATH_S_USER_ID_1);
        sIdCache1 = new Cache<>(sIdFile1, new GsonParser<>(MidInfo.class));

        File sIdFile2 = new File(Environment.getExternalStorageDirectory(), PATH_S_USER_ID_2);
        sIdCache2 = new Cache<>(sIdFile2, new GsonParser<>(MidInfo.class));

        File infoFile = new File(BaseContext.getContext().getFilesDir(), PATH_USER_INFO);
        infoCache = new RamCache<>(infoFile, new GsonParser<>(UserInfo.class));
    }

    private String mMid;

    public String getMid()
    {
        for (int i = 0; TextUtils.isEmpty(mMid); i++)
        {
            switch (i)
            {
                case 0:
                    mMid = getMid(mIdCache);
                    break;
                case 1:
                    mMid = getMid(sIdCache1);
                    break;
                case 2:
                    mMid = getMid(sIdCache2);
                    break;
                case 3:
                    mMid = generateMid();
                    break;
            }
        }
        setMid(mMid);
        return mMid;
    }

    private String getMid(Cache<MidInfo> cache)
    {
        MidInfo info = cache.get();
        if (info != null)
        {
            if (!TextUtils.isEmpty(info.id) && !TextUtils.isEmpty(info.crc))
            {
                if (info.crc.equals(MD5.getCRC32(info.id)))
                {
                    return info.id;
                }
            }
        }
        return null;
    }

    private void setMid(String mid)
    {
        MidInfo info = new MidInfo();
        info.id = mid;
        info.crc = MD5.getCRC32(mid);
        mIdCache.set(info);
        sIdCache1.set(info);
        sIdCache2.set(info);
    }

    private String generateMid()
    {
        return "mid" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    public synchronized UserInfo getUserInfo()
    {
        return infoCache.get();
    }

    public synchronized void setUserInfo(UserInfo info)
    {
        infoCache.set(info);
    }
}
