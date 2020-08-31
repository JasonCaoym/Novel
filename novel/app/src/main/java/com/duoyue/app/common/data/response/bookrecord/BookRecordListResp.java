package com.duoyue.app.common.data.response.bookrecord;

import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.data.dao.BookRecordBean;

import java.util.List;

/**
 * 获取历史阅读记录汇总响应信息
 * @author caoym
 * @data 2019/3/30  16:52
 */
public class BookRecordListResp
{

    /**
     * 阅读历史记录列表.
     */
    private List<BookRecordInfoResp> storedBookList;

    public BookRecordListResp()
    {
    }

    public List<BookRecordInfoResp> getStoredBookList() {
        return storedBookList;
    }
}
