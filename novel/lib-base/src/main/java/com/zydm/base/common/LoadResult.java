package com.zydm.base.common;

/**
 * Created by yan on 2017/5/11.
 */

public class LoadResult {

    // 刷新成功
    public static final int FORCE_UPDATE_SUCCEED = 0;
    public static final int FORCE_UPDATE_FAIL = 1;

    // 刷新失败
    public static final int LOAD_MORE_SUCCEED = 0;
    public static final int LOAD_MORE_FAIL = 1;
    public static final int LOAD_MORE_FAIL_NO_DATA = 2;//暂无数据
}
