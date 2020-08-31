package com.duoyue.mod.stats.common.upload;

import android.text.TextUtils;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.threadpool.ZSchedulers;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.stats.FuncPageStatsApi;
import com.duoyue.mod.stats.common.upload.request.FuncPageStatsInfo;
import com.duoyue.mod.stats.common.upload.request.FuncPageStatsReq;
import com.duoyue.mod.stats.common.upload.request.FuncPageUpdateStatsReq;
import com.duoyue.mod.stats.common.upload.response.FuncStatsResp;
import com.duoyue.mod.stats.data.entity.FuncPageStatsEntity;
import com.duoyue.mod.stats.data.helper.FuncPagetatsHelper;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageStatsUploadMgr {

    private static String TAG = "Stats#PageStatsUploadMgr";

    private static class InnerClass {
        public static final PageStatsUploadMgr INSTANCE = new PageStatsUploadMgr();
    }

    public static PageStatsUploadMgr getInstance() {
        return InnerClass.INSTANCE;
    }

    /**
     * 上传功能相关统计数据(实时上报)
     *
     * @return
     */
    public void uploadFuncStatsNow(final long bookId, final String prevPageId, final String currPageId, final int modelId,
                                                final String operator, final String source) {
        uploadFuncStatsNow(bookId, prevPageId, currPageId, String.valueOf(modelId), operator, source, "");
    }

    public void uploadFuncStatsNoNow(final long bookId, final String prevPageId, final String currPageId, final String modelId,
                                                  final String operator, final String source) {
        uploadFuncStatsNoNow(bookId, prevPageId, currPageId, modelId, operator, source, "");
    }

    /**
     * 上传功能相关统计数据(实时上报)   1.2.0类型修改
     *
     * @return
     */
    public void uploadFuncStatsNow(final long bookId, final String prevPageId, final String currPageId, final String modelId,
                                                final String operator, final String source) {
        uploadFuncStatsNow(bookId, prevPageId, currPageId, modelId, operator, source, "");
    }

    /**
     * 热更新下载安装  等  需要单独上报(实时上报)   1.2.3添加
     *
     * @return
     */
    public void uploadUpdateFuncStatsNow(final long bookId, final String prevPageId, final String currPageId, final int modelId,
                                                      final String operator, final String source) {
        Logger.i(TAG, "新节点热更新OR更新实时上报: operator = " + operator + ", bookId = " + bookId + ", modelId = " + modelId
                + ", prevPageId = " + prevPageId + ", currPageId = " + currPageId + ", source = " + source + ", field1 = " + "");
        if (TextUtils.isEmpty(operator)) {
            return;
        }
        try {
            //创建事件参数.
            List<FuncPageStatsInfo> statsInfoList = new ArrayList<>();
            FuncPageStatsInfo funcStatsInfoReq = new FuncPageStatsInfo();
            funcStatsInfoReq.setBookId(bookId);
            funcStatsInfoReq.setPrevPageId(prevPageId);
            funcStatsInfoReq.setCurrPageId(currPageId);
            funcStatsInfoReq.setModelId(String.valueOf(modelId));
            funcStatsInfoReq.setOperator(operator);
            funcStatsInfoReq.setNum(1);
            funcStatsInfoReq.setSource(source);
            funcStatsInfoReq.setField1("");
            statsInfoList.add(funcStatsInfoReq);
            FuncPageUpdateStatsReq funcPageUpdateStatsReq = new FuncPageUpdateStatsReq();
            funcPageUpdateStatsReq.setBatchNumber(String.valueOf(TimeTool.currentTimeMillis()) + funcStatsInfoReq.hashCode());
            funcPageUpdateStatsReq.setPageStats(statsInfoList);
            new JsonPost.AsyncPost<FuncStatsResp>().setRequest(funcPageUpdateStatsReq).setResponseType(FuncStatsResp.class)
                    .subscribeOn(Schedulers.io()).observeOn(ZSchedulers.getInstance().io()).post(new DisposableObserver<JsonResponse<FuncStatsResp>>() {
                @Override
                protected void onStart() {
                    super.onStart();
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void onNext(JsonResponse<FuncStatsResp> response) {
                    if (response == null || response.status != 1) {
                        //上报数据失败, 添加到离线数据中上报.
                        FuncPageStatsApi.addStatsForFunc(bookId, prevPageId, currPageId, String.valueOf(modelId), operator, source, "");
                        Logger.i(TAG, "新节点热更新OR更新实时上报失败: " + operator);
                    } else {
                        Logger.i(TAG, "新节点热更新OR更新实时上报成功: " + operator);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    Logger.e(TAG, "新节点热更新OR更新实时上报上报: onError: {}, {}, {}", operator, bookId, throwable);
                    //上报数据失败, 添加到离线数据中上报.
                    FuncPageStatsApi.addStatsForFunc(bookId, prevPageId, currPageId, String.valueOf(modelId), operator, source, "");
                }
            });
        } catch (Throwable throwable) {
            Logger.e(TAG, "新节点热更新OR更新实时上报 {}, {}, {}", operator, bookId, throwable);
            //上报数据失败, 添加到离线数据中上报.
            FuncPageStatsApi.addStatsForFunc(bookId, prevPageId, currPageId, String.valueOf(modelId), operator, source, "");
        }
    }

    /**
     * 上传功能相关统计数据(实时上报)   1.2.1修改
     *
     * @return
     */
    public void uploadFuncStatsNow(final long bookId, final String prevPageId, final String currPageId, final String modelId,
                                   final String operator, final String source, final String field1) {
        Logger.i(TAG, "新节点实时上报: operator = " + operator + ", bookId = " + bookId + ", modelId = " + modelId
                + ", prevPageId = " + prevPageId + ", currPageId = " + currPageId + ", source = " + source + ", field1 = " + field1);
        try {
            if (TextUtils.isEmpty(operator)) {
                return;
            }
            //创建事件参数.
            List<FuncPageStatsInfo> statsInfoList = new ArrayList<>();
            FuncPageStatsInfo funcStatsInfoReq = new FuncPageStatsInfo();
            funcStatsInfoReq.setBookId(bookId);
            funcStatsInfoReq.setPrevPageId(prevPageId);
            funcStatsInfoReq.setCurrPageId(currPageId);
            funcStatsInfoReq.setModelId(modelId);
            funcStatsInfoReq.setOperator(operator);
            funcStatsInfoReq.setNum(1);
            funcStatsInfoReq.setSource(source);
            funcStatsInfoReq.setField1(field1);

            statsInfoList.add(funcStatsInfoReq);
            //批次号追加00, 防止与离线上报数据的批次号重叠.
            new JsonPost.AsyncPost<FuncStatsResp>().setRequest(new FuncPageStatsReq(String.valueOf(TimeTool.currentTimeMillis()) + funcStatsInfoReq.hashCode(), statsInfoList)).setResponseType(FuncStatsResp.class)
                    .subscribeOn(Schedulers.io()).observeOn(ZSchedulers.getInstance().io()).post(new DisposableObserver<JsonResponse<FuncStatsResp>>() {
                @Override
                protected void onStart() {
                    super.onStart();
                }

                @Override
                public void onComplete() {
                }

                @Override
                public void onNext(JsonResponse<FuncStatsResp> response) {
                    if (response == null || response.status != 1) {
                        //上报数据失败, 添加到离线数据中上报.
                        FuncPageStatsApi.addStatsForFunc(bookId, prevPageId, currPageId, modelId, operator, source, field1);
                        Logger.i(TAG, "新节点实时上报失败: " + operator);
                    } else {
                        Logger.i(TAG, "新节点实时上报成功: " + operator);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    Logger.e(TAG, "新节点实时上报: onError: {}, {}, {}", operator, bookId, throwable);
                    //上报数据失败, 添加到离线数据中上报.
                    FuncPageStatsApi.addStatsForFunc(bookId, prevPageId, currPageId, modelId, operator, source, field1);
                }
            });
        } catch (Throwable throwable) {
            Logger.e(TAG, "新节点实时上报: {}, {}, {}", operator, bookId, throwable);
            //上报数据失败, 添加到离线数据中上报.
            FuncPageStatsApi.addStatsForFunc(bookId, prevPageId, currPageId, modelId, operator, source, field1);
        }
    }

    /**
     * 上传功能相关统计数据(非实时上报)   1.2.3
     * 先保存到本地数据库 数据库定时（eg：1分钟）后定时上报节点数据
     *
     * @return
     */
    public void uploadFuncStatsNoNow(final long bookId, final String prevPageId, final String currPageId, final String modelId,
                                     final String operator, final String source, final String field1) {
        FuncPageStatsApi.addStatsForFunc(bookId, prevPageId, currPageId, modelId, operator, source, field1);
    }

    /**
     * 定时上报（包括定时和上传失败的节点）上传功能相关统计数据
     */
    public synchronized long uploadFuncStats() {
        //判断当前UID是否为空.如果为空, 则先不上报.
        if (UserManager.getInstance().getUserInfo() == null || StringFormat.isEmpty(UserManager.getInstance().getUserInfo().uid)) {
            Logger.e(TAG, "新节点: UID为空, 暂不上报数据.");
            return 0;
        }
        //查询需要上报的节点数据集合.
        Map<String, List<FuncPageStatsEntity>> functionStatsMap = FuncPagetatsHelper.getInstance().findUploadDataMap();
        if (functionStatsMap == null || functionStatsMap.isEmpty()) {
            Logger.i(TAG, "新节点: 无可数据上报.");
            return 0;
        }
        //延迟访问时间(分钟).
        long interval = 0;
        List<FuncPageStatsInfo> statsInfoList;
        //遍历要上报的数据.
        for (String key : functionStatsMap.keySet()) {
            //创建请求上行参数.
            statsInfoList = createReqParamForFunc(functionStatsMap.get(key));
            if (statsInfoList == null || statsInfoList.size() <= 0) {
                continue;
            }
            try {
                JsonResponse<FuncStatsResp> jsonResponse = new JsonPost.SyncPost<FuncStatsResp>().setRequest(
                        new FuncPageStatsReq(key, statsInfoList)).setResponseType(FuncStatsResp.class).post();
                //如果上传成功, 则根据批次号删除对应数据.
                if (jsonResponse != null && jsonResponse.status == 1) {
                    Logger.i(TAG, "新节点: 上报数据成功:{}, {}", key, jsonResponse);
                    //获取延时访问时间.
                    interval = jsonResponse.interval;
                    //上报数据成功.
                    FuncPagetatsHelper.getInstance().removeStatsByBatchNumber(key);
                } else {
                    Logger.e(TAG, "新节点: 上报数据失败:{}, {}", key, jsonResponse);
                }
            } catch (Throwable throwable) {
                Logger.e(TAG, "新节点: {}, {}", key, throwable);
            }
        }
        return interval;
    }

    /**
     * 创建功能统计请求上行参数.
     *
     * @param funcStatsList 功能统计节点列表.
     * @return
     */
    private List<FuncPageStatsInfo> createReqParamForFunc(List<FuncPageStatsEntity> funcStatsList) {
        if (funcStatsList == null || funcStatsList.isEmpty()) {
            return null;
        }
        List<FuncPageStatsInfo> funcStatsInfoList = new ArrayList<>();
        FuncPageStatsInfo funcStatsInfo;
        JSONObject extInfoJSONObj;
        for (FuncPageStatsEntity funcStats : funcStatsList) {
            funcStatsInfo = new FuncPageStatsInfo();
            //书籍Id.
            funcStatsInfo.setBookId(funcStats.getBookId());
            // 上一个界面名称
            funcStatsInfo.setPrevPageId(funcStats.getPrevPageId());
            // 类型/状态
            funcStatsInfo.setModelId(funcStats.getModelId());
            //节点名称,启动节点"STTCN", 在线时长"EOLNL", 活跃节点ACTVU
            funcStatsInfo.setOperator(funcStats.getNodeName());
            funcStatsInfo.setSource(funcStats.getSource());
            // 叠加次数
            funcStatsInfo.setNum(funcStats.nodeCount);
            funcStatsInfo.setCurrPageId(funcStats.getCurrPageId());
            String extInfo = funcStats.getExtInfo();
            if (extInfo != null) {
                try {
                    JSONObject jsonObject = new JSONObject(extInfo);
                    String field1 = (String) jsonObject.opt("field1");
                    funcStatsInfo.setField1(field1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

//            try
//            {
//                if (!StringFormat.isEmpty(funcStats.extInfo))
//                {
//                    extInfoJSONObj = new JSONObject(funcStats.extInfo);
//                    //数据产生的目标来源, 如上报书籍曝光信息时, 上报分栏ID
//                    funcStatsInfo.setTarget(extInfoJSONObj.optString("TARGET", ""));
//                    //判断是否为退出阅读器节点.
//                    if (FunctionStatsApi.READ_QUIT.equalsIgnoreCase(funcStats.nodeName))
//                    {
//                        //设置Num为阅读器停留时长(毫秒).
//                        funcStatsInfo.setNum(String.valueOf(extInfoJSONObj.optLong("TIME", 0)));
//                    }
//                }
//            } catch (Throwable throwable)
//            {
//                Logger.e(TAG, "createReqParamForFunc: {}, {}", funcStats.extInfo, throwable);
//            }
            funcStatsInfoList.add(funcStatsInfo);
        }
        return funcStatsInfoList;
    }

    /**
     * 读取当日未上报阅读时长.
     *
     * @return
     */
    public int getCurrDayReadingTime() {
        return FuncPagetatsHelper.getInstance().findCurrDayReadingTime();
    }

    /**
     * 读取所有未上报阅读时长.
     *
     * @return
     */
    public int getTotalReadingTime() {
        return FuncPagetatsHelper.getInstance().findTotalReadingTime();
    }
}
