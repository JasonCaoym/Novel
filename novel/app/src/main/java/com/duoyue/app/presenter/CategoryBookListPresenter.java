package com.duoyue.app.presenter;

import android.app.Activity;
import com.duoyue.app.bean.CategoryBookBean;
import com.duoyue.app.bean.CategoryBookListBean;
import com.duoyue.app.common.data.request.category.CategoryBookListReq;
import com.duoyue.app.ui.activity.CategoryBookListActivity;
import com.duoyue.app.ui.view.BookPageView;
import com.duoyue.lib.base.app.http.JsonPost;
import com.duoyue.lib.base.app.http.JsonResponse;
import com.zydm.base.common.LoadResult;
import com.zydm.base.data.bean.CategoryBean;
import com.zydm.base.tools.TooFastChecker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class CategoryBookListPresenter implements CategoryBookListActivity.Presenter {

    private Activity mActivity;
    private BookPageView pageView;
    private TooFastChecker mTooFastChecker = new TooFastChecker();
    private CategoryBean categoryBean;
    private DisposableObserver disposableObserver;
    private int firstTag;
    private int secondTag;
    private int threeTag;
    private int sortType = 1;
    private String tag;
    private int subCategoryId;
    private Object adBean;
    private boolean showAd;
    private int nextPage;

    public CategoryBookListPresenter(Activity activity, BookPageView pageView, CategoryBean categoryBean) {
        this.mActivity = activity;
        this.pageView = pageView;
        this.categoryBean = categoryBean;
    }

    @Override
    public void loadMoreData(int pageIndex) {
        loadPageData(sortType, firstTag, secondTag, threeTag, tag, subCategoryId, pageIndex, false);
    }

    @Override
    public void loadPageData(final int type, final int firstTag, final int secondTag, final int threeTag, final String tag, final int subCategoryId, final int pageIndex, boolean loadAd) {
        showAd = loadAd;
        /*if (showAd) {
            AdManager.getInstance().createAdSource(mActivity).loadListAd(null, 640, 320)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new SingleObserver<ArrayList<?>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(ArrayList<?> objects) {
                            if (objects != null && objects.size() > 0) {
                                adBean = objects.get(0);
                            }
                            rqeustData(type, firstTag, secondTag, threeTag, tag, subCategoryId, pageIndex);

                        }

                        @Override
                        public void onError(Throwable e) {
                            rqeustData(type, firstTag, secondTag, threeTag, tag, subCategoryId, pageIndex);

                        }
                    });
        } else*/ {
            rqeustData(type, firstTag, secondTag, threeTag, tag, subCategoryId, pageIndex);
        }
    }

    private void rqeustData(int type, int firstTag, int secondTag, int threeTag, String tag, int subCategoryId, final int pageIndex) {
        this.sortType = type;
        this.firstTag = firstTag;
        this.secondTag = secondTag;
        this.threeTag = threeTag;
        this.tag = tag;
        this.subCategoryId = subCategoryId;

        final CategoryBookListReq request = new CategoryBookListReq();
        request.categoryId = categoryBean != null ? categoryBean.getId() : "";
        request.firstTag = firstTag;
        request.secondTag = secondTag;
        request.threeTag = threeTag;
        request.tag = tag;
        request.subCategoryId = subCategoryId;
        request.pageIndex = pageIndex;
        request.parentId = categoryBean != null ? categoryBean.getSex() : 0;
        disposableObserver = new DisposableObserver<JsonResponse<CategoryBookListBean>>() {
            @Override
            public void onNext(JsonResponse<CategoryBookListBean> jsonResponse) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                if (jsonResponse.status == 1 && jsonResponse.data != null && jsonResponse.data.getList() != null
                        && !jsonResponse.data.getList().isEmpty()) {
                    nextPage = jsonResponse.data.getNextPage();
                    if (pageIndex == 1) {
                        pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_SUCCEED);
                    } else {
                        pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_SUCCEED);
                    }
                    List<Object> bookList = new ArrayList<>();
                    if (showAd && adBean != null) {
                        bookList.add(adBean);
                    }
                    List<CategoryBookBean> categoryBookBeans = jsonResponse.data.getList();
                    for (CategoryBookBean item : categoryBookBeans) {
                        item.setType(sortType);
                        //字数类型.
                        item.setWordCountType(request.threeTag);
                    }
                    bookList.addAll(categoryBookBeans);
                    pageView.showPage(bookList);
                } else {
                    if (pageIndex == 1) {
                        pageView.showEmpty();
                        pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL);
                    } else {
                        pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL_NO_DATA);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                mTooFastChecker.cancel();
                pageView.dismissLoading();
                pageView.showForceUpdateFinish(LoadResult.FORCE_UPDATE_FAIL);
                if (pageIndex == 1) {
                    pageView.showNetworkError();
                } else {
                    pageView.showLoadMoreFinish(LoadResult.LOAD_MORE_FAIL);
                }
            }

            @Override
            public void onComplete() {

            }
        };

        new JsonPost.AsyncPost<CategoryBookListBean>()
                .setRequest(request)
                .setResponseType(CategoryBookListBean.class)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .post(disposableObserver);
    }

    @Override
    public void onPageDestroy() {
        if (disposableObserver != null) {
            disposableObserver.dispose();
        }
    }

    @Override
    public Object getAdData() {
        return adBean;
    }

    @Override
    public int getNextPage() {
        return nextPage;
    }

}
