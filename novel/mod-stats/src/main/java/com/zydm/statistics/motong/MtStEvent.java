package com.zydm.statistics.motong;

import com.zydm.base.data.base.MtMap;
import com.zydm.base.data.bean.JsonSerialize;
import com.zydm.base.utils.StringUtils;
import com.zydm.base.utils.TimeUtils;

/**
 * Created by yan on 2017/3/6.
 */

public class MtStEvent implements JsonSerialize {

    public String eventId = "";
    public long clickTime;//second
    public MtMap<String, String> eventParams;
    public double eventValue = 0d;

    public MtStEvent(String eventId, double eventValue, MtMap<String, String> eventParams) {
        this.eventId = StringUtils.getString(eventId);
        this.eventParams = eventParams;
        this.eventValue = eventValue;
        this.clickTime = TimeUtils.getUnixTime();
    }

    @Override
    public String toString() {
        return "StEvent{" +
                "eventId=" + eventId +
                ", clickTime=" + clickTime +
                ", eventParams=" + eventParams +
                ", eventValue=" + eventValue +
                '}';
    }
}
