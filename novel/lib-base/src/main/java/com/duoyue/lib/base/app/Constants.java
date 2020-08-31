package com.duoyue.lib.base.app;

public class Constants {
    /**
     * 应用Id.
     */
    public static final long APP_ID = 13;

    /**
     * 是否为爱告定制包.
     */
    public static final boolean IS_AIGAO = false;

    /**
     * 是否为测试环境(false:正式环境;true:测试环境)
     */
    private static final boolean IS_TEST = false;

    /**
     * 接口协议版本号.
     */
    public static final int PROTOCOL_CODE = 2;
    //升级接口-内网服务器.
    //public static final String DOMAIN_UPGRADE = "http://192.168.0.128:6100";
    //public static final String DOMAIN_UPGRADE = "http://112.95.161.57:60021";
    //升级接口-外网服务器.
    //public static final String DOMAIN_UPGRADE = "http://ebkdomain.duoyueapp.com";
    /**
     * 升级接口.
     */
    public static final String DOMAIN_UPGRADE = IS_TEST ? "http://112.95.161.57:60021" : "http://ebkdomain.duoyueapp.com";
    //业务接口-内网服务器.
    //public static final String DOMAIN_BUSINESS = "http://192.168.0.128:6078";
    //public static final String DOMAIN_BUSINESS = "http://112.95.161.57:60019";
    //业务接口-外网服务器.
    //public static final String DOMAIN_BUSINESS = "http://ebkapi.duoyueapp.com";
    /**
     * 业务接口
     */
    public static final String DOMAIN_BUSINESS = IS_TEST ? "http://112.95.161.57:60019" : "http://ebkapi.duoyueapp.com";
    //分享书籍H5页面——内网服务器
    //public static final String DOMAIN_SHARE_H5 = "http://192.168.0.79:8000";
    //public static final String DOMAIN_TASK_H5 = "http://192.168.0.79:8000";
    //分享书籍H5页面——外网服务器
    //public static final String DOMAIN_SHARE_H5 = "http://m.duoyueapp.com";
    //public static final String DOMAIN_TASK_H5 = "http://taskcenter.duoyueapp.com";
    /**
     * 分享书籍H5页面
     */
    public static final String DOMAIN_SHARE_H5 = IS_TEST ? "http://192.168.0.79:8000" : "http://m.duoyueapp.com";
    public static final String DOMAIN_TASK_H5 = IS_TEST ? "http://192.168.0.79:8000" : "http://taskcenter.duoyueapp.com";
    // 内网广告接口
    //public static final String AD_BASE_URL = "http://192.168.0.91:7090";
    // 外网广告接口
    //public static final String AD_BASE_URL = "http://ebkadpush.duoyueapp.com";
    /**
     * 广告接口
     */
    public static final String AD_BASE_URL = IS_TEST ? "http://192.168.0.91:7090" : "http://ebkadpush.duoyueapp.com";
    //内网广告渠道(顺序>>0:开屏;1:横幅;2:信息流;3:阅读器信息流激励视频;4:阅读器横幅激励视频;5:签到激励视频;6:任务激励视频;7:悬浮球;8:退出APP激励视频;9:疲劳弹窗激励视频;10:阅读任务奖励翻倍激励视频;11:章节末信息流和横幅广告;12:横幅补充请求).
    public static final String[] CHANNAL_CODES_BETA = {"sk0001_sk01", "banner_0001", "novel1_flow", "novel2_read", "readba_nar1", "signin_L001", "h5task_0001", "XFQ001_0001", "exit01_0001", "tired1_0001", "RDTask_0001", "rd_end_0001", "banner_0002"};
    //外网广告渠道(顺序>>0:开屏;1:横幅;2:信息流;3:阅读器信息流激励视频;4:阅读器横幅激励视频;5:签到激励视频;6:任务激励视频;7:悬浮球;8:退出APP激励视频;9:疲劳弹窗激励视频;10:阅读任务奖励翻倍激励视频;11:章节末信息流和横幅广告;12:横幅补充请求).
    public static final String[] CHANNAL_CODES_RELEASE = {"DY1001_1001", "DY1003_1003", "DY1002_1002", "DY1006_1006", "DY1007_1007", "DY1004_1004", "DY1005_1005", "DY1009_1009", "DY1008_1008", "DY1010_1010", "DY1012_1012", "DY1011_1011", "DY1013_1013"};
    /**
     * 广告渠道(顺序>>0:开屏;1:横幅;2:信息流;3:阅读器信息流激励视频;4:阅读器横幅激励视频;5:签到激励视频;6:任务激励视频;7:悬浮球;8:退出APP激励视频;9:疲劳弹窗激励视频;10:阅读任务奖励翻倍激励视频;11:章节末信息流和横幅广告;12:横幅补充请求).
     */
    public static final String[] channalCodes = IS_TEST ? CHANNAL_CODES_BETA : CHANNAL_CODES_RELEASE;

    /**
     * 错误日志
     */
    public static final String DOMAINM_ERROR = "http://123.59.121.174";

    /**
     * 微信AppId(小说:wx96722d71b13d18a3).
     */
    public static final String WX_APP_ID = "wx96722d71b13d18a3";

    /**
     * QQ AppId.
     */
    public static final String QQ_APP_ID = "101558665";

    /**
     * QQ群Key
     */
    public static final String QQ_GROUP_KEY = "_auZVaoWTVPoHO8aPuivncRh66UyPmv8";

    /**
     * 新浪微博.
     */
    public static final String WEIBO_APP_KEY = "497760849";

    /**
     * 友盟AppKey
     */
    public static final String UM_APP_KEY = "5ca40f352036572faa00018b";

    /**
     * 登录成功广播Action
     */
    public static final String LOGIN_SUCC_ACTION = "com.duoyue.novel.action.LOGIN_SUCC";

    /**
     * oppo推送的APPid
     */
    public static final String OPPO_APPID = "30048273";

    /**
     * OPPO推送的APPkey
     */
    public static final String OPPO_APPKEY = "97d53512b8014f8ea206f551b70795bd";

    /**
     * oppo推送的appSecret
     */
    public static final String OPPO_APPSECRET = "f9f2a29abba04f35aa083ace42efff63";

    /**
     * 小米推送的APP_id
     */
    public static final String XIAOMI_APPID = "2882303761517974662";

    /**
     * 小米推送的APP_key
     */
    public static final String XIAOMI_KEY = "5721797477662";
}
