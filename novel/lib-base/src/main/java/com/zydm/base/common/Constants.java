package com.zydm.base.common;

public class Constants {

    public static final int NEGATIVE_ONE_NUM = -1;
    public static final int ZERO_NUM = 0;
    public static final int ONE_NUM = 1;
    public static final int TWO_NUM = 2;
    public static final int THREE_NUM = 3;
    public static final int ONE_HOUR = 1000 * 60 * 60;
    public static final String EMPTY = "";
    public static final String NULL = "null";
    public static final String BLANK_THREE = "   ";
    public static final String TMP_FILE_EXT = ".gntmp";
    public static final String PACKAGE = "package";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String FILE_PREFIX = "file://";
    public static final String UTF_8 = "UTF-8";
    public static final String APK = ".apk";
    public static final String JPG = ".jpg";
    public static final String PNG = ".png";

    // symbol
    public static final String CONNECTOR = "~";
    public static final String COLON = ":";
    public static final String MONEY_SYMBOL = "￥";
    public static final String FOLD_DIVIDER = "/";
    public static final String COMMA_SYMBOL = ",";
    public static final String QUOTATION_MARKS_SYMBOL = "\'";
    public static final String MONEY_K = "K";
    public static final String MONEY_W = "万";
    public static final String MONEY_TEN_MILLION = "千万";
    public static final String PERCENTAGE = "%";
    public static final int THOUSAND = 1000;
    public static final int TEN_THOUSAND = 10000;
    public static final int MILLION = 1000000;
    public static final int TEN_MILLION = 10000000;
    public static final int HUNDRED_MILLION = 100000000;

    // time
    public static final int MILLIS_30 = 30;
    public static final int MILLIS_100 = 100;
    public static final int MILLIS_200 = 200;
    public static final int MILLIS_250 = 250;
    public static final int MILLIS_300 = 300;
    public static final int MILLIS_400 = 400;
    public static final int MILLIS_500 = 500;
    public static final int MILLIS_600 = 600;
    public static final int MILLIS_700 = 700;
    public static final int MILLIS_800 = 800;
    public static final int MILLIS_900 = 900;

    public static final int SECOND_0 = 0;
    public static final int SECOND_1 = 1000;
    public static final int SECOND_2 = 2000;
    public static final int SECOND_3 = 3000;
    public static final int SECOND_5 = 5000;
    public static final int SECOND_10 = 10000;
    public static final int SECOND_20 = 20000;
    public static final int SECOND_30 = 30000;

    public static final int MINUTE_1 = SECOND_1 * 60;
    public static final int MINUTE_2 = MINUTE_1 * 2;
    public static final int MINUTE_5 = MINUTE_1 * 5;
    public static final int MINUTE_10 = MINUTE_1 * 10;
    public static final int MINUTE_30 = MINUTE_1 * 30;

    public static final int HOUR_1 = MINUTE_1 * 60;
    public static final int HOUR_2 = HOUR_1 * 2;
    public static final int HOUR_3 = HOUR_1 * 3;
    public static final int HOUR_8 = HOUR_1 * 8;

    public static final long DAY_1 = HOUR_1 * 24;
    public static final long DAY_2 = DAY_1 * 2;
    public static final long DAY_3 = DAY_1 * 3;
    public static final long DAY_7 = DAY_1 * 7;

    public static final long YEAR_1 = DAY_1 * 365;

    // size
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String B = "B";
    public static final String K = "KB";
    public static final String M = "MB";
    public static final String G = "GB";
    public static final int KB = 1024;
    public static final int MB = KB * KB;
    public static final int GB = KB * KB * KB;

    public static final int NUM_WAN = 10000;
    public static final int NUM_YI = NUM_WAN * NUM_WAN;
    public static final String UNIT_WAN = "万";
    public static final String UNIT_YI = "亿";
    public static final String UNIT_INFINITY = "爆表";

    //webView打开h5，设置userAgent。app内部打开h5
    public static final String USER_AGENT_SUFFIX = "_motong";
    public static final String APP_SCHEME = "com.motong.cm";
    public static final String WIFI = "WIFI";

    public static final String ELLIPSIS = "……";

    public static final String CHINESE_PARAGRAPH_SPACE = "        ";
}
