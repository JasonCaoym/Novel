package com.duoyue.mod.stats.common.upload;

import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZSchedulers;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.stats.FunctionStatsApi;
import com.duoyue.mod.stats.common.upload.request.AdStatsInfoReq;
import com.duoyue.mod.stats.common.upload.request.FuncStatsInfoReq;
import com.duoyue.mod.stats.common.upload.request.FuncStatsReq;
import com.duoyue.mod.stats.common.upload.response.FuncStatsResp;
import com.duoyue.mod.stats.data.entity.AdStatsEntity;
import com.duoyue.mod.stats.data.entity.FunctionStatsEntity;
import com.duoyue.mod.stats.data.helper.FunctionStatsHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 上报统计数据管理类
 * @author caoym
 * @data 2019/3/23  14:15
 */
public class UploadStatsMgr {
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#UploadStatsMgr";

    private static class Inner {
        private static volatile UploadStatsMgr INSTANCE = new UploadStatsMgr();
    }
    /**
     * 构造方法.
     */
    private UploadStatsMgr()
    {
    }

    /**
     * 创建当前类单例对象
     */
    public static UploadStatsMgr getInstance()
    {
        return Inner.INSTANCE;
    }

    /**
     * 创建广告统计请求上行参数.
     * @param adStatsList 广告统计节点列表.
     * @return
     */
    private List<AdStatsInfoReq> createReqParamForAd(List<AdStatsEntity> adStatsList)
    {
        if (adStatsList == null || adStatsList.isEmpty())
        {
            return null;
        }
        List<AdStatsInfoReq> adStatsInfoList = new ArrayList<>();
        AdStatsInfoReq adStatsInfo;
        for (AdStatsEntity adStats : adStatsList)
        {
            adStatsInfo = new AdStatsInfoReq();
            //节点类型(开始请求:"START"、拉取成功:"PULLED"、拉取失败:"PULLFAIL"、展示成功:"SHOWED",展示失败:"SHOWFAIL",点击广告:"CLICK)
            adStatsInfo.setOperator(adStats.getNodeName());
            //广告id
            adStatsInfo.setAdId(adStats.getAdSoltId());
            //广告位类型(1:开屏;2:精选列表;3:完结列表;4:新书列表;5:排行榜;6:书籍详情;7:分类列表;8:书架;9:阅读器章节末尾;10:目录;11:阅读器插页;12:激励视频).
            adStatsInfo.setAdSite(adStats.getAdSite());
            //广告类型(1:开屏;2:横屏;3:插屏;4:信息流;5:视频).
            adStatsInfo.setAdType(adStats.getAdType());
            //广告源(1:广点通2:穿山甲 3:百度)
            adStatsInfo.setOrigin(adStats.getOrigin());
            //次数
            adStatsInfo.setNum(adStats.getNodeCount() <= 0 ? 1 : adStats.getNodeCount());
            adStatsInfoList.add(adStatsInfo);
        }
        return adStatsInfoList;
    }

    /**
     * 上传功能相关统计数据
     */
    public long uploadFuncStats()
    {
        //判断当前UID是否为空.如果为空, 则先不上报.
        if (UserManager.getInstance().getUserInfo() == null || StringFormat.isEmpty(UserManager.getInstance().getUserInfo().uid))
        {
            Logger.e(TAG, "uploadFuncStats: UID为空, 暂不上报数据.");
            return 0;
        }
        //查询需要上报的节点数据集合.
        Map<String, List<FunctionStatsEntity>> functionStatsMap = FunctionStatsHelper.findUploadDataMap();
        if (functionStatsMap == null || functionStatsMap.isEmpty())
        {
            Logger.i(TAG, "uploadFuncStats: 无上报统计数据.");
            return 0;
        }
        Logger.i(TAG, "uploadFuncStats: {}", functionStatsMap.size());
        //延迟访问时间(分钟).
        long interval = 0;
        List<FuncStatsInfoReq> statsInfoList;
        //遍历要上报的数据.
        for (String key : functionStatsMap.keySet())
        {
            //创建请求上行参数.
            statsInfoList = getInstance().createReqParamForFunc(functionStatsMap.get(key));
            if (statsInfoList == null || statsInfoList.size() <= 0)
            {
                continue;
            }
            try {
                JsonResponse<FuncStatsResp> jsonResponse = new JsonPost.SyncPost<FuncStatsResp>().setRequest(new FuncStatsReq(key, statsInfoList)).setResponseType(FuncStatsResp.class).post();
                //如果上传成功, 则根据批次号删除对应数据.
                if (jsonResponse != null && jsonResponse.status == 1)
                {
                    Logger.i(TAG, "uploadFuncStats: 上报数据成功:{}, {}", key, jsonResponse);
                    //获取延时访问时间.
                    interval = jsonResponse.interval;
                    //上报数据成功.
                    FunctionStatsHelper.removeFuncStatsForBatchNumber(key);
                } else
                {
                    Logger.e(TAG, "uploadFuncStats: 上报数据失败:{}, {}", key, jsonResponse);
                }
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "uploadFuncStats: {}, {}", key, throwable);
            }
        }
        return interval;
    }

    /**
     * 创建功能统计请求上行参数.
     * @param funcStatsList 功能统计节点列表.
     * @return
     */
    private List<FuncStatsInfoReq> createReqParamForFunc(List<FunctionStatsEntity> funcStatsList)
    {
        if (funcStatsList == null || funcStatsList.isEmpty())
        {
            return null;
        }
        List<FuncStatsInfoReq> funcStatsInfoList = new ArrayList<>();
        FuncStatsInfoReq funcStatsInfo;
        JSONObject extInfoJSONObj;
        for (FunctionStatsEntity funcStats : funcStatsList)
        {
            funcStatsInfo = new FuncStatsInfoReq();
            //时长/分钟
            funcStatsInfo.setNum(String.valueOf(funcStats.nodeCount));
            //节点名称,启动节点"STTCN", 在线时长"EOLNL", 活跃节点ACTVU
            funcStatsInfo.setOperator(funcStats.nodeName);
            //书籍Id.
            funcStatsInfo.setBookId(funcStats.bookId);
            try
            {
                if (!StringFormat.isEmpty(funcStats.extInfo))
                {
                    extInfoJSONObj = new JSONObject(funcStats.extInfo);
                    //数据产生的目标来源, 如上报书籍曝光信息时, 上报分栏ID
                    funcStatsInfo.setTarget(extInfoJSONObj.optString("TARGET", ""));
                    //判断是否为退出阅读器节点.
                    if (FunctionStatsApi.READ_QUIT.equalsIgnoreCase(funcStats.nodeName))
                    {
                        //设置Num为阅读器停留时长(毫秒).
                        funcStatsInfo.setNum(String.valueOf(extInfoJSONObj.optLong("TIME", 0) / 60_000L));
                    }
                }
            } catch (Throwable throwable)
            {
                Logger.e(TAG, "createReqParamForFunc: {}, {}", funcStats.extInfo, throwable);
            }
            funcStatsInfoList.add(funcStatsInfo);
        }
        return funcStatsInfoList;
    }

    /**
     * 上传功能相关统计数据(实时上报)
     * @param nodeName 节点名称
     * @param bookId 书籍Id
     * @return
     */
    public synchronized void uploadFuncStatsForTimely(final String nodeName, final long bookId)
    {
        Logger.i(TAG, "uploadFuncStatsForTimely: {}, {}", nodeName, bookId);
        try
        {
            //创建事件参数.
            List<FuncStatsInfoReq> statsInfoList = new ArrayList<>();
            FuncStatsInfoReq funcStatsInfoReq = new FuncStatsInfoReq();
            //节点名称.
            funcStatsInfoReq.setOperator(nodeName);
            //书籍Id.
            funcStatsInfoReq.setBookId(bookId);
            statsInfoList.add(funcStatsInfoReq);
            //批次号追加00, 防止与离线上报数据的批次号重叠.
            new JsonPost.AsyncPost<FuncStatsResp>().setRequest(new FuncStatsReq(String.valueOf(TimeTool.currentTimeMillis()) + "00", statsInfoList)).setResponseType(FuncStatsResp.class)
                .subscribeOn(ZSchedulers.getInstance().io()).observeOn(AndroidSchedulers.mainThread()).post(new DisposableObserver<JsonResponse<FuncStatsResp>>(){
                @Override
                protected void onStart()
                {
                    super.onStart();
                }

                @Override
                public void onComplete()
                {
                    Logger.i(TAG, "uploadFuncStatsForTimely: onComplete:  {}, {}", nodeName, bookId);
                }

                @Override
                public void onNext(JsonResponse<FuncStatsResp> response)
                {
                    Logger.i(TAG, "uploadFuncStatsForTimely: onNext: {}, {}, {}", nodeName, bookId, response);
                    if (response == null || response.status != 1)
                    {
                        //上报数据失败, 添加到离线数据中上报.
                        FunctionStatsApi.addStats(nodeName, bookId);
                    }
                }

                @Override
                public void onError(Throwable throwable)
                {
                    Logger.e(TAG, "uploadFuncStatsForTimely: onError: {}, {}, {}", nodeName, bookId, throwable);
                    //上报数据失败, 添加到离线数据中上报.
                    FunctionStatsApi.addStats(nodeName, bookId);
                }
            });
        } catch (Throwable throwable)
        {
            Logger.e(TAG, "uploadFuncStatsForTimely: {}, {}, {}", nodeName, bookId, throwable);
            //上报数据失败, 添加到离线数据中上报.
            FunctionStatsApi.addStats(nodeName, bookId);
        }
    }
}
