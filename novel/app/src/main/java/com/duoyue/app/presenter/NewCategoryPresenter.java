package com.duoyue.app.presenter;

import com.duoyue.app.bean.BookCategoryListBean;
import com.duoyue.app.bean.BookSiteBean;
import com.duoyue.app.common.data.DataCacheManager;
import com.duoyue.app.common.data.request.bookcity.BookCategoryReq;
import com.duoyue.app.common.data.request.bookcity.BookSiteListReq;
import com.duoyue.app.ui.activity.BookRankActivity;
import com.duoyue.app.ui.view.NewCategoryView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应NewCategoryFragment的Presenter
 *
 * @author wangt
 * @date 2019/06/17
 */
public class NewCategoryPresenter {

    NewCategoryView newCategoryView;

    public NewCategoryPresenter(NewCategoryView view) {
        this.newCategoryView = view;
    }

    public void getBookListData() {
        newCategoryView.showLoading();
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                // 是否需要获取类别
                List<BookCategoryListBean> list = new ArrayList<>();
                BookCategoryListBean maleList = getCategoryData(BookRankActivity.MALE);
                BookCategoryListBean femaleList = getCategoryData(BookRankActivity.FEMALE);
                if (maleList != null && femaleList != null) {
                    list.add(maleList);
                    list.add(femaleList);
                    emitter.onNext(list);
                } else {
                    emitter.onError(new Throwable("empty"));
                }

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookCategoryListBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<BookCategoryListBean> dataList) {
                        DataCacheManager.getInstance().setCategoryLeft(dataList);
                        newCategoryView.dismissLoading();
                        newCategoryView.updateCategory(dataList.get(0), dataList.get(1));
                    }

                    @Override
                    public void onError(Throwable e) {
                        newCategoryView.dismissLoading();
                        newCategoryView.showNetworkError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    private BookCategoryListBean getCategoryData(int sex) {
        BookCategoryReq request = new BookCategoryReq();
        request.sex = sex;
        try {
            JsonResponse<BookCategoryListBean> response = new JsonPost.SyncPost<BookCategoryListBean>()
                    .setRequest(request)
                    .setResponseType(BookCategoryListBean.class)
                    .post();
            if (response != null && response.status == 1) {
                return response.data;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


//    public void loadSiteData() {
//        BookSiteListReq bookSiteListReq = new BookSiteListReq();
//        bookSiteListReq.site = 3;
//        bookSiteListReq.chan = -1;
//
//        new JsonPost.AsyncPost<BookSiteBean>()
//                .setRequest(bookSiteListReq)
//                .setResponseType(BookSiteBean.class)
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .post(new DisposableObserver<JsonResponse<BookSiteBean>>() {
//                    @Override
//                    public void onNext(JsonResponse<BookSiteBean> jsonResponse) {
//                        if (jsonResponse.status == 1 && jsonResponse.data != null) {
//                            newCategoryView.showSite(jsonResponse.data);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }


    public interface onScollListener {

        void onStartScoll();

        void onStopScoll();

    }
}
