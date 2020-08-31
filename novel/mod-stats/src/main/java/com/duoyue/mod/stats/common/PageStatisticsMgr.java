package com.duoyue.mod.stats.common;

import android.text.TextUtils;
import com.duoyue.lib.base.format.StringFormat;
import com.duoyue.lib.base.log.Logger;
import com.duoyue.lib.base.time.TimeTool;
import com.duoyue.mod.stats.data.entity.FuncPageStatsEntity;
import com.duoyue.mod.stats.data.helper.FuncPagetatsHelper;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 统计管理类对象
 *
 * @author caoym
 * @data 2019/3/23  14:13
 */
public class PageStatisticsMgr {
    /**
     * 日志Tag
     */
    private static final String TAG = "Stats#NewStatistics";

    /**
     * 构造方法
     */
    private PageStatisticsMgr() {
    }


    public static void addStatsForFunc(final long bookId, final String parentId, String currId, final String modelId,
                                       final String operator, final String source, final String field1) {
        Logger.i(TAG, "addStatsForFunc: operator ={}, bookId = {}, modelId = {} ", operator, bookId, modelId);
        if (TextUtils.isEmpty(operator)) {
            return;
        }
        //创建统计节点数据对象.
        FuncPageStatsEntity statsEntity = new FuncPageStatsEntity();
        //设置书籍Id.
        statsEntity.setBookId(bookId);
        // 设置上一个页面
        statsEntity.setPrevPageId(parentId);
        // 设置当前界面
        statsEntity.setCurrPageId(currId);
        // 设置类型
        statsEntity.setModelId(modelId);
        //设置节点名称.
        statsEntity.setNodeName(operator);
        // 设置来源
        statsEntity.setSource(source);
        //设置格外字段
        if (!TextUtils.isEmpty(field1)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("field1", field1);
                statsEntity.setExtInfo(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //设置当前日期.
        statsEntity.setNodeDate(TimeTool.getCurrentDate(TimeTool.DATE_FORMAT_SMALL_01));
        //保存时间.
        statsEntity.setSaveTime(TimeTool.currentTimeMillis());
        //保存到数据库.
        FuncPagetatsHelper.getInstance().saveStatsInfo(statsEntity);
    }

    /**
     * 创建Target扩展参数(categoryId)
     *
     * @param paramJSONObj
     * @param target
     * @return
     */
    public static JSONObject createParamTarget(JSONObject paramJSONObj, String target) {
        if (!StringFormat.isEmpty(target)) {
            try {
                if (paramJSONObj == null) {
                    paramJSONObj = new JSONObject();
                }
                paramJSONObj.put("TARGET", target);
            } catch (Throwable throwable) {
                Logger.e(TAG, "createParamTarget: {}", throwable);
            }
        }
        return paramJSONObj;
    }

    /**
     * 创建Target扩展参数(阅读器停留时长)
     *
     * @param paramJSONObj
     * @param time
     * @return
     */
    public static JSONObject createParamTime(JSONObject paramJSONObj, long time) {
        if (time > 0) {
            try {
                if (paramJSONObj == null) {
                    paramJSONObj = new JSONObject();
                }
                paramJSONObj.put("TIME", time);
            } catch (Throwable throwable) {
                Logger.e(TAG, "createParamTime: {}", throwable);
            }
        }
        return paramJSONObj;
    }

}
