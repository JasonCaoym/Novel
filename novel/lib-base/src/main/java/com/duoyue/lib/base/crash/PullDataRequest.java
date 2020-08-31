package com.duoyue.lib.base.crash;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

import java.util.List;

/**
 * 拉取信息上报
 *
 */
@AutoPost(action = "/app/stats/v1/pull", domain = DomainType.BUSINESS)
public class PullDataRequest extends JsonRequest {

    private List<PullDataList> pullList;

    public List<PullDataList> getPullList() {
        return pullList;
    }

    public void setPullList(List<PullDataList> pullList) {
        this.pullList = pullList;
    }

    public PullDataRequest(List<PullDataList> pullList) {
       this.pullList = pullList;
    }


}
