package com.duoyue.app.common.mgr;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;
import com.coloros.mcssdk.PushManager;
import com.coloros.mcssdk.callback.PushAdapter;
import com.coloros.mcssdk.mode.ErrorCode;
import com.duoyue.app.common.data.request.push.UploadPushDeviceInfoReq;
import com.duoyue.lib.base.BaseContext;
import com.duoyue.lib.base.app.Constants;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.duoyue.lib.base.app.user.UserInfo;
import com.duoyue.lib.base.app.user.UserManager;
import com.duoyue.lib.base.devices.PhoneUtil;
import com.duoyue.lib.base.log.Logger;
import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zydm.base.common.BaseApplication;
import com.zydm.base.utils.SPUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 推送管理类
 */
public class PushMgr {
    /**
     * 日志Tag
     */
    private static final String TAG = "App#PushMgr";
    public static final String SET_VIVO_ALIAS = "set_vivo_alias";
    public static final String SET_XIAOMI_ALIAS = "set_xiaomi_alias";
    public static final String SET_OPPO_REGISTERID = "set_oppo_registerid";
    public static final String SET_HUAWEI_TOKEN = "set_huawei_token";

    /**
     * 当前类对象
     */
    private static volatile PushMgr sInstance;

    private PushMgr() {
    }

    /**
     * 创建当前类单例对象
     */
    private static void createInstance() {
        if (sInstance == null) {
            synchronized (PushMgr.class) {
                if (sInstance == null) {
                    sInstance = new PushMgr();
                }
            }
        }
    }

    /**
     * @return 只要返回不是""，则是EMUI版本
     */
    public static String getEmuiVersion() {
        String emuiVerion = "";
        Class<?>[] clsArray = new Class<?>[]{String.class};
        Object[] objArray = new Object[]{"ro.build.version.emui"};
        try {
            Class<?> SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method get = SystemPropertiesClass.getDeclaredMethod("get", clsArray);
            String version = (String) get.invoke(SystemPropertiesClass, objArray);
            if (!TextUtils.isEmpty(version)) {
                return version;
            }
        } catch (Exception e) {
        }
        return emuiVerion;
    }

    public static void connectHW(Activity activity) {
        if (TextUtils.isEmpty(PushMgr.getEmuiVersion())) {
            Logger.d(TAG, "connectHW: 非华为系统");
            return;
        }
        HMSAgent.connect(activity, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                Logger.d(TAG, "HMS connect end:" + rst);
                getHWToken();
            }
        });
    }

    /**
     * 获取token
     */

    public static void getHWToken() {
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rst) {
                Logger.d(TAG, "获取token : " + rst);
            }
        });
    }

    /**
     * 开启vivo推送
     */
    public static void openVivoPush() {
        if (!PushClient.getInstance(BaseApplication.context.getApplication()).isSupport()) {
            Logger.d(TAG, "openVivoPush: 系统不支持");
            return;
        }
        PushClient.getInstance(BaseApplication.context.getApplication()).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                // TODO: 开关状态处理
                if (state != 0) {
                    Logger.d(TAG, "openVivoPush:打开push异常[" + state + "]");
                } else {
                    Logger.d(TAG, "openVivoPush:打开push成功");
                }
            }
        });
    }

    /**
     * 设置vivo推送别名
     */
    public static void setVivoAlias() {
        if (!PushClient.getInstance(BaseApplication.context.getApplication()).isSupport()) {
            Logger.d(TAG, "setVivoAlias: 系统不支持");
            return;
        }
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }
        final String alias = userInfo.uid.replace("-", "");
        Logger.d(TAG, "onStateChanged: " + alias);
        PushClient.getInstance(BaseApplication.context.getApplication()).bindAlias(alias, new IPushActionListener() {

            @Override
            public void onStateChanged(int state) {
                if (state != 0) {
                    Logger.d(TAG, "onStateChanged: 设置别名异常[" + state + "]");
                } else {
                    Logger.d(TAG, "onStateChanged: 设置别名成功" + alias);
                    SPUtils.INSTANCE.putString(PushMgr.SET_VIVO_ALIAS, alias);
                    PushMgr.uploadPushDeviceInfo();

                }
            }
        });
    }

    /**
     * 删除vivo推送别名
     */
    public static void delVivoAlias(String alias) {
        if (!PushClient.getInstance(BaseApplication.context.getApplication()).isSupport()) {
            Logger.d(TAG, "openVivoPush: 系统不支持");
            return;
        }
        PushClient.getInstance(BaseApplication.context.getApplication()).unBindAlias(alias, new IPushActionListener() {

            @Override
            public void onStateChanged(int state) {
                if (state != 0) {
                    Logger.d(TAG, "onStateChanged: 取消别名异常[" + state + "]");
                } else {
                    Logger.d(TAG, "onStateChanged: 取消别名成功");
                }
            }
        });
    }

    /**
     * 检查vivo别名是否设置,未设置时设置,别名不同时重新设置
     */
    public static void checkVivoAlias() {
        if (!PushClient.getInstance(BaseApplication.context.getApplication()).isSupport()) {
            Logger.d(TAG, "openVivoPush: 系统不支持");
            return;
        }
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }
        String oldVivoUid = SPUtils.INSTANCE.getString(PushMgr.SET_VIVO_ALIAS);
        if (TextUtils.isEmpty(oldVivoUid)) {//为空,注册极光别名
            PushMgr.setVivoAlias();
        } else if (!TextUtils.equals(oldVivoUid, userInfo.uid.replace("-", ""))) {//现有uid和已注册的uid不同,删除原有别名,重新注册(vivo自动以最后一次绑定的别名为主,不需要删除原有别名)
            PushMgr.setVivoAlias();
        }
    }

    /**
     * 初始化OPPO推送
     */
    public static void initOppoPush() {
        //在执行Oppo推送注册之前，需要先判断当前平台是否支持Oppo推送
        Logger.d(TAG, "onRegister: oppo推送注册开始");
        if (PushManager.isSupportPush(BaseApplication.context.getApplication())) {
            PushManager.getInstance().register(BaseApplication.context.getApplication(), Constants.OPPO_APPKEY, Constants.OPPO_APPSECRET, new PushAdapter() {
                @Override
                public void onRegister(int i, String registerId) {
                    if (i == ErrorCode.SUCCESS) {
                        //注册成功
                        Logger.d(TAG, "onRegister: oppo推送注册成功，registerId=" + registerId);
                        SPUtils.INSTANCE.putString(PushMgr.SET_OPPO_REGISTERID, registerId);
                    } else {
                        //注册失败
                        Logger.d(TAG, "onRegister: OPPO推送注册失败");
                    }
                }
            });
        }
    }


    /**
     * 判断是否小米手机
     * @return
     */
    public static boolean isXiaomi() {
        return TextUtils.equals(getPhoneBrand(), "Xiaomi");
    }

    /**
     * 初始化小米推送
     *
     * @param context
     */
    public static void registerXimiPush(Context context) {
        if (isXiaomi() && shouldInit(context)) {
            //初始化push推送服务
            MiPushClient.registerPush(context, Constants.XIAOMI_APPID, Constants.XIAOMI_KEY);
        }
    }

    /**
     * 设置小米推送别名
     *
     * @param context
     */
    public static void setXimiAlias(Context context) {
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }
        final String alias = userInfo.uid.replace("-", "");
        Logger.d(TAG, "setXimiAlias: " + alias);
        MiPushClient.setAlias(context, alias, null);
    }

    /**
     * 删除小米别名
     *
     * @param context
     * @param oldXiaomiUid
     */
    public static void deleteXiaoMiAlias(Context context, String oldXiaomiUid) {
        Logger.d(TAG, "deleteXiaoMiAlias: " + oldXiaomiUid);
        MiPushClient.unsetAlias(context, oldXiaomiUid, null);
    }

    /**
     * 检查小米别名是否设置,未设置时设置,别名不同时重新设置
     */
    public static void checkXiaomiAlias(Context context) {
        if (!isXiaomi()) {
            Logger.d(TAG, "checkXiaomiAlias: 系统不支持");
            return;
        }
        UserInfo userInfo = UserManager.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }
        String oldXiaomiUid = SPUtils.INSTANCE.getString(PushMgr.SET_XIAOMI_ALIAS);
        if (TextUtils.isEmpty(oldXiaomiUid)) {//为空,注册小米别名
            PushMgr.setXimiAlias(context);
        } else if (!TextUtils.equals(oldXiaomiUid, userInfo.uid.replace("-", ""))) {//现有uid和已注册的uid不同,删除原有别名,重新注册
            PushMgr.setXimiAlias(context);
        }
    }

    /**
     * 小米推送需要在主线程注册
     *
     * @param context
     * @return
     */
    public static boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 小米Push开启日志
     * @param context
     */
    public static void openDebugLog(Context context) {
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Logger.d(TAG, "log: " + content);
            }

            @Override
            public void log(String content) {
                Logger.d(TAG, "log: " + content);
            }
        };
        com.xiaomi.mipush.sdk.Logger.setLogger(context, newLogger);
    }

    /**
     * 获取手机产商
     *
     * @return HUAWEI  vivo  OPPO  Xiaomi
     */
    public static String getPhoneBrand() {
        String name = Build.MANUFACTURER;
        switch (name) {
            case "HUAWEI":
                break;
            case "vivo":
                break;
            case "OPPO":
                break;
            case "Xiaomi":
                break;
            case "samsung":
                break;
            case "Coolpad":
                break;
            case "Meizu":
                break;
            case "Sony":
                break;
            case "LG":
                break;
            default:
                break;
        }
        return name;
    }

    /**
     * 获取四大厂商标识
     *
     * @return 0不是四大厂商 1:huawei;2:oppo;3:vivo;4:xiaomi
     */
    public static int getBrand() {
        String phoneBrand = getPhoneBrand();
        if (TextUtils.equals(phoneBrand, "HUAWEI")) {
            return 1;
        } else if (TextUtils.equals(phoneBrand, "vivo")) {
            return 3;
        } else if (TextUtils.equals(phoneBrand, "OPPO")) {
            return 2;
        } else if (TextUtils.equals(phoneBrand, "Xiaomi")) {
            return 4;
        } else {
            return 0;
        }
    }

    /**
     * 上报推送设备标识接口
     * 设备推送标识(华为的为token OPPO的为registerid vivo/小米的为alias)
     */
    public static void uploadPushDeviceInfo() {
        //判断当前网络是否可用.
        if (!PhoneUtil.isNetworkAvailable(BaseContext.getContext())) {
            Logger.e(TAG, "uploadPushDeviceInfo: 网络不可用.");
            return;
        }

        if (getBrand() == 0) {
            return;
        }
        try {
            UploadPushDeviceInfoReq uploadPushDeviceInfoReq = new UploadPushDeviceInfoReq();
            String targetId = "";
            if (getBrand() == 1) {
                targetId = SPUtils.INSTANCE.getString(PushMgr.SET_HUAWEI_TOKEN, "");
            } else if (getBrand() == 2) {
                targetId = SPUtils.INSTANCE.getString(PushMgr.SET_OPPO_REGISTERID, "");
            } else if (getBrand() == 3) {
                targetId = SPUtils.INSTANCE.getString(PushMgr.SET_VIVO_ALIAS, "");
            } else if (getBrand() == 4) {
                targetId = SPUtils.INSTANCE.getString(PushMgr.SET_XIAOMI_ALIAS, "");
            }
            if (TextUtils.isEmpty(targetId)){
                Logger.e(TAG, "uploadPushDeviceInfo: targetId为空");
                return;
            }
            uploadPushDeviceInfoReq.setTargetId(targetId);
            uploadPushDeviceInfoReq.setType(getBrand());
            new JsonPost.AsyncPost<JsonResponse>()
                    .setRequest(uploadPushDeviceInfoReq)
                    .setResponseType(JsonResponse.class)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .post(new DisposableObserver<JsonResponse<JsonResponse>>() {
                        @Override
                        public void onNext(JsonResponse jsonResponse) {
                            if (jsonResponse != null && jsonResponse.status == 1) {
                                Logger.d(TAG, "onNext: 厂商标识上报成功");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(TAG, "厂商标识上报错误");
                        }

                        @Override
                        public void onComplete() {
                            Logger.e(TAG, "厂商标识上报完成");
                        }
                    });


        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }
}
