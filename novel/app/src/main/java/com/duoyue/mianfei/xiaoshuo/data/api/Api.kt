package com.duoyue.mianfei.xiaoshuo.data.api;

import com.duoyue.mianfei.xiaoshuo.data.api.definition.MessageApi
import com.duoyue.mianfei.xiaoshuo.data.api.definition.QuestionApi
import com.zydm.base.data.net.ApiFactory

object Api {

    fun Question() = ApiFactory.getApiInstance(QuestionApi::class.java)

    fun message() = ApiFactory.getApiInstance(MessageApi::class.java)

}