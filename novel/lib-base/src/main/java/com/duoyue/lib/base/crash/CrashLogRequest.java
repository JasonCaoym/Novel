package com.duoyue.lib.base.crash;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

import java.util.List;

/**
 * 崩溃日志上报
 */
@AutoPost(action = "/app/stats/v1/crash", domain = DomainType.BUSINESS)
public class CrashLogRequest extends JsonRequest {
    private List<CrashList> crashList;

    public List<CrashList> getCrashList() {
        return crashList;
    }

    public void setCrashList(List<CrashList> crashList) {
        this.crashList = crashList;
    }

    public CrashLogRequest(List<CrashList> crashLists) {
        crashList = crashLists;
    }
}
