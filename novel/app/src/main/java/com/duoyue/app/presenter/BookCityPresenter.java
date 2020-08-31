package com.duoyue.app.presenter;

import android.text.TextUtils;
import android.util.SparseArray;
import com.duoyue.app.bean.BookChildColumnsBean;
import com.duoyue.app.bean.BookCityChildChangeBean;
import com.duoyue.app.bean.BookCityItemBean;
import com.duoyue.app.bean.BookCityModuleBean;
import com.duoyue.app.common.data.request.bookcity.BookCityMoreReq;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class BookCityPresenter {

    private DisposableObserver commentDisposable;

    private BookMoreView mBookMoreView;

    private StringBuilder stringBuffer;

    private BookCityChangeBean bookCityChangeBean;

    private int mType;

    public BookCityPresenter(BookMoreView bookMoreView) {
        this.mBookMoreView = bookMoreView;
        this.bookCityChangeBean = BookCityChangeBean.getInstance();
    }


    public void loadData(BookCityModuleBean bookCityModuleBean) {
//        Log.i("loadData", "----->"+bookCityModuleBean.getType());
        this.mType = bookCityModuleBean.getType();
        if (bookCityModuleBean.getMtType() == 0) {
            addRepeatBookId(bookCityModuleBean);
        } else {
            SparseArray<List<Long>> sparseArray = bookCityChangeBean.getSparseArray().get(mType);
            stringBuffer = new StringBuilder();
            if (sparseArray != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    for (Long id : sparseArray.valueAt(i)) {
                        stringBuffer.append(id);
                        stringBuffer.append(",");
                    }
                }
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }
        }
        // 1.2.2需求加入缓存书城数据   上报id可能为空
        // eg 用户第一次请求数据成功
        // 换一换数据没有缓存  上报id 也没有缓存  换一换功能就没用
        // 第二次请求数据请求失败  接口返回失败就没办法拿到上报id
        // 当用户点击换一换时如果请求接口就会出现上报id为空的情况
        // 当上报id为空时就不用去请求换一换接口
        if (TextUtils.isEmpty(stringBuffer.toString())) return;
        BookCityMoreReq bookDetailSaveCommentReq = new BookCityMoreReq();
        bookDetailSaveCommentReq.setTag(bookCityModuleBean.getTag());
        bookDetailSaveCommentReq.setTypeId(bookCityModuleBean.getTypeId());
//        Logger.d("initRepeatBookId", Arrays.asList(stringBuffer.toString().split(",")).size() + "<----->" + stringBuffer.toString());
        bookDetailSaveCommentReq.setRepeatBookId(stringBuffer.toString());
        bookDetailSaveCommentReq.setColumnId(bookCityModuleBean.getId());

        commentDisposable = new DisposableObserver<JsonResponse<BookCityChildChangeBean>>() {
            @Override
            public void onNext(JsonResponse<BookCityChildChangeBean> jsonResponse) {

                if (jsonResponse.status == 1 && jsonResponse.data != null) {
                    BookCityChildChangeBean bookCityChangeBean = jsonResponse.data;
                    mBookMoreView.loadMoreData(bookCityChangeBean);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost()
                .setRequest(bookDetailSaveCommentReq)
                .setResponseType(BookCityChildChangeBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(commentDisposable);
    }

    public void addRepeatBookId(int positon, BookCityChildChangeBean ids) {

        if (bookCityChangeBean.getSparseArray().get(mType) != null && bookCityChangeBean.getSparseArray().get(mType).get(positon) != null) {
            bookCityChangeBean.getSparseArray().get(mType).remove(positon);
        }
        List<Long> longs = new ArrayList<>();
        for (BookChildColumnsBean bookChildColumnsBean : ids.getChildColumns()) {
            if (bookChildColumnsBean.getBooks() == null) break;
            for (BookCityItemBean bookCityItemBean : bookChildColumnsBean.getBooks()) {
                longs.add(bookCityItemBean.getId());
            }
        }
        bookCityChangeBean.getSparseArray().get(mType).put(positon, longs);
        initRepeatBookId();
    }

    //人工
    void addRepeatBookId(BookCityModuleBean ids) {
        stringBuffer = new StringBuilder();
        for (BookChildColumnsBean bookChildColumnsBean : ids.getChildColumns()) {
            if (bookChildColumnsBean.getBooks() == null) break;
            for (BookCityItemBean bookCityItemBean : bookChildColumnsBean.getBooks()) {
                stringBuffer.append(bookCityItemBean.getId());
                stringBuffer.append(",");
            }
        }
        if (stringBuffer.length() > 0) stringBuffer.deleteCharAt(stringBuffer.length() - 1);

    }

    //算法
    void initRepeatBookId() {
        stringBuffer = new StringBuilder();
        SparseArray<List<Long>> list = bookCityChangeBean.getSparseArray().get(mType);
        for (int j = 0; j < list.size(); j++) {
            List<Long> longs = list.valueAt(j);
            for (Long id : longs) {
                stringBuffer.append(id);
                stringBuffer.append(",");
            }
        }
        if (stringBuffer.length() > 0) stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    public interface BookMoreView {
        void loadMoreData(BookCityChildChangeBean list);
    }
}
