package com.duoyue.mianfei.xiaoshuo.data.api.definition

import com.duoyue.mianfei.xiaoshuo.data.bean.ProblemListBean
import com.zydm.base.common.ParamKey
import com.zydm.base.data.net.*
import io.reactivex.Completable

@BasePath("/Api/Feedback/")
interface QuestionApi {

    @ApiConfigs(expTime = ExpTime.ONE_DAY)
    fun getType(): DataSrcBuilder<ProblemListBean>

    fun addIssue(@Param(ParamKey.TYPE_ID) typeId: String,
                 @Param(ParamKey.CONTACT) contact: String,
                 @Param(ParamKey.CONTENT) content: String): Completable
}
