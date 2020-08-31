package com.duoyue.app.common.data.request.bookshelf;

import com.duoyue.lib.base.app.http.AutoPost;
import com.duoyue.lib.base.app.http.DomainType;
import com.duoyue.lib.base.app.http.JsonRequest;

/**
 * 每日推荐图书.
 * @author wangt
 * @date 2019/06/10
 */
@AutoPost(action = "/app/books/v1/dayRecommendBook", domain = DomainType.BUSINESS)
public class DayRecommendBookReq extends JsonRequest {


}
