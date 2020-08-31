package com.zydm.base.presenter;

import com.zydm.base.common.Constants;
import com.zydm.base.data.bean.ListBean;
import com.zydm.base.data.tools.DataUtils;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;

import java.util.List;

/**
 * Created by yan on 2017/5/10.
 */

/**
 *
 * @param <D> 仅支持  IListBean  和  List
 */
public class DefaultMoreDataMerger<D> implements BiFunction<D, D, D> {
    @Override
    public D apply(@NonNull D curData, @NonNull D moreData) throws Exception {
        if (curData instanceof ListBean) {
            ListBean curListBean = (ListBean) curData;
            if (!DataUtils.isEmptyData(moreData)) {
                ListBean moreListBean = (ListBean) moreData;
                curListBean.getList().addAll(moreListBean.getList());
                curListBean.setNextCursor(moreListBean.getNextCursor());
            } else {
                curListBean.setNextCursor(Constants.EMPTY);
            }
        } else if (curData instanceof List) {
            if (!DataUtils.isEmptyData(moreData)) {
                ((List) curData).addAll((List) moreData);
            }
        }
        return curData;
    }
}
