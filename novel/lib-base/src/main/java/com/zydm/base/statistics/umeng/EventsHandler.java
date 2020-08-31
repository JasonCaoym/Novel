package com.zydm.base.statistics.umeng;


import android.text.TextUtils;
import com.umeng.analytics.MobclickAgent;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.data.base.MtMap;
import com.zydm.base.data.tools.DataUtils;
import com.zydm.base.utils.ClassUtils;
import com.zydm.base.utils.LogUtils;
import com.zydm.base.utils.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdy on 2017/4/21.
 */
class EventsHandler implements InvocationHandler {
    private static final String TAG = "EventsHandler";
    private static final String UMTAG = "MobclickAgent:";
    private static final String EMPTY = "";

    EventsHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] paramValues) throws Throwable {
        //TODO:不需要友盟进行功能统计 20190508 去除
        //EventInfo mEventInfo = parseEvent(method, paramValues);
        //onEvent(mEventInfo);
        return null;
    }

    private EventInfo parseEvent(Method method, Object[] paramValues) {
        List<String> paramKeys = getParamKeys(method);
        EventInfo eventInfo = new EventInfo();
        eventInfo.paramKeysSize = paramKeys.size();
        eventInfo.params = getParam(method, paramKeys, paramValues);

        check(eventInfo, paramValues);

        String name = method.getName();
        String regex = "$";
        if (name.indexOf(regex) > 0) {
            eventInfo.eventId = name.split(regex)[0];
        } else {
            eventInfo.eventId = name;
        }
        eventInfo.label = getLabel(eventInfo.params.size(), paramValues);
        eventInfo.value = getValue(eventInfo.paramKeysSize, paramValues);

        return eventInfo;
    }

    private void onEvent(final EventInfo mEventInfo) {
        LogUtils.d(TAG, "UMEventsHandler-onEvent():mEventInfo = " + mEventInfo);
        final String eventId = mEventInfo.eventId;
        final MtMap<String, String> param = mEventInfo.params;
        String label = mEventInfo.label;
        final int value = mEventInfo.value;

        if (!TextUtils.isEmpty(label)) {
            //LogUtils.d(TAG, "event--label");
            MobclickAgent.onEvent(BaseApplication.context.globalContext, mEventInfo.eventId, mEventInfo.label);
            return;
        }

        if (Integer.MIN_VALUE != value) {
            //LogUtils.d(TAG, "event--map-du");
            MobclickAgent.onEventValue(BaseApplication.context.globalContext, eventId, param, value);
            return;
        }

        if (param.isEmpty()) {
            //LogUtils.d(TAG, "event");
            MobclickAgent.onEvent(BaseApplication.context.globalContext, eventId);
            return;
        }

        //LogUtils.d(TAG, "event--map");
        MobclickAgent.onEvent(BaseApplication.context.globalContext, eventId, param);
    }

    /**
     * 检查统计事件参数列表中的参数是否合格
     */
    private void check(EventInfo eventInfo, Object[] paramValues) {
        String eventId = eventInfo.eventId;
        int paramsSize = eventInfo.paramKeysSize;
        int valuesLength = null == paramValues ? 0 : paramValues.length;
        if (0 == valuesLength || valuesLength == paramsSize) {
            return;
        }

        if (paramsSize + 1 != valuesLength) {
            handleException("EventMethods." + eventId + "(): @param error! @paramCount:" + paramsSize + " paramValueCount:" + paramValues.length);
        }

        Object lastParam = paramValues[valuesLength - 1];
        if (lastParam instanceof Integer) {
            return;
        }

        if (lastParam instanceof String && eventInfo.params.size() == 0) {
            return;
        }

        handleException(eventId + ":the input param is not match the UMeng request! ");
    }

    /**
     * 得到所有的带注解的参数值
     *
     * @param method: 方法名（以事件名命名的）
     * @return
     */
    private MtMap<String, String> getParam(Method method, List<String> paramNames, Object[] paramValues) {
        MtMap<String, String> param = new MtMap<>();
//        addDefaultParams(method, param);
        addParams(paramNames, paramValues, param);
        return param;
    }

//    private void addDefaultParams(Method method, MtMap<String, String> param) {
//        param.clear();
//        Annotation[] annotations = method.getAnnotations();
//        if (annotations == null || annotations.length == 0) return;
//
//        CommonParam defaultParam = ClassUtils.findAnnotation(annotations, CommonParam.class);
//        if (defaultParam == null) return;
//
//        String[] params = defaultParam.params();
//        for (String paramKey : params) {
//            String value = CustomEventMgr.getInstance().get(paramKey);
//            if (TextUtils.isEmpty(value)) {
//                handleException(method.getName() + ":The key " + paramKey + " corresponding value is null,please init it!");
//                return;
//            } else {
//                putParam(paramKey, value, param);
//            }
//        }
//    }

    private void addParams(List<String> paramNames, Object[] paramValues, MtMap<String, String> param) {
        int nameCount = paramNames.size();  //参数列表中带注解的参数个数
        for (int i = 0; i < nameCount; i++) {
            putParam(paramNames.get(i), paramValues[i], param);
        }
    }

    /**
     * @param paramSize:   全部注解的个数
     * @param paramValues: 参数列表里面的所有值
     * @return
     */
    private String getLabel(int paramSize, Object[] paramValues) {
        int paramValuesLength = null == paramValues ? 0 : paramValues.length;
//        LogUtils.d(TAG, "UMEventsHandler-getLabel()-参数列表中所有参数的个数:paramValuesLength = " + paramValuesLength + ", 全部注解的个数:paramSize = " + paramSize);
        if (paramValuesLength != 1 || paramSize != 0) {
            return EMPTY;
        }

        Object lastParam = DataUtils.getItem(paramValues, paramValuesLength - 1);
//        LogUtils.d(TAG, "UMEventsHandler-getLabel():lastParam = " + lastParam);
        if ((lastParam instanceof String)) {
            return String.valueOf(lastParam);
        }

        return EMPTY;
    }

    /**
     * @param paramValues: 参数列表里面的所有值
     * @return
     */
    private int getValue(int paramSize, Object[] paramValues) {
        int paramValuesLength = null == paramValues ? 0 : paramValues.length;
        if (0 == paramValuesLength || paramSize == paramValuesLength) {
            return Integer.MIN_VALUE;
        }

        Object lastParam = paramValues[paramValuesLength - 1];
//        LogUtils.d(TAG, "UMEventsHandler-getDu():lastParam = " + lastParam);
        if (lastParam instanceof Integer) {
            return Integer.parseInt(String.valueOf(lastParam));
        }

        return Integer.MIN_VALUE;
    }

    /**
     * 得到参数表中的参数的key
     *
     * @param method
     * @return
     */
    private static List<String> getParamKeys(Method method) {
        List<String> parameterNames = new ArrayList<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            Param param = ClassUtils.findAnnotation(parameterAnnotation, Param.class);
            if (param == null) {
                continue;
            }
            parameterNames.add(param.value());
        }
        return parameterNames;
    }

    private void putParam(String key, Object value, MtMap<String, String> params) {
        if (StringUtils.isBlank(key) || value == null) return;
        params.put(key, String.valueOf(value));
    }

    private void handleException(String message) {
        if (BaseApplication.context.isTestEnv()) {
            throw new RuntimeException(message);
        }
    }

    class EventInfo {
        public String eventId = EMPTY;
        public MtMap<String, String> params = new MtMap<>();
        public String label = EMPTY;
        public int value = Integer.MIN_VALUE;
        public int paramKeysSize = 0;  //参数列表中带注解的参数个数

        @Override
        public String toString() {
            return "EventInfo{" +
                    "eventId='" + eventId + '\'' +
                    ", params=" + params +
                    ", label='" + label + '\'' +
                    ", value=" + value +
                    ", paramsSize=" + paramKeysSize +
                    '}';
        }
    }
}
