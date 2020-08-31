package com.duoyue.mianfei.xiaoshuo.data.api.definition;

import com.duoyue.mianfei.xiaoshuo.data.bean.FeedBackMsgBean;
import com.zydm.base.data.net.ApiConfigs;
import com.zydm.base.data.net.BasePath;
import com.zydm.base.data.net.DataSrcBuilder;
import com.zydm.base.data.net.ExpTime;

@BasePath("/Api/Message/")
public interface MessageApi {

    @ApiConfigs(expTime = ExpTime.ONE_SECOND)
    DataSrcBuilder<FeedBackMsgBean> feedBackHistory();
}
