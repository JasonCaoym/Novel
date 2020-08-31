package com.duoyue.app.presenter;

import com.duoyue.app.bean.BookCategoryListBean;
import com.duoyue.app.bean.BookRankCategoryBean;
import com.duoyue.app.common.data.request.bookcity.BookCategoryReq;
import com.duoyue.app.common.mgr.StartGuideMgr;
import com.duoyue.app.ui.activity.BookRankActivity;
import com.duoyue.app.ui.view.BookCategoryView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;


public class BookCategoryPresenter {

    private BookCategoryView categoryView;


    public BookCategoryPresenter(BookCategoryView view) {
        categoryView = view;
        getBookListData();
    }

    public void getBookListData() {
        Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                // 是否需要获取类别
                List<BookCategoryListBean> list = new ArrayList<>();
                BookCategoryListBean maleList = getCategoryData(BookRankActivity.MALE);
                BookCategoryListBean femaleList = getCategoryData(BookRankActivity.FEMALE);
                if (maleList != null && femaleList != null) {

                    if (StartGuideMgr.getChooseSex() == StartGuideMgr.SEX_MAN) {
                        List<BookRankCategoryBean> items = maleList.getItems();
                        BookRankCategoryBean item = items.get(0);
                        item.setSelected(true);
                        items.set(0, item);
                        maleList.setItems(items);
                    } else {
                        List<BookRankCategoryBean> items = femaleList.getItems();
                        BookRankCategoryBean item = items.get(0);
                        item.setSelected(true);
                        items.set(0, item);
                        femaleList.setItems(items);
                    }
                    List<BookRankCategoryBean> items = maleList.getItems();
                    BookRankCategoryBean item = items.get(0);
                    item.setSelected(true);
                    items.set(0, item);
                    maleList.setItems(items);

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
                        categoryView.updateCategory(dataList.get(0), dataList.get(1));
                    }

                    @Override
                    public void onError(Throwable e) {
                        categoryView.showError();
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

}
