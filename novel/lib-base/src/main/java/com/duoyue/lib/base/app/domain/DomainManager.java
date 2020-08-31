package com.duoyue.lib.base.app.domain;

import android.net.Uri;
import android.text.TextUtils;

import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.timer.TimerTask;
import com.duoyue.lib.base.cache.Cache;
import com.duoyue.lib.base.cache.GsonParser;
import com.duoyue.lib.base.cache.RamCache;
import com.duoyue.lib.base.cache.StringParser;
import com.duoyue.lib.base.log.Logger;

import java.io.File;
import java.net.InetAddress;

public class DomainManager
{
    private static final String TAG = "Base#DomainManager";

    private final String PATH_DOMAIN_CONFIG = "novel/domain/config";
    private final String PATH_DOMAIN_BUSINESS = "novel/domain/business";
    private final String PATH_DOMAIN_UPGRADE = "novel/domain/upgrade";

    private static class Holder
    {
        private static final DomainManager INSTANCE = new DomainManager();
    }

    public static DomainManager getInstance()
    {
        return Holder.INSTANCE;
    }

    private Cache<DomainConfig> configCache;
    private RamCache<String> businessCache;
    private RamCache<String> upgradeCache;

    private FetchConfigTask fetchConfigTask;
    private PingConfigTask pingConfigTask;

    private DomainManager()
    {
        File configFile = new File(BaseContext.getContext().getFilesDir(), PATH_DOMAIN_CONFIG);
        configCache = new Cache<>(configFile, new GsonParser<>(DomainConfig.class));
        File businessFile = new File(BaseContext.getContext().getFilesDir(), PATH_DOMAIN_BUSINESS);
        businessCache = new RamCache<>(businessFile, new StringParser());
        File upgradeFile = new File(BaseContext.getContext().getFilesDir(), PATH_DOMAIN_UPGRADE);
        upgradeCache = new RamCache<>(upgradeFile, new StringParser());

        fetchConfigTask = new FetchConfigTask();
        pingConfigTask = new PingConfigTask();
    }

    public TimerTask getFetchConfigTask()
    {
        return fetchConfigTask;
    }

    public TimerTask getPingConfigTask()
    {
        return pingConfigTask;
    }

    public String getUpgradeDomain()
    {
        String domain = upgradeCache.get();
        if (!TextUtils.isEmpty(domain))
        {
            return domain;
        }
        return Constants.DOMAIN_UPGRADE;
    }

    public String getBusinessDomain()
    {
        String domain = businessCache.get();
        if (!TextUtils.isEmpty(domain))
        {
            return domain;
        }
        return Constants.DOMAIN_BUSINESS;
    }

    private class FetchConfigTask extends TimerTask
    {
        @Override
        public String getAction()
        {
            return "DomainManager#FetchConfigTask";
        }

        @Override
        public long getPollTime()
        {
            return 6 * 60;
        }

        @Override
        public boolean requireNetwork()
        {
            return true;
        }

        @Override
        public long timeUp() throws Throwable
        {
            DomainRequest request = new DomainRequest();
            JsonResponse<DomainConfig> response = new JsonPost.SyncPost<DomainConfig>()
                    .setRequest(request)
                    .setResponseType(DomainConfig.class)
                    .post();
            configCache.set(response.data);
            return response.interval;
        }
    }

    private class PingConfigTask extends TimerTask
    {
        @Override
        public String getAction()
        {
            return "DomainManager#PingConfigTask";
        }

        @Override
        public long getPollTime()
        {
            return 60;
        }

        @Override
        public boolean requireNetwork()
        {
            return true;
        }

        @Override
        public long timeUp() throws Throwable
        {
            DomainConfig config = configCache.get();
            if (config != null)
            {
                ping(businessCache, config.appHosts);
                ping(upgradeCache, config.upgradedHosts);
            }
            return 0;
        }

        private void ping(RamCache<String> cache, String config)
        {
            if (config != null)
            {
                String[] urls = config.split(",");
                for (String url : urls)
                {
                    if (ping(url))
                    {
                        Logger.d(TAG, "ping: success! ", url);
                        cache.set(url);
                        return;
                    }
                }
            }
        }

        private boolean ping(String url)
        {
            try
            {
                String host = Uri.parse(url).getHost();
                InetAddress address = InetAddress.getByName(host);
                return address.isReachable(5000);
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "ping: failed! ", url);
            }
            return false;
        }
    }
}
